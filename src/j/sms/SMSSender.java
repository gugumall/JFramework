package j.sms;

import j.log.Logger;
import j.nvwa.Nvwa;
import j.util.ConcurrentList;



/**
 * 
 * @author 肖炯
 *
 */
public class SMSSender implements Runnable{	
	private static Logger log=Logger.create(SMSSender.class);//日志输出
	public  final static String CONTENT_HTML="text/html";
	public  final static String CONTENT_TEXT="text/plain";
	public  final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	private String id;
	private int num;
	private SMSSenderConfig config;
	private ConcurrentList tasks=new ConcurrentList();
	private SMSChannel channel=null;
	private volatile boolean shutdown=false;
	
	/**
	 * 
	 * @param id
	 * @param num
	 * @param config
	 */
	protected SMSSender(String id,int num,SMSSenderConfig config){
		this.id=id;
		this.num=num;
		this.config=config;
	}
	
	/**
	 * 
	 * @return
	 */
	protected SMSSenderConfig getConfig(){
		return this.config;
	}
	

	/**
	 * 
	 * @param to
	 * @param text
	 * @param encoding
	 * @param fromName
	 * @return
	 */
	public boolean doSend(String to,String text,String encoding,String fromName){
		try {
			synchronized(this){
				if(channel==null){
					if(config.channelNvwaCode!=null&&!"".equals(config.channelNvwaCode)){
						channel=(SMSChannel)Nvwa.create(config.channelNvwaCode);
					}
					
					if(channel==null){				
						channel=(SMSChannel)Class.forName(config.channelImpl).newInstance();
					}
				}
			}
			
			return channel.send(to, text, encoding, fromName);
		} catch (Exception e) {
			log.log(e, Logger.LEVEL_ERROR);
			return false;
		}
	}

	/**
	 * 
	 * @param to
	 * @param text
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 * @return
	 */
	public boolean doSend(String to,String text, String encoding, String[] filePaths,String fromName) {
		try {
			synchronized(this){
				if(channel==null){
					if(config.channelNvwaCode!=null&&!"".equals(config.channelNvwaCode)){
						channel=(SMSChannel)Nvwa.create(config.channelNvwaCode);
					}
					
					if(channel==null){				
						channel=(SMSChannel)Class.forName(config.channelImpl).newInstance();
					}
				}
			}
			
			return channel.send(to, text, encoding, filePaths, fromName);
		} catch (Exception e) {
			log.log(e, Logger.LEVEL_ERROR);
			return false;
		}
	}
	

	/**
	 * 
	 * @param to
	 * @param texts
	 * @param encoding
	 * @param fromName
	 * @return
	 */
	public boolean doSend(String to,String[] texts,String encoding,String fromName){
		try {
			synchronized(this){
				if(channel==null){
					if(config.channelNvwaCode!=null&&!"".equals(config.channelNvwaCode)){
						channel=(SMSChannel)Nvwa.create(config.channelNvwaCode);
					}
					
					if(channel==null){				
						channel=(SMSChannel)Class.forName(config.channelImpl).newInstance();
					}
				}
			}
			
			return channel.send(to, texts, encoding, fromName);
		} catch (Exception e) {
			log.log(e, Logger.LEVEL_ERROR);
			return false;
		}
	}

	/**
	 * 
	 * @param to
	 * @param texts
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 * @return
	 */
	public boolean doSend(String to,String[] texts, String encoding, String[] filePaths,String fromName) {
		try {
			synchronized(this){
				if(channel==null){
					if(config.channelNvwaCode!=null&&!"".equals(config.channelNvwaCode)){
						channel=(SMSChannel)Nvwa.create(config.channelNvwaCode);
					}
					
					if(channel==null){				
						channel=(SMSChannel)Class.forName(config.channelImpl).newInstance();
					}
				}
			}
			
			return channel.send(to, texts, encoding, filePaths, fromName);
		} catch (Exception e) {
			log.log(e, Logger.LEVEL_ERROR);
			return false;
		}
	}
	
	/**
	 * 
	 * @param to
	 * @param text
	 * @param encoding
	 * @param filePaths
	 */
	public void send(String to,String text, String encoding, String[] filePaths){
		tasks.add(new SendSMSTask(to,text, encoding, filePaths));
	}
	
