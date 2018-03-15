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
public class JfsTask implements Serializable{

	private java.lang.String uuid;
	private java.sql.Timestamp taskTime;
	private java.lang.String fromUuid;
	private java.lang.String toUuid;
	private java.lang.String filePath;
	private java.lang.String taskOperation;
	private java.lang.String taskData;
	private java.lang.Integer synTimes;
	private java.sql.Timestamp synTime;

	public java.lang.String getUuid(){
		return this.uuid;
	}
	public void setUuid(java.lang.String uuid){
		this.uuid=uuid;
	}

	public java.sql.Timestamp getTaskTime(){
		return this.taskTime;
	}
	public void setTaskTime(java.sql.Timestamp taskTime){
		this.taskTime=taskTime;
	}

	public java.lang.String getFromUuid(){
		return this.fromUuid;
	}
	public void setFromUuid(java.lang.String fromUuid){
		this.fromUuid=fromUuid;
	}

	public java.lang.String getToUuid(){
		return this.toUuid;
	}
	public void setToUuid(java.lang.String toUuid){
		this.toUuid=toUuid;
	}

	public java.lang.String getFilePath(){
		return this.filePath;
	}
	public void setFilePath(java.lang.String filePath){
		this.filePath=filePath;
	}

	public java.lang.String getTaskOperation(){
		return this.taskOperation;
	}
	public void setTaskOperation(java.lang.String taskOperation){
		this.taskOperation=taskOperation;
	}

	public java.lang.String getTaskData(){
		return this.taskData;
	}
	public void setTaskData(java.lang.String taskData){
		this.taskData=taskData;
	}

	public java.lang.Integer getSynTimes(){
		return this.synTimes;
	}
	public void setSynTimes(java.lang.Integer synTimes){
		this.synTimes=synTimes;
	}

	public java.sql.Timestamp getSynTime(){
		return this.synTime;
	}
	public void setSynTime(java.sql.Timestamp synTime){
		this.synTime=synTime;
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
