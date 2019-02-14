package j.app.sso;

import j.app.Constants;
import j.app.webserver.Handler;
import j.app.webserver.Handlers;
import j.common.JObject;
import j.http.JHttp;
import j.http.JHttpContext;
import j.log.Logger;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.JUtilDom4j;
import j.util.JUtilMD5;
import j.util.JUtilString;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 
 * @author 肖炯
 *
 */
public class LoginAgent implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.create(LoginAgent.class);
	
	protected String clientId;
	protected boolean avail;
	protected String[] deny;
	protected String[] allow;
	protected boolean denyAll=false;
	protected boolean allowAll=false;
	protected Authenticator authenticator;//认证类
	protected String Interface;
	
	/**
	 * 
	 * @param _clientId
	 * @param _avail
	 * @param _forOthers
	 * @param _authenticator
	 * @param _Interface
	 */
	LoginAgent(String _clientId,String _avail,String _forOthers,String _authenticator,String _Interface){
		this.clientId=_clientId;
		this.avail="true".equalsIgnoreCase(_avail);
		
		String[] forOthers=_forOthers.split(";");
		
		for(int i=0;i<forOthers.length;i++){
			if(forOthers[i].equals("_DENY_ALL")) denyAll=true;
			else if(forOthers[i].equals("_ALLOW_ALL")) allowAll=true;
			else if(forOthers[i].startsWith("_DENY:")){
				deny=forOthers[i].substring(6).split(",");
			}else if(forOthers[i].startsWith("_ALLOW:")){
				allow=forOthers[i].substring(7).split(",");
			}
		}
		
		if(this.avail&&SysConfig.getSysId().equals(this.clientId)){
			try{
				this.authenticator=(Authenticator)Class.forName(_authenticator).newInstance();
			}catch(Exception e){
				this.authenticator=null;
				log.log(e,Logger.LEVEL_FATAL);
			}
		}
		
		this.Interface=_Interface;
	}
	
	/**
	 * 
	 * @return
	 */
	Authenticator getAuthenticator(){
		return authenticator;
	}
	
	/**
	 * 
	 * @param fromClientId
	 * @return
	 */
	boolean available(String fromClientId){
		if(!avail) return false;
		
		if(!clientId.equals(fromClientId)) {//用户不是来自本系统（本SSO Client）的网页上登录
			if(!forIt(fromClientId)) return false;//不为该SSO Client（fromClientId）提供用户验证
		}
		return true;
	}
	
	/**
	 * 向SSO Server 为以fromClientId为ID的SSO Client提供用户验证
	 * @param fromClientId
	 * @param request
	 * @return
	 */
	LoginResult login(String fromClientId,HttpServletRequest request){
		Client parent=parent();//关联的SSO Client信息
		
		String _Interface=this.Interface;
		if(!_Interface.startsWith("http")) _Interface=parent.getUrlDefault()+_Interface;//如使用相对地址，自动生成绝对地址
		
		String[] paraNamesAndQueryString=getParameters(request);//登录请求参数及相关详情
		String paraNames=paraNamesAndQueryString[0];
		String paraValues=paraNamesAndQueryString[1];
		String queryString=paraNamesAndQueryString[2];
		
		//把所有登录请求参数全部原样传送过去
		if(!paraNames.equals("")){
			if(_Interface.indexOf("&")>0||_Interface.indexOf("?")>0) _Interface+="&"+queryString.substring(1);
			else _Interface+=queryString;
		}
		
		//md5拼串 - 登录请求参数名（多个逗号分隔）+按参数名顺序累加的各参数值+提供验证的SSO Client与SSO Server交互的passport（密钥）
		//收到请求时，应该先获取_parameters参数，并按照其指明的参数顺序拼接各参数值，然后按照上述一样的方式得出md5值，与_verifier参数值相同才能通过验证
		//详见j.app.sso.SSOClient.login()
		//System.out.println("1 - "+paraNames+paraValues+parent.getPassport());
		String md5=JUtilMD5.MD5EncodeToHex(paraNames+paraValues+parent.getPassport());
		if(_Interface.indexOf("&")>0||_Interface.indexOf("?")>0) _Interface+="&_parameters="+paraNames+"&_verifier="+md5;
		else _Interface+="?_parameters="+paraNames+"&_verifier="+md5;
		_Interface+="&"+Constants.SSO_USER_IP+"="+JHttp.getRemoteIp(request);
		
		String userDomain=JUtilString.getProtocal(request.getRequestURL().toString())+"://"+JUtilString.getHost(request.getRequestURL().toString());
		_Interface+="&"+Constants.SSO_USER_DOMAIN+"="+JUtilString.encodeURI(userDomain,SysConfig.sysEncoding);
		//log.log("login agent interface:"+_Interface,-1);
		
		//发起验证请求并获得结果
		String result=null;
		try{
			JHttp http=JHttp.getInstance();
			JHttpContext context=http.get(null,null,_Interface,SysConfig.sysEncoding);
			
			//log.log("context.getStatus():"+context.getStatus(), -1);
			
			result=context.getStatus()==200?context.getResponseText():null;
			context.finalize();
			context=null;
		}catch(Exception e){
			log.log("agent login error,login agent interface - "+_Interface,-1);
			e.printStackTrace();
			result=null;
			log.log(e,Logger.LEVEL_FATAL);
		}
		
		//log.log("agent login result - "+result,-1);
		
		if(result==null){
			LoginResult loginResult=new LoginResult();
			loginResult.setResult(LoginResult.RESULT_ERROR);
			
			return loginResult;
		}
		
		if(result.indexOf("<"+Constants.SSO_USER_ID+">")>-1
				&&result.indexOf("<"+Constants.SSO_LOGIN_RESULT_CODE+">")>-1
				&&result.indexOf("<"+Constants.SSO_LOGIN_RESULT_MSG+">")>-1){
			Document doc=null;
			try{
				doc=JUtilDom4j.parseString(result,"UTF-8");
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
				LoginResult loginResult=new LoginResult();
				loginResult.setResult(LoginResult.RESULT_ERROR);
				
				return loginResult;
			}
			
			Element root=doc.getRootElement();

			String sysId=root.elementText(Constants.SSO_SYS_ID);
			String machineId=root.elementText(Constants.SSO_MACHINE_ID);
			String userId=root.elementText(Constants.SSO_USER_ID);
			String code=root.elementText(Constants.SSO_LOGIN_RESULT_CODE);
			String msg=root.elementText(Constants.SSO_LOGIN_RESULT_MSG);
		
			LoginResult loginResult=new LoginResult();
			loginResult.setSysId(sysId);
			loginResult.setMachineId(machineId);
			loginResult.setUserId(userId);
			loginResult.setResult(Integer.parseInt(code));
			loginResult.setResultMsg(msg);
			
			List ms=root.element("messages").elements();
			for(int i=0;i<ms.size();i++){
				Element m=(Element)ms.get(i);
				loginResult.setMessage(m.getName(),JObject.intSequence2String(m.getText()));
			}
			
			root=null;
			doc=null;
			
			return loginResult;
		}else{
			LoginResult loginResult=new LoginResult();
			loginResult.setResult(LoginResult.RESULT_ERROR);
			
			return loginResult;
		}
	}
	
	/**
	 * 是否为该SSO Client（fromClientId）提供用户验证
	 * @param fromClientId
	 * @return
	 */
	boolean forIt(String fromClientId){
		if(JUtilString.contain(deny,fromClientId)) return false;
		else if(JUtilString.contain(allow,fromClientId)) return true;
		else if(denyAll) return false;
		else if(allowAll) return true;
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	Client parent(){
		return SSOConfig.getSsoClientByIdOrUrl(this.clientId);
	}
	
	/**
	 * 
	 * @param request
	 * @return  [0]-参数名  [1]-参数值  [2]-query string 
	 */
	String[] getParameters(HttpServletRequest request){
		Handler handler=Handlers.getHandler("/ssoserver");
		
		String queryString="";
		String names="";
		String values="";
		
        int i=0;
        Enumeration parameters=request.getParameterNames();
    	try{
    		while(parameters.hasMoreElements()){
	        	String parameter=(String)parameters.nextElement();
	        	if(parameter.equals(handler.getRequestBy())
	        			||parameter.equalsIgnoreCase(Constants.SSO_USER_IP)) continue;
	        	
	        	names+=parameter+",";
	        
        		String value=SysUtil.getHttpParameter(request,parameter);
	        	if(value!=null){
	        		try{
	        			value=JUtilString.encodeURI(value,SysConfig.sysEncoding);
	        		}catch(Exception e){
	        			log.log(e,Logger.LEVEL_ERROR);
	        		}
	        		if(i==0){
	        			queryString+="?"+parameter+"="+value;
	        		}else{
	        			queryString+="&"+parameter+"="+value;
	        		}
	        		i++;
	        	}
	        	
	        	values+=value;
        	}
    	}catch(Exception e){}
    	
    	if(names.length()>0) names=names.substring(0,names.length()-1);
    	
        return new String[]{names,values,queryString};
	}	
}
