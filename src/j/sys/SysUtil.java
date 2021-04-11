package j.sys;

import j.I18N.I18N;
import j.I18N.I18NResponseWrapper;
import j.app.Constants;
import j.app.sso.Client;
import j.app.sso.SSOConfig;
import j.common.JObject;
import j.log.Logger;
import j.util.JUtilDom4j;
import j.util.JUtilMD5;
import j.util.JUtilString;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


/**
 * @author 肖炯
 *
 */
public class SysUtil {		
	private static Logger log=Logger.create(SysUtil.class);
	public static final String USER_AGENT_UNKNOWN="NONE";
	
	public static final String USER_AGENT_PC="PC";
	
	public static final String USER_AGENT_IOS="IOS";
	public static final String[] USER_AGENT_IOS_KEYWORDS=new String[]{"iphone", "ipod", "ipad"};
	
	public static final String USER_AGENT_ANDROID="ANDROID";
	public static final String[] USER_AGENT_ANDROID_KEYWORDS=new String[]{"android"};
	
	public static final String USER_AGENT_WEIXIN="WEIXIN";
	public static final String[] USER_AGENT_WEIXIN_KEYWORDS=new String[]{"micromessenger"};
	
	public static final String USER_AGENT_MOBILE="MOBILE";
	public static final String[] USER_AGENT_MOBILE_KEYWORDS=new String[]{"iphone", "ipod", "ipad", "android", "mobile", "blackberry", "webos", "incognito", "webmate", "bada", "nokia", "lg", "ucweb", "skyfire"};
	
	public static final String USER_AGENT_BROWSER="BROWSER";
	public static final String[] USER_AGENT_BROWSER_KEYWORDS=new String[]{"chrome", "qqbrowser", "ucbrowser", "sogou", "firefox", "edge", "safari", "opera", "taobrowser", "lbbrowser", "maxthon"};
	
	
	/**
	 * @deprecated 请使用j.app.webserver.UserAgents中相关方法
	 * @param request
	 * @return
	 */
	public static String getUserAgentType(HttpServletRequest request){
		if(request==null) return USER_AGENT_UNKNOWN;
		
		String userAgent=request.getHeader("User-Agent");
		if(userAgent==null) return USER_AGENT_UNKNOWN;
		
		userAgent=userAgent.toLowerCase();
		if(JUtilString.existsIgnoreCase(userAgent,USER_AGENT_WEIXIN_KEYWORDS)){
			return USER_AGENT_WEIXIN;
		}else if(JUtilString.existsIgnoreCase(userAgent,USER_AGENT_MOBILE_KEYWORDS)
				&&JUtilString.existsIgnoreCase(userAgent,USER_AGENT_BROWSER_KEYWORDS)){
			return USER_AGENT_MOBILE;
		}else if(JUtilString.existsIgnoreCase(userAgent,USER_AGENT_IOS_KEYWORDS)){
			return USER_AGENT_IOS;
		}else if(JUtilString.existsIgnoreCase(userAgent,USER_AGENT_ANDROID_KEYWORDS)){
			return USER_AGENT_ANDROID;
		}else{
			return USER_AGENT_PC;
		}
	}
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	public static String getException(Exception e){
		if(e==null) return null;
		String ex=e.getMessage()+"\r\n";
		StackTraceElement[] es=e.getStackTrace();
		for(int i=0;i<es.length;i++){
			ex+="\t"+es[i].getClassName()+","+es[i].getMethodName();
			if(es[i].getFileName()==null){
				ex+=",unknown source";
			}else{
				ex+=","+es[i].getFileName();
				if(es[i].getLineNumber()>0){
					ex+=",line "+es[i].getLineNumber();
				}
			}
			ex+="\r\n";
		}
		return ex;
	}
	
	/**
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getCookie(HttpServletRequest request,String name){
		Cookie[] cs=request==null?null:request.getCookies();
		if(cs==null) return null;
		
		String value=null;
		for(int i=0;i<cs.length;i++){
			if(cs[i].getName().equalsIgnoreCase(name)) value=cs[i].getValue();
		}
		return value;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getHttpParametersAsXml(HttpServletRequest request){
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding(SysConfig.sysEncoding);
		Element root = doc.addElement("root");
		Enumeration parameters = request.getParameterNames();
		while (parameters.hasMoreElements()) {
			String parameter = (String) parameters.nextElement();

			Element pEle = root.addElement("p");
			pEle.addAttribute("name", parameter);
			pEle.setText(SysUtil.getHttpParameter(request, parameter));
		}
		try {
			String ps = JUtilDom4j.toString(doc);
			doc = null;

			return ps;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param client
	 * @param httpRequest
	 * @param httpResponse
	 * @param url
	 * @param parameters
	 * @throws Exception
	 */
	public static void redirectByFormSubmit(Client client,
			HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			String url,
			Map parameters) throws Exception {
		redirectByFormSubmit(client,httpRequest,httpResponse,url,parameters,"",null);
	}
	
