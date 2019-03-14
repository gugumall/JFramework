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
public class JdbMirror implements Serializable{

	private java.lang.String dbMirrorCode;
	private java.lang.String dbCode;
	private java.lang.String dbsysCode;
	private java.lang.String serverCode;
	private java.lang.String dbMirrorName;
	private java.lang.String beRead;
	private java.lang.String beUpdated;
	private java.lang.String beInserted;
	private java.lang.String commInterface;
	private java.lang.String commKey;
	private java.lang.String dbMirrorStat;
	private java.lang.String remarks;

	public java.lang.String getDbMirrorCode(){
		return this.dbMirrorCode;
	}
	public void setDbMirrorCode(java.lang.String dbMirrorCode){
		this.dbMirrorCode=dbMirrorCode;
	}

	public java.lang.String getDbCode(){
		return this.dbCode;
	}
	public void setDbCode(java.lang.String dbCode){
		this.dbCode=dbCode;
	}

	public java.lang.String getDbsysCode(){
		return this.dbsysCode;
	}
	public void setDbsysCode(java.lang.String dbsysCode){
		this.dbsysCode=dbsysCode;
	}

	public java.lang.String getServerCode(){
		return this.serverCode;
	}
	public void setServerCode(java.lang.String serverCode){
		this.serverCode=serverCode;
	}

	public java.lang.String getDbMirrorName(){
		return this.dbMirrorName;
	}
	public void setDbMirrorName(java.lang.String dbMirrorName){
		this.dbMirrorName=dbMirrorName;
	}

	public java.lang.String getBeRead(){
		return this.beRead;
	}
	public void setBeRead(java.lang.String beRead){
		this.beRead=beRead;
	}

	public java.lang.String getBeUpdated(){
		return this.beUpdated;
	}
	public void setBeUpdated(java.lang.String beUpdated){
		this.beUpdated=beUpdated;
	}

	public java.lang.String getBeInserted(){
		return this.beInserted;
	}
	public void setBeInserted(java.lang.String beInserted){
		this.beInserted=beInserted;
	}

	public java.lang.String getCommInterface(){
		return this.commInterface;
	}
	public void setCommInterface(java.lang.String commInterface){
		this.commInterface=commInterface;
	}

	public java.lang.String getCommKey(){
		return this.commKey;
	}
	public void setCommKey(java.lang.String commKey){
		this.commKey=commKey;
	}

	public java.lang.String getDbMirrorStat(){
		return this.dbMirrorStat;
	}
	public void setDbMirrorStat(java.lang.String dbMirrorStat){
		this.dbMirrorStat=dbMirrorStat;
	}

	public java.lang.String getRemarks(){
		return this.remarks;
	}
	public void setRemarks(java.lang.String remarks){
		this.remarks=remarks;
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
