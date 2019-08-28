package j.app.sso;

import j.app.Constants;
import j.app.permission.Permission;
import j.app.webserver.Handlers;
import j.app.webserver.JHandler;
import j.app.webserver.JSession;
import j.cache.CachedMap;
import j.cache.JCacheParams;
import j.common.JObject;
import j.http.JHttp;
import j.http.JHttpContext;
import j.log.Logger;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.JUtilBean;
import j.util.JUtilDom4j;
import j.util.JUtilMD5;
import j.util.JUtilString;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.HttpClient;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 
 * @author 肖炯
 *
 */
public class SSOClient extends JHandler implements Runnable{	
	private static Logger log=Logger.create(SSOClient.class);
	private static CachedMap users=null;
	private static JHttp http=null;
	private static HttpClient hclient=null;
	public static LoginStatusUpdater updater=new LoginStatusUpdater();
	
	static{
		SSOClient instance=new SSOClient();
		Thread thread=new Thread(instance);
		thread.start();
		log.log("SSOClient started",-1);
	}

	/**
	 * 
	 */
	private static void _init(){
		synchronized(updater){
			try{
				if(users==null) users=new CachedMap(SysConfig.getSysId()+"."+SysConfig.getMachineID()+"."+Constants.SSO_LOGIN_STATUS_CACHE);
			}catch(Exception e){
				log.log(e,Logger.LEVEL_FATAL);
				try{
					Thread.sleep(5000);
				}catch(Exception ex){}
				_init();
			}
		}
	}

