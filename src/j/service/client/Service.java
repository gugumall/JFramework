package j.service.client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Properties;

/**
 * 服务的基本信息，主要包括与每个服务进行md5校验的密钥
 * @author 肖炯
 *
 */
public class Service implements Serializable {
	private static final long serialVersionUID = 1L;
	private String code=null;
	private String name=null;
	private Properties config=null;
	
	/**
	 * 
	 *
	 */
	public Service() throws RemoteException{
		super();
		config=new Properties();
	}
	
	/**
	 * 
	 * @param code
	 */
	public void setCode(String code){
		this.code=code;
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
	 * @param value
	 */
	public void addConfig(String key,String value){
		config.put(key,value);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getConfig(String key){
		return (String)config.getProperty(key);
	}
	
	/**
	 * 
	 * @return
	 */
	public Properties getConfig(){
		return config;
	}
	
	/**
	 * 与服务进行md5校验的密钥
	 * @return
	 */
	public String getClientKey4Service(){
		return this.getConfig("j.service.key");
	}
}
