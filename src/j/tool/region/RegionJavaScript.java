package j.tool.region;

import j.db.Jcity;
import j.db.Jcountry;
import j.db.Jcounty;
import j.db.Jprovince;
import j.fs.JFile;

import java.util.List;

public class RegionJavaScript {	
	/**
	 * 生成JS
	 *
	 */
	public static void generateJs(boolean genZones) throws Exception{
		System.out.println("generating area.js......");

		//RFile file=new RFile(SysConfig.getWebRoot()+"templates/area.js");
		JFile file=JFile.create("F:\\work\\JFramework_v2.0\\WebContent\\js\\region\\region.js.template");
		String template=file.string("utf-8");
		file=null;
		
		try{
			StringBuffer js=new StringBuffer();
			StringBuffer i18n=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
			i18n.append("<root>\r\n");
			i18n.append("\t<group name=\"r\" desc=\"地域名称\">\r\n");
			
			//国家
			List list=Region.getCountries();
			for(int i=0;i<list.size();i++){
				Jcountry o=(Jcountry)list.get(i);
				
				js.append("_country('"+o.getCountryId()+"','I{r,"+o.getCountryName()+"}');\r\n");	
				
				i18n.append("\t\t<string key=\""+o.getCountryName()+"\">\r\n");
				i18n.append("\t\t\t<language code=\"zh-cn\">"+o.getCountryName()+"</language>\r\n");
				i18n.append("\t\t\t<language code=\"en-us\">"+o.getCountryNameEn()+"</language>\r\n");
				i18n.append("\t\t</string>\r\n");
			}
			
			//省份
			List provinces=Region.getProvinces();
			for(int i=0;i<provinces.size();i++){
				Jprovince p=(Jprovince)provinces.get(i);
				js.append("_a('"+p.getProvinceId()+"','I{r,"+p.getProvinceNameShort()+"}');\r\n");		
				
				i18n.append("\t\t<string key=\""+p.getProvinceNameShort()+"\">\r\n");
				i18n.append("\t\t\t<language code=\"zh-cn\">"+p.getProvinceNameShort()+"</language>\r\n");
				i18n.append("\t\t\t<language code=\"en-us\">"+p.getProvinceNameEn()+"</language>\r\n");
				i18n.append("\t\t</string>\r\n");
				
				String provinceId=p.getProvinceId();
				if(provinceId.endsWith("0000")) provinceId=provinceId.substring(0,provinceId.length()-4);
				if(provinceId.endsWith("00")) provinceId=provinceId.substring(0,provinceId.length()-2);
				
				List cities=Region.getCities(p.getProvinceId());
				for(int j=0;j<cities.size();j++){
					Jcity c=(Jcity)cities.get(j);
					
					js.append("_b('"+provinceId+"','"+c.getCityId()+"','I{r,"+c.getCityName()+"}','"+c.getAreaCode()+"','"+c.getPostalCode()+"');\r\n");
					

					
					i18n.append("\t\t<string key=\""+c.getCityName()+"\">\r\n");
					i18n.append("\t\t\t<language code=\"zh-cn\">"+c.getCityName()+"</language>\r\n");
					i18n.append("\t\t\t<language code=\"en-us\">"+c.getCityNameEn()+"</language>\r\n");
					i18n.append("\t\t</string>\r\n");

					String cityId=c.getCityId();
					if(cityId.endsWith("0000")) cityId=cityId.substring(0,cityId.length()-4);
					if(cityId.endsWith("00")) cityId=cityId.substring(0,cityId.length()-2);
					
					List cs=Region.getCounties(c.getCityId());
					for(int k=0;k<cs.size();k++){
						Jcounty county=(Jcounty)cs.get(k);
						js.append("_c('"+provinceId+"','"+cityId+"','"+county.getCountyId()+"','I{r,"+county.getCountyName()+"}','"+county.getAreaCode()+"','"+county.getPostalCode()+"');\r\n");
					
						i18n.append("\t\t<string key=\""+county.getCountyName()+"\">\r\n");
						i18n.append("\t\t\t<language code=\"zh-cn\">"+county.getCountyName()+"</language>\r\n");
						i18n.append("\t\t\t<language code=\"en-us\">"+county.getCountyNameEn()+"</language>\r\n");
						i18n.append("\t\t</string>\r\n");
						
//						if(genZones){
//							List zs=Region.getZones(county.getCountyId());
//							if(zs.size()>0){
//								String zsJs="zones=new Array();\r\n";
//								for(int x=0;x<zs.size();x++){
//									Jzone z=(Jzone)zs.get(x);
//									zsJs.append("zones.push(new Array('"+z.getZoneId()+"','I{r,"+z.getZoneName()+"}'));\r\n");
//								}
//								
//								JFile savedFile=JFile.create("F:\\work\\JFramework\\WebContent\\js\\region\\zones\\"+county.getCountyId()+".js");
//								savedFile.save(zsJs, false, "utf-8");
//								savedFile=null;
//								//System.out.println("F:\\work\\JFramework\\WebContent\\js\\region\\zones\\"+county.getCountyId()+".js");
//							}else{
//								JFile savedFile=JFile.create("F:\\work\\JFramework\\WebContent\\js\\region\\zones\\"+county.getCountyId()+".js");
//								savedFile.save("", false, "utf-8");
//								savedFile=null;
//								//System.out.println("F:\\work\\JFramework\\WebContent\\js\\region\\zones\\"+county.getCountyId()+".js");
//							}
//						}
						
						System.out.println("...............");
					}
				}
			}
			
			template=template.replace("REGIONS", js.toString());
			js=null;
			
			//RFile savedFile=new RFile(SysConfig.getWebRoot()+"js/area.js");
			JFile savedFile=JFile.create("F:\\work\\JFramework_v2.0\\WebContent\\js\\region\\region.js");
			savedFile.save(template, false, "utf-8");
			savedFile=null;
			template=null;
			
			
			i18n.append("\t</group>\r\n");
			i18n.append("</root>\r\n");
			savedFile=JFile.create("F:\\work\\JFramework_v2.0\\I18N\\91_地域.xml");
			savedFile.save(i18n.toString(), false, "utf-8");
			savedFile=null;
			i18n=null;
			
			System.out.println("generating area.js ok!");
			
			System.exit(0);
		}catch(Exception e){
			System.out.println("generating area.js failed!");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		generateJs(false);
	}
}
