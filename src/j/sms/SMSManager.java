package j.sms;

import j.common.JProperties;
import j.log.Logger;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilKeyValue;
import j.util.JUtilRandom;
import j.util.JUtilSorter;
import j.util.JUtilString;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 
 * @author 肖炯
 *
 */
public class SMSManager implements Runnable{
	private static Logger log=Logger.create(SMSManager.class);//日志输出
	private static long configLastModified=0;//配置文件上次修改时间
	private static volatile boolean loading=true;
	private static List configs=new ArrayList();
	private static ConcurrentMap senders=new ConcurrentMap();
	private static SMSSenderSorter sorter=new SMSSenderSorter();
	
	
	static{
		load();
		
		SMSManager m=new SMSManager();
		Thread thread=new Thread(m);
		thread.start();
		log.log("SMSManager monitor thread started.",-1);
	}
	
	/**
	 * 
	 * @return
	 */
	public static List getSenderConfigs(){
		List _configs=new LinkedList();
		_configs.addAll(configs);
		return _configs;
	}
	
	/**
	 * 
	 * @param business
	 * @return
	 */
	public static List getSenderConfigsOfBusiness(String business){
		if(business==null||"".equals(business)) return getSenderConfigs();
		
		List _configs=new LinkedList();
		for(int i=0;i<configs.size();i++){
			SMSSenderConfig config=(SMSSenderConfig)configs.get(i);
			if(_configs.equals(config.getBusiness())){
				_configs.add(config);
			}
		}
		return _configs;
	}
	
