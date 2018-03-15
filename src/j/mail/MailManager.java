package j.mail;

import j.Properties;
import j.log.Logger;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilRandom;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 
 * @author 肖炯
 *
 */
public class MailManager implements Runnable{
	private static Logger log=Logger.create(MailManager.class);//日志输出
	private static long configLastModified=0;//配置文件上次修改时间
	private static volatile boolean loading=true;
	private static ConcurrentMap configs=new ConcurrentMap();
	private static ConcurrentMap senders=new ConcurrentMap();
	private static ConcurrentMap readers=new ConcurrentMap();
	
	
	static{
		MailManager mm=new MailManager();
		Thread thread=new Thread(mm);
		thread.start();
		log.log("MailManager started.", -1);
		load();
	}
	
	/**
	 * 
	 * @param senderId
	 * @return
	 */
	public static MailSenderConfig getConfig(String senderId){
		return (MailSenderConfig)configs.get(senderId);
	}
	
	/**
	 * 
	 * @return
	 */
	public static List getSenders(){
		return senders.listValues();
	}
	
	/**
	 * 
	 * @param senderId
	 * @return
	 */
	public static MailReader getReader(String senderId){
		try{
			MailSenderConfig config=getConfig(senderId);
			if(config==null) return null;
			
			if(config.getReaderManager()==null||"".equals(config.getReaderManager())){
				return new MailReader(senderId);
			}else{
				MailReader reader=(MailReader)Class.forName(config.getReaderManager()).newInstance();
				reader.setSenderId(senderId);
				
				return reader;
			}
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return null;
		}
	}
	
	/**
	 * 
	 * @param senderId 发送器ID
	 * @param to 收件人
	 * @param cc 抄送
	 * @param subj 标题
	 * @param text 正文
	 * @param type 邮件正文内容类型，text/html或text/plain
	 * @param encoding 邮件正文内容字符编码
	 * @param filePaths 邮件附件的文件路径[]
	 * @throws Exception
	 */
	public static void send(String senderId,String to, String cc, String subj, String text, String type, String encoding, String[] filePaths) throws Exception{
		waitWhileLoading();
		List works=(List)senders.get(senderId);
		
		if(works==null||works.size()==0){
			throw new Exception("no mail sender of id - "+senderId);
		}
		
		MailSender sender=(MailSender)works.get(JUtilRandom.nextInt(works.size()));
		sender.send(to,cc,subj,text,type,encoding,filePaths);
	}
	
	/**
	 * 
	 * @param senderId
	 * @param to
	 * @param cc
	 * @param subj
	 * @param text
	 * @param type
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 * @throws Exception
	 */
	public static void send(String senderId,String to, String cc, String subj, String text, String type, String encoding, String[] filePaths,String fromName) throws Exception{
		waitWhileLoading();
		List works=(List)senders.get(senderId);
		
		if(works==null||works.size()==0){
			throw new Exception("no mail sender of id - "+senderId);
		}
		
		MailSender sender=(MailSender)works.get(JUtilRandom.nextInt(works.size()));
		sender.send(to,cc,subj,text,type,encoding,filePaths,fromName);
	}
	
