package j.security;

import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import j.util.JUtilBase64;
import j.util.JUtilString;
import sun.rmi.runtime.Log;

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

    ////////////////////////微信小程序数据解密//////////////////////////////
    /**
     * 
     * @param data
     * @param key
     * @param offset
     * @return
     */
    public static String decrypt4WechatMiniProgram(String data, String key, String offset) {
    	try {
    		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
	        SecretKeySpec skeySpec = new SecretKeySpec(JUtilBase64.decode(key, true), ALGORITHM);
	        IvParameterSpec iv = new IvParameterSpec(JUtilBase64.decode(offset, true));//使用CBC模式，需要一个向量iv，可增加加密算法的强度
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	        byte[] buffer = Base64.getMimeDecoder().decode(data);
	        byte[] encrypted = cipher.doFinal(buffer);
	        return new String(encrypted, ENCODING);//此处使用BASE64做转码。
    	}catch(Exception e) {
    		//e.printStackTrace();
    		return data;
    	}
    }
    ////////////////////////微信小程序数据解密 end//////////////////////////////

    
    ////////////////////////支付宝小程序数据解密//////////////////////////////
    public static String decrypt4AlipayMiniProgram(String content, String key, String charset){
    	try {
	        //反序列化AES密钥
	        SecretKeySpec keySpec = new SecretKeySpec(Base64.getMimeDecoder().decode(key.getBytes()), "AES");
	         
	        //128bit全零的IV向量
	        byte[] iv = new byte[16];
	        for (int i = 0; i < iv.length; i++) {
	            iv[i] = 0;
	        }
	        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
	         
	        //初始化加密器并加密
	        Cipher deCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        deCipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
	        byte[] encryptedBytes = Base64.getMimeDecoder().decode(content.getBytes());
	        byte[] bytes = deCipher.doFinal(encryptedBytes);
	        return new String(bytes);
    	}catch(Exception e) {
    		//e.fillInStackTrace();
    		return content;
    	}
         
    }
    ////////////////////////支付宝小程序数据解密 end//////////////////////////////
    
    public static void main(String[] args) throws Exception{
    	/**
    	 * iv -> 
encryptedData -> 
session_key -> 
    	 */                                     
    	String de=AES.decrypt4AlipayMiniProgram("pS19C2IgDRd Ud0bOFjdYdULuxyKTSaNCVWe//tcBF1407b4BCcjpiCluuSVKUGXfYulsxVyjsKZue0ZMIJkrA==", 
    			"x+K3Sja0v5vHoAS/R0SLsA==", 
    			"UTF-8");
    	System.out.println("");
    	System.out.println("de:"+de);
    }
}
