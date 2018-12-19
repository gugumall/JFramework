package j.cache;

import j.common.JObject;
import j.log.Logger;
import j.service.client.Client;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author JFramework
 *
 */
public class JDCache extends JCache{
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.create(JDCache.class);
	private static ConcurrentMap services=new ConcurrentMap();
	private String serviceUuid;

	/**
	 * 
	 *
	 */
	public JDCache() {
		super();
	}
	
	/**
	 * 
	 * @param serviceUuid
	 */
	public JDCache(String serviceUuid) {
		super();
		this.serviceUuid=serviceUuid;
	}
	
	/**
	 * 
	 * @param cacheId
	 * @return
	 * @throws Exception
	 */
	synchronized private Servant findService(String cacheId) throws Exception{
		Servant info=(Servant)services.get(cacheId);

		JDCacheMapping mapping=JCacheConfig.mapping(cacheId);
		if(mapping==null){
			log.log("JDCacheMapping can't be found - "+cacheId,Logger.LEVEL_DEBUG);
			throw new Exception("JDCacheMapping can't be found - "+cacheId);
		}else{
			if(info==null) info=new Servant();
			info.serviceCode=mapping.getServiceCode();
			info.serviceChannel=mapping.getServiceChannel();
			
			if(this.serviceUuid!=null){
				if("rmi".equalsIgnoreCase(info.serviceChannel)){
					info.service=(JDCacheService)Client.rmiGetService(this.serviceUuid);			
					//log.log("JDCacheService(rmi) found - "+info.service+" - "+cacheId,Logger.LEVEL_DEBUG);
				}else{	
					info.httpChannel=Client.httpGetService(info.jhttp,info.jclient,this.serviceUuid);
					//log.log("JDCacheService(http) found - "+info.httpChannel+" - "+cacheId,Logger.LEVEL_DEBUG);
					
					if(info.httpChannel==null||!info.httpChannel.startsWith("http")){
						throw new Exception("the httpChannel is null or empty - "+info.httpChannel);
					}
				}
			}else{
				//log.log("JDCacheMapping found,the related service is - "+info.serviceCode+"("+info.serviceChannel+") - "+cacheId,Logger.LEVEL_DEBUG);
				info.service=(JDCacheService)Client.rmiGetService(info.serviceCode,true);	
				if(info.service==null){
					if("rmi".equalsIgnoreCase(info.serviceChannel)){
						info.service=(JDCacheService)Client.rmiGetService(info.serviceCode);			
						//log.log("JDCacheService(rmi) found - "+info.service+" - "+cacheId,Logger.LEVEL_DEBUG);
					}else{	
						info.httpChannel=Client.httpGetService(info.jhttp,info.jclient,info.serviceCode);
						log.log("JDCacheService(http) found - "+info.httpChannel+" - "+cacheId,Logger.LEVEL_DEBUG);
						
						if(info.httpChannel==null||!info.httpChannel.startsWith("http")){
							throw new Exception("the httpChannel is null or empty - "+info.httpChannel);
						}
					}
				}
			}
			
			services.put(cacheId,info);
			
			return info;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#create(java.lang.String, int, int)
	 */
	public void createUnit(String cacheId, int unitType, int lifeCircleType) throws Exception {		
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.createUnit(cacheId,unitType,lifeCircleType);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("unitType",unitType+"");
			params.put("lifeCircleType",lifeCircleType+"");
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"createUnit",params);
			params.clear();
			params=null;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#setActiveTime(java.lang.String)
	 */
	public void setActiveTime(String cacheId) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.setActiveTime(cacheId);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"setActiveTime",params);
			params.clear();
			params=null;	
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addOne(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void addOne(String cacheId, Object key, Object value) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.addOne(cacheId,key,value);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("key",JObject.serializable2String((Serializable)key));
			params.put("value",JObject.serializable2String((Serializable)value));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"addOne",params);
			params.clear();
			params=null;	
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addAll(java.lang.String, java.util.Map)
	 */
	public void addAll(String cacheId, Map mappings) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.addAll(cacheId,mappings);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("mappings",JObject.serializable2String((Serializable)mappings));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"addAll",params);
			params.clear();
			params=null;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addOne(java.lang.String, java.lang.Object)
	 */
	public void addOne(String cacheId, Object value) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.addOne(cacheId,value);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("value",JObject.serializable2String((Serializable)value));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"addOne",params);
			params.clear();
			params=null;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addAll(java.lang.String, java.util.Collection)
	 */
	public void addAll(String cacheId, Collection values) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.addAll(cacheId,values);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("values",JObject.serializable2String((Serializable)values));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"addAll",params);
			params.clear();
			params=null;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addOneIfNotContains(java.lang.String, java.lang.Object)
	 */
	public void addOneIfNotContains(String cacheId, Object value) throws Exception{
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.addOneIfNotContains(cacheId,value);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("value",JObject.serializable2String((Serializable)value));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"addOneIfNotContains",params);
			params.clear();
			params=null;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#contains(java.lang.String, j.cache.JCacheParams)
	 */
	public boolean contains(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.contains(cacheId,jdcParams);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("params",JObject.serializable2String((Serializable)jdcParams));
			String response=Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"contains",params);
			params.clear();
			params=null;
			
			return "true".equalsIgnoreCase(response);
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#size(java.lang.String)
	 */
	public int size(String cacheId) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.size(cacheId);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			String response=Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"size",params);
			params.clear();
			params=null;
			
			return Integer.parseInt(response);	
		}
	}

	/*
	 * (non-Javadoc)
	 * @see j.cache.JCache#size(java.lang.String, j.cache.JCacheParams)
	 */
	public int size(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.size(cacheId);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("params",JObject.serializable2String((Serializable)jdcParams));
			String response=Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"size",params);
			params.clear();
			params=null;
			
			return Integer.parseInt(response);	
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#get(java.lang.String, j.cache.JCacheParams)
	 */
	public Object get(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.get(cacheId,jdcParams);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("params",JObject.serializable2String((Serializable)jdcParams));
			String response=Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"get",params);
			params.clear();
			params=null;
			
			return JObject.string2Serializable(response);
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#remove(java.lang.String, j.cache.JCacheParams)
	 */
	public void remove(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.remove(cacheId,jdcParams);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("params",JObject.serializable2String((Serializable)jdcParams));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"remove",params);
			params.clear();
			params=null;	
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#clear(java.lang.String)
	 */
	public void clear(String cacheId) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.clear(cacheId);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"clear",params);
			params.clear();
			params=null;	
		}		
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#update(java.lang.String, j.cache.JCacheParams)
	 */
	public void update(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.update(cacheId,jdcParams);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("params",JObject.serializable2String((Serializable)jdcParams));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"update",params);
			params.clear();
			params=null;	
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#sub(java.lang.String, j.cache.JCacheParams)
	 */
	public Object sub(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.sub(cacheId,jdcParams);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("params",JObject.serializable2String((Serializable)jdcParams));
			String response=Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"sub",params);
			params.clear();
			params=null;	
			
			return JObject.string2Serializable(response);	
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#keys(java.lang.String, j.cache.JCacheParams)
	 */
	public ConcurrentList keys(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.keys(cacheId,jdcParams);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("params",JObject.serializable2String((Serializable)jdcParams));
			String response=Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"keys",params);
			params.clear();
			params=null;	
			
			return (ConcurrentList)JObject.string2Serializable(response);	
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#values(java.lang.String, j.cache.JCacheParams)
	 */
	public ConcurrentList values(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.values(cacheId,jdcParams);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("params",JObject.serializable2String((Serializable)jdcParams));
			String response=Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"values",params);
			params.clear();
			params=null;	
			
			return (ConcurrentList)JObject.string2Serializable(response);	
		}
	}
}