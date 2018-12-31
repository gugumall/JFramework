package j.cache;

import j.util.ConcurrentList;

import java.util.Collection;

/**
 * 
 * @author 肖炯
 *
 */
public class CachedList{
	private String cacheId=null;
	private JCache cache=null;
	
	/**
	 * 
	 * @param cacheId
	 * @throws Exception
	 */
	public CachedList(String cacheId) throws Exception{
		super();
		if(cacheId==null){
			this.cacheId=""+(new Object()).hashCode();;
		}else{
			this.cacheId=cacheId;
		}
		cache=JCache.getInstance();
		cache.createUnit(cacheId,JCache.UNIT_LIST,JCache.LIFECIRCLE_SYNCHRONIZED);
	}
	
	/**
	 * 
	 * @param nvwaObjectCode
	 * @param cacheId
	 * @throws Exception
	 */
	public CachedList(String nvwaObjectCode,String cacheId) throws Exception{
		super();
		if(cacheId==null){
			this.cacheId=""+(new Object()).hashCode();;
		}else{
			this.cacheId=cacheId;
		}
		cache=JCache.getInstance(nvwaObjectCode);
		cache.createUnit(cacheId,JCache.UNIT_LIST,JCache.LIFECIRCLE_SYNCHRONIZED);
	}
	
	

	/**
	 * 
	 * @param value
	 * @throws Exception
	 */
	public void addOne(Object value) throws Exception {
		cache.addOne(this.cacheId,value);	
	}

	/**
	 * 
	 * @param values
	 * @throws Exception
	 */
	public void addAll(Collection values) throws Exception {
		cache.addAll(this.cacheId,values);
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
	public ConcurrentList sub(JCacheParams params) throws Exception {
		return (ConcurrentList)cache.sub(this.cacheId,params);
	}
}
