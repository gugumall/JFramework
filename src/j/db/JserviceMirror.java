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
public class JserviceMirror implements Serializable{

	private java.lang.String serviceMirrorCode;
	private java.lang.String serviceCode;
	private java.lang.String appMirrorCode;
	private java.lang.String serviceMirrorName;
	private java.lang.String commInterface;
	private java.lang.String commKey;
	private java.lang.String serviceMirrorStat;
	private java.lang.String remarks;

	public java.lang.String getServiceMirrorCode(){
		return this.serviceMirrorCode;
	}
	public void setServiceMirrorCode(java.lang.String serviceMirrorCode){
		this.serviceMirrorCode=serviceMirrorCode;
	}

	public java.lang.String getServiceCode(){
		return this.serviceCode;
	}
	public void setServiceCode(java.lang.String serviceCode){
		this.serviceCode=serviceCode;
	}

	public java.lang.String getAppMirrorCode(){
		return this.appMirrorCode;
	}
	public void setAppMirrorCode(java.lang.String appMirrorCode){
		this.appMirrorCode=appMirrorCode;
	}

	public java.lang.String getServiceMirrorName(){
		return this.serviceMirrorName;
	}
	public void setServiceMirrorName(java.lang.String serviceMirrorName){
		this.serviceMirrorName=serviceMirrorName;
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

	public java.lang.String getServiceMirrorStat(){
		return this.serviceMirrorStat;
	}
	public void setServiceMirrorStat(java.lang.String serviceMirrorStat){
		this.serviceMirrorStat=serviceMirrorStat;
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
