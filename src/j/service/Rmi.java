package j.service;

import java.io.Serializable;
import java.util.Properties;

/**
 * rmi-iiop 或 rmi 相关配置信息
 * @author 肖炯
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
		this.config=new Properties(config);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void addConfig(String key,String value){
		config.setProperty(key,value);
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
	
	public static void main(String[] args) {
		Properties p1=new Properties();
		Properties p2=new Properties();
		
		p1.setProperty("111", "aaa");
		p2.setProperty("111", "bbb");
		
		System.out.println(p1.getProperty("111"));
		System.out.println(p2.getProperty("111"));
	}
}
