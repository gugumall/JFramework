package j.cache;

import j.nvwa.Nvwa;
import j.util.ConcurrentList;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * 为能实现分布式方案，所有放入缓存的对象都应该实现Serializable接口
 * 
 * @author 肖炯
 *
 */
public abstract class JCache implements Serializable{	
	private static final long serialVersionUID = 1L;
	public static final int UNIT_MAP=1;
	public static final int UNIT_LIST=2;
	public static final int LIFECIRCLE_TEMPORARY=1;//临时缓存，超时未用的自动清除
	public static final int LIFECIRCLE_SYNCHRONIZED=2;//同步缓存，比如与数据库保持同步，调用相关API来保持同步
	public static final int LIFECIRCLE_PERSISTED=3;//持久缓存，保存到文件系统等
	
	/**
	 * 
	 *
	 */
	public JCache() {
		super();
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static JCache getInstance() throws Exception{		
		JCache cache = (JCache)Nvwa.create("Cache");
		
		return cache;
	}

	/**
	 * 
	 * @param nvwaObjectCode
	 * @return
	 * @throws Exception
	 */
	public static JCache getInstance(String nvwaObjectCode) throws Exception{		
		JCache cache = (JCache)Nvwa.create(nvwaObjectCode);
		
		return cache;
	}
	
	/**
	 * 
	 * @param cacheId
	 * @param unitType
	 * @param lifeCircleType
	 */
	public abstract void createUnit(String cacheId,int unitType,int lifeCircleType) throws Exception;
	
	/**
	 * 
	 * @param cacheId
	 * @throws Exception
	 */
	public abstract void setActiveTime(String cacheId) throws Exception;
		
	/**
	 * 
	 * @param cacheId
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public abstract void addOne(String cacheId,Object key,Object value) throws Exception;
	
	/**
	 * 
	 * @param cacheId
	 * @param mappings
	 * @throws Exception
	 */
	public abstract void addAll(String cacheId,Map mappings) throws Exception;
	

	/**
	 * 
	 * @param cacheId
	 * @param value
	 * @throws Exception
	 */
	public abstract void addOne(String cacheId,Object value) throws Exception;

	/**
	 * 
	 * @param cacheId
	 * @param value
	 * @throws Exception
	 */
	public abstract void addOneIfNotContains(String cacheId, Object value) throws Exception;

	
	/**
	 * 
	 * @param cacheId
	 * @param values
	 * @throws Exception
	 */
	public abstract void addAll(String cacheId,Collection values) throws Exception;

	/**
	 * 
	 * @param cacheId
	 * @param jdcParams
	 * @return
	 * @throws Exception
	 */
	public abstract boolean contains(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 
	 * @param cacheId
	 * @return
	 * @throws Exception
	 */
	public abstract int size(String cacheId) throws Exception;
	
	/**
	 * 
	 * @param cacheId
	 * @param jdcParams
	 * @return
	 * @throws Exception
	 */
	public abstract int size(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 
	 * @param cacheId
	 * @param jdcParams
	 * @return
	 * @throws Exception
	 */
	public abstract Object get(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 
	 * @param cacheId
	 * @param jdcParams
	 * @throws Exception
	 */
	public abstract void remove(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 
	 * @param cacheId
	 * @throws Exception
	 */
	public abstract void clear(String cacheId) throws Exception;
	
	/**
	 * 
	 * @param cacheId
	 * @param jdcParams
	 * @throws Exception
	 */
	public abstract void update(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 
	 * @param cacheId
	 * @param jdcParams
	 * @throws Exception
	 */
	public abstract void updateCollection(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 
	 * @param cacheId
	 * @param jdcParams
	 * @return
	 * @throws Exception
	 */
	public abstract Object sub(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 
	 * @param cacheId
	 * @param jdcParams
	 * @return
	 * @throws Exception
	 */
	public abstract ConcurrentList keys(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 
	 * @param cacheId
	 * @param jdcParams
	 * @return
	 * @throws Exception
	 */
	public abstract ConcurrentList values(String cacheId,JCacheParams jdcParams) throws Exception;
}
