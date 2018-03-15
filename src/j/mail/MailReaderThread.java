package j.mail;

import j.log.Logger;

/**
 * 
 * @author one
 * 
 */
public class MailReaderThread implements Runnable{
	private static Logger log=Logger.create(MailReaderThread.class);// 日志输出
	private String senderId=null;
	private boolean shutdonw=false;
	private long executed=0;

	/**
	 * 
	 */
	public MailReaderThread(){
		executed=System.currentTimeMillis();
	}
	
	/**
	 * 
	 * @param senderId
	 */
	public MailReaderThread(String senderId){
		executed=System.currentTimeMillis();
		this.senderId=senderId;
	}
	
	/**
	 * 
	 * @param senderId
	 */
	public void setSenderId(String senderId){
		this.senderId=senderId;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSenderId(){
		return this.senderId;
	}
	
	/**
	 * 
	 */
	public void shutdown(){
		this.shutdonw=true;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isBlocked(){
		MailSenderConfig config=MailManager.getConfig(senderId);
		if(config==null||config.getReaderInterval()<=0){
			return true;
		}
		
		return System.currentTimeMillis()-this.executed>config.getReaderInterval()*4;
	}

	@Override
	public void run(){
		try{
			Thread.sleep(5000);
		}catch(Exception e){}
		
		while(true&&!shutdonw){
			try{
				MailManager.waitWhileLoading();
				
				MailSenderConfig config=MailManager.getConfig(senderId);
				if(config==null){
					log.log("mail reader "+senderId+" ended.",-1);
					return;
				}
				
				if(config.getReaderThread()<=0){
					log.log("mail reader "+senderId+" paused beacause the interval is less than zero.",-1);
					this.executed=System.currentTimeMillis();
					
					try{
						Thread.sleep(5000);
					}catch(Exception e){}
					
					continue;
				}
				
				log.log(senderId+" try to read mail",-1);
				
				MailReader reader=MailManager.getReader(senderId);
				if(reader.connect()){
					log.log(senderId+" begin read mail",-1);
					reader.read(config.getReaderFolder(),config.getReaderCount(),config.getReaderFlagRead());
					reader.close();
					log.log(senderId+" end read mail",-1);
					
					this.executed=System.currentTimeMillis();
				}
				
				try{
					Thread.sleep(config.getReaderInterval());
				}catch(Exception e){}
			}catch(Exception e){
				this.executed=System.currentTimeMillis();
				log.log(e,Logger.LEVEL_ERROR);
			}
		}
	}
}
