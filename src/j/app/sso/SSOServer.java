package j.app.sso;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import j.app.Constants;
import j.app.webserver.JHandler;
import j.app.webserver.JSession;
import j.cache.CachedMap;
import j.cache.JCacheParams;
import j.common.JObject;
import j.http.JHttp;
import j.log.Logger;
import j.security.Verifier;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilMD5;
import j.util.JUtilMath;
import j.util.JUtilString;
import j.util.JUtilUUID;

/**
 * @author 肖炯
 * 
 */
public class SSOServer extends JHandler implements Runnable{
	private static Logger log=Logger.create(SSOServer.class);
	private static CachedMap users=null;
	
	static{
		SSOServer instance=new SSOServer();
		Thread thread=new Thread(instance);
		thread.start();
		log.log("SSOServer started",-1);
		
		_init();
	}
	
	/**
	 * 
	 *
	 */
	private static void _init(){		
		try{
			users=new CachedMap(Constants.SSO_LOGIN_STATUS_CACHE);
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
	 * @param globalSessionId
	 * @param loginStatus
	 * @throws Exception
	 */
	protected static void saveLoginStatus(String globalSessionId,LoginStatus loginStatus) throws Exception{
		users.addOne(globalSessionId,loginStatus);
	}

	/**
	 * 
	 * @param loginStatus
	 */
	public static void refreshLoginStatus(LoginStatus loginStatus) {
		try{
			users.addOne(loginStatus.getGlobalSessionId(),loginStatus);
		}catch(Exception ex){
			log.log(ex,Logger.LEVEL_ERROR);
		}
	}

	/**
	 * 
	 * @param updater
	 */
	public static void updateLoginStatus(LoginStatusUpdater updater) {
		if(users==null) return;
		try{
			JCacheParams params=new JCacheParams();
			params.updater=updater;
			users.update(params);
			params=null;
		}catch(Exception ex){
			log.log(ex,Logger.LEVEL_ERROR);
		}
	}
	
	/**
	 * @deprecated
	 * @param globalSessionIdOrUid
	 * @return
	 */
	public static LoginStatus findLoginStatus(String globalSessionIdOrUid){
		if(globalSessionIdOrUid==null
				||globalSessionIdOrUid.equals("")) return null;
		LoginStatus stat=findLoginStatusOfSessionId(globalSessionIdOrUid);
		if(stat!=null){
			return stat;
		}
		
		LoginStatus[] arr=findLoginStatusOfUserId(globalSessionIdOrUid);
		return arr!=null&&arr.length>0?arr[0]:null;
	}
	
	
	/**
	 * 
	 * @param globalSessionId
	 * @return
	 */
	public static LoginStatus findLoginStatusOfSessionId(String globalSessionId){
		if(globalSessionId==null
				||globalSessionId.equals("")) return null;
		LoginStatus stat=null;
		try{
			stat=(LoginStatus)users.get(new JCacheParams(globalSessionId));
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
		}
		return stat;
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public static LoginStatus[] findLoginStatusOfUserId(String userId){
		if(userId==null
				||userId.equals("")) return null;
		
		try{
			List temp=users.values(new JCacheParams(new LoginStatusFilter(userId)));
			LoginStatus[] arr=new LoginStatus[temp.size()];
			temp.toArray(arr);
			
			return arr;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
		}
		return null;
	}
	
	/**
	 * 
	 * @param userId
	 * @param subUserId
	 * @return
	 */
	public static LoginStatus[] findLoginStatusOfUserId(String userId, String subUserId){
		if(userId==null
				||userId.equals("")) return null;
		
		try{
			List temp=users.values(new JCacheParams(new LoginStatusFilter(userId, subUserId)));
			LoginStatus[] arr=new LoginStatus[temp.size()];
			temp.toArray(arr);
			
			return arr;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
		}
		return null;
	}
	
	//LoginStatusFilter

	/**
	 * 
	 * @param session
	 * @return
	 */
	public static LoginStatus findLoginStatus(HttpSession session){
		return findLoginStatusOfSessionId((String)session.getAttribute(Constants.SSO_GLOBAL_SESSION_ID_ON_SERVER));
	}
	
	/**
	 * 
	 * @param client
	 * @param loginStatus
	 * @param notifyAll
	 */
	private static void login(Client client,LoginStatus loginStatus){		
		//当前操作的SSO Client直接发送登录信息
		SSONotifier.getNotifier(client).login(client,
				loginStatus.getGlobalSessionId(),
				loginStatus.getUserId(),
				loginStatus.getSubUserId(),
				loginStatus.getUserIp());
		
		//其它SSO Client通过通知线程发送登录信息
		ConcurrentList ssoClients=SSOConfig.getSsoClients();
		for(int i=0;i<ssoClients.size();i++){
			Client c=(Client)ssoClients.get(i);
			
			if(c.getId().equals(client.getId())) continue;//当前操作的SSO Client直接发送登录信息
			
			SSONotifier.addTask(c,
					loginStatus.getGlobalSessionId(),
					loginStatus.getUserId(),
					loginStatus.getSubUserId(),
					loginStatus.getUserIp(),
					SSONotifier.type_login);
		}
	}

	
	/**
	 * 注销sso server端信息并通知sso client端注销
	 * @param client
	 * @param loginStatus
	 * @throws Exception
	 */
	protected static void logout(Client client,LoginStatus loginStatus) throws Exception{
		HttpSession session=SSOContext.getSession(loginStatus.getSessionId());
		if(session!=null){//移除session中保存的全局会话ID
			try{
				session.removeAttribute(Constants.SSO_GLOBAL_SESSION_ID);				
			}catch(Exception e){
				log.log(e.getMessage(),Logger.LEVEL_INFO);
			}
		}
		
		//从缓存中清除
		users.remove(new JCacheParams(loginStatus.getGlobalSessionId()));
		
		//当前操作的SSO Client直接发送注销命令
//		SSONotifier.getNotifier(client).logout(client,
//				loginStatus.getGlobalSessionId(),
//				loginStatus.getUserId(),
//				loginStatus.getSubUserId(),
//				loginStatus.getUserIp());
		
		//其它SSO Client通过通知线程发送注销命令
		ConcurrentList ssoClients=SSOConfig.getSsoClients();
		for(int i=0;i<ssoClients.size();i++){
			Client c=(Client)ssoClients.get(i);
			
//			if(c.getId().equals(client.getId())) continue;//当前操作的SSO Client不发送注销命令
			
			SSONotifier.addTask(c,
					loginStatus.getGlobalSessionId(),
					loginStatus.getUserId(),
					loginStatus.getSubUserId(),
					loginStatus.getUserIp(),
					SSONotifier.type_logout,
					"true");
		}
		
		loginStatus=null;//set to null
	}
	
	/**
	 * 与验证码关联的UUID
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void sso_verifier_uuid(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String scriptType=SysUtil.getHttpParameter(request,"type");
		String form=SysUtil.getHttpParameter(request,"form");
		String sn=SysUtil.getHttpParameter(request,"sn");
		String sid=SysUtil.getHttpParameter(request,Constants.SSO_CLIENT_SESSION_ID);

		String uuid=sid==null||"".equals(sid)?JHttp.getRemoteIp(request):sid;
		if(JUtilMath.isInt(sn)&&Integer.parseInt(sn)>=0&&Integer.parseInt(sn)<10){
			uuid+=":"+sn;
		}
		uuid=JUtilMD5.MD5EncodeToHex(uuid);
		
		uuid=Verifier.allotUuid(uuid);
		
		String script="";
		if("variable".equalsIgnoreCase(scriptType)){//输出js变量
			script+="var "+Constants.SSO_VERIFIER_UUID+"='"+uuid+"';";
		}else if("input".equalsIgnoreCase(scriptType)){//输出input对象
			script+=form+"."+Constants.SSO_VERIFIER_UUID+".value='"+uuid+"';";
		}else{//两者兼有
			script+="var "+Constants.SSO_VERIFIER_UUID+"='"+uuid+"';\r\n";
			script+=form+"."+Constants.SSO_VERIFIER_UUID+".value='"+uuid+"';";
		}
		
		jsession.resultString=script;
	}
	
	/**
	 * 输出验证码
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void sso_verifier_code(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String uuid=(String)SysUtil.getHttpParameter(request,Constants.SSO_VERIFIER_UUID);
		if(uuid==null||!Verifier.allotted(uuid)){//必须先通过sso_verifier_uuid请求与验证码关联的UUID
			jsession.resultString="";
		}else{			
			Verifier.writeImage(uuid,response);
		}
	}
	
	
	/**
	 * 登录
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void ssologin(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		if(!SSOConfig.isServer()){//不是sso server端
			request.setAttribute(Constants.SSO_MSG,Constants.SSO_SERVICE_UNAVAILABLE);
			jsession.result="error";
			return;
		}
		
		//登录类型
		String loginType=SysUtil.getHttpParameter(request,Constants.SSO_LOGIN_TYPE,"");	
		
		//是否SSO Client
		String clientUrlPrefix=SysUtil.getHttpParameter(request,Constants.SSO_CLIENT);	
		
		//SSO CLIENT SESSION ID
		String clientSessionId=SysUtil.getHttpParameter(request,Constants.SSO_CLIENT_SESSION_ID);	
		
		Client client=SSOConfig.getSsoClientByIdOrUrl(clientUrlPrefix);
		
		String loginFromDomain=JUtilString.getHost(clientUrlPrefix);
		
		if(client==null||!client.canLogin()){//不是sso client
			request.setAttribute(Constants.SSO_MSG,Constants.SSO_BAD_CLIENT);
			jsession.result="error";
			return;
		}
		if(clientUrlPrefix.indexOf("http")<0) clientUrlPrefix=client.getUrlDefault();
		
		if(!client.compatible()){
			String token=SysUtil.getHttpParameter(request,Constants.SSO_TOKEN);
			if(token==null||token.equals("")){
				request.setAttribute(Constants.SSO_MSG,Constants.SSO_BAD_TOKEN);
				jsession.result="error";
				return;
			}
			
			String _token=SSOConfig.getLoginToken(client, request);
			if(!_token.equalsIgnoreCase(token)){
				request.setAttribute(Constants.SSO_MSG,Constants.SSO_BAD_TOKEN);
				jsession.result="error";
				return;
			}
		}
		
		//是否使用代理验证
		Client agent=null;
		String loginAgent=SysUtil.getHttpParameter(request,Constants.SSO_LOGIN_AGENT);
		//log.log("loginAgent:"+loginAgent, -1);
		if(loginAgent!=null&&!"".equals(loginAgent)){
			agent=SSOConfig.getSsoClientByIdOrUrl(loginAgent);
			//log.log("agent:"+agent, -1);
			if(agent==null||!agent.available(loginAgent)){//代理不存在、不可用或不为该SSO Client提供服务
				request.setAttribute(Constants.SSO_MSG,Constants.SSO_BAD_AGENT);
				jsession.result="error";
				return;
			}
		}
		
		
		//返回的地址
		String back=SysUtil.getHttpParameter(request,Constants.SSO_BACK_URL);
		if(back!=null){
			try{
				back=JUtilString.decodeURI(back,SysConfig.sysEncoding);
			}catch(Exception e){}
			
//			if(back.startsWith("http")){
//				Client clientOfBack=SSOConfig.getSsoClientByIdOrUrl(back);
//				if(clientOfBack==null||!clientOfBack.getId().equals(client.getId())){//返回地址不合法
//					request.setAttribute(Constants.SSO_MSG,Constants.SSO_BAD_CLIENT);
//					jsession.result="error";
//					return;
//				}
//			}
		}else{
			back=client.getHomePage();
		}
		back=SSOConfig.getAbsoluteUrl(client,clientUrlPrefix,back);
		
		
		//登录页面
		String loginPage=SysUtil.getHttpParameter(request,Constants.SSO_LOGIN_PAGE);
		String loginPageDefined=loginPage;
		if(loginPage!=null){
			try{
				loginPage=JUtilString.decodeURI(loginPage,SysConfig.sysEncoding);
			}catch(Exception e){}
		}else{
			loginPage=client.getLoginPage();
		}
		//loginPage=SSOConfig.getAbsoluteUrl(client,clientUrlPrefix,loginPage);
		loginPage=SSOConfig.getAbsoluteUrlSameDomainOfFromUrl(back,loginPage);

		LoginResult result=null;//登录结果，如登录成功LoginResult必须设置userId			
		try{
			String verifierUuid=SysUtil.getHttpParameter(request,Constants.SSO_VERIFIER_UUID);
			String verifierCode=SysUtil.getHttpParameter(request,Constants.SSO_VERIFIER_CODE);
			
			
			//如果启用了登录验证码，判断验证码是否正确
			if(SSOConfig.getVerifierCodeEnabled()
					||(verifierCode!=null&&!"".equals(verifierCode))){//统一判断验证码
				if(!Verifier.isCorrect(verifierUuid,verifierCode)) {
					result=new LoginResult();
					result.setResult(LoginResult.RESULT_VERIFIER_CODE_INCORRECT);
				}
			}
			
			if(result==null) {
				if((SSOConfig.getSsoClients().size()==1
						&&JUtilString.getHost(back).equalsIgnoreCase(SysUtil.getHttpDomain(request)))
						||agent==null) {
					//log.log("only one sso client and back host equals request host, so log in locally(SSOConfig.getAuthenticator().login).", -1);
					String ip=JHttp.getRemoteIp(request);
					result=SSOConfig.getAuthenticator().login(request,session,ip);	
					//log.log("only one sso client and back host equals request host, so log in locally(SSOConfig.getAuthenticator().login), finished.", -1);
				}else {
					result=agent.login(client.getId(),request);
				}
			}
			
			if(result==null){//验证出现错误（无验证结果）
				result=new LoginResult();
				result.setResult(LoginResult.RESULT_ERROR);
			}
	
			if(result.getResult()==LoginResult.RESULT_PASSED){//登录成功	
				//注销该session中已登录的用户
				String globalSessionIdOld=(String)session.getAttribute(Constants.SSO_GLOBAL_SESSION_ID_ON_SERVER);
				if(globalSessionIdOld!=null){
					LoginStatus loginStatusOld=SSOServer.findLoginStatusOfSessionId(globalSessionIdOld);
					if(loginStatusOld!=null){
						logout(client,loginStatusOld);
					}
				}
				
				//该用户如在别处登录了，先注销
				if(!"none".equalsIgnoreCase(SSOConfig.getLogoutOtherSessions())){
					//log.log("try logout userId"+result.getUserId()+", subUserId:"+result.getSubUserId(), -1);
					LoginStatus[] loginStatusOlds=SSOServer.findLoginStatusOfUserId(result.getUserId(), result.getSubUserId());
					if(loginStatusOlds!=null){
						for(int i=0;i<loginStatusOlds.length;i++){
							if("all".equalsIgnoreCase(SSOConfig.getLogoutOtherSessions())
									||loginFromDomain.equals(loginStatusOlds[i].getLoginFromDomain())){
								logout(client,loginStatusOlds[i]);
							}
						}
					}
				}
				
				
				String globalSessionId=JUtilUUID.genUUID();				
				session.setAttribute(Constants.SSO_GLOBAL_SESSION_ID_ON_SERVER,globalSessionId);
				LoginStatus loginStatus=new LoginStatus(client.getId(),
						session,
						globalSessionId,
						result.getUserId(),
						JHttp.getRemoteIp(request),
						SysConfig.getSysId(),
						SysConfig.getMachineID(),
						result.getSysId(),
						loginFromDomain);
				loginStatus.setSubUserId(result.getSubUserId());
				
				//自定义信息
				Map messages=result.getMessages();
				for(Iterator keys=messages.keySet().iterator();keys.hasNext();){
					Object key=keys.next();
					Object val=messages.get(key);
					loginStatus.setMessage(key,val);
				}
				
				users.addOne(globalSessionId,loginStatus);
				
				//返回client的登录接口
				String redirect=back;
				
				//如果只有一个客户端，且当前客户访问域名与sso server域名相同，不需要通知客户端，直接登录
				if(SSOConfig.getSsoClients().size()==1
						&&JUtilString.getHost(back).equalsIgnoreCase(SysUtil.getHttpDomain(request))) {
					//log.log("only one sso client and back host equals request host, so log in locally(SSOClient.ssologin).", -1);
					//本地登录
					redirect=SSOClient.ssologin(session, 
							request, 
							back,
							loginPage,
							loginStatus);
					
					if(redirect.equals(loginPage)
							&&loginPageDefined!=null) {
						if(redirect.indexOf("?")>0){
							redirect+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(loginPageDefined,SysConfig.sysEncoding);
						}else{
							redirect+="?"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(loginPageDefined,SysConfig.sysEncoding);
						}
					}
					
					//log.log("如果只有一个客户端，且当前客户访问域名与sso server域名相同，不需要通知客户端，直接登录: backurl:"+back+", redirect:"+redirect, -1);
					
					SysUtil.redirectByFormSubmit(client,request,response,redirect,messages);
				}else {
					//通知客户端
					login(client,loginStatus);
					
					if(redirect.indexOf("?")>0){
						redirect+="&"+Constants.SSO_GLOBAL_SESSION_ID+"="+loginStatus.getGlobalSessionId();
					}else{
						redirect+="?"+Constants.SSO_GLOBAL_SESSION_ID+"="+loginStatus.getGlobalSessionId();
					}
					redirect+="&"+Constants.SSO_LOGIN_FROM_SYS_ID+"="+loginStatus.getLoginFrom();
					redirect+="&"+Constants.SSO_USER_ID+"="+loginStatus.getUserId();
					redirect+="&"+Constants.SSO_USER_IP+"="+loginStatus.getUserIp();
					redirect+="&"+Constants.SSO_USER_ID+"="+loginStatus.getSubUserId();
					
					//redirect+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(back,SysConfig.sysEncoding);		
					if(loginPageDefined!=null){
						redirect+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(loginPageDefined,SysConfig.sysEncoding);
					}
					
					SysUtil.redirectByFormSubmit(client,request,response,redirect,messages);
				}
			}else{//登录失败
				if(loginPage.indexOf("?")>0){
					loginPage+="&"+Constants.SSO_LOGIN_RESULT_CODE+"="+result.getResult();
				}else{
					loginPage+="?"+Constants.SSO_LOGIN_RESULT_CODE+"="+result.getResult();
				}
				loginPage+="&"+Constants.SSO_LOGIN_CHANCES+"="+result.getChances();
				loginPage+="&"+Constants.SSO_LOGIN_TYPE+"="+loginType;
				loginPage+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(back,SysConfig.sysEncoding);
				if(loginPageDefined!=null){
					loginPage+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(loginPageDefined,SysConfig.sysEncoding);
				}
				
				SysUtil.redirectByFormSubmit(client,request,response,loginPage,result.getMessages());
			}
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			
			if(result!=null&&result.getResult()==LoginResult.RESULT_PASSED){
				try{
					//注销该session中已登录的用户
					String globalSessionIdOld=(String)session.getAttribute(Constants.SSO_GLOBAL_SESSION_ID_ON_SERVER);
					if(globalSessionIdOld!=null){
						LoginStatus loginStatusOld=SSOServer.findLoginStatusOfSessionId(globalSessionIdOld);
						if(loginStatusOld!=null) logout(client,loginStatusOld);
					}
					
					//该用户如在别处登录了，先注销
//					LoginStatus loginStatusOld=SSOServer.findLoginStatus(result.getUserId());
//					if(loginStatusOld!=null){
//						logout(client,loginStatusOld);
//					}
				}catch(Exception ex){
					log.log(e,Logger.LEVEL_ERROR);
				}
			}

			if(loginPage.indexOf("?")>0){
				loginPage+="&"+Constants.SSO_LOGIN_RESULT_CODE+"="+result.getResult();
			}else{
				loginPage+="?"+Constants.SSO_LOGIN_RESULT_CODE+"="+result.getResult();
			}
			loginPage+="&"+Constants.SSO_LOGIN_TYPE+"="+loginType;
			loginPage+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(back,SysConfig.sysEncoding);
			if(loginPageDefined!=null){
				loginPage+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(loginPageDefined,SysConfig.sysEncoding);
			}
			
			SysUtil.redirectByFormSubmit(client,request,response,loginPage,null);
		}
	}
	
	
	/**
	 * 登录
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void ssologinauto(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		if(!SSOConfig.isServer()){//不是sso server端
			jsession.resultString=Constants.SSO_SERVICE_UNAVAILABLE;
			return;
		}
		
		//是否SSO Client
		String clientUrlPrefix=SysUtil.getHttpParameter(request,Constants.SSO_CLIENT);	
		
		String loginFromDomain=JUtilString.getHost(clientUrlPrefix);
		
		Client client=SSOConfig.getSsoClientByIdOrUrl(clientUrlPrefix);
		if(client==null||!client.canLogin()){//不是sso client
			jsession.resultString=Constants.SSO_BAD_CLIENT;
			return;
		}
		if(clientUrlPrefix.indexOf("http")<0) clientUrlPrefix=client.getUrlDefault();
			
		try{
			String ssoUserId=SysUtil.getHttpParameter(request,Constants.SSO_USER_ID);
			String ssoSubUserId=SysUtil.getHttpParameter(request,Constants.SSO_SUB_USER_ID);
			
			//该用户如在别处登录了，先注销
			if(!"none".equalsIgnoreCase(SSOConfig.getLogoutOtherSessions())){
				LoginStatus[] loginStatusOlds=SSOServer.findLoginStatusOfUserId(ssoUserId, ssoSubUserId);
				if(loginStatusOlds!=null){
					for(int i=0;i<loginStatusOlds.length;i++){
						if("all".equalsIgnoreCase(SSOConfig.getLogoutOtherSessions())
								||loginFromDomain.equals(loginStatusOlds[i].getLoginFromDomain())){
							logout(client,loginStatusOlds[i]);
						}
					}
				}
			}
			
			String globalSessionId=JUtilUUID.genUUID();				
			LoginStatus loginStatus=new LoginStatus(client.getId(),
					session,
					globalSessionId,
					ssoUserId,
					JHttp.getRemoteIp(request),
					SysConfig.getSysId(),
					SysConfig.getMachineID(),
					SysConfig.getSysId(),
					loginFromDomain);		
			loginStatus.setSubUserId(ssoSubUserId);
			
			String infos=SysUtil.getHttpParameter(request, Constants.SSO_LOGIN_INFO);
			if(infos!=null){
				Map messages=(ConcurrentMap)JObject.string2Serializable(infos);
				for(Iterator keys=messages.keySet().iterator();keys.hasNext();){
					Object key=keys.next();
					Object val=messages.get(key);
					loginStatus.setMessage(key,val);
				}
			}
			
			users.addOne(globalSessionId,loginStatus);
			
			String token=JUtilUUID.genUUID();
			SSOContext.addToken(loginStatus.getGlobalSessionId(),token,ssoUserId);
			
			jsession.resultString=Constants.RESPONSE_OK+":"+loginStatus.getGlobalSessionId()+":"+token;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.RESPONSE_ERR;
		}
	}
	
	
	/**
	 * 登录
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void jump(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		if(!SSOConfig.isServer()){//不是sso server端
			request.setAttribute(Constants.SSO_MSG,Constants.SSO_SERVICE_UNAVAILABLE);
			jsession.result="error";
			return;
		}
		
		//是否SSO Client
		String clientUrlPrefix=SysUtil.getHttpParameter(request,Constants.SSO_CLIENT);	
		Client client=SSOConfig.getSsoClientByIdOrUrl(clientUrlPrefix);
		if(client==null){//不是sso client
			request.setAttribute(Constants.SSO_MSG,Constants.SSO_BAD_CLIENT);
			jsession.result="error";
			return;
		}
		if(clientUrlPrefix.indexOf("http")<0) clientUrlPrefix=client.getUrlDefault();
		
		
		//返回的地址
		String back=SysUtil.getHttpParameter(request,Constants.SSO_BACK_URL);
		if(back!=null){
			try{
				back=JUtilString.decodeURI(back,SysConfig.sysEncoding);
			}catch(Exception e){}
			
//			if(back.startsWith("http")){
//				Client clientOfBack=SSOConfig.getSsoClientByIdOrUrl(back);
//				if(clientOfBack==null||!clientOfBack.getId().equals(client.getId())){//返回地址不合法
//					request.setAttribute(Constants.SSO_MSG,Constants.SSO_BAD_CLIENT);
//					jsession.result="error";
//					return;
//				}
//			}
		}else{
			back=client.getHomePage();
		}
		back=SSOConfig.getAbsoluteUrl(client,clientUrlPrefix,back);
		
		
		//登录页面
		String loginPage=SysUtil.getHttpParameter(request,Constants.SSO_LOGIN_PAGE);
		String loginPageDefined=loginPage;
		if(loginPage!=null){
			try{
				loginPage=JUtilString.decodeURI(loginPage,SysConfig.sysEncoding);
			}catch(Exception e){}
		}else{
			loginPage=client.getLoginPage();
		}
		//loginPage=SSOConfig.getAbsoluteUrl(client,clientUrlPrefix,loginPage);
		loginPage=SSOConfig.getAbsoluteUrlSameDomainOfFromUrl(back,loginPage);
		
		LoginStatus loginStatus=SSOServer.findLoginStatus(session);
		if(loginStatus!=null&&!loginStatus.isTimeout()){//如果已经登录
			SSOContext.removeToken(loginStatus.getGlobalSessionId());//移除token
			
			//再次发送登录信息
			SSONotifier.getNotifier(client).login(client,
					loginStatus.getGlobalSessionId(),
					loginStatus.getUserId(),
					loginStatus.getSubUserId(),
					loginStatus.getUserIp());
			
			//返回client的登录接口
			String redirect=back;//clientUrlPrefix+client.getLoginInterface();
			if(redirect.indexOf("?")>0){
				redirect+="&"+Constants.SSO_GLOBAL_SESSION_ID+"="+loginStatus.getGlobalSessionId();
			}else{
				redirect+="?"+Constants.SSO_GLOBAL_SESSION_ID+"="+loginStatus.getGlobalSessionId();
			}
			redirect+="&"+Constants.SSO_LOGIN_FROM_SYS_ID+"="+loginStatus.getLoginFrom();
			redirect+="&"+Constants.SSO_USER_ID+"="+loginStatus.getUserId();
			redirect+="&"+Constants.SSO_USER_IP+"="+loginStatus.getUserIp();
			//redirect+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(back,SysConfig.sysEncoding);
			if(loginPageDefined!=null){
				loginPage+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(loginPageDefined,SysConfig.sysEncoding);
			}

			SysUtil.redirectByFormSubmit(client,request,response,redirect,loginStatus.getMessages());
			return;
		}
			
		try{
			String globalSessionId=SysUtil.getHttpParameter(request,Constants.SSO_GLOBAL_SESSION_ID);
			
			Object[] tokenInfo=SSOContext.getToken(globalSessionId);
			String token=tokenInfo==null?null:(String)tokenInfo[0];
			loginStatus=globalSessionId==null||token==null?null:SSOServer.findLoginStatusOfSessionId(globalSessionId);
			
			if(token==null||loginStatus==null){//未登录过
				if(loginPage.indexOf("?")>0){
					loginPage+="&"+Constants.SSO_LOGIN_RESULT_CODE+"="+LoginResult.RESULT_FAILED;
				}else{
					loginPage+="?"+Constants.SSO_LOGIN_RESULT_CODE+"="+LoginResult.RESULT_FAILED;
				}
				loginPage+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(back,SysConfig.sysEncoding);
				if(loginPageDefined!=null){
					loginPage+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(loginPageDefined,SysConfig.sysEncoding);
				}
				
				SysUtil.redirectByFormSubmit(client,request,response,loginPage,null);
				return;
			}
		
			session.setAttribute(Constants.SSO_GLOBAL_SESSION_ID_ON_SERVER,globalSessionId);
			
			login(client,loginStatus);//通知客户端
			
			//返回client的登录接口
			String redirect=back;//clientUrlPrefix+client.getLoginInterface();
			if(redirect.indexOf("?")>0){
				redirect+="&"+Constants.SSO_GLOBAL_SESSION_ID+"="+loginStatus.getGlobalSessionId();
			}else{
				redirect+="?"+Constants.SSO_GLOBAL_SESSION_ID+"="+loginStatus.getGlobalSessionId();
			}
			redirect+="&"+Constants.SSO_LOGIN_FROM_SYS_ID+"="+loginStatus.getLoginFrom();
			redirect+="&"+Constants.SSO_USER_ID+"="+loginStatus.getUserId();
			redirect+="&"+Constants.SSO_USER_IP+"="+loginStatus.getUserIp();
			//redirect+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(back,SysConfig.sysEncoding);
			if(loginPageDefined!=null){
				loginPage+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(loginPageDefined,SysConfig.sysEncoding);
			}
			
			SysUtil.redirectByFormSubmit(client,request,response,redirect,loginStatus.getMessages(),"");
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.result="error";
			return;
		}
	}
	
	/**
	 * 
	 * 当用户访问client，需要登录而未登录时，跳转到sso server，执行ssoquery方法，
	 * 如果已经登录，则返回client的登录接口以通知其登录该用户，如未登录则转向登录页面
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void ssoquery(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		if(!SSOConfig.isServer()){//不是sso server端
			request.setAttribute(Constants.SSO_MSG,Constants.SSO_SERVICE_UNAVAILABLE);
			jsession.result="error";
			return;
		}
		
		
		//是否SSO Client
		String clientUrlPrefix=SysUtil.getHttpParameter(request,Constants.SSO_CLIENT);		
		Client client=SSOConfig.getSsoClientByIdOrUrl(clientUrlPrefix);
		if(client==null||!client.canLogin()){//不是sso client
			request.setAttribute(Constants.SSO_MSG,Constants.SSO_BAD_CLIENT);
			jsession.result="error";
			return;
		}
		
		//返回的地址
		String back=SysUtil.getHttpParameter(request,Constants.SSO_BACK_URL);
		if(back!=null){
			try{
				back=JUtilString.decodeURI(back,SysConfig.sysEncoding);
			}catch(Exception e){}
		}else{
			back=client.getHomePage();
		}
		//loginPage=SSOConfig.getAbsoluteUrl(client,clientUrlPrefix,loginPage);
		back=SSOConfig.getAbsoluteUrl(client,clientUrlPrefix,back);
		
		
		//登录页面
		String loginPage=SysUtil.getHttpParameter(request,Constants.SSO_LOGIN_PAGE);
		String loginPageDefined=loginPage;
		if(loginPage!=null){
			try{
				loginPage=JUtilString.decodeURI(loginPage,SysConfig.sysEncoding);
			}catch(Exception e){}
		}else{
			loginPage=client.getLoginPage();
		}
		loginPage=SSOConfig.getAbsoluteUrlSameDomainOfFromUrl(back,loginPage);
		
		
		try{				
			LoginStatus loginStatus=SSOServer.findLoginStatus(session);
			if(loginStatus!=null&&!loginStatus.isTimeout()){//如果已经登录				
				//再次发送登录信息
				SSONotifier.getNotifier(client).login(client,
						loginStatus.getGlobalSessionId(),
						loginStatus.getUserId(),
						loginStatus.getSubUserId(),
						loginStatus.getUserIp());
				
				//返回client的登录接口
				String redirect=back;//clientUrlPrefix+client.getLoginInterface();
				if(redirect.indexOf("?")>0){
					redirect+="&"+Constants.SSO_GLOBAL_SESSION_ID+"="+loginStatus.getGlobalSessionId();
				}else{
					redirect+="?"+Constants.SSO_GLOBAL_SESSION_ID+"="+loginStatus.getGlobalSessionId();
				}
				redirect+="&"+Constants.SSO_LOGIN_FROM_SYS_ID+"="+loginStatus.getLoginFrom();
				redirect+="&"+Constants.SSO_USER_ID+"="+loginStatus.getUserId();
				if(loginStatus.getSubUserId()!=null) {
					redirect+="&"+Constants.SSO_SUB_USER_ID+"="+loginStatus.getSubUserId();
				}
				redirect+="&"+Constants.SSO_USER_IP+"="+loginStatus.getUserIp();
				//redirect+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(back,SysConfig.sysEncoding);
				if(loginPageDefined!=null){
					loginPage+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(loginPageDefined,SysConfig.sysEncoding);
				}
				
				if(loginPageDefined!=null&&loginPageDefined.indexOf(Constants.SSO_GET_LOGIN_STATUS_RESULT)>-1){
					SysUtil.redirectByFormSubmit(client,request,response,redirect,loginStatus.getMessages(),"","get");
				}else{
					SysUtil.redirectByFormSubmit(client,request,response,redirect,loginStatus.getMessages());
				}
			}else{//未登录
				if(loginStatus!=null){
					logout(client,loginStatus);//已经登录但过期，注销
				}
				
				if(loginPage.indexOf("?")>0){
					loginPage+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(back,SysConfig.sysEncoding);
				}else{
					loginPage+="?"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(back,SysConfig.sysEncoding);
				}			
				if(loginPageDefined!=null){
					loginPage+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(loginPageDefined,SysConfig.sysEncoding);
				}

				if(loginPageDefined!=null&&loginPageDefined.indexOf(Constants.SSO_GET_LOGIN_STATUS_RESULT)>-1){
					SysUtil.redirectByFormSubmit(client,request,response,loginPage,null,"","get");
				}else{
					SysUtil.redirectByFormSubmit(client,request,response,loginPage,null);
				}
			}
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			
			if(loginPage.indexOf("?")>0){
				loginPage+="&"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(back,SysConfig.sysEncoding);
			}else{
				loginPage+="?"+Constants.SSO_BACK_URL+"="+JUtilString.encodeURI(back,SysConfig.sysEncoding);
			}	
			if(loginPageDefined!=null){
				loginPage+="&"+Constants.SSO_LOGIN_PAGE+"="+JUtilString.encodeURI(loginPageDefined,SysConfig.sysEncoding);
			}
			
			SysUtil.redirectByFormSubmit(client,request,response,loginPage,null);
		}
	}

	/**
	 * 注销
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void ssologout(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		if(!SSOConfig.isServer()){//不是sso server端
			request.setAttribute(Constants.SSO_MSG,Constants.SSO_SERVICE_UNAVAILABLE);
			jsession.result="error";
			return;
		}
		
		
		//是否SSO Client
		String clientUrlPrefix=SysUtil.getHttpParameter(request,Constants.SSO_CLIENT);		
		Client client=SSOConfig.getSsoClientByIdOrUrl(clientUrlPrefix);
		if(client==null||!client.canLogin()){//如果未指定sso client，则默认本应用
			client=SSOConfig.getSsoClientByIdOrUrl(SysConfig.getSysId());
		}
		
		//返回的地址
		String back=SysUtil.getHttpParameter(request,Constants.SSO_BACK_URL);
		if(back!=null){
			try{
				back=JUtilString.decodeURI(back,SysConfig.sysEncoding);
			}catch(Exception e){}
		}else{
			back=client.getHomePage();
		}
		back=SSOConfig.getAbsoluteUrl(client,clientUrlPrefix,back);
		
		/*
		 * 如果参数SSO_GLOBAL_SESSION_ID不为空，则注销该session id对应的用户，
		 * 否则注销session中的用户（两者可能是统一的）：
		 * 比如用户在passport登录后，portal中有登录各client应用的链接（包含session id，user id等参数），
		 * 这时如果用户拷贝该链接，在新开browser窗口中访问该链接登录，则新开browser窗口中没有用户登录passport的session，
		 * 如果client的“退出”链接不带SSO_GLOBAL_SESSION_ID参数跳转到passport进行注销，
		 * 则新开browser窗口中passport的session里没有该用户的登录信息，不能成功注销，
		 * 所以，client的“退出”链接要带上SSO_GLOBAL_SESSION_ID参数
		 */
		String globalSessionId=SysUtil.getHttpParameter(request,Constants.SSO_GLOBAL_SESSION_ID);	
		if(globalSessionId!=null&&!"".equals(globalSessionId)){
			LoginStatus loginStatus=findLoginStatusOfSessionId(globalSessionId);
			if(loginStatus!=null){
				logout(client,loginStatus);//注销
			}
		}else{			
			//注销该session中已登录的用户
			globalSessionId=(String)session.getAttribute(Constants.SSO_GLOBAL_SESSION_ID_ON_SERVER);
			if(globalSessionId!=null){
				LoginStatus loginStatus=SSOServer.findLoginStatusOfSessionId(globalSessionId);
				if(loginStatus!=null) logout(client,loginStatus);
			}
		}

		SysUtil.redirectByFormSubmit(client,request,response,back,null);
	}	

	
	/**
	 * sso client端调用此方法告知server注销某个用户
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void ssologoutuser(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		if(!SSOConfig.isServer()){//不是sso server端
			jsession.resultString=Constants.SSO_SERVICE_UNAVAILABLE;
			return;
		}
		
		//是否SSO Client
		String clientUrlPrefix=SysUtil.getHttpParameter(request,Constants.SSO_CLIENT);		
		Client client=SSOConfig.getSsoClientByIdOrUrl(clientUrlPrefix);
		if(client==null||!client.canLogin()){//不是sso client
			jsession.resultString=Constants.SSO_BAD_CLIENT;
			return;
		}

		String time=SysUtil.getHttpParameter(request,Constants.SSO_TIME);
		String globalSessionId=SysUtil.getHttpParameter(request,Constants.SSO_GLOBAL_SESSION_ID);
		String userId=SysUtil.getHttpParameter(request,Constants.SSO_USER_ID);
		String subUserId=SysUtil.getHttpParameter(request,Constants.SSO_SUB_USER_ID);
		String key=SysUtil.getHttpParameter(request,Constants.SSO_MD5_STRING);

		String md5=JUtilMD5.MD5EncodeToHex(client.getPassport()+time+globalSessionId+userId);
		if(!md5.equalsIgnoreCase(key)){//md5校验未通过
			jsession.resultString=Constants.RESPONSE_MD5_ERR;
			return;
		}
		
		LoginStatus[] loginStatus=SSOServer.findLoginStatusOfUserId(userId, subUserId);
		if(loginStatus==null || loginStatus.length==0) loginStatus=new LoginStatus[] {SSOServer.findLoginStatusOfSessionId(globalSessionId)};
		
		if(loginStatus!=null && loginStatus.length>0 && loginStatus[0]!=null) logout(client,loginStatus[0]);
		
		jsession.resultString=Constants.RESPONSE_OK;
	}	

	
	/**
	 * sso client端调用此方法告知server端客户最近活动的时间
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void ssoupdate(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		if(!SSOConfig.isServer()){//不是sso server端
			jsession.resultString=Constants.SSO_SERVICE_UNAVAILABLE;
			return;
		}
		
		
		//是否SSO Client
		String clientUrlPrefix=SysUtil.getHttpParameter(request,Constants.SSO_CLIENT);		
		Client client=SSOConfig.getSsoClientByIdOrUrl(clientUrlPrefix);
		if(client==null||!client.canLogin()){//不是sso client
			jsession.resultString=Constants.SSO_BAD_CLIENT;
			return;
		}

		
		String time=SysUtil.getHttpParameter(request,Constants.SSO_TIME);
		String updates=SysUtil.getHttpParameter(request,Constants.SSO_UPDATES);
		String key=SysUtil.getHttpParameter(request,Constants.SSO_MD5_STRING);
		String md5=JUtilMD5.MD5EncodeToHex(client.getPassport()+time+updates);
		if(!md5.equalsIgnoreCase(key)){//md5校验未通过
			jsession.resultString=Constants.RESPONSE_MD5_ERR;
			return;
		}
		
		//updates为更新内容，格式为：globalSessionId,updateTime;globalSessionId,updateTime......
		String[] arr=updates.split(";");
		for(int i=0;i<arr.length;i++){
			String globalSessionId=arr[i].substring(0,arr[i].indexOf(","));
			String updateTime=arr[i].substring(arr[i].indexOf(",")+1);
			LoginStatus stat=SSOServer.findLoginStatusOfSessionId(globalSessionId);
			if(stat!=null) stat.setUpdateTime(Long.parseLong(updateTime));//更新用户最近活动时间
		}

		jsession.resultString=Constants.RESPONSE_OK;
	}	

	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void ssoaddurl(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		if(!SSOConfig.isServer()){//不是sso server端
			jsession.resultString=Constants.SSO_SERVICE_UNAVAILABLE;
			return;
		}
		
		
		//是否SSO Client
		String clientId=SysUtil.getHttpParameter(request,Constants.SSO_CLIENT);		
		Client client=SSOConfig.getSsoClientByIdOrUrl(clientId);
		if(client==null){//不是sso client
			jsession.resultString=Constants.SSO_BAD_CLIENT;
			return;
		}

		String url=SysUtil.getHttpParameter(request,"url","");
		
		client.addUrl(url);
		
		jsession.resultString=Constants.RESPONSE_OK;
	}

	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void ssodelurl(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		if(!SSOConfig.isServer()){//不是sso server端
			jsession.resultString=Constants.SSO_SERVICE_UNAVAILABLE;
			return;
		}
		
		
		//是否SSO Client
		String clientId=SysUtil.getHttpParameter(request,Constants.SSO_CLIENT);		
		Client client=SSOConfig.getSsoClientByIdOrUrl(clientId);
		if(client==null){//不是sso client
			jsession.resultString=Constants.SSO_BAD_CLIENT;
			return;
		}

		String url=SysUtil.getHttpParameter(request,"url","-");
		
		client.delUrl(url);
		
		jsession.resultString=Constants.RESPONSE_OK;
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			try{
				Thread.sleep(15000);
			}catch(Exception ex){}
			
			if(!SSOConfig.isServer()) continue;
			
			//注销过期用户
			try{			
				JCacheParams params=new JCacheParams();
				params.valueFilter=LoginStatusRemover.getInstance();
				
				List values=users.values(params);
				for(int i=0;i<values.size();i++){
					LoginStatus loginStatus=(LoginStatus)values.get(i);
					if(loginStatus!=null&&loginStatus.isTimeout()){//超时
						try{
							logout(SSOConfig.getSsoClientByIdOrUrl(loginStatus.getClientId()),loginStatus);//注销
						}catch(Exception e){}
						
						if(loginStatus!=null) loginStatus=null;
					}
				}
				values.clear();
				values=null;
				
				users.remove(params);
				params=null;
			}catch(Exception ex){
				log.log(ex,Logger.LEVEL_ERROR);
			}
		}
	}
}