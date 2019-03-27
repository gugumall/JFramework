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
	 * 根据实现类自定义的规则对缓存单元中的元素进行更新
	 * @param map
	 * @throws Exception
	 */
	public void update(ConcurrentMap map) throws Exception;
	
	/**
	 * 根据实现类自定义的规则对缓存单元中的一组元素进行更新
	 * @param collection
	 * @throws Exception
	 */
	public void updateCollection(ConcurrentMap collection) throws Exception;
	
	/**
	 * 根据实现类自定义的规则对缓存单元中的元素进行更新
	 * @param list
	 * @throws Exception
	 */
	public void update(ConcurrentList list) throws Exception;
	
	/**
	 * 根据实现类自定义的规则对缓存单元中的一组元素进行更新
	 * @param list
	 * @throws Exception
	 */
	public void updateCollection(ConcurrentList list) throws Exception;
}
