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
public class Jserver implements Serializable{

	private java.lang.String serverCode;
	private java.lang.String serverName;
	private java.lang.String idcName;
	private java.lang.String zoneId;
	private java.lang.String countyId;
	private java.lang.String cityId;
	private java.lang.String provinceId;
	private java.lang.String countryId;
	private java.lang.String continentId;
	private java.lang.String addr;
	private java.sql.Timestamp startupTime;
	private java.sql.Timestamp shutdownTime;
	private java.lang.String wanIp;
	private java.lang.String lanIp;
	private java.lang.String cpu;
	private java.lang.Integer disk;
	private java.lang.Integer ram;
	private java.lang.String os;
	private java.lang.String inUse;
	private java.lang.String serverStat;
	private java.lang.String remarks;

	public java.lang.String getServerCode(){
		return this.serverCode;
	}
	public void setServerCode(java.lang.String serverCode){
		this.serverCode=serverCode;
	}

	public java.lang.String getServerName(){
		return this.serverName;
	}
	public void setServerName(java.lang.String serverName){
		this.serverName=serverName;
	}

	public java.lang.String getIdcName(){
		return this.idcName;
	}
	public void setIdcName(java.lang.String idcName){
		this.idcName=idcName;
	}

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

	public java.lang.String getAddr(){
		return this.addr;
	}
	public void setAddr(java.lang.String addr){
		this.addr=addr;
	}

	public java.sql.Timestamp getStartupTime(){
		return this.startupTime;
	}
	public void setStartupTime(java.sql.Timestamp startupTime){
		this.startupTime=startupTime;
	}

	public java.sql.Timestamp getShutdownTime(){
		return this.shutdownTime;
	}
	public void setShutdownTime(java.sql.Timestamp shutdownTime){
		this.shutdownTime=shutdownTime;
	}

	public java.lang.String getWanIp(){
		return this.wanIp;
	}
	public void setWanIp(java.lang.String wanIp){
		this.wanIp=wanIp;
	}

	public java.lang.String getLanIp(){
		return this.lanIp;
	}
	public void setLanIp(java.lang.String lanIp){
		this.lanIp=lanIp;
	}

	public java.lang.String getCpu(){
		return this.cpu;
	}
	public void setCpu(java.lang.String cpu){
		this.cpu=cpu;
	}

	public java.lang.Integer getDisk(){
		return this.disk;
	}
	public void setDisk(java.lang.Integer disk){
		this.disk=disk;
	}

	public java.lang.Integer getRam(){
		return this.ram;
	}
	public void setRam(java.lang.Integer ram){
		this.ram=ram;
	}

	public java.lang.String getOs(){
		return this.os;
	}
	public void setOs(java.lang.String os){
		this.os=os;
	}

	public java.lang.String getInUse(){
		return this.inUse;
	}
	public void setInUse(java.lang.String inUse){
		this.inUse=inUse;
	}

	public java.lang.String getServerStat(){
		return this.serverStat;
	}
	public void setServerStat(java.lang.String serverStat){
		this.serverStat=serverStat;
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