	/**
	 * 
	 * @param client
	 * @param httpRequest
	 * @param httpResponse
	 * @param url
	 * @param parameters
	 * @param title
	 * @throws Exception
	 */
	public static void redirectByFormSubmit(Client client,
			HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			String url,
			Map parameters,
			String title) throws Exception {
		redirectByFormSubmit(client,
				httpRequest,
				httpResponse,
				url,
				parameters,
				title,
				null);
	}
	
	/**
	 * 
	 * @param client
	 * @param httpRequest
	 * @param httpResponse
	 * @param url
	 * @param parameters
	 * @param title
	 * @param method
	 * @throws Exception
	 */
	public static void redirectByFormSubmit(Client client,
			HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			String url,
			Map parameters,
			String title,
			String method) throws Exception {
		boolean urlOfOuterSystem=false;
		if(url.startsWith("http://")
				||url.startsWith("https://")
				||url.startsWith("ftp://")
				||url.equals("about:blank")){//如果是系统外部地址
			urlOfOuterSystem=true;
		}	
		
		String action=url;
		if(action.indexOf("?")>0){
			action=action.substring(0,action.indexOf("?"));
		}
		
		if(method==null) method="post";
		
		String redirect="";
		redirect+="<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n";
		redirect+="<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n";
		redirect+="<head>\r\n";
		redirect+="<meta http-equiv=\"Content-Type\" content=\"text/html; charset="+SysConfig.sysEncoding+"\" />\r\n";
		redirect+="<title>"+title+"</title>\r\n";
		redirect+="</head>\r\n";
		redirect+="<body>\r\n";
		redirect+="<form id=\"redirect\" name=\"redirect\" action=\""+(urlOfOuterSystem?action:(httpResponse.encodeRedirectURL(httpRequest.getContextPath()+action)))+"\" method=\""+method+"\">\r\n";
		
		String sso_parameter_names="";
		String sso_parameter_values="";
		
		if(url.indexOf("?")>0){
			url=url.substring(url.indexOf("?")+1);
			String paras[]=url.split("&");
			for(int i=0;i<paras.length;i++){
				if(paras[i].indexOf("=")<0) continue;
				String name=paras[i].substring(0,paras[i].indexOf("="));
				String value=paras[i].substring(paras[i].indexOf("=")+1);
				value=JUtilString.decodeURI(value,SysConfig.sysEncoding);
				redirect+="<input type=\"hidden\" name=\""+name+"\" value=\"jis:"+JObject.string2IntSequence(value)+"\">\r\n";
				
				sso_parameter_names+=name+"|";
				sso_parameter_values+=value;
			}
		}

		if(parameters!=null){
			for(Iterator keys=parameters.keySet().iterator();keys.hasNext();){
				Object key=keys.next();
				Object val=parameters.get(key);
				
				redirect+="<input type=\"hidden\" name=\""+key+"\" value=\"jis:"+JObject.string2IntSequence(val.toString())+"\">\r\n";
				
				sso_parameter_names+=key+"|";
				sso_parameter_values+=val;
			}
		}
		
		if(sso_parameter_names.length()>0){
			sso_parameter_names=sso_parameter_names.substring(0,sso_parameter_names.length()-1);
		}
		
		redirect+="<input type=\"hidden\" name=\""+Constants.SSO_PNAMES+"\" value=\""+sso_parameter_names+"\">\r\n";
		redirect+="<input type=\"hidden\" name=\""+Constants.SSO_PVERIFY+"\" value=\""+JUtilMD5.MD5EncodeToHex(sso_parameter_values+client.getPassport())+"\">\r\n";
		redirect+="</form>\r\n";
		redirect+="<script type=\"text/javascript\">redirect.submit();</script>\r\n";
		redirect+="</body>\r\n";
		redirect+="</html>";
		SysUtil.outHttpResponse(httpResponse,redirect,JUtilString.bytes(redirect, "UTF-8"));
	}
	
