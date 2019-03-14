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
public class Jwebserver implements Serializable{

	private java.lang.String webserverCode;
	private java.lang.String serverCode;
	private java.lang.String webserverName;
	private java.lang.String webserverStat;
	private java.lang.String remarks;

	public java.lang.String getWebserverCode(){
		return this.webserverCode;
	}
	public void setWebserverCode(java.lang.String webserverCode){
		this.webserverCode=webserverCode;
	}

	public java.lang.String getServerCode(){
		return this.serverCode;
	}
	public void setServerCode(java.lang.String serverCode){
		this.serverCode=serverCode;
	}

	public java.lang.String getWebserverName(){
		return this.webserverName;
	}
	public void setWebserverName(java.lang.String webserverName){
		this.webserverName=webserverName;
	}

	public java.lang.String getWebserverStat(){
		return this.webserverStat;
	}
	public void setWebserverStat(java.lang.String webserverStat){
		this.webserverStat=webserverStat;
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