	/**
	 * 
	 * @param senderId
	 * @return
	 */
	public static SMSSenderConfig getSenderConfig(String senderId){
		for(int i=0;i<configs.size();i++){
			SMSSenderConfig config=(SMSSenderConfig)configs.get(i);
			if(config.getId().equals(senderId)) return config;
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public static ConcurrentMap getBusinesses(){
		ConcurrentMap businesses=new ConcurrentMap();
		for(int i=0;i<configs.size();i++){
			SMSSenderConfig config=(SMSSenderConfig)configs.get(i);
			if(!businesses.containsKey(config.getBusiness())){
				businesses.put(config.getBusiness(),new JUtilKeyValue(config.getBusiness(),config.getBusinessName()));
			}
		}
		
		return businesses;
	}
	
	/**
	 * 
	 * @param business
	 * @param senderId
	 * @param to
	 * @return
	 */
	public static boolean reachable(String business,String senderId,String to){
		if(senderId!=null
				&&!"".equals(senderId)
				&&senders.containsKey(senderId)){//指定ID
			String region=MobileVerifier.valid(to);
			if(region==null){//匹配不到
				return false;
			}
			
			SMSSenderConfig config=SMSManager.getSenderConfig(senderId);
			if(!config.region.equals(region)) return false;
			
			List works=(List)senders.get(senderId);
			if(works==null||works.size()==0){
				return false;
			}
			
			return true;
		}else{//自动匹配
			String region=MobileVerifier.valid(to);
			if(region==null){//匹配不到
				return false;
			}
			
			for(int i=0;i<configs.size();i++){
				SMSSenderConfig config=(SMSSenderConfig)configs.get(i);
				if(!config.business.equals(business)) continue;//不是指定业务的短信通道
				
				if(config.getRegion().equals(region)){//匹配上地区
					senderId=config.id;
					
					List works=(List)senders.get(senderId);
					
					if(works==null||works.size()==0){
						return false;
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param business
	 * @param senderId
	 * @param to
	 * @return
	 * @throws Exception
	 */
	private static SMSSender selectSender(String business,String senderId,String to)throws Exception{
		if(senderId!=null
				&&!"".equals(senderId)
				&&senders.containsKey(senderId)){//指定ID
			List works=(List)senders.get(senderId);
			
			if(works==null||works.size()==0){
				throw new Exception("no works for sender "+senderId);
			}
			
			return (SMSSender)works.get(JUtilRandom.nextInt(works.size()));
		}else{//自动匹配
			String region=MobileVerifier.valid(to);
			if(region==null){//匹配不到
				throw new Exception("can not find region of mobile "+to);
			}
			
			for(int i=0;i<configs.size();i++){
				SMSSenderConfig config=(SMSSenderConfig)configs.get(i);
				if(!config.business.equals(business)) continue;//不是指定业务的短信通道
				
				if(config.getRegion().equals(region)){//匹配上地区
					senderId=config.id;
					
					List works=(List)senders.get(senderId);
					
					if(works==null||works.size()==0){
						throw new Exception("no works for sender("+senderId+",auto match),business - "+business+", mobile - "+to);
					}
					
					log.log(senderId+" matches business - "+business+",mobile - "+to,-1);
					
					return (SMSSender)works.get(JUtilRandom.nextInt(works.size()));
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param business
	 * @param senderId
	 * @param to
	 * @param text
	 * @param encoding
	 * @param filePaths
	 * @throws Exception
	 */
	public static void send(String business,String senderId,String to,String text, String encoding, String[] filePaths) throws Exception{
		waitWhileLoading();
		SMSSender sender=selectSender(business,senderId,to);
		if(sender==null){
			throw new Exception("no sender matches,business - "+business+",senderId - "+senderId+",to - "+to);
		}
		sender.send(to,text,encoding,filePaths);
	}
	
	/**
	 * 
	 * @param business
	 * @param senderId
	 * @param to
	 * @param text
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 * @throws Exception
	 */
	public static void send(String business,String senderId,String to, String text, String encoding, String[] filePaths,String fromName) throws Exception{
		waitWhileLoading();
		SMSSender sender=selectSender(business,senderId,to);
		if(sender==null){
			throw new Exception("no sender matches,business - "+business+",senderId - "+senderId+",to - "+to);
		}
		sender.send(to,text,encoding,filePaths,fromName);
	}
	
	/**
	 * 
	 * @param business
	 * @param senderId
	 * @param to
	 * @param texts
	 * @param encoding
	 * @param filePaths
	 * @throws Exception
	 */
	public static void send(String business,String senderId,String to,String[] texts, String encoding, String[] filePaths) throws Exception{
		waitWhileLoading();
		SMSSender sender=selectSender(business,senderId,to);
		if(sender==null){
			throw new Exception("no sender matches,business - "+business+",senderId - "+senderId+",to - "+to);
		}
		sender.send(to,texts,encoding,filePaths);
	}
	
	/**
	 * 
	 * @param business
	 * @param senderId
	 * @param to
	 * @param texts
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 * @throws Exception
	 */
	public static void send(String business,String senderId,String to, String[] texts, String encoding, String[] filePaths,String fromName) throws Exception{
		waitWhileLoading();
		SMSSender sender=selectSender(business,senderId,to);
		if(sender==null){
			throw new Exception("no sender matches,business - "+business+",senderId - "+senderId+",to - "+to);
		}
		sender.send(to,texts,encoding,filePaths,fromName);
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
				List works=(List)values.get(i);
				for(int j=0;j<works.size();j++){
					SMSSender sender=(SMSSender)works.get(j);
					sender.shutdown();
				}
			}
			senders.clear();
			configs.clear();
			
			
			//文件是否存在
			File file = new File(JProperties.getConfigPath()+"sms.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			
			Document document=JUtilDom4j.parse(JProperties.getConfigPath()+"sms.xml","UTF-8");
			Element root=document.getRootElement();
			
			Element verifierEle=root.element("mobile-verifier");
			if(verifierEle!=null){
				List verifierRules=verifierEle.elements("rule");
				
				MobileVerifier.rules=new MobileVerifierRule[verifierRules.size()];
				for(int i=0;i<verifierRules.size();i++){
					Element ruleEle=(Element)verifierRules.get(i);
					MobileVerifierRule rule=new MobileVerifierRule(ruleEle.attributeValue("region"),ruleEle.getTextTrim());
					MobileVerifier.rules[i]=rule;
				}
			}
			
			List senderEles=root.elements("sender");
			for(int i=0;i<senderEles.size();i++){
				Element senderEle=(Element)senderEles.get(i);
				
				SMSSenderConfig config=new SMSSenderConfig();
				config.id=senderEle.elementText("id");
				config.priority=Integer.parseInt(senderEle.elementText("priority"));
				config.region=senderEle.elementText("region");
				config.business=senderEle.elementText("business");
				config.businessName=senderEle.elementText("business-name");
				config.channelImpl=senderEle.elementText("channel-impl");
				config.channelNvwaCode=senderEle.elementText("channel-nvwa-code");
				config.from=senderEle.elementText("from");
				config.fromName=senderEle.elementText("from-name");
				config.threads=Integer.parseInt(senderEle.elementText("threads"));
				config.maxTries=Integer.parseInt(senderEle.elementText("max-tries"));
				
				configs.add(config);
			}
			configs=sorter.bubble(configs, JUtilSorter.DESC);
			
			for(int i=0;i<configs.size();i++){
				SMSSenderConfig config=(SMSSenderConfig)configs.get(i);
				int threads=config.threads;
				
				List works=new LinkedList();
				for(int t=0;t<threads;t++){
					SMSSender sender=new SMSSender(config.id,t,config);
					Thread thread=new Thread(sender);
					thread.start();
					log.log("sms sender "+config.id+","+t+" started.",-1);
					
					works.add(sender);
				}
				
				senders.put(config.id,works);
			}
			
			root=null;
			document=null;

			//配置文件最近修改时间
			File configFile=new File(JProperties.getConfigPath()+"sms.xml");
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
	private static void waitWhileLoading(){
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
		 * 检测sms.xml是否修改过，如修改过重新加载配置
		 */
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			if(configLastModified<=0) continue;

			File configFile=new File(JProperties.getConfigPath()+"sms.xml");
			if(configLastModified<configFile.lastModified()){
				log.log("sms.xml has been modified, so reload it.",-1);
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
		for(int i=0;i<5;i++){
			String str=JUtilString.randomStr(64);
			SMSManager.send("verify",
					"",
					"13800138000",
					str,
					"utf-8",
					null);
		}
	}
}