	/**
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param url
	 * @throws Exception
	 */
	public static void redirectByFormSubmit(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			String url) throws Exception {
		redirectByFormSubmit(httpRequest,httpResponse,url,null);
	}
	
	/**
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param url
	 * @param parameters
	 * @throws Exception
	 */
	public static void redirectByFormSubmit(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			String url,
			Map parameters) throws Exception {
		redirectByFormSubmit(httpRequest,httpResponse,url,parameters,"",null);
	}
	
	/**
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param url
	 * @param parameters
	 * @param title
	 * @throws Exception
	 */
	public static void redirectByFormSubmit(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			String url,
			Map parameters,
			String title) throws Exception {
		redirectByFormSubmit(httpRequest,
				httpResponse,
				url,
				parameters,
				title,
				null);
	}
	
	/**
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param url
	 * @param parameters
	 * @param title
	 * @param method
	 * @throws Exception
	 */
	public static void redirectByFormSubmit(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			String url,
			Map parameters,
			String title,
			String method) throws Exception {
		boolean urlOfOuterSystem=false;
		if(url.startsWith("http://")
				||url.startsWith("https://")
				||url.startsWith("ftp://")
				||url.equals("about:blank")){//如果是系统外部地址
			urlOfOuterSystem=true;
		}	
		
		String action=url;
		if(action.indexOf("?")>0){
			action=action.substring(0,action.indexOf("?"));
		}
		
		if(method==null) method="post";
		
		String redirect="";
		redirect+="<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n";
		redirect+="<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n";
		redirect+="<head>\r\n";
		redirect+="<meta http-equiv=\"Content-Type\" content=\"text/html; charset="+SysConfig.sysEncoding+"\" />\r\n";
		redirect+="<title>"+title+"</title>\r\n";
		redirect+="</head>\r\n";
		redirect+="<body>\r\n";
		redirect+="<form id=\"redirect\" name=\"redirect\" action=\""+(urlOfOuterSystem?action:(httpResponse.encodeRedirectURL(httpRequest.getContextPath()+action)))+"\" method=\""+method+"\">\r\n";
		
		if(url.indexOf("?")>0){
			url=url.substring(url.indexOf("?")+1);
			String paras[]=url.split("&");
			for(int i=0;i<paras.length;i++){
				if(paras[i].indexOf("=")<0) continue;
				String name=paras[i].substring(0,paras[i].indexOf("="));
				String value=paras[i].substring(paras[i].indexOf("=")+1);
				value=JUtilString.decodeURI(value,SysConfig.sysEncoding);
				redirect+="<input type=\"hidden\" name=\""+name+"\" value=\"jis:"+JObject.string2IntSequence(value)+"\">\r\n";
			}
		}


		if(parameters!=null){
			for(Iterator keys=parameters.keySet().iterator();keys.hasNext();){
				Object key=keys.next();
				Object val=parameters.get(key);
				
				redirect+="<input type=\"hidden\" name=\""+key+"\" value=\"jis:"+JObject.string2IntSequence(val.toString())+"\">\r\n";
			}
		}
		
		redirect+="</form>\r\n";
		redirect+="<script type=\"text/javascript\">redirect.submit();</script>\r\n";
		redirect+="</body>\r\n";
		redirect+="</html>";

		SysUtil.outHttpResponse(httpResponse,redirect,JUtilString.bytes(redirect, "UTF-8"));
	}
	
