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
public class Jprovince implements Serializable{

	private java.lang.String provinceId;
	private java.lang.String countryId;
	private java.lang.String continentId;
	private java.lang.String provinceName;
	private java.lang.String provinceNameShort;
	private java.lang.String provinceNameTw;
	private java.lang.String provinceNameEn;
	private java.lang.String areaCode;
	private java.lang.Double timeZone;
	private java.lang.String postalCode;
	private java.lang.String isAvail;

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

	public java.lang.String getProvinceName(){
		return this.provinceName;
	}
	public void setProvinceName(java.lang.String provinceName){
		this.provinceName=provinceName;
	}

	public java.lang.String getProvinceNameShort(){
		return this.provinceNameShort;
	}
	public void setProvinceNameShort(java.lang.String provinceNameShort){
		this.provinceNameShort=provinceNameShort;
	}

	public java.lang.String getProvinceNameTw(){
		return this.provinceNameTw;
	}
	public void setProvinceNameTw(java.lang.String provinceNameTw){
		this.provinceNameTw=provinceNameTw;
	}

	public java.lang.String getProvinceNameEn(){
		return this.provinceNameEn;
	}
	public void setProvinceNameEn(java.lang.String provinceNameEn){
		this.provinceNameEn=provinceNameEn;
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
