package j.cache;

import j.util.ConcurrentList;
import j.util.ConcurrentMap;

import java.util.Map;

/**
 * 基于缓存实现的Map——实际开发中，请通过此类来使用缓存服务
 * @author 肖炯
 *
 */
public class CachedMap{
	private String cacheId=null;
	private JCache cache=null;
	
	/**
	 * 创建JCache.UNIT_LIST类型的缓存单元，缓存服务实现类在对象工厂中的code为<font color="blue">JCache</font>（默认实现）
	 * @param cacheId 缓存单元ID
	 * @throws Exception
	 */
	public CachedMap(String cacheId) throws Exception{
		super();
		if(cacheId==null){
			this.cacheId=""+(new Object()).hashCode();;
		}else{
			this.cacheId=cacheId;
		}
		cache=JCache.getInstance();
		cache.createUnit(cacheId,JCache.UNIT_MAP,JCache.LIFECIRCLE_DURABLE);
	}
	
	/**
	 * 创建JCache.UNIT_LIST类型的缓存单元，缓存服务实现类在对象工厂中的code为参数nvwaObjectCode所指定的值
	 * @param nvwaObjectCode 缓存服务实现类在对象工厂中的code
	 * @param cacheId 缓存单元ID
	 * @throws Exception
	 */
	public CachedMap(String nvwaObjectCode,String cacheId) throws Exception{
		super();
		if(cacheId==null){
			this.cacheId=""+(new Object()).hashCode();;
		}else{
			this.cacheId=cacheId;
		}
		cache=JCache.getInstance(nvwaObjectCode);
		cache.createUnit(cacheId,JCache.UNIT_MAP,JCache.LIFECIRCLE_DURABLE);
	}

	/**
	 * 添加一个key-value到缓存单元
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void addOne(Object key, Object value) throws Exception {
		cache.addOne(this.cacheId,key,value);
	}

	/**
	 * 添加一组key-value到缓存单元
	 * @param mappings key-value集合
	 * @throws Exception
	 */
	public void addAll(Map<Object,Object> mappings) throws Exception {
		cache.addAll(this.cacheId,mappings);
	}

	/**
	 * 缓存单元中是否包含符合缓存操作参数的key-value
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public boolean contains(JCacheParams params) throws Exception {
		return cache.contains(this.cacheId,params);
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public int size() throws Exception {
		return cache.size(this.cacheId);
	}
	
	/**
	 * 
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public int size(JCacheParams params) throws Exception {
		return cache.size(this.cacheId,params);
	}
	
	/**
	 * 
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public int[] sizes(JCacheParams[] params) throws Exception {
		return cache.sizes(this.cacheId,params);
	}

	/**
	 * 
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public Object get(JCacheParams params) throws Exception {
		return cache.get(this.cacheId,params);
	}

	/**
	 * 
	 * @param params 缓存操作参数
	 * @throws Exception
	 */
	public void remove(JCacheParams params) throws Exception {
		cache.remove(this.cacheId,params);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void clear() throws Exception {
		cache.clear(this.cacheId);
	}

	/**
	 * 
	 * @param params 缓存操作参数
	 * @throws Exception
	 */
	public void update(JCacheParams params) throws Exception {
		cache.update(this.cacheId,params);
	}

	/**
	 * 
	 * @param params 缓存操作参数
	 * @throws Exception
	 */
	public void updateCollection(JCacheParams params) throws Exception {
		cache.updateCollection(this.cacheId,params);
	}

	/**
	 * 返回符合缓存操作参数的对象集合
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public ConcurrentMap sub(JCacheParams params) throws Exception {
		return (ConcurrentMap)cache.sub(this.cacheId,params);
	}

	/**
	 * 返回符合缓存操作参数的子集的key的list
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public ConcurrentList keys(JCacheParams params) throws Exception {
		return cache.keys(this.cacheId,params);
	}

	/**
	 * 返回符合缓存操作参数的子集的value的list
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public ConcurrentList values(JCacheParams params) throws Exception {
		return cache.values(this.cacheId,params);
	}
}
