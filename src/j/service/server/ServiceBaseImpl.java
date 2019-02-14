package j.service.server;

import j.app.webserver.JHandler;
import j.app.webserver.JSession;
import j.service.Client;
import j.service.Constants;
import j.sys.SysUtil;
import j.util.JUtilMD5;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 所有基于此框架的服务实现类都必须继承此类
 * 还有一个所有服务方法必须遵循的规则在此没有体现，那就是每个服务方法的前2个参数都必须是：
 * String clientUuid, String md54Service
 * 即客户节点的uuid，客户节点传过来的md5校验串
 * 
 * 继承JHandler实现基于“请求-应答框架”的http接口，需在actions.service.xml中配置相关action
 * @author 肖炯
 *
 */
public class ServiceBaseImpl extends JHandler implements ServiceBase,Serializable{
	private static final long serialVersionUID = 1L;
	protected ServiceConfig serviceConfig;

	
	/*
	 *  (non-Javadoc)
	 * @see j.service.server.ServiceBase#setServiceConfig(j.service.server.ServiceConfig)
	 */
	public void setServiceConfig(ServiceConfig config) throws RemoteException{
		this.serviceConfig=config;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.service.server.ServiceBase#getServiceConfig()
	 */
	public ServiceConfig getServiceConfig() throws RemoteException{
		return this.serviceConfig;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.service.server.ServiceBase#init()
	 */
	public void init() throws RemoteException{
		//nothing to do by default
	}
	
	/**
	 * 当客户节点获取服务入口或调用服务时需要调用此方法进行md5校验
	 * 服务实现类的每个方法最开始都应该调用auth方法确保访问是合法的
	 * 如hello方法，最开始应该是如下代码
	 * try{
	 * 		auth(clientUuid,"hello",md54Service);
	 * }catch(RemoteException e){
	 * 		throw new RemoteException(Constants.AUTH_FAILED);
	 * }
	 * @param clientUuid
	 * @param method
	 * @param md54Service
	 * @return
	 * @throws RemoteException
	 */
	public String auth(String clientUuid, String method, String md54Service) throws RemoteException{
		if(serviceConfig==null) return Constants.AUTH_PASSED;
		if(method==null||serviceConfig.getMethod(method)==null){//获取服务入口或未在配置文件中定义方法		
			if(Constants.PRIVICY_PUBLIC.equalsIgnoreCase(serviceConfig.getPrivacy())){
				return Constants.AUTH_PASSED;
			}else if(Constants.PRIVICY_MD5.equalsIgnoreCase(serviceConfig.getPrivacy())){		
				Client client=serviceConfig.getClient(clientUuid);
				if(client==null) throw new RemoteException(Constants.AUTH_FAILED);
				
				String md5="";
				md5+=clientUuid;
				md5+=method;
				md5+=client.getKey();
				md5=JUtilMD5.MD5EncodeToHex(md5);
				
				if(!md5.equalsIgnoreCase(md54Service)) throw new RemoteException(Constants.AUTH_FAILED);
				else return Constants.AUTH_PASSED;
			}else{//未实现的隐私策略
				throw new RemoteException(Constants.AUTH_FAILED);
			}
		}else{//调用方法
			Method m=serviceConfig.getMethod(method);
			
			//不需要认证
			if(Constants.PRIVICY_PUBLIC.equalsIgnoreCase(m.getPrivacy())){
				return Constants.AUTH_PASSED;
			}else if(Constants.PRIVICY_MD5.equalsIgnoreCase(m.getPrivacy())){		
				Client client=serviceConfig.getClient(clientUuid);
				if(client==null) throw new RemoteException(Constants.AUTH_FAILED);
				
				String md5="";
				md5+=clientUuid;
				md5+=method;
				md5+=client.getKey();
				md5=JUtilMD5.MD5EncodeToHex(md5);
				
				if(!md5.equalsIgnoreCase(md54Service)) throw new RemoteException(Constants.AUTH_FAILED);
				else return Constants.AUTH_PASSED;
			}else{//未实现的隐私策略
				throw new RemoteException(Constants.AUTH_FAILED);
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.service.server.ServiceBase#auth(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void auth(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException{
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String method=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_SERVICE_METHOD);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		
		if(method==null||serviceConfig.getMethod(method)==null){//获取服务入口			
			//不需要认证
			if(Constants.PRIVICY_PUBLIC.equalsIgnoreCase(serviceConfig.getPrivacy())){
				jsession.resultString=Constants.AUTH_PASSED;
			}else if(Constants.PRIVICY_MD5.equalsIgnoreCase(serviceConfig.getPrivacy())){		
				Client client=serviceConfig.getClient(clientUuid);
				if(client==null){
					jsession.resultString=Constants.AUTH_FAILED;
					return;
				}
				
				String md5="";
				md5+=clientUuid;
				md5+=method;
				md5+=client.getKey();
				md5=JUtilMD5.MD5EncodeToHex(md5);
				
				if(!md5.equalsIgnoreCase(md54Service)){
					jsession.resultString=Constants.AUTH_FAILED;
				}else{
					jsession.resultString=Constants.AUTH_PASSED;
				}
			}else{//未实现的隐私策略
				jsession.resultString=Constants.AUTH_FAILED;
			}
		}else{//调用方法
			Method m=serviceConfig.getMethod(method);
			
			//不需要认证
			if(Constants.PRIVICY_PUBLIC.equalsIgnoreCase(m.getPrivacy())){
				jsession.resultString=Constants.AUTH_PASSED;
			}else if(Constants.PRIVICY_MD5.equalsIgnoreCase(m.getPrivacy())){		
				Client client=serviceConfig.getClient(clientUuid);
				if(client==null){
					jsession.resultString=Constants.AUTH_FAILED;
				}
				
				String md5="";
				md5+=clientUuid;
				md5+=method;
				md5+=client.getKey();
				md5=JUtilMD5.MD5EncodeToHex(md5);
				
				if(!md5.equalsIgnoreCase(md54Service)){
					jsession.resultString=Constants.AUTH_FAILED;
				}else{
					jsession.resultString=Constants.AUTH_PASSED;
				}
			}else{//未实现的隐私策略
				jsession.resultString=Constants.AUTH_FAILED;
			}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.service.server.ServiceBaseInterface#heartbeat()
	 */
	public String heartbeat() throws RemoteException {
		return Constants.STATUS_OK;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.service.server.ServiceBase#heartbeat(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void heartbeat(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		jsession.resultString=Constants.STATUS_OK;
	}
}
