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
public class Jlog implements Serializable{

	private java.lang.String eventId;
	private java.lang.String asvrId;
	private java.lang.String asysId;
	private java.lang.String adomain;
	private java.lang.String aurl;
	private java.lang.String auIp;
	private java.lang.String auId;
	private java.lang.String bizCode;
	private java.lang.String bizId;
	private java.lang.String bizName;
	private java.lang.String bizLink;
	private java.lang.String bizIcon;
	private java.lang.String bizData;
	private java.sql.Timestamp eventTime;
	private java.lang.String eventCode;
	private java.lang.String eventData;
	private java.lang.String eventStat;
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

	public java.lang.String getBizCode(){
		return this.bizCode;
	}
	public void setBizCode(java.lang.String bizCode){
		this.bizCode=bizCode;
	}

	public java.lang.String getBizId(){
		return this.bizId;
	}
	public void setBizId(java.lang.String bizId){
		this.bizId=bizId;
	}

	public java.lang.String getBizName(){
		return this.bizName;
	}
	public void setBizName(java.lang.String bizName){
		this.bizName=bizName;
	}

	public java.lang.String getBizLink(){
		return this.bizLink;
	}
	public void setBizLink(java.lang.String bizLink){
		this.bizLink=bizLink;
	}

	public java.lang.String getBizIcon(){
		return this.bizIcon;
	}
	public void setBizIcon(java.lang.String bizIcon){
		this.bizIcon=bizIcon;
	}

	public java.lang.String getBizData(){
		return this.bizData;
	}
	public void setBizData(java.lang.String bizData){
		this.bizData=bizData;
	}

	public java.sql.Timestamp getEventTime(){
		return this.eventTime;
	}
	public void setEventTime(java.sql.Timestamp eventTime){
		this.eventTime=eventTime;
	}

	public java.lang.String getEventCode(){
		return this.eventCode;
	}
	public void setEventCode(java.lang.String eventCode){
		this.eventCode=eventCode;
	}

	public java.lang.String getEventData(){
		return this.eventData;
	}
	public void setEventData(java.lang.String eventData){
		this.eventData=eventData;
	}

	public java.lang.String getEventStat(){
		return this.eventStat;
	}
	public void setEventStat(java.lang.String eventStat){
		this.eventStat=eventStat;
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