	/**
	 * 
	 * @param loginStatus
	 */
	public static void refreshLoginStatus(LoginStatus loginStatus) {
		_init();
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
		_init();
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
	 * 
	 *
	 */
	public SSOClient(){
		super();
		if(http==null){
			http=JHttp.getInstance();
		}
		if(hclient==null){
			hclient=http.createClient();
		}
	}
	
	/**
	 * 
	 * @param client
	 * @param request
	 * @return
	 */
	private static boolean verifySsoLogin(Client client,HttpServletRequest request){
		String verify=SysUtil.getHttpParameter(request,Constants.SSO_PVERIFY);
		String names=SysUtil.getHttpParameter(request,Constants.SSO_PNAMES);
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
	
	
	
	/**
	 * @deprecated
	 * 查找是否有给定参数所对应的登录信息
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
		_init();
		
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
	 * 查找是否有给定参数所对应的登录信息
	 * @param globalSessionIdOrUid
	 * @return
	 */
	public static LoginStatus[] findLoginStatusOfUserId(String userId){
		_init();
		
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
     * 保存sso客户端应用的LoginStatus到session中
     * @param session
     * @param loginStat
     */
	public static void setLoginStatus(HttpSession session,LoginStatus loginStat){
    	session.setAttribute(Constants.SSO_STAT_CLIENT,loginStat);
    }
	
	 
    /**
     * sso客户端应用保存在session中的LoginStatus
     * @param session
     * @return
     */
	public static LoginStatus getLoginStatus(HttpSession session){
    	return (LoginStatus)session.getAttribute(Constants.SSO_STAT_CLIENT);
    }
 
    /**
     * sso客户端应用的LoginStatus从session中移除
     * @param session
     * @return
     */
	private static void removeLoginStatus(HttpSession session){
    	try{
			Object obj=session.getAttribute(Constants.SSO_STAT_CLIENT);
	    	session.removeAttribute(Constants.SSO_STAT_CLIENT);
			if(obj!=null) obj=null;
    	}catch(Exception e){
    		log.log(e.getMessage(),Logger.LEVEL_WARNING);    		
    	}    	
    }
    
	
	
	/**
     * 保存当前用户到session中
	 * @param session
	 * @param user
	 */
	public static void setCurrentUser(HttpSession session,User user){
    	session.setAttribute(Constants.SSO_USER,user);
    }
	
    /**
     * 得到当前用户
     * @param session
     * @return
     */    
	public static User getCurrentUser(HttpSession session){
    	return session==null?null:(User)session.getAttribute(Constants.SSO_USER);
    } 
    
    /**
     * 从session中移除当前用户
     * @param session
     */
	private static void removeCurrentUser(HttpSession session){
    	try{
    		if(session==null) return;
    		
			Object obj=session.getAttribute(Constants.SSO_USER);
	    	session.removeAttribute(Constants.SSO_USER);
			if(obj!=null){
				if(obj instanceof User){
					User user=(User)obj;
					user.destroy();
				}
				obj=null;
			}
    	}catch(Exception e){
    		log.log(e.getMessage(),Logger.LEVEL_WARNING);
    	}
    }
	

	/**
	 * 保存登录信息
	 * @param session
	 * @param loginStatus
	 * @param user
	 */
	public static void saveUserInformation(HttpSession session,LoginStatus loginStatus,User user){
		if(session==null) return;
		
		setLoginStatus(session,loginStatus);
		setCurrentUser(session,user);						
		session.setAttribute(Constants.SSO_TIME,SysUtil.getNow()+"");
		session.setAttribute(Constants.SSO_USER_ID,loginStatus.getUserId());
		session.setAttribute(Constants.SSO_GLOBAL_SESSION_ID,loginStatus.getGlobalSessionId());
	}
	
	/**
	 * 注销该用户相关的client端登录信息
	 * @param globalSessionId
	 * @throws Exception
	 */
	private static void clearUserInformation(String globalSessionId) throws Exception{
		LoginStatus loginStatus=findLoginStatusOfSessionId(globalSessionId);
		clearUserInformation(loginStatus);
	}
	
	/**
	 * 
	 * @param loginStatus
	 * @throws Exception
	 */
	private static void clearUserInformation(LoginStatus loginStatus) throws Exception{
		_init();
		if(loginStatus!=null){
			HttpSession session=SSOContext.getSession(loginStatus.getSessionId());
			
			users.remove(new JCacheParams(loginStatus.getUserId()));
			users.remove(new JCacheParams(loginStatus.getGlobalSessionId()));
			
			if(session!=null){
				removeLoginStatus(session);
				removeCurrentUser(session);
				session.removeAttribute(Constants.SSO_TIME);
				session.removeAttribute(Constants.SSO_USER_ID);
				session.removeAttribute(Constants.SSO_GLOBAL_SESSION_ID);			
			}
			
			try{
				SSOConfig.getAuthenticator().logout(session);
			}catch(Exception e){
				log.log(e.getMessage(),Logger.LEVEL_WARNING);
			}
			
			if(loginStatus!=null) loginStatus=null;
		}
	}
	
	/**
	 * 
	 * @param session
	 * @throws Exception
	 */
	public static void clearUserInformationInSession(HttpSession session) throws Exception{
		if(session!=null){
			removeLoginStatus(session);
			removeCurrentUser(session);
			session.removeAttribute(Constants.SSO_TIME);
			session.removeAttribute(Constants.SSO_USER_ID);
			session.removeAttribute(Constants.SSO_GLOBAL_SESSION_ID);			
		}
		
		try{
			SSOConfig.getAuthenticator().logout(session);
		}catch(Exception e){
			log.log(e.getMessage(),Logger.LEVEL_WARNING);
		}
	}
	
	/**
	 * 注销全部用户的client信息
	 */
	private static void logoutAllUsers() throws Exception{
		_init();
		List values=users.values(null);
		for(int i=0;i<values.size();i++){
			LoginStatus loginStatus=(LoginStatus)values.get(i);
			if(loginStatus!=null){//超时
				clearUserInformation(loginStatus.getUserId());
			}
		}
		values.clear();
		values=null;
		users.clear();
	}
	
	
	/**
	 * 
	 * @param requestURL
	 * @param globalSessionId
	 * @param userId
	 * @throws Exception
	 */
	public static void tellServerToLogoutUser(String globalSessionId,String userId)throws Exception{
		if(globalSessionId==null&&userId==null) return;
		
		Client client=SSOConfig.getSsoClientByIdOrUrl(SysConfig.getSysId());
		String time=SysUtil.getNow()+"";
		String md5Key=JUtilMD5.MD5EncodeToHex(client.getPassport()+time+globalSessionId+userId);

		String logout=SSOConfig.getSsoServer()+"ssoserver"+Handlers.getActionPathPattern();
		logout+="?"+Handlers.getHandler("/ssoserver").getRequestBy()+"=ssologoutuser";		
		logout+="&"+Constants.SSO_CLIENT+"="+client.getUrlDefault();	
		logout+="&"+Constants.SSO_MD5_STRING+"="+md5Key;	
		logout+="&"+Constants.SSO_TIME+"="+time;
		logout+="&"+Constants.SSO_GLOBAL_SESSION_ID+"="+globalSessionId;
		logout+="&"+Constants.SSO_USER_ID+"="+userId;
		logout+="&"+Constants.SSO_PASSPORT+"="+Permission.getSSOPassport();
		
		JHttpContext context=http.get(null,hclient,logout);
		String _response=context.getStatus()==200?context.getResponseText():null;
		context.finalize();
		context=null;
		log.log("tellServerToLogoutUser - "+_response,-1);
	}
	
	/**
	 * 
	 * @param userId
	 * @throws Exception
	 */
	public static void logoutUserId(String userId)throws Exception{
		if(userId==null||"".equals(userId)) return;
		
		//log.log("logoutUserId:"+userId, -1);
		
		LoginStatus[] loginStatus=findLoginStatusOfUserId(userId);		
		if(loginStatus==null){			
			log.log("logoutUserId,loginStatus null:"+userId, -1);
			return;
		}
		
		for(int i=0;i<loginStatus.length;i++){
			log.log("logoutUserId["+loginStatus.length+" sessions],"+userId, -1);
			tellServerToLogoutUser(loginStatus[i].getGlobalSessionId(),loginStatus[i].getUserId());
		}
	}
	
	/**
	 * 
	 * @param globalSessionId
	 * @throws Exception
	 */
	public static void logoutGlobalSessionId(String globalSessionId)throws Exception{
		if(globalSessionId==null||"".equals(globalSessionId)) return;
		
		LoginStatus loginStatus=findLoginStatusOfSessionId(globalSessionId);		
		if(loginStatus==null) return;
		
		tellServerToLogoutUser(loginStatus.getGlobalSessionId(),loginStatus.getUserId());
	}
	
	/**
	 * 接收sso server端发出的某个会员登录的命令
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void ssologin(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		_init();
		String globalSessionId=SysUtil.getHttpParameter(request,Constants.SSO_GLOBAL_SESSION_ID);
		String userId=SysUtil.getHttpParameter(request,Constants.SSO_USER_ID);
		String userIp=SysUtil.getHttpParameter(request,Constants.SSO_USER_IP);	
		String loginFrom=SysUtil.getHttpParameter(request,Constants.SSO_LOGIN_FROM_SYS_ID);	
		String key=SysUtil.getHttpParameter(request,Constants.SSO_MD5_STRING);		
		Client client=SSOConfig.getSsoClientByIdOrUrl(SysConfig.getSysId());
		
		try{
			if(key!=null){//sso server端发送的指令
				String time=SysUtil.getHttpParameter(request,Constants.SSO_TIME);
				String md5=JUtilMD5.MD5EncodeToHex(client.getPassport()+time+globalSessionId+userId+userIp);
				if(md5.equalsIgnoreCase(key)){//md5校验通过
					//clearUserInformation(userId);//注销该用户client端的登录信息
					//log.log("login from server notify "+globalSessionId, -1);
					
					//保存新的登录信息
					LoginStatus loginStatus=new LoginStatus(client.getId(),
							null,
							globalSessionId,
							userId,
							userIp,
							SysConfig.getSysId(),
							SysConfig.getMachineID(),
							loginFrom,
							"");
					refreshLoginStatus(loginStatus);
					
					jsession.resultString=Constants.RESPONSE_OK;//返回给server处理结果
				}else{										
					jsession.resultString=Constants.RESPONSE_MD5_ERR;//返回给server处理结果
				}
			}else{//从用户浏览器过来的请求
				if(!verifySsoLogin(client,request)){
					jsession.resultString=Constants.RESPONSE_MD5_ERR;//返回给server处理结果
					return;
				}
				
				String back=SysUtil.getHttpParameter(request,Constants.SSO_BACK_URL);
				String loginPage=SysUtil.getHttpParameter(request,Constants.SSO_LOGIN_PAGE);	
				if(back!=null){
					try{
						back=JUtilString.decodeURI(back,SysConfig.sysEncoding);
					}catch(Exception e){}
				}else{
					back=client.getHomePage();
				}
				if(loginPage!=null){
					try{
						loginPage=JUtilString.decodeURI(loginPage,SysConfig.sysEncoding);
					}catch(Exception e){}
				}else{
					loginPage=client.getLoginPage();
				}

				LoginStatus loginStatus=findLoginStatusOfSessionId(globalSessionId);
				
				if(loginStatus==null){//未登录，直接返回
					log.log("Login Status is not found.", -1);
					SysUtil.redirect(request,response,loginPage);
				}else{//已经登录，加载用户信息
					User user=User.loadUser(session,request,loginStatus.getUserId());//加载用户信息
					
					if(user!=null){//加载用户信息成功	
						loginStatus.setSession(session);
						loginStatus.login();//确认登录
						loginStatus.setUpdateTime(SysUtil.getNow());
						loginStatus.setLoginFromDomain(SysUtil.getHttpDomain(request));
						loginStatus.setUserAgent(request.getHeader("User-Agent"));
						
						refreshLoginStatus(loginStatus);
							
						SSOClient.saveUserInformation(session,loginStatus,user);
						
						SysUtil.redirect(request,response,back);			
					}else{
						log.log("已经登录，但加载用户信息失败 - "+globalSessionId+","+loginStatus.getUserId()+","+loginStatus.getUserIp(),-1);
						SysUtil.redirect(request,response,loginPage);	
					}
				}
			}
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			if(key==null){//从用户浏览器过来的请求，出错返回错误页面
				SysUtil.redirect(request,response,SysConfig.errorPage);
			}else{//sso server端发送的指令，返回错误代码
				jsession.resultString=Constants.RESPONSE_ERR;
			}
		}
	}	
	
	/**
	 * 接收sso server端发出的注销某个会员或全部会员的命令
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void ssologout(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		Client client=SSOConfig.getSsoClientByIdOrUrl(SysConfig.getSysId());
		String key=SysUtil.getHttpParameter(request,Constants.SSO_MD5_STRING);
		String back=SysUtil.getHttpParameter(request,Constants.SSO_BACK_URL);
		if(back!=null){
			try{
				back=JUtilString.decodeURI(back,SysConfig.sysEncoding);
			}catch(Exception e){}
		}else{
			back=client.getHomePage();
		}
		try{
			/*
			 * “退出"链接必须是: 
			 * {sso client}/ssoclient.handler?request=ssologout
			 */
			if(key==null){//从用户浏览器过来的请求,转向server端进行退出操作
				//通过调用server注销接口
				LoginStatus loginStatus=getLoginStatus(session);
				if(loginStatus!=null){
					SSOClient.clearUserInformationInSession(session);
					SSOClient.clearUserInformation(loginStatus);
					tellServerToLogoutUser(loginStatus.getGlobalSessionId(),loginStatus.getUserId());
				}
				SysUtil.redirect(request,response,back);
			}else{//sso server端发送的指令
				String time=SysUtil.getHttpParameter(request,Constants.SSO_TIME);
				String globalSessionId=SysUtil.getHttpParameter(request,Constants.SSO_GLOBAL_SESSION_ID);
				
				if(globalSessionId==null){//注销全部用户
					String md5=JUtilMD5.MD5EncodeToHex(client.getPassport()+time);
					if(md5.equalsIgnoreCase(key)){//md5校验通过
						logoutAllUsers();
						
						jsession.resultString=Constants.RESPONSE_OK;//返回给server处理结果
					}else{
						jsession.resultString=Constants.RESPONSE_MD5_ERR;//返回给server处理结果
					}
				}else{
					String userId=SysUtil.getHttpParameter(request,Constants.SSO_USER_ID);
					String userIp=SysUtil.getHttpParameter(request,Constants.SSO_USER_IP);
					
					String md5=JUtilMD5.MD5EncodeToHex(client.getPassport()+time+globalSessionId+userId+userIp);
					if(md5.equalsIgnoreCase(key)){//md5校验通过
						clearUserInformation(globalSessionId);//注销该用户client端的登录信息
						jsession.resultString=Constants.RESPONSE_OK;//返回给server处理结果
					}else{						
						jsession.resultString=Constants.RESPONSE_MD5_ERR;//返回给server处理结果
					}
				}
			}
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			if(key==null){//从用户浏览器过来的请求，出错返回错误页面
				SysUtil.redirect(request,response,SysConfig.errorPage);
			}else{//sso server端发送的指令，返回错误代码
				jsession.resultString=Constants.RESPONSE_ERR;
			}
		}
	}	
	
	/**
	 * 登录状态及相关用户信息，通过传递values参数，其值为用;分隔的系统用户类的一个或多个变量名，
	 * 变量名实际可以不存在，只要通过命名规则转换成的get方法存在即可，系统通过调用这些get方法取得登录用户的相关信息，
	 * 如账户余额、用户收到的信息数：
	 * 系统用户类（即实现j.framework.sso.User接口的类，在sso.xml中指定）中的get方法——
	 * public float getBill(){......}
	 * public float getMsgCount(){......}
	 * 
	 * 客户端可以用如下url查询——
	 * /ssoclient.handler?request=ssostatus&values=bill;msgCount
	 * 
	 * 返回内容为xml，例如：
	 * <?xml version="1.0" encoding="utf-8" ?>
	 * <root is-login="t" session-id="..." bill="1000.00" msgCount="0"/> 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 */
	public void ssostatus(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response){
		String values=SysUtil.getHttpParameter(request,"values");
		try{
			LoginStatus loginStatus=getLoginStatus(session);
			User user=getCurrentUser(session);
			
			jsession.resultString="<?xml version=\"1.0\" encoding=\""+SysConfig.sysEncoding+"\" ?><root";
			if(loginStatus==null||user==null){
				jsession.resultString+=" is-login=\"false\"";
				jsession.resultString+=" session-id=\"\"";
			}else{				
				jsession.resultString+=" is-login=\"true\"";				
				jsession.resultString+=" session-id=\""+loginStatus.getGlobalSessionId()+"\"";
				
				if(values!=null&&values.trim().length()>0){
					String[] fields=JUtilString.getTokensWithoutEmptyStr(values,";");
					for(int i=0;i<fields.length;i++){
						Object value=JUtilBean.getPropertyValue(user,fields[i]);					
						jsession.resultString+=" "+fields[i]+"=\""+(value==null?"":JUtilString.xmlConvertSpecialCharacters(value.toString()))+"\"";
					}
				}		
			}
			jsession.resultString+="/>";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString="<?xml version=\"1.0\" encoding=\""+SysConfig.sysEncoding+"\" ?><root is-login=\"f\"/>";
		}
	}	
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 */
	public void ssologinagent(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response){
		try{
			//log.log("ssologinagent:000",-1);
			String _verifier=SysUtil.getHttpParameter(request,"_verifier");
			String _parameters=SysUtil.getHttpParameter(request,"_parameters");
			if(_verifier==null||_parameters==null){
				//log.log("ssologinagent:001",-1);
				jsession.resultString=Constants.RESPONSE_MD5_ERR;
				return;
			}

			Client client=SSOConfig.getSsoClientByIdOrUrl(SysConfig.getSysId());
			
			
			//md5拼串 - 登录请求参数名（多个逗号分隔）+按参数名顺序累加的各参数值+提供验证的SSO Client与SSO Server交互的passport（密钥）
			//收到请求时，应该先获取_parameters参数，并按照其指明的参数顺序拼接各参数值，然后按照上述一样的方式得出md5值，与_verifier参数值相同才能通过验证
			//详见j.app.sso.LoginAgent.login
			String md5=_parameters;
			String[] paraNames=_parameters.split(",");
			for(int i=0;i<paraNames.length;i++){
				md5+=JUtilString.encodeURI(SysUtil.getHttpParameter(request,paraNames[i]),SysConfig.sysEncoding);
			}
			md5+=client.getPassport();
			//System.out.println("2 - "+md5);
			
			md5=JUtilMD5.MD5EncodeToHex(md5);
			
			if(!_verifier.equalsIgnoreCase(md5)){//md5校验未通过
				//log.log("ssologinagent:002",-1);
				jsession.resultString=Constants.RESPONSE_MD5_ERR;
				return;
			}
			
			String ip=SysUtil.getHttpParameter(request,Constants.SSO_USER_IP);
			
			LoginResult loginResult=client.getLoginAgent().getAuthenticator().login(request,session,ip);
			if(loginResult==null){
				//log.log("ssologinagent:003",-1);
				jsession.resultString=Constants.RESPONSE_ERR;
			}else{
				Document doc=DocumentHelper.createDocument();
				doc.setXMLEncoding("UTF-8");
				
				Element root=doc.addElement("root");
				
				Element e1=root.addElement(Constants.SSO_SYS_ID);
				e1.setText(SysConfig.getSysId());
				
				Element e2=root.addElement(Constants.SSO_MACHINE_ID);
				e2.setText(SysConfig.getMachineID());
				
				Element e3=root.addElement(Constants.SSO_USER_ID);
				e3.setText(loginResult.getUserId());
				
				Element e4=root.addElement(Constants.SSO_LOGIN_RESULT_CODE);
				e4.setText(loginResult.getResult()+"");
				
				Element e5=root.addElement(Constants.SSO_LOGIN_RESULT_MSG);
				e5.setText(loginResult.getResultMsg());
				
				Element e6=root.addElement(Constants.SSO_LOGIN_CHANCES);
				e6.setText(loginResult.getChances()+"");
				
				Element eMessages=root.addElement("messages");
				Map messages=loginResult.getMessages();
				for(Iterator keys=messages.keySet().iterator();keys.hasNext();){
					Object key=keys.next();
					Object val=messages.get(key);
					
					Element e=eMessages.addElement(key.toString());
					e.setText(JObject.string2IntSequence(val.toString()));
				}
				
				jsession.resultString=JUtilDom4j.toString(doc,"UTF-8");
				
				doc=null;
			}
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.RESPONSE_ERR;
		}
	}

	/**
	 * 同步用户最近活动时间到server端
	 */
	public void run() {	
		try{
			Thread.sleep(1000);
		}catch(Exception ex){}
		
		_init();
		
		try{
			Thread.sleep(30000);
		}catch(Exception ex){}
		
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception ex){}
			
			try{
				updater.doUpdate(); 
			}catch(Exception ex){
				log.log(ex,Logger.LEVEL_ERROR);
			}
		}
	}
}