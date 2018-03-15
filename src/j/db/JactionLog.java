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
public class JactionLog implements Serializable{

	private java.lang.String eventId;
	private java.lang.String asvrId;
	private java.lang.String asysId;
	private java.lang.String adomain;
	private java.lang.String aurl;
	private java.lang.String auIp;
	private java.lang.String auId;
	private java.lang.String actionHandler;
	private java.lang.String actionId;
	private java.lang.String actionParameters;
	private java.lang.String actionResult;
	private java.lang.String eventStat;
	private java.sql.Timestamp eventTime;
	private java.lang.String delBySys;

	public java.lang.String getEventId(){
		return this.eventId;
	}
	public void setEventId(java.lang.String eventId){
		this.eventId=eventId;
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

	public java.lang.String getAurl(){
		return this.aurl;
	}
	public void setAurl(java.lang.String aurl){
		this.aurl=aurl;
	}

	public java.lang.String getAuIp(){
		return this.auIp;
	}
	public void setAuIp(java.lang.String auIp){
		this.auIp=auIp;
	}

	public java.lang.String getAuId(){
		return this.auId;
	}
	public void setAuId(java.lang.String auId){
		this.auId=auId;
	}

	public java.lang.String getActionHandler(){
		return this.actionHandler;
	}
	public void setActionHandler(java.lang.String actionHandler){
		this.actionHandler=actionHandler;
	}

	public java.lang.String getActionId(){
		return this.actionId;
	}
	public void setActionId(java.lang.String actionId){
		this.actionId=actionId;
	}

	public java.lang.String getActionParameters(){
		return this.actionParameters;
	}
	public void setActionParameters(java.lang.String actionParameters){
		this.actionParameters=actionParameters;
	}

	public java.lang.String getActionResult(){
		return this.actionResult;
	}
	public void setActionResult(java.lang.String actionResult){
		this.actionResult=actionResult;
	}

	public java.lang.String getEventStat(){
		return this.eventStat;
	}
	public void setEventStat(java.lang.String eventStat){
		this.eventStat=eventStat;
	}

	public java.sql.Timestamp getEventTime(){
		return this.eventTime;
	}
	public void setEventTime(java.sql.Timestamp eventTime){
		this.eventTime=eventTime;
	}

	public java.lang.String getDelBySys(){
		return this.delBySys;
	}
	public void setDelBySys(java.lang.String delBySys){
		this.delBySys=delBySys;
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
