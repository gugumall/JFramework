package j.sms;

import j.log.Logger;

/**
 * 
 * @author 肖炯
 *
 */
public class SMSChannelDefault implements SMSChannel{
	private static Logger log=Logger.create(SMSChannelDefault.class);//日志输出
	
	
	/**
	 * 是否可送到（该短信通道是否可给某号码发送短信）
	 * @param dest 目标号码
	 * @return
	 */
	public boolean reachable(String dest){
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.sms.SMSChannel#send(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean send(String to,String text,String encoding,String fromName) throws Exception{
		log.log("send sms to "+to+":"+text, -1);
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.sms.SMSChannel#send(java.lang.String, java.lang.String, java.lang.String, java.lang.String[], java.lang.String)
	 */
	public boolean send(String to,String text, String encoding, String[] filePaths,String fromName) throws Exception{
		log.log("send sms(include files) to "+to+":"+text, -1);
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.sms.SMSChannel#send(java.lang.String, java.lang.String[], java.lang.String, java.lang.String)
	 */
	public boolean send(String to,String[] texts,String encoding,String fromName) throws Exception{
		String content="";
		for(int i=0;i<texts.length;i++){
			content+="{"+texts[i]+"}";
		}
		log.log("send sms to "+to+":"+content, -1);
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.sms.SMSChannel#send(java.lang.String, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	public boolean send(String to,String[] texts, String encoding, String[] filePaths,String fromName) throws Exception{
		String content="";
		for(int i=0;i<texts.length;i++){
			content+="{"+texts[i]+"}";
		}
		log.log("send sms(include files) to "+to+":"+content, -1);
		return true;
	}
}
