package j.app.sso;

import j.Properties;
import j.app.Constants;
import j.app.permission.Permission;
import j.app.webserver.Handlers;
import j.common.JObject;
import j.http.JHttp;
import j.http.JHttpContext;
import j.log.Logger;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilMD5;
import j.util.JUtilString;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.HttpClient;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 
 * @author JFramework
 *
 */
public class SSOConfig implements Runnable{
	private static Logger log=Logger.create(SSOConfig.class);
	private static boolean isServer=false;
	private static String ssoServer;//单点登录服务器地址
	private static Authenticator authenticator;//认证类
	private static boolean verifierCodeEnabled=true;//是否启用登录验证码
	private static int sessionTimeout;//登录用户过期时间，即过多久没有与系统交互则视为会话超时，以秒为单位
	private static int onlineActiveTime;//多久没有活动表示用户离线，以秒为单位
	private static int notifiersPerClient=1;//对每个sso client，sso server启用多少个通知线程	
	private static String logoutOtherSessions="domain";//登录时是否注销同一用户的其它session,all 表示全部注销，domain 表示同一域名，none表示不注销
	private static ConcurrentList ssoClients=new ConcurrentList();//单点登录客户
	private static ConcurrentList cosites=new ConcurrentList();//合作登录站点
	private static ConcurrentMap ssoClientsKeyedById=new ConcurrentMap();//单点登录客户
	private static ConcurrentMap cositesKeyedById=new ConcurrentMap();//合作登录站点
	private static long configLastModified=0;//配置文件上次修改时间
	private static boolean loading=false;
	private static JHttp http=null;
	private static HttpClient hclient=null;
	
	static{
		try{
			load();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_FATAL);
		}
		
		SSOConfig m=new SSOConfig();
		Thread thread=new Thread(m);
		thread.start();
		log.log("SSOConfig monitor thread started.",-1);
		