	/**
	 * 
	 * @param to
	 * @param text
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 */
	public void send(String to, String text, String encoding, String[] filePaths,String fromName){
		tasks.add(new SendSMSTask(to,text, encoding, filePaths,fromName));
	}
	
	/**
	 * 
	 * @param to
	 * @param text
	 * @param encoding
	 * @param filePaths
	 */
	public void send(String to,String[] texts, String encoding, String[] filePaths){
		tasks.add(new SendSMSTask(to,texts, encoding, filePaths));
	}
	
	/**
	 * 
	 * @param to
	 * @param text
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 */
	public void send(String to, String[] texts, String encoding, String[] filePaths,String fromName){
		tasks.add(new SendSMSTask(to,texts, encoding, filePaths,fromName));
	}
	
	/**
	 * 
	 *
	 */
	public void shutdown(){
		this.shutdown=true;
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			try{
				try{
					Thread.sleep(1000);
				}catch(Exception ex){}
				
				SendSMSTask task=null;
				if(tasks.size()>0) task=(SendSMSTask)tasks.get(0);
				else if(shutdown) break;
				
				if(task!=null){					
					boolean ok=false;
					if(task.texts!=null){
						if(task.filePaths==null||task.filePaths.length<1){//无附件
							ok=doSend(task.to, task.texts, task.encoding,task.fromName);
						}else{
							ok=doSend(task.to, task.texts, task.encoding,task.filePaths,task.fromName);
						}
					}else{
						if(task.filePaths==null||task.filePaths.length<1){//无附件
							ok=doSend(task.to, task.text, task.encoding,task.fromName);
						}else{
							ok=doSend(task.to, task.text, task.encoding,task.filePaths,task.fromName);
						}
					}
					if(ok){
						log.log("sms:"+task.toString()+" has been sent by thread "+this.id+","+this.num+"!",-1);
						tasks.remove(0);
						task=null;
					}else{
						task.failCount++;
						if(task.failCount>config.maxTries){
							log.log("sms:"+task.toString()+" failed to send! "+this.id+","+this.num+"!",-1);
							tasks.remove(0);
							task=null;
						}else{
							log.log("sms:"+task.toString()+" has not been sent, to try again! "+this.id+","+this.num+"!",-1);
						}
					}
				}
			}catch(Exception e){
				log.log(e, Logger.LEVEL_ERROR);
				try{
					Thread.sleep(5000);
				}catch(Exception ex){}
			}
		}
	}
}

/**
 * 
 * @author 肖炯
 *
 */
class SendSMSTask{
	String to;
	String text=null;
	String[] texts=null;
	String encoding;
	String[] filePaths;
	int failCount=0;
	String fromName=null;
	
	/**
	 * 
	 * @param to
	 * @param text
	 * @param encoding
	 * @param filePaths
	 */
	public SendSMSTask(String to,String text, String encoding, String[] filePaths){
		this.to=to;
		this.text=text;
		this.encoding=encoding;
		this.filePaths=filePaths;
	}
	
	/**
	 * 
	 * @param to
	 * @param text
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 */
	public SendSMSTask(String to, String text,String encoding, String[] filePaths,String fromName){
		this.to=to;
		this.text=text;
		this.encoding=encoding;
		this.filePaths=filePaths;
		this.fromName=fromName;
	}
	
	/**
	 * 
	 * @param to
	 * @param texts
	 * @param encoding
	 * @param filePaths
	 */
	public SendSMSTask(String to,String[] texts, String encoding, String[] filePaths){
		this.to=to;
		this.texts=texts;
		this.encoding=encoding;
		this.filePaths=filePaths;
	}
	
	/**
	 * 
	 * @param to
	 * @param texts
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 */
	public SendSMSTask(String to, String[] texts,String encoding, String[] filePaths,String fromName){
		this.to=to;
		this.texts=texts;
		this.encoding=encoding;
		this.filePaths=filePaths;
		this.fromName=fromName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String s="";
		if(texts!=null){
			for(int i=0;i<texts.length;i++){
				s+="{"+texts[i]+"} ";
			}
			s="to "+to+"\r\n"+s;
		}else{
			s="to "+to+"\r\n"+text+"\r\n";
		}
		if(filePaths!=null&&filePaths.length>0){
			for(int i=0;i<filePaths.length;i++){
				s+="["+filePaths[i]+"] ";
			}
		}
		return s;
	}
}
