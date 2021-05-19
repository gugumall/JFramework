package j.http;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpRequestBase;

import j.util.JUtilString;

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
	private Map<String, JHttpCookie> cookies;
	private int retries;
	private long retryInterval;
	private String requestBody=null;
	private boolean clearRequestHeadersOnFinish=true;

	/**
	 * 
	 *
	 */
	public JHttpContext() {
		requestHeaders = new HashMap();
		responseHeaders = new HashMap();
		cookies = new HashMap();
		retries=0;
		retryInterval=0;
		
		requestHeaders.put("Accept-Encoding", "gzip, deflate");
	}
	
	/**
	 * 
	 */
	public JHttpContext clone() {
		JHttpContext c=new JHttpContext();
		c.addResponseHeaders(this.getResponseHeaders());
		c.addCookies(this.getCookies());
		c.setClearRequestHeadersOnFinish(this.clearRequestHeadersOnFinish);
		return c;
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
		this.addRequestHeader("Content-Type", contentType);
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
	 * @param hs
	 */
	public void addRequestHeaders(Map hs) {
		this.requestHeaders.putAll(hs);
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
		this.responseHeaders.put(name.toLowerCase(), value);
	}

	/**
	 * 
	 * @param hs
	 */
	public void addResponseHeaders(Map hs) {
		this.responseHeaders.putAll(hs);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public String getResponseHeader(String name) {
		return (String)this.responseHeaders.get(name.toLowerCase());
	}


	/**
	 * 
	 * @param name
	 * @param value
	 * @param version
	 * @param domain
	 * @param path
	 */
	public void addCookie(String name, String value, int version, String domain, String path) {
		this.cookies.put(name.toLowerCase(), new JHttpCookie(name.toLowerCase(), value, version, domain, path));
	}


	/**
	 * 
	 * @param cs
	 */
	public void addCookies(Map cs) {
		this.cookies.putAll(cs);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public JHttpCookie getCookie(String name) {
		return this.cookies.get(name.toLowerCase());
	}

	/**
	 * 
	 *
	 */
	public void clearCookies() {
		this.cookies.clear();
	}
	
	/**
	 * 
	 * @return
	 */
	public Map getCookies() {
		return this.cookies;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSessionId() {
		JHttpCookie c=getCookie("JSESSIONID");
		return c==null?null:c.getValue();
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
	 * @param clearRequestHeadersOnFinish
	 */
	public void setClearRequestHeadersOnFinish(boolean clearRequestHeadersOnFinish) {
		this.clearRequestHeadersOnFinish=clearRequestHeadersOnFinish;
	}
	
	/**
	 * 
	 */
	public void finish(){
		if(responseStream!=null){
		
		}
		if(request!=null){
			
		}
		
		if(responseText!=null){
			responseText=null;
		}
		
		if(requestBody!=null){
			requestBody=null;
		}
		
		if(this.clearRequestHeadersOnFinish) {
			clearRequestHeader();
		}
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
		clearCookies();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	public void finalize(){
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
		clearCookies();
	}
}
