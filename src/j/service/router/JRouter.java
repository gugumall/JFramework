package j.service.router;

import j.app.webserver.JSession;
import j.service.server.ServiceBase;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author 肖炯
 *
 */
public interface JRouter extends Remote {	
	/**
	 * 
	 * @param routerConfig
	 * @throws RemoteException
	 */
	public void setRouterConfig(RouterConfig routerConfig) throws RemoteException;
	
	/**
	 * 
	 * @throws RemoteException
	 */
	public RouterConfig getRouterConfig() throws RemoteException;
	
	/**
	 * 
	 * @throws RemoteException
	 */
	public void startup() throws RemoteException;
	
	
	/**
	 * 
	 * @throws RemoteException
	 */
	public void shutdown() throws RemoteException;
	
	/**
	 * 
	 * @param clientUuid
	 * @param code
	 * @param uuid
	 * @param rmiiiop
	 * @param http
	 * @param interfaceClassName
	 * @param md54Routing
	 * @return
	 * @throws RemoteException
	 */
	public String register(String clientUuid,String code, String uuid, String rmiiiop, String http,String interfaceClassName,String md54Routing) throws RemoteException;

	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void register(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;


	/**
	 * 
	 * @param clientUuid
	 * @param code
	 * @param uuid
	 * @param md54Routing
	 * @return
	 * @throws RemoteException
	 */
	public String unregister(String clientUuid,String code,String uuid,String md54Routing) throws RemoteException;


	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void unregister(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;

	
	/**
	 * 
	 * @param clientUuid
	 * @param code
	 * @param md54Routing
	 * @return
	 * @throws RemoteException
	 */
	public ServiceBase service(String clientUuid,String code,String md54Routing) throws RemoteException;

	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void service(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	/**
	 * 
	 * @param clientUuid
	 * @param code
	 * @param md54Routing
	 * @return
	 * @throws RemoteException
	 */
	public ServiceBase[] getAllServiceNodeAvailable(String clientUuid,String code,String md54Routing) throws RemoteException;

	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void getAllServiceNodeAvailable(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
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
