package j.cache;

import j.http.JHttp;

import org.apache.http.client.HttpClient;

/**
 * 
 * @author JFramework
 *
 */
public class Servant {
	public JHttp jhttp=null;
	public HttpClient jclient=null;
	public String serviceCode=null;
	public String serviceChannel="rmi";
	public JDCacheService service=null;
	public String httpChannel=null;
	
	/**
	 * 
	 *
	 */
	public Servant(){
		jhttp=JHttp.getInstance();
		jclient=jhttp.createClient();
	}
}
