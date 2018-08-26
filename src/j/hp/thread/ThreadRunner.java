package j.hp.thread;

import j.log.Logger;
import j.util.ConcurrentList;

/**
 * 
 * @author ceo
 *
 */
public class ThreadRunner implements Runnable{
	private static Logger log=Logger.create(ThreadRunner.class);
	private ConcurrentList tasks=new ConcurrentList();
	private ThreadPool inPool;
	private boolean end=false;
	
	/**
	 * 
	 * @param interval
	 */
	public ThreadRunner(ThreadPool pool){
		this.inPool=pool;
	}
	
	/**
	 * 
	 * @return
	 */
	public ConcurrentList getTasks(){
		return tasks.snapshot();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getTasksCount(){
		return tasks.size();
	}
	
	/**
	 * 
	 * @param task
	 * @return
	 */
	public boolean exists(ThreadTask task){
		for(int i=0;i<tasks.size();i++){
			ThreadTask t=(ThreadTask)tasks.get(i);
			if(t==null) return false;
			if(t.equalz(task)) return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param task
	 */
	public boolean addTask(ThreadTask task){
		if(!this.exists(task)){
			tasks.add(task);
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 */
	public void destroy(){
		while(!this.tasks.isEmpty()){
			try{
				Thread.sleep(100);
			}catch(Exception e){}
		}
		end=true;
	}
	
	@Override
	public void run(){
		while(!end){
			try{
				Thread.sleep(this.inPool.getInterval());
			}catch(Exception e){}
			
			try{
				if(!this.tasks.isEmpty()){
					ThreadTask task=(ThreadTask)tasks.remove(0);
					int retries=0;
					while(retries<=task.getRetries()){
						try{
							task.execute();
							break;
						}catch(Exception e){
							retries++;
							log.log(e,Logger.LEVEL_ERROR);
						}
					}
				}
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
		}
	}
}
