package j.http;

import j.util.JUtilString;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * 
 * @author 肖炯
 *
 */
public class JHttpContext {
	private String[] allowedErrorCodes=new String[]{"200","302"};
	private int status=0;
	private String responseText;
	private InputStream responseStream;
	private HttpRequestBase request;

	private String contentType;
	private String requestEncoding;
	private Map requestHeaders;
	private Map responseHeaders;
	private int retries;
	private long retryInterval;
	private String requestBody=null;

	/**
	 * 
	 *
	 */
	public JHttpContext() {
		requestHeaders = new HashMap();
		responseHeaders = new HashMap();
		retries=0;
		retryInterval=0;
		
		requestHeaders.put("Accept-Encoding", "gzip, deflate");
	}
	
	/**
	 * 
	 * @param retries
	 */
	public void setRetries(int retries){
		this.retries=retries;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getRetries(){
		return this.retries;
	}
	
	/**
	 * 
	 * @param retryInterval
	 */
	public void setRetryInterval(int retryInterval){
		this.retryInterval=retryInterval;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getRetryInterval(){
		return this.retryInterval;
	}
	
	/**
	 * 
	 * @param allowedErrorCodes
	 */
	public void setAllowedErrorCodes(String[] allowedErrorCodes){
		this.allowedErrorCodes=allowedErrorCodes;
	}
	public String[] getAllowedErrorCodes(){
		return allowedErrorCodes;
	}
	public boolean isErrorCodeAllowed(int status){
		if(allowedErrorCodes!=null
				&&allowedErrorCodes.length>0
				&&allowedErrorCodes[0].equalsIgnoreCase("ALL")){
			return true;
		}
		
		if(allowedErrorCodes==null){
			return status==200||status==302;
		}else{
			return JUtilString.contain(allowedErrorCodes, status+"");
		}
	}
	
	
	/**
	 * 
	 * @param status
	 */
	public void setStatus(int status){
		this.status=status;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getStatus(){
		return this.status;
	}
	
	/**
	 * 
	 * @param responseText
	 */
	public void setResponseText(String responseText){
		this.responseText=responseText;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getResponseText(){
		return this.responseText;
	}
	
	/**
	 * 
	 * @param responseStream
	 */
	public void setResponseStream(InputStream responseStream){
		this.responseStream=responseStream;
	}
	
	/**
	 * 
	 * @return
	 */
	public InputStream getResponseStream(){
		return this.responseStream;
	}
	
	/**
	 * 
	 * @param request
	 */
	public void setRequest(HttpRequestBase request){
		this.request=request;
	}

	/**
	 * 
	 * @param contentType
	 */
	public void setContentType(String contentType){
		this.contentType=contentType;
	}
	

	/**
	 * 
	 * @return
	 */
	public String getContentType(){
		return  this.contentType;
	}

	/**
	 * 
	 * @param requestEncoding
	 */
	public void setRequestEncoding(String requestEncoding){
		this.requestEncoding=requestEncoding;
	}
	

	/**
	 * 
	 * @return
	 */
	public String getRequestEncoding(){
		return  this.requestEncoding;
	}

	/**
	 * 
	 * @param name
	 * @param value
	 */
	public void addRequestHeader(String name, String value) {
		this.requestHeaders.put(name, value);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public String getRequestHeader(String name) {
		return (String)this.requestHeaders.get(name);
	}

	/**
	 * 
	 * @return
	 */
	public Map getRequestHeaders() {
		return this.requestHeaders;
	}

	/**
	 * 
	 *
	 */
	public void clearRequestHeader() {
		this.requestHeaders.clear();
		this.requestHeaders.put("Accept-Encoding", "gzip, deflate");
	}


	/**
	 * 
	 * @param name
	 * @param value
	 */
	public void addResponseHeader(String name, String value) {
		this.responseHeaders.put(name, value);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public String getResponseHeader(String name) {
		return (String)this.responseHeaders.get(name);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSessionId() {
		//Set-Cookie:JSESSIONID=780D255A35F4BB4E4AC3BA333307F27D; Path=/; Secure; HttpOnly
		String setCookie=this.getResponseHeader("Set-Cookie");
		if(setCookie.indexOf("JSESSIONID=")>-1) {
			setCookie=setCookie.substring(11);
			if(setCookie.indexOf(";")>0) {
				setCookie=setCookie.substring(0,setCookie.indexOf(";"));
			}
			return setCookie;
		}else {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public Map getResponseHeaders() {
		return this.responseHeaders;
	}

	/**
	 * 
	 *
	 */
	public void clearResponseHeader() {
		this.responseHeaders.clear();
	}
	
	/**
	 * 
	 * @param requestBody
	 */
	public void setRequestBody(String requestBody){
		this.requestBody=requestBody;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRequestBody(){
		return this.requestBody;
	}
	
	
	/**
	 * 
	 *
	 */
	public void reset(){
		if(responseStream!=null){
			try{
				responseStream.close();
			}catch(Exception e){}
			responseStream=null;
		}
		if(request!=null){
			try{
				request.releaseConnection();
				request.abort();
			}catch(Exception e){}
			request=null;
		}
		if(responseText!=null){
			responseText=null;
		}
		clearRequestHeader();
		clearResponseHeader();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	public void finalize(){
		if(responseStream!=null){
//			try{
//				responseStream.close();
//			}catch(Exception e){}
//			responseStream=null;
		}
		if(responseText!=null){
			responseText=null;
		}
		requestHeaders.clear();
	}
}
