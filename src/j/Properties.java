package j;

import j.util.JUtilKeyValue;
import j.util.JUtilMath;
import j.util.JUtilSorter;
import j.util.JUtilString;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @deprecated
 * @author 肖炯
 *
 */
public class Properties implements Runnable {	
	/*
	 * 保存system.properties中以key-value形式记录的系统变量。
	 * 其中有两个jframework必须的变量WebRoot（web应用根目录）、ConfigPath（jframe配置文件存放目录），例如：
	 * WebRoot=d:/tomcat/webapps/jframework/
	 * ConfigPath=d:/tomcat/webapps/jframework/WEB-INF/classes/config/
	 */
	private static Map properties= new ConcurrentHashMap();
	private static Map groups= new ConcurrentHashMap();
	private static String JFRAMEWORK_HOME=""; 
	private static String OS_TYPE="linux"; 
	private static volatile boolean loading=true;
	
	static{		
		load();
		
		//启动监控线程
		Properties i=new Properties();
		Thread th=new Thread(i);
		th.start();
		System.out.println("Properties thread started.");
	}
	
	/**
	 * 应用根路径
	 * @return
	 */
	public static String getAppRoot(){
		return JFRAMEWORK_HOME;
	}
	
	public static String getOsType(){
		return OS_TYPE;
	}
	
	/**
	 * 
	 *
	 */
	private static void load(){
		loading=true;
		properties.clear();
		groups.clear();
		try{
			String os=System.getProperty("os.name");
			if(os!=null&&os.toLowerCase().indexOf("windows")>-1) OS_TYPE="windows";
			else OS_TYPE="linux";
			
			try{
				JFRAMEWORK_HOME=System.getenv("JFRAMEWORK_HOME");
				if(JFRAMEWORK_HOME==null||"".equals(JFRAMEWORK_HOME)){
					JFRAMEWORK_HOME=Properties.class.getClassLoader().getResource("").toString();
					if(JFRAMEWORK_HOME.indexOf("/WEB-INF/")>0){
						JFRAMEWORK_HOME=JFRAMEWORK_HOME.substring(0,JFRAMEWORK_HOME.indexOf("/WEB-INF/"));
					}
					
					if(JFRAMEWORK_HOME.startsWith("file:/")){
						JFRAMEWORK_HOME=JFRAMEWORK_HOME.substring(6);
					}else if(JFRAMEWORK_HOME.startsWith("/")){
						JFRAMEWORK_HOME=JFRAMEWORK_HOME.substring(1);
					}
					
					if("linux".equals(OS_TYPE)&&!JFRAMEWORK_HOME.startsWith("/")){
						JFRAMEWORK_HOME="/"+JFRAMEWORK_HOME;
					}
					
					System.out.println("get JFRAMEWORK_HOME by getResource:"+JFRAMEWORK_HOME);
				}else{
					System.out.println("get JFRAMEWORK_HOME from system env:"+JFRAMEWORK_HOME);
				}
				
				if(JFRAMEWORK_HOME!=null){
					JFRAMEWORK_HOME=JUtilString.replaceAll(JFRAMEWORK_HOME,"%20"," ");
				}
			}catch(Exception e){
				System.out.println("load properties error: "+e.getMessage());
				//e.printStackTrace();
			}
			
			ResourceBundle keyValuePairs = ResourceBundle.getBundle("config.jframework");
			for(Iterator it=keyValuePairs.keySet().iterator();it.hasNext();){
				String key=(String)it.next();
				String value=(String)keyValuePairs.getObject(key);
				value=new String(value.getBytes("iso-8859-1"),"UTF-8");
				
				if(JFRAMEWORK_HOME!=null
						&&!"".equals(JFRAMEWORK_HOME)
						&&value.indexOf("JFRAMEWORK_HOME")>-1){
					value=JUtilString.replaceAll(value,"JFRAMEWORK_HOME", JFRAMEWORK_HOME);
				}
				
				int no=0;
				if(key.startsWith("<")){
					int noEnd=key.indexOf(">");
					if(noEnd>1){
						String noString=key.substring(1,noEnd);
						if(JUtilMath.isInt(noString)) no=Integer.parseInt(noString);
						key=key.substring(noEnd+1);
					}
				}
				
				properties.put(key,new JUtilKeyValue(key,value,no));
				System.out.println("key-value pair from system.properties: "+key+" = "+value); 
			}
		}catch(Exception e){
			System.out.println("load properties error: "+e.getMessage());
			//e.printStackTrace();
		}
		loading=false;
	}
	
