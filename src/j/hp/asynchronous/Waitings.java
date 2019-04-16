package j.hp.asynchronous;

import java.util.List;

import j.log.Logger;
import j.util.ConcurrentMap;
import j.util.JUtilUUID;

/**
 * 
 * @author 肖炯
 *
 * 2019年4月16日
 *
 * <b>功能描述</b> 当一个进程触发一个异步操作后，需要等待获得该异步操作的结果才能返回，该类通过分配一个UUID将当前进程与异步操作关联起来，异步操作向UUID设置操作结果，当前进程从UUID查询结果，并可设定等待超时时间。
 */
public class Waitings implements Runnable{
	private static Logger log=Logger.create(Waitings.class);
	private static ConcurrentMap<String,Waiting> waitings=new ConcurrentMap<String,Waiting>();
	
	//启动监控线程清除已获取结果的等待
	static {
		Waitings instance=new Waitings();
		Thread thread=new Thread(instance);
		thread.start();
		log.log("waitings monitor started",-1);
	}
	
	/**
	 * 
	 * @param timeout
	 * @param defaultResultWhenTimeout
	 * @return 返回为任务分配的uuid
	 */
	public static String waiting(long timeout,Object defaultResultWhenTimeout) {
		String UUID=JUtilUUID.genUUID();
		Waiting waiting=new Waiting(UUID,timeout,defaultResultWhenTimeout);
		waitings.put(UUID,waiting);
		return UUID;
	}
	
	/**
	 * @param UUID
	 * @param timeout
	 * @param defaultResultWhenTimeout
	 * @return 返回为任务分配的uuid
	 */
	public static String waiting(String UUID,long timeout,Object defaultResultWhenTimeout) {
		if(UUID==null) UUID=JUtilUUID.genUUID();
		Waiting waiting=new Waiting(UUID,timeout,defaultResultWhenTimeout);
		waitings.put(UUID,waiting);
		return UUID;
	}
	
	/**
	 * 得到结果（直到结果返回或超时）
	 * @param UUID
	 * @return
	 */
	public static Object getResult(String UUID) {
		Waiting waiting=waitings.get(UUID);
		if(waiting==null) return null;
		
		Object result=waiting.getResult();
		while(result==null&&!waiting.isTimeout()) {
			try {
				Thread.sleep(10);
			}catch(Exception e) {}
			result=waiting.getResult();
		}
		
		//标记为已经获得结果（或超时）
		waiting.setGot();
		
		return result;
	}
	
	/**
	 * 设置结果
	 * @param UUID
	 * @param result
	 */
	public static void setResult(String UUID,Object result) {
		Waiting waiting=waitings.get(UUID);
		if(waiting==null) return;
		
		waiting.setResult(result);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(10);
			}catch(Exception e) {}
			
			List _waitings=waitings.listValues();
			for(int i=0;i<_waitings.size();i++) {
				Waiting waiting=(Waiting)_waitings.get(i);
				
				//已经获得结果（或超时），从等待列表移除
				if(waiting==null||waiting.isGot()) {
					waitings.remove(waiting.getUUID());
					waiting=null;
				}
			}
		}
	}
}