	/**
	 * url重定向
	 * @param httpRequest
	 * @param httpResponse
	 * @param url
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void redirect(HttpServletRequest httpRequest,HttpServletResponse httpResponse,String url) throws IOException, ServletException {
		if(url==null){
			url="/";
		}
		if(url.startsWith("http://")
				||url.startsWith("https://")
				||url.startsWith("ftp://")
				||url.equals("about:blank")){//如果是系统外部地址
			httpResponse.sendRedirect(url);
		}else{//如果是系统内部地址
			httpResponse.sendRedirect(httpRequest.getContextPath()+url);
		}	
	}

	/**
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param action
	 * @param paras
	 * @throws Exception
	 */
	public static void redirect(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			String action, 
			Map paras)throws Exception{
        try{
        	action = JUtilString.decodeURI(action, SysConfig.sysEncoding);
        	action = JUtilString.decodeURI(action, SysConfig.sysEncoding);
        }catch(Exception e){}
        
        for(Iterator it=paras.keySet().iterator();it.hasNext();){
        	Object name=it.next();
        	String value=paras.get(name)==null?null:paras.get(name).toString();
        	
        	try{
            	value=JUtilString.decodeURI(value,SysConfig.sysEncoding);
            	value=JUtilString.decodeURI(value,SysConfig.sysEncoding);
            }catch(Exception e){}
            
            try{
            	value=JUtilString.encodeURI(value,SysConfig.sysEncoding);
            }catch(Exception e){}
        	
        	if(action.indexOf("?")==-1){
        		action+="?"+name+"="+value;
        	}else{
        		action+="&"+name+"="+value;
        	}
        }

        SysUtil.redirect(httpRequest,httpResponse,action);
    }
	
	
	/**
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param url
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void forward(HttpServletRequest httpRequest,HttpServletResponse httpResponse, String url)throws ServletException, IOException {
		httpRequest.getRequestDispatcher(httpResponse.encodeRedirectURL(url)).forward(httpRequest,httpResponse);
	}
	
	/**
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param url
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void forwardI18N(HttpServletRequest httpRequest,HttpServletResponse httpResponse, String url)throws ServletException, IOException {
		if(!I18N.enabled||!I18N.need(httpRequest)){
			forward(httpRequest,httpResponse,url);
    		return;
    	}
		
		HttpSession session=httpRequest.getSession(true);
		
		I18N.changeLanguage(httpRequest,session);
		
		I18NResponseWrapper wrapper = new I18NResponseWrapper(httpResponse); 
		wrapper.resetBuffer();
		httpRequest.getRequestDispatcher(httpResponse.encodeRedirectURL(url)).include(httpRequest,wrapper);
		
		String content = wrapper.getContent(); 
				
		int start=content.indexOf("<div class=\"I18N-GROUP\">");
		int end=content.indexOf("</div>",start);
		if(start>0&&end>start){//页面中指定了多语言分组
			String group=content.substring(start+"<div class=\"I18N-GROUP\">".length(),end);
			content=I18N.convert(content,group,session);
		}else{
			content=I18N.convert(content,httpRequest,session);
		}
       
    	//重置响应输出的内容长度
		httpResponse.setContentLength(-1);
	    	
		//设置编码格式
		if(url.endsWith(".js")){
			httpResponse.setContentType("application/javascript");
		}if(url.endsWith(".css")){
			httpResponse.setContentType("text/css");
		}else{
			httpResponse.setContentType("text/html");
		}
		httpResponse.setCharacterEncoding(SysConfig.sysEncoding);
	    	
		//输出最终的结果
		PrintWriter out = httpResponse.getWriter();
		out.print(content);
		out.flush();
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getHttpDomain(HttpServletRequest request){
		if(request==null) return SSOConfig.getMainDomain(SysConfig.getSysId());
		return JUtilString.getHost(request.getRequestURL().toString());
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getHttpMainDomain(HttpServletRequest request){
		String useDomain=SysUtil.getHttpParameter(request, "use_domain");
		if(useDomain!=null&&!"".equals(useDomain)) return getHttpMainDomain(useDomain);
		
		String domain=getHttpDomain(request);
		return getHttpMainDomain(domain);
	}
	
	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static String getHttpMainDomain(String domain){
		String _mainDomains=AppConfig.getPara("SYSTEM", "DOMAINS");
		String[] mainDomains=_mainDomains==null||"".equals(_mainDomains)?null:_mainDomains.split(",");
		if(mainDomains!=null && JUtilString.containIgnoreCase(mainDomains, domain)) return domain;
 		
		String[] cells=JUtilString.getTokens(domain, ".");
		if(cells.length<2) return domain;
		else return cells[cells.length-2]+"."+cells[cells.length-1];
	}
	
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getHttpProtocol(HttpServletRequest request){
		if(request.getScheme().toLowerCase().indexOf("https")>-1) return "https";
		return "http";
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestURLBase(HttpServletRequest request){
		return getRequestURLBase(request.getRequestURL().toString());
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestURLBase(String url){
		int i=url.indexOf("://");
		if(i<0) return null;
		
		String p=url.substring(0,i);
		String u=url.substring(i+3);
		
		i=u.indexOf("/");
		if(i<0) return p+"://"+u;
		else{
			return p+"://"+u.substring(0,i); 
		}
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestURLWithoutParams(HttpServletRequest request){
		return getHttpProtocol(request)+"://"+getHttpDomain(request)+request.getRequestURI();
	}
	
	
	
	/**
	 * 得到用户当前访问的url
	 * @param request
	 * @return
	 */
	public static String getRequestURL(HttpServletRequest request){
		return getRequestURL(request,null);
	}	
	
