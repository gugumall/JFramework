package j.http;

import java.io.IOException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

/**
 * 
 * @author 肖炯
 *
 */
public class MyHttpRequestRetryHandler implements HttpRequestRetryHandler{
	private int retries=2;
	
	/**
	 * 
	 *
	 */
	public MyHttpRequestRetryHandler(){
		super();
	}
	
	/**
	 * 
	 * @param retries
	 */
	public MyHttpRequestRetryHandler(int retries){
		this.retries=retries;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.apache.http.client.HttpRequestRetryHandler#retryRequest(java.io.IOException, int, org.apache.http.protocol.HttpContext)
	 */
	public boolean retryRequest(IOException exception, int executionCount,HttpContext context) {
		if (executionCount >= retries) {
			// Do not retry if over max retry count
			return false;
		}
		if (exception instanceof NoHttpResponseException) {
			//Do not retry if the server dropped connection on us
			//return false;
		}
		if (exception instanceof SSLHandshakeException) {
			// Do not retry on SSL handshake exception
			return false;
		}
		HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
		boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
		if (idempotent) {
			// Retry if the request is considered idempotent
			return false;
		}
		return false;
	}

}
