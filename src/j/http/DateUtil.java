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
public class DateUtil {
	public static final int DEFAULT = 0;
	public static final int YM = 1;
	public static final int YMR_SLASH = 11;
	public static final int NO_SLASH = 2;
	public static final int YM_NO_SLASH = 3;
	public static final int DATE_TIME = 4;
	public static final int DATE_TIME_NO_SLASH = 5;
	public static final int DATE_HM = 6;
	public static final int TIME = 7;
	public static final int HM = 8;
	public static final int LONG_TIME = 9;
	public static final int SHORT_TIME = 10;
	public static final int DATE_TIME_LINE = 12;

	/**
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String dateToStr(Date date, String pattern) {
		if ((date == null) || (date.equals(""))){
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToStr(Date date) {
		return dateToStr(date, "yyyy/MM/dd");
	}

	/**
	 * 
	 * @param date
	 * @param type
	 * @return
	 */
	public static String dateToStr(Date date, int type) {
		switch (type) {
		case 0:
			return dateToStr(date);
		case 1:
			return dateToStr(date, "yyyy/MM");
		case 2:
			return dateToStr(date, "yyyyMMdd");
		case 11:
			return dateToStr(date, "yyyy-MM-dd");
		case 3:
			return dateToStr(date, "yyyyMM");
		case 4:
			return dateToStr(date, "yyyy/MM/dd HH:mm:ss");
		case 5:
			return dateToStr(date, "yyyyMMddHHmmss");
		case 6:
			return dateToStr(date, "yyyy/MM/dd HH:mm");
		case 7:
			return dateToStr(date, "HH:mm:ss");
		case 8:
			return dateToStr(date, "HH:mm");
		case 9:
			return dateToStr(date, "HHmmss");
		case 10:
			return dateToStr(date, "HHmm");
		case 12:
			return dateToStr(date, "yyyy-MM-dd HH:mm:ss");
		}
		throw new IllegalArgumentException("Type undefined : " + type);
	}
	

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
		
		String response=http.getResponse(context, client, url);

		System.out.println(response);
		

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
		System.out.println(responseSign);
		
		if(v.equalsIgnoreCase(responseSign)){
			System.out.println("校验通过");
		}else{
			System.out.println("校验未通过");
		}
	}
}