package j.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author 肖炯
 *
 * 2020年4月17日
 *
 * <b>功能描述</b>
 */
public class JUtilRSA{
	public static final String SIGN_ALGORITHMS="SHA1WithRSA";
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

			java.security.Signature signature=java.security.Signature.getInstance(SIGN_ALGORITHMS);

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
		try{
			KeyFactory keyFactory=KeyFactory.getInstance(RSA_ALGORITHM);
			byte[] encodedKey=JUtilBase64.decode(publicKey);
			PublicKey pubKey=keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature=java.security.Signature.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(input_charset));

			boolean bverify=signature.verify(JUtilBase64.decode(sign));
			return bverify;
		}catch(Exception e){
			//e.printStackTrace();
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

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String[] pair=createKeyPair(1024);
		System.out.println("public key:"+pair[0]);
		System.out.println("private key:"+pair[1]);
		
		String s="As212121";
		String key="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTFJCe5ogqR++wLkPZm0bnBirEvFhuEY81FjP17mdKYDEQzRYmmVp4888z4rC618ovjfeJ8RJFUf7X5JgEyimKJEWUwxP8GVkMtjimtQpEp5TTZIattH3Jlwub/YlCuL+a9LfPkbqXWCdO6dc7oiRaOqPVk65KKY9usGnHG0X3XQIDAQAB";
		String other="48CA474B2CE18C55ABE2818B9CF9AFFC4DA27FC07A2DDCBF";
		
		String e=JUtilRSA.publicEncrypt(other+"|"+s, key, 64, "UTF-8");
		System.out.println("encrypt:"+e);
		//System.out.println("decrypt:"+JUtilRSA.publicDecrypt(e, pair[1], 128, "UTF-8"));
	}
}
