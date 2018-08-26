package j.hp.thread;

import j.util.ConcurrentMap;

/**
 * 
 * @author ceo
 *
 */
public class ThreadManager{
	private static ConcurrentMap pools=new ConcurrentMap();
	
	/**
	 * 
	 * @param poolId
	 * @param poolSize
	 * @param interval
	 * @param destroyAfterIdle
	 * @return
	 */
	public static ThreadPool getPool(String poolId,int poolSize,long interval,long destroyAfterIdle){
		if(poolId==null||"".equals(poolId)) return null;
		
		if(pools.containsKey(poolId)){
			ThreadPool pool=(ThreadPool)pools.get(poolId);
			return pool.getInstance(poolSize);
		}else{
			ThreadPool pool=new ThreadPool(poolId,poolSize,interval,destroyAfterIdle);
			pools.put(poolId,pool);
			return pool;
		}
	}
}
