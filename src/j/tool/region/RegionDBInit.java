package j.tool.region;

import j.dao.DAO;
import j.dao.DB;
import j.util.JUtilString;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RegionDBInit {
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		System.out.println("start...");
		
		//插入数据
		DAO dao=DB.connect("Region",RegionDBInit.class);
		
		DB.sqliteSetSynchronous(dao,DB.sqliteSynchronousOff);
		
		dao.executeSQL("create table IF NOT EXISTS j_city(CITY_ID varchar, PROVINCE_ID varchar, COUNTRY_ID varchar, CONTINENT_ID varchar, CITY_NAME varchar, CITY_NAME_TW varchar, CITY_NAME_EN varchar, AREA_CODE varchar, TIME_ZONE double, POSTAL_CODE varchar, IS_AVAIL varchar)");
		dao.executeSQL("create table IF NOT EXISTS j_continent(CONTINENT_ID varchar, CONTINENT_CODE varchar, CONTINENT_NAME varchar, CONTINENT_NAME_TW varchar, CONTINENT_NAME_EN varchar, IS_AVAIL varchar)");
		dao.executeSQL("create table IF NOT EXISTS j_country(COUNTRY_ID varchar, CONTINENT_ID varchar, COUNTRY_CODE varchar, COUNTRY_NAME varchar, COUNTRY_NAME_TW varchar, COUNTRY_NAME_EN varchar, AREA_CODE varchar, TIME_ZONE double, IS_AVAIL varchar)");
		dao.executeSQL("create table IF NOT EXISTS j_county(COUNTY_ID varchar, CITY_ID varchar, PROVINCE_ID varchar, COUNTRY_ID varchar, CONTINENT_ID varchar, COUNTY_NAME varchar, COUNTY_NAME_TW varchar, COUNTY_NAME_EN varchar, AREA_CODE varchar, TIME_ZONE double, POSTAL_CODE varchar, IS_AVAIL varchar)");
		dao.executeSQL("create table IF NOT EXISTS j_province(PROVINCE_ID varchar, COUNTRY_ID varchar, CONTINENT_ID varchar, PROVINCE_NAME varchar, PROVINCE_NAME_SHORT varchar(30), PROVINCE_NAME_TW varchar, PROVINCE_NAME_EN varchar, AREA_CODE varchar, TIME_ZONE double, POSTAL_CODE varchar, IS_AVAIL varchar)");
		dao.executeSQL("create table IF NOT EXISTS j_zone(ZONE_ID varchar, COUNTY_ID varchar, CITY_ID varchar, PROVINCE_ID varchar, COUNTRY_ID varchar, CONTINENT_ID varchar, ZONE_NAME varchar, ZONE_NAME_TW varchar, ZONE_NAME_EN varchar, AREA_CODE varchar, TIME_ZONE double, POSTAL_CODE varchar, IS_AVAIL varchar)");
		
		dao.executeSQL("delete from j_continent");
		dao.executeSQL("delete from j_country");
		dao.executeSQL("delete from j_province");
		dao.executeSQL("delete from j_city");
		dao.executeSQL("delete from j_county");
		dao.executeSQL("delete from j_zone");
		
		File file=new File("F:\\work\\jframework_v2.0\\doc\\regions.sql");
		InputStream in=new FileInputStream(file);
		BufferedReader reader=new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String line=null;
		int cnt=0;
		try{
	    	line=reader.readLine();
	    	while(line!=null){	 
	    		if(!line.startsWith("/")&&line.length()>10){
					//System.out.println(line);
		    		dao.executeSQL(JUtilString.replaceAll(line,"\\'","''"));
		    		if(cnt%1000==0){
		    			System.out.println(cnt);
		    		}
		    		cnt++;
	    		}
	    		line=reader.readLine();
	    	}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(line);
		}
		//插入数据 end
		
//		List list=Region.getCountries();
//		for(int i=0;i<list.size();i++){
//			Jcountry o=(Jcountry)list.get(i);
//			System.out.println(o.getCountryId()+","+o.getCountryName()+","+o.getCountryNameEn());
//			
//			List list2=Region.getProvinces(o.getCountryId());
//			for(int j=0;j<list2.size();j++){
//				Jprovince o2=(Jprovince)list2.get(j);
//				System.out.println("\t"+o2.getProvinceId()+","+o2.getProvinceName());
//				
//				List list3=Region.getCities(o2.getProvinceId());
//				for(int k=0;k<list3.size();k++){
//					Jcity o3=(Jcity)list3.get(k);
//					System.out.println("\t\t"+o3.getCityId()+","+o3.getCityName()+","+o3.getAreaCode()+","+o3.getPostalCode());
//					
//					List list4=Region.getCounties(o3.getCityId());
//					for(int l=0;l<list4.size();l++){
//						Jcounty o4=(Jcounty)list4.get(l);
//						System.out.println("\t\t\t"+o4.getCountyId()+","+o4.getCountyName()+","+o4.getAreaCode()+","+o4.getPostalCode());
//						
//					}
//				}
//			}
//		}
		
		System.out.println("end...");
		
    	System.exit(0);
	}
}