		http=JHttp.getInstance();
		hclient=http.createClient();
	}

	/**
	 * 
	 *
	 */
	public SSOConfig() {
		super();
	}
	
	//getters
	public static boolean isServer(){
		waitWhileLoading();
		return isServer;
	}
	
	public static Authenticator getAuthenticator(){
		waitWhileLoading();
		return authenticator;
	}
	
	public static boolean getVerifierCodeEnabled(){
		waitWhileLoading();
		return verifierCodeEnabled;
	}
	
	public static int getSessionTimeout(){
		waitWhileLoading();
		return sessionTimeout;
	}
	
	public static int getOnlineActiveTime(){
		waitWhileLoading();
		return onlineActiveTime;
	}
	
	public static int getNotifiersPerClient(){
		waitWhileLoading();
		return notifiersPerClient;
	}
	
	public static String getLogoutOtherSessions(){
		waitWhileLoading();
		return logoutOtherSessions;
	}
	
	public static ConcurrentList getSsoClients(){
		waitWhileLoading();
		return ssoClients;
	}
	
	public static Client getSsoClientById(String id){
		waitWhileLoading();
		if(id==null||"".equals(id)) return null;
		
		return (Client)ssoClientsKeyedById.get(id);
	}
	
	public static Client getSsoClientByIdOrUrl(String idOrUrl){
		waitWhileLoading();
		if(idOrUrl==null||"".equals(idOrUrl)) return null;
		
		Client client = (Client)ssoClientsKeyedById.get(idOrUrl);
		if(client==null){
			for(int i=0;i<ssoClients.size();i++){
				Client thisOne = (Client)ssoClients.get(i);
				if(thisOne.isMine(idOrUrl)) return thisOne;
			}
		}
		
		if(client==null){
			for(int i=0;i<ssoClients.size();i++){
				Client thisOne = (Client)ssoClients.get(i);
				if(thisOne.isMineWildcard(idOrUrl)) return thisOne;
			}
		}
		
		return client;
	}
	
	public static String getDefaultUrl(String idOrUrl){
		waitWhileLoading();
		Client client=getSsoClientByIdOrUrl(idOrUrl);
		return client==null?null:client.getUrlDefault();
	}
	
	public static String getAbsoluteUrl(Client client,String clientUrlPrefix,String url){
		waitWhileLoading();
		if(url.startsWith("http")) return url;
		else if(url.startsWith("/")) return client.getUrlPrefix(clientUrlPrefix)+url.substring(1);
		else return client.getUrlPrefix(clientUrlPrefix)+url; 
	}
	
	public static String getAbsoluteUrlSameDomainOfFromUrl(String fromUrl,String url){
		waitWhileLoading();
		if(url.startsWith("http")) return url;
		else if(url.startsWith("/")) return SysUtil.getRequestURLBase(fromUrl)+url;
		else return SysUtil.getRequestURLBase(fromUrl)+"/"+url; 
	}
	
	public static String getMainDomain(String idOrUrl){
		waitWhileLoading();
		
		if(idOrUrl==null||"".equals(idOrUrl)) return "";
		
		Client client = getSsoClientById(idOrUrl);
		if(client!=null){
			idOrUrl=client.getUrlDefault();
		}else{
			client=getSsoClientByIdOrUrl(idOrUrl);
		}
		
		if(client==null){
			return JUtilString.getMainDomain(idOrUrl);
		}
		
		List domains=client.getDomains();
		for(int i=0;i<domains.size();i++){
			String domain = (String)domains.get(i);
			if(idOrUrl.endsWith(domain+"/")) return domain;
		}
		
		return JUtilString.getMainDomain(idOrUrl);
	}
	
	public static ConcurrentList getCosites(){
		waitWhileLoading();
		return cosites;
	}

	public static String getLoginToken(Client client,HttpServletRequest request){
		String userAgentIp=JHttp.getRemoteIp(request);
		String _token=JUtilMD5.MD5EncodeToHex(userAgentIp+"."+client.getPassport());
		return _token;
	}
	//////////

	
	public static String getSsoServer(){
		waitWhileLoading();
		return ssoServer;
	}
	
	public static String getSsoServer(Client c,String currentUrl){
		waitWhileLoading();
		if(c.isSsoServer()){
			String defaultHost=SysUtil.getRequestURLBase(ssoServer);
			return ssoServer.replaceAll(defaultHost,SysUtil.getRequestURLBase(currentUrl));
		}else{
			return ssoServer;
		}
	}
	
	/**
	 * 
	 * @param clientId
	 * @param url
	 * @throws Exception
	 */
	public static void tellServerToAddUrl(String clientId,String url)throws Exception{
		if(clientId==null&&url==null) return;

		try{
			String addUrl=SSOConfig.getSsoServer()+"ssoserver"+Handlers.getActionPathPattern();
			addUrl+="?"+Handlers.getHandler("/ssoserver").getRequestBy()+"=ssoaddurl";		
			addUrl+="&"+Constants.SSO_CLIENT+"="+clientId;	
			addUrl+="&url="+JUtilString.encodeURI(url,SysConfig.sysEncoding);	
			addUrl+="&"+Constants.SSO_PASSPORT+"="+Permission.getSSOPassport();
			
			//log.log("tellServerToAddUrl on url - "+addUrl,-1);
			
			int loop=0;
			String _response="";
			while(!Constants.RESPONSE_OK.equals(_response)
					&&loop<3){
				JHttpContext context=http.get(null,hclient,addUrl);
				_response=context.getStatus()==200?context.getResponseText():null;
				context.finalize();
				context=null;
				
				loop++;
				
				if(!Constants.RESPONSE_OK.equals(_response)){
					try{
						Thread.sleep(3000);
					}catch(Exception e){}
				}
			}
			log.log("tellServerToAddUrl - "+_response,Logger.LEVEL_DEBUG);
		}catch(Exception ex){
			log.log(ex,Logger.LEVEL_ERROR);
		}
	}
	
	/**
	 * 
	 * @param clientId
	 * @param url
	 * @throws Exception
	 */
	public static void tellServerToDelUrl(String clientId,String url)throws Exception{
		if(clientId==null&&url==null) return;

		try{
			String delUrl=SSOConfig.getSsoServer()+"ssoserver"+Handlers.getActionPathPattern();
			delUrl+="?"+Handlers.getHandler("/ssoserver").getRequestBy()+"=ssodelurl";		
			delUrl+="&"+Constants.SSO_CLIENT+"="+clientId;	
			delUrl+="&url="+JUtilString.encodeURI(url,SysConfig.sysEncoding);	
			delUrl+="&"+Constants.SSO_PASSPORT+"="+Permission.getSSOPassport();
			
			log.log("tellServerToDelUrl on url - "+delUrl,Logger.LEVEL_DEBUG);
			
			int loop=0;
			String _response="";
			while(!Constants.RESPONSE_OK.equals(_response)
					&&loop<3){
				JHttpContext context=http.get(null,hclient,delUrl);
				_response=context.getStatus()==200?context.getResponseText():null;
				context.finalize();
				context=null;
				
				loop++;
				
				if(!Constants.RESPONSE_OK.equals(_response)){
					try{
						Thread.sleep(3000);
					}catch(Exception e){}
				}
			}
			log.log("tellServerToDelUrl - "+_response,Logger.LEVEL_DEBUG);
		}catch(Exception ex){
			log.log(ex,Logger.LEVEL_ERROR);
		}
	}
	
	/**
	 * 
	 * @param clientId
	 * @param ssoUserId
	 * @param ssoBackUrl
	 * @param ssoLoginPage
	 * @param infos
	 * @return
	 * @throws Exception
	 */
	public static String tellServerToLogin(String clientIdOrUrl,String ssoUserId,String ssoBackUrl,String ssoLoginPage,ConcurrentMap infos)throws Exception{
		try{
			String loginUrl=SSOConfig.getSsoServer()+"ssoserver"+Handlers.getActionPathPattern();
			loginUrl+="?"+Handlers.getHandler("/ssoserver").getRequestBy()+"=ssologinauto";	
			
			Map paras=new HashMap();
			paras.put(Constants.SSO_CLIENT, clientIdOrUrl);
			paras.put(Constants.SSO_USER_ID, ssoUserId);
			paras.put(Constants.SSO_LOGIN_INFO, JObject.serializable2String(infos,false));
			paras.put(Constants.SSO_PASSPORT, Permission.getSSOPassport());
			
			int loop=0;
			String _response="";
			while((_response==null||!_response.startsWith(Constants.RESPONSE_OK+":"))
					&&loop<3){
				JHttpContext context=new JHttpContext();
				context.setAllowedErrorCodes(new String[]{"301"});
				context=http.post(context,hclient,loginUrl,paras);
				_response=context.getStatus()==200?context.getResponseText():null;
				context.finalize();
				context=null;
				
				loop++;
				
				if(_response==null||!_response.startsWith(Constants.RESPONSE_OK+":")){
					try{
						Thread.sleep(3000);
					}catch(Exception e){}
				}
			}
			if(_response==null||!_response.startsWith(Constants.RESPONSE_OK+":")){
				return null;
			}else{
				String globalSessionId=_response.substring(Constants.RESPONSE_OK.length()+1,_response.lastIndexOf(":"));
				String token=_response.substring(_response.lastIndexOf(":")+1);
				
				if(clientIdOrUrl.matches(JUtilString.RegExpHttpUrl)){
					loginUrl=SSOConfig.getSsoServer(SSOConfig.getSsoClientByIdOrUrl(clientIdOrUrl),clientIdOrUrl)+"ssoserver"+Handlers.getActionPathPattern();
				}else if(ssoBackUrl.matches(JUtilString.RegExpHttpUrl)){
					loginUrl=SSOConfig.getSsoServer(SSOConfig.getSsoClientByIdOrUrl(clientIdOrUrl),ssoBackUrl)+"ssoserver"+Handlers.getActionPathPattern();
				}else{
					loginUrl=SSOConfig.getSsoServer()+"ssoserver"+Handlers.getActionPathPattern();
				}
				loginUrl+="?"+Handlers.getHandler("/ssoserver").getRequestBy()+"=jump";		
				loginUrl+="&"+Constants.SSO_CLIENT+"="+clientIdOrUrl;	
				if(ssoBackUrl!=null){
					loginUrl+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(ssoBackUrl,SysConfig.sysEncoding);
				}
				if(ssoLoginPage!=null){
					loginUrl+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(ssoLoginPage,SysConfig.sysEncoding);
				}
				loginUrl+="&"+Constants.SSO_GLOBAL_SESSION_ID+"="+globalSessionId;
				loginUrl+="&"+Constants.SSO_TOKEN+"="+token;
				log.log("sso jump url - "+loginUrl,Logger.LEVEL_DEBUG);
				
				return loginUrl;
			}
		}catch(Exception ex){
			log.log(ex,Logger.LEVEL_ERROR);
			return null;
		}
	}
	
	/**
	 * 
	 * @param clientId
	 * @param ssoUserId
	 * @param ssoBackUrl
	 * @param ssoLoginPage
	 * @param infos
	 * @return
	 * @throws Exception
	 */
	public static String tellServerToLoginSameProtocalAsBackUrl(String clientIdOrUrl,String ssoUserId,String ssoBackUrl,String ssoLoginPage,ConcurrentMap infos)throws Exception{
		try{
			String loginUrl=SSOConfig.getSsoServer()+"ssoserver"+Handlers.getActionPathPattern();
			loginUrl+="?"+Handlers.getHandler("/ssoserver").getRequestBy()+"=ssologinauto";	
			
			if(JUtilString.getProtocal(ssoBackUrl).equals("http")){
				loginUrl=JUtilString.replaceAll(loginUrl,"https://","http://");
			}
			
			Map paras=new HashMap();
			paras.put(Constants.SSO_CLIENT, clientIdOrUrl);
			paras.put(Constants.SSO_USER_ID, ssoUserId);
			paras.put(Constants.SSO_LOGIN_INFO, JObject.serializable2String(infos,false));
			paras.put(Constants.SSO_PASSPORT, Permission.getSSOPassport());
			
			int loop=0;
			String _response="";
			while((_response==null||!_response.startsWith(Constants.RESPONSE_OK+":"))
					&&loop<3){
				JHttpContext context=new JHttpContext();
				context.setAllowedErrorCodes(new String[]{"301"});
				context=http.post(context,hclient,loginUrl,paras);
				_response=context.getStatus()==200?context.getResponseText():null;
				context.finalize();
				context=null;
				
				loop++;
				
				if(_response==null||!_response.startsWith(Constants.RESPONSE_OK+":")){
					try{
						Thread.sleep(3000);
					}catch(Exception e){}
				}
			}
			log.log("tellServerToLogin - "+_response,Logger.LEVEL_DEBUG);
			if(_response==null||!_response.startsWith(Constants.RESPONSE_OK+":")){
				return null;
			}else{
				String globalSessionId=_response.substring(Constants.RESPONSE_OK.length()+1,_response.lastIndexOf(":"));
				String token=_response.substring(_response.lastIndexOf(":")+1);
				
				if(clientIdOrUrl.matches(JUtilString.RegExpHttpUrl)){
					loginUrl=SSOConfig.getSsoServer(SSOConfig.getSsoClientByIdOrUrl(clientIdOrUrl),clientIdOrUrl)+"ssoserver"+Handlers.getActionPathPattern();
				}else if(ssoBackUrl.matches(JUtilString.RegExpHttpUrl)){
					loginUrl=SSOConfig.getSsoServer(SSOConfig.getSsoClientByIdOrUrl(clientIdOrUrl),ssoBackUrl)+"ssoserver"+Handlers.getActionPathPattern();
				}else{
					loginUrl=SSOConfig.getSsoServer()+"ssoserver"+Handlers.getActionPathPattern();
				}
				loginUrl+="?"+Handlers.getHandler("/ssoserver").getRequestBy()+"=jump";		
				loginUrl+="&"+Constants.SSO_CLIENT+"="+clientIdOrUrl;	
				if(ssoBackUrl!=null){
					loginUrl+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(ssoBackUrl,SysConfig.sysEncoding);
				}
				if(ssoLoginPage!=null){
					loginUrl+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(ssoLoginPage,SysConfig.sysEncoding);
				}
				loginUrl+="&"+Constants.SSO_GLOBAL_SESSION_ID+"="+globalSessionId;
				loginUrl+="&"+Constants.SSO_TOKEN+"="+token;
				log.log("sso jump url - "+loginUrl,Logger.LEVEL_DEBUG);
				
				return loginUrl;
			}
		}catch(Exception ex){
			log.log(ex,Logger.LEVEL_ERROR);
			return null;
		}
	}


	/**
	 * 
	 * 
	 */
	private static void load() {
		try{
			loading=true;
			
			ssoClients.clear();
			cosites.clear();
			ssoClientsKeyedById.clear();
			cositesKeyedById.clear();
			
			//create dom document
			Document doc = JUtilDom4j.parse(j.Properties.getConfigPath()+"sso.xml", "UTF-8");
			Element root = doc.getRootElement();
			//create dom document end
			
			//isServer
			SSOConfig.isServer ="true".equalsIgnoreCase(root.elementText("is-server"));
			log.log("SSOConfig.isServer:"+SSOConfig.isServer, -1);
			
			//server
			String ssoServerUrl=root.elementText("server");
			if(!ssoServerUrl.endsWith("/")){
				ssoServerUrl+="/";
			}
			SSOConfig.ssoServer=ssoServerUrl;
			log.log("SSOConfig.ssoServer:"+SSOConfig.ssoServer, -1);

			//authenticator
			SSOConfig.authenticator =(Authenticator)Class.forName(root.elementText("authenticator")).newInstance();
			log.log("SSOConfig.authenticator:"+SSOConfig.authenticator.getClass(), -1);
		
			//
			SSOConfig.verifierCodeEnabled="true".equalsIgnoreCase(root.elementText("verifier-code-enabled"));
			log.log("SSOConfig.verifierCodeEnabled:"+SSOConfig.verifierCodeEnabled, -1);
			
			//session timeout
			SSOConfig.sessionTimeout=Integer.parseInt(root.elementText("session-time-out"));
			log.log("SSOConfig.sessionTimeout(int seconds):"+SSOConfig.sessionTimeout, -1);
			
			//online active time
			SSOConfig.onlineActiveTime=Integer.parseInt(root.elementText("online-active-time"));
			log.log("SSOConfig.onlineActiveTime(int seconds):"+SSOConfig.onlineActiveTime, -1);	
		
			//sso client notifiers
			SSOConfig.notifiersPerClient=Integer.parseInt(root.elementText("notifiers-per-client"));
			log.log("SSOConfig.notifiersPerClient:"+SSOConfig.notifiersPerClient, -1);

			
			//sso client notifiers
			SSOConfig.logoutOtherSessions=root.elementText("logout-other-sessions");
			log.log("SSOConfig.logoutOtherSessions:"+SSOConfig.logoutOtherSessions, -1);
			
			
			//sso client 与 cosite 配置信息加载器
			String clientsConfigLoader=root.elementText("clients-conf-loader");
			SSOConfigLoader loader=(SSOConfigLoader)Class.forName(clientsConfigLoader).newInstance();
			
			//加载sso client
			List clients=loader.loadClients();
			for(int i=0;clients!=null&&i<clients.size();i++){
				Client client=(Client)clients.get(i);
				SSOConfig.ssoClients.add(client);
				SSOConfig.ssoClientsKeyedById.put(client.getId(),client);
			}
			clients.clear();
			clients=null;
			
			root=null;
			doc=null;
			

			//配置文件最近修改时间
			File configFile=new File(Properties.getConfigPath()+"sso.xml");
			configLastModified=configFile.lastModified();
			configFile=null;
			
			loading=false;
		}catch(Exception e){
			loading=false;			
			log.log(e,Logger.LEVEL_FATAL);
		}
	}
	
	/**
	 * 
	 *
	 */
	private static void waitWhileLoading(){
		while(loading){
			try{
				Thread.sleep(100);
			}catch(Exception ex){}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * 检测sso.xml是否修改过，如修改过重新加载配置
		 */
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}

			File configFile=new File(Properties.getConfigPath()+"sso.xml");
			if(configLastModified<configFile.lastModified()){
				log.log("sso.xml has been modified, so reload it.",-1);
				load();
			}
			configFile=null;
		}
	}
}
