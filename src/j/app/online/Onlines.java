package j.app.online;

import j.app.Constants;
import j.app.permission.Permission;
import j.app.permission.Resource;
import j.app.sso.Client;
import j.app.sso.LoginStatus;
import j.app.sso.SSOClient;
import j.app.sso.SSOConfig;
import j.app.sso.User;
import j.app.webserver.Handlers;
import j.cache.JCacheParams;
import j.common.JProperties;
import j.http.JHttp;
import j.log.Logger;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.tool.ip.IP;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilMD5;
import j.util.JUtilMath;
import j.util.JUtilString;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 
 * @author 肖炯
 *
 */
public class Onlines implements Filter,Runnable{
	public static final int CHATTING_PENDING=0;//未发起聊天
	public static final int CHATTING_WAITING=1;//已发起聊天，等待客服相应
	public static final int CHATTING_INPROCESS=2;//正在聊天
	public static final int CHATTING_ENDED=3;//聊天结束
	public static final int CHATTING_REFUSED=-1;//被客服拒绝（1次）
	public static final int CHATTING_REFUSED_SESSION=-2;//被客服拒绝（本次会话）
	private static Logger log=Logger.create(Onlines.class);
	
	private static ConcurrentMap counts=new ConcurrentMap();
	private static ConcurrentMap onlines=new ConcurrentMap();
	
	private static int updaters=1;
	private static long updateInterval=1000;
	private static int maxRequestsPerMinutes=60;
	private static int maxSessionsPerIp=5;
	private static int maxPostSize=64;
	private static int maxUploadSize=1024;
	
	private static String[] ignoredIps=new String[]{};
	private static String[] credibleIps=new String[]{};
	private static ConcurrentList blackIps=new ConcurrentList();
	private static ConcurrentList blackRegions=new ConcurrentList();
	private static ConcurrentList domainLimits=new ConcurrentList();
	private static String[] fileUploadAllowedUrls;
	private static String[] ignoredUrls;
	private static String[] forbiddenSpiders;
	private static OnlineHandler handler;
	
	private static long configLastModified=0;//配置文件上次修改时间
	private static boolean loading=false;
	static{
		try{
			load();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_FATAL);
		}
		
		Onlines instance=new Onlines();
		Thread thread=new Thread(instance);
		thread.start();
		log.log("Onlines started",-1);
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getUpdaters(){
		return updaters;
	}
	
