package j.cache;

import j.log.Logger;
import j.util.ConcurrentList;

import java.util.Collection;
import java.util.Map;

/**
 * 
 * @author 肖炯
 *
 */
public class JDCacheSynchronizer implements Runnable{
	private static Logger log=Logger.create(JDCacheSynchronizer.class);
	private String serviceUuid;
	private ConcurrentList tasks=new ConcurrentList();
	private JDCache target=null;
	
	/**
	 * 
	 * @param serviceUuid
	 */
	public JDCacheSynchronizer(String serviceUuid){
		this.serviceUuid=serviceUuid;
		target=new JDCache(this.serviceUuid);
	}
	
	
	/**
	 * 
	 * @param task
	 */
	public void addTask(Object[] task){
		tasks.add(task);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			try{
				Thread.sleep(100);
			}catch(Exception ex){}
			
			while(!tasks.isEmpty()){
				Object[] task=(Object[])tasks.get(0);
				String method=(String)task[0];
				String cacheId="syn:"+(String)task[1]+","+task[task.length-2]+":"+task[task.length-1];
				
				log.log("syn cache to "+this.serviceUuid+","+method+","+cacheId,Logger.LEVEL_DEBUG);
				
				try{
					if(method.equals("createUnit")){
						target.createUnit(cacheId,((Integer)task[2]).intValue(),((Integer)task[3]).intValue());
					}else if(method.equals("setActiveTime")){
						target.setActiveTime(cacheId);
					}else if(method.equals("addOne-key-value")){
						target.addOne(cacheId,task[2],task[3]);
					}else if(method.equals("addAll-mappings")){
						target.addAll(cacheId,(Map)task[2]);
					}else if(method.equals("addOne-value")){
						target.addOne(cacheId,task[2]);
					}else if(method.equals("addOneIfNotContains-value")){
						target.addOneIfNotContains(cacheId,task[2]);
					}else if(method.equals("addAll-values")){
						target.addAll(cacheId,(Collection)task[2]);
					}else if(method.equals("remove")){
						target.remove(cacheId,(JCacheParams)task[2]);
					}else if(method.equals("clear")){
						target.clear(cacheId);
					}else if(method.equals("update")){
						target.update(cacheId,(JCacheParams)task[2]);
					}
					
					tasks.remove(0);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					try{
						Thread.sleep(5000);
					}catch(Exception ex){}
				}
			}
		}
	}
}
