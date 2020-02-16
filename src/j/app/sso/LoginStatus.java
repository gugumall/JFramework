package j.app.sso;


import j.sys.SysUtil;
import j.util.ConcurrentMap;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpSession;


/**
 * 
 * @author 肖炯
 *
 */
public class LoginStatus implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final int STAT_CREATE=0;//登录状态-刚接收到server端某个用户登录的命令，但该用户还未曾到访
	public static final int STAT_VISITED=1;//登录状态-接收到server端某个用户登录的命令，并且该用户已经到访并成功加载用户信息
	
	private String           clientId;//SSO Client ID
	private String           globalSessionId;//全局会话ID
	private String           sessionId;//相关联的session ID
	private volatile long    refreshTime;//最近更新缓存时间
	private volatile long    updateTime;//最近访问系统的时间
	private String           userId;//用户ID
	private String           subUserId;//子账号ID
	private String           userIp;//用户Host
	private String           sysId;
	private String           machineId;
	private String           loginFrom;
	private String           loginFromDomain;
	private String           userAgent;
	private int              stat;//登录状态
	private ConcurrentMap    messages=new ConcurrentMap();

	
	/**
	 * 
	 * @param _clientId
	 * @param _session
	 * @param _globalSessionId
	 * @param _userId
	 * @param _userIp
	 * @param _sysId
	 * @param _machineId
	 * @param _loginFrom
	 */
	public LoginStatus(String _clientId,
			HttpSession _session,
			String _globalSessionId,
			String _userId,
			String _userIp,
			String _sysId,
			String _machineId,
			String _loginFrom,
			String _loginFromDomain){
		if(_session!=null){
			SSOContext.addSession(_session);
			sessionId=_session.getId();
		}
		clientId=_clientId;
		globalSessionId=_globalSessionId;
		userId=_userId;
		userIp=_userIp;
		sysId=_sysId;
		machineId=_machineId;
		loginFrom=_loginFrom;
		loginFromDomain=_loginFromDomain;
		refreshTime=SysUtil.getNow();
		updateTime=SysUtil.getNow();
		stat=STAT_CREATE;
	}
	
	//getters
	public String getClientId(){
		return clientId;
	}
	public String getGlobalSessionId(){
		return globalSessionId;
	}
	public String getSessionId(){
		return this.sessionId;
	}
	public long getRefreshTime(){
		return this.refreshTime;
	}
	public long getUpdateTime(){
		return this.updateTime;
	}	
	public String getUserId(){
		return this.userId;
	}
	public String getSubUserId(){
		return this.subUserId;
	}
	public String getUserIp(){
		return this.userIp;
	}
	public String getSysId(){
		return this.sysId;
	}
	public String getMachineId(){
		return this.machineId;
	}
	public String getLoginFrom(){
		return this.loginFrom;
	}
	public String getLoginFromDomain(){
		return this.loginFromDomain;
	}
	public String getUserAgent(){
		return this.userAgent;
	}
	public boolean isTimeout(){
		return SysUtil.getNow()-updateTime>SSOConfig.getSessionTimeout()*1000L;
	}
	public int getStat(){
		return this.stat;
	}
	public Object getMessage(Object key){
		return messages.get(key);
	}
	public Map getMessages(){
		return messages;
	}
	//getters end
	
	//setters
	public void setUserHost(String _userHost){
		userIp=_userHost;
	}
	public void update(){
		updateTime=SysUtil.getNow();
	}	
	public void setUpdateTime(long _updateTime){
		updateTime=_updateTime;
	}
	public void setSubUserId(String subUserId){
		this.subUserId=subUserId;
	}
	public void setUserIp(String _userIp){
		this.userIp=_userIp;
	}
	public void setSession(HttpSession _session){
		sessionId=_session.getId();
		SSOContext.addSession(_session);
	}
	public void login(){
		this.stat=STAT_VISITED;
	}
	public void setMessage(Object key,Object val){
		messages.put(key,val);
	}
	public void setLoginFromDomain(String _loginFromDomain){
		this.loginFromDomain=_loginFromDomain;
	}
	public void setUserAgent(String userAgent){
		this.userAgent=userAgent;
	}
	//setters end
}