	/**
	 * 得到用户当前访问的url
	 * @param request
	 * @param excludePrefix 排除以excludePrefix开头的参数
	 * @return
	 */
	public static String getRequestURL(HttpServletRequest request,String excludePrefix){
		String url=request.getRequestURL().toString();
		url=url.replaceFirst(":"+request.getRemotePort(), "");

        int i=0;
        Enumeration parameters=request.getParameterNames();
    	try{
    		while(parameters.hasMoreElements()){
	        	String parameter=(String)parameters.nextElement();
	        	if(excludePrefix!=null&&parameter.startsWith(excludePrefix)){
	        		continue;
	        	}
        		String value=getHttpParameter(request,parameter);
	        	if(value!=null){
	        		if(value.length()>256) value=value.substring(0,256)+"(too long)";
	        		try{
	        			value=JUtilString.encodeURI(value,SysConfig.sysEncoding);
	        		}catch(Exception e){
	        			log.log(e,Logger.LEVEL_ERROR);
	        		}
	        		if(i==0){
	        			url+="?"+parameter+"="+value;
	        		}else{
	        			url+="&"+parameter+"="+value;
	        		}
	        		i++;
	        	}
        	}
    	}catch(Exception e){}
        return url;
	}
	
	/**
	 * 得到用户当前访问的url的参数串，即url中?后面的串，包括?
	 * @param request
	 * @return
	 */
	public static String getParaSequence(HttpServletRequest request){
		return getParaSequence(request,(String)null);
	}
	
	/**
	 * 得到用户当前访问的url的参数串，即url中?后面的串，包括?
	 * @param request
	 * @param excludePrefix 排除以excludePrefix开头的参数
	 * @return
	 */
	public static String getParaSequence(HttpServletRequest request,String excludePrefix){
		String url="";
        int i=0;
        Enumeration parameters=request.getParameterNames();
    	try{
    		while(parameters.hasMoreElements()){
	        	String parameter=(String)parameters.nextElement();
	        	if(excludePrefix!=null&&parameter.startsWith(excludePrefix)){
	        		continue;
	        	}
        		String value=getHttpParameter(request,parameter);
	        	if(value!=null){
	        		try{
	        			value=JUtilString.encodeURI(value,SysConfig.sysEncoding);
	        		}catch(Exception e){
	        			log.log(e,Logger.LEVEL_ERROR);
	        		}
	        		if(i==0){
	        			url+="?"+parameter+"="+value;
	        		}else{
	        			url+="&"+parameter+"="+value;
	        		}
	        		i++;
	        	}
        	}
    	}catch(Exception e){}
        return url;
	}	
	
	/**
	 * 得到用户当前访问的url的参数串，即url中?后面的串，包括?
	 * @param request
	 * @param excludePrefix 排除以excludePrefix开头的参数
	 * @return
	 */
	public static String getParaSequence(HttpServletRequest request,String[] excludePrefix){
		String url="";
        int i=0;
        Enumeration parameters=request.getParameterNames();
    	try{
    		while(parameters.hasMoreElements()){
	        	String parameter=(String)parameters.nextElement();
	        	if(excludePrefix!=null&&JUtilString.contain(excludePrefix,parameter)){
	        		continue;
	        	}
        		String value=getHttpParameter(request,parameter);
	        	if(value!=null){
	        		try{
	        			value=JUtilString.encodeURI(value,SysConfig.sysEncoding);
	        		}catch(Exception e){
	        			log.log(e,Logger.LEVEL_ERROR);
	        		}
	        		
	        		if(i==0){
	        			url+="?"+parameter+"="+value;
	        		}else{
	        			url+="&"+parameter+"="+value;
	        		}
	        		i++;
	        	}
        	}
    	}catch(Exception e){}
        return url;
	}	
	
