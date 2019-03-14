package j.service;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import j.common.JProperties;
import j.log.Logger;
import j.service.hello.HelloClient;
import j.service.router.RouterManager;
import j.service.server.ServiceManager;
import j.sys.Initializer;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;

/**
 * 启动服务框架
 * @author 肖炯
 *
 */
public class Manager implements Initializer{
	private static Logger log=Logger.create(Manager.class);
	private static volatile boolean debug=false;//是否测试环境
	private static volatile boolean is_router=false;//是否路由节点
	private static volatile boolean is_server=false;//是否服务节点
	private static volatile boolean is_client=false;//是否应用节点
	private static ConcurrentMap<String,String> routerConfig=new ConcurrentMap<String,String>();
	private static ConcurrentMap<String,String> serverConfig=new ConcurrentMap<String,String>();
	private static ConcurrentMap<String,String> clientConfig=new ConcurrentMap<String,String>();
	
	static{
		init();
	}
	
	/**
	 * 加载信息
	 *
	 */
	private static void init(){
		try{			
			//文件是否存在
			File file = new File(JProperties.getConfigPath()+"service.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
	        
			Document document=JUtilDom4j.parse(JProperties.getConfigPath()+"service.xml","UTF-8");
			Element root=document.getRootElement();
			
			//是否调试模式
			debug="true".equalsIgnoreCase(root.elementText("debug"));
			
			//当前节点是否路由节点
			Element ele=root.element("is-router");
			if(ele!=null){
				is_router=true;
				List props=ele.elements("property");
				for(int i=0;i<props.size();i++){
					Element pEle=(Element)props.get(i);
					routerConfig.put(pEle.attributeValue("key"),pEle.attributeValue("value"));
				}
			}
			

			//当前节点是否服务节点
			ele=root.element("is-server");
			if(ele!=null){
				is_server=true;
				List props=ele.elements("property");
				for(int i=0;i<props.size();i++){
					Element pEle=(Element)props.get(i);
					serverConfig.put(pEle.attributeValue("key"),pEle.attributeValue("value"));
				}
			}
			

			//当前节点是否客户节点
			ele=root.element("is-client");
			if(ele!=null){
				is_client=true;
				List props=ele.elements("property");
				for(int i=0;i<props.size();i++){
					Element pEle=(Element)props.get(i);
					clientConfig.put(pEle.attributeValue("key"),pEle.attributeValue("value"));
				}
			}
			
			root=null;
			document=null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean isRouter(){
		return is_router;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getRouterNodeUuid(){
		return (String)routerConfig.get("j.service.uuid");
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean isServer(){
		return is_server;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getServerNodeUuid(){
		return (String)serverConfig.get("j.service.uuid");
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getServerKeyToRouter(){
		return (String)serverConfig.get("j.service.key");
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean isClient(){
		return is_client;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getClientNodeUuid(){
		return (String)clientConfig.get("j.service.uuid");
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getClientKeyToRouter(){
		return (String)clientConfig.get("j.service.key");
	}
	
	
	
	/**
	 * 
	 *
	 */
	public static void startup(){
		log.log("to start service manager......",-1);
		
		RouterManager.load();//启动路由（如果是路由节点）与路由代理（不管任何节点都会启动路由代理）
	
		if(isServer()){
			ServiceManager.load();//启动服务
		}
		
		if(isClient()){
			j.service.client.Client.load();//启动客户端
		}
	}

	//在sys.xml中配置为Initializer后，系统启动时会自动调用该方法（初始化servlet j.sys.Initializers中调用）
	public void initialization() throws Exception {
		
		startup();
		
		if(debug){
			try{		
				HelloClient.test();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 启动服务框架
	 * @param args
	 */
	public static void main(String[] args){
		startup();
		
		if(debug){
			try{		
				HelloClient.test();
			}catch(Exception e){}
		}
	}
}
