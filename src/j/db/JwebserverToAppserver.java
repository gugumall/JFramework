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
public class JwebserverToAppserver implements Serializable{

	private java.lang.String uuid;
	private java.lang.String appserverCode;
	private java.lang.String webserverCode;
	private java.lang.String remarks;

	public java.lang.String getUuid(){
		return this.uuid;
	}
	public void setUuid(java.lang.String uuid){
		this.uuid=uuid;
	}

	public java.lang.String getAppserverCode(){
		return this.appserverCode;
	}
	public void setAppserverCode(java.lang.String appserverCode){
		this.appserverCode=appserverCode;
	}

	public java.lang.String getWebserverCode(){
		return this.webserverCode;
	}
	public void setWebserverCode(java.lang.String webserverCode){
		this.webserverCode=webserverCode;
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
