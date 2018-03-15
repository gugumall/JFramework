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
public class Jappserver implements Serializable{

	private java.lang.String appserverCode;
	private java.lang.String serverCode;
	private java.lang.String appserverName;
	private java.lang.String appserverStat;
	private java.lang.String remarks;

	public java.lang.String getAppserverCode(){
		return this.appserverCode;
	}
	public void setAppserverCode(java.lang.String appserverCode){
		this.appserverCode=appserverCode;
	}

	public java.lang.String getServerCode(){
		return this.serverCode;
	}
	public void setServerCode(java.lang.String serverCode){
		this.serverCode=serverCode;
	}

	public java.lang.String getAppserverName(){
		return this.appserverName;
	}
	public void setAppserverName(java.lang.String appserverName){
		this.appserverName=appserverName;
	}

	public java.lang.String getAppserverStat(){
		return this.appserverStat;
	}
	public void setAppserverStat(java.lang.String appserverStat){
		this.appserverStat=appserverStat;
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
