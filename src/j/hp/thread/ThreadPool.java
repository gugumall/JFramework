package j.hp.thread;

import j.sys.SysUtil;
import j.util.ConcurrentList;

/**
 * 
 * @author ceo
 *
 */
public class ThreadPool{
	private String poolId;
	private int poolSize=1;
	private long interval=1000;
	private long destroyAfterIdle=0;
	private long latestUsed=0;
	private ConcurrentList threads=new ConcurrentList();
	private int selector=0;
	
	/**
	 * 
	 * @param poolId
	 * @param poolSize
	 * @param interval
	 * @param destroyAfterIdle
	 */
	public ThreadPool(String poolId,int poolSize,long interval,long destroyAfterIdle){
		if(poolSize<=0) poolSize=1;
		this.poolId=poolId;
		this.poolSize=poolSize;
		this.interval=interval;
		this.destroyAfterIdle=destroyAfterIdle;
		this.start();
	}
	
	/**
	 * 
	 * @return
	 */
	public ThreadPool getInstance(){
		this.start();
		return this;
	}
	
	/**
	 * 
	 * @param poolSize
	 * @return
	 */
	public ThreadPool getInstance(int poolSize){
		this.poolSize=poolSize;
		this.start();
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPoolId(){
		return this.poolId;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getPoolSize(){
		return this.poolSize;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getInterval(){
		return this.interval;
	}
	
	/**
	 * 
	 * @param task
	 * @return
	 */
	private ThreadRunner exists(ThreadTask task){
		for(int i=0;i<this.threads.size();i++){
			ThreadRunner runner=(ThreadRunner)this.threads.get(i);
			if(runner.exists(task)) return runner;
		}
		return null;
	}
	
	/**
	 * 
	 * @param task
	 * @return
	 */
	synchronized public ThreadRunner addTask(ThreadTask task){
		this.latestUsed=SysUtil.getNow();
		
		ThreadRunner runner=exists(task);
		
		if(runner!=null) return runner;
		
		if(selector>=this.threads.size()) selector=0;
		runner=(ThreadRunner)this.threads.get(selector++);
		runner.addTask(task);
		
		return runner;
	}
	
	/**
	 * 
	 */
	public void clearIfIdle(){
		if(this.destroyAfterIdle>0
				&&this.latestUsed>0
				&&SysUtil.getNow()-this.latestUsed>this.destroyAfterIdle){
			for(int i=0;i<this.threads.size();i++){
				ThreadRunner runner=(ThreadRunner)this.threads.get(i);
				runner.destroy();
			}
			this.threads.clear();
			this.latestUsed=0;
			this.selector=0;
		}
	}
	
	/**
	 * 
	 */
	private void start(){
		while(threads.size()<this.poolSize){
			ThreadRunner runner=new ThreadRunner(this);
			Thread thread=new Thread(runner);
			thread.start();
			threads.add(runner);
		}
	}
}
