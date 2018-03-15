package j.sys;

import j.util.ConcurrentList;
import j.util.ConcurrentMap;

/**
 * 
 * @author 肖炯
 *
 */
public class Statics {
	private static ConcurrentMap statics=new ConcurrentMap();
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static ConcurrentList concurrentList(String key){
		if(statics.containsKey(key)){
			return (ConcurrentList)statics.get(key);
		}else{
			ConcurrentList _static=new ConcurrentList();
			statics.put(key, _static);
			return _static;
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static ConcurrentMap concurrentMap(String key){
		if(statics.containsKey(key)){
			return (ConcurrentMap)statics.get(key);
		}else{
			ConcurrentMap _static=new ConcurrentMap();
			statics.put(key, _static);
			return _static;
		}
	}
}
