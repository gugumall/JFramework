package j.security;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    // 编码
    private static final String ENCODING = "UTF-8";
    
    //算法
    private static final String ALGORITHM = "AES";
    
    // 默认的加密算法
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    
    /**
     * 
     * @param data
     * @param key
     * @param offset
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key, String offset) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("ASCII"), ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(offset.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(data.getBytes(ENCODING));
        return Base64.getMimeEncoder().encodeToString(encrypted);//此处使用BASE64做转码。
    }
    
    /**
     * 
     * @param data
     * @param key
     * @param offset
     * @return
     * @throws Exception
     */
    public static String decrypt(String data, String key, String offset) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("ASCII"), ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(offset.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] buffer = Base64.getMimeDecoder().decode(data);
        byte[] encrypted = cipher.doFinal(buffer);
        return new String(encrypted, ENCODING);//此处使用BASE64做转码。
    }
    
    public static void main(String[] args) throws Exception{
    	String data="data";
    	String en=AES.encrypt(data, "161772df-d4f6-4a", "b1-a373-1baf8b23");
    	System.out.println(en);
    	String de=AES.decrypt(en, "161772df-d4f6-4a", "b1-a373-1baf8b23");
    	System.out.println(de);
    }
}
