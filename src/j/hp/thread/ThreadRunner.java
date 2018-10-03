package j.hp.thread;

import java.util.List;

import j.log.Logger;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;

/**
 * 
 * @author ceo
 *
 */
public class ThreadRunner implements Runnable{
	private static Logger log=Logger.create(ThreadRunner.class);
	private ConcurrentList tasks=new ConcurrentList();
	private ConcurrentMap results=new ConcurrentMap();
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
		if(end){
			return false;
		}
		
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
		end=true;
	}
	
	@Override
	public void run(){
		while(!end||tasks.size()>0){
			try{
				Thread.sleep(this.inPool.getInterval());
			}catch(Exception e){}
			
			try{
				List keys=results.listKeys();
				for(int i=0;i<keys.size();i++){
					String key=(String)keys.get(i);
					ThreadTaskResult result=(ThreadTaskResult)results.get(i);
					if(result.isTimeout()){
						results.remove(key);
						result=null;
					}
				}
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
			
			try{
				if(!this.tasks.isEmpty()){
					ThreadTask task=(ThreadTask)tasks.remove(0);
					int retries=0;
					while(retries<=task.getRetries()){
						try{
							Object[] result=task.execute();
							if(task.getUuid()!=null
									&&!"".equals(task.getUuid())
									&&result!=null){
								results.put(task.getUuid(),new ThreadTaskResult(result,task.getResultTimeout()));
							}
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
