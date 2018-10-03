package j.hp.thread;

import j.util.ConcurrentMap;

import java.util.List;

/**
 * 
 * @author ceo
 *
 */
public class ThreadManager implements Runnable{
	private static ConcurrentMap pools=new ConcurrentMap();
	
	static{
		ThreadManager m=new ThreadManager();
		Thread t=new Thread(m);
		t.start();
		
		System.out.println("并发线程池管理类已启动......");
	}
	
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

	@Override
	public void run(){
		while(true){
			try{
				Thread.sleep(1000);
			}catch(Exception e){}
			
			try{
				List _pools=pools.listValues();
				for(int i=0;i<_pools.size();i++){
					ThreadPool pool=(ThreadPool)_pools.get(i);
					pool.clearIfIdle();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