	/**
	 * 
	 *
	 */
	private static void reload(){
		try{
			java.util.Properties ps=new java.util.Properties();
			ps.load(new InputStreamReader(new FileInputStream(getConfigPath()+"jframework.properties"),"UTF-8"));
			
			loading=true;
			properties.clear();
			groups.clear();
			
			for(Iterator it=ps.keySet().iterator();it.hasNext();){
				String key=(String)it.next();
				String value=(String)ps.getProperty(key);
				if(JFRAMEWORK_HOME!=null
						&&!"".equals(JFRAMEWORK_HOME)
						&&value.indexOf("JFRAMEWORK_HOME")>-1){
					value=JUtilString.replaceAll(value,"JFRAMEWORK_HOME", JFRAMEWORK_HOME);
				}
				
				int no=0;
				if(key.startsWith("<")){
					int noEnd=key.indexOf(">");
					if(noEnd>1){
						String noString=key.substring(1,noEnd);
						if(JUtilMath.isInt(noString)) no=Integer.parseInt(noString);
						key=key.substring(noEnd+1);
					}
				}
				
				properties.put(key,new JUtilKeyValue(key,value,no));
				
				System.out.println("reload key-value pair from system.properties: "+key+" = "+value); 
			}
		}catch(Exception e){
			System.out.println("reload properties error: "+e.getMessage());
			e.printStackTrace();
		}

		loading=false;
	}
	
	/**
	 * 
	 * @param propertyName
	 * @return
	 */
	public static String getProperty(String propertyName){
		waitWhileLoading();
		JUtilKeyValue p=(JUtilKeyValue)properties.get(propertyName);
		return p==null?null:p.getValue().toString();
	}
	
	/**
	 * 
	 * @param groupName
	 * @param propertyName
	 * @return
	 */
	public static String getProperty(String groupName,String propertyName){
		waitWhileLoading();
		JUtilKeyValue p=(JUtilKeyValue)properties.get("["+groupName+"]"+propertyName);
		return p==null?null:p.getValue().toString();
	}
	
	/**
	 * 配置文件根目录
	 * @return
	 */
	public static String getConfigPath(){
		waitWhileLoading();
		return getProperty("ConfigPath");
	}
	
	/**
	 * 多语言资源
	 * @return
	 */
	public static String getI18NPath(){
		waitWhileLoading();
		return getProperty("I18NPath");
	}
	
	/**
	 * web应用根目录
	 * @return
	 */
	public static String getWebRoot(){
		waitWhileLoading();
		return getProperty("WebRoot");
	}
	
	/**
	 * 类文件存放根目录
	 * @return
	 */
	public static String getClassPath(){
		waitWhileLoading();
		return getProperty("ClassPath");
	}
	
	/**
	 * 类文件存放根目录
	 * @return
	 */
	public static String getJarPath(){
		waitWhileLoading();
		return getProperty("JarPath");
	}
	
	/**
	 * 日志级别
	 * @return
	 */
	public static String getLogLevel(){
		waitWhileLoading();
		return getProperty("LogLevel");
	}
	
	/**
	 * 日志存储数据库
	 * @return
	 */
	public static String getLogDatabase(){
		waitWhileLoading();
		return getProperty("LogDatabase");
	}
	
	/**
	 * 日志处理线程个数
	 * @return
	 */
	public static int getLoggers(){
		waitWhileLoading();
		String v=getProperty("Loggers");
		if(JUtilMath.isInt(v)) return Integer.parseInt(v);
		return 1;
	}
	
	/**
	 * JHttp实例个数
	 * @return
	 */
	public static int getJHttpInstances(){
		waitWhileLoading();
		if(getProperty("JHttpInstances")==null) return 5;
		return Integer.parseInt(getProperty("JHttpInstances"));
	}
	
	/**
	 * 每个JHttp实例默认HttpClient数
	 * @return
	 */
	public static int getClientsOfJHttpInstance(){
		waitWhileLoading();
		if(getProperty("ClientsOfJHttpInstance")==null) return 1;
		return Integer.parseInt(getProperty("ClientsOfJHttpInstance"));
	}
	
