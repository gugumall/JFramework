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
	
	public static void main(String[] args) throws Exception{
		String s="{exCode:\"exCodex\",template:[\"xx\\\"xxxxx\"]}";
		System.out.println(s);
		JSONObject obj=JUtilJSON.parse(s);
		System.out.print(obj.getJSONArray("template").get(0));
		
		String key="mHOgAMuK4590";//AppConfig.getPara("KUAIDI100", "key");
		String customer="08E9C2A098F3E028338A2A25A5867D5F";//AppConfig.getPara("KUAIDI100", "customer");
		
		
		//发起订阅
		String url="http://poll.kuaidi100.com/poll";//订阅接口
		
		String param="{\"company\":\"zhongtong\",";
		param+="\"number\":\"75114716763449\",";
		param+="\"key\":\""+key+"\",";
		param+="\"parameters\":{\"callbackurl\":\"https://www.gugumall.cn/express/kuaidi100/callback.jhtml\",";
		param+="\"salt\":\"15db9742161470366f23\",";
		param+="\"resultv2\":\"1\"}}";
		
		String sign=JUtilMD5.MD5EncodeToHex(param+key+customer).toUpperCase();

		Map paras=new HashMap();
		paras.put("schema","json");
		paras.put("param",param);
		
		try{
			JHttp http=JHttp.getInstance();
			JHttpContext context=new JHttpContext();
			HttpClient client=http.createClient();
			
			String result=http.postResponse(context,client,url,paras,"UTF-8");
			System.out.println(result);
			JSONObject resp=JUtilJSON.parse(result);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
}
