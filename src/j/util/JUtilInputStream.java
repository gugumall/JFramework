package j.util;

import it.sauronsoftware.base64.Base64;
import j.common.JObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author 肖炯
 *
 */
public class JUtilInputStream {
	
	/**
	 * 将输入流读入字节数组变量(bytes)，读入完成后会关闭输入流并将inputstream对象设为null
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static byte[] bytes(InputStream in) throws IOException {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		
		byte[] buffer=new byte[1024];
		int readed=in.read(buffer);
		while(readed>-1){
			bos.write(buffer,0,readed);
			readed=in.read(buffer);
		}
		bos.flush();
		byte[] bytes=bos.toByteArray();		

		try{
			in.close();
			in=null;
		}catch(Exception e){}
		
		try{
			bos.close();
			bos=null;
		}catch(Exception e){}
	
		return bytes;
	}
	
	/**
	 * 将输入流解析成字符串，读入完成后会关闭输入流并将inputstream对象设为null
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String string(InputStream in)throws IOException{
//		BufferedReader reader=new BufferedReader(new InputStreamReader(in));
//    	String txt="";
//    	String line=reader.readLine();
//    	while(line!=null){	        		
//    		txt+=line+Global.lineSeparator;
//    		line=reader.readLine();
//    	}
//    	
//		try{
//			reader.close();
//		}catch(Exception e){}
//		
//		try{
//			in.close();
//		}catch(Exception e){}  
//		
//		if(txt.endsWith(Global.lineSeparator)) txt=txt.substring(0,txt.length()-Global.lineSeparator.length());
//
//    	return JUtilString.reviseString(txt,"ISO-8859-1");
		
		return new String(bytes(in));
	}
	
	/**
	 * 将输入流解析成指定编码的字符串，读入完成后会关闭输入流并将inputstream对象设为null
	 * @param in
	 * @param encoding 字符串编码
	 * @return
	 * @throws IOException
	 */
	public static String string(InputStream in,String encoding)throws IOException{
//		BufferedReader reader=new BufferedReader(new InputStreamReader(in,encoding));
//    	String txt="";
//    	String line=reader.readLine();
//    	while(line!=null){	        		
//    		txt+=line+Global.lineSeparator;
//    		line=reader.readLine();
//    	}
//    	
//		try{
//			reader.close();
//		}catch(Exception e){}
//		
//		try{
//			in.close();
//		}catch(Exception e){} 
//		
//		if(txt.endsWith(Global.lineSeparator)) txt=txt.substring(0,txt.length()-Global.lineSeparator.length());
//
//		return JUtilString.reviseString(txt,encoding);
		
		return new String(bytes(in),encoding);
	}	
	
	public static void main(String[] args)throws Exception{
		String s=Base64.decode("anNlcmlhbGl6YWJsZTo/qKoABXVyABNbTGphdmEubGFuZy5PYmplY3Q7Pz9YPxBzKWwCAAB4cAAAAAF0AChEOlx0b21jYXRcd2ViYXBwc1xiYWlsaWFuXFJPT1RcaVwxXDIuanBn");
		System.out.println(JObject.string2Serializable(new String(s.getBytes("UTF-8"),"ISO-8859-1")));
	}
}