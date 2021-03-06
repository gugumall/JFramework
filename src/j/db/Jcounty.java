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
public class Jcounty implements Serializable{

	private java.lang.String countyId;
	private java.lang.String cityId;
	private java.lang.String provinceId;
	private java.lang.String countryId;
	private java.lang.String continentId;
	private java.lang.String countyName;
	private java.lang.String countyNameTw;
	private java.lang.String countyNameEn;
	private java.lang.String areaCode;
	private java.lang.Double timeZone;
	private java.lang.String postalCode;
	private java.lang.String isAvail;

	public java.lang.String getCountyId(){
		return this.countyId;
	}
	public void setCountyId(java.lang.String countyId){
		this.countyId=countyId;
	}

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

	public java.lang.String getCountyName(){
		return this.countyName;
	}
	public void setCountyName(java.lang.String countyName){
		this.countyName=countyName;
	}

	public java.lang.String getCountyNameTw(){
		return this.countyNameTw;
	}
	public void setCountyNameTw(java.lang.String countyNameTw){
		this.countyNameTw=countyNameTw;
	}

	public java.lang.String getCountyNameEn(){
		return this.countyNameEn;
	}
	public void setCountyNameEn(java.lang.String countyNameEn){
		this.countyNameEn=countyNameEn;
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
