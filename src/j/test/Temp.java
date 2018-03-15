package j.test;

import j.http.JHttp;
import j.http.JHttpContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;

/**
 * 
 * @author 肖炯
 *
 */
public class Temp {
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		//String s="{\"method\":\"add\", \"params\": [1,2], \"id\": 1}";
		String s="{\"method\":\"getdifficulty\", \"id\": 1}";

        JHttpContext context=new JHttpContext();
        JHttp http=JHttp.getInstance();
        HttpClient client=http.createClient("112.74.65.173",8332,"http","gugucoinuser","gugucoinpass");
       
       // context.setRequestBody(s);
        s= http.postResponse(context,null,"http://112.74.65.173:8332/",null);
        
		// URL: http://user:pass@localhost:8332
		System.out.println(s); 
	}
}
