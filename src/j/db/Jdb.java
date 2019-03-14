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
public class Jdb implements Serializable{

	private java.lang.String dbCode;
	private java.lang.String dbName;
	private java.lang.String remarks;

	public java.lang.String getDbCode(){
		return this.dbCode;
	}
	public void setDbCode(java.lang.String dbCode){
		this.dbCode=dbCode;
	}

	public java.lang.String getDbName(){
		return this.dbName;
	}
	public void setDbName(java.lang.String dbName){
		this.dbName=dbName;
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
