package j.service.server;

import java.io.Serializable;

/**
 * 服务方法
 * @author JFramework
 *
 */
public class Method implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name=null;//方法名
	private String privacy=null;//隐私（认证）策略
	
	/**
	 * 
	 *
	 */
	public Method() {
		super();
	}
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name){
		this.name=name;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * 
	 * @param privacy
	 */
	public void setPrivacy(String privacy){
		this.privacy=privacy;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPrivacy(){
		return this.privacy;
	}
}
