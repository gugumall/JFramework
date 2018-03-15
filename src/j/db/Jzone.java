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
public class Jzone implements Serializable{

	private java.lang.String zoneId;
	private java.lang.String countyId;
	private java.lang.String cityId;
	private java.lang.String provinceId;
	private java.lang.String countryId;
	private java.lang.String continentId;
	private java.lang.String zoneName;
	private java.lang.String zoneNameTw;
	private java.lang.String zoneNameEn;
	private java.lang.String areaCode;
	private java.lang.Double timeZone;
	private java.lang.String postalCode;
	private java.lang.String isAvail;

	public java.lang.String getZoneId(){
		return this.zoneId;
	}
	public void setZoneId(java.lang.String zoneId){
		this.zoneId=zoneId;
	}

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

	public java.lang.String getZoneName(){
		return this.zoneName;
	}
	public void setZoneName(java.lang.String zoneName){
		this.zoneName=zoneName;
	}

	public java.lang.String getZoneNameTw(){
		return this.zoneNameTw;
	}
	public void setZoneNameTw(java.lang.String zoneNameTw){
		this.zoneNameTw=zoneNameTw;
	}

	public java.lang.String getZoneNameEn(){
		return this.zoneNameEn;
	}
	public void setZoneNameEn(java.lang.String zoneNameEn){
		this.zoneNameEn=zoneNameEn;
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