	/**
	 * 
	 *
	 */
	public static void load(){
		try{
			loading=true;
			
			List values=senders.listValues();
			for(int i=0;i<values.size();i++){
				List ss=(List)values.get(i);
				for(int j=0;j<ss.size();j++){
					MailSender sender=(MailSender)ss.get(j);
					sender.shutdown();
				}
			}
			senders.clear();
			
			configs.clear();
			
			//文件是否存在
			File file = new File(Properties.getConfigPath()+"mail.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			
			Document document=JUtilDom4j.parse(Properties.getConfigPath()+"mail.xml","UTF-8");
			Element root=document.getRootElement();
			
			List senderEles=root.elements("sender");
			for(int i=0;i<senderEles.size();i++){
				Element senderEle=(Element)senderEles.get(i);
				int threads=Integer.parseInt(senderEle.elementText("threads"));
				
				MailSenderConfig config=new MailSenderConfig();
				config.setId(senderEle.elementText("id"));
				config.setDesc(senderEle.attributeValue("desc"));
				config.setHost(senderEle.elementText("host"));
				config.setPort(senderEle.elementText("port"));
				config.setUser(senderEle.elementText("user"));
				config.setReaderProtocol(senderEle.elementText("reader-protocol"));
				config.setReaderHost(senderEle.elementText("reader-host"));
				config.setReaderPort(senderEle.elementText("reader-port"));
				config.setReaderManager(senderEle.elementText("reader-manager"));
				config.setReaderThread(senderEle.elementText("reader-thread"));
				config.setReaderInterval(senderEle.elementText("reader-interval"));
				config.setReaderCount(senderEle.elementText("reader-count"));
				config.setReaderFlagRead(senderEle.elementText("reader-flag-read"));
				config.setReaderFolder(senderEle.elementText("reader-folder"));
				config.setReaderName(senderEle.elementText("reader-name"));
				config.setReaderVersion(senderEle.elementText("reader-version"));
				config.setPassword(senderEle.elementText("password"));
				config.setAuthCode(senderEle.elementText("auth-code"));
				config.setFrom(senderEle.elementText("from"));
				config.setFromName(senderEle.elementText("from-name"));
				config.setSecure("true".equalsIgnoreCase(senderEle.elementText("secure")));
				config.setMaxTries(Integer.parseInt(senderEle.elementText("max-tries")));
				
				List params=senderEle.elements("param");
				for(int p=0;p<params.size();p++){
					Element paramE=(Element)params.get(p);
					config.setParam(paramE.attributeValue("key"),paramE.attributeValue("value"));
				}
				
				configs.put(config.getId(),config);
				
				//启动邮件发送线程
				List works=new LinkedList();
				for(int t=0;t<threads;t++){
					MailSender sender=new MailSender(senderEle.elementText("id"),t,config);
					Thread thread=new Thread(sender);
					thread.start();
					log.log("mail sender "+senderEle.elementText("id")+","+t+" started.",-1);
					
					works.add(sender);
				}
				
				senders.put(senderEle.elementText("id"),works);
				
				//启动邮件收取线程
				if(config.getReaderThread()>0){
					MailReaderThread reader=(MailReaderThread)readers.get(config.getId());
					if(reader!=null){
						reader.shutdown();
						log.log("shutdown mail reader "+config.getId()+","+config.getDesc()+".",-1);
					}
					
					reader=new MailReaderThread(config.getId());
					Thread thread=new Thread(reader);
					thread.start();
					
					readers.put(config.getId(),reader);
					
					log.log("mail reader "+config.getId()+","+config.getDesc()+" started.",-1);
				}
			}
			
			root=null;
			document=null;

			//配置文件最近修改时间
			File configFile=new File(Properties.getConfigPath()+"mail.xml");
			configLastModified=configFile.lastModified();
			configFile=null;
			
			loading=false;
		}catch(Exception e){
			loading=false;
			log.log(e,Logger.LEVEL_FATAL);
		}
	}
	
	/**
	 * 
	 *
	 */
	public static void waitWhileLoading(){
		while(loading){
			try{
				Thread.sleep(100);
			}catch(Exception ex){}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * 检测mail.xml是否修改过，如修改过重新加载配置
		 */
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			List _readers=readers.listValues();
			for(int i=0;i<_readers.size();i++){
				MailReaderThread reader=(MailReaderThread)_readers.get(i);
				if(reader.isBlocked()){
					MailSenderConfig config=MailManager.getConfig(reader.getSenderId());
					
					log.log("shutdown mail reader "+config.getId()+","+config.getDesc()+" because blocking.",-1);
					reader.shutdown();
					
					reader=new MailReaderThread(config.getId());
					Thread thread=new Thread(reader);
					thread.start();
					
					readers.put(config.getId(),reader);
					
					log.log("new mail reader "+config.getId()+","+config.getDesc()+" started after shutdown the blocked.",-1);
				}
			}
			
			if(configLastModified<=0) continue;

			File configFile=new File(Properties.getConfigPath()+"mail.xml");
			if(configLastModified<configFile.lastModified()){
				log.log("mail.xml has been modified, so reload it.",-1);
				load();
			}
			configFile=null;
		}
	}
	

	
	/**
	 * 测试
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
//		for(int i=0;i<5;i++){
//			String str=JUtilString.randomStr(64);
//			MailManager.send("sender_a",
//					"crazyroar@126.com",
//					"",
//					str,
//					str,
//					MailSender.CONTENT_HTML,
//					"utf-8",
//					null);
//		}
		
		System.out.println("mail manager startup!");
	}
}