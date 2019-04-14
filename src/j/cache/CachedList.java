package j.cache;

import j.util.ConcurrentList;

import java.util.Collection;

/**
 * 基于缓存实现的List——实际开发中，请通过此类来使用缓存服务
 * @author 肖炯
 *
 */
public class CachedList{
	private String cacheId=null;//缓存单元ID
	private JCache cache=null;//缓存单元
	
	/**
	 * 创建JCache.UNIT_LIST类型的缓存单元，缓存服务实现类在对象工厂中的code为<font color="blue">JCache</font>（默认实现）
	 * @param cacheId 缓存单元ID
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
		cache.createUnit(cacheId,JCache.UNIT_LIST,JCache.LIFECIRCLE_DURABLE);
	}
	
	/**
	 * 创建JCache.UNIT_LIST类型的缓存单元，缓存服务实现类在对象工厂中的code为参数nvwaObjectCode所指定的值
	 * @param nvwaObjectCode 缓存服务实现类在对象工厂中的code
	 * @param cacheId 缓存单元ID
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
		cache.createUnit(cacheId,JCache.UNIT_LIST,JCache.LIFECIRCLE_DURABLE);
	}
	
	

	/**
	 * 添加一个对象到缓存单元
	 * @param value
	 * @throws Exception
	 */
	public void addOne(Object value) throws Exception {
		cache.addOne(this.cacheId,value);	
	}

	/**
	 * 添加一个对象集合到缓存单元
	 * @param values
	 * @throws Exception
	 */
	public void addAll(Collection<Object> values) throws Exception {
		cache.addAll(this.cacheId,values);
	}

	/**
	 * 缓存单元是否存在符合缓存操作参数的对象
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public boolean contains(JCacheParams params) throws Exception {
		return cache.contains(this.cacheId,params);
	}
	
	/**
	 * 缓存单元中全部对象的数量
	 * @return
	 * @throws Exception
	 */
	public int size() throws Exception {
		return cache.size(this.cacheId);
	}
	
	/**
	 * 缓存单元中符合缓存操作参数的对象的数量
	 * @param params 缓存操作参数
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
	public int[] sizes(JCacheParams[] params) throws Exception {
		return cache.sizes(this.cacheId,params);
	}

	/**
	 * 获得符合缓存操作参数的对象，如有多个符合，返回索引位置最靠前的那个
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public Object get(JCacheParams params) throws Exception {
		return cache.get(this.cacheId,params);
	}

	/**
	 * 移除符合缓存操作参数的对象
	 * @param params 缓存操作参数
	 * @throws Exception
	 */
	public void remove(JCacheParams params) throws Exception {
		cache.remove(this.cacheId,params);
	}

	/**
	 * 清空缓存单元
	 * @throws Exception
	 */
	public void clear() throws Exception {
		cache.clear(this.cacheId);
	}

	/**
	 * 调用缓存操作参数中指定的更新器对缓存单元中特定对象进行更新，更新规则由更新器定义
	 * @param params 缓存操作参数
	 * @throws Exception
	 */
	public void update(JCacheParams params) throws Exception {
		cache.update(this.cacheId,params);
	}

	/**
	 * 调用缓存操作参数中指定的更新器对缓存单元中特定对象集合进行更新，更新规则由更新器定义
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
	public ConcurrentList<Object> sub(JCacheParams params) throws Exception {
		return (ConcurrentList)cache.sub(this.cacheId,params);
	}
}
