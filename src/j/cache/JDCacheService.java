package j.cache;

import j.app.webserver.JSession;
import j.service.server.ServiceBase;
import j.util.ConcurrentList;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author JFramework
 *
 */
public interface JDCacheService extends ServiceBase {	
	/**
	 * 
	 * @param cacheId
	 * @param unitType
	 * @param lifeCircleType
	 */
	public void createUnit(String cacheId,int unitType,int lifeCircleType) throws RemoteException;
		
	/**
	 * 
	 * @param cacheId
	 * @throws RemoteException
	 */
	public void setActiveTime(String cacheId) throws RemoteException;

	/**
	 * 
	 * @param cacheId
	 * @param key
	 * @param value
	 * @throws RemoteException
	 */
	public void addOne(String cacheId,Object key,Object value) throws RemoteException;

	/**
	 * 
	 * @param cacheId
	 * @param mappings
	 * @throws RemoteException
	 */
	public void addAll(String cacheId,Map mappings) throws RemoteException;

	/**
	 * 
	 * @param cacheId
	 * @param value
	 * @throws RemoteException
	 */
	public void addOne(String cacheId,Object value) throws RemoteException;

	/**
	 * 
	 * @param cacheId
	 * @param value
	 * @throws RemoteException
	 */
	public void addOneIfNotContains(String cacheId, Object value) throws RemoteException;

	
	/**
	 * 
	 * @param cacheId
	 * @param values
	 * @throws RemoteException
	 */
	public void addAll(String cacheId,Collection values) throws RemoteException;
	

	/**
	 * 
	 * @param cacheId
	 * @param params
	 * @return
	 * @throws RemoteException
	 */
	public boolean contains(String cacheId,JCacheParams params) throws RemoteException;

	/**
	 * 
	 * @param cacheId
	 * @return
	 * @throws RemoteException
	 */
	public int size(String cacheId) throws RemoteException;

	/**
	 * 
	 * @param cacheId
	 * @param params
	 * @return
	 * @throws RemoteException
	 */
	public int size(String cacheId,JCacheParams params) throws RemoteException;

	/**
	 * 
	 * @param cacheId
	 * @param params
	 * @return
	 * @throws RemoteException
	 */
	public Object get(String cacheId,JCacheParams params) throws RemoteException;

	/**
	 * 
	 * @param cacheId
	 * @param params
	 * @throws RemoteException
	 */
	public void remove(String cacheId,JCacheParams params) throws RemoteException;
	
	/**
	 * 
	 * @param cacheId
	 * @throws RemoteException
	 */
	public void clear(String cacheId) throws RemoteException;

	/**
	 * 
	 * @param cacheId
	 * @param params
	 * @throws RemoteException
	 */
	public void update(String cacheId,JCacheParams params) throws RemoteException;

	/**
	 * 
	 * @param cacheId
	 * @param params
	 * @throws RemoteException
	 */
	public void updateCollection(String cacheId,JCacheParams params) throws RemoteException;
	
	/**
	 * 
	 * @param cacheId
	 * @param params
	 * @return
	 * @throws RemoteException
	 */
	public Object sub(String cacheId,JCacheParams params) throws RemoteException;
	
	/**
	 * 
	 * @param cacheId
	 * @param params
	 * @return
	 * @throws RemoteException
	 */
	public ConcurrentList keys(String cacheId,JCacheParams params) throws RemoteException;
	
	/**
	 * 
	 * @param cacheId
	 * @param params
	 * @return
	 * @throws RemoteException
	 */
	public ConcurrentList values(String cacheId,JCacheParams params) throws RemoteException;
	
	
	///////////http channels/////////////
	public void createUnit(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;

	public void setActiveTime(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	public void addOne(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	public void addAll(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	public void addOneIfNotContains(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	public void contains(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	public void size(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	public void get(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	public void remove(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	public void clear(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;

	public void update(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	public void updateCollection(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	public void sub(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	public void keys(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	
	public void values(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
}
