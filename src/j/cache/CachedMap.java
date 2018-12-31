package j.cache;

import j.util.ConcurrentList;
import j.util.ConcurrentMap;

import java.util.Map;

/**
 * 
 * @author 肖炯
 *
 */
public class CachedMap{
	private String cacheId=null;
	private JCache cache=null;
	
	/**
	 * 
	 * @param cacheId
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
		cache.createUnit(cacheId,JCache.UNIT_MAP,JCache.LIFECIRCLE_SYNCHRONIZED);
	}
	
	/**
	 * 
	 * @param nvwaObjectCode
	 * @param cacheId
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
		cache.createUnit(cacheId,JCache.UNIT_MAP,JCache.LIFECIRCLE_SYNCHRONIZED);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void addOne(Object key, Object value) throws Exception {
		cache.addOne(this.cacheId,key,value);
	}

	/**
	 * 
	 * @param mappings
	 * @throws Exception
	 */
	public void addAll(Map mappings) throws Exception {
		cache.addAll(this.cacheId,mappings);
	}

	/**
	 * 
	 * @param params
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
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int size(JCacheParams params) throws Exception {
		return cache.size(this.cacheId,params);
	}

	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Object get(JCacheParams params) throws Exception {
		return cache.get(this.cacheId,params);
	}

	/**
	 * 
	 * @param params
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
	 * @param params
	 * @throws Exception
	 */
	public void update(JCacheParams params) throws Exception {
		cache.update(this.cacheId,params);
	}

	/**
	 * 
	 * @param params
	 * @throws Exception
	 */
	public void updateCollection(JCacheParams params) throws Exception {
		cache.updateCollection(this.cacheId,params);
	}

	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public ConcurrentMap sub(JCacheParams params) throws Exception {
		return (ConcurrentMap)cache.sub(this.cacheId,params);
	}

	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public ConcurrentList keys(JCacheParams params) throws Exception {
		return cache.keys(this.cacheId,params);
	}

	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public ConcurrentList values(JCacheParams params) throws Exception {
		return cache.values(this.cacheId,params);
	}
}
