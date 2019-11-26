package j.app.webserver;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import j.app.Constants;
import j.common.JObject;
import j.http.JHttp;
import j.http.JHttpContext;
import j.log.Logger;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.JUtilString;

/**
 * 
 * @author 肖炯
 *
 * 2019年11月11日
 *
 * <b>功能描述</b> 响应节点信息，所谓响应节点是指用户发起一个请求时，不是调用本地action，而是调用远程节点中对应的action，调用过程为：<br/>
 * step1: 用远程节点的域名或IP替换本地请求地址的域名或IP（包括协议HTTP/HTTPS），将本地session中对象序列化后作为参数、同原始请求参数一起post给远程节点。
 * step2: 远程节点收到请求后，反序列化session对象并设置到当前session中，然后调用action。
 * step3: 收到远程节点响应后，输出到给客户端。
 */
public class JResponser extends JObject{
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.create(JResponser.class);
	private String id;
	private String name;
	private String urlBase;
	private String key;
	private String[] urlPatterns;//需要调用远程节点的URL,*号表示全部
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param urlBase
	 * @param key
	 * @param urlPatterns
	 */
	public JResponser(String id, String name, String urlBase, String key, String[] urlPatterns) {
		this.id=id;
		this.name=name;
		this.urlBase=urlBase;
		this.key=key;
		this.urlPatterns=urlPatterns;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getUrlBase() {
		return this.urlBase;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String[] getUrlPatterns() {
		return this.urlPatterns;
	}
	
	/**
	 * 
	 * @param uri
	 * @return
	 */
	public boolean matches(String uri) {
		if(this.urlPatterns==null || this.urlPatterns.length==0) return false;
		for(int i=0; i<this.urlPatterns.length; i++) {
			if("*".equals(this.urlPatterns[i])) return true;
			if(JUtilString.match(uri, this.urlPatterns[i], "*")>-1) return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param session
	 * @param request
	 * @param requestURI
	 * @param ignoreParameters
	 * @return
	 * @throws Exception
	 */
	public JHttpContext call(HttpSession session, HttpServletRequest request, String requestURI, String[] ignoreParameters) throws Exception{
		Map map=new LinkedHashMap();//需要传递到远程节点的参数
		//log.log("call responser["+this+"]:"+this.getUrlBase()+requestURI, -1);
        
		//原始参数
		Enumeration parameters=request.getParameterNames();
    	try{
    		while(parameters.hasMoreElements()){
	        	String parameter=(String)parameters.nextElement();
	        	if(parameter.equals(Constants.J_ACTION_RESPONSER_SET)
	        			||(ignoreParameters!=null && JUtilString.contain(ignoreParameters, parameter))) continue;
        		String value=SysUtil.getHttpParameter(request, parameter);
        		map.put(parameter,value);
				//log.log("call this parameter "+parameter+"="+value, -1);
        	}
    	}catch(Exception e){}
    	
    	//序列化后的session对象
		Enumeration sessionNames=session.getAttributeNames();
		try{
    		while(sessionNames.hasMoreElements()){
	        	String parameter=(String)sessionNames.nextElement();
	        	if(parameter.equals(Constants.J_ACTION_RESPONSER)) continue;
        		Object value=session.getAttribute(parameter);
        		if(value instanceof Serializable) {
					//log.log("call responser["+this+"] session object "+parameter+"="+value, -1);
	        		map.put(Constants.J_ACTION_RESPONSER_SESSION_PREFIX+parameter, JObject.serializable2String((Serializable)value, false));
        		}
        	}
    	}catch(Exception e){}
		
		//设置远程调用标志
		map.put(Constants.J_ACTION_RESPONSER_FROM, SysConfig.getSysId());
		
		//交互密钥
		map.put(Constants.J_ACTION_RESPONSER_KEY, this.getKey());

		try {
			JHttpContext httpContext=new JHttpContext();
			httpContext.setRequestEncoding(SysConfig.sysEncoding);
			JHttp http=JHttp.getInstance();
			String url=this.getUrlBase()+requestURI;
			http.post(httpContext, null, url, map, SysConfig.sysEncoding);
			//log.log("call responser["+this+"] result "+resp, -1);
	    	
	    	return httpContext;
		}catch(Exception e) {
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
	}
	
	@Override
	public String toString() {
		StringBuffer s=new StringBuffer();
		s.append("{\"id\":\""+this.id+"\"");
		s.append(",\"name\":\""+this.name+"\"");
		s.append(",\"urlBase\":\""+this.urlBase+"\"");
		s.append(",\"key\":\""+this.key+"\"");
		s.append(",\"urls\":[");
		for(int i=0; this.urlPatterns!=null && i<this.urlPatterns.length; i++) {
			if(i>0) s.append(",");
			s.append("{\"url\":\""+this.urlPatterns[i]+"\"}");
		}
		s.append("]}");
		return s.toString();
	}
}
