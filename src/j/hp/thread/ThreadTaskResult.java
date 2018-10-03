package j.hp.thread;

import j.common.JObject;

/**
 * 
 * @author ceo
 *
 */
public class ThreadTaskResult extends JObject{
	private static final long serialVersionUID=1L;
	private long time=0;
	private long timeout=30000L;
	private Object[] result;
	
	/**
	 * 
	 * @param result
	 * @param timeout
	 */
	public ThreadTaskResult(Object[] result,long timeout){
		this.timeout=timeout;
		this.time=System.currentTimeMillis();
		this.result=result;
	}
	
	/**
	 * 
	 * @param result
	 */
	public ThreadTaskResult(Object[] result){
		this.time=System.currentTimeMillis();
		this.result=result;
	}
	
	/**
	 * 
	 * @return
	 */
	public Object[] getResult(){
		return this.result;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isTimeout(){
		return System.currentTimeMillis()-time>timeout;
	}
}
