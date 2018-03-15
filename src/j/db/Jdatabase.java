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
public class Jdatabase implements Serializable{

	private java.lang.String dbsysCode;
	private java.lang.String serverCode;
	private java.lang.String dbsysName;
	private java.lang.String dbsysType;
	private java.lang.Integer dbsysConns;
	private java.lang.String dbsysStat;
	private java.lang.String remarks;

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

	public java.lang.String getDbsysName(){
		return this.dbsysName;
	}
	public void setDbsysName(java.lang.String dbsysName){
		this.dbsysName=dbsysName;
	}

	public java.lang.String getDbsysType(){
		return this.dbsysType;
	}
	public void setDbsysType(java.lang.String dbsysType){
		this.dbsysType=dbsysType;
	}

	public java.lang.Integer getDbsysConns(){
		return this.dbsysConns;
	}
	public void setDbsysConns(java.lang.Integer dbsysConns){
		this.dbsysConns=dbsysConns;
	}

	public java.lang.String getDbsysStat(){
		return this.dbsysStat;
	}
	public void setDbsysStat(java.lang.String dbsysStat){
		this.dbsysStat=dbsysStat;
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
