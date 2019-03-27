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
	public static final int UNIT_MAP=1;//缓存单元类型: KEY-VALUE
	public static final int UNIT_LIST=2;//缓存单元类型: LIST
	public static final int LIFECIRCLE_TEMPORARY=1;//临时缓存，超时未用的自动清除
	public static final int LIFECIRCLE_DURABLE=2;//常驻缓存，重启应用才会清除
	public static final int LIFECIRCLE_PERSISTED=3;//持久缓存，保存到文件系统等
	
	/**
	 * 
	 *
	 */
	public JCache() {
		super();
	}

	/**
	 * 使用默认的缓存实现类
	 * @return
	 * @throws Exception
	 */
	public static JCache getInstance() throws Exception{		
		JCache cache = (JCache)Nvwa.create("Cache");
		
		return cache;
	}

	/**
	 * 指定缓存实现类
	 * @param nvwaObjectCode 缓存实现类在对象工厂中所配置的code的值
	 * @return
	 * @throws Exception
	 */
	public static JCache getInstance(String nvwaObjectCode) throws Exception{		
		JCache cache = (JCache)Nvwa.create(nvwaObjectCode);
		
		return cache;
	}
	
	/**
	 * 创建缓存单元
	 * @param cacheId  缓存单元ID，不管在哪个模块、哪个应用，同一个缓存单元ID总会指向同一个缓存单元
	 * @param unitType 缓存单元类型（Map or List）
	 * @param lifeCircleType 缓存单元生命周期类型
	 */
	public abstract void createUnit(String cacheId,int unitType,int lifeCircleType) throws Exception;
	
	/**
	 * 设置缓存单元的最近活动（被使用）时间
	 * @param cacheId 缓存单元ID
	 * @throws Exception
	 */
	public abstract void setActiveTime(String cacheId) throws Exception;
		
	/**
	 * 添加一个key-value到缓存单元，仅当缓存单元类型为Map时有效
	 * @param cacheId 缓存单元ID
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public abstract void addOne(String cacheId,Object key,Object value) throws Exception;
	
	/**
	 * 将Map中的所有key-value全部添加到缓存单元，仅当缓存单元类型为Map时有效
	 * @param cacheId 缓存单元ID
	 * @param mappings 包含需要添加的key-value
	 * @throws Exception
	 */
	public abstract void addAll(String cacheId,Map mappings) throws Exception;
	

	/**
	 * 添加一个对象到缓存单元，仅当缓存单元类型为List时有效
	 * @param cacheId 缓存单元ID
	 * @param value 需添加的对象
	 * @throws Exception
	 */
	public abstract void addOne(String cacheId,Object value) throws Exception;

	/**
	 * 添加一个对象到缓存单元（仅当这个对象在缓存单元中不存在时才添加），仅当缓存单元类型为List时有效
	 * @param cacheId 缓存单元ID
	 * @param value 需添加的对象
	 * @throws Exception
	 */
	public abstract void addOneIfNotContains(String cacheId, Object value) throws Exception;

	
	/**
	 * 添加集合中的全部对象到缓存单元，仅当缓存单元类型为List时有效
	 * @param cacheId 缓存单元ID
	 * @param values 需添加的对象集合
	 * @throws Exception
	 */
	public abstract void addAll(String cacheId,Collection values) throws Exception;

	/**
	 * 是否包含符合缓存操作参数的对象
	 * @param cacheId 缓存单元ID
	 * @param jdcParams 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public abstract boolean contains(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 缓存单元中对象的数量
	 * @param cacheId 缓存单元ID
	 * @return
	 * @throws Exception
	 */
	public abstract int size(String cacheId) throws Exception;
	
	/**
	 * 缓存单元中符合指定缓存操作参数的对象数量
	 * @param cacheId 缓存单元ID
	 * @param jdcParams 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public abstract int size(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 获得符合缓存操作参数的对象，如有多个符合则返回第一个
	 * @param cacheId 缓存单元ID
	 * @param jdcParams 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public abstract Object get(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 将符合缓存操作参数的对象从缓存单元中移除
	 * @param cacheId 缓存单元ID
	 * @param jdcParams 缓存操作参数
	 * @throws Exception
	 */
	public abstract void remove(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 清空缓存单元
	 * @param cacheId 缓存单元ID
	 * @throws Exception
	 */
	public abstract void clear(String cacheId) throws Exception;
	
	/**
	 * 调用缓存操作参数中指定的更新器对缓存单元中特定对象进行更新，更新哪些对象以及怎样更新由指定的更新器来决定
	 * @param cacheId 缓存单元ID
	 * @param jdcParams 缓存操作参数
	 * @throws Exception
	 */
	public abstract void update(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 调用缓存操作参数中指定的集合更新器对缓存单元中特定对象集合进行更新，更新哪些对象以及怎样更新由指定的更新器来决定
	 * @param cacheId 缓存单元ID
	 * @param jdcParams 缓存操作参数
	 * @throws Exception
	 */
	public abstract void updateCollection(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 返回符合查询的子集
	 * @param cacheId 缓存单元ID
	 * @param jdcParams 缓存操作参数
	 * @return Map类缓存单元应该返回Map，List类型缓存单元应该返回List
	 * @throws Exception
	 */
	public abstract Object sub(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 返回符合缓存操作参数的子集的key的List，仅当缓存单元类型为Map时有效
	 * @param cacheId 缓存单元ID
	 * @param jdcParams 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public abstract ConcurrentList keys(String cacheId,JCacheParams jdcParams) throws Exception;
	
	/**
	 * 返回符合缓存操作参数的子集的value的List，仅当缓存单元类型为Map时有效
	 * @param cacheId 缓存单元ID
	 * @param jdcParams 缓存操作参数
	 * @return
	 * @throws Exception
	 */
	public abstract ConcurrentList values(String cacheId,JCacheParams jdcParams) throws Exception;
}
