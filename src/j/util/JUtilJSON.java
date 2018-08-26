package j.util;

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
		org.json.JSONObject js=null;
		try{
			js=new org.json.JSONObject(s);
		}catch(Exception e){
			try{
				js=new org.json.JSONObject("{}"); 
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
			return js.getString(key);
		}catch(Exception e){
			return null;
		}
	}
}
