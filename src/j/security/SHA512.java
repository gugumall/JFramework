package j.security;

import j.util.JUtilBytes;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author ceo
 * 
 */
public class SHA512{
	/**
	 * 利用java原生的摘要实现SHA512加密
	 * 
	 * @param text 加密后的报文
	 * @return
	 */
	public static String encode(String text){
		MessageDigest digest;
		String encode="";
		try{
			digest=MessageDigest.getInstance("SHA-512");
			digest.update(text.getBytes("UTF-8"));
			encode=JUtilBytes.byte2Hex(digest.digest());
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return encode;
	}
}