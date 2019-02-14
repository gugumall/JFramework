package j.service;

import java.io.Serializable;

/**
 * 客户节点信息，服务节点是路由节点的客户节点，应用节点是路由节点和服务节点的客户节点
 * @author 肖炯
 *
 */
public class Client implements Serializable{
	private static final long serialVersionUID = 1L;
	private String uuid=null;//客户节点uuid
	private String name=null;//客户节点名
	private String key=null;//客户节点与服务节点或路由节点间约定的用于md5校验的密钥
	
	
	public Client() {
		super();
	}
	
	/**
	 * 
	 * @param uuid
	 */
	public void setUuid(String uuid){
		this.uuid=uuid;
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
	 * @param key
	 */
	public void setKey(String key){
		this.key=key;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getKey(){
		return this.key;
	}
}
