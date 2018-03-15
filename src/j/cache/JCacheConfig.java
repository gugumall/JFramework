package j.cache;

import j.Properties;
import j.log.Logger;
import j.util.ConcurrentList;
import j.util.JUtilDom4j;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 
 * @author JFramework
 *
 */
public class JCacheConfig implements Runnable{
	private static Logger log=Logger.create(JCacheConfig.class);	
	private static ConcurrentList mappings=new ConcurrentList();
	private static String defaultChannel="http";
	private static volatile long cacheTimeout=0;
	private static volatile int synchronizers=1;
	private static volatile long configLastModified=0;//配置文件上次修改时间
	private static volatile boolean loading=true;
	
	static{
		load();
		
		JCacheConfig m=new JCacheConfig();
		Thread thread=new Thread(m);
		thread.start();
		log.log("JCacheConfig monitor thread started.",-1);
	}

	/**
	 * 
	 *
	 */
	public JCacheConfig() {
		super();
	}

	
	/**
	 * 
	 * @param cacheId
	 * @return
	 */
	public static JDCacheMapping mapping(String cacheId){
		waitWhileLoading();
		for(int i=0;i<mappings.size();i++){
			JDCacheMapping m=(JDCacheMapping)mappings.get(i);
			if(m.matches(cacheId)) return m;
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getDefaultChannel(){
		waitWhileLoading();
		return defaultChannel;
	}
	
	/**
	 * 
	 * @return
	 */
	public static long getCacheTimeout(){
		waitWhileLoading();
		return cacheTimeout;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getSynchronizers(){
		waitWhileLoading();
		return synchronizers;
	}
	
	/**
	 * 
	 *
	 */
	public static void load(){
		try{
			loading=true;
			
			mappings.clear();
			
			
			//文件是否存在
			File file = new File(Properties.getConfigPath()+"JCache.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			
			Document document=JUtilDom4j.parse(Properties.getConfigPath()+"JCache.xml","UTF-8");
			Element root=document.getRootElement();

			defaultChannel=root.elementText("service-channel");		
			cacheTimeout=Long.parseLong(root.elementText("cache-timeout"));		
			synchronizers=Integer.parseInt(root.elementText("synchronizers"));	
			
			List mappingEles=root.elements("mapping");
			for(int i=0;i<mappingEles.size();i++){
				Element mappingEle=(Element)mappingEles.get(i);

				JDCacheMapping mapping=new JDCacheMapping(mappingEle.attributeValue("selector"),
						mappingEle.attributeValue("service-code"),
						mappingEle.attributeValue("service-channel"),
						mappingEle.attributeValue("os"));
				
				mappings.add(mapping);
			}
			
			root=null;
			document=null;

			//配置文件最近修改时间
			configLastModified=file.lastModified();
			
			loading=false;
		}catch(Exception e){
			loading=false;
			log.log(e,Logger.LEVEL_FATAL);
		}
	}
	
	/**
	 * 
	 *
	 */
	private static void waitWhileLoading(){
		while(loading){
			try{
				Thread.sleep(100);
			}catch(Exception ex){}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			if(configLastModified>0){
				File configFile=new File(Properties.getConfigPath()+"JCache.xml");
				if(configLastModified<configFile.lastModified()){
					log.log("JCache.xml has been modified, so reload it.",-1);
					load();
				}
				configFile=null;
			}
		}
	}
}
