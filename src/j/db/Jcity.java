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
public class Jcity implements Serializable{

	private java.lang.String cityId;
	private java.lang.String provinceId;
	private java.lang.String countryId;
	private java.lang.String continentId;
	private java.lang.String cityName;
	private java.lang.String cityNameTw;
	private java.lang.String cityNameEn;
	private java.lang.String areaCode;
	private java.lang.Double timeZone;
	private java.lang.String postalCode;
	private java.lang.String isAvail;

	public java.lang.String getCityId(){
		return this.cityId;
	}
	public void setCityId(java.lang.String cityId){
		this.cityId=cityId;
	}

	public java.lang.String getProvinceId(){
		return this.provinceId;
	}
	public void setProvinceId(java.lang.String provinceId){
		this.provinceId=provinceId;
	}

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

	public java.lang.String getCityName(){
		return this.cityName;
	}
	public void setCityName(java.lang.String cityName){
		this.cityName=cityName;
	}

	public java.lang.String getCityNameTw(){
		return this.cityNameTw;
	}
	public void setCityNameTw(java.lang.String cityNameTw){
		this.cityNameTw=cityNameTw;
	}

	public java.lang.String getCityNameEn(){
		return this.cityNameEn;
	}
	public void setCityNameEn(java.lang.String cityNameEn){
		this.cityNameEn=cityNameEn;
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

	public java.lang.String getPostalCode(){
		return this.postalCode;
	}
	public void setPostalCode(java.lang.String postalCode){
		this.postalCode=postalCode;
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
