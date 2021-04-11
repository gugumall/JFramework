package j.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import j.http.JHttp;
import j.security.CertificateHelper;

/**
 * 
 * @author 肖炯
 *
 * 2020年4月17日
 *
 * <b>功能描述</b>
 */
public class JUtilRSA{
	public static final String SHA1WithRSA="SHA1WithRSA";
	public static final String SHA256withRSA="SHA256withRSA";
    public static final String RSA_ALGORITHM = "RSA";
    

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

			java.security.Signature signature=java.security.Signature.getInstance(SHA1WithRSA);

			signature.initSign(priKey);
			signature.update(content.getBytes(input_charset));

			byte[] signed=signature.sign();

			return JUtilBase64.encode(signed);
		}catch(Exception e){
			//e.printStackTrace();
		}

		return null;
	}

	/**
	 * RSA验签名检查
	 * 
	 * @param content 待签名数据
	 * @param sign 签名值
	 * @param publicKey 公钥
	 * @param input_charset 编码格式
	 * @return 布尔值
	 */
	public static boolean verify(String content,String sign,String publicKey,String input_charset){
		return verify(content, sign, publicKey, input_charset, SHA1WithRSA);
	}
	
	/**
	 * 
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @param input_charset
	 * @param ALGO
	 * @return
	 */
	public static boolean verify(String content,String sign,String publicKey,String input_charset,String ALGO){
		try{
			KeyFactory keyFactory=KeyFactory.getInstance(RSA_ALGORITHM);
			byte[] encodedKey=JUtilBase64.decode(publicKey);
			PublicKey pubKey=keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature=java.security.Signature.getInstance(ALGO);

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
	 * 
	 * @param content
	 * @param sign
	 * @param publicKeyIs
	 * @param input_charset
	 * @return
	 */
	public static boolean verify(String content,String sign,InputStream publicKeyIs,String input_charset){
		return verify(content, sign, publicKeyIs, input_charset, SHA1WithRSA);
	}
	
	/**
	 * 
	 * @param expectedSignature
	 * @param actualSignatureEncoded
	 * @param publicKeyIs
	 * @param input_charset
	 * @param ALGO
	 * @return
	 */
	public static boolean verify(String expectedSignature,
			String actualSignatureEncoded,
			InputStream publicKeyIs,
			String input_charset,
			String ALGO){
		try{
			Collection<X509Certificate> clientCerts=CertificateHelper.getX509CertificateFromStream(publicKeyIs);
			System.out.println("clientCerts -> "+clientCerts.size());

			if(clientCerts==null || clientCerts.isEmpty()) return false;
			
			// Get the signatureAlgorithm from the PAYPAL-AUTH-ALGO HTTP header
			Signature signatureAlgorithm = Signature.getInstance(ALGO);
			// Get the certData from the URL provided in the HTTP headers and cache it
			
			X509Certificate[] clientChain = clientCerts.toArray(new X509Certificate[0]);
			signatureAlgorithm.initVerify(clientChain[0].getPublicKey());
			if(input_charset==null) signatureAlgorithm.update(expectedSignature.getBytes());
			else signatureAlgorithm.update(expectedSignature.getBytes(input_charset));
			
			// Actual signature is base 64 encoded and available in the HTTP headers
			byte[] actualSignature = input_charset==null?Base64.decodeBase64(actualSignatureEncoded.getBytes()):Base64.decodeBase64(actualSignatureEncoded.getBytes(input_charset));
			boolean isValid = signatureAlgorithm.verify(actualSignature);
			return isValid;
		}catch(Exception e){
			e.printStackTrace();
		}

		return false;
	}
	
    /**
     * 
     * @param data
     * @param publicKey
     * @param blockSize
     * @param CHARSET
     * @return
     * @throws Exception
     */
	public static String publicEncrypt(String data, String publicKey, int blockSize, String CHARSET) throws Exception{
		RSAPublicKey pubKey = JUtilRSA.getPublicKey(publicKey);
		
		Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		
		InputStream ins=new ByteArrayInputStream(data.getBytes(CHARSET));
		ByteArrayOutputStream writer=new ByteArrayOutputStream();
		
		byte[] buffer=new byte[64];
		int bufferLength;

		while((bufferLength=ins.read(buffer))!=-1){
			byte[] block=null;
			if(buffer.length==bufferLength){
				block=buffer;
			}else{
				block=new byte[bufferLength];
				for(int i=0;i<bufferLength;i++){
					block[i]=buffer[i];
				}
			}
			writer.write(cipher.doFinal(block));
		}

		return JUtilBase64.encode(writer.toByteArray(), true);
	}

    /**
     * 
     * @param data
     * @param privateKey
     * @param blockSize
     * @param CHARSET
     * @return
     * @throws Exception
     */
	public static String publicDecrypt(String data, String privateKey, int blockSize, String CHARSET) throws Exception{
		RSAPrivateKey priKey = JUtilRSA.getPrivateKey(privateKey);
		
		Cipher cipher=Cipher.getInstance(RSA_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE,priKey);

		InputStream ins=new ByteArrayInputStream(JUtilBase64.decode(data));
		ByteArrayOutputStream writer=new ByteArrayOutputStream();
		
		byte[] buffer=new byte[blockSize];
		int bufferLength;

		while((bufferLength=ins.read(buffer))!=-1){
			byte[] block=null;

			if(buffer.length==bufferLength){
				block=buffer;
			}else{
				block=new byte[bufferLength];
				for(int i=0;i<bufferLength;i++){
					block[i]=buffer[i];
				}
			}

			writer.write(cipher.doFinal(block));
		}

		return new String(writer.toByteArray(), CHARSET);
	}
	
	/**
     * 
     * @param data
     * @param privateKey
     * @param blockSize
     * @param CHARSET
     * @return
     * @throws Exception
     */
	public static String privateEncrypt(String data, String privateKey, int blockSize, String CHARSET) throws Exception{
		RSAPrivateKey priKey = JUtilRSA.getPrivateKey(privateKey);
		
		Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, priKey);
		
		InputStream ins=new ByteArrayInputStream(data.getBytes(CHARSET));
		ByteArrayOutputStream writer=new ByteArrayOutputStream();
		
		byte[] buffer=new byte[64];
		int bufferLength;

		while((bufferLength=ins.read(buffer))!=-1){
			byte[] block=null;
			if(buffer.length==bufferLength){
				block=buffer;
			}else{
				block=new byte[bufferLength];
				for(int i=0;i<bufferLength;i++){
					block[i]=buffer[i];
				}
			}
			writer.write(cipher.doFinal(block));
		}

		return JUtilBase64.encode(writer.toByteArray(), true);
	}

    /**
     * 
     * @param data
     * @param publicKey
     * @param blockSize
     * @param CHARSET
     * @return
     * @throws Exception
     */
	public static String privateDecrypt(String data, String publicKey, int blockSize, String CHARSET) throws Exception{
		RSAPublicKey pubKey = JUtilRSA.getPublicKey(publicKey);
		
		Cipher cipher=Cipher.getInstance(RSA_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE,pubKey);

		InputStream ins=new ByteArrayInputStream(JUtilBase64.decode(data));
		ByteArrayOutputStream writer=new ByteArrayOutputStream();
		
		byte[] buffer=new byte[blockSize];
		int bufferLength;

		while((bufferLength=ins.read(buffer))!=-1){
			byte[] block=null;

			if(buffer.length==bufferLength){
				block=buffer;
			}else{
				block=new byte[bufferLength];
				for(int i=0;i<bufferLength;i++){
					block[i]=buffer[i];
				}
			}

			writer.write(cipher.doFinal(block));
		}

		return new String(writer.toByteArray(), CHARSET);
	}

	/**
	 * 
	 * @param publicKey 密钥字符串（经过base64编码）
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 
     * @param privateKey  密钥字符串（经过base64编码）
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }
    
    /**
     * 
     * @param keySize
     * @return [公钥，私钥]
     */
    public static String[] createKeyPair(int keySize) throws Exception{
        // 为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm -> " + RSA_ALGORITHM);
        }

        // 初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        
        // 生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        
        // 得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = JUtilBase64.encode(publicKey.getEncoded());
        //String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        
        // 得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = JUtilBase64.encode(privateKey.getEncoded());
        //String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
       
        // 返回map
        return new String[] {publicKeyStr, privateKeyStr};
    }
    
    public static long crc32(String data) {
		if (data == null) {
			return -1;
		}

		try {
			// get bytes from string
			byte bytes[] = data.getBytes("UTF-8");
			Checksum checksum = new CRC32();
			// update the current checksum with the specified array of bytes
			checksum.update(bytes, 0, bytes.length);
			// get the current checksum value
			return checksum.getValue();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String[] pair=createKeyPair(1024);
		System.out.println("public key:"+pair[0]);
		System.out.println("private key:"+pair[1]);
		
		String body="{ \"id\": \"WH-COC11055RA711503B-4YM959094A144403T\", \"create_time\": \"2018-04-16T21:21:49.000Z\", \"event_type\": \"CHECKOUT.ORDER.COMPLETED\", \"resource_type\": \"checkout-order\", \"resource_version\": \"2.0\", \"summary\": \"Checkout Order Completed\", \"resource\": { \"id\": \"5O190127TN364715T\", \"status\": \"COMPLETED\", \"intent\": \"CAPTURE\", \"gross_amount\": { \"currency_code\": \"USD\", \"value\": \"100.00\" }, \"payer\": { \"name\": { \"given_name\": \"John\", \"surname\": \"Doe\" }, \"email_address\": \"buyer@example.com\", \"payer_id\": \"QYR5Z8XDVJNXQ\" }, \"purchase_units\": [ { \"reference_id\": \"d9f80740-38f0-11e8-b467-0ed5f89f718b\", \"amount\": { \"currency_code\": \"USD\", \"value\": \"100.00\" }, \"payee\": { \"email_address\": \"seller@example.com\" }, \"shipping\": { \"method\": \"United States Postal Service\", \"address\": { \"address_line_1\": \"2211 N First Street\", \"address_line_2\": \"Building 17\", \"admin_area_2\": \"San Jose\", \"admin_area_1\": \"CA\", \"postal_code\": \"95131\", \"country_code\": \"US\" } }, \"payments\": { \"captures\": [ { \"id\": \"3C679366HH908993F\", \"status\": \"COMPLETED\", \"amount\": { \"currency_code\": \"USD\", \"value\": \"100.00\" }, \"seller_protection\": { \"status\": \"ELIGIBLE\", \"dispute_categories\": [ \"ITEM_NOT_RECEIVED\", \"UNAUTHORIZED_TRANSACTION\" ] }, \"final_capture\": true, \"seller_receivable_breakdown\": { \"gross_amount\": { \"currency_code\": \"USD\", \"value\": \"100.00\" }, \"paypal_fee\": { \"currency_code\": \"USD\", \"value\": \"3.00\" }, \"net_amount\": { \"currency_code\": \"USD\", \"value\": \"97.00\" } }, \"create_time\": \"2018-04-01T21:20:49Z\", \"update_time\": \"2018-04-01T21:20:49Z\", \"links\": [ { \"href\": \"https://api.paypal.com/v2/payments/captures/3C679366HH908993F\", \"rel\": \"self\", \"method\": \"GET\" }, { \"href\": \"https://api.paypal.com/v2/payments/captures/3C679366HH908993F/refund\", \"rel\": \"refund\", \"method\": \"POST\" } ] } ] } } ], \"create_time\": \"2018-04-01T21:18:49Z\", \"update_time\": \"2018-04-01T21:20:49Z\", \"links\": [ { \"href\": \"https://api.paypal.com/v2/checkout/orders/5O190127TN364715T\", \"rel\": \"self\", \"method\": \"GET\" } ] }, \"links\": [ { \"href\": \"https://api.sandbox.paypal.com/v1/notifications/webhooks-events/WH-COC11055RA711503B-4YM959094A144403T\", \"rel\": \"self\", \"method\": \"GET\" }, { \"href\": \"https://api.sandbox.paypal.com/v1/notifications/webhooks-events/WH-COC11055RA711503B-4YM959094A144403T/resend\", \"rel\": \"resend\", \"method\": \"POST\" } ], \"zts\": 1494957670, \"event_version\": \"1.0\" }";
		long crc=crc32(body);
		System.out.println("callback crc -> " +crc);
		
		String transmissionId="483fa030-92b4-11eb-87f7-855a176f5051";
		String transmissionTime="2021-04-01T06:34:16Z";
		String webhookId="WH-COC11055RA711503B-4YM959094A144403T";
		
		JHttp http=JHttp.getInstance();
		
		InputStream publicKeyIs=http.getStreamResponse(null, null, "https://api.paypal.com/v1/notifications/certs/CERT-360caa42-fca2a594-5edc0ebc");
		
		String expectedSignature = String.format("%s|%s|%s|%s", transmissionId, transmissionTime, webhookId, crc);
		System.out.println("callback expectedSignature -> " +expectedSignature);
		
		
		String sign="a4vNhYMDauBKsBT6KrxtVV1vzWHLns5wo63fNGmB4g7+vSdB/snu7nII3Q8zw7uXnGz4sSdPO6+gUPcV/6QexDqAJGIrq+oFNML/gBTJJMFdFgKCjxG7Ec7WTlT9qNizd0UXm4xq2QmtFXuicqwGCzq6g2H+riBNUbWAIW7PMM5O3KOvHvD7S7zLLdOsV2F2XDjnoWY9+l1UkxbCcstLFT22x/gpwbSEWS0e8uXvmf1YDZ5G2Of2v1n7v9NG1voibMTprmgY9KUjaED5pxMi/9AwTBnb8o5YXtPoWe0l54lkKb71W1BZ1wgToP718OXbX0zjzIa23T1wo8L8q/6pRw==";
		System.out.println("decrypt:"+JUtilRSA.verify(expectedSignature, 
				sign, 
				publicKeyIs, 
				"UTF-8", 
				SHA256withRSA));
		
		System.exit(0);
	}
}