	/**
	 * 
	 * @return
	 */
	public static long getUpdateInterval(){
		return updateInterval;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getMaxPostSizeKB(){
		return maxPostSize;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getMaxUploadSizeKB(){
		return maxUploadSize;
	}
	
	/**
	 * 
	 * @param domain
	 * @param url
	 * @return
	 */
	public static boolean pass(String domain,String url){
		for(int i=0;i<domainLimits.size();i++){
			DomainLimit limit=(DomainLimit)domainLimits.get(i);
			if(limit.matches(url)&&!limit.allowed(domain)){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * 
	 */
	private static void load() {
		try{
			loading=true;
			
			//create dom document
			Document doc = JUtilDom4j.parse(JProperties.getConfigPath()+"onlines.xml", "UTF-8");
			Element root = doc.getRootElement();
			//create dom document end
			
			updaters=Integer.parseInt(root.elementTextTrim("updaters"));
			updateInterval=Long.parseLong(root.elementTextTrim("update-interval"));
			
			String _maxRequestsPerMinutes=root.elementTextTrim("max-requests-per-minute");
			if(JUtilMath.isInt(_maxRequestsPerMinutes)){
				maxRequestsPerMinutes=Integer.parseInt(_maxRequestsPerMinutes);
				log.log("maxRequestsPerMinutes:"+maxRequestsPerMinutes, -1);
			}
			
			String _maxSessionsPerIp=root.elementTextTrim("max-sessions-per-ip");
			if(JUtilMath.isInt(_maxSessionsPerIp)){
				maxSessionsPerIp=Integer.parseInt(_maxSessionsPerIp);
				log.log("maxSessionsPerIp:"+maxSessionsPerIp, -1);
			}
			
			String _maxPostSize=root.elementTextTrim("max-post-size");
			if(JUtilMath.isInt(_maxPostSize)){
				maxPostSize=Integer.parseInt(_maxPostSize);
				log.log("maxPostSize:"+maxPostSize, -1);
			}
			
			String _maxUploadSize=root.elementTextTrim("max-upload-size");
			if(JUtilMath.isInt(_maxUploadSize)){
				maxUploadSize=Integer.parseInt(_maxUploadSize);
				log.log("maxUploadSize:"+maxUploadSize, -1);
			}
			
			
			List eles=root.elements("ignored-ip");
			ignoredIps=new String[eles.size()];
			for(int i=0;i<eles.size();i++){
				Element temp=(Element)eles.get(i);
				ignoredIps[i]=temp.getTextTrim();
			}
			
			eles=root.elements("credible-ip");
			credibleIps=new String[eles.size()];
			for(int i=0;i<eles.size();i++){
				Element temp=(Element)eles.get(i);
				credibleIps[i]=temp.getTextTrim();
			}

			blackIps.clear();
			eles=root.elements("black-ip");
			for(int i=0;i<eles.size();i++){
				Element temp=(Element)eles.get(i);
				blackIps.add(new BlackIp(temp.getTextTrim()));
			}

			blackRegions.clear();
			eles=root.elements("black-region");
			for(int i=0;i<eles.size();i++){
				Element temp=(Element)eles.get(i);
				blackRegions.add(new BlackRegion(temp.getTextTrim()));
			}
			
			List urls=root.elements("file-upload-allowed-url");
			fileUploadAllowedUrls=new String[urls.size()];
			for(int i=0;i<urls.size();i++){
				Element temp=(Element)urls.get(i);
				fileUploadAllowedUrls[i]=temp.getTextTrim();
			}
			
			
			urls=root.elements("ignored-url");
			ignoredUrls=new String[urls.size()];
			for(int i=0;i<urls.size();i++){
				Element temp=(Element)urls.get(i);
				ignoredUrls[i]=temp.getTextTrim();
			}
			
			List spiders=root.elements("forbidden-spider");
			forbiddenSpiders=new String[spiders.size()];
			for(int i=0;i<spiders.size();i++){
				Element temp=(Element)spiders.get(i);
				forbiddenSpiders[i]=temp.getTextTrim();
			}
			
			domainLimits.clear();
			List domainLimitElements=root.elements("domain-limit");
			for(int i=0;i<domainLimitElements.size();i++){
				Element temp=(Element)domainLimitElements.get(i);
		
				DomainLimit limit=new DomainLimit();
				
				List matches=temp.elements("match");
				for(int j=0;j<matches.size();j++){
					Element temp2=(Element)matches.get(j);
					limit.addMatch(temp2.attributeValue("type"),temp2.getTextTrim());
				}

				List allowedDomains=temp.elements("allowed-domain");
				for(int j=0;j<allowedDomains.size();j++){
					Element temp2=(Element)allowedDomains.get(j);
					limit.addAllowedDomain(temp2.getTextTrim());
				}
				
				domainLimits.add(limit);
			}
			
			try{
				handler=(OnlineHandler)Class.forName(root.elementText("initializer")).newInstance();
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
			
			root=null;
			doc=null;
			
			//配置文件最近修改时间
			File configFile=new File(JProperties.getConfigPath()+"onlines.xml");
			configLastModified=configFile.lastModified();
			configFile=null;
			
			OnlineUpdater.startup();
			
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
	
	/**
	 * 
	 * @param session
	 * @return
	 */
	public static Online find(HttpSession session){
		if(session==null) return null;
		Online online=null;
		try{
			LoginStatus status=(LoginStatus)session.getAttribute(Constants.SSO_STAT_CLIENT);
			if(status!=null){
				online=(Online)onlines.get(status.getGlobalSessionId());
				if(online!=null) online.setFoundBy(Online.FOUND_BY_GLOBAL_SESSION_ID);
			}
			
			if(online==null){
				online=(Online)onlines.get(session.getId());
				if(online!=null) online.setFoundBy(Online.FOUND_BY_SESSION_ID);
			}
			
			return online;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return null;
		}
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public static Online findByUserId(String userId){
		List all=getActiveUsers();
		for(int i=0;i<all.size();i++){
			Online online=(Online)all.get(i);
			if(userId.equals(online.getUid())){				
				all.clear();
				all=null;
				
				return online;
			}
		}
		
		all.clear();
		all=null;
		
		return null;
	}
	
	/**
	 * 
	 * @param sessionId
	 * @return
	 */
	public static Online findBySessionId(String sessionId){
		if(sessionId==null||"".equals(sessionId)) return null;
		List all=getActiveUsers();
		for(int i=0;i<all.size();i++){
			Online online=(Online)all.get(i);
			if(sessionId.equals(online.getCurrentSessionId())){				
				all.clear();
				all=null;
				
				return online;
			}
		}
		
		all.clear();
		all=null;
		
		return null;
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public static Online[] findAllByUserId(String userId){
		List all=getActiveUsers();
		List temp=new LinkedList();
		for(int i=0;i<all.size();i++){
			Online online=(Online)all.get(i);
			if(userId.equals(online.getUid())){				
				temp.add(online);
			}
		}
		
		if(temp.isEmpty()) return null;
		
		return (Online[])temp.toArray(new Online[temp.size()]);
	}
	
	/**
	 * 
	 * @return
	 */
	public static List getActiveUsers(){
		try{
			return onlines.listValues();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return new ConcurrentList();
		}
	}
	
	/**
	 * 
	 * @param rpp
	 * @param pn
	 * @return
	 */
	public static List getActiveUsers(int rpp,int pn){
		List all=getActiveUsers();
		List tmp=new LinkedList();
		for(int i=rpp*(pn-1);i<rpp*pn&&i<all.size();i++){
			tmp.add(all.get(i));
		}
		all.clear();
		all=null;
		
		return tmp;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getActiveUsersTotal(){
		try{
			return onlines.size();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return 0;
		}
	}
	
	/**
	 * 
	 * @param online
	 */
	public static void update(Online online){
		try{
			/*
			if(online.getGlobalSessionId()!=null&&!"".equals(online.getGlobalSessionId())){
				if(online.getFoundBy()==Online.FOUND_BY_SESSION_ID
						&&online.getCurrentSessionId()!=null
						&&!"".equals(online.getCurrentSessionId())){
					onlines.remove(online.getCurrentSessionId());
				}
				onlines.put(online.getGlobalSessionId(), online);
			}else{
				if(online.getFoundBy()==Online.FOUND_BY_GLOBAL_SESSION_ID
						&&online.getGlobalSessionId()!=null
						&&!"".equals(online.getGlobalSessionId())){
					onlines.remove(online.getGlobalSessionId());
				}
				onlines.put(online.getCurrentSessionId(), online);
			}*/
			
			onlines.put(online.getCurrentSessionId(), online);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
		}
	}
	
	/**
	 * 未登录的会话数
	 * @return
	 */
	public static int sessionsPerIp(String ip){
		int sessions=0;
		List all=getActiveUsers();
		for(int i=0;i<all.size();i++){
			Online online=(Online)all.get(i);
			if(ip.equals(online.getCurrentIp())){
				sessions++;
			}
		}
		return sessions;
	}
	
	/**
	 * 
	 * @param ip
	 * @return
	 */
	public static int requestsPerMinute(String ip){
		RequestCount count=(RequestCount)counts.get(ip);
		if(count==null){
			return 0;
		}else{
			float minutes=(float)Math.ceil((count.latestRequestTime-count.firstRequestTime)/60000F);
			if(minutes<1) minutes=1;
			float requestsPerMinute=Float.parseFloat(JUtilMath.formatPrint(count.requests/minutes,3));
			return Math.round(requestsPerMinute);
		}
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static boolean credible(HttpServletRequest request){
		String ip=JHttp.getRemoteIp(request);
		
		boolean credible=false;
		if(Permission.hasValidPassport(request)){
			credible=true;
		}else{
			for(int i=0;i<credibleIps.length;i++){
				if(JUtilString.match(ip, credibleIps[i], "*")>-1){
					credible=true;
					break;
				}
			}
		}
		
		return credible;
	}
	
	/**
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean black(String ip){
		boolean black=false;
		for(int i=0;i<blackIps.size();i++){
			BlackIp bi=(BlackIp)blackIps.get(i);
			if(JUtilString.match(ip, bi.ip, "*")>-1){
				black=true;
				break;
			}
		}
		
		return black;
	}
	
	/**
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean blackRegion(String ip){
		try{
			String region=IP.getLocation(ip);
			if(region==null||"".equals(region)) return false;
			
			boolean black=false;
			for(int i=0;i<blackRegions.size();i++){
				BlackRegion br=(BlackRegion)blackRegions.get(i);
				if(region.indexOf(br.region)>-1){
					black=true;
					break;
				}
			}
			
			return black;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return false;
		}
	}
	
	/**
	 * 
	 * @param ip
	 * @param timeout
	 */
	public static void addBlackTemporary(String ip,long timeout){
		blackIps.add(new BlackIp(ip,timeout));
	}
	
	/**
	 * 
	 * @param ip
	 */
	public static void addBlackPermanent(String ip){
		blackIps.add(new BlackIp(ip));
	}
	
	/**
	 * 
	 * @param client
	 * @param request
	 * @return
	 */
	private static boolean verifySsoLogin(Client client,HttpServletRequest request){
		String verify=request.getParameter(Constants.SSO_PVERIFY);
		String names=request.getParameter(Constants.SSO_PNAMES);
		if(verify==null||names==null) return false;
		
		String[] ns=names.split("\\|");
		String values="";
		for(int i=0;i<ns.length;i++){
			if("".equals(ns[i])) continue;
			values+=SysUtil.getHttpParameter(request,ns[i]);
		}
		values+=client.getPassport();
		return verify.equalsIgnoreCase(JUtilMD5.MD5EncodeToHex(values));
	}


	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest _request, 
			ServletResponse _response,
			FilterChain chain) throws IOException, ServletException {
		waitWhileLoading();

		HttpServletRequest request=(HttpServletRequest)_request;
		HttpServletResponse response=(HttpServletResponse)_response;	
		String ip=JHttp.getRemoteIp(request);
		String currentUrl=SysUtil.getRequestURL(request,"sso");	
		String requestURL=request.getRequestURL().toString();
		requestURL=requestURL.replaceFirst(":"+request.getRemotePort(), "");
		
		if(JUtilString.getTokens(JUtilString.getHost(requestURL),".").length<2){
			log.log("ip "+ip+" 非法URL - "+requestURL,Logger.LEVEL_ERROR);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		if(!requestURL.matches(JUtilString.RegExpHttpUrl)){
			log.log("ip "+ip+" 非法URL - "+requestURL,Logger.LEVEL_ERROR);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		try{	
			HttpSession session=request.getSession(true);			
	
			User user=SSOClient.getCurrentUser(session);
			
			long now=SysUtil.getNow();
			
			String url=request.getRequestURL().toString();
			String urlWithoutDomain=url.substring(url.indexOf("/",10));
			String uri=request.getRequestURI();
			String domain=JUtilString.getHost(url);
			
			String userAgent=request.getHeader("User-Agent");
			if(userAgent==null) userAgent="";
			
			String referer=request.getHeader("Referer");
			
			String method=request.getMethod();
			if(method==null) method="POST";
			
			String contentType=request.getContentType();
			if(contentType==null) contentType="";
			
			int contentLength=request.getContentLength();
			
	
			/////////////////////////////////////////安全控制////////////////////////////////////
			if(!Onlines.pass(domain,urlWithoutDomain)){//域名限制
				log.log("域名限制："+domain+","+urlWithoutDomain,Logger.LEVEL_ERROR);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			if(black(ip)){//黑IP
				log.log("黑名单IP："+ip,Logger.LEVEL_ERROR);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			if(blackRegion(ip)){//黑地区
				log.log("黑名单地区："+ip,Logger.LEVEL_ERROR);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			boolean credible=credible(request);
			if(!credible){
				//被禁止的搜索引擎
				if(userAgent!=null){
					userAgent=userAgent.toLowerCase();
					for(int i=0;i<forbiddenSpiders.length;i++){
						if(forbiddenSpiders[i].startsWith("EQUALS")){
							if(forbiddenSpiders[i].substring(6).equalsIgnoreCase(userAgent)){
								log.log("被禁止的User-Agent："+userAgent,Logger.LEVEL_ERROR);
								response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
								return;
							}
						}else if(userAgent.indexOf(forbiddenSpiders[i])>-1){
							log.log("被禁止的User-Agent："+userAgent,Logger.LEVEL_ERROR);
							response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							return;
						}
					}
				}
				//被禁止的搜索引擎  end
				
				//每个ip会话数
				int sessionsOfIp=sessionsPerIp(ip);
				if(sessionsOfIp>maxSessionsPerIp){
					if(handler!=null){
						handler.onManySessionsOnIp(ip);
					}
					log.log("ip "+ip+" 上共有  "+sessionsOfIp+" 个会话，超出限制 "+maxSessionsPerIp,Logger.LEVEL_ERROR);
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				}
				
				//是否允许通过
				if(handler!=null&&!handler.canPass(session,request)){
					log.log("被业务规则禁止访问："+handler.getClass().getCanonicalName(),Logger.LEVEL_ERROR);
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				}
			}
			
			boolean ignore=false;
			if(ignoredIps!=null
					&&JUtilString.contain(ignoredIps, ip)){
				ignore=true;
			}
			
			if(!ignore
					&&Permission.hasValidPassport(request)){
				ignore=true;
			}
			
			boolean ignoreUrl=false;
			if(!ignore
					&&ignoredUrls!=null){
				for(int i=0;i<ignoredUrls.length;i++){
					if(JUtilString.match(uri, ignoredUrls[i],"*")>-1){
						ignore=true;
						ignoreUrl=true;
						break;
					}
				}
			}
			
			if(!ignoreUrl){				
				//文件上传是否允许
				if(contentType.indexOf("boundary")>-1){
					if(user==null){//未登录
						boolean allowed=false;
						for(int i=0;i<fileUploadAllowedUrls.length;i++){
							if(JUtilString.match(uri, fileUploadAllowedUrls[i], "*")>-1){
								allowed=true;
								break;
							}
						}
						
						if(!allowed){
							log.log("ip "+ip+" 未登录, 试图上传文件("+uri+") ，大小 "+contentLength+" ，已被禁止.", Logger.LEVEL_ERROR);
							
							SysUtil.outHttpResponse(response, "-login");
							return;
						}
					}
					
					if(contentLength<0||contentLength>maxUploadSize*1024){
						log.log("ip "+ip+" 试图上传文件 ("+uri+")，大小 "+contentLength+" 超过 "+ (maxUploadSize*1024) +"("+maxUploadSize+"K)，已被禁止.", Logger.LEVEL_ERROR);

						SysUtil.outHttpResponse(response, "-max-upload-size-"+maxUploadSize);
						return;
					}
				}else if(user==null
						&&"POST".equalsIgnoreCase(method)
						&&contentLength>maxPostSize*1024
						&&!Permission.hasValidPassport(request)){
					log.log("ip "+ip+" 发起POST请求("+uri+") ，大小 "+contentLength+" 超过 "+ (maxPostSize*1024) +"("+maxPostSize+"K)，已被禁止.", Logger.LEVEL_ERROR);
					
					SysUtil.outHttpResponse(response, "-max-post-size-"+maxPostSize);
					return;
				}
			}
			
			if(!credible&&!ignoreUrl){
				//频繁访问
				RequestCount count=(RequestCount)counts.get(ip);
				if(count==null){
					count=new RequestCount();
					count.firstRequestTime=now;
					count.latestRequestTime=now;
					count.requests=1;
					counts.put(ip, count);
				}else{
					if(now-count.latestRequestTime>=SSOConfig.getOnlineActiveTime()*1000){//状态为离线了，重新计时
						count.firstRequestTime=now;
						count.latestRequestTime=now;
						count.requests=1;
					}else{
						count.latestRequestTime=now;
						count.requests++;
					}
					counts.put(ip, count);
				}
			
				double minutes=Math.ceil((double)(count.latestRequestTime-count.firstRequestTime)/(double)60000);
				if(minutes<1) minutes=1;
				double requestsPerMinute=Double.parseDouble(JUtilMath.formatPrint(count.requests/minutes,3));
				if(requestsPerMinute>maxRequestsPerMinutes){
					log.log("ip "+ip+" 共在线 "+minutes+" 分钟，请求  "+count.requests+" 次，平均每分钟 "+requestsPerMinute+" 次，超出限制 "+maxRequestsPerMinutes,Logger.LEVEL_ERROR);

					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				}
				//频繁访问 END
			}	
			/////////////////////////////////////////安全控制 end////////////////////////////////////
		
		
			/////////////////////////////////////////权限处理////////////////////////////////////
			
			Client client=SSOConfig.getSsoClientByIdOrUrl(SysConfig.getSysId());
			//log.log("requestURL:"+requestURL+",client:"+client,-1);
			if(client==null){//如果不是在sso.xml中指定的域名，禁止任何操作
				return;
			}	
			
			//设置session超时时间，单位秒
			if(SSOConfig.getSessionTimeout()>0){
				session.setMaxInactiveInterval(SSOConfig.getSessionTimeout());
			}	
					
			//持有通行证，不进行权限认证和SSO相关操作
			if(Permission.hasValidPassport(request)){
				chain.doFilter(_request,_response);
				return;
			}
			
			String loginFrom=request.getParameter(Constants.SSO_LOGIN_FROM_SYS_ID);
			if(loginFrom!=null){
				String ssoPutLoginStatus=request.getParameter(Constants.SSO_PUT_LOGIN_STATUS);
				String globalSessionId=SysUtil.getHttpParameter(request,Constants.SSO_GLOBAL_SESSION_ID);
				
				String loginPage=SysUtil.getHttpParameter(request,Constants.SSO_LOGIN_PAGE);
				if(loginPage!=null){
					try{
						loginPage=JUtilString.decodeURI(loginPage,SysConfig.sysEncoding);
					}catch(Exception e){}
				}else{
					loginPage=client.getLoginPage();
				}
				
				if(!verifySsoLogin(client,request)){
					SysUtil.redirect(request,response,loginPage);
					return;
				}

				LoginStatus loginStatus=SSOClient.findLoginStatusOfSessionId(globalSessionId);
				
				if(loginStatus==null){//未登录，直接返回
					if("true".equalsIgnoreCase(ssoPutLoginStatus)){
						SysUtil.redirect(request,response,SysUtil.getRequestURLBase(request)+Constants.SSO_GET_LOGIN_STATUS_RESULT+"?ok=0");
					}else{
						SysUtil.redirect(request,response,loginPage);	
					}
					return;
				}else{//已经登录，加载用户信息
					user=User.loadUser(session,request,loginStatus.getUserId());//加载用户信息
					
					if(user!=null){//加载用户信息成功	
						loginStatus.setSession(session);
						loginStatus.login();//确认登录
						loginStatus.setUpdateTime(SysUtil.getNow());
						loginStatus.setLoginFromDomain(SysUtil.getHttpDomain(request));
						loginStatus.setUserAgent(request.getHeader("User-Agent"));
						
						SSOClient.refreshLoginStatus(loginStatus);
						
						SSOClient.saveUserInformation(session,loginStatus,user);
						
						if("true".equalsIgnoreCase(ssoPutLoginStatus)){
							SysUtil.redirect(request,response,SysUtil.getRequestURLBase(request)+Constants.SSO_GET_LOGIN_STATUS_RESULT+"?ok=1");
						}else{
							SysUtil.redirect(request,response,currentUrl);
						}
						return;
					}else{
						log.log("已经登录，但加载用户信息失败 - "+globalSessionId+","+loginStatus.getUserId()+","+loginStatus.getUserIp(),Logger.LEVEL_ERROR);
						if("true".equalsIgnoreCase(ssoPutLoginStatus)){
							SysUtil.redirect(request,response,SysUtil.getRequestURLBase(request)+Constants.SSO_GET_LOGIN_STATUS_RESULT+"?ok=0");
						}else{
							SysUtil.redirect(request,response,loginPage);	
						}
						return;
					}
				}
			}
			
			//保存在session中的单点登录信息
			LoginStatus loginStatus=SSOClient.getLoginStatus(session);
			
			//更新用户最近活动时间
			if(loginStatus!=null){
				loginStatus.update();
				SSOClient.setLoginStatus(session,loginStatus);
				
				SSOClient.updater.addKey(loginStatus);
			}	
			
			if(user!=null){
				SSOClient.setCurrentUser(session,user);	
			}
			
			//查询登录状态
			String ssoGetLoginStatus=request.getParameter(Constants.SSO_GET_LOGIN_STATUS);
			if("true".equalsIgnoreCase(ssoGetLoginStatus)){
				if(user!=null&&loginStatus!=null&&loginStatus.getStat()==LoginStatus.STAT_VISITED){
					SysUtil.redirect(request,response,Constants.SSO_GET_LOGIN_STATUS_RESULT+"?ok=1");	
					return;
				}else{
					String redirect=SSOConfig.getSsoServer()+"ssoserver"+Handlers.getActionPathPattern();
					redirect+="?"+Handlers.getHandler("/ssoserver").getRequestBy()+"=ssoquery";	
					redirect+="&"+Constants.SSO_CLIENT+"="+client.getUrlDefault();
					
					if(currentUrl.indexOf("?")<0){
						redirect+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(currentUrl+"?"+Constants.SSO_PUT_LOGIN_STATUS+"=true",SysConfig.sysEncoding);
						redirect+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(SysUtil.getRequestURLBase(request)+Constants.SSO_GET_LOGIN_STATUS_RESULT+"?ok=0",SysConfig.sysEncoding);
					}else{
						redirect+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(currentUrl+"&"+Constants.SSO_PUT_LOGIN_STATUS+"=true",SysConfig.sysEncoding);
						redirect+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(SysUtil.getRequestURLBase(request)+Constants.SSO_GET_LOGIN_STATUS_RESULT+"?ok=0",SysConfig.sysEncoding);
					}
					SysUtil.redirect(request,response,redirect);
					return;
				}
			}
			//查询登录状态 end
			
			//是否是需要认证的资源
			Resource res=Permission.permission(request,user);
			
			//如果需要认证
			if(res!=null){	
				if(loginStatus==null
						||loginStatus.getStat()==LoginStatus.STAT_CREATE
						||user==null){//如果未登录
					String redirect=SSOConfig.getSsoServer()+"ssoserver"+Handlers.getActionPathPattern();
					redirect+="?"+Handlers.getHandler("/ssoserver").getRequestBy()+"=ssoquery";	
					redirect+="&"+Constants.SSO_CLIENT+"="+client.getUrlDefault();
					redirect+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(currentUrl,SysConfig.sysEncoding);
					
					if(res!=null&&res.getLoginPage()!=null){
						redirect+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(res.getLoginPage(),SysConfig.sysEncoding);
					}
					
					SysUtil.redirect(request,response,redirect);
					return;
				}else{//否则是权限不够
					String noPermissionPage="";
					if(res.getNoPermissionPage()!=null){
						noPermissionPage=res.getNoPermissionPage();
					}else{
						noPermissionPage=Permission.getNoPermissionPage();
					}	
					if(noPermissionPage.indexOf("?")>0){
						noPermissionPage+="&"+Constants.SSO_BACK_URL+"="+URLEncoder.encode(currentUrl,SysConfig.sysEncoding);
					}else{
						noPermissionPage+="?"+Constants.SSO_BACK_URL+"="+URLEncoder.encode(currentUrl,SysConfig.sysEncoding);
					}
					if(currentUrl.indexOf(Constants.SSO_INFO_GETTER)<0
							&&currentUrl.indexOf(Constants.SSO_INFO_GETTER_LOGIN)<0){						
						log.log("试图访问没有权限的资源:"+res+","+user.getUserId()+","+currentUrl,Logger.LEVEL_FATAL);	
						SysUtil.redirect(request,response,noPermissionPage);	
					}else{
						SysUtil.outHttpResponse(response,"<script>try{if(top.onSsoInfoGot) top.onSsoInfoGot();}catch(e){}</script>");
					}
					return;
				}
			}
			/////////////////////////////////////////权限处理  end////////////////////////////////////
			
			
			/////////////////////////////////////////兼容性处理////////////////////////////////////
			String compatibleResource=SysConfig.getCssCompatibleResource(uri,SysConfig.getUserAgentType(request));
			if(compatibleResource!=null){
				SysUtil.forwardI18N(request,response,compatibleResource);
				return;
			}
			/////////////////////////////////////////兼容性处理 end////////////////////////////////////
		

			/////////////////////////////////////////在线用户处理////////////////////////////////////
			if(!ignore){
				if("POST".equalsIgnoreCase(method)){
					uri=method+" "+contentType+" "+contentLength+" "+SysUtil.getRequestURL(request);
				}else{
					uri=method+" "+contentType+" "+SysUtil.getRequestURL(request);
				}
				
				Online online=find(session);
				if(online!=null){
					if(online.getUser()==null&&user!=null){
						LoginStatus status=(LoginStatus)session.getAttribute(Constants.SSO_STAT_CLIENT);
						if(status!=null){
							online.setGlobalSessionId(status.getGlobalSessionId());
						}
						online.setUid(user.getUserId());
						online.setUname(user.getUserName());
						online.setUser(user);
						
						if(handler!=null){
							handler.onLogin(online, user, session, request);
						}
					}else if(online.getUser()!=null&&user==null){
						online.removeUser();
						online.setUid(null);
						online.setUname(null);
						
						if(handler!=null){
							handler.onLogout(online, user, session, request);
						}
					}
					
					online.setCurrentIp(ip);
					online.setCurrentSysId(SysConfig.getSysId());
					online.setCurrentMachineId(SysConfig.getMachineID());
					online.setCurrentSessionId(session.getId());
					online.setCurrentUrl(uri);
				
					online.update();
					
					update(online);
				}else{		
					online=new Online();
					
					if(handler!=null){
						handler.onInit(online, user, session, request);
					}
					
					if(user!=null){
						LoginStatus status=(LoginStatus)session.getAttribute(Constants.SSO_STAT_CLIENT);
						if(status!=null){
							online.setGlobalSessionId(status.getGlobalSessionId());
						}
						online.setUid(user.getUserId());
						online.setUname(user.getUserName());
						online.setUser(user);
						
						if(handler!=null){
							handler.onLogin(online, user, session, request);
						}
					}
					
					if(referer==null) referer="";
					if(userAgent==null) userAgent="";
					
					online.setCurrentIp(ip);
					online.setCurrentSysId(SysConfig.getSysId());
					online.setCurrentMachineId(SysConfig.getMachineID());
					online.setCurrentSessionId(session.getId());
					online.setCurrentReferer(referer);
					online.setCurrentUserAgent(userAgent);
					online.setCurrentUrl(uri);
					
					update(online);
				}
				
				if(handler!=null){
					handler.doFilter(_request, _response, chain);
				}else{
					chain.doFilter(_request,_response);
				}
			}else{
				if(handler!=null){
					handler.doFilter(_request, _response, chain);
				}else{
					chain.doFilter(_request,_response);
				}
			}
			/////////////////////////////////////////在线用户处理 end////////////////////////////////////
		}catch(Exception e){
			log.log("errors on url \r\n"+requestURL+"\r\n"+currentUrl,Logger.LEVEL_ERROR);
			log.log(e,Logger.LEVEL_ERROR);
			SysUtil.redirect(request,response,SysConfig.errorPage);
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		//nothing to do
	}


	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		//nothing to do
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		JCacheParams params=new JCacheParams();
		params.valueFilter=new OnlineRemover();
		
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			//配置文件变动
			try{
				File configFile=new File(JProperties.getConfigPath()+"onlines.xml");
				if(configLastModified<configFile.lastModified()){
					log.log("onlines.xml has been modified, so reload it.",-1);
					load();
				}
				configFile=null;
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
			
			//移除过期黑名单
			for(int i=0;i<blackIps.size();i++){
				BlackIp bi=(BlackIp)blackIps.get(i);
				if(bi.isTimeout()){
					blackIps.remove(i);
					i--;
				}
			}
			
			//移除过期对象
			long now=SysUtil.getNow();
			List keys=onlines.listKeys();
			for(int i=0;i<keys.size();i++){
				String key=(String)keys.get(i);
				Online o=(Online)onlines.get(key);
				if(o==null||now-o.getUpdateTime()>SSOConfig.getOnlineActiveTime()*1000){
					onlines.remove(key);
				}
			}
			keys.clear();
			keys=null;
			
			//移除过期访问记录
			keys=counts.listKeys();
			for(int i=0;i<keys.size();i++){
				String ip=(String)keys.get(i);
				RequestCount c=(RequestCount)counts.get(ip);
				if(c==null||now-c.latestRequestTime>SSOConfig.getOnlineActiveTime()*1000){//5分钟
					counts.remove(ip);
				}
			}
			keys.clear();
			keys=null;
		}
	}
}


class BlackIp{
	public String ip;
	public long timeout=0;
	public long createTime=0;
	
	public BlackIp(String ip){
		this.ip=ip;
		this.timeout=-1;
		this.createTime=SysUtil.getNow();
	}
	
	public BlackIp(String ip,long timeout){
		this.ip=ip;
		this.timeout=timeout;
		this.createTime=SysUtil.getNow();
	}
	
	public boolean isTimeout(){
		if(this.timeout<=0) return false;
		return (SysUtil.getNow()-this.createTime>=this.timeout);
	}
}

class BlackRegion{
	public String region;
	public long timeout=0;
	public long createTime=0;
	
	public BlackRegion(String region){
		this.region=region;
		this.timeout=-1;
		this.createTime=SysUtil.getNow();
	}
	
	public BlackRegion(String region,long timeout){
		this.region=region;
		this.timeout=timeout;
		this.createTime=SysUtil.getNow();
	}
	
	public boolean isTimeout(){
		if(this.timeout<=0) return false;
		return (SysUtil.getNow()-this.createTime>=this.timeout);
	}
}

class DomainLimit{
	public List matches=new LinkedList();
	public List allowedDomains=new LinkedList();
	
	public DomainLimit(){
		
	}
	
	public void addMatch(String type,String pattern){
		matches.add(type+"^"+pattern);
	}
	
	public void addAllowedDomain(String pattern){
		allowedDomains.add(pattern);
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public boolean matches(String url){
		if(matches.isEmpty()) return false;
		
		for(int i=0;i<matches.size();i++){
			String[] match=((String)matches.get(i)).split("\\^");
			if(match[1].indexOf("*")>-1||match[0].equals("matches")){
				if(JUtilString.match(url, match[1], "*")>-1) return true;
			}else if(match[0].equals("startsWith")){
				if(url.startsWith(match[1])) return true;
			}else if(match[0].equals("equals")){
				if(url.equals(match[1])) return true;
			}else if(match[0].equals("contains")){
				if(url.indexOf(match[1])>-1) return true;
			}
		}
		
		return false;
	}

	/**
	 * 
	 * @param domain
	 * @return
	 */
	public boolean allowed(String domain){
		if(allowedDomains.isEmpty()) return false;
		
		for(int i=0;i<allowedDomains.size();i++){
			String allowedDomain=(String)allowedDomains.get(i);
			if("*".equals(allowedDomain)) return true;
			
			if(allowedDomain.indexOf("*")>-1){
				if(JUtilString.match(domain, allowedDomain, "*")>-1) return true;
			}else{
				if(domain.equalsIgnoreCase(allowedDomain)) return true;
			}
		}
		
		return false;
	}
}
