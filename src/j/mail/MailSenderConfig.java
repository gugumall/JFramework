package j.mail;

import j.util.ConcurrentMap;
import j.util.JUtilMath;




/**
 * 
 * @author 肖炯
 *
 */
public class MailSenderConfig {	
	private String id;
	private String desc;
	private String host;
	private String port;
	private String user;
	private String readerProtocol;
	private String readerHost;
	private String readerPort;
	private String readerManager;
	private int readerThread;
	private long readerInterval;
	private int readerCount;
	private boolean readFlagRead;
	private String readerFolder;
	private String readerName;
	private String readerVersion;
	private String password;
	private String authCode;
	private String from;
	private String fromName;
	private boolean secure;
	private int maxTries;
	private ConcurrentMap params=new ConcurrentMap();
	
	public void setId(String id){
		this.id=id;
	}
	public String getId(){
		return this.id;
	}
	
	public void setDesc(String desc){
		this.desc=desc;
	}
	public String getDesc(){
		return this.desc;
	}
	
	
	public void setHost(String host){
		this.host=host;
	}
	public String getHost(){
		return this.host;
	}
	
	
	public void setPort(String port){
		this.port=port;
	}
	public String getPort(){
		return this.port;
	}

	public void setReaderProtocol(String readerProtocol){
		this.readerProtocol=readerProtocol;
	}
	public String getReaderProtocol(){
		return this.readerProtocol;
	}

	public void setReaderHost(String readerHost){
		this.readerHost=readerHost;
	}
	public String getReaderHost(){
		return this.readerHost;
	}

	public void setReaderPort(String readerPort){
		this.readerPort=readerPort;
	}
	public String getReaderPort(){
		return this.readerPort;
	}

	public void setReaderManager(String readerManager){
		this.readerManager=readerManager;
	}
	public String getReaderManager(){
		return this.readerManager;
	}

	public void setReaderThread(String readerThread){
		if(JUtilMath.isInt(readerThread)){
			this.readerThread=Integer.parseInt(readerThread);
		}else{
			this.readerThread=0;
		}
	}
	public int getReaderThread(){
		return this.readerThread;
	}
	
	public void setReaderInterval(String readerInterval){
		if(JUtilMath.isLong(readerInterval)){
			this.readerInterval=Long.parseLong(readerInterval);
		}else{
			this.readerInterval=60000;
		}
	}
	public long getReaderInterval(){
		return this.readerInterval;
	}

	public void setReaderCount(String readerCount){
		if(JUtilMath.isInt(readerCount)){
			this.readerCount=Integer.parseInt(readerCount);
		}else{
			this.readerCount=0;
		}
	}
	public int getReaderCount(){
		return this.readerCount;
	}
	
	
	public void setReaderFlagRead(String readerFlagRead){
		this.readFlagRead="true".equals(readerFlagRead);
	}
	public boolean getReaderFlagRead(){
		return this.readFlagRead;
	}

	public void setReaderFolder(String readerFolder){
		this.readerFolder=readerFolder;
	}
	public String getReaderFolder(){
		return this.readerFolder;
	}

	public void setReaderName(String readerName){
		this.readerName=readerName;
	}
	public String getReaderName(){
		return this.readerName;
	}

	public void setReaderVersion(String readerVersion){
		this.readerVersion=readerVersion;
	}
	public String getReaderVersion(){
		return this.readerVersion;
	}
	
	public void setUser(String user){
		this.user=user;
	}
	public String getUser(){
		return this.user;
	}
	
	
	public void setPassword(String password){
		this.password=password;
	}
	public String getPassword(){
		return this.password;
	}
	
	public void setAuthCode(String authCode){
		this.authCode=authCode;
	}
	public String getAuthCode(){
		return this.authCode;
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
	
	
	public void setSecure(boolean secure){
		this.secure=secure;
	}
	public boolean getSecure(){
		return this.secure;
	}
	
	
	public void setMaxTries(int maxTries){
		this.maxTries=maxTries;
	}
	public int getMaxTries(){
		return this.maxTries;
	}
	
	public void setParam(String key,String value){
		params.put(key,value);
	}
	public String getParam(String key){
		return key==null?null:(String)params.get(key);
	}
}