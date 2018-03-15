package j.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * 
 * @author one
 * 
 */
public class JUtilRSA{
	public static final String SIGN_ALGORITHMS="SHA1WithRSA";

	/**
	 * RSA签名
	 * 
	 * @param content 待签名数据
	 * @param privateKey 商户私钥(java使用PKCS8格式，PHP/.Net语言使用rsa_private_key.pem文件中内容)
	 * @param input_charset 编码格式
	 * @return 签名值
	 */
	public static String sign(String content,String privateKey,String input_charset){
		try{
			PrivateKey priKey=JUtilRSA.getPrivateKey(privateKey);

			java.security.Signature signature=java.security.Signature.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(input_charset));

			byte[] signed=signature.sign();

			return JUtilBase64.encode(signed);
		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * RSA验签名检查
	 * 
	 * @param content 待签名数据
	 * @param sign 签名值
	 * @param ali_public_key 支付宝公钥
	 * @param input_charset 编码格式
	 * @return 布尔值
	 */
	public static boolean verify(String content,String sign,String ali_public_key,String input_charset){
		try{
			KeyFactory keyFactory=KeyFactory.getInstance("RSA");
			byte[] encodedKey=JUtilBase64.decode(ali_public_key);
			PublicKey pubKey=keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature=java.security.Signature.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(input_charset));

			boolean bverify=signature.verify(JUtilBase64.decode(sign));
			return bverify;

		}catch(Exception e){
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 解密
	 * 
	 * @param content 密文
	 * @param private_key 商户私钥(java使用PKCS8格式，PHP/.Net语言使用rsa_private_key.pem文件中内容)
	 * @param input_charset 编码格式
	 * @return 解密后的字符串
	 */
	public static String decrypt(String content,String private_key,String input_charset) throws Exception{
		PrivateKey prikey=getPrivateKey(private_key);

		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE,prikey);

		InputStream ins=new ByteArrayInputStream(JUtilBase64.decode(content));
		ByteArrayOutputStream writer=new ByteArrayOutputStream();
		// rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
		byte[] buf=new byte[128];
		int bufl;

		while((bufl=ins.read(buf))!=-1){
			byte[] block=null;

			if(buf.length==bufl){
				block=buf;
			}else{
				block=new byte[bufl];
				for(int i=0;i<bufl;i++){
					block[i]=buf[i];
				}
			}

			writer.write(cipher.doFinal(block));
		}

		return new String(writer.toByteArray(),input_charset);
	}

	/**
	 * 得到私钥
	 * 
	 * @param key 密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(String key) throws Exception{
		byte[] keyBytes;

		keyBytes=JUtilBase64.decode(key);

		PKCS8EncodedKeySpec keySpec=new PKCS8EncodedKeySpec(keyBytes);

		KeyFactory keyFactory=KeyFactory.getInstance("RSA");

		PrivateKey privateKey=keyFactory.generatePrivate(keySpec);

		return privateKey;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		String s="<success>true</success><biz_content>MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDTyURkPgbxO2yzjki1b6M2rb2EjmWOkZH2wbXnK62yQwi7TMEV9n6QB2dZmsvyYMOhQXHGYLm4O5bcJyY3k/uzgpyKvQhwfidGXFma8bDzfcoaFmx7IOgrXokrLAzYY0ouv7clh0KYlX4UnxEodFJbnQaGC6Gju8T0nWAGLazhnQIDAQAB</biz_content>";
		String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMzwhjSQHqf5QZoEgJRVnEULy/Q5+cCfHOl/cRxzosDY4m4+0ajMoL0daP23M9i3GzjcsA7SkUcH7G3Jc5A6nXYwbE4BaOM36LWu+yXyzUiZZzz5n8EQ+UcJUZQx/pEu6/LtgN/di4L+ardIFa0XguHT0Pn8d1nvcbC2dS42RnppAgMBAAECgYApXXL5MQ2/AcSH/dimGBGOri7ggMM0aelACAUgpQZ4vk4VyoAu/f6DrWf/rfa9C1hnRcQTedTw7Vx/XGYC+pHFaL/UjeXmuyx40WHKnWBOYE9DVuGJLJbOAEVIA2QgvL9z3fmYG0e85fOcAXGW4qTziT34aqUGKG9j+Tt/f7bVwQJBAO4epGsio/yMNVTTRx++6Dfk94pOKmDxi+6qIlFjnJ7Ig7Kwg96/Hf6mHCHcNQYdyNB0Qa6l/79V0/DmC3wcV8UCQQDcVA/4Ezh57N/DxaKdRqjTgR3p2CD7HqwG2cHAyVKGEjy60wdKRxagO3jpvDX0NsNuUNgH4yQ0LoK64vVNR15VAkEAst3nqeaKgjGb+gz+1zTjYOEopQaUROAMugmo37RQuOFsNDTtycuML3X9md29IswKxbMeh5+AHezN2J4lMGRl9QJBALv1xs3Gb+ar6lUUNF7h2cLdooxMwg4ZI36QpFb4KLuRsVdcEhsOvEGjmsojsw+M7Hoe470OzGLrzsDqP4RWvyUCQBkK8XRE4KkRoUVQyK8tgzCszwilIw15ZoJ/7KgDQMmsMoZb2GCEVzMYmMMSqwaTGaxSSfsq3z5WcnlTfWVBgy4=";

		System.out.println(JUtilRSA.sign(s,privateKey,"GBK"));
	}
}
