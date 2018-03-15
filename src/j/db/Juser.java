/*
 * Created on 2016-12-08
 *
 */
package j.db;


import java.io.Serializable;


/**
 * @author JStudio-BeanGenerator
 *
 */
public class Juser implements Serializable{

	private java.lang.String uid;
	private java.lang.String asvrId;
	private java.lang.String asysId;
	private java.lang.String adomain;
	private java.lang.String unum;
	private java.lang.String ustat;
	private java.lang.String spw;
	private java.lang.String smail;
	private java.lang.String smailVerified;
	private java.lang.String sphone;
	private java.lang.String sphoneVerified;
	private java.lang.String stwoDimensionCodes;
	private java.lang.String sloginGuard;
	private java.lang.String unick;
	private java.lang.String uname;
	private java.lang.String ucert;
	private java.lang.String comName;
	private java.lang.String comCert;
	private java.lang.String regVia;
	private java.lang.String regIp;
	private java.sql.Timestamp regTime;
	private java.lang.String loginIp;
	private java.sql.Timestamp loginTime;
	private java.lang.String previousLoginIp;
	private java.sql.Timestamp previousLoginTime;
	private java.lang.Integer loginCount;

	public java.lang.String getUid(){
		return this.uid;
	}
	public void setUid(java.lang.String uid){
		this.uid=uid;
	}

	public java.lang.String getAsvrId(){
		return this.asvrId;
	}
	public void setAsvrId(java.lang.String asvrId){
		this.asvrId=asvrId;
	}

	public java.lang.String getAsysId(){
		return this.asysId;
	}
	public void setAsysId(java.lang.String asysId){
		this.asysId=asysId;
	}

	public java.lang.String getAdomain(){
		return this.adomain;
	}
	public void setAdomain(java.lang.String adomain){
		this.adomain=adomain;
	}

	public java.lang.String getUnum(){
		return this.unum;
	}
	public void setUnum(java.lang.String unum){
		this.unum=unum;
	}

	public java.lang.String getUstat(){
		return this.ustat;
	}
	public void setUstat(java.lang.String ustat){
		this.ustat=ustat;
	}

	public java.lang.String getSpw(){
		return this.spw;
	}
	public void setSpw(java.lang.String spw){
		this.spw=spw;
	}

	public java.lang.String getSmail(){
		return this.smail;
	}
	public void setSmail(java.lang.String smail){
		this.smail=smail;
	}

	public java.lang.String getSmailVerified(){
		return this.smailVerified;
	}
	public void setSmailVerified(java.lang.String smailVerified){
		this.smailVerified=smailVerified;
	}

	public java.lang.String getSphone(){
		return this.sphone;
	}
	public void setSphone(java.lang.String sphone){
		this.sphone=sphone;
	}

	public java.lang.String getSphoneVerified(){
		return this.sphoneVerified;
	}
	public void setSphoneVerified(java.lang.String sphoneVerified){
		this.sphoneVerified=sphoneVerified;
	}

	public java.lang.String getStwoDimensionCodes(){
		return this.stwoDimensionCodes;
	}
	public void setStwoDimensionCodes(java.lang.String stwoDimensionCodes){
		this.stwoDimensionCodes=stwoDimensionCodes;
	}

	public java.lang.String getSloginGuard(){
		return this.sloginGuard;
	}
	public void setSloginGuard(java.lang.String sloginGuard){
		this.sloginGuard=sloginGuard;
	}

	public java.lang.String getUnick(){
		return this.unick;
	}
	public void setUnick(java.lang.String unick){
		this.unick=unick;
	}

	public java.lang.String getUname(){
		return this.uname;
	}
	public void setUname(java.lang.String uname){
		this.uname=uname;
	}

	public java.lang.String getUcert(){
		return this.ucert;
	}
	public void setUcert(java.lang.String ucert){
		this.ucert=ucert;
	}

	public java.lang.String getComName(){
		return this.comName;
	}
	public void setComName(java.lang.String comName){
		this.comName=comName;
	}

	public java.lang.String getComCert(){
		return this.comCert;
	}
	public void setComCert(java.lang.String comCert){
		this.comCert=comCert;
	}

	public java.lang.String getRegVia(){
		return this.regVia;
	}
	public void setRegVia(java.lang.String regVia){
		this.regVia=regVia;
	}

	public java.lang.String getRegIp(){
		return this.regIp;
	}
	public void setRegIp(java.lang.String regIp){
		this.regIp=regIp;
	}

	public java.sql.Timestamp getRegTime(){
		return this.regTime;
	}
	public void setRegTime(java.sql.Timestamp regTime){
		this.regTime=regTime;
	}

	public java.lang.String getLoginIp(){
		return this.loginIp;
	}
	public void setLoginIp(java.lang.String loginIp){
		this.loginIp=loginIp;
	}

	public java.sql.Timestamp getLoginTime(){
		return this.loginTime;
	}
	public void setLoginTime(java.sql.Timestamp loginTime){
		this.loginTime=loginTime;
	}

	public java.lang.String getPreviousLoginIp(){
		return this.previousLoginIp;
	}
	public void setPreviousLoginIp(java.lang.String previousLoginIp){
		this.previousLoginIp=previousLoginIp;
	}

	public java.sql.Timestamp getPreviousLoginTime(){
		return this.previousLoginTime;
	}
	public void setPreviousLoginTime(java.sql.Timestamp previousLoginTime){
		this.previousLoginTime=previousLoginTime;
	}

	public java.lang.Integer getLoginCount(){
		return this.loginCount;
	}
	public void setLoginCount(java.lang.Integer loginCount){
		this.loginCount=loginCount;
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
