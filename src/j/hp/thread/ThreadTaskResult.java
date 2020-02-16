package j.hp.thread;

import j.common.JObject;

/**
 * 
 * @author ceo
 *
 */
public class ThreadTaskResult extends JObject{
	private static final long serialVersionUID=1L;
	private String uuid=null;
	private long time=0;
	private long timeout=30000L;
	private Object[] result;
	
	/**
	 * 
	 * @param uuid
	 * @param result
	 * @param timeout
	 */
	public ThreadTaskResult(String uuid, Object[] result,long timeout){
		this.uuid=uuid;
		this.timeout=timeout;
		this.time=System.currentTimeMillis();
		this.result=result;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param result
	 */
	public ThreadTaskResult(String uuid, Object[] result){
		this.uuid=uuid;
		this.time=System.currentTimeMillis();
		this.result=result;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUuid() {
		return uuid;
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
