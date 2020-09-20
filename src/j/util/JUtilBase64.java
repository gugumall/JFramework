package j.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import j.http.JHttp;
import j.tool.QRCoder.JQRCode;

public final class JUtilBase64{
	/**
	 * 
	 * @param binaryData
	 * @return
	 */
	public static String encode(byte[] binaryData){
		return encode(binaryData, true);
	}

	/**
	 * 
	 * @param encoded
	 * @return
	 */
	public static byte[] decode(String encoded){
		return decode(encoded, true);
	}
	
	/**
	 * 
	 * @param binaryData
	 * @param useMimeEncoder
	 * @return
	 */
	public static String encode(byte[] binaryData, boolean useMimeEncoder){
		try {
			if(useMimeEncoder) return Base64.getMimeEncoder().encodeToString(binaryData);
			else return Base64.getEncoder().encodeToString(binaryData);
		}catch(Exception e) {
			return new String(binaryData);
		}
	}

	/**
	 * 
	 * @param encoded
	 * @param useMimeDecoder
	 * @return
	 */
	public static byte[] decode(String encoded, boolean useMimeDecoder){
		try {
			if(useMimeDecoder) return Base64.getMimeDecoder().decode(encoded);
			else return Base64.getDecoder().decode(encoded);
		}catch(Exception e) {
			return encoded.getBytes();
		}
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public static String image2Base64(File file) {
		 try {
             InputStream is = new FileInputStream(file);
             
             //将内容读取内存中
             ByteArrayOutputStream data = new ByteArrayOutputStream();
             int len = -1;
             byte[] buffer= new byte[1024];
             while ((len = is.read(buffer)) != -1) {
                 data.write(buffer, 0, len);
             }
    
             //对字节数组Base64编码
             String encode=Base64.getMimeEncoder().encodeToString(data.toByteArray());
         
             // 关闭流
             is.close();
             
             return encode;
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String image2Base64(String  url) {
		 try {
			 JHttp http=JHttp.getInstance();
             InputStream is = http.getStreamResponse(null, null, url);
             
             //将内容读取内存中
             ByteArrayOutputStream data = new ByteArrayOutputStream();
             int len = -1;
             byte[] buffer= new byte[1024];
             while ((len = is.read(buffer)) != -1) {
                 data.write(buffer, 0, len);
             }
    
             //对字节数组Base64编码
             String encode=Base64.getMimeEncoder().encodeToString(data.toByteArray());
         
             // 关闭流
             is.close();
             
             return encode;
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
	}
	
	/**
	 * 
	 * @param image
	 * @param imageType
	 * @return
	 */
	public static String image2Base64(BufferedImage image, String imageType) {
		 try {
			 InputStream is= JUtilImage.image2InputStream(image, imageType);
			
             //将内容读取内存中
             ByteArrayOutputStream data = new ByteArrayOutputStream();
             int len = -1;
             byte[] buffer= new byte[1024];
             while ((len = is.read(buffer)) != -1) {
                 data.write(buffer, 0, len);
             }
    
             //对字节数组Base64编码
             String encode=Base64.getMimeEncoder().encodeToString(data.toByteArray());
         
             // 关闭流
             is.close();
             
             return encode;
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println(JUtilString.encodeURI("\"\n\t+'/{}[]", "UTF-8"));
	}
}
