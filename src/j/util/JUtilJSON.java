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
	 * @param array
	 * @param index
	 * @return
	 */
	public static Object getObject(JSONArray array,int index){
		try{
			return array.get(index);
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
	 * @param js
	 * @param key
	 * @return
	 */
	public static Object get(JSONObject js,String key){
		try{
			return js.get(key);
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
		String a="{\"StationIDs\":[\"1553933605720538\",\"1553933605720538\"]}";
		JSONObject json=JUtilJSON.parse(a);
		JSONArray _StationIDs=JUtilJSON.array(json, "StationIDs");
		System.out.println(JUtilJSON.getObject(_StationIDs, 0));
	}
}
