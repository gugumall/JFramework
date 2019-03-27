package j.cache;

import j.sys.SysUtil;
import j.util.ConcurrentList;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * 缓存单元
 * @author 肖炯
 *
 */
public abstract class JCacheUnit implements Serializable{
	private static final long serialVersionUID = 1L;
	protected int lifeCircleType;//缓存单元生命周期类型（见JCache）
	protected volatile long updateTime=0;//缓存单元最近使用/更新时间
	
	/**
	 * 该单元的生命周期类型
	 * @return
	 */
	public int getLifeCircleType() {
		return this.lifeCircleType;
	}
	
	/**
	 * 该缓存单元的类型（JCache.UNIT_MAP或JCache.UNIT_LIST）
	 * @return
	 */
	public abstract int getUnitType();
	
	/**
	 * 添加一对key-value到缓存单元，仅当缓存单元类型为JCache.UNIT_MAP时有效
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public abstract void addOne(Object key,Object value) throws Exception;

	/**
	 * 添加一组key-value集合到缓存单元，仅当缓存单元类型为JCache.UNIT_MAP时有效
	 * @param mappings
	 * @throws Exception
	 */
	public abstract void addAll(Map mappings) throws Exception;

	/**
	 * 添加一个对象到缓存单元，仅当缓存单元类型为JCache.UNIT_LIST时有效
	 * @param value
	 * @throws Exception
	 */
	public abstract void addOne(Object value) throws Exception;
	
	/**
	 * 添加一个对象到缓存单元（仅当该对象在缓存单元中不存在时才添加），仅当缓存单元类型为JCache.UNIT_LIST时有效
	 * @param value
	 * @throws Exception
	 */
	public abstract void addOneIfNotContains(Object value) throws Exception;

	/**
	 * 添加一个对象集合到缓存单元，仅当缓存单元类型为JCache.UNIT_LIST时有效
	 * @param values
	 * @throws Exception
	 */
	public abstract void addAll(Collection values) throws Exception;
	
	/**
	 * 缓存单元中是否包含符合缓存操作参数的对象
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public abstract boolean contains(JCacheParams params) throws Exception;
	
	/**
	 * 缓存单元中对象的数量
	 * @return
	 * @throws Exception
	 */
	public abstract int size() throws Exception;
	
	/**
	 * 缓存单元中符合缓存操作参数的对象的数量
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public abstract int size(JCacheParams params) throws Exception;
	
	/**
	 * 得到缓存单元中符合缓存操作参数的对象，如有多个符合返回第一个
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public abstract Object get(JCacheParams params) throws Exception;
	
	/**
	 * 从缓存单元中移除符合缓存操作参数的对象
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public abstract void remove(JCacheParams params) throws Exception;
	
	/**
	 * 清空缓存单元
	 * @throws Exception
	 */
	public abstract void clear() throws Exception;
	
	/**
	 * 调用缓存操作参数中指定的更新器对缓存单元中特定对象进行更新
	 * @param params 缓存操作参数
	 * @throws Exception
	 */
	public abstract void update(JCacheParams params) throws Exception;
	
	/**
	 * 调用缓存操作参数中指定的更新器对缓存单元中特定对象集合进行更新
	 * @param params 缓存操作参数
	 * @throws Exception
	 */
	public abstract void updateCollection(JCacheParams params) throws Exception;
	
	/**
	 * 返回符合缓存操作参数的子集
	 * @param params 缓存操作参数
	 * @return Map类缓存单元应该返回Map，List类型缓存单元应该返回List
	 * @throws Exception
	 */
	public abstract Object sub(JCacheParams params) throws Exception;
	
	/**
	 * 返回符合缓存操作参数的子集的key的List，仅当缓存单元类型为JCache.UNIT_MAP时有效
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public abstract ConcurrentList keys(JCacheParams params) throws Exception;
	
	/**
	 * 返回符合缓存操作参数的子集的value的List，仅当缓存单元类型为JCache.UNIT_MAP时有效
	 * @param params 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public abstract ConcurrentList values(JCacheParams params) throws Exception;
	
	/**
	 * 记录最近更新/使用时间
	 *
	 */
	public void using(){
		updateTime=SysUtil.getNow();
	}
	
	/**
	 * 是否已经失效（当生命周期类型为JCache.LIFECIRCLE_TEMPORARY时）
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
