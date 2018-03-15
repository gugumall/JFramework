package j.security;

import j.app.webserver.JSession;
import j.cache.CachedMap;
import j.cache.JCacheParams;
import j.common.JObject;
import j.log.Logger;
import j.service.Constants;
import j.sys.SysUtil;
import j.util.ConcurrentMap;
import j.util.JUtilMath;
import j.util.JUtilString;

import java.rmi.RemoteException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author 肖炯
 *
 */
public class VerifyCodeServiceImpl extends VerifyCodeServiceAbstract implements Runnable{
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.create(VerifyCodeServiceImpl.class);
	private ConcurrentMap caches=new ConcurrentMap();
	private boolean removerStarted=false;
	
	/**
	 * 
	 * @param cacheId
	 * @return
	 */
	private CachedMap createCache(String cacheId){
		if(!removerStarted){
			Thread thread=new Thread(this);
			thread.start();
			log.log("verify code remover thread started.",-1);
		}
		
		try{
			return new CachedMap(cacheId);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			try{
				Thread.sleep(10000);
			}catch(Exception ex){}
			return createCache(cacheId);
		}
	}
	
	/**
	 * 
	 * @param cacheId
	 * @return
	 */
	private CachedMap cache(String cacheId){
		if(!caches.containsKey(cacheId)){
			caches.put(cacheId,createCache(cacheId));
		}
		return (CachedMap)caches.get(cacheId);
	}

	/*
	 * (non-Javadoc)
	 * @see j.security.VerifyCodeService#g(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, long)
	 */
	public String g(String clientUuid, String md54Service,String cacheId, String uuid,int type,int length,long timeout) throws RemoteException {
		if(type!=VerifyCode.TYPE_CHAR&&type!=VerifyCode.TYPE_NUMBER){
			return "";
		}
		
		if(uuid==null||"".equals(uuid)){
			return "";
		}
		
		if(length<1||length>64){
			return "";
		}
		
		try{
			auth(clientUuid,"g",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}
		
		
		
		String code="";
		if(type==VerifyCode.TYPE_CHAR){
			code=JUtilString.randomStr(length);
		}else if(type==VerifyCode.TYPE_NUMBER){
			code=JUtilString.randomNum(length);
		}else{
			return "";
		}
		
		VerifyCodeBean vcb=new VerifyCodeBean(uuid,code,timeout);
		try{
			cache(cacheId).addOne(uuid, vcb);
			
			return code==null?"":code;
		}catch(Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * @see j.security.VerifyCodeService#g(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void g(JSession jsession, HttpSession session,HttpServletRequest request, HttpServletResponse response)throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		
		String uuid=SysUtil.getHttpParameter(request,"uuid");
		String type=SysUtil.getHttpParameter(request,"type");
		String length=SysUtil.getHttpParameter(request,"length");
		String timeout=SysUtil.getHttpParameter(request,"timeout");
		String cacheId=SysUtil.getHttpParameter(request,"cacheId");
		
		if(!JUtilMath.isInt(type)
				||!JUtilMath.isInt(length)
				||!JUtilMath.isLong(timeout)){
			jsession.resultString="";
		}
		
		jsession.resultString=this.g(clientUuid, md54Service,cacheId, uuid, Integer.parseInt(type),Integer.parseInt(length), Long.parseLong(timeout));
	}

	/*
	 * (non-Javadoc)
	 * @see j.security.VerifyCodeService#c(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String c(String clientUuid, String md54Service,String cacheId, String uuid,String code) throws RemoteException {
		try{
			auth(clientUuid,"c",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}
		
		
		try{
			VerifyCodeBean vcb=(VerifyCodeBean)cache(cacheId).get(new JCacheParams(uuid)); 
			if(vcb==null) return "false";
			
			return (code.equalsIgnoreCase(vcb.getCode())&&SysUtil.getNow()-vcb.getTime()<vcb.getTimeout())?"true":"false";
		}catch(Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return "false";
		}
	}

	/*
	 * (non-Javadoc)
	 * @see j.security.VerifyCodeService#c(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void c(JSession jsession, HttpSession session,HttpServletRequest request, HttpServletResponse response)throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		
		String uuid=SysUtil.getHttpParameter(request,"uuid");
		String code=SysUtil.getHttpParameter(request,"code");
		String cacheId=SysUtil.getHttpParameter(request,"cacheId");
		
		jsession.resultString=c(clientUuid,md54Service,cacheId,uuid,code);
	}

	/*
	 * (non-Javadoc)
	 * @see j.security.VerifyCodeService#exists(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public VerifyCodeBean exists(String clientUuid, String md54Service,String cacheId, String uuid) throws RemoteException {
		try{
			auth(clientUuid,"exists",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}
		
		
		try{
			return (VerifyCodeBean)cache(cacheId).get(new JCacheParams(uuid));
		}catch(Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see j.security.VerifyCodeService#c(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void exists(JSession jsession, HttpSession session,HttpServletRequest request, HttpServletResponse response)throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		
		String uuid=SysUtil.getHttpParameter(request,"uuid");
		String cacheId=SysUtil.getHttpParameter(request,"cacheId");
		try{
			jsession.resultString=JObject.serializable2String(exists(clientUuid,md54Service,cacheId,uuid));
		}catch(Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}
	


	/*
	 * (non-Javadoc)
	 * @see j.security.VerifyCodeService#exists(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void remove(String clientUuid, String md54Service,String cacheId, String uuid) throws RemoteException {
		try{
			auth(clientUuid,"remove",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}
		
		try{
			cache(cacheId).remove(new JCacheParams(uuid));
		}catch(Exception e){
			log.log(e, Logger.LEVEL_ERROR);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see j.security.VerifyCodeService#c(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void remove(JSession jsession, HttpSession session,HttpServletRequest request, HttpServletResponse response)throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		
		String uuid=SysUtil.getHttpParameter(request,"uuid");
		String cacheId=SysUtil.getHttpParameter(request,"cacheId");
		try{
			remove(clientUuid,md54Service,cacheId,uuid);
			jsession.resultString=Constants.INVOKING_DONE;
		}catch(Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			

			try{
				List cs=caches.listValues();
				for(int i=0;i<cs.size();i++){
					CachedMap c=(CachedMap)cs.get(i);
					c.remove(new JCacheParams(new VerifyCodeRemover()));
				}
			}catch(Exception e){
				log.log(e, Logger.LEVEL_ERROR);
			}
		}
	}
}
