package j.security;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacSHA1{
	private static final String ALGORITHM="HmacSHA1";
	private static final String CHARSET_UTF8="UTF-8";

	/**
	 * 使用 HMAC-SHA1 签名方法对data进行签名
	 * 
	 * @param data 被签名的字符串
	 * @param key 密钥
	 * @return 加密后的字符串
	 */
	public static byte[] genHMAC(String baseString,String secret){
		try{
			Mac mac=Mac.getInstance(ALGORITHM);
			SecretKeySpec keySpec=new SecretKeySpec(secret.getBytes(CHARSET_UTF8),ALGORITHM);
			mac.init(keySpec);
			return mac.doFinal(baseString.getBytes(CHARSET_UTF8));
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String newStringByBase64(byte[] bytes) throws UnsupportedEncodingException{
		if(bytes==null||bytes.length==0){
			return null;
		}
		return Base64.getEncoder().encodeToString(bytes);
	}

	/**
	 * 
	 * @param queries
	 * @return
	 */
	public static String composeStringToSign(Map<String,String> queries){
		String[] sortedKeys=(String[])queries.keySet().toArray(new String[0]);
		Arrays.sort(sortedKeys);
		StringBuilder canonicalizedQueryString=new StringBuilder();
		for(String key:sortedKeys){
			canonicalizedQueryString.append("&").append(percentEncode(key)).append("=").append(percentEncode((String)queries.get(key)));
		}
		StringBuilder stringToSign=new StringBuilder();
		stringToSign.append("GET");
		stringToSign.append("&");
		stringToSign.append(percentEncode("/"));
		stringToSign.append("&");
		stringToSign.append(percentEncode(canonicalizedQueryString.toString().substring(1)));
		return stringToSign.toString();
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static String percentEncode(String value){
		try{
			return value==null?null:URLEncoder.encode(value,CHARSET_UTF8).replace("+","%20").replace("*","%2A").replace("%7E","~");
		}catch(Exception e){
		}
		return "";
	}

	/**
	 * get SignatureNonce
	 * @return
	 */
	public static String getUniqueNonce(){
		UUID uuid=UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * get timestamp
	 * @return
	 */
	public static String getISO8601Time(){
		Date nowDate=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(new SimpleTimeZone(0,"GMT"));
		return df.format(nowDate);
	}
}
