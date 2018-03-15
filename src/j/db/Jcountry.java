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
public class Jcountry implements Serializable{

	private java.lang.String countryId;
	private java.lang.String continentId;
	private java.lang.String countryCode;
	private java.lang.String countryName;
	private java.lang.String countryNameTw;
	private java.lang.String countryNameEn;
	private java.lang.String areaCode;
	private java.lang.Double timeZone;
	private java.lang.String isAvail;

	public java.lang.String getCountryId(){
		return this.countryId;
	}
	public void setCountryId(java.lang.String countryId){
		this.countryId=countryId;
	}

	public java.lang.String getContinentId(){
		return this.continentId;
	}
	public void setContinentId(java.lang.String continentId){
		this.continentId=continentId;
	}

	public java.lang.String getCountryCode(){
		return this.countryCode;
	}
	public void setCountryCode(java.lang.String countryCode){
		this.countryCode=countryCode;
	}

	public java.lang.String getCountryName(){
		return this.countryName;
	}
	public void setCountryName(java.lang.String countryName){
		this.countryName=countryName;
	}

	public java.lang.String getCountryNameTw(){
		return this.countryNameTw;
	}
	public void setCountryNameTw(java.lang.String countryNameTw){
		this.countryNameTw=countryNameTw;
	}

	public java.lang.String getCountryNameEn(){
		return this.countryNameEn;
	}
	public void setCountryNameEn(java.lang.String countryNameEn){
		this.countryNameEn=countryNameEn;
	}

	public java.lang.String getAreaCode(){
		return this.areaCode;
	}
	public void setAreaCode(java.lang.String areaCode){
		this.areaCode=areaCode;
	}

	public java.lang.Double getTimeZone(){
		return this.timeZone;
	}
	public void setTimeZone(java.lang.Double timeZone){
		this.timeZone=timeZone;
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
