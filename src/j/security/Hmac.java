package j.security;

import j.util.JUtilBytes;
import j.util.JUtilRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Hmac{
	public static final String ALGORITHM_HmacMD5="HmacMD5";
	public static final String ALGORITHM_HmacSHA1="HmacSHA1";
	public static final String ALGORITHM_HmacSHA256="HmacSHA256";
	public static final String ALGORITHM_HmacSHA384="HmacSHA384";
	public static final String ALGORITHM_HmacSHA512="HmacSHA512";
	
	/**
	 * 初始化HMAC密钥
	 * @param algorithm
	 * @return
	 * @throws Exception
	 */
	public static byte[] initHmacKey(String algorithm) throws Exception{
		KeyGenerator keyGenerator=KeyGenerator.getInstance(algorithm);// HmacMD5,HmacSHA1,HmacSHA256,HmacSHA384,HmacSHA512
		return keyGenerator.generateKey().getEncoded();
	}

	/**
	 * 使用Hmac生成的密钥对数据进行加密
	 * @param algorithm
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptHmac(String algorithm, byte[] data, byte[] key) throws Exception{
		SecretKey secretKey=new SecretKeySpec(key, algorithm);
		Mac mac=Mac.getInstance(algorithm);
		mac.init(secretKey);
		return mac.doFinal(data);
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		//DIDI_OPERATOR_ID + enString + timeStamp + "0001"
		String enString="{\"OperatorSecret\":\"tNT9Q5hKANhD2CwD\",\"OperatorID\":\"101437000\"}";
		enString=AES.encrypt(enString, "uYwGAYhrU6nsBbnB", "7UktJHEftTAq8itW");

		System.out.println("enString1 -> "+enString);
		
		String enString2="J9rRGUJ+AOEIO0hFlfaqsuAgcP7O3FbqfpCihb8PRYm7WQjA3jq2qMyCGXWPTfNUTQWje0RsMavGeACt9eVhHg==";
		
		System.out.println("enString2 -> "+enString2);
		String s="101437000"+enString+"202005151530130001";
		byte[] data=s.getBytes("UTF-8");
		
		byte[] key="DpRJ2QaSHekuh9jE".getBytes("UTF-8");//initHmacKey(ALGORITHM_HmacMD5);
		System.out.println("key:"+new String(key, "UTF-8"));
		
		byte[] _data1=encryptHmac(ALGORITHM_HmacMD5, data, key);

		System.out.println("_data1:"+JUtilBytes.byte2Hex(_data1).toUpperCase());
		
		System.out.println("_data2:"+AES.decrypt(enString, "uYwGAYhrU6nsBbnB", "7UktJHEftTAq8itW"));
		
	}
}
