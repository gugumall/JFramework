package j.sms;


/**
 * 
 * @author 肖炯
 *
 */
public class SMSSenderConfig {	
	protected String id;
	protected int priority=0;
	protected String region;
	protected String business;
	protected String businessName;
	protected String channelImpl;
	protected String channelNvwaCode;
	protected String from;
	protected String fromName;
	protected int threads;
	protected int maxTries;
	
	public void setId(String id){
		this.id=id;
	}
	public String getId(){
		return this.id;
	}
	
	public void setPriority(int priority){
		this.priority=priority;
	}
	public int getPriority(){
		return this.priority;
	}
	
	
	public void setRegion(String region){
		this.region=region;
	}
	public String getRegion(){
		return this.region;
	}
	
	
	public void setBusiness(String business){
		this.business=business;
	}
	public String getBusiness(){
		return this.business;
	}
	
	
	public void setBusinessName(String businessName){
		this.businessName=businessName;
	}
	public String getBusinessName(){
		return this.businessName;
	}
	
	
	public void setChannelImpl(String channelImpl){
		this.channelImpl=channelImpl;
	}
	public String getChannelImpl(){
		return this.channelImpl;
	}
	
	
	public void setChannelNavaCode(String channelNvwaCode){
		this.channelNvwaCode=channelNvwaCode;
	}
	public String getChannelNavaCode(){
		return this.channelNvwaCode;
	}
	
	
	public void setFrom(String from){
		this.from=from;
	}
	public String getFrom(){
		return this.from;
	}
	
	
	public void setFromName(String fromName){
		this.fromName=fromName;
	}
	public String getFromName(){
		return this.fromName;
	}
	
	public void setThreads(int threads){
		this.threads=threads;
	}
	public int getThreads(){
		return this.threads;
	}
	
	public void setMaxTries(int maxTries){
		this.maxTries=maxTries;
	}
	public int getMaxTries(){
		return this.maxTries;
	}
}