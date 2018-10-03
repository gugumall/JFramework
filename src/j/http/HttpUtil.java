package j.http;

import j.util.JUtilInputStream;
import j.util.JUtilString;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * 
 * @author 肖炯
 *
 */
public class HttpUtil{	
	private static SSLContext sslContext=null;
	
	static{
		try{
			sslContext=SSLContext.getInstance("TLS");
			sslContext.init(null,new TrustManager[]{new MyTrustManager()},null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String getHttp(String url)throws Exception{
		URL u=new URL(url);
	
		InputStream in=u.openStream();
		return JUtilInputStream.string(in);
	}	
	
	/**
	 * 
	 * @param url
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String getHttp(String url,String encoding)throws Exception{
		URL u=new URL(url);
		
		InputStream in=u.openStream();
		return JUtilInputStream.string(in, encoding);
	}
	
	/**
	 * 
	 * @param url
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static String getHttp(String url,int timeout)throws Exception{
		URL u=new URL(url);
		HttpURLConnection connection=(HttpURLConnection)u.openConnection();
		connection.setConnectTimeout(timeout);
		connection.setRequestMethod("GET");
		
		InputStream in=connection.getInputStream();
		String ret=JUtilInputStream.string(in);

		connection.disconnect();
		
		return ret;
	}
	
	/**
	 * 
	 * @param url
	 * @param encoding
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static String getHttp(String url,String encoding,int timeout)throws Exception{
		URL u=new URL(url);
		HttpURLConnection connection=(HttpURLConnection)u.openConnection();
		connection.setConnectTimeout(timeout);
		connection.setRequestMethod("GET");
		
		InputStream in=connection.getInputStream();
		String ret=JUtilInputStream.string(in,encoding);

		connection.disconnect();
		
		return ret;
	}
	
	/**
	 * 
	 * @param url
	 * @param paras
	 * @param encodePara
	 * @return
	 * @throws Exception
	 */
	public static String postHttp(String url,Map paras,boolean encodePara)throws Exception{
		URL u=new URL(url);
		HttpURLConnection connection=(HttpURLConnection)u.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		OutputStream out = connection.getOutputStream();
		java.io.Writer writer = new OutputStreamWriter(out);
		writer.write(generateQueryString(paras,null,encodePara));
		writer.flush();
		writer.close();
		writer=null;
		
		InputStream in=connection.getInputStream();
		String ret=JUtilInputStream.string(in);

		connection.disconnect();
		
		return ret;
	}
	
	/**
	 * 
	 * @param url
	 * @param paras
	 * @param encoding
	 * @param encodePara
	 * @return
	 * @throws Exception
	 */
	public static String postHttp(String url,Map paras,String encoding,boolean encodePara)throws Exception{
		URL u=new URL(url);
		HttpURLConnection connection=(HttpURLConnection)u.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		OutputStream out = connection.getOutputStream();
		java.io.Writer writer = new OutputStreamWriter(out);
		writer.write(generateQueryString(paras,encoding,encodePara));
		writer.flush();
		writer.close();
		writer=null;
		
		InputStream in=connection.getInputStream();
		String ret=JUtilInputStream.string(in, encoding);
		
		connection.disconnect();
		
		return ret;
	}	
	
	/**
	 * 
	 * @param url
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String postData(String url,String data)throws Exception{
		URL u=new URL(url);
		HttpURLConnection connection=(HttpURLConnection)u.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type","multipart/form-data");
		OutputStream out = connection.getOutputStream();
		java.io.Writer writer = new OutputStreamWriter(out);
		writer.write(data);
		writer.flush();
		writer.close();
		writer=null;
		
		InputStream in=connection.getInputStream();
		String ret=JUtilInputStream.string(in);

		connection.disconnect();
		
		return ret;
	}
	
	/**
	 * 
	 * @param url
	 * @param data
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String postData(String url,String data,String encoding)throws Exception{
		URL u=new URL(url);
		HttpURLConnection connection=(HttpURLConnection)u.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type","multipart/form-data");
		OutputStream out = connection.getOutputStream();
		java.io.Writer writer = new OutputStreamWriter(out);
		writer.write(data);
		writer.flush();
		writer.close();
		writer=null;
		
		InputStream in=connection.getInputStream();
		String ret=JUtilInputStream.string(in, encoding);
		
		connection.disconnect();
		
		return ret;
	}	
	
	/**
	 * 
	 * @param paras
	 * @param encoding
	 * @param encodePara
	 * @return
	 * @throws Exception
	 */
	public static String generateQueryString(Map paras,String encoding,boolean encodePara)throws Exception{
		if(encoding==null){
			encoding="utf-8";
		}
		int i=0;
		String body="";
		for(Iterator keys=paras.keySet().iterator();keys.hasNext();i++){
			String key=(String)keys.next();
			String val=(String)paras.get(key);
			if(encodePara){
				if(i==0){
					body+=key+"="+JUtilString.encodeURI(val,encoding);
				}else{
					body+="&"+key+"="+JUtilString.encodeURI(val,encoding);
				}		
			}else{
				if(i==0){
					body+=key+"="+val;
				}else{
					body+="&"+key+"="+val;
				}
			}
		}
		return body;
	}
	
	/**
	 * 
	 * @param fileUploadServer
	 * @param filetoBeUpload
	 */
	public static void uploadFile(String fileUploadServer,File filetoBeUpload){
		String end="\r\n";
		String twoHyphens="--";
		String boundary="******";
		HttpURLConnection httpURLConnection=null;
		try{		
			URL url=new URL(fileUploadServer);
			httpURLConnection=(HttpURLConnection)url.openConnection();
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection","Keep-Alive");
			httpURLConnection.setRequestProperty("Charset","UTF-8");
			httpURLConnection.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);

			DataOutputStream dos=new DataOutputStream(httpURLConnection.getOutputStream());
			dos.writeBytes(twoHyphens+boundary+end);
			dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""+filetoBeUpload.getName()+"\""+end);
			dos.writeBytes(end);

			FileInputStream fis=new FileInputStream(filetoBeUpload);
			byte[] buffer=new byte[8192]; // 8k
			int count=0;
			while((count=fis.read(buffer))!=-1){
				dos.write(buffer,0,count);
			}
			fis.close();

			dos.writeBytes(end);
			dos.writeBytes(twoHyphens+boundary+twoHyphens+end);
			dos.flush();

			InputStream is=httpURLConnection.getInputStream();
			InputStreamReader isr=new InputStreamReader(is,"utf-8");
			BufferedReader br=new BufferedReader(isr);
			
			StringBuffer resultString=new StringBuffer();
			String line=br.readLine();
			while(line!=null){
				resultString.append(line);
				line=br.readLine();
			}
			
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String url="https://www.gugumall.cn/utils/handshake.jhtml";
		String s=HttpUtil.postData(url,"contactscontactscontactscontacts");
		System.out.println(s);
	}
}