package j.cache;

import j.common.JObject;
import j.log.Logger;
import j.service.Manager;
import j.service.client.Client;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 分布式缓存服务的实现
 * @author 肖炯
 *
 */
public class JDCache extends JCache{
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.create(JDCache.class);
	private static ConcurrentMap<String,Servant> services=new ConcurrentMap<String,Servant>();//key-缓存单元ID，value-缓存单元所调用的分布式服务对象（RMI通信时）
	private String serviceUuid;//指定所调用缓存服务的节点uuid（而不是通过路由器的负载均衡机制随机调用），仅用于同一缓存服务不同镜像间的同步

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
		Servant servant=(Servant)services.get(cacheId);

		JDCacheMapping mapping=JCacheConfig.mapping(cacheId);
		if(mapping==null){//不能根据缓存单元ID找到所需调用的缓存服务
			log.log("JDCacheMapping can't be found - "+cacheId,Logger.LEVEL_DEBUG);
			throw new Exception("JDCacheMapping can't be found - "+cacheId);
		}else{
			if(servant==null) servant=new Servant();
			servant.serviceCode=mapping.getServiceCode();
			servant.serviceChannel=mapping.getServiceChannel();
			
			if(this.serviceUuid!=null){
				if("rmi".equalsIgnoreCase(servant.serviceChannel)){
					servant.service=(JDCacheService)Client.rmiGetService(this.serviceUuid);			
					//log.log("JDCacheService(rmi) found - "+info.service+" - "+cacheId,Logger.LEVEL_DEBUG);
				}else{	
					servant.httpChannel=Client.httpGetService(servant.jhttp,servant.jclient,this.serviceUuid);
					//log.log("JDCacheService(http) found - "+info.httpChannel+" - "+cacheId,Logger.LEVEL_DEBUG);
					
					if(servant.httpChannel==null||!servant.httpChannel.startsWith("http")){
						throw new Exception("the httpChannel is null or empty - "+servant.httpChannel);
					}
				}
			}else{
				//log.log("JDCacheMapping found,the related service is - "+info.serviceCode+"("+info.serviceChannel+") - "+cacheId,Logger.LEVEL_DEBUG);
				servant.service=(JDCacheService)Client.rmiGetService(servant.serviceCode,true);//优先查找本地服务（同一个jvm中，可直接本地调用，不涉及网络通信，效率最高）
				if(servant.service==null){//如果未找到本地服务
					if("rmi".equalsIgnoreCase(servant.serviceChannel)){
						servant.service=(JDCacheService)Client.rmiGetService(servant.serviceCode);			
						//log.log("JDCacheService(rmi) found - "+info.service+" - "+cacheId,Logger.LEVEL_DEBUG);
					}else{	
						servant.httpChannel=Client.httpGetService(servant.jhttp,servant.jclient,servant.serviceCode);
						log.log("JDCacheService(http) found - "+servant.httpChannel+" - "+cacheId,Logger.LEVEL_DEBUG);
						
						if(servant.httpChannel==null||!servant.httpChannel.startsWith("http")){
							throw new Exception("the httpChannel is null or empty - "+servant.httpChannel);
						}
					}
				}
			}
			
			services.put(cacheId,servant);
			
			return servant;
		}
	}


	@Override
	public void createUnit(String cacheId, int unitType, int lifeCircleType) throws Exception {		
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.createUnit(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"createUnit"),cacheId,unitType,lifeCircleType);
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


	@Override
	public void setActiveTime(String cacheId) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.setActiveTime(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"setActiveTime"),cacheId);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"setActiveTime",params);
			params.clear();
			params=null;	
		}
	}


	@Override
	public void addOne(String cacheId, Object key, Object value) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.addOne(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"addOne"),cacheId,key,value);
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


	@Override
	public void addAll(String cacheId, Map mappings) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.addAll(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"addAll"),cacheId,mappings);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("mappings",JObject.serializable2String((Serializable)mappings));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"addAll",params);
			params.clear();
			params=null;
		}
	}


	@Override
	public void addOne(String cacheId, Object value) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.addOne(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"addOne"),cacheId,value);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("value",JObject.serializable2String((Serializable)value));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"addOne",params);
			params.clear();
			params=null;
		}
	}


	@Override
	public void addAll(String cacheId, Collection values) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.addAll(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"addAll"),cacheId,values);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("values",JObject.serializable2String((Serializable)values));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"addAll",params);
			params.clear();
			params=null;
		}
	}


	@Override
	public void addOneIfNotContains(String cacheId, Object value) throws Exception{
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.addOneIfNotContains(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"addOneIfNotContains"),cacheId,value);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("value",JObject.serializable2String((Serializable)value));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"addOneIfNotContains",params);
			params.clear();
			params=null;
		}
	}


	@Override
	public boolean contains(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.contains(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"contains"),cacheId,jdcParams);
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


	@Override
	public int size(String cacheId) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.size(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"size"),cacheId);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			String response=Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"size",params);
			params.clear();
			params=null;
			
			return Integer.parseInt(response);	
		}
	}


	@Override
	public int size(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.size(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"size"),cacheId,jdcParams);
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

	@Override
	public int[] sizes(String cacheId,JCacheParams[] jdcParams) throws Exception{
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.sizes(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"sizes"),cacheId,jdcParams);
		}else{ 
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("params",JObject.serializable2String((Serializable)jdcParams));
			String response=Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"sizes",params);
			params.clear();
			params=null;
			
			String[] _sizes=response.split(",");
			int[] sizes=new int[_sizes.length];
			for(int i=0;i<sizes.length;i++) sizes[i]=Integer.parseInt(_sizes[i]);
			
			return sizes;	
		}
	}


	@Override
	public Object get(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.get(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"get"),cacheId,jdcParams);
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


	@Override
	public void remove(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.remove(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"remove"),cacheId,jdcParams);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("params",JObject.serializable2String((Serializable)jdcParams));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"remove",params);
			params.clear();
			params=null;	
		}
	}


	@Override
	public void clear(String cacheId) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.clear(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"clear"),cacheId);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"clear",params);
			params.clear();
			params=null;	
		}		
	}

	@Override
	public void update(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.update(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"update"),cacheId,jdcParams);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("params",JObject.serializable2String((Serializable)jdcParams));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"update",params);
			params.clear();
			params=null;	
		}
	}

	@Override
	public void updateCollection(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			info.service.updateCollection(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"updateCollection"),cacheId,jdcParams);
		}else{
			Map params=new HashMap();
			params.put("cacheId",cacheId);
			params.put("params",JObject.serializable2String((Serializable)jdcParams));
			Client.httpCallPost(info.jhttp,info.jclient,info.serviceCode,info.httpChannel,"updateCollection",params);
			params.clear();
			params=null;	
		}
	}

	@Override
	public Object sub(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.sub(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"sub"),cacheId,jdcParams);
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

	@Override
	public ConcurrentList keys(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.keys(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"keys"),cacheId,jdcParams);
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

	@Override
	public ConcurrentList values(String cacheId, JCacheParams jdcParams) throws Exception {
		Servant info=findService(cacheId);
		
		if(info.service!=null){
			return info.service.values(Manager.getClientNodeUuid(),Client.md54Service(info.serviceCode,"values"),cacheId,jdcParams);
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