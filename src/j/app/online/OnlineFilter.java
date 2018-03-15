package j.app.online;

import j.cache.JCacheFilter;
import j.util.JUtilString;

/**
 * 
 * @author 肖炯
 *
 */
public class OnlineFilter implements JCacheFilter{
	private static final long serialVersionUID = 1L;
	private String serviceStaffId;
	private String ip;
	private String uid;
	private String uname;
	private String unick;
	private String[] uids;
	private int login=-100;//-100,全部; 0，未登录的用户; 1，登录的用户
	private String sessionId;
	private String sysId;
	private String machineId;
	private int chatting=-100;//-100,全部; 其它，聊天状态
	
	/**
	 * 
	 */
	public OnlineFilter(){
		
	}
	
	//getters and setters
	public String getIp(){
		return ip;
	}
	public void setIp(String ip){
		this.ip=ip;
	}
	
	public String getUid(){
		return uid;
	}
	public void setUid(String uid){
		this.uid=uid;
	}
	
	public String getUname(){
		return this.uname;
	}
	public void setUname(String uname){
		this.uname=uname;
	}
	
	public String getUnick(){
		return this.unick;
	}
	public void setUnick(String unick){
		this.unick=unick;
	}
	
	public String getSessionId(){
		return sessionId;
	}
	public void setSessionId(String sessionId){
		this.sessionId=sessionId;
	}
	
	public String getSysId(){
		return this.sysId;
	}
	public void setSysId(String sysId){
		this.sysId=sysId;
	}
	
	public String getMachineId(){
		return this.machineId;
	}
	public void setMachineId(String machineId){
		this.machineId=machineId;
	}
	
	public String getServiceStaffId(){
		return this.serviceStaffId;
	}
	public void setServiceStaffId(String serviceStaffId){
		this.serviceStaffId=serviceStaffId;
	}

	public String[] getUids(){
		return uids;
	}
	public void setUids(String[] uids){
		this.uids=uids;
	}

	public int getLogin(){
		return this.login;
	}
	public void setLogin(int login){
		this.login=login;
	}
	
	public int getChatting(){
		return this.chatting;
	}
	public void setChatting(int chatting){
		this.chatting=chatting;
	}
	//getters and setters end

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheFilter#matches(java.lang.Object)
	 */
	public boolean matches(Object obj) {
		if(obj==null) return false;
		
		Online v=(Online)obj;
		
		if(ip!=null&&!"".equals(ip)&&!ip.equals(v.getCurrentIp())) return false;
		
		if(uid!=null&&!"".equals(uid)&&!uid.equals(v.getUid())) return false;

		if(uname!=null&&!"".equals(uname)&&!uname.equals(v.getUname())) return false;

		if(unick!=null&&!"".equals(unick)&&!unick.equals(v.getUnick())) return false;

		if(uids!=null&&!JUtilString.contain(uids,v.getUid())) return false;

		if(login==0&&!v.getUsers().isEmpty()) return false;

		if(login==1&&v.getUsers().isEmpty()) return false;
		
		if(sessionId!=null&&!"".equals(sessionId)&&!sessionId.equals(v.getCurrentSessionId())) return false;

		if(sysId!=null&&!"".equals(sysId)&&!sysId.equals(v.getCurrentSysId())) return false;
		
		if(machineId!=null&&!"".equals(machineId)&&!machineId.equals(v.getCurrentMachineId())) return false;

		if(serviceStaffId!=null&&!"".equals(serviceStaffId)&&!serviceStaffId.equals(v.getServiceStaffId())) return false;
		
		if(chatting!=-100&&chatting!=v.getChatting()) return false;
		
		return true;
	}
}
