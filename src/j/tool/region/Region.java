package j.tool.region;

import j.dao.DAO;
import j.dao.DB;
import j.db.Jcity;
import j.db.Jcountry;
import j.db.Jcounty;
import j.db.Jprovince;
import j.db.Jzone;
import j.log.Logger;
import j.util.ConcurrentMap;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 肖炯
 *
 */
public final class Region{
	private static Logger log=Logger.create(Region.class);
	private static ConcurrentMap countries=new ConcurrentMap();//国家
	private static ConcurrentMap provinces=new ConcurrentMap();//省份
	private static ConcurrentMap cities=new ConcurrentMap();//城市
	private static ConcurrentMap citiesKeyedByAreaCode=new ConcurrentMap();//城市
	private static ConcurrentMap counties=new ConcurrentMap();//区县
	private static ConcurrentMap zones=new ConcurrentMap();//区县
	private static ConcurrentMap cache=new ConcurrentMap();//缓存
	
	static{
		load();
	}
	
	/**
	 * 
	 */
	private static void load(){
		DAO dao=null;
		try{
			dao=DB.connect("Region",Region.class);
			
			List temp=dao.find("j_country","");
			for(int i=0;i<temp.size();i++){
				Jcountry o=(Jcountry)temp.get(i);
				countries.put(o.getCountryId(), o);
			}
			temp.clear();
			temp=null;
			log.log(countries.size()+" countries loaded.",-1);
			
			temp=dao.find("j_province","order by province_id*1 asc");
			for(int i=0;i<temp.size();i++){
				Jprovince o=(Jprovince)temp.get(i);
				provinces.put(o.getProvinceId(), o);
			}
			temp.clear();
			temp=null;
			log.log(provinces.size()+" provinces loaded.",-1);
			
			temp=dao.find("j_city","order by city_name_en asc");
			for(int i=0;i<temp.size();i++){
				Jcity o=(Jcity)temp.get(i);
				cities.put(o.getCityId(), o);
				if(o.getAreaCode()!=null&&!"".equals(o.getAreaCode())){
					citiesKeyedByAreaCode.put(o.getAreaCode(),o);
				}
			}
			temp.clear();
			temp=null;
			log.log(cities.size()+" cities loaded.",-1);
			
			
			temp=dao.find("j_county","");
			for(int i=0;i<temp.size();i++){
				Jcounty o=(Jcounty)temp.get(i);
				counties.put(o.getCountyId(), o);
			}
			temp.clear();
			temp=null;
			log.log(counties.size()+" counties loaded.",-1);
			
			
			
			temp=dao.find("j_zone","");
			for(int i=0;i<temp.size();i++){
				Jzone o=(Jzone)temp.get(i);
				zones.put(o.getZoneId(), o);
				
				String key="zones."+o.getCountyId();
				List ofParent=(List)cache.get(key);
				if(ofParent==null){
					ofParent=new ArrayList();
					cache.put(key, ofParent);
				}
				ofParent.add(o);
			}
			temp.clear();
			temp=null;
			log.log(zones.size()+" zones loaded.",-1);
			
			dao.close();
			dao=null;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			try{
				dao.close();
				dao=null;
			}catch(Exception ex){}
		}
	}


	
	/**
	 * 
	 * @return
	 */
	public static List getCountries(){
		if(cache.containsKey("countries")) return (List)cache.get("countries");
		
		List list=countries.listValues();
		cache.put("countries",list);
		return list;
	}
	
	/**
	 * 
	 * @param countryId
	 * @return
	 */
	public static Jcountry getCountry(String countryId){
		if(countryId==null||"".equals(countryId)) return null;
		
		return (Jcountry)countries.get(countryId);
	}
	
	/**
	 * 
	 * @return
	 */
	public static List getProvinces(){
		String key="provinces";
		if(cache.containsKey(key)) return (List)cache.get(key);
		
		List list=getProvinces("2");
		list.addAll(getProvinces("1"));
		return list;
	}
	
	/**
	 * 
	 * @param countryId
	 * @return
	 */
	public static List getProvinces(String countryId){
		if(countryId==null||"".equals(countryId)) return null;
		
		String key="provinces."+countryId;
		if(cache.containsKey(key)) return (List)cache.get(key);
		
		List list=provinces.listValues();
		for(int i=0;i<list.size();i++){
			Jprovince o=(Jprovince)list.get(i);
			if(!o.getCountryId().equals(countryId)){
				list.remove(i);
				i--;
				continue;
			}
		}
		cache.put(key,list);
		
		return list;
	}
	
	/**
	 * 
	 * @param provinceId
	 * @return
	 */
	public static Jprovince getProvince(String provinceId){
		if(provinceId==null||"".equals(provinceId)) return null;
		
		return (Jprovince)provinces.get(provinceId);
	}
	
	/**
	 * 
	 * @param provinceId
	 * @return
	 */
	public static List getCities(String provinceId){
		if(provinceId==null||"".equals(provinceId)) return null;
		
		String key="cities."+provinceId;
		if(cache.containsKey(key)) return (List)cache.get(key);
		
		List list=cities.listValues();
		for(int i=0;i<list.size();i++){
			Jcity o=(Jcity)list.get(i);
			if(!o.getProvinceId().equals(provinceId)){
				list.remove(i);
				i--;
				continue;
			}
		}
		cache.put(key,list);
		
		return list;
	}
	
	/**
	 * 
	 * @param cityId
	 * @return
	 */
	public static Jcity getCity(String cityId){
		if(cityId==null||"".equals(cityId)) return null;
		
		return (Jcity)cities.get(cityId);
	}
	
	/**
	 * 
	 * @param areaCode
	 * @return
	 */
	public static Jcity getCityByAreaCode(String areaCode){
		if(areaCode==null||"".equals(areaCode)) return null;
		
		return (Jcity)citiesKeyedByAreaCode.get(areaCode);
	}
	
	/**
	 * 
	 * @param cityId
	 * @return
	 */
	public static List getCounties(String cityId){
		if(cityId==null||"".equals(cityId)) return null;
		
		String key="counties."+cityId;
		if(cache.containsKey(key)) return (List)cache.get(key);
		
		List list=counties.listValues();
		for(int i=0;i<list.size();i++){
			Jcounty o=(Jcounty)list.get(i);
			if(!o.getCityId().equals(cityId)){
				list.remove(i);
				i--;
				continue;
			}
		}
		cache.put(key,list);
		
		return list;
	}
	
	/**
	 * 
	 * @param countyId
	 * @return
	 */
	public static Jcounty getCounty(String countyId){
		if(countyId==null||"".equals(countyId)) return null;
		
		return (Jcounty)counties.get(countyId);
	}
	
	/**
	 * 
	 * @return
	 */
	public static List getZones(){		
		return zones.listValues();
	}
	
	/**
	 * 
	 * @param countyId
	 * @return
	 */
	public static List getZones(String countyId){
		if(countyId==null||"".equals(countyId)) return null;
		
		String key="zones."+countyId;
		if(cache.containsKey(key)) return (List)cache.get(key);
		
		List list=zones.listValues();
		for(int i=0;i<list.size();i++){
			Jzone o=(Jzone)list.get(i);
			if(!o.getCountyId().equals(countyId)){
				list.remove(i);
				i--;
				continue;
			}
		}
		cache.put(key,list);
		
		return list;
	}
	
	/**
	 * 
	 * @param zoneId
	 * @return
	 */
	public static Jzone getZone(String zoneId){
		if(zoneId==null||"".equals(zoneId)) return null;
		
		return (Jzone)zones.get(zoneId);
	}
}
