package j.test;

import j.http.JHttp;
import j.http.JHttpContext;

import org.apache.http.client.HttpClient;

public class Apple {
	public static void main(String[] args) throws Exception{
		JHttp http=JHttp.getInstance();
		HttpClient client=http.createClient();
		
		JHttpContext context=new JHttpContext();
		
		String s1=http.getResponse(context,client,"http://www.apple.com/cn/","UTF-8");
		System.out.println(s1);
		
		context.addRequestHeader("Referer","http://www.apple.com/cn/");
		String s2=http.getResponse(context,client,"https://idmsa.apple.com/IDMSWebAuth/login.html?appIdKey=990d5c9e38720f4e832a8009a0fe4cad7dd151f99111dbea0df5e2934f267ec8&language=CN-zh&segment=R479&grpcode=g001&paramcode=h006&path=%2Fgeniusbar%2FR479%2Fsignin%2Fack&path2=%2Fgeniusbar%2FR479%2Fsignin%2Fack","UTF-8");
		System.out.println(s2);
	}
}
