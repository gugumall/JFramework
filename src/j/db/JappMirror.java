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
public class JappMirror implements Serializable{

	private java.lang.String appMirrorCode;
	private java.lang.String appCode;
	private java.lang.String appserverCode;
	private java.lang.String appMirrorName;
	private java.lang.String commInterface;
	private java.lang.String commKey;
	private java.lang.String appMirrorStat;
	private java.lang.String remarks;

	public java.lang.String getAppMirrorCode(){
		return this.appMirrorCode;
	}
	public void setAppMirrorCode(java.lang.String appMirrorCode){
		this.appMirrorCode=appMirrorCode;
	}

	public java.lang.String getAppCode(){
		return this.appCode;
	}
	public void setAppCode(java.lang.String appCode){
		this.appCode=appCode;
	}

	public java.lang.String getAppserverCode(){
		return this.appserverCode;
	}
	public void setAppserverCode(java.lang.String appserverCode){
		this.appserverCode=appserverCode;
	}

	public java.lang.String getAppMirrorName(){
		return this.appMirrorName;
	}
	public void setAppMirrorName(java.lang.String appMirrorName){
		this.appMirrorName=appMirrorName;
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

	public java.lang.String getAppMirrorStat(){
		return this.appMirrorStat;
	}
	public void setAppMirrorStat(java.lang.String appMirrorStat){
		this.appMirrorStat=appMirrorStat;
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
