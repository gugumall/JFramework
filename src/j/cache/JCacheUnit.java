package j.cache;

import j.sys.SysUtil;
import j.util.ConcurrentList;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * 
 * @author 肖炯
 *
 */
public abstract class JCacheUnit implements Serializable{
	private static final long serialVersionUID = 1L;
	protected int lifeCircleType;
	protected volatile long updateTime=0;	
	
	/**
	 * 
	 *
	 */
	public void using(){
		updateTime=SysUtil.getNow();
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract int getUnitType();
	
	/**
	 * 
	 * @return
	 */
	public abstract int getLifeCircleType();
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public abstract void addOne(Object key,Object value) throws Exception;
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @param initializing
	 * @throws Exception
	 */
	public abstract void addOne(Object key,Object value,boolean initializing) throws Exception;
	
	/**
	 * 
	 * @param mappings
	 * @throws Exception
	 */
	public abstract void addAll(Map mappings) throws Exception;
	
	/**
	 * 
	 * @param mappings
	 * @param initializing
	 * @throws Exception
	 */
	public abstract void addAll(Map mappings,boolean initializing) throws Exception;
	
	/**
	 * 
	 * @param value
	 * @throws Exception
	 */
	public abstract void addOne(Object value) throws Exception;
	
	/**
	 * 
	 * @param value
	 * @throws Exception
	 */
	public abstract void addOneIfNotContains(Object value) throws Exception;
	
	/**
	 * 
	 * @param value
	 * @param initializing
	 * @throws Exception
	 */
	public abstract void addOneIfNotContains(Object value,boolean initializing) throws Exception;
	
	/**
	 * 
	 * @param value
	 * @param initializing
	 * @throws Exception
	 */
	public abstract void addOne(Object value,boolean initializing) throws Exception;
	
	/**
	 * 
	 * @param values
	 * @throws Exception
	 */
	public abstract void addAll(Collection values) throws Exception;
	
	/**
	 * 
	 * @param values
	 * @param initializing
	 * @throws Exception
	 */
	public abstract void addAll(Collection values,boolean initializing) throws Exception;
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public abstract boolean contains(JCacheParams params) throws Exception;
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract int size() throws Exception;
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public abstract int size(JCacheParams params) throws Exception;
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public abstract Object get(JCacheParams params) throws Exception;
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public abstract void remove(JCacheParams params) throws Exception;
	
	/**
	 * 
	 * @throws Exception
	 */
	public abstract void clear() throws Exception;
	
	/**
	 * 
	 * @param params
	 * @throws Exception
	 */
	public abstract void update(JCacheParams params) throws Exception;
	
	/**
	 * 
	 * @param params
	 * @throws Exception
	 */
	public abstract void updateCollection(JCacheParams params) throws Exception;
	
	/**
	 * 
	 * @param params
	 * @return Map or List
	 * @throws Exception
	 */
	public abstract Object sub(JCacheParams params) throws Exception;
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public abstract ConcurrentList keys(JCacheParams params) throws Exception;
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public abstract ConcurrentList values(JCacheParams params) throws Exception;
	
	/**
	 * 
	 * @return 
	 * @throws Exception
	 */
	public boolean isTimeout() throws Exception{
		if(this.lifeCircleType==JCache.LIFECIRCLE_TEMPORARY
				&&JCacheConfig.getCacheTimeout()>0
				&&SysUtil.getNow()-this.updateTime>JCacheConfig.getCacheTimeout()){
			return true;
		}
		return false;
	}
}
