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
public class Jblacklist implements Serializable{

	private java.lang.String blackId;
	private java.lang.String uip;
	private java.lang.String uaddr;
	private java.lang.String blackType;
	private java.sql.Timestamp startTime;
	private java.sql.Timestamp endTime;
	private java.lang.String blackRemark;

	public java.lang.String getBlackId(){
		return this.blackId;
	}
	public void setBlackId(java.lang.String blackId){
		this.blackId=blackId;
	}

	public java.lang.String getUip(){
		return this.uip;
	}
	public void setUip(java.lang.String uip){
		this.uip=uip;
	}

	public java.lang.String getUaddr(){
		return this.uaddr;
	}
	public void setUaddr(java.lang.String uaddr){
		this.uaddr=uaddr;
	}

	public java.lang.String getBlackType(){
		return this.blackType;
	}
	public void setBlackType(java.lang.String blackType){
		this.blackType=blackType;
	}

	public java.sql.Timestamp getStartTime(){
		return this.startTime;
	}
	public void setStartTime(java.sql.Timestamp startTime){
		this.startTime=startTime;
	}

	public java.sql.Timestamp getEndTime(){
		return this.endTime;
	}
	public void setEndTime(java.sql.Timestamp endTime){
		this.endTime=endTime;
	}

	public java.lang.String getBlackRemark(){
		return this.blackRemark;
	}
	public void setBlackRemark(java.lang.String blackRemark){
		this.blackRemark=blackRemark;
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
