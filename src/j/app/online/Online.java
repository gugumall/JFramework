package j.app.online;

import j.app.sso.User;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author 肖炯
 *
 */
public class Online implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final int FOUND_BY_SESSION_ID=1;
	public static final int FOUND_BY_GLOBAL_SESSION_ID=2;

	private long updateTime;
	private long createTime;
	
	private String currentIp;
	private String currentSysId;
	private String currentMachineId;
	private String currentSessionId;
	private String currentReferer;
	private String currentUserAgent;
	private String currentUrl;

	private String globalSessionId;//全局会话ID
	private String uid;
	private String unum;
	private String mail;
	private String phone;
	private String uname;
	private String unick;
	private String comName;
	
	//在各个sso client登录的用户对象，key为  系统ID+"."+机器ID，value为 j.app.sso.User的实现类（保存为序列化后的字符串）
	private ConcurrentMap usersOnClients=new ConcurrentMap();
	
	///////////////////（用于在线客服）///////////////
	private ConcurrentList messagesIn=new ConcurrentList();//收到的信息（来自管理员）
	private ConcurrentList messagesOut=new ConcurrentList();//发送的信息（来自管理员）
	private ConcurrentMap messagesWithSellerIn=new ConcurrentMap();//收到的信息	（来自卖家）
	private ConcurrentMap messagesWithSellerOut=new ConcurrentMap();//发送的信息（来自卖家）
	private String serviceStaffId=null;//客服人员ID（来自管理员）
	private ConcurrentMap serviceWithSellerStaffId=new ConcurrentMap();//客服人员ID（来自卖家）
	private int chatting=0;//聊天状态
	///////////////////（用于在线客服） end///////////////
	
	//
	private int foundBy=-1;
	
	public volatile long firstRequestTime=0;
	public volatile long latestRequestTime=0;
	public volatile long requests=0;
	
	public Online(){
		this.createTime=SysUtil.getNow();
		this.updateTime=this.createTime;
		this.currentSysId=SysConfig.getSysId();
		this.currentMachineId=SysConfig.getMachineID();
	}
	
	//setters and getters
	public void update(){
		this.updateTime=SysUtil.getNow();
	}
	public long getUpdateTime(){
		return this.updateTime;
	}
	
	public long getCreateTime(){
		return this.createTime;
	}
	
	public void setCurrentIp(String ip){
		this.currentIp=ip;
	}
	public String getCurrentIp(){
		return this.currentIp;
	}
	
	public void setCurrentSysId(String sysId){
		this.currentSysId=sysId;
	}
	public String getCurrentSysId(){
		return this.currentSysId;
	}
	
	public void setCurrentMachineId(String machineId){
		this.currentMachineId=machineId;
	}
	public String getCurrentMachineId(){
		return  this.currentMachineId;
	}
	
	public void setCurrentSessionId(String sessionId){
		this.currentSessionId=sessionId;
	}
	public String getCurrentSessionId(){
		return this.currentSessionId;
	}
	
	public void setCurrentReferer(String referer){
		this.currentReferer=referer;
	}
	public String getCurrentReferer(){
		return this.currentReferer;
	}
	
	public void setCurrentUserAgent(String userAgent){
		this.currentUserAgent=userAgent;
	}
	public String getCurrentUserAgent(){
		return this.currentUserAgent;
	}
	
	public void setCurrentUrl(String url){
		this.currentUrl=url;
	}
	public String getCurrentUrl(){
		return this.currentUrl;
	}
	
	
	
	public void setGlobalSessionId(String globalSessionId){
		this.globalSessionId=globalSessionId;
	}
	public String getGlobalSessionId(){
		return  this.globalSessionId;
	}
	
	public void setUid(String uid){
		this.uid=uid;
	}
	public String getUid(){
		return this.uid;
	}
	
	public void setUnum(String unum){
		this.unum=unum;
	}
	public String getUnum(){
		return this.unum;
	}
	
	public void setMail(String mail){
		this.mail=mail;
	}
	public String getMail(){
		return this.mail;
	}
	
	public void setPhone(String phone){
		this.phone=phone;
	}
	public String getPhone(){
		return this.phone;
	}
	
	public void setUname(String uname){
		this.uname=uname;
	}
	public String getUname(){
		return this.uname;
	}
	
	public void setUnick(String unick){
		this.unick=unick;
	}
	public String getUnick(){
		return this.unick;
	}
	
	public void setComName(String comName){
		this.comName=comName;
	}
	public String getComName(){
		return this.comName;
	}
	
	public void setUser(User user){
		String key=SysConfig.getSysId()+"."+SysConfig.getMachineID();
		this.usersOnClients.put(key, user);
	}
	public void removeUser(){
		String key=SysConfig.getSysId()+"."+SysConfig.getMachineID();
		this.usersOnClients.remove(key);
	}
	public User getUser(){
		String key=SysConfig.getSysId()+"."+SysConfig.getMachineID();
		return (User)this.usersOnClients.get(key);
	}
	public User getUser(String sysId,String machineId){
		String key=sysId+"."+machineId;
		return (User)this.usersOnClients.get(key);
	}
	public List getUsers(){
		return this.usersOnClients.listValues();
	}
	
	public int getFoundBy(){
		return this.foundBy;
	}
	public void setFoundBy(int foundBy){
		this.foundBy=foundBy;
	}
	//setters and getters end
	
	//chatting
	public void addMessageIn(Serializable msg){
		this.messagesIn.add(msg);
	}
	public ConcurrentList getMessagesIn(){
		return this.messagesIn;
	}
	public void clearMessageIn(){
		this.messagesIn.clear();
	}
	public void removeMessageInFirst(){
		if(!this.messagesIn.isEmpty()){
			this.messagesIn.remove(0);
		}
	}
	
	public void addMessageOut(Object msg){
		this.messagesOut.add(msg);
	}
	public ConcurrentList getMessagesOut(){
		return this.messagesOut;
	}
	public void clearMessageOut(){
		this.messagesOut.clear();
	}
	public void removeMessageOutFirst(){
		if(!this.messagesOut.isEmpty()){
			this.messagesOut.remove(0);
		}
	}
	
	public void setServiceStaffId(String serviceStaffId){
		this.serviceStaffId=serviceStaffId;
	}
	public String getServiceStaffId(){
		return this.serviceStaffId;
	}
	
	public void addMessageIn(Serializable msg,String sellerId){
		ConcurrentList temp=(ConcurrentList)messagesWithSellerIn.get(sellerId);
		if(temp==null) temp=new ConcurrentList();
		temp.add(msg);
		messagesWithSellerIn.put(sellerId,temp);
	}
	public ConcurrentList getMessagesIn(String sellerId){
		ConcurrentList temp=(ConcurrentList)messagesWithSellerIn.get(sellerId);
		if(temp==null) temp=new ConcurrentList();
		return temp;
	}
	public void clearMessageIn(String sellerId){
		messagesWithSellerIn.remove(sellerId);
	}
	public void removeMessageInFirst(String sellerId){
		ConcurrentList temp=(ConcurrentList)messagesWithSellerIn.get(sellerId);
		if(temp==null) temp=new ConcurrentList();
		
		if(!temp.isEmpty()){
			temp.remove(0);
		}
		messagesWithSellerIn.put(sellerId,temp);
	}
	
	public void addMessageOut(Serializable msg,String sellerId){
		ConcurrentList temp=(ConcurrentList)messagesWithSellerOut.get(sellerId);
		if(temp==null) temp=new ConcurrentList();
		temp.add(msg);
		messagesWithSellerOut.put(sellerId,temp);
	}
	public ConcurrentList getMessagesOut(String sellerId){
		ConcurrentList temp=(ConcurrentList)messagesWithSellerOut.get(sellerId);
		if(temp==null) temp=new ConcurrentList();
		return temp;
	}
	public void clearMessageOut(String sellerId){
		messagesWithSellerOut.remove(sellerId);
	}
	public void removeMessageOutFirst(String sellerId){
		ConcurrentList temp=(ConcurrentList)messagesWithSellerOut.get(sellerId);
		if(temp==null) temp=new ConcurrentList();
		
		if(!temp.isEmpty()){
			temp.remove(0);
		}
		messagesWithSellerOut.put(sellerId,temp);
	}
	
	public void setServiceStaffId(String serviceStaffId,String sellerId){
		serviceWithSellerStaffId.put(sellerId,serviceStaffId);
	}
	public String getServiceStaffId(String sellerId){
		return (String)serviceWithSellerStaffId.get(sellerId);
	}
	
	public void setChatting(int chatting){
		if(chatting!=Onlines.CHATTING_PENDING
				&&chatting!=Onlines.CHATTING_WAITING
				&&chatting!=Onlines.CHATTING_INPROCESS
				&&chatting!=Onlines.CHATTING_ENDED
				&&chatting!=Onlines.CHATTING_REFUSED
				&&chatting!=Onlines.CHATTING_REFUSED_SESSION){
			return;
		}
		
		this.chatting=chatting;
	}
	public int getChatting(){
		return this.chatting;
	}
	//chatting end
}
