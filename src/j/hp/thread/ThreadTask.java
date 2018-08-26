package j.hp.thread;

import j.common.JObject;

/**
 * 
 * @author ceo
 *
 */
public abstract class ThreadTask extends JObject{
	private static final long serialVersionUID=1L;
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
