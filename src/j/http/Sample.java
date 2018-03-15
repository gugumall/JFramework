package j.http;

import j.util.ConcurrentMap;
import j.util.JUtilMD5;
import j.util.JUtilSorter;
import j.util.JUtilString;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.client.HttpClient;

/**
 * 
 * @author 肖炯
 *
 */
public class Sample {
	/**
	 * 
	 * @param key
	 * @param parameters
	 * @return
	 */
	public static String createSign(String key,ConcurrentMap parameters){
		List keys=parameters.listKeys();//所有参数名
		
		JUtilString sorter=new JUtilString();
		keys=sorter.bubble(keys, JUtilSorter.ASC);//将参数名按字母排序，如  aac,aad,cac,f2v...
		
		StringBuffer sb = new StringBuffer();

        for(int i=0;i<keys.size();i++){//按参数名的字母顺序拼接字符串
            String k = (String)keys.get(i);
            String v = (String)parameters.get(k);

            if(!"v".equals(k)) {//不包括签名本身和无关参数
                sb.append(k+"="+v+"&");
            }
        }

        sb.append("k="+key);//通信秘钥
        
        System.out.println(sb.toString());

        String sign = JUtilMD5.MD5EncodeToHex(sb.toString());

        return sign;
    }
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		JHttp http=JHttp.getInstance();
		HttpClient client=http.createClient();
		JHttpContext context=new JHttpContext();
		
		final String keyOfThisShop="0000-0000-0000-0000";
		
		String url="http://www.gugumall.cn/I.handler";

		url+="?request=call";
		url+="&cmd=get-products";
		url+="&seller=systemseller";
		url+="&update=2016-06-06 00:00:00";
		url+="&catalog=";
		url+="&rpp=5";
		url+="&pn=1";
		
		ConcurrentMap params=new ConcurrentMap();
		params.put("request", "call");
		params.put("cmd", "get-products");
		params.put("seller", "systemseller");
		params.put("update", "2016-06-06 00:00:00");
		params.put("catalog", "");
		params.put("rpp", "5");
		params.put("pn", "1");
		
		String sign=createSign(keyOfThisShop,params);
		
		url+="&v="+sign;
		
		String response=http.getResponse(context, client, url,"UTF-8");
		

		int start=response.lastIndexOf("<v>");
		if(start<0){
			System.out.println("未成功识别出商户信息，无检验码，具体错误请将整个response作为xml解析");
			return;
		}
		
		int end=response.indexOf("</v>",start);
		String v=response.substring(start+3,end);
		

		System.out.println(v);
		
		response=response.substring(0,start);//xml部分
		
		String responseSign=response+keyOfThisShop;
		
		responseSign = JUtilMD5.MD5EncodeToHex(responseSign);
		
		if(v.equalsIgnoreCase(responseSign)){
			System.out.println("校验通过");
		}else{
			System.out.println("校验未通过");
		}
	}
}