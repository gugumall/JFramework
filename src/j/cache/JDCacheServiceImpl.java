package j.cache;

import j.app.webserver.JSession;
import j.common.JObject;
import j.log.Logger;
import j.service.Constants;
import j.service.server.ServiceConfig;
import j.service.server.ServiceManager;
import j.sys.SysUtil;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilList;
import j.util.JUtilRandom;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author JFramework
 *
 */
public class JDCacheServiceImpl extends JDCacheServiceAbstract implements Runnable{
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.create(JCacheDefault.class);	
	protected ConcurrentMap units=new ConcurrentMap();
	protected ConcurrentMap synchronizers=new ConcurrentMap();
	

	/**
	 * 
	 *
	 */
	public JDCacheServiceImpl() {
		super();
		
		Thread thread=new Thread(this);
		thread.start();
		log.log("JDCacheServiceImpl monitor thread started.",-1);
	}
	
	/**
	 * 
	 * @param task
	 * @param unitType
	 * @param lifeCircleType
	 * @throws RemoteException
	 */
	public void addTask(Object[] task) throws RemoteException{
		if(this.synchronizers.isEmpty()){//启动同步线程
			int synchronizerThreads=JCacheConfig.getSynchronizers();
			try{//创建同步线程	
				ServiceConfig[] members=ServiceManager.getServices(this.getServiceConfig().getCode(),true);
				for(int t=0;t<members.length;t++){
					if(members[t].getUuid().equals(this.getServiceConfig().getUuid())) continue;
					
					List syns=(List)synchronizers.get(members[t].getUuid());
					if(syns==null) syns=new ArrayList();
					
					for(int i=0;i<synchronizerThreads;i++){
						JDCacheSynchronizer syn=new JDCacheSynchronizer(members[t].getUuid());
						Thread threadSyn=new Thread(syn);
						threadSyn.start();
						log.log("JDCacheSynchronizer["+i+"] for "+members[t].getCode()+","+members[t].getUuid()+" started.",-1);
						syns.add(syn);
						
						synchronizers.put(members[t].getUuid(),syns);
					}
				}
			}catch(Exception e){
				log.log(e,Logger.LEVEL_FATAL);
			}
		}
		
		ServiceConfig[] members=ServiceManager.getServices(this.getServiceConfig().getCode(),true);
		for(int t=0;t<members.length;t++){
			if(members[t].getUuid().equals(this.getServiceConfig().getUuid())) continue;
			
			getSynchronizer(members[t].getUuid()).addTask(task);
		}
	}
	
	/**
	 * 
	 * @param serviceUuid
	 * @return
	 */
	public JDCacheSynchronizer getSynchronizer(String serviceUuid){
		List syns=(List)synchronizers.get(serviceUuid);
		return (JDCacheSynchronizer)syns.get(JUtilRandom.nextInt(syns.size()));
	}
	
	//为Nvwa重启时保持已有对象
	public ConcurrentMap getUnits(){
		return this.units;
	}
	
	public void setUnits(ConcurrentMap units){
		this.units=units;
	}
	//为Nvwa重启时保持对象 end
	
