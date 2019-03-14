/*
 * Created on 2019-03-14
 *
 */
package j.db;


import java.io.Serializable;


/**
 * @author JFramework-BeanGenerator
 *
 */
public class JuserLogin implements Serializable{

	private java.lang.String uuid;
	private java.lang.String userId;
	private java.lang.String userAgentSn;
	private java.lang.String userIp;
	private java.lang.String thirdpartyCode;
	private java.lang.String thirdpartyUserId;
	private java.lang.Long loginTimeTry;
	private java.lang.Long loginTimeOk;
	private java.lang.Long loginTimeAuto;
	private java.lang.String loginStatus;
	private java.lang.String loginMethod;
	private java.lang.Short loginFailedTimes;
	private java.lang.String appidLoginFrom;
	private java.lang.String sessionIdLoginFrom;
	private java.lang.String sessionIdGlobal;

	public java.lang.String getUuid(){
		return this.uuid;
	}
	public void setUuid(java.lang.String uuid){
		this.uuid=uuid;
	}

	public java.lang.String getUserId(){
		return this.userId;
	}
	public void setUserId(java.lang.String userId){
		this.userId=userId;
	}

	public java.lang.String getUserAgentSn(){
		return this.userAgentSn;
	}
	public void setUserAgentSn(java.lang.String userAgentSn){
		this.userAgentSn=userAgentSn;
	}

	public java.lang.String getUserIp(){
		return this.userIp;
	}
	public void setUserIp(java.lang.String userIp){
		this.userIp=userIp;
	}

	public java.lang.String getThirdpartyCode(){
		return this.thirdpartyCode;
	}
	public void setThirdpartyCode(java.lang.String thirdpartyCode){
		this.thirdpartyCode=thirdpartyCode;
	}

	public java.lang.String getThirdpartyUserId(){
		return this.thirdpartyUserId;
	}
	public void setThirdpartyUserId(java.lang.String thirdpartyUserId){
		this.thirdpartyUserId=thirdpartyUserId;
	}

	public java.lang.Long getLoginTimeTry(){
		return this.loginTimeTry;
	}
	public void setLoginTimeTry(java.lang.Long loginTimeTry){
		this.loginTimeTry=loginTimeTry;
	}

	public java.lang.Long getLoginTimeOk(){
		return this.loginTimeOk;
	}
	public void setLoginTimeOk(java.lang.Long loginTimeOk){
		this.loginTimeOk=loginTimeOk;
	}

	public java.lang.Long getLoginTimeAuto(){
		return this.loginTimeAuto;
	}
	public void setLoginTimeAuto(java.lang.Long loginTimeAuto){
		this.loginTimeAuto=loginTimeAuto;
	}

	public java.lang.String getLoginStatus(){
		return this.loginStatus;
	}
	public void setLoginStatus(java.lang.String loginStatus){
		this.loginStatus=loginStatus;
	}

	public java.lang.String getLoginMethod(){
		return this.loginMethod;
	}
	public void setLoginMethod(java.lang.String loginMethod){
		this.loginMethod=loginMethod;
	}

	public java.lang.Short getLoginFailedTimes(){
		return this.loginFailedTimes;
	}
	public void setLoginFailedTimes(java.lang.Short loginFailedTimes){
		this.loginFailedTimes=loginFailedTimes;
	}

	public java.lang.String getAppidLoginFrom(){
		return this.appidLoginFrom;
	}
	public void setAppidLoginFrom(java.lang.String appidLoginFrom){
		this.appidLoginFrom=appidLoginFrom;
	}

	public java.lang.String getSessionIdLoginFrom(){
		return this.sessionIdLoginFrom;
	}
	public void setSessionIdLoginFrom(java.lang.String sessionIdLoginFrom){
		this.sessionIdLoginFrom=sessionIdLoginFrom;
	}

	public java.lang.String getSessionIdGlobal(){
		return this.sessionIdGlobal;
	}
	public void setSessionIdGlobal(java.lang.String sessionIdGlobal){
		this.sessionIdGlobal=sessionIdGlobal;
	}

	public boolean equals(Object obj){
		return super.equals(obj);
	}

	public int hashCode(){
		return super.hashCode();
	}

	public String toString(){
		return super.toString();
	}

}
