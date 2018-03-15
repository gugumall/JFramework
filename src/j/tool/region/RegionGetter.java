package j.tool.region;

import j.fs.JDFSFile;
import j.http.JHttp;
import j.http.JHttpContext;
import j.util.JUtilString;
import j.util.JUtilTextWriter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;

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
		
		String s=JDFSFile.read(new File("F:\\work\\JFramework_v2.0\\doc\\淘宝地域\\tdist_py.js"),"GBK");
		
		String[] ls=JUtilString.getTokens(s,"\n");
		
		for(int i=0;i<ls.length;i++){
			if(ls[i].indexOf(":[")<0) continue;
			
			String id=ls[i].substring(0,ls[i].indexOf(":["));
			id=JUtilString.replaceAll(id,"'","");
			
			String info=ls[i].substring(ls[i].indexOf(":[")+2,ls[i].indexOf("]"));
			info=JUtilString.replaceAll(info,"'","");
			
			String[] cells=info.split(",");
			String name=cells[0];
			String pid=cells[1];
			String nameEn=cells[2];

			int idi=Integer.parseInt(id);
			if(idi>=1000&&idi<110000) continue;//外国城市不要
			
			//国家			
			if(idi<1000){
				log.addLine("insert into j_country values ('"+id+"','"+pid+"','','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','',0,'T');");
				System.out.println("insert into j_country values ('"+id+"','"+pid+"','','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','',0,'T');");
			}else if(id.endsWith("0000")){//省
				if(provinceId==null||!provinceId.equals(id)){
					provinceId=id;
				}
				
				String nameShort=name;
				nameShort=nameShort.replaceAll("市","");
				nameShort=nameShort.replaceAll("维吾尔自治区","");
				nameShort=nameShort.replaceAll("壮族自治区","");
				nameShort=nameShort.replaceAll("回族自治区","");
				nameShort=nameShort.replaceAll("自治区","");
				
				log.addLine(" insert into j_province values ('"+id+"','"+pid+"','"+continentId+"','"+name+"','"+nameShort+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','',0,'','T');");
				System.out.println(" insert into j_province values ('"+id+"','"+pid+"','"+continentId+"','"+name+"','"+nameShort+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','',0,'','T');");
			
			}else if(pid.equals(provinceId)){
				if(cityId==null||!cityId.equals(id)){
					cityId=id;
				}
				
				String areaCode="";
				String postCode="";
				String[] data=(String[])codes.get(id);
				if(data!=null){
					areaCode=data[0];
					postCode=data[1];
				}
				
				
				log.addLine("  insert into j_city values ('"+id+"','"+pid+"','"+countryId+"','"+continentId+"','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','"+areaCode+"',0,'"+postCode+"','T');");
				System.out.println("  insert into j_city values ('"+id+"','"+pid+"','"+countryId+"','"+continentId+"','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','"+areaCode+"',0,'"+postCode+"','T');");
			}else{
				if(countyId==null||!countyId.equals(id)){
					countyId=id;
				}
				
				String areaCode="";
				String postCode="";
				String[] data=(String[])codes.get(id);
				if(data!=null){
					areaCode=data[0];
					postCode=data[1];
				}
				
				log.addLine("   insert into j_county values ('"+id+"','"+pid+"','"+provinceId+"','"+countryId+"','"+continentId+"','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','"+areaCode+"',0,'"+postCode+"','T');");
				System.out.println("   insert into j_county values ('"+id+"','"+pid+"','"+provinceId+"','"+countryId+"','"+continentId+"','"+name+"','"+JUtilString.toZhTw(name)+"','"+nameEn+"','"+areaCode+"',0,'"+postCode+"','T');");
			

				JHttpContext context=new JHttpContext();
				context.addRequestHeader("Referer","http://buy.taobao.com/auction/buy_now.jhtml");
				String zones=http.postResponse(context,client,"http://lsp.wuliu.taobao.com/locationservice/addr/output_address_town.do?l1="+provinceId+"&l2="+cityId+"&l3="+countyId+"&_ksTS=1396014988093_459&callback=jsonp460",null,"UTF-8");
				zones=zones.substring(zones.indexOf("result:{")+"result:{".length());
				
				zones=JUtilString.replaceAll(zones,"],","],\r\n");
				String[] zs=JUtilString.getTokens(zones,"\r\n");
				for(int j=0;j<zs.length;j++){
					if(zs[j].indexOf(":[")<0) continue;
					
					String id2=zs[j].substring(0,zs[j].indexOf(":["));
					id2=JUtilString.replaceAll(id2,"'","");
					
					String info2=zs[j].substring(zs[j].indexOf(":[")+2,zs[j].indexOf("]"));
					info2=JUtilString.replaceAll(info2,"'","");
					
					String[] cells2=info2.split(",");
					String name2=cells2[0];
					String nameEn2=cells2[2];
					
					log.addLine("    insert into j_zone values ('"+id2+"','"+countyId+"','"+cityId+"','"+provinceId+"','"+countryId+"','"+continentId+"','"+name2+"','"+JUtilString.toZhTw(name2)+"','"+nameEn2+"','',0,'','T');");
					System.out.println("    insert into j_zone values ('"+id2+"','"+countyId+"','"+cityId+"','"+provinceId+"','"+countryId+"','"+continentId+"','"+name2+"','"+JUtilString.toZhTw(name2)+"','"+nameEn2+"','',0,'','T');");
				}
			}
		}
		
		System.exit(0);
	}
}
