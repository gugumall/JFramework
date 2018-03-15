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
public class Jip implements Serializable{

	private java.lang.Long ipId;
	private java.lang.Long ipStart;
	private java.lang.Long ipEnd;
	private java.lang.String ipAddr;

	public java.lang.Long getIpId(){
		return this.ipId;
	}
	public void setIpId(java.lang.Long ipId){
		this.ipId=ipId;
	}

	public java.lang.Long getIpStart(){
		return this.ipStart;
	}
	public void setIpStart(java.lang.Long ipStart){
		this.ipStart=ipStart;
	}

	public java.lang.Long getIpEnd(){
		return this.ipEnd;
	}
	public void setIpEnd(java.lang.Long ipEnd){
		this.ipEnd=ipEnd;
	}

	public java.lang.String getIpAddr(){
		return this.ipAddr;
	}
	public void setIpAddr(java.lang.String ipAddr){
		this.ipAddr=ipAddr;
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
