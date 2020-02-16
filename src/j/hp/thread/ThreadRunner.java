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
			if(t==null) continue;
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
	 * @param uuid
	 * @return
	 */
	public ThreadTaskResult getResult(String uuid) {
		if(uuid==null||"".equals(uuid)) return null;
		
		while(!results.containsKey(uuid)) {
			try {
				Thread.sleep(1);
			}catch(Exception e) {}
		}
		ThreadTaskResult result=(ThreadTaskResult)results.remove(uuid);
		if(result==null) return null;
		
		Object[] resultObjs=result.getResult();
		if(resultObjs!=null && resultObjs.length>0 && "IS-NULL".equals(resultObjs[0])) {
			return null;
		}
		
		return result;
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
							//System.out.println("execute ..."+task.getUuid());
							Object[] result=task.execute();
							if(task.getUuid()!=null
									&&!"".equals(task.getUuid())
									&&result!=null){
								results.put(task.getUuid(),new ThreadTaskResult(task.getUuid(), result,task.getResultTimeout()));
							}
							break;
						}catch(Exception e){
							retries++;
							log.log(e,Logger.LEVEL_ERROR);
						}
						
						//如果任务指定了ID，即使没有结果，也写入表示为null的固定字符串（以免调用上下文获取不到结果无限等待）
						if(task.getUuid()!=null
								&&!"".equals(task.getUuid())
								&&!results.containsKey(task.getUuid())) {
							results.put(task.getUuid(),new ThreadTaskResult(task.getUuid(), new Object[] {"IS-NULL",task.getResultTimeout()}));
						}
					}
				}
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
		}
	}
}
