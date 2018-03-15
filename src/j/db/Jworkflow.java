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
public class Jworkflow implements Serializable{

	private java.lang.String workflowId;
	private java.lang.String workflowCode;
	private java.sql.Timestamp startTime;
	private java.sql.Timestamp updTime;
	private java.lang.String workflowStatus;
	private java.lang.String nodeId;
	private java.lang.String nodeStatus;
	private java.lang.String nodeResult;
	private java.lang.String nodeData;

	public java.lang.String getWorkflowId(){
		return this.workflowId;
	}
	public void setWorkflowId(java.lang.String workflowId){
		this.workflowId=workflowId;
	}

	public java.lang.String getWorkflowCode(){
		return this.workflowCode;
	}
	public void setWorkflowCode(java.lang.String workflowCode){
		this.workflowCode=workflowCode;
	}

	public java.sql.Timestamp getStartTime(){
		return this.startTime;
	}
	public void setStartTime(java.sql.Timestamp startTime){
		this.startTime=startTime;
	}

	public java.sql.Timestamp getUpdTime(){
		return this.updTime;
	}
	public void setUpdTime(java.sql.Timestamp updTime){
		this.updTime=updTime;
	}

	public java.lang.String getWorkflowStatus(){
		return this.workflowStatus;
	}
	public void setWorkflowStatus(java.lang.String workflowStatus){
		this.workflowStatus=workflowStatus;
	}

	public java.lang.String getNodeId(){
		return this.nodeId;
	}
	public void setNodeId(java.lang.String nodeId){
		this.nodeId=nodeId;
	}

	public java.lang.String getNodeStatus(){
		return this.nodeStatus;
	}
	public void setNodeStatus(java.lang.String nodeStatus){
		this.nodeStatus=nodeStatus;
	}

	public java.lang.String getNodeResult(){
		return this.nodeResult;
	}
	public void setNodeResult(java.lang.String nodeResult){
		this.nodeResult=nodeResult;
	}

	public java.lang.String getNodeData(){
		return this.nodeData;
	}
	public void setNodeData(java.lang.String nodeData){
		this.nodeData=nodeData;
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
