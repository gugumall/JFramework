package j.service.server;

import j.app.webserver.JSession;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 所有基于此框架的服务接口类都必须继承此类
 * 还有一个所有服务方法必须遵循的规则在此没有体现，那就是每个服务方法的前2个参数都必须是：
 * String clientUuid, String md54Service
 * 即客户节点的uuid，客户节点传过来的md5校验串
 * @author JFramework
 *
 */
public interface ServiceBase extends Remote{	
	/**
	 * 
	 * @param config
	 * @throws RemoteException
	 */
	public void setServiceConfig(ServiceConfig config) throws RemoteException;
	
	/**
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public ServiceConfig getServiceConfig() throws RemoteException;
	
	/**
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public void init() throws RemoteException;
	
	/**
	 * 当客户节点获取服务入口或调用服务时需要调用此方法进行md5校验
	 * 服务实现类的每个方法最开始都应该调用auth方法确保访问是合法的
	 * 如hello方法，最开始应该是如下代码
	 * try{
	 * 		auth(clientUuid,"hello",md54Service);
	 * }catch(RemoteException e){
	 * 		throw new RemoteException(Constants.SERVICE_AUTH_FAILED);
	 * }
	 * @param clientUuid
	 * @param method
	 * @param md54Service
	 * @return
	 * @throws RemoteException
	 */
	public String auth(String clientUuid, String method, String md54Service) throws RemoteException;
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 * @throws RemoteException
	 */
	public void auth(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	/**
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public String heartbeat() throws RemoteException;
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void heartbeat(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
}
