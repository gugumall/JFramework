package j.http;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author ceo
 *
 */
public class HttpResponseAndHeaders {
	public String responseContent;
	public Map<String,List<String>> responseHeaders;
	
	/**
	 * 
	 * @param responseContent
	 * @param responseHeaders
	 */
	public HttpResponseAndHeaders(String responseContent,Map<String,List<String>> responseHeaders) {
		this.responseContent=responseContent;
		this.responseHeaders=responseHeaders;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSessionId() {
		//Set-Cookie:JSESSIONID=780D255A35F4BB4E4AC3BA333307F27D; Path=/; Secure; HttpOnly
		
		if(this.responseHeaders==null) return null;
		List<String> setCookie=this.responseHeaders.get("Set-Cookie");
		if(setCookie==null||setCookie.isEmpty()) return null;
		
		String _setCookie=setCookie.get(0);
		if(_setCookie.indexOf("JSESSIONID=")>-1) {
			_setCookie=_setCookie.substring(11);
			if(_setCookie.indexOf(";")>0) {
				_setCookie=_setCookie.substring(0,_setCookie.indexOf(";"));
			}
			return _setCookie;
		}else {
			return null;
		}
	}
}
