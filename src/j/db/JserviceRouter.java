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
public class JserviceRouter implements Serializable{

	private java.lang.String routerCode;
	private java.lang.String appMirrorCode;
	private java.lang.String routerName;
	private java.lang.String commInterface;
	private java.lang.String commKey;
	private java.lang.String routerStat;
	private java.lang.String remarks;

	public java.lang.String getRouterCode(){
		return this.routerCode;
	}
	public void setRouterCode(java.lang.String routerCode){
		this.routerCode=routerCode;
	}

	public java.lang.String getAppMirrorCode(){
		return this.appMirrorCode;
	}
	public void setAppMirrorCode(java.lang.String appMirrorCode){
		this.appMirrorCode=appMirrorCode;
	}

	public java.lang.String getRouterName(){
		return this.routerName;
	}
	public void setRouterName(java.lang.String routerName){
		this.routerName=routerName;
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

	public java.lang.String getRouterStat(){
		return this.routerStat;
	}
	public void setRouterStat(java.lang.String routerStat){
		this.routerStat=routerStat;
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