	/**
	 * 
	 * @param cacheId
	 * @throws RemoteException
	 */
	private JCacheUnit checkStatus(String cacheId) throws RemoteException{
		if(cacheId==null){
			throw new RemoteException("the cache id is null.");
		}
		
		if(cacheId.startsWith("syn:")){//如果是同步，自动创建cache unit
			String type=cacheId.substring(cacheId.lastIndexOf(",")+1);
			
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
			if(units.get(cacheId)==null){
				int unitType=Integer.parseInt(type.substring(0,type.indexOf(":")));
				int lifeCircleType=Integer.parseInt(type.substring(type.indexOf(":")+1));
				
				try{
					if(unitType==JCache.UNIT_MAP){
						units.put(cacheId,new JCacheUnitMap(lifeCircleType));
					}else if(unitType==JCache.UNIT_LIST){
						units.put(cacheId,new JCacheUnitList(lifeCircleType));
					}
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					throw new RemoteException(e.getMessage());
				}
			}
		}else if(units.get(cacheId)==null){
			throw new RemoteException("the cache unit is not exists.");
		}
		
		return (JCacheUnit)units.get(cacheId);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#create(java.lang.String, int, int)
	 */
	public void createUnit(String cacheId, int unitType, int lifeCircleType) throws RemoteException{
		if(!cacheId.startsWith("syn:")){
			addTask(new Object[]{"createUnit",cacheId,new Integer(unitType),new Integer(lifeCircleType)});
		}else{
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
		}
		
		try{
			if(units.containsKey(cacheId)) return;
			if(unitType==JCache.UNIT_MAP){
				units.put(cacheId,new JCacheUnitMap(lifeCircleType));
			}else if(unitType==JCache.UNIT_LIST){
				units.put(cacheId,new JCacheUnitList(lifeCircleType));
			}
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#setActiveTime(java.lang.String)
	 */
	public void setActiveTime(String cacheId) throws RemoteException{
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			addTask(new Object[]{"setActiveTime",cacheId,new Integer(unit.getUnitType()),new Integer(unit.getLifeCircleType())});
		}else{
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
		}
		unit.using();
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addOne(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void addOne(String cacheId, Object key, Object value) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			addTask(new Object[]{"addOne-key-value",cacheId,key,value,new Integer(unit.getUnitType()),new Integer(unit.getLifeCircleType())});
		}else{
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
		}
		
		try{
			unit.addOne(key,value);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addAll(java.lang.String, java.util.Map)
	 */
	public void addAll(String cacheId, Map mappings) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			addTask(new Object[]{"addAll-mappings",cacheId,mappings,new Integer(unit.getUnitType()),new Integer(unit.getLifeCircleType())});
		}else{
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
		}
		
		try{
			unit.addAll(mappings);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addOne(java.lang.String, java.lang.Object)
	 */
	public void addOne(String cacheId, Object value) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			addTask(new Object[]{"addOne-value",cacheId,value,new Integer(unit.getUnitType()),new Integer(unit.getLifeCircleType())});
		}else{
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
		}
		
		try{
			unit.addOne(value);		
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addOneIfNotContains(java.lang.String, java.lang.Object)
	 */
	public void addOneIfNotContains(String cacheId, Object value) throws RemoteException{
		JCacheUnit unit=checkStatus(cacheId);
		if(!cacheId.startsWith("syn:")){
			addTask(new Object[]{"addOneIfNotContains-value",cacheId,value,new Integer(unit.getUnitType()),new Integer(unit.getLifeCircleType())});
		}else{
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
		}
			
		try{
			unit.addOneIfNotContains(value);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addAll(java.lang.String, java.util.Collection)
	 */
	public void addAll(String cacheId, Collection values) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);
		if(!cacheId.startsWith("syn:")){
			addTask(new Object[]{"addAll-values",cacheId,values,new Integer(unit.getUnitType()),new Integer(unit.getLifeCircleType())});
		}else{
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
		}
			
		try{
			unit.addAll(values);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#contains(java.lang.String, j.cache.JCacheParams)
	 */
	public boolean contains(String cacheId, JCacheParams params) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		try{
			return unit.contains(params);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#size(java.lang.String)
	 */
	public int size(String cacheId) throws RemoteException{
		JCacheUnit unit=checkStatus(cacheId);	
		try{
			return unit.size();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#get(java.lang.String, j.cache.JCacheParams)
	 */
	public Object get(String cacheId, JCacheParams params) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		try{
			return unit.get(params);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#remove(java.lang.String, j.cache.JCacheParams)
	 */
	public void remove(String cacheId, JCacheParams params) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			addTask(new Object[]{"remove",cacheId,params,new Integer(unit.getUnitType()),new Integer(unit.getLifeCircleType())});
		}else{
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
		}
		
		try{
			unit.remove(params);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#clear(java.lang.String)
	 */
	public void clear(String cacheId) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			addTask(new Object[]{"clear",cacheId,new Integer(unit.getUnitType()),new Integer(unit.getLifeCircleType())});
		}else{
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
		}
		
		try{
			unit.clear();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#update(java.lang.String, j.cache.JCacheParams)
	 */
	public void update(String cacheId, JCacheParams params) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			addTask(new Object[]{"update",cacheId,params,new Integer(unit.getUnitType()),new Integer(unit.getLifeCircleType())});
		}else{
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
		}
		
		try{
			unit.update(params);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#sub(java.lang.String, j.cache.JCacheParams)
	 */
	public Object sub(String cacheId, JCacheParams params) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		try{
			return unit.sub(params);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#keys(java.lang.String, j.cache.JCacheParams)
	 */
	public ConcurrentList keys(String cacheId, JCacheParams params) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		try{
			return unit.keys(params);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see j.cache.JDCacheService#values(java.lang.String, j.cache.JCacheParams)
	 */
	public ConcurrentList values(String cacheId, JCacheParams params) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);
		try{	
			return unit.values(params);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#createUnit(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void createUnit(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			int unitType=Integer.parseInt(SysUtil.getHttpParameter(request,"unitType"));
			int lifeCircleType=Integer.parseInt(SysUtil.getHttpParameter(request,"lifeCircleType"));
			
			this.createUnit(cacheId,unitType,lifeCircleType);
			
			jsession.resultString=Constants.INVOKING_DONE;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.service.server.ServiceBase#setActiveTime(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void setActiveTime(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			
			this.setActiveTime(cacheId);
			
			jsession.resultString=Constants.INVOKING_DONE;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#addOne(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void addOne(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String key=SysUtil.getHttpParameter(request,"key");
			String value=SysUtil.getHttpParameter(request,"value");

			if(key!=null){
				this.addOne(cacheId,JObject.string2Serializable(key),JObject.string2Serializable(value));
			}else{
				this.addOne(cacheId,JObject.string2Serializable(value));		
			}
			
			jsession.resultString=Constants.INVOKING_DONE;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#addAll(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void addAll(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String mappings=SysUtil.getHttpParameter(request,"mappings");
			String values=SysUtil.getHttpParameter(request,"values");
			
			if(mappings!=null){
				this.addAll(cacheId,(Map)JObject.string2Serializable(mappings));				
			}else if(values!=null){
				this.addAll(cacheId,(List)JObject.string2Serializable(values));			
			}
			
			jsession.resultString=Constants.INVOKING_DONE;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#addOneIfNotContains(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void addOneIfNotContains(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String value=SysUtil.getHttpParameter(request,"value");
			
			this.addOne(cacheId,JObject.string2Serializable(value));
			
			jsession.resultString=Constants.INVOKING_DONE;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#contains(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void contains(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			jsession.resultString=""+this.contains(cacheId,(JCacheParams)JObject.string2Serializable(params));
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#size(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void size(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			
			jsession.resultString=""+this.size(cacheId);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#get(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void get(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			jsession.resultString=JObject.serializable2String((Serializable)this.get(cacheId,(JCacheParams)JObject.string2Serializable(params)));
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#remove(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void remove(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			this.remove(cacheId,(JCacheParams)JObject.string2Serializable(params));
			
			jsession.resultString=Constants.INVOKING_DONE;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#clear(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void clear(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");

			this.clear(cacheId);
			
			jsession.resultString=Constants.INVOKING_DONE;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#update(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void update(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			this.update(cacheId,(JCacheParams)JObject.string2Serializable(params));
			
			jsession.resultString=Constants.INVOKING_DONE;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#sub(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void sub(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			jsession.resultString=JObject.serializable2String((Serializable)this.sub(cacheId,(JCacheParams)JObject.string2Serializable(params)));
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#keys(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void keys(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			jsession.resultString=JObject.serializable2String((Serializable)this.keys(cacheId,(JCacheParams)JObject.string2Serializable(params)));
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JDCacheService#values(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void values(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			jsession.resultString=JObject.serializable2String((Serializable)this.values(cacheId,(JCacheParams)JObject.string2Serializable(params)));
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		while(true){
			try{
				Thread.sleep(30000);
			}catch(Exception e){}
			
			try{
				List keys=units.listKeys();
				for(int i=0;i<keys.size();i++){
					Object key=keys.get(i);
					JCacheUnit unit=(JCacheUnit)units.get(key);
					if(unit.isTimeout()){
						unit.clear();
						units.remove(key);
						unit=null;
					}
				}
				JUtilList.clear_AllNull(keys);
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
		}
	}
}
