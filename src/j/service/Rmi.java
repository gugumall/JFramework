package j.service;

import java.io.Serializable;
import java.util.Properties;

/**
 * rmi-iiop 或 rmi 相关配置信息
 * @author JFramework
 *
 */
public class Rmi implements Serializable{
	private static final long serialVersionUID = 1L;
	private Properties config=null;//配置信息
	
	/**
	 * 
	 *
	 */
	public Rmi() {
		super();
		config=new Properties();
	}
	
	/**
	 * 
	 * @param config
	 */
	public Rmi(Properties config) {
		super();
		this.config=config;
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
}
