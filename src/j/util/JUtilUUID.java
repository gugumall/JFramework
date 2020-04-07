
package j.util;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import j.http.JHttp;
import j.http.JHttpContext;


/**
 * 
 * @author 肖炯
 *
 */
public final class JUtilUUID {
	private static Object lock = new Object();
	
	private static int hostUnique = (new Object()).hashCode();
	private static long lastTime = System.currentTimeMillis();
	private static long DELAY = 1L;

	/**
	 * 生成UUID
	 * @return
	 */
	public static String genUUID() {
		synchronized (lock) {
			String uuid=Integer.toString(hostUnique, 16) + Long.toString(lastTime, 16) + JUtilString.randomStr(8);
			lastTime+=DELAY;
			return uuid;
		}
	}

	/**
	 * 生成UUID
	 * @return
	 */
	public static String genUUIDShort() {
		synchronized (lock) {
			String uuid=lastTime + JUtilString.randomNum(3);
			lastTime+=DELAY;
			return uuid;
		}
	}

	static String latestLeagueName="xxx";
	public static Object format(String all)throws Exception{
		try{
			Map gain=new LinkedHashMap();
			String original=all;
			
			if(all.indexOf("rt-data-hide")>0){
				all=all.substring(0,all.indexOf("rt-data-hide"));
				original=original.substring(original.indexOf("rt-data-hide"));
			}
			
			//int _currentPage = thread.getKeyObject("currentPage") == null ? 1: Integer.parseInt(thread.getKeyObject("currentPage").toString());

			System.out.println("champion all:"+all);
			System.out.println("champion original--:"+original);
	
			int start=-1;
			int end=-1;
	
			//log.log(thread.getRealManager().getCode()+",all:"+all,-1);
			if(all.indexOf("rt-event-or")<0){			
				latestLeagueName=all.substring(0,all.indexOf("<"));
				latestLeagueName=JUtilString.replaceAll(latestLeagueName,"- ","-");
				latestLeagueName=JUtilString.replaceAll(latestLeagueName," -","-");
				return null;
			}
			
			if(latestLeagueName==null){
				System.out.println("latestLeagueName null");
				return null;
			}
			
			start=all.indexOf("'btn-toggle'>")+13;
			end=all.indexOf("日",start);
			String startTime=all.substring(start,end);
			
			String year=startTime.substring(0,startTime.indexOf("年"));
			
			String month=startTime.substring(startTime.indexOf("年")+1,startTime.indexOf("月"));
			if(month.length()==1) month="0"+month;
			
			String date=startTime.substring(startTime.indexOf("月")+1);
			if(date.length()==1) date="0"+date;
			
			startTime=year+"-"+month+"-"+date;
			
			start=all.indexOf("id=\"e-")+6;
			end=all.indexOf("\"",start);
			String matchId=all.substring(start,end);
			gain.put("match_id",matchId);
			
			if(all.indexOf("<spanclass='e-cancel'>")>0){
				start=all.indexOf("<spanclass='e-cancel'>")+22;
				end=all.indexOf("<",start);
				String result=all.substring(start,end);
				gain.put("result",result.replaceAll("/","<BR>"));
			}else{			
				start=all.indexOf("<divclass='rt-set");
				start=all.indexOf(">",start)+1;
				end=all.indexOf("<",start);
				String result=all.substring(start,end);
				
				//other winner
				start=all.indexOf("spanclass='prop")+15;
				while(start>15){
					start=all.indexOf(">",start)+1;
					end=all.indexOf("</span>",start);
					result+="<br/>"+all.substring(start,end);
					
					start=all.indexOf("spanclass='prop",end)+15;
				}
				gain.put("result",result);			
			}
			
			//other events
			start=original.indexOf("title=\"")+7;
			while(start>7){
				end=original.indexOf("\"",start);
				if(end>=start){
					original=original.substring(0,start-7)+original.substring(end+1);
				}
				start=original.indexOf("title=\"",end)+7;
			}
			//log.log("original:"+original, -1);
			
			int index=1;
			start=original.indexOf("<spanclass=\"pt\"")+15;
			while(start>15){
				Map next=formatSelf(matchId,original,start);
				if(next!=null){
					gain.put("next_"+index,next);
					index++;
				}
				
				start=original.indexOf("<spanclass=\"pt\"",start)+15;
			}
			
			all=null;

			System.out.println();
			System.out.println();
			System.out.println(gain);
			System.out.println();
			System.out.println();
			
			List keys=new ArrayList();
			keys.addAll(gain.keySet());
			
			for(int k=0;k<keys.size();k++){
				String key=(String)keys.get(k);
				if(key.startsWith("next_")){
					Map next=(Map)gain.remove(key);

					System.out.println(next);
				}
			}
			System.out.println();
			System.out.println();
			System.out.println(gain);
			
			return gain;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param date
	 * @param all
	 * @param start
	 * @return
	 */
	private static  Map formatSelf(String parentMatchId,String all,int start){
		Map gain=new LinkedHashMap();
		
		//gain.put("date",date);
		
		if(all.indexOf("title")==start){
			start=all.indexOf("\">",start)+2;
		}else{
			start=all.indexOf(">",start)+1;
		}
		int end=all.indexOf("<",start);
		String event=all.substring(start,end);
		//gain.put("event",event);	
		
		start=all.indexOf("class=\"r-odds",end)+13;
		start=all.indexOf(">",start)+1;
		end=all.indexOf("<",start);
		String result=all.substring(start,end);
		gain.put("result",result);	

		String temp=JUtilString.replaceAll(event, " ", "");
		String hostId=(parentMatchId+temp).hashCode()+"";
		gain.put("host_id",hostId);

		temp=JUtilString.replaceAll(event, " ", "");
		temp=JUtilString.replaceAll(temp,"收盘后之最后位数", "收盘后之最后位数 ");
		String hostIdOpt=(parentMatchId+temp).hashCode()+"";
		gain.put("host_id_optional",hostIdOpt);
		

		temp=JUtilString.replaceAll(event, " ", "");
		temp=JUtilString.replaceAll(temp,"/", "\\/");
		String hostIdOpt2=(parentMatchId+temp).hashCode()+"";
		gain.put("host_id_optional_2",hostIdOpt2);
		//log.log("other:"+gain,-1);
		
		return gain;
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println(JUtilMD5.MD5EncodeToHex("Aa131232"));

		System.exit(0);
	}
}