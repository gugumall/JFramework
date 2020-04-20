package j.tool.region;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import j.fs.JDFSFile;
import j.http.JHttp;
import j.http.JHttpContext;
import j.util.JUtilJSON;
import j.util.JUtilString;
import j.util.JUtilTextWriter;

public class RegionGetter{	
	private static Map codes=new HashMap();
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		fromBaidu();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private static void fromBaidu() throws Exception{
		JHttp http=JHttp.getInstance();
		HttpClient client=http.createClient();
		
		String continentId="0";
		String countryId="1";
		String provinceId=null;
		String cityId=null;
		String countyId=null;
		
		JUtilTextWriter log=new JUtilTextWriter(new File("F:\\work\\JFramework_v2.0\\doc\\regions.sql"),"UTF-8");
		log.addLine("use jframework;");
		
		String s=JDFSFile.read(new File("F:\\work\\JFramework_v2.0\\doc\\淘宝地域\\tdist.js"),"GBK");
		System.out.println(s);
		
		JSONObject regions=JUtilJSON.parse(s);
		
		List<String> _keys=new ArrayList();
		Iterator keys=regions.keys();
		while(keys.hasNext()) {
			String key=(String)keys.next();
			_keys.add(key);
		}
		

		for(int x=0; x<_keys.size(); x++) {
			countryId=_keys.get(x);
			if(countryId.length()>3) continue;//不是国家
			
			JSONArray country=regions.getJSONArray(countryId);
			String name=country.getString(0);
			String nameEn=country.getString(2);
			
			log.addLine("insert into j_country values ('"+countryId+"','0','','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','',0,'T');");
			System.out.println("insert into j_country values ('"+countryId+"','0','','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','',0,'T');");
			
			for(int i=0; i<_keys.size(); i++) {
				provinceId=_keys.get(i);
				
				//["安徽省", "1", "an hui sheng", ""],
				JSONArray province=regions.getJSONArray(provinceId);
				String pid=province.getString(1);
				if(!pid.equals(countryId)) continue;//不属于该国家
				name=province.getString(0);
				nameEn=province.getString(2);
				
				String nameShort=name;
				nameShort=nameShort.replaceAll("市","");
				nameShort=nameShort.replaceAll("维吾尔自治区","");
				nameShort=nameShort.replaceAll("壮族自治区","");
				nameShort=nameShort.replaceAll("回族自治区","");
				nameShort=nameShort.replaceAll("自治区","");
				
				log.addLine(" insert into j_province values ('"+provinceId+"','"+countryId+"','"+continentId+"','"+name+"','"+nameShort+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','',0,'','T');");
				System.out.println(" insert into j_province values ('"+provinceId+"','"+countryId+"','"+continentId+"','"+name+"','"+nameShort+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','',0,'','T');");
				
				//城市
				for(int j=0; j<_keys.size(); j++) {
					cityId=_keys.get(j);
					
					//["安徽省", "1", "an hui sheng", ""],
					JSONArray city=regions.getJSONArray(cityId);
					pid=city.getString(1);
					if(!pid.equals(provinceId)) continue;//不属于该省
					
					name=city.getString(0);
					nameEn=city.getString(2);
					
					nameShort=name;
					nameShort=nameShort.replaceAll("市","");
					nameShort=nameShort.replaceAll("维吾尔自治区","");
					nameShort=nameShort.replaceAll("壮族自治区","");
					nameShort=nameShort.replaceAll("回族自治区","");
					nameShort=nameShort.replaceAll("自治区","");
					
					String areaCode="";
					String postCode="";
					
					log.addLine("  insert into j_city values ('"+cityId+"','"+provinceId+"','"+countryId+"','"+continentId+"','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','"+areaCode+"',0,'"+postCode+"','T');");
					System.out.println("  insert into j_city values ('"+cityId+"','"+provinceId+"','"+countryId+"','"+continentId+"','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','"+areaCode+"',0,'"+postCode+"','T');");
				
					//区县
					boolean hasCounties=false;//市下面是否有区县
					for(int k=0; k<_keys.size(); k++) {
						countyId=_keys.get(k);
						
						//["安徽省", "1", "an hui sheng", ""],
						JSONArray county=regions.getJSONArray(countyId);
						pid=county.getString(1);
						
						if(!pid.equals(cityId)) continue;//不属于城市
						
						hasCounties=true;
						
						name=county.getString(0);
						nameEn=county.getString(2);
						
						nameShort=name;
						nameShort=nameShort.replaceAll("市","");
						nameShort=nameShort.replaceAll("维吾尔自治区","");
						nameShort=nameShort.replaceAll("壮族自治区","");
						nameShort=nameShort.replaceAll("回族自治区","");
						nameShort=nameShort.replaceAll("自治区","");
											
						log.addLine("   insert into j_county values ('"+countyId+"','"+cityId+"','"+provinceId+"','"+countryId+"','"+continentId+"','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','"+areaCode+"',0,'"+postCode+"','T');");
						System.out.println("   insert into j_county values ('"+countyId+"','"+cityId+"','"+provinceId+"','"+countryId+"','"+continentId+"','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','"+areaCode+"',0,'"+postCode+"','T');");
					
						//乡镇、街道
						if(countryId.equals("1")) {
							JHttpContext context=new JHttpContext();
							context.addRequestHeader("Referer","http://buy.taobao.com/auction/buy_now.jhtml");
							String zones=http.postResponse(context,client,"http://lsp.wuliu.taobao.com/locationservice/addr/output_address_town.do?l1="+provinceId+"&l2="+cityId+"&l3="+countyId+"&_ksTS=1396014988093_459&callback=jsonp460",null,"UTF-8");
							if(zones.indexOf("jsonp460(")<0) {
								System.out.println(name+" -> 未获取到街道乡镇 -> "+zones);
							}else {							
								zones=zones.substring(zones.indexOf("jsonp460(")+"jsonp460(".length(), zones.length()-2);
								zones=JUtilString.replaceAll(zones, "'", "\"");
								zones=JUtilString.replaceAll(zones, "success:", "\"success\":");
								zones=JUtilString.replaceAll(zones, "result:", "\"result\":");
								System.out.println(name+" ->> 获取到街道乡镇 -> "+zones);
								
								JSONObject zonesJson=JUtilJSON.parse(zones);
								zonesJson=JUtilJSON.object(zonesJson, "result");
								
								Iterator zonesKeys=zonesJson.keys();
							
								while(zonesKeys.hasNext()) {
									String zonesKey=(String)zonesKeys.next();
									JSONArray zone=JUtilJSON.array(zonesJson, zonesKey);
									
									String zoneId=zonesKey;
									
									name=zone.getString(0);
									nameEn=zone.getString(2);
									
									log.addLine("    insert into j_zone values ('"+zoneId+"','"+countyId+"','"+cityId+"','"+provinceId+"','"+countryId+"','"+continentId+"','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','',0,'','T');");
									System.out.println("    insert into j_zone values ('"+zoneId+"','"+countyId+"','"+cityId+"','"+provinceId+"','"+countryId+"','"+continentId+"','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','',0,'','T');");
								}
							}
						}
					}
					
					if(!hasCounties && countryId.equals("1")) {//如果没有区县（比如中山市下面就没有区县），将乡镇街道作为区县
						//乡镇、街道（作为区县）
						JHttpContext context=new JHttpContext();
						
						context.addRequestHeader("Referer","http://buy.taobao.com/auction/buy_now.jhtml");
						String zones=http.postResponse(context,client,"http://lsp.wuliu.taobao.com/locationservice/addr/output_address_town.do?l1="+provinceId+"&l2="+cityId+"&l3="+cityId+"&_ksTS=1396014988093_459&callback=jsonp460",null,"UTF-8");
						
						if(zones.indexOf("jsonp460(")<0) {
							System.out.println(name+" -> 未获取到街道乡镇 -> "+zones);
						}else {						
							zones=zones.substring(zones.indexOf("jsonp460(")+"jsonp460(".length(), zones.length()-2);
							zones=JUtilString.replaceAll(zones, "'", "\"");
							zones=JUtilString.replaceAll(zones, "success:", "\"success\":");
							zones=JUtilString.replaceAll(zones, "result:", "\"result\":");
							System.out.println(name+" ->> 获取到街道乡镇 -> "+zones);
							
							JSONObject zonesJson=JUtilJSON.parse(zones);
							zonesJson=JUtilJSON.object(zonesJson, "result");
							
							Iterator zonesKeys=zonesJson.keys();
						
							while(zonesKeys.hasNext()) {
								String zonesKey=(String)zonesKeys.next();
								JSONArray zone=JUtilJSON.array(zonesJson, zonesKey);
								String zoneId=zonesKey;
								
								name=zone.getString(0);
								nameEn=zone.getString(2);
								
								log.addLine("   insert into j_county values ('"+zoneId+"','"+cityId+"','"+provinceId+"','"+countryId+"','"+continentId+"','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','"+areaCode+"',0,'"+postCode+"','T');");
								System.out.println("   insert into j_county values ('"+zoneId+"','"+cityId+"','"+provinceId+"','"+countryId+"','"+continentId+"','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','"+areaCode+"',0,'"+postCode+"','T');");
							}
						}
					}
				}
			}
		}
		
		System.exit(0);
	}
}
