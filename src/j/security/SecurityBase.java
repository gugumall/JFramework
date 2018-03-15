package j.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SecurityBase{
	/**
	 * 
	 * @param string
	 * @return
	 */
	public static String encryptSHA1(String string){
		try{
			//指定sha1算法  
	        MessageDigest crypt = MessageDigest.getInstance("SHA-1");  
	        crypt.reset();
	        crypt.update(string.getBytes());
	        
	        //获取字节数组  
	        byte messageDigest[] = crypt.digest();  
	        
	        //Create Hex String  
	        StringBuffer hexString = new StringBuffer();  
	        
	        //字节数组转换为 十六进制 数  
	        for(int i=0;i<messageDigest.length;i++){  
	            String shaHex=Integer.toHexString(messageDigest[i]&0xFF);  
	            if(shaHex.length()<2) {  
	                hexString.append(0);  
	            }  
	            hexString.append(shaHex);  
	        }  
	        return hexString.toString().toLowerCase(); 
		}catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return "";
        }
	}
	

	/**
	 * 
	 * @param string
	 * @param sorted
	 * @return
	 */
	public static String encryptSHA1(String[] strings,boolean sorted){
		if(strings==null||strings.length==0) return "";
		if(sorted) Arrays.sort(strings);
		
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < strings.length; i++) {
			sb.append(strings[i]);
		}
		String str = sb.toString();
		
		return encryptSHA1(str);
	}
}
