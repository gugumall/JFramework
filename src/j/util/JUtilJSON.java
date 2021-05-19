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
			Object obj=js.get(key);
			if(obj==null) return null;
			
			String s="";			
			String cls=obj.getClass().getName();
			if("java.lang.String".equals(cls)) s=obj.toString();
			else if("java.lang.Integer".equals(cls)) s=obj.toString();
			else if("java.lang.Long".equals(cls)) s=obj.toString();
			else if("java.lang.Double".equals(cls)) s=JUtilMath.formatPrintPrecisionNoChange((Double)obj, (Double)obj, 0);
			else s=obj.toString();

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
	public static String encode(String s){
		if(s==null||"".equals(s)) return s;
		return JUtilString.encodeURI(s, "UTF-8");
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static String convert(String s){
		if(s==null||"".equals(s)) return s;

		s=JUtilString.replaceAll(s, "\\", "\\\\");
		s=JUtilString.replaceAll(s, "\"", "\\\"");
		s=JUtilString.replaceAll(s, "/", "\\/");
		s=JUtilString.replaceAll(s, "\b", "\\b");
		s=JUtilString.replaceAll(s, "\f", "\\f");
		s=JUtilString.replaceAll(s, "\n", "\\n");
		s=JUtilString.replaceAll(s, "\r", "\\r");
		s=JUtilString.replaceAll(s, "\t", "\\t");
		
		return s;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static String convertChars(String s){
		if(s==null||"".equals(s)) return s;
		
		s=JUtilString.replaceAll(s, "\\", "\\\\");
		s=JUtilString.replaceAll(s, "\"", "\\\"");
		s=JUtilString.replaceAll(s, "/", "\\/");
		s=JUtilString.replaceAll(s, "\b", "\\b");
		s=JUtilString.replaceAll(s, "\f", "\\f");
		s=JUtilString.replaceAll(s, "\n", "\\n");
		s=JUtilString.replaceAll(s, "\r", "\\r");
		s=JUtilString.replaceAll(s, "\t", "\\t");
		
		return s;
	}  
	
	public static void main(String[] args) throws Exception{
		System.out.println(JUtilJSON.convert("{\"agParent0\":\"v888888\",\"agParent1\":\"cc9988\"}"));
		
		Double x=0.23d;
		
		System.out.println(JUtilMath.formatPrintPrecisionNoChange(x, x, 0));
	}
}
