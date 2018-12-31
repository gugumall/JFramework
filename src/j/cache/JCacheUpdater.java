package j.cache;

import j.util.ConcurrentList;
import j.util.ConcurrentMap;

import java.io.Serializable;

/**
 * 
 * @author 肖炯
 *
 */
public interface JCacheUpdater extends Serializable{
	/**
	 * 遍历所有缓存数据进行按需更新
	 * @param map
	 * @throws Exception
	 */
	public void update(ConcurrentMap map) throws Exception;
	
	/**
	 * 更新指定集合中的元素
	 * @param collection
	 * @throws Exception
	 */
	public void updateCollection(ConcurrentMap collection) throws Exception;
	
	/**
	 * 
	 * @param list
	 * @throws Exception
	 */
	public void update(ConcurrentList list) throws Exception;
}
