package j.util;

import org.json.JSONArray;
import org.json.JSONObject;

import j.common.JObject;

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
		//s=JUtilString.replaceAll(s,"[","\\[");
		//s=JUtilString.replaceAll(s,"]","\\]");
		//s=JUtilString.replaceAll(s,"{","\\{");
		//s=JUtilString.replaceAll(s,"}","\\}");
		return s;
	}
	
	public static void main(String[] args) throws Exception{
		String a="{\"openedPeriodNumber\":20200208103,\"openedDate\":\"2020/2/8 14:39:00\",\"openingPeriodNumber\":104,\"openingDate\":\"2020/2/8 14:44:00\",\"totalSeconds\":214,\"jackpot\":1719332,\"numbersArray\":[\"7\",\"3\",\"9\",\"2\",\"4\",\"8\",\"6\",\"1\",\"10\",\"5\"],\"curDate\":\"2020/2/8 14:40:35\"}";
		synchronized(a){
			System.out.println("22/2/2".split("/")[1]);
		}
	}
}
