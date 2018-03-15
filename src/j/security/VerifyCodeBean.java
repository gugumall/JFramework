package j.security;

import j.sys.SysUtil;

import java.io.Serializable;

/**
 * 
 * @author 肖炯
 *
 */
public class VerifyCodeBean implements Serializable{
	private static final long serialVersionUID = 1L;

	private String uuid;
	private String code;
	private long timeout;
	private long time;
	
	/**
	 * 
	 * @param uuid
	 * @param code
	 * @param timeout
	 */
	public VerifyCodeBean(String uuid,String code,long timeout){
		this.uuid=uuid;
		this.code=code;
		this.timeout=timeout;
		this.time=SysUtil.getNow();
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
	public String getCode(){
		return this.code;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getTimeout(){
		return this.timeout;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getTime(){
		return this.time;
	}
}
