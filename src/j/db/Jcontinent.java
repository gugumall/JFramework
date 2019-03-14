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
public class Jcontinent implements Serializable{

	private java.lang.String continentId;
	private java.lang.String continentCode;
	private java.lang.String continentName;
	private java.lang.String continentNameTw;
	private java.lang.String continentNameEn;
	private java.lang.String isAvail;

	public java.lang.String getContinentId(){
		return this.continentId;
	}
	public void setContinentId(java.lang.String continentId){
		this.continentId=continentId;
	}

	public java.lang.String getContinentCode(){
		return this.continentCode;
	}
	public void setContinentCode(java.lang.String continentCode){
		this.continentCode=continentCode;
	}

	public java.lang.String getContinentName(){
		return this.continentName;
	}
	public void setContinentName(java.lang.String continentName){
		this.continentName=continentName;
	}

	public java.lang.String getContinentNameTw(){
		return this.continentNameTw;
	}
	public void setContinentNameTw(java.lang.String continentNameTw){
		this.continentNameTw=continentNameTw;
	}

	public java.lang.String getContinentNameEn(){
		return this.continentNameEn;
	}
	public void setContinentNameEn(java.lang.String continentNameEn){
		this.continentNameEn=continentNameEn;
	}

	public java.lang.String getIsAvail(){
		return this.isAvail;
	}
	public void setIsAvail(java.lang.String isAvail){
		this.isAvail=isAvail;
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
