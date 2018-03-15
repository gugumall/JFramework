package j.common;

import j.util.JUtilKeyValue;

/**
 * 
 * @author one
 *
 */
public class JProperties{

	/**
	 * 
	 * @param propertyName
	 * @return
	 */
	public static String getProperty(String propertyName){
		return j.Properties.getProperty(propertyName);
	}
	
	/**
	 * 
	 * @param groupName
	 * @param propertyName
	 * @return
	 */
	public static String getProperty(String groupName,String propertyName){
		return j.Properties.getProperty(groupName,propertyName);
	}
	
	/**
	 * 配置文件根目录
	 * @return
	 */
	public static String getConfigPath(){
		return j.Properties.getConfigPath();
	}
	
	/**
	 * 多语言资源
	 * @return
	 */
	public static String getI18NPath(){
		return j.Properties.getI18NPath();
	}
	
	/**
	 * web应用根目录
	 * @return
	 */
	public static String getWebRoot(){
		return j.Properties.getWebRoot();
	}
	
	/**
	 * 类文件存放根目录
	 * @return
	 */
	public static String getClassPath(){
		return j.Properties.getClassPath();
	}
	
	/**
	 * 类文件存放根目录
	 * @return
	 */
	public static String getJarPath(){
		return j.Properties.getJarPath();
	}
	
	/**
	 * 应用根路径
	 * @return
	 */
	public static String getAppRoot(){
		return j.Properties.getAppRoot();
	}
	
	/**
	 * 日志级别
	 * @return
	 */
	public static String getLogLevel(){
		return j.Properties.getLogLevel();
	}
	
	/**
	 * 日志存储数据库
	 * @return
	 */
	public static String getLogDatabase(){
		return j.Properties.getLogDatabase();
	}
	
	/**
	 * 日志处理线程个数
	 * @return
	 */
	public static int getLoggers(){
		return j.Properties.getLoggers();
	}
	
	/**
	 * JHttp实例个数
	 * @return
	 */
	public static int getJHttpInstances(){
		return j.Properties.getJHttpInstances();
	}
	
	/**
	 * 每个JHttp实例默认HttpClient数
	 * @return
	 */
	public static int getClientsOfJHttpInstance(){
		return j.Properties.getClientsOfJHttpInstance();
	}
	
	/**
	 * 
	 * @param group
	 * @return
	 */
	public static java.util.Properties getProperties(String group){
		return j.Properties.getProperties(group);
	}
	
	/**
	 * 
	 * @param group
	 * @return
	 */
	public static JUtilKeyValue[] getPropertiesAsArray(String group){
		return j.Properties.getPropertiesAsArray(group);
	}
	
	/**
	 * 
	 * @param prefix
	 * @return
	 */
	public static java.util.Properties getPropertiesStartsWith(String prefix){
		return j.Properties.getPropertiesStartsWith(prefix);
	}
	
	/**
	 * 
	 * @param prefix
	 * @return
	 */
	public static JUtilKeyValue[] getPropertiesStartsWithAsArray(String prefix){
		return j.Properties.getPropertiesStartsWithAsArray(prefix);
	}
}
