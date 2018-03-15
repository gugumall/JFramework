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
public class Japp implements Serializable{

	private java.lang.String appCode;
	private java.lang.String appName;
	private java.lang.String remarks;

	public java.lang.String getAppCode(){
		return this.appCode;
	}
	public void setAppCode(java.lang.String appCode){
		this.appCode=appCode;
	}

	public java.lang.String getAppName(){
		return this.appName;
	}
	public void setAppName(java.lang.String appName){
		this.appName=appName;
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
