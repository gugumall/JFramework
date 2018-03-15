package j.security;

import j.util.JUtilBytes;
import j.util.JUtilRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HmacSHA512{
	/**
	 * 
	 * @return
	 */
	private static byte[] genPrivateKeyOriginal(){
		byte bytes[]=new byte[16];
		for(int i=0;i<16;i++){
			bytes[i]=(byte)JUtilRandom.nextInt(256+1);
		}

		return bytes;
	}
	
	/**
	 * 初始化HMAC密钥
	 */
	public static byte[] initHmacKey() throws Exception{
		KeyGenerator keyGenerator=KeyGenerator.getInstance("HmacSHA512");// HmacMD5,HmacSHA1,HmacSHA256,HmacSHA384,HmacSHA512
		return keyGenerator.generateKey().getEncoded();
	}

	/**
	 * 使用Hmac生成的密钥对数据进行加密
	 */
	public static byte[] encryptHmac(byte[] data,byte[] key) throws Exception{
		SecretKey secretKey=new SecretKeySpec(key,"HmacSHA512");
		Mac mac=Mac.getInstance("HmacSHA512");
		mac.init(secretKey);
		return mac.doFinal(data);
	}
	
	public static void main(String[] args)throws Exception{
		byte[] data=genPrivateKeyOriginal();

		byte[] key=initHmacKey();
		
		for(int i=0;i<10;i++){
			
			data=encryptHmac(data,key);
			
			System.out.println(JUtilBytes.byte2Hex(data));
		}
	}
}
