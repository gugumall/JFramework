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
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param unitType
	 * @param lifeCircleType
	 * @throws RemoteException
	 */
	public void createUnit(String clientUuid, String md54Service,String cacheId,int unitType,int lifeCircleType) throws RemoteException;
		
	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @throws RemoteException
	 */
	public void setActiveTime(String clientUuid, String md54Service,String cacheId) throws RemoteException;

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param key
	 * @param value
	 * @throws RemoteException
	 */
	public void addOne(String clientUuid, String md54Service,String cacheId,Object key,Object value) throws RemoteException;

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param mappings
	 * @throws RemoteException
	 */
	public void addAll(String clientUuid, String md54Service,String cacheId,Map mappings) throws RemoteException;

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param value
	 * @throws RemoteException
	 */
	public void addOne(String clientUuid, String md54Service,String cacheId,Object value) throws RemoteException;

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param value
	 * @throws RemoteException
	 */
	public void addOneIfNotContains(String clientUuid, String md54Service,String cacheId, Object value) throws RemoteException;

	
	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param values
	 * @throws RemoteException
	 */
	public void addAll(String clientUuid, String md54Service,String cacheId,Collection values) throws RemoteException;
	

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param params
	 * @return
	 * @throws RemoteException
	 */
	public boolean contains(String clientUuid, String md54Service,String cacheId,JCacheParams params) throws RemoteException;

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @return
	 * @throws RemoteException
	 */
	public int size(String clientUuid, String md54Service,String cacheId) throws RemoteException;

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param params
	 * @return
	 * @throws RemoteException
	 */
	public int size(String clientUuid, String md54Service,String cacheId,JCacheParams params) throws RemoteException;

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param params
	 * @return
	 * @throws RemoteException
	 */
	public Object get(String clientUuid, String md54Service,String cacheId,JCacheParams params) throws RemoteException;

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param params
	 * @throws RemoteException
	 */
	public void remove(String clientUuid, String md54Service,String cacheId,JCacheParams params) throws RemoteException;
	
	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @throws RemoteException
	 */
	public void clear(String clientUuid, String md54Service,String cacheId) throws RemoteException;

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param params
	 * @throws RemoteException
	 */
	public void update(String clientUuid, String md54Service,String cacheId,JCacheParams params) throws RemoteException;

	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param params
	 * @throws RemoteException
	 */
	public void updateCollection(String clientUuid, String md54Service,String cacheId,JCacheParams params) throws RemoteException;
	
	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param params
	 * @return
	 * @throws RemoteException
	 */
	public Object sub(String clientUuid, String md54Service,String cacheId,JCacheParams params) throws RemoteException;
	
	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param params
	 * @return
	 * @throws RemoteException
	 */
	public ConcurrentList keys(String clientUuid, String md54Service,String cacheId,JCacheParams params) throws RemoteException;
	
	/**
	 * 
	 * @param clientUuid
	 * @param md54Service
	 * @param cacheId
	 * @param params
	 * @return
	 * @throws RemoteException
	 */
	public ConcurrentList values(String clientUuid, String md54Service,String cacheId,JCacheParams params) throws RemoteException;
	
	
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
