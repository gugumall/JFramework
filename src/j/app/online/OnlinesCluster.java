package j.app.online;

import j.Properties;
import j.app.Constants;
import j.app.permission.Permission;
import j.app.sso.LoginStatus;
import j.app.sso.SSOClient;
import j.app.sso.User;
import j.cache.CachedMap;
import j.cache.JCacheParams;
import j.http.JHttp;
import j.log.Logger;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilMath;
import j.util.JUtilString;

import java.io.File;
import java.io.IOException;
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
public class OnlinesCluster implements Filter,Runnable{
	public static final int CHATTING_PENDING=0;//未发起聊天
	public static final int CHATTING_WAITING=1;//已发起聊天，等待客服相应
	public static final int CHATTING_INPROCESS=2;//正在聊天
	public static final int CHATTING_ENDED=3;//聊天结束
	public static final int CHATTING_REFUSED=-1;//被客服拒绝（1次）
	public static final int CHATTING_REFUSED_SESSION=-2;//被客服拒绝（本次会话）
	
	private static ConcurrentMap counts=new ConcurrentMap();
	
	private static Logger log=Logger.create(OnlinesCluster.class);
	private static CachedMap onlines=null;
	
	private static int updaters=1;
	private static long updateInterval=1000;
	private static int maxRequestsPerMinutes=60;
	private static int maxSessionsPerIp=5;
	
	private static String[] ignoredIps;
	private static String[] credibleIps;
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
		
