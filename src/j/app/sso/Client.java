package j.app.sso;

import j.util.ConcurrentMap;
import j.util.JUtilString;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author JFramework
 *
 */
public class Client implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private boolean isSsoServer=false;
	private boolean canLogin=true;
	private boolean compatible=true;
	private String id;
	private String name;
	private String urlDefault;
	private List urls=new LinkedList();
	private List domains=new LinkedList();
	private String loginPage;
	private String homePage;
	private String passport;
	private String loginInterface;
	private String logoutInterface;
	private LoginAgent loginAgent;
	private String userClass;
	private ConcurrentMap properties;//自定义参数
	
	/**
	 * 
	 *
	 */
	protected Client(){
		properties=new ConcurrentMap();
	}
	
	
	//isSsoServer
	synchronized public boolean isSsoServer(){
		return this.isSsoServer;
	}
	synchronized public void setIsSsoServer(boolean isSsoServer){
		this.isSsoServer=isSsoServer;
	}
	
	//canLogin
	synchronized public boolean canLogin(){
		return this.canLogin;
	}
	synchronized public void setCanLogin(boolean canLogin){
		this.canLogin=canLogin;
	}
	
	//compatible
	synchronized public boolean compatible(){
		return this.compatible;
	}
	synchronized public void setCompatible(boolean compatible){
		this.compatible=compatible;
	}
	
	//id
	synchronized public String getId(){
		return this.id;
	}
	synchronized public void setId(String id){
		this.id=id;
	}
	
	//name
	synchronized public String getName(){
		return this.name;
	}
	synchronized public void setName(String name){
		this.name=name;
	}
	
	//about default url
	synchronized public String getUrlDefault(){
		return this.urlDefault;
	}
	synchronized public void setUrlDefault(String urlDefault){
		this.urlDefault=urlDefault;
	}
	synchronized public boolean isUrlDefault(String url){
		return this.urlDefault.equalsIgnoreCase(url);
	}
	synchronized public String getUrlDefault(HttpServletRequest request){
		if(request.getScheme().toLowerCase().indexOf("https")>-1){
			if(this.urlDefault.startsWith("https:")) return this.urlDefault;
			else return "https"+this.urlDefault.substring(4);
		}else{
			if(this.urlDefault.startsWith("http:")) return this.urlDefault;
			else return "http"+this.urlDefault.substring(5);
		}
	}

	//about url
	synchronized public List getUrls(){
		return this.urls;
	}	
	synchronized public boolean isMine(String requestURL){
		if(requestURL==null) return false;
		
		for(int i=0;i<urls.size();i++){
			String url=(String)urls.get(i);
			if(requestURL.startsWith(url)
					||requestURL.startsWith(url.replaceAll("https","http"))
					||requestURL.startsWith(url.replaceAll("http","https"))) return true;
		}
		return false;
	}
	synchronized public boolean isMineWildcard(String requestURL){
		if(requestURL==null) return false;
		
		for(int i=0;i<urls.size();i++){
			String url=(String)urls.get(i);
			if(JUtilString.matchIgnoreCase(requestURL, url, "*")==0
					||JUtilString.matchIgnoreCase(requestURL, url.replaceAll("https","http"), "*")==0
					||JUtilString.matchIgnoreCase(requestURL, url.replaceAll("http","https"), "*")==0) return true;
		}
		return false;
	}
	synchronized public String getUrlPrefix(String requestURL){
		if(requestURL==null) return null;
		
		for(int i=0;i<urls.size();i++){
			String url=(String)urls.get(i);
			if(requestURL.startsWith(url)) return url;
		}
		
		for(int i=0;i<urls.size();i++){
			String url=(String)urls.get(i);
			if(JUtilString.matchIgnoreCase(requestURL, url, "*")==0){
				requestURL=requestURL.substring(0,requestURL.indexOf("/",8)+1);
			}
		}
		
		return null;
	}
	synchronized public boolean contains(String url){
		return this.urls.contains(url);
	}
	synchronized public void addUrl(String url){
		if(!this.urls.contains(url)) this.urls.add(url);
	}
	synchronized public void delUrl(String url){
		this.urls.remove(url);
	}	
	synchronized public void clearUrls(){
		urls.clear();
	}
	
	//all main domain
	synchronized public List getDomains(){
		return this.domains;
	}	
	synchronized public void addDomain(String domain){
		if(!this.domains.contains(domain)) this.domains.add(domain);
	}
	
	//loginPage
	synchronized public String getLoginPage(){
		return this.loginPage;
	}
	synchronized public void setLoginPage(String loginPage){
		this.loginPage=loginPage;
	}
	
	//homePage
	synchronized public String getHomePage(){
		return this.homePage;
	}
	synchronized public void setHomePage(String homePage){
		this.homePage=homePage;
	}
	
	//passport
	synchronized public String getPassport(){
		return this.passport;
	}
	synchronized public void setPassport(String passport){
		this.passport=passport;
	}
	
	//loginInterface
	synchronized public String getLoginInterface(){
		return this.loginInterface;
	}
	synchronized public void setLoginInterface(String loginInterface){
		this.loginInterface=loginInterface;
	}
	
	//logoutInterface
	synchronized public String getLogoutInterface(){
		return this.logoutInterface;
	}
	synchronized public void setLogoutInterface(String logoutInterface){
		this.logoutInterface=logoutInterface;
	}
	
	//LoginAgent
	synchronized public LoginAgent getLoginAgent(){
		return this.loginAgent;
	}
	synchronized public void setLoginAgent(LoginAgent loginAgent){
		this.loginAgent=loginAgent;
	}
	
	//userClass
	synchronized public String getUserClass(){
		return this.userClass;
	}
	synchronized public void setUserClass(String userClass){
		this.userClass=userClass;
	}
	
	//properties
	synchronized public ConcurrentMap getProperties(){
		return this.properties;
	}	
	synchronized public String getProperty(String key){
		return (String)this.properties.get(key);
	}
	synchronized public void setProperty(String key,String value){
		this.properties.put(key,value);
	}
	
	//agent login
	public boolean available(String fromClientId){
		return this.loginAgent.available(fromClientId);
	}
	
	public LoginResult login(String fromClientId,HttpServletRequest request){
		return this.loginAgent.login(fromClientId,request);
	}
	
	/**
	 * 
	 * @param 测试
	 */
	public static void main(String[] args){
		System.out.println(JUtilString.matchIgnoreCase("http://www.baidu.com/xx.htm", "http://*.baidu.com/", "*"));
	}
}