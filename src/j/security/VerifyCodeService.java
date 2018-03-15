package j.security;

import j.app.webserver.JSession;
import j.service.server.ServiceBase;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author 肖炯
 *
 */
public interface VerifyCodeService extends ServiceBase{
	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param uuid
	 * @param type
	 * @param length
	 * @param timeout
	 * @return
	 * @throws RemoteException
	 */
	public String g(String clientUuid, String md54Service,String cacheId,String uuid,int type,int length,long timeout) throws RemoteException;
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void g(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param uuid
	 * @param code
	 * @return
	 * @throws RemoteException
	 */
	public String c(String clientUuid, String md54Service,String cacheId,String uuid,String code) throws RemoteException;
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void c(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param uuid
	 * @return
	 * @throws RemoteException
	 */
	public VerifyCodeBean exists(String clientUuid, String md54Service,String cacheId,String uuid) throws RemoteException;
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void exists(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param uuid
	 * @return
	 * @throws RemoteException
	 */
	public void remove(String clientUuid, String md54Service,String cacheId,String uuid) throws RemoteException;
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void remove(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
}