	/**
	 * 
	 * @param group
	 * @return
	 */
	public static java.util.Properties getProperties(String group){
		waitWhileLoading();
		group=(group).intern();
		synchronized(group){
			if(!groups.containsKey(group)){
				java.util.Properties props=new java.util.Properties();
				Iterator it=properties.keySet().iterator();
				while(it.hasNext()){
					String key=(String)it.next();
					if(key.startsWith("["+group+"]")){
						props.put(key.substring(group.length()+2),getProperty(key));
					}
				}
				groups.put(group,props);
			}
				
			return (java.util.Properties)groups.get(group);
		}
	}
	
	/**
	 * 
	 * @param group
	 * @return
	 */
	public static JUtilKeyValue[] getPropertiesAsArray(String group){
		waitWhileLoading();
		
		String groupName=(group+".AsArray").intern();
		synchronized(groupName){
			if(!groups.containsKey(group)){	
				List props=new LinkedList();
				Iterator it=properties.keySet().iterator();
				while(it.hasNext()){
					String key=(String)it.next();
					if(key.startsWith("["+group+"]")){
						JUtilKeyValue kv=(JUtilKeyValue)properties.get(key);
						key=key.substring(group.length()+2);
						props.add(new JUtilKeyValue(key,kv.getValue(),kv.getNo()));
					}
				}
				
				PropertySorter sorter=new PropertySorter();
				props=sorter.bubble(props,JUtilSorter.ASC);
				
				groups.put(group,props.toArray(new JUtilKeyValue[props.size()]));
			}
			
			return (JUtilKeyValue[])groups.get(group);
		}
	}
	
	/**
	 * 
	 * @param prefix
	 * @return
	 */
	public static java.util.Properties getPropertiesStartsWith(String prefix){
		waitWhileLoading();
		synchronized(prefix){
			java.util.Properties props=new java.util.Properties();
			Iterator it=properties.keySet().iterator();
			while(it.hasNext()){
				String key=(String)it.next();
				if(key.startsWith(prefix)){
					props.put(key,getProperty(key));
				}
			}
			return props;
		}
	}
	
	/**
	 * 
	 * @param prefix
	 * @return
	 */
	public static JUtilKeyValue[] getPropertiesStartsWithAsArray(String prefix){
		waitWhileLoading();
		
		synchronized(prefix){
			List props=new LinkedList();
			Iterator it=properties.keySet().iterator();
			while(it.hasNext()){
				String key=(String)it.next();
				if(key.startsWith(prefix)){
					props.add(properties.get(key));
				}
			}
			
			PropertySorter sorter=new PropertySorter();
			props=sorter.bubble(props,JUtilSorter.ASC);
			
			JUtilKeyValue[] temp=new JUtilKeyValue[props.size()]; 
			props.toArray(temp);
			return temp;
		}
	}
	
	/**
	 * 
	 *
	 */
	private static void waitWhileLoading(){
		while(loading){
			try{
				Thread.sleep(1000);
			}catch(Exception e){}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		String config=Properties.getClassPath()+"config/jframework.properties";
		long upd=0;
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			File file=new File(config);
			if(upd==0){
				upd=file.lastModified();
			}else{
				if(upd<file.lastModified()){
					System.out.println("jframework.properties has been modified, so reload it.");
					reload();
					upd=file.lastModified();
				}
			}
		}
	}
} 

/**
 * 
 * @author 肖炯
 *
 */
class PropertySorter extends JUtilSorter{
	private static final long serialVersionUID = 1L;

	/*
	 *  (non-Javadoc)
	 * @see j.util.JUtilSorter#compare(java.lang.Object, java.lang.Object)
	 */
	public String compare(Object pre, Object after){
		JUtilKeyValue beanPre=(JUtilKeyValue)pre;
		JUtilKeyValue beanAfter=(JUtilKeyValue)after;
		String beanPreId=(String)beanPre.getKey();
		String beanAfterId=(String)beanAfter.getKey();
		
		if(beanPre.getNo()<beanAfter.getNo()){
			return JUtilSorter.SMALLER;
		}else if(beanPre.getNo()>beanAfter.getNo()){
			return JUtilSorter.BIGGER;
		}else{
			if(beanPreId.compareTo(beanAfterId)>0){
				return JUtilSorter.BIGGER;
			}else{
				return JUtilSorter.SMALLER;
			}
		}
	}
}
