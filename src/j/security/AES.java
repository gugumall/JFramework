package j.security;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import j.util.JUtilString;

public class AES {
    // 编码
    private static final String ENCODING = "UTF-8";
    
    //算法
    private static final String ALGORITHM = "AES";
    
    // 默认的加密算法
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    
    //默认KEY和OFFSET
    private static final String KEY_DEFAULT="YDCk6EXK7i9v3ZeN";
    private static final String OFFSET_DEFAULT="ZGp5IfuxKDyDGyPI";
    
    /**
     * 
     * @param data
     * @param key
     * @param offset
     * @return
     */
    public static String encrypt(String data, String key, String offset) {
    	try {
	    	if(key==null || offset==null) {
	    		key=KEY_DEFAULT;
	    		offset=OFFSET_DEFAULT;
	    	}
	    	
	        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
	        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("ASCII"), ALGORITHM);
	        IvParameterSpec iv = new IvParameterSpec(offset.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
	        byte[] encrypted = cipher.doFinal(data.getBytes(ENCODING));
	        return Base64.getMimeEncoder().encodeToString(encrypted);//此处使用BASE64做转码。
    	}catch(Exception e) {
    		return data;
    	}
    }
    
    /**
     * 
     * @param data
     * @param key
     * @param offset
     * @return
     */
    public static String decrypt(String data, String key, String offset) {
    	try {
	    	if(key==null || offset==null) {
	    		key=KEY_DEFAULT;
	    		offset=OFFSET_DEFAULT;
	    	}
	    	
	        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
	        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("ASCII"), ALGORITHM);
	        IvParameterSpec iv = new IvParameterSpec(offset.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	        byte[] buffer = Base64.getMimeDecoder().decode(data);
	        byte[] encrypted = cipher.doFinal(buffer);
	        return new String(encrypted, ENCODING);//此处使用BASE64做转码。
    	}catch(Exception e) {
    		return data;
    	}
    }
    
    public static void main(String[] args) throws Exception{
    	String s1=JUtilString.randomStr(16);
    	String s2=JUtilString.randomStr(16);
    	
    	System.out.println(s1);
    	System.out.println(s2);
    	
    	String data="data";
    	String en=AES.encrypt(data, s1, s2);
    	System.out.println(en);
    	String de=AES.decrypt(en, s1, s2);
    	System.out.println(de);
    }
}
