package j.util;

import j.common.JObject;
import j.http.JHttp;
import j.http.JHttpContext;
import j.log.Logger;
import j.sys.AppConfig;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author ceo
 *
 */
public class JUtilJSON{
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static JSONObject parse(String s){
		JSONObject js=null;
		try{
			js=new JSONObject(s);
		}catch(Exception e){
			try{
				js=new JSONObject("{}"); 
			}catch(Exception ex){}
		}
		return js;
	}
	
	/**
	 * 
	 * @param js
	 * @param key
	 * @return
	 */
	public static String string(JSONObject js,String key){
		try{
			String s=js.getString(key);
			if(s!=null&&s.startsWith("jis:")) s=JObject.intSequence2String(s);
			return s;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 
	 * @param js
	 * @param key
	 * @return
	 */
	public static JSONArray array(JSONObject js,String key){
		try{
			return js.getJSONArray(key);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 
	 * @param array
	 * @param index
	 * @return
	 */
	public static JSONObject get(JSONArray array,int index){
		try{
			return array.getJSONObject(index);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 
	 * @param js
	 * @param key
	 * @return
	 */
	public static JSONObject object(JSONObject js,String key){
		try{
			return js.getJSONObject(key);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static String format(String s){
		return "jis:"+JObject.string2IntSequence(s);
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static String convert(String s){
		if(s==null||"".equals(s)) return s;
		s=JUtilString.replaceAll(s,"\"","\\\"");
		s=JUtilString.replaceAll(s,"[","\\[");
		s=JUtilString.replaceAll(s,"]","\\]");
		s=JUtilString.replaceAll(s,"{","\\{");
		s=JUtilString.replaceAll(s,"}","\\}");
		return s;
	}
	
	public static void main(String[] args) throws Exception{
		String a="a";
		synchronized(a){
			System.out.println("\\\"");
			synchronized(a){
				System.out.println("22222222");
			}
		}
	}
}
