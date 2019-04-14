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
	private static Logger log=Logger.create(JDCacheServiceImpl.class);	
	protected ConcurrentMap<String,JCacheUnit> units=new ConcurrentMap<String,JCacheUnit>();//key-缓存单元ID，value-缓存单元
	protected ConcurrentMap<String,JDCacheSynchronizer> synchronizers=new ConcurrentMap<String,JDCacheSynchronizer>();//key-服务镜像的节点uuid，value-同步任务执行对象
	

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
	
	
	//////////////////////////////多镜像缓存同步////////////////////////
	//如缓存服务存在多个镜像节点，则除查询、获取类操作外，其它操作均会同步到其它镜像。
	//同步到其它镜像时，缓存ID的格式为  syn:实际缓存单元ID,缓存单元类型:缓存单元生命周期类型，镜像收到同步任务时，仅执行，不会再次同步给其它镜像。
	/**
	 * 添加多镜像缓存同步任务
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
					if(members[t].getUuid().equals(this.getServiceConfig().getUuid())) continue;//当前镜像，无需同步
					
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
		
		//向其它镜像节点的同步线程中添加同步任务
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
	//////////////////////////////多镜像缓存同步  END////////////////////////
	
	
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

	@Override
	public void createUnit(String clientUuid, String md54Service,String cacheId, int unitType, int lifeCircleType) throws RemoteException{
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"createUnit",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
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

	@Override
	public void setActiveTime(String clientUuid, String md54Service,String cacheId) throws RemoteException{
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"setActiveTime",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
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
	public void addOne(String clientUuid, String md54Service,String cacheId, Object key, Object value) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"addOne",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
			addTask(new Object[]{"addOne-key-value",cacheId,key,value,new Integer(unit.getUnitType()),new Integer(unit.getLifeCircleType())});
		}else{
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
		}
		
		try{
			//log.log("add key-value to "+cacheId+" via service(rmi channel)",-1);
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
	public void addAll(String clientUuid, String md54Service,String cacheId, Map mappings) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"addAll",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
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
	public void addOne(String clientUuid, String md54Service,String cacheId, Object value) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"addOne",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
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
	public void addOneIfNotContains(String clientUuid, String md54Service,String cacheId, Object value) throws RemoteException{
		JCacheUnit unit=checkStatus(cacheId);
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"addOneIfNotContains",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
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
	public void addAll(String clientUuid, String md54Service,String cacheId, Collection values) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"addAll",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
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
	public boolean contains(String clientUuid, String md54Service,String cacheId, JCacheParams params) throws RemoteException {
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"contains",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
		}
		
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
	public int size(String clientUuid, String md54Service,String cacheId) throws RemoteException{
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"size",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
		}
		
		JCacheUnit unit=checkStatus(cacheId);	
		try{
			return unit.size();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}
	
	public int size(String clientUuid, String md54Service,String cacheId, JCacheParams params) throws RemoteException{
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"size",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
		}
		
		JCacheUnit unit=checkStatus(cacheId);	
		try{
			return unit.size(params);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}
	
	public int[] sizes(String clientUuid, String md54Service,String cacheId, JCacheParams[] params) throws RemoteException{
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"sizes",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
		}
		
		JCacheUnit unit=checkStatus(cacheId);	
		try{
			int[] sizes=new int[params.length];
			for(int i=0;i<sizes.length;i++) sizes[i]=unit.size(params[i]);
			return sizes;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#get(java.lang.String, j.cache.JCacheParams)
	 */
	public Object get(String clientUuid, String md54Service,String cacheId, JCacheParams params) throws RemoteException {
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"get",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
		}
		
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
	public void remove(String clientUuid, String md54Service,String cacheId, JCacheParams params) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"remove",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
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
	public void clear(String clientUuid, String md54Service,String cacheId) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"clear",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
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
	public void update(String clientUuid, String md54Service,String cacheId, JCacheParams params) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"update",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
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
	 * @see j.cache.JCache#update(java.lang.String, j.cache.JCacheParams)
	 */
	public void updateCollection(String clientUuid, String md54Service,String cacheId, JCacheParams params) throws RemoteException {
		JCacheUnit unit=checkStatus(cacheId);	
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"updateCollection",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
			addTask(new Object[]{"updateCollection",cacheId,params,new Integer(unit.getUnitType()),new Integer(unit.getLifeCircleType())});
		}else{
			cacheId=cacheId.substring(4,cacheId.lastIndexOf(","));
		}
		
		try{
			unit.updateCollection(params);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#sub(java.lang.String, j.cache.JCacheParams)
	 */
	public Object sub(String clientUuid, String md54Service,String cacheId, JCacheParams params) throws RemoteException {
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"sub",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
		}
		
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
	public ConcurrentList keys(String clientUuid, String md54Service,String cacheId, JCacheParams params) throws RemoteException {
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"keys",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
		}
		
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
	public ConcurrentList values(String clientUuid, String md54Service,String cacheId, JCacheParams params) throws RemoteException {
		if(!cacheId.startsWith("syn:")){
			try{
				auth(clientUuid,"values",md54Service);
			}catch(RemoteException e){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
		}
		
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			int unitType=Integer.parseInt(SysUtil.getHttpParameter(request,"unitType"));
			int lifeCircleType=Integer.parseInt(SysUtil.getHttpParameter(request,"lifeCircleType"));
			
			this.createUnit(clientUuid,md54Service,cacheId,unitType,lifeCircleType);
			
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			
			this.setActiveTime(clientUuid,md54Service,cacheId);
			
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String key=SysUtil.getHttpParameter(request,"key");
			String value=SysUtil.getHttpParameter(request,"value");

			if(key!=null){
				this.addOne(clientUuid,md54Service,cacheId,JObject.string2Serializable(key),JObject.string2Serializable(value));
			}else{
				this.addOne(clientUuid,md54Service,cacheId,JObject.string2Serializable(value));		
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String mappings=SysUtil.getHttpParameter(request,"mappings");
			String values=SysUtil.getHttpParameter(request,"values");
			
			if(mappings!=null){
				this.addAll(clientUuid,md54Service,cacheId,(Map)JObject.string2Serializable(mappings));				
			}else if(values!=null){
				this.addAll(clientUuid,md54Service,cacheId,(List)JObject.string2Serializable(values));			
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String value=SysUtil.getHttpParameter(request,"value");
			
			this.addOneIfNotContains(clientUuid,md54Service,cacheId,JObject.string2Serializable(value));
			
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			jsession.resultString=""+this.contains(clientUuid,md54Service,cacheId,(JCacheParams)JObject.string2Serializable(params));
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			if(params!=null){
				jsession.resultString=""+this.size(clientUuid,md54Service,cacheId,(JCacheParams)JObject.string2Serializable(params));
			}else{
				jsession.resultString=""+this.size(clientUuid,md54Service,cacheId);
			}
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			jsession.resultString=JObject.serializable2String((Serializable)this.get(clientUuid,md54Service,cacheId,(JCacheParams)JObject.string2Serializable(params)));
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			this.remove(clientUuid,md54Service,cacheId,(JCacheParams)JObject.string2Serializable(params));
			
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");

			this.clear(clientUuid,md54Service,cacheId);
			
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			this.update(clientUuid,md54Service,cacheId,(JCacheParams)JObject.string2Serializable(params));
			
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
	public void updateCollection(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		try{
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			this.updateCollection(clientUuid,md54Service,cacheId,(JCacheParams)JObject.string2Serializable(params));
			
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			jsession.resultString=JObject.serializable2String((Serializable)this.sub(clientUuid,md54Service,cacheId,(JCacheParams)JObject.string2Serializable(params)));
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			jsession.resultString=JObject.serializable2String((Serializable)this.keys(clientUuid,md54Service,cacheId,(JCacheParams)JObject.string2Serializable(params)));
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
			String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
			String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
			String cacheId=SysUtil.getHttpParameter(request,"cacheId");
			String params=SysUtil.getHttpParameter(request,"params");
			
			jsession.resultString=JObject.serializable2String((Serializable)this.values(clientUuid,md54Service,cacheId,(JCacheParams)JObject.string2Serializable(params)));
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

			//清除过期未使用的临时缓存单元
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
