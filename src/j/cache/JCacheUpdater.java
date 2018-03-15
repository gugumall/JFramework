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
	 * 
	 * @param map
	 * @throws Exception
	 */
	public void update(ConcurrentMap map) throws Exception;
	
	/**
	 * 
	 * @param list
	 * @throws Exception
	 */
	public void update(ConcurrentList list) throws Exception;
}
