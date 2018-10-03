package j.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 
 * @author one
 *
 */
public class MyHostnameVerifier implements HostnameVerifier {  
	  
    @Override  
    public boolean verify(String hostname, SSLSession session) {  
        return true;  
    }  
}  