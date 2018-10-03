package j.app.webserver;


import j.I18N.I18N;
import j.app.Constants;
import j.app.online.Onlines;
import j.log.Logger;
import j.nvwa.Nvwa;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.ConcurrentMap;
import j.util.JUtilString;
import j.util.JUtilUUID;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author JFramework
 *
 * 控制中心，根据用户请求调用相应的业务处理类，并根据执行结果导航
 */
public class Server{
	private static Logger log=Logger.create(Server.class);//日志输出	
	private static ConcurrentMap handlers=new ConcurrentMap();
	
	/**
	 * 
	 * @param session
	 * @return
	 */
	public static String[] getRequestUuid(HttpSession session){
		String[] uuid=new String[]{SysUtil.getNow()+"",JUtilUUID.genUUIDShort()};
		session.setAttribute(Constants.J_REQUEST_UUID+"_"+uuid[0], uuid[1]);
		return uuid;
	}
	
	/**
	 * 
	 * @param session
	 * @param sn
	 * @return
	 */
	public static String[] getRequestUuid(HttpSession session,String sn){
		String[] uuid=new String[]{sn,JUtilUUID.genUUIDShort()};
		session.setAttribute(Constants.J_REQUEST_UUID+"_"+uuid[0], uuid[1]);
		return uuid;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 */
	private static String[] ignoredLogActionsOfService=new String[]{"register","unregister","heartbeat","auth","service"};
	public static void service(Handler handler,HttpServletRequest request,HttpServletResponse response)throws ServletException{
		String requestUuid=request.getParameter(Constants.J_REQUEST_UUID);
		String requestUuidSn=request.getParameter(Constants.J_REQUEST_UUID_SN);
		
		HttpSession session=request.getSession();//得到会话（HttpSession）
				
		JSession jsession=null;
		JHandler jHandler=null;//业务处理类
		Action action=null;		
		String actionId=request.getParameter(handler.getRequestBy());//得到用户请求的操作名
		
		String navigateType =request.getParameter(Constants.J_BACK_TYPE);//调转到返回地址所使用的机制	
		String navigateUrl=request.getParameter(Constants.J_BACK_URL);//返回给用户的地址
		if(navigateType==null){
			navigateType=(String)request.getAttribute(Constants.J_BACK_TYPE);
		}
		if(navigateUrl==null){
			navigateUrl=(String)request.getAttribute(Constants.J_BACK_URL);
		}

		
		ActionLogger logger=Handlers.selectLogger();
		boolean toLog=true;
		try{
			if(actionId==null){
				try{
					SysUtil.outHttpResponse(response,Constants.J_NO_ACTION);
				}catch(Exception e){}
				return;
			}
			
			if(!actionId.matches("^([a-zA-Z_.\\-]{1,64})$")){
				try{
					SysUtil.outHttpResponse(response,Constants.J_ILLEGAL_VISITOR);
				}catch(Exception e){}
				return;
			}
			
			action=handler.getAction(actionId);
			if(action==null) throw new Exception(handler.getPath()+" - 找不到请求的方法 - "+actionId);
			
			if(".service".equals(handler.getPathPattern())
					&&JUtilString.contain(ignoredLogActionsOfService, actionId)){
				toLog=false;
			}
			
			if(requestUuid==null||"".equals(requestUuid)) requestUuid=JUtilUUID.genUUID();
			
			if(toLog) logger.before(action,requestUuid,session,request);
			
			//重复提交检查
			if(requestUuid!=null
					&&!"".equals(requestUuid)
					&&requestUuidSn!=null
							&&!"".equals(requestUuidSn)){
				String _requestUuid=(String)session.getAttribute(Constants.J_REQUEST_UUID+"_"+requestUuidSn);
				session.removeAttribute(Constants.J_REQUEST_UUID+"_"+requestUuidSn);
				if(!requestUuid.equals(_requestUuid)){
					logger.after(action,session,requestUuid,Constants.J_DUPLICATED_RQUEST);
					SysUtil.outHttpResponse(response,Constants.J_DUPLICATED_RQUEST);//print返回内容给用户
					return;
				}
			}
			//重复提交检查 end
			
			String processResult="";//调用业务处理类后的处理结果	
			//boolean respondWithString=action.getRespondWithString();//是否直接print
			
			
			//根据操作名找到对应的业务处理类，并调用其process方法		
			jsession=new JSession(action);
			
			if(handler.getNonNvwaObj()){
				if(handler.getSingleton()){
					if(handlers.containsKey(handler.getPath())){
						jHandler=(JHandler)handlers.get(handler.getPath());
					}else{
						jHandler=(JHandler)Class.forName(handler.getClazz()).newInstance();
						handlers.put(handler.getPath(), jHandler);
					}
				}else{
	    			jHandler=(JHandler)Class.forName(handler.getClazz()).newInstance();
				}
        	}else{
    			jHandler=(JHandler)Nvwa.entrustCreate(handler.getPath(),handler.getClazz(),handler.getSingleton());
        	}
			jHandler.init(jsession,session,request,response);
			jHandler.process(jsession,session,request,response);
			processResult=jsession.result;//处理结果
			
			if(response.isCommitted()){
				if(!handler.getSingleton()&&jHandler!=null) jHandler=null;
				
				if(toLog) logger.after(action,session,requestUuid);
				return;//如果已经返回给客户端	
			}
			
			if(navigateUrl==null||(!navigateUrl.startsWith("http")&&!navigateUrl.startsWith("/"))){
				navigateUrl=jsession.getDynamicBackUrl();//动态返回url
			}
			
			if(jsession.resultString==null
					&&jsession.jresponse==null
					&&jsession.result==null
					&&navigateUrl==null){
				SysUtil.outHttpResponse(response,"");
				return;//无处理结果
			}
			
			if(jsession.jresponse!=null){//如果是直接输出
				if(!handler.getSingleton()&&jHandler!=null) jHandler=null;
				
				String resultString="";
				
				String referer=request.getHeader("referer");
				referer=JUtilString.getUri(referer);
			
				//多语言转换
				String responseCode=jsession.jresponse.getCode();
				String responseMessage=jsession.jresponse.getMessage();
				if(referer!=null&&referer.startsWith("/")){
					responseCode=I18N.convert(responseCode,referer,session);
					responseMessage=I18N.convert(responseMessage,referer,session);
				}else{
					responseCode=I18N.convert(responseCode,I18N.getCurrentLanguage(session));
					responseMessage=I18N.convert(responseMessage,I18N.getCurrentLanguage(session));
				}
				jsession.jresponse.setCode(responseCode);
				jsession.jresponse.setMessage(responseMessage);
				
				//if(referer!=null&&referer.startsWith("/")){
				//	resultString=I18N.convert(jsession.jresponse.toString(),referer,session);
				//}else{
				//	resultString=I18N.convert(jsession.jresponse.toString(),I18N.getCurrentLanguage(session));
				//}
				resultString=jsession.jresponse.toString();
				//多语言转换  end
				
				
				if(toLog) logger.after(action,session,requestUuid,resultString);
				SysUtil.outHttpResponse(response,resultString);//print返回内容给用户
				return;
			}else if(jsession.resultString!=null){//如果是直接输出
				if(!handler.getSingleton()&&jHandler!=null) jHandler=null;
				
				String resultString=jsession.resultString;
				String referer=request.getHeader("referer");
				referer=JUtilString.getUri(referer);
				if(referer!=null&&referer.startsWith("/")){
					resultString=I18N.convert(resultString,referer,session);
				}
				
				if(toLog) logger.after(action,session,requestUuid,jsession.resultString);
				SysUtil.outHttpResponse(response,jsession.resultString);//print返回内容给用户
				return;
			}
		
			if(jsession.getIsBackToGlobalNavigation()){//执行全局导航定义
				if(navigateUrl==null||(!navigateUrl.startsWith("http")&&!navigateUrl.startsWith("/"))){
					navigateUrl=jsession.getDynamicBackUrl();//动态返回url
					if(navigateUrl==null){//如果未设置了动态返回url
						navigateUrl=Handlers.getGlobalNavigateUrl(processResult);//返回地址
					}
				}
				if(!"forward".equals(navigateType)&&!"redirect".equals(navigateType)){
					navigateType=Handlers.getGlobalNavigateType(processResult);//返回类型
				}
			}else{
				if(navigateUrl==null||(!navigateUrl.startsWith("http")&&!navigateUrl.startsWith("/"))){
					navigateUrl=jsession.getDynamicBackUrl();//动态返回url
					if(navigateUrl==null){//如果未设置了动态返回url
						navigateUrl=action.getNavigateUrl(processResult);//返回地址
					}
				}
				if(!"forward".equals(navigateType)&&!"redirect".equals(navigateType)){
					navigateType=action.getNavigateType(processResult);//返回类型
				}
			}	

			if(!handler.getSingleton()&&jHandler!=null) jHandler=null;
			
			if(toLog) logger.after(action,session,requestUuid,navigateType,navigateUrl);
			
			if(navigateType==null){
				throw new Exception("no defined view matches the result");
			}

			if(Onlines.getHandler()!=null){
				navigateUrl=Onlines.getHandler().adjustUrl(session,request,navigateUrl);
			}
			if(navigateType.equalsIgnoreCase("forward")){//如果返回类型为forward		
				SysUtil.forwardI18N(request,response,navigateUrl);
			}else{//如果返回类型为sendRedirect
				SysUtil.redirect(request,response,navigateUrl);			
			}	
		}catch(Exception ex){
			if(!handler.getSingleton()&&jHandler!=null) jHandler=null;
			
			if(toLog) logger.after(action,session,requestUuid,ex);
			log.log("errors on "+SysUtil.getRequestURL(request)+"\r\n the handler is - "+jHandler,Logger.LEVEL_ERROR);
			log.log(ex,Logger.LEVEL_ERROR);
			if(!response.isCommitted()){//如果未返回
				if(action!=null&&action.getOnError()!=null){//如果设置了on-error属性
					Navigate nav=action.getNavigate(action.getOnError());
					if(nav!=null){//on-error设置的属性值所代表的<navigate>存在
						navigateType=nav.getType();
						navigateUrl=nav.getUrl();

						if(Onlines.getHandler()!=null){
							navigateUrl=Onlines.getHandler().adjustUrl(session,request,navigateUrl);
						}
						if(navigateType.equalsIgnoreCase("forward")){//如果返回类型为forward	
							try{
								SysUtil.forwardI18N(request,response,navigateUrl);
								return;
							}catch(IOException ioEx){}							
						}else{//如果返回类型为sendRedirect
							try{
								SysUtil.redirect(request,response,navigateUrl);
								return;
							}catch(IOException ioEx){}		
						}
					}
				}
				
				try{
					SysUtil.redirect(request,response,SysConfig.errorPage);
				}catch(IOException ioEx){}
			}
		}finally{
			if(jsession!=null) jsession=null;
		}
	}
}