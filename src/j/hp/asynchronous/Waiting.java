package j.hp.asynchronous;

import j.common.JObject;
import j.sys.SysUtil;

/**
 * 
 * @author 肖炯
 *
 * 2019年4月16日
 *
 * <b>功能描述</b> 等待一个异步操作的执行结果
 */
public class Waiting extends JObject{
	private static final long serialVersionUID = 1L;
	private long created=0;//等待创建时间
	private String UUID;//uuid
	private long timeout=30000;//等待超时时间
	private Object defaultResultWhenTimeout=null;//当等待超时时设置的默认结果
	private Object result=null;//执行结果
	private boolean got=false;//结果是否已经返回

	/**
	 * 
	 * @param UUID
	 * @param timeout
	 * @param defaultResultWhenTimeout
	 */
	public Waiting(String UUID,long timeout,Object defaultResultWhenTimeout) {
		this.created=SysUtil.getNow();
		this.UUID=UUID;
		if(timeout>0) this.timeout=timeout;
		this.defaultResultWhenTimeout=defaultResultWhenTimeout;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isTimeout() {
		return SysUtil.getNow()-this.created>this.timeout;
	}
	
	/**
	 * 
	 * @param result
	 */
	synchronized public void setResult(Object result) {
		this.result=result;
	}
	
	/**
	 * 
	 * @return
	 */
	synchronized public Object getResult() {
		if(result!=null) return result;
		else if(this.isTimeout()) return this.defaultResultWhenTimeout;
		else return null;
	}
	
	/**
	 * 
	 * @return
	 */
	synchronized public void setGot() {
		this.got=true;
	}
	
	/**
	 * 
	 * @return
	 */
	synchronized public boolean isGot() {
		return this.got;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUUID() {
		return this.UUID;
	}
}