	/**
	 * 
	 * @param url
	 * @param parameters
	 * @return
	 */
	public static String appendHttpParameter(String url,Map parameters){
		if(url.lastIndexOf("/")<=7) url+="/";//https://...
		if(parameters!=null){
			for(Iterator keys=parameters.keySet().iterator();keys.hasNext();){
				String key=(String)keys.next();
				String val=(String)parameters.get(key);
				if(val==null) val="";
				
				if(url.indexOf("?")>0) url+="&"+key+"="+JUtilString.encodeURI(val,SysConfig.sysEncoding);
				else url+="?"+key+"="+JUtilString.encodeURI(val,SysConfig.sysEncoding);
			}
		}
		return url;
	}
	
	/**
	 * 得到http参数
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getHttpParameter(HttpServletRequest request,String name){
		if(request==null||name==null||name.trim().equals("")){
			return null;
		}
		String value=request.getParameter(name);
		if(value==null){
			return null;
		}
		if("true".equalsIgnoreCase(AppConfig.getPara("SYSTEM", "http-get-parameter-encoding-convert"))
				&&request.getMethod().equalsIgnoreCase("GET")){
			try{
				value=new String(value.getBytes("ISO-8859-1"),SysConfig.sysEncoding);
			}catch(Exception e){
			}
		}
		if(value!=null&&value.startsWith("jis:")){
			value=JObject.intSequence2String(value);
		}
		
		if(value!=null) value=value.trim();
		
		return value;
	}
	
	/**
	 * 得到http参数
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static String getHttpParameter(HttpServletRequest request,String name,String defaultValue){
		String value=getHttpParameter(request,name);
		if(value==null){
			value=defaultValue;
		}
		return value;
	}
	
	
	/**
	 * 得到http参数
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getHttpParameterLowerCase(HttpServletRequest request,String name){
		String value=getHttpParameter(request,name);
		
		if(value!=null) value=value.toLowerCase();
		
		return value;
	}
	
	/**
	 * 得到http参数
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static String getHttpParameterLowerCase(HttpServletRequest request,String name,String defaultValue){
		String value=getHttpParameterLowerCase(request,name);
		if(value==null){
			value=defaultValue;
		}
		return value;
	}
	
	
	/**
	 * 得到http参数
	 * @param request
	 * @param name
	 * @return
	 */
	public static String[] getHttpParameters(HttpServletRequest request,String name){
		if(request==null||name==null||name.trim().equals("")){
			return null;
		}
		String[] values=request.getParameterValues(name);
		if(values==null){
			return null;
		}
		if(values!=null
				&&"true".equalsIgnoreCase(AppConfig.getPara("SYSTEM", "http-get-parameter-encoding-convert"))
				&&request.getMethod().equalsIgnoreCase("GET")){
			try{
				for(int i=0;i<values.length;i++){
					values[i]=new String(values[i].getBytes("ISO-8859-1"),SysConfig.sysEncoding);
				}
			}catch(Exception e){}
		}
		
		for(int i=0;values!=null&&i<values.length;i++){
			if(values[i]!=null&&values[i].startsWith("jis:")){
				values[i]=JObject.intSequence2String(values[i]);
			}
		}
		
		return (values==null||values.length==0)?null:values;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static Map getHttpParameterMap(HttpServletRequest request){
		Map map=new LinkedHashMap();
        Enumeration parameters=request.getParameterNames();
    	try{
    		while(parameters.hasMoreElements()){
	        	String parameter=(String)parameters.nextElement();
        		String value=getHttpParameter(request,parameter);
	        	if(value!=null){
	        		map.put(parameter,value);
	        	}
        	}
    	}catch(Exception e){}
    	return map;
	}
	
	/**
	 * 
	 * @param request
	 * @param excludePrefix
	 * @return
	 */
	public static Map getHttpParameterMap(HttpServletRequest request,String excludePrefix){
		Map map=new LinkedHashMap();
        Enumeration parameters=request.getParameterNames();
    	try{
    		while(parameters.hasMoreElements()){
	        	String parameter=(String)parameters.nextElement();
	        	if(excludePrefix!=null&&parameter.startsWith(excludePrefix)){
	        		continue;
	        	}
        		String value=getHttpParameter(request,parameter);
	        	if(value!=null){
	        		map.put(parameter,value);
	        	}
        	}
    	}catch(Exception e){}
    	return map;
	}
	
	/**
	 * 
	 * @param request
	 * @param excludePrefix
	 * @return
	 */
	public static Map getHttpParameterMap(HttpServletRequest request,String[] excludePrefix){
		Map map=new LinkedHashMap();
        Enumeration parameters=request.getParameterNames();
    	try{
    		while(parameters.hasMoreElements()){
	        	String parameter=(String)parameters.nextElement();
	        	if(excludePrefix!=null&&JUtilString.contain(excludePrefix,parameter)){
	        		continue;
	        	}
        		String value=getHttpParameter(request,parameter);
	        	if(value!=null){
	        		map.put(parameter,value);
	        	}
        	}
    	}catch(Exception e){}
        return map;
	}
	

	/**
	 * 当前时间
	 * @return
	 */
	public static long getNow(){
		if(AppConfig.getPara("SYSTEM","time-zone-diff")!=null){
			//在当前时间上加上app.xml中SYSTEM组中定义的参数time-zone-diff的值（单位毫秒）
			return System.currentTimeMillis()+Long.parseLong(AppConfig.getPara("SYSTEM","time-zone-diff"));
		}else{
			return System.currentTimeMillis();
		}
	}
	
	/**
	 * 
	 * @param response
	 * @param s
	 * @throws Exception
	 */
	public static void outHttpResponse(HttpServletResponse response,String s) throws Exception{
		response.setContentType("text/html;charset="+SysConfig.sysEncoding);
		response.setContentLength(-1);
		PrintWriter out=response.getWriter();
		out.print(s);
		out.flush();
		out.close();
	}
	
	/**
	 * 
	 * @param response
	 * @param s
	 * @throws Exception
	 */
	public static void outHttpResponse(HttpServletResponse response,int s) throws Exception{
		response.setContentType("text/html;charset="+SysConfig.sysEncoding);
		response.setContentLength(-1);
		PrintWriter out=response.getWriter();
		out.print(s);
		out.flush();
		out.close();
	}
	
	/**
	 * 
	 * @param response
	 * @param s
	 * @throws Exception
	 */
	public static void outHttpResponse(HttpServletResponse response,String s,int contentLength) throws Exception{
		response.setContentType("text/html;charset="+SysConfig.sysEncoding);
		response.setContentLength(contentLength);
		PrintWriter out=response.getWriter();
		out.print(s);
		out.flush();
		out.close();
	}
	
	/**
	 * 
	 * @param response
	 * @param s
	 * @throws Exception
	 */
	public static void outHttpResponse(HttpServletResponse response,int s,int contentLength) throws Exception{
		response.setContentType("text/html;charset="+SysConfig.sysEncoding);
		response.setContentLength(contentLength);
		PrintWriter out=response.getWriter();
		out.print(s);
		out.flush();
		out.close();
	}
	
	/**
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param url
	 * @param paras
	 * @param encodePara
	 * @param encoding
	 * @throws Exception
	 */
	public static void response(HttpServletRequest httpRequest,HttpServletResponse httpResponse,String url,Map paras,boolean encodePara,String encoding) throws Exception{
		for(Iterator keys=paras.keySet().iterator();keys.hasNext();){
			String key=(String)keys.next();
			String val=(String)paras.get(key);
			if(encodePara){
				if(url.indexOf("?")<0){
					url+="?"+key+"="+JUtilString.encodeURI(val,encoding);
				}else{
					url+="&"+key+"="+JUtilString.encodeURI(val,encoding);
				}		
			}else{
				if(url.indexOf("?")<0){
					url+="?"+key+"="+val;
				}else{
					url+="&"+key+"="+val;
				}	
			}
		}
		
		SysUtil.redirect(httpRequest,httpResponse,url);
	}
	
	/**
	 * 
	 * @param session
	 * @param name
	 * @return
	 */
	public static Object getHttpSessionAttr(HttpSession session,String name){
		return session==null?null:session.getAttribute(name);
	}
	
	/**
	 * 
	 * @param session
	 * @param name
	 * @param defaultValueIfNull
	 * @return
	 */
	public static Object getHttpSessionAttr(HttpSession session,String name,Object defaultValueIfNull){
		Object obj=session==null?null:session.getAttribute(name);
		return obj==null?defaultValueIfNull:obj;
	}
	
	public static void main(String[] args){
		String requestUrl="www.gugumall.cn/";
		Map p=new HashMap();
		p.put("state","aaaa");
		requestUrl=SysUtil.appendHttpParameter(requestUrl,p);
		System.out.println(requestUrl);
	}
}
