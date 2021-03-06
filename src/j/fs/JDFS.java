package j.fs;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.Element;

import j.app.online.UIVersions;
import j.common.JProperties;
import j.log.Logger;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilMath;
import j.util.JUtilString;

/**
 * 
 * @author 肖炯
 *
 */
public class JDFS implements Runnable{
	private static Logger log=Logger.create(JDFS.class);
	private static ConcurrentList<JDFSMapping> mappings=new ConcurrentList<JDFSMapping>();//
	private static String serviceChannel="rmi";//通信方式
	private static int maxFileSize=102400;
	
	//需要监控是文件
	private static ConcurrentMap<String,JDFSMonitorFile> monitorFiles=new ConcurrentMap<String,JDFSMonitorFile>();
	
	private static long configLastModified=0;//配置文件上次修改时间
	private static volatile boolean loading=true;//是否正在加载配置文件
	
	static{
		load();
		
		JDFS m=new JDFS();
		Thread thread=new Thread(m);
		thread.start();
		log.log("JDFS monitor thread started.",-1);
	}

	/**
	 * 
	 *
	 */
	public JDFS() {
		super();
	}
	
	/**
	 * 根据虚拟路径获得对应的分布式服务及虚拟路径与物理路径映射关系等信息
	 * @param virtualPath
	 * @return
	 */
	public static JDFSMapping mapping(String virtualPath){
		waitWhileLoading();
		for(int i=0;i<mappings.size();i++){
			JDFSMapping m=(JDFSMapping)mappings.get(i);
			if(m.getRule(virtualPath)!=null) return m;
		}
		return null;
	}
	
	/**
	 * 根据虚拟路径获得对应物理路径（如果是本地文件）或虚拟路径（如果是远程文件）
	 * @param virtualPath
	 * @return
	 */
	public static String mappingPath(String virtualPath){
		waitWhileLoading();
		
		JDFSMapping mapping=null;
		JDFSMappingRule rule=null;
		for(int i=0;i<mappings.size();i++){
			mapping=(JDFSMapping)mappings.get(i);
			rule=mapping.getRule(virtualPath);
			if(rule!=null) break;
		}
		if(rule==null){
			return virtualPath;
		}else{
			return rule.isLocal()?rule.virtual2Physical(virtualPath,mapping.getOs()):virtualPath;
		}
	}
	
	/**
	 * 根据虚拟路径获得对应物理路径
	 * @param virtualPath
	 * @return
	 */
	public static String physicalPath(String virtualPath){
		waitWhileLoading();
		
		JDFSMapping mapping=null;
		JDFSMappingRule rule=null;
		for(int i=0;i<mappings.size();i++){
			mapping=(JDFSMapping)mappings.get(i);
			rule=mapping.getRule(virtualPath);
			if(rule!=null) break;
		}
		if(rule==null){
			return virtualPath;
		}else{
			return rule.virtual2Physical(virtualPath,mapping.getOs());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getServiceChannel(){
		waitWhileLoading();
		return serviceChannel;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getMaxFileSize(){
		return JDFS.maxFileSize;
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
			File file = new File(JProperties.getConfigPath()+"JFS.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			
			Document document=JUtilDom4j.parse(JProperties.getConfigPath()+"JFS.xml","UTF-8");
			Element root=document.getRootElement();
			
			JDFS.serviceChannel=root.elementText("service-channel");	
			if(JUtilMath.isInt(root.elementText("max-file-size"))){
				JDFS.maxFileSize=Integer.parseInt(root.elementText("max-file-size"));
			}
			
			List mappingEles=root.elements("mapping");
			for(int i=0;i<mappingEles.size();i++){
				Element mappingEle=(Element)mappingEles.get(i);

				JDFSMapping mapping=new JDFSMapping(mappingEle.attributeValue("service-code"),
						mappingEle.attributeValue("service-channel"),
						mappingEle.attributeValue("os"));
				
				List ruleEles=mappingEle.elements("rule");
				for(int j=0;j<ruleEles.size();j++){
					Element ruleEle=(Element)ruleEles.get(j);
					
					String physicalRoot=ruleEle.attributeValue("physical-root");
					physicalRoot=JUtilString.replaceAll(physicalRoot, "JFRAMEWORK_HOME", JProperties.getAppRoot());
					
					log.log(ruleEle.attributeValue("selector")+","+ruleEle.attributeValue("virtual-root")+","+physicalRoot, -1);
					
					mapping.addRule(new JDFSMappingRule(ruleEle.attributeValue("selector"),
							ruleEle.attributeValue("virtual-root"),
							physicalRoot,
							"true".equalsIgnoreCase(ruleEle.attributeValue("local"))));
				}
				
				mappings.add(mapping);
			}
			
			Element monitor=root.element("monitor");
			List monitorFileEles=monitor.elements("file");
			for(int i=0;i<monitorFileEles.size();i++){
				Element monitorFileEle=(Element)monitorFileEles.get(i);
				
				String path=JProperties.getAppRoot()+monitorFileEle.getTextTrim();
				File monitorFile=new File(path);
				if(monitorFile.exists() && monitorFile.isDirectory()) {
					File[] files=monitorFile.listFiles();
					for(int f=0; f<files.length; f++) {
						if(files[f].isDirectory()) continue;
						path=files[f].getAbsolutePath().substring(JProperties.getAppRoot().length());
						
						monitorFiles.put(path,new JDFSMonitorFile(path));
						log.log("monitor file in dir: "+path,-1);
					}
				}else {
					monitorFiles.put(monitorFileEle.getTextTrim(),new JDFSMonitorFile(monitorFileEle.getTextTrim()));
					log.log("monitor file: "+monitorFileEle.getTextTrim(),-1);
				}
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
	 * @param path
	 * @return
	 */
	public static long getLastModified(String path){
		JDFSMonitorFile file=(JDFSMonitorFile)monitorFiles.get(path);
		return file==null?0:file.getLastModified();
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static long getLastModified(HttpSession session, String path){
		String convertedPathOfUI=UIVersions.convert(session, path);
		if(convertedPathOfUI!=null) path=convertedPathOfUI;
		JDFSMonitorFile file=(JDFSMonitorFile)monitorFiles.get(path);
		return file==null?0:file.getLastModified();
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
	public void run() {
		/*
		 * 检测JFS.xml是否修改过，如修改过重新加载配置
		 */
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			List files=monitorFiles.listValues();
			for(int i=0;i<files.size();i++){
				JDFSMonitorFile file=(JDFSMonitorFile)files.get(i);
				file.renew();
			}
			
			if(configLastModified<=0) continue;

			File configFile=new File(JProperties.getConfigPath()+"JFS.xml");
			if(configLastModified<configFile.lastModified()){
				log.log("JFS.xml has been modified, so reload it.",-1);
				load();
			}
			configFile=null;
		}
	}
}
