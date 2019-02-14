package j.service;

import java.io.Serializable;
import java.util.Properties;

/**
 * 基于http的服务接口的相关配置信息
 * @author 肖炯
 *
 */
public class Http implements Serializable{
	private static final long serialVersionUID = 1L;
	private Properties config=null;//配置信息
	
	/**
	 * 
	 *
	 */
	public Http() {
		super();
		config=new Properties();
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
	 * 服务入口地址
	 * @return
	 */
	public String getEntrance(){
		return this.getConfig("j.service.http");
	}
}