		OnlinesCluster instance=new OnlinesCluster();
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
	 */
	private static void _init(){
		try{
			if(onlines==null) onlines=new CachedMap(Constants.SSO_ONLINES_CACHE);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_FATAL);
			try{
				Thread.sleep(5000);
			}catch(Exception ex){}
			_init();
		}
	}
	
	/**
	 * 
	 * 
	 */
	private static void load() {
		try{
			loading=true;
			
			//create dom document
			Document doc = JUtilDom4j.parse(j.Properties.getConfigPath()+"onlines.xml", "UTF-8");
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
			
			
			List ips=root.elements("ignored-ip");
			ignoredIps=new String[ips.size()];
			for(int i=0;i<ips.size();i++){
				Element temp=(Element)ips.get(i);
				ignoredIps[i]=temp.getTextTrim();
			}
			
			ips=root.elements("credible-ip");
			credibleIps=new String[ips.size()];
			for(int i=0;i<ips.size();i++){
				Element temp=(Element)ips.get(i);
				credibleIps[i]=temp.getTextTrim();
			}
			
			List urls=root.elements("ignored-url");
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
			
			
			try{
				handler=(OnlineHandler)Class.forName(root.elementText("initializer")).newInstance();
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
			
			root=null;
			doc=null;
			
			//配置文件最近修改时间
			File configFile=new File(Properties.getConfigPath()+"onlines.xml");
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
				online=(Online)onlines.get(new JCacheParams(status.getGlobalSessionId()));
				if(online!=null) online.setFoundBy(Online.FOUND_BY_GLOBAL_SESSION_ID);
			}
			
			if(online==null){
				online=(Online)onlines.get(new JCacheParams(session.getId()));
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
		JCacheParams params=new JCacheParams();
		OnlineFilter filter=new OnlineFilter();
		filter.setUid(userId);
		params.valueFilter=filter;
		try{
			return (Online)onlines.get(params);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return null;
		}
	}
	
	/**
	 * 
	 * @param sessionId
	 * @return
	 */
	public static Online findBySessionId(String sessionId){
		JCacheParams params=new JCacheParams();
		OnlineFilter filter=new OnlineFilter();
		filter.setSessionId(sessionId);
		params.valueFilter=filter;
		try{
			return (Online)onlines.get(params);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static List getActiveUsers(){
		try{
			return onlines.values(null);
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
		JCacheParams params=new JCacheParams();
		params.recordsPerPage=rpp;
		params.pageNum=pn;
		try{
			return onlines.values(params);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return new ConcurrentList();
		}
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
			if(online.getGlobalSessionId()!=null&&!"".equals(online.getGlobalSessionId())){
				if(online.getFoundBy()==Online.FOUND_BY_SESSION_ID
						&&online.getCurrentSessionId()!=null
						&&!"".equals(online.getCurrentSessionId())){
					onlines.remove(new JCacheParams(online.getCurrentSessionId()));
				}
				onlines.addOne(online.getGlobalSessionId(), online);
			}else{
				if(online.getFoundBy()==Online.FOUND_BY_GLOBAL_SESSION_ID
						&&online.getGlobalSessionId()!=null
						&&!"".equals(online.getGlobalSessionId())){
					onlines.remove(new JCacheParams(online.getGlobalSessionId()));
				}
				onlines.addOne(online.getCurrentSessionId(), online);
			}
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
		}
	}
	
	/**
	 * 
	 * @param online
	 */
	public static void updateDelay(Online online){
		OnlineUpdater.update(online);
	}
	
	/**
	 * 
	 * @return
	 */
	public static int sessionsPerIp(String ip){
		if(onlines==null) return 0;
		try{
			OnlineFilter filter=new OnlineFilter();
			filter.setIp(ip);
			return onlines.sub(new JCacheParams(filter)).size();
		}catch(Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return 0;
		}
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
		HttpSession session=request.getSession(true);	
		
		
		User user=SSOClient.getCurrentUser(session);
		long now=SysUtil.getNow();
		String ip=JHttp.getRemoteIp(request);
		String url=SysUtil.getRequestURL(request);
		String userAgent=request.getHeader("User-Agent");
		if(userAgent==null) userAgent="";
		String referer=request.getHeader("Referer");
		
		boolean credible=false;
		for(int i=0;i<credibleIps.length;i++){
			if(JUtilString.match(ip, credibleIps[i], "*")>-1){
				credible=true;
				break;
			}
		}
		
		if(!credible){
			//被禁止的搜索引擎
			if(userAgent!=null){
				userAgent=userAgent.toLowerCase();
				for(int i=0;i<forbiddenSpiders.length;i++){
					if(forbiddenSpiders[i].startsWith("EQUALS")){
						if(forbiddenSpiders[i].substring(6).equalsIgnoreCase(userAgent)){
							response.sendError(HttpServletResponse.SC_BAD_REQUEST);
							return;
						}
					}else if(userAgent.indexOf(forbiddenSpiders[i])>-1){
						response.sendError(HttpServletResponse.SC_BAD_REQUEST);
						return;
					}
				}
			}
			//被禁止的搜索引擎  end
			
			//是否允许通过
			if(handler!=null&&!handler.canPass(request)){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			//每个ip会话数
			int sessionsOfIp=sessionsPerIp(ip);
			if(sessionsOfIp>maxSessionsPerIp){
				if(handler!=null){
					handler.onManySessionsOnIp(ip);
				}
				//log.log("ip "+ip+" 上共有  "+sessionsOfIp+" 个会话，超出限制 "+maxSessionsPerIp, -1);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
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
				if(JUtilString.match(url, ignoredUrls[i],"*")>-1){
					ignore=true;
					ignoreUrl=true;
					break;
				}
			}
		}
		
		if(!credible&&!ignoreUrl){
			//频繁访问
			RequestCount count=(RequestCount)counts.get(ip);
			if(count==null){
				//log.log("开始监测ip "+ip+" 的频繁访问.", -1);
				count=new RequestCount();
				count.firstRequestTime=now;
				count.latestRequestTime=now;
				count.requests=1;
				counts.put(ip, count);
			}else{
				count.latestRequestTime=now;
				if(now-count.latestRequestTime>60000){
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
				log.log("ip "+ip+" 共在线 "+minutes+" 分钟，请求  "+count.requests+" 次，平均每分钟 "+requestsPerMinute+" 次，超出限制 "+maxRequestsPerMinutes, -1);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			//频繁访问 END
		}
		
		if(!ignore){
			_init();
			
			try{
				Online online=find(session);
				if(online!=null){
					boolean updateDelay=true;
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
						
						updateDelay=false;
					}else if(online.getUser()!=null&&user==null){
						online.removeUser();
						
						if(handler!=null){
							handler.onLogout(online, user, session, request);
						}
						
						updateDelay=false;
					}
					
					online.setCurrentIp(ip);
					online.setCurrentSysId(SysConfig.getSysId());
					online.setCurrentMachineId(SysConfig.getMachineID());
					online.setCurrentSessionId(session.getId());
					online.setCurrentUrl(url);
				
					online.update();
					
					if(!updateDelay){
						update(online);
					}else{
						updateDelay(online);
					}
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
					online.setCurrentUrl(url);
					
					update(online);
				}
				
				if(handler!=null){
					handler.doFilter(_request, _response, chain);
				}else{
					try{
						chain.doFilter(_request,_response);
					}catch(Exception e){}
				}
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
		}else{
			try{
				chain.doFilter(_request,_response);
			}catch(Exception e){}
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
				File configFile=new File(Properties.getConfigPath()+"onlines.xml");
				if(configLastModified<configFile.lastModified()){
					log.log("onlines.xml has been modified, so reload it.",-1);
					load();
				}
				configFile=null;
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
			
			//移除过期对象
			try{
				if(onlines==null) continue;
				onlines.remove(params);
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
			
			//移除过期访问记录
			long now=SysUtil.getNow();
			List keys=counts.listKeys();
			for(int i=0;i<keys.size();i++){
				String ip=(String)keys.get(i);
				RequestCount c=(RequestCount)counts.get(ip);
				if(c==null||now-c.latestRequestTime>60000){
					counts.remove(ip);
				}
			}
			keys.clear();
			keys=null;
		}
	}
}
