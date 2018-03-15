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
public class Jservice implements Serializable{

	private java.lang.String serviceCode;
	private java.lang.String serviceName;
	private java.lang.String remarks;

	public java.lang.String getServiceCode(){
		return this.serviceCode;
	}
	public void setServiceCode(java.lang.String serviceCode){
		this.serviceCode=serviceCode;
	}

	public java.lang.String getServiceName(){
		return this.serviceName;
	}
	public void setServiceName(java.lang.String serviceName){
		this.serviceName=serviceName;
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
