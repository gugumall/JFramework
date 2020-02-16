package j.hp.MapReduce;

import java.util.ArrayList;
import java.util.List;

import j.hp.thread.ThreadPool;
import j.hp.thread.ThreadRunner;
import j.hp.thread.ThreadTask;
import j.hp.thread.ThreadTaskResult;

/**
 * 
 * @author 肖炯
 *
 * 2020年2月16日
 *
 * <b>功能描述</b>
 */
public class Overman {
	/**
	 * 
	 * @param pool
	 * @param tasks
	 * @return
	 */
	public static List<ThreadTaskResult> execute(ThreadPool pool, List<ThreadTask> tasks){
		List<ThreadTaskResult> results=new ArrayList();
		List<ThreadRunner> runners=new ArrayList();
		for(int i=0; i<tasks.size(); i++) {
			runners.add(pool.addTask(tasks.get(i)));
		}
		
		while(results.size()<runners.size()) {
			for(int i=0; i<runners.size(); i++) {
				String uuid=tasks.get(i).getUuid();
				ThreadTaskResult result=runners.get(i).getResult(uuid);
				if(result!=null) results.add(result);
			}
			try {
				Thread.sleep(10);
			}catch(Exception e) {}
		}
		
		return results;
	}
}
