package j.hp.thread;

import j.common.JObject;

/**
 * 
 * @author ceo
 *
 */
public abstract class ThreadTask extends JObject{
	private static final long serialVersionUID=1L;
	private String uuid;
	private long resultTimeout=30000L;
	private Object[] in;
	private Object[] out;
	protected int retries=0;
	
	/**
	 * 
	 * @param in
	 * @param retries
	 */
	public ThreadTask(Object[] in,int retries){
		this.retries=retries;
		this.in=in;
	}
	
	/**
	 * 
	 * @param in
	 * @param retries
	 */
	public ThreadTask(Object[] in,int retries,String uuid){
		this.retries=retries;
		this.in=in;
		this.uuid=uuid;
	}
	
	/**
	 * 
	 * @param in
	 * @param retries
	 * @param uuid
	 * @param resultTimeout
	 */
	public ThreadTask(Object[] in,int retries,String uuid,long resultTimeout){
		this.retries=retries;
		this.in=in;
		this.uuid=uuid;
		this.resultTimeout=resultTimeout;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUuid(){
		return this.uuid;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getResultTimeout(){
		return this.resultTimeout;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract Object[] execute() throws Exception;
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	public abstract boolean equalz(ThreadTask other);
	
	/**
	 * 
	 * @return
	 */
	public int getRetries(){
		return this.retries;
	}
	
	/**
	 * 
	 * @return
	 */
	public Object[] getIn(){
		return this.in;
	}
	
	/**
	 * 
	 * @return
	 */
	public Object[] getOut(){
		return this.out;
	}
}
