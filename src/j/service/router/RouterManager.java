package j.service.router;

import java.io.File;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.dom4j.Document;
import org.dom4j.Element;

import j.Properties;
import j.common.JProperties;
import j.http.JHttp;
import j.log.Logger;
import j.nvwa.Nvwa;
import j.nvwa.NvwaObject;
import j.service.Client;
import j.service.Constants;
import j.service.Http;
import j.service.Manager;
import j.service.Rmi;
import j.service.server.ServiceBase;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilList;
import j.util.JUtilRandom;

/**
 * 加载路由信息并启动路由器（只在server.xml中标记为is-router才会启动）与路由代理
 * @author 肖炯
 *
 */
public class RouterManager implements Runnable {
	private static Logger log=Logger.create(RouterManager.class);
	
	private static ConcurrentMap routerConfigs=new ConcurrentMap();//键：路由节点uuid  值j.service.router.RouterConfig对象
	private static ConcurrentMap routerContainers=new ConcurrentMap();//键：路由节点uuid  值j.service.router.RouterContainer对象
	private static ConcurrentMap agents=new ConcurrentMap();//键：路由节点uuid  值：j.service.router.RouterAgent对象
	
	private static ConcurrentList uuidOfRouters=new ConcurrentList();//路由节点的uuid列表
	private static ConcurrentList uuidOfRoutersAvailableHttp=new ConcurrentList();//可用路由节点的uuid列表
	private static ConcurrentList uuidOfRoutersAvailableRmi=new ConcurrentList();//可用路由节点的uuid列表
	private static long configLastModified=0;//配置文件上次修改时间
	private static volatile boolean loading=true;//是否正在加载配置文件
	
	static{
		RouterManager m=new RouterManager();
		Thread thread=new Thread(m);
		thread.start();
		log.log("RouterManager monitor thread started.",-1);
	}

	
	/**
	 * 
	 *
	 */
	public RouterManager(){
	}
	
	/**
	 * 
	 *
	 */
	public static void load(){
		try{		
			loading=true;
			
			List currentRouters=new LinkedList();
			
			//文件是否存在
			File file = new File(Properties.getConfigPath()+"service.router.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			
			Document document=JUtilDom4j.parse(Properties.getConfigPath()+"service.router.xml","UTF-8");
			Element root=document.getRootElement();
			
			List routerEles=root.elements("router");
			for(int i=0;i<routerEles.size();i++){
				Element routerEle=(Element)routerEles.get(i);
				
				RouterConfig config=new RouterConfig();
				config.setServerUuid(routerEle.attributeValue("server-uuid"));			
				config.setUuid(routerEle.elementText("uuid"));
				config.setName(routerEle.elementText("name"));
				config.setPrivacy(routerEle.elementText("privacy"));

				List routerProps=routerEle.elements("property");
				for(int j=0;j<routerProps.size();j++){
					Element pEle=(Element)routerProps.get(j);
					config.addConfig(pEle.attributeValue("key"),pEle.attributeValue("value"));
				}
				
				Element rmiEle=routerEle.element("rmi");
				if(rmiEle!=null&&"true".equalsIgnoreCase(rmiEle.attributeValue("available"))){
					Rmi rmi=new Rmi(JProperties.getProperties("rmi"));
					
					List props=rmiEle.elements("property");
					for(int j=0;j<props.size();j++){
						Element pEle=(Element)props.get(j);
						rmi.addConfig(pEle.attributeValue("key"),pEle.attributeValue("value"));
					}
					
					config.setRmi(rmi);
				}
				
				Element httpEle=routerEle.element("http");
				if(httpEle!=null&&"true".equalsIgnoreCase(httpEle.attributeValue("available"))){
					Http http=new Http();
					
					List props=httpEle.elements("property");
					for(int j=0;j<props.size();j++){
						Element pEle=(Element)props.get(j);
						http.addConfig(pEle.attributeValue("key"),pEle.attributeValue("value"));
					}
					
					config.setHttp(http);
				}


				List clients=root.elements("client");
				for(int j=0;j<clients.size();j++){
					Element clientEle=(Element)clients.get(j);
					
					Client client=new Client();
					client.setUuid(clientEle.attributeValue("uuid"));
					client.setName(clientEle.attributeValue("name"));
					client.setKey(clientEle.attributeValue("key"));

					config.addClient(client);
				}

				currentRouters.add(config);
			}
			
			root=null;
			document=null;
			
			
			//停止已启动服务
			List olds=routerConfigs.listValues();
			for(int i=0;i<olds.size();i++){
				RouterConfig config=(RouterConfig)olds.get(i);
				RouterContainer containter=(RouterContainer)routerContainers.get(config.getUuid());
				if(containter!=null&&containter.getStarted()){
					containter.shutdown();
				}
				
				RouterAgent agent=(RouterAgent)agents.get(config.getUuid());
				agent.shutdown();
			}
			JUtilList.clear_AllNull(olds);
			routerConfigs.clear();
			routerContainers.clear();
			agents.clear();
			uuidOfRouters.clear();
			uuidOfRoutersAvailableHttp.clear();
			//停止已启动服务 end
			
			
			//处理最新服务
			for(int i=0;i<currentRouters.size();i++){
				RouterConfig routerConfig=(RouterConfig)currentRouters.get(i);
				
				routerConfigs.put(routerConfig.getUuid(),routerConfig);
				uuidOfRouters.add(routerConfig.getUuid());
				
				if(routerConfig.getServerUuid().equals(Manager.getRouterNodeUuid())){//如果是路由节点，启动路由服务	
					NvwaObject nvwaObject=Nvwa.entrust(routerConfig.getRelatedHttpHandlerPath(),routerConfig.getClassName(),true);//托管至nvwa
					nvwaObject.setFiled("routerConfig","ref",true);
					
					JRouter router=(JRouter)Nvwa.entrustCreate(routerConfig.getRelatedHttpHandlerPath(),routerConfig.getClassName(),true);	
					router.setRouterConfig(routerConfig);

					
					if(routerConfig.getRmi()!=null) {
						log.log(routerConfig.getName()+" start router container, the rmi provider url is "+routerConfig.getRmi().getConfig("java.naming.provider.url"), -1);
					}
					
					if(routerConfig.getHttp()!=null) {
						log.log(routerConfig.getName()+" start router container, the http provider url is "+routerConfig.getHttp().getConfig("j.service.http"), -1);
					}
					
					RouterContainer container=new RouterContainer(routerConfig,router);
					routerContainers.put(routerConfig.getUuid(),container);
					container.startup();
				}
				
				//启动路由代理
				RouterAgent agent=new RouterAgent(routerConfig);
				agents.put(routerConfig.getUuid(),agent);
				agent.startup();				
			}
			currentRouters.clear();
			currentRouters=null;
			//处理最新服务 END
			

			//配置文件最近修改时间
			File configFile=new File(Properties.getConfigPath()+"service.router.xml");
			configLastModified=configFile.lastModified();
			configFile=null;
			
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
	
	/**
	 * 
	 * @param routerUuid
	 * @return
	 */
	public static RouterConfig getRouterConfig(String routerUuid){
		waitWhileLoading();
		return (RouterConfig)routerConfigs.get(routerUuid);
	}
	
	/**
	 * 
	 * @param routerUuid
	 * @return
	 */
	private static RouterAgent getAgent(String routerUuid){
		waitWhileLoading();
		return (RouterAgent)agents.get(routerUuid);
	}
	
	/**
	 * 
	 * @param routerUuid
	 * @param available
	 */
	public static void setAvailableHttp(String routerUuid,boolean available){
		waitWhileLoading();
		RouterConfig config=getRouterConfig(routerUuid);
		if(config==null) return;
		
		if(!available) uuidOfRoutersAvailableHttp.remove(routerUuid);
		else if(!uuidOfRoutersAvailableHttp.contains(routerUuid)) uuidOfRoutersAvailableHttp.add(routerUuid);
	}
	
	/**
	 * 
	 * @param routerUuid
	 * @param available
	 */
	public static void setAvailableRmi(String routerUuid,boolean available){
		waitWhileLoading();
		RouterConfig config=getRouterConfig(routerUuid);
		if(config==null) return;
		
		if(!available) uuidOfRoutersAvailableRmi.remove(routerUuid);
		else if(!uuidOfRoutersAvailableRmi.contains(routerUuid)) uuidOfRoutersAvailableRmi.add(routerUuid);
	}
	
	/**
	 * 
	 * @param routerUuid
	 * @return
	 */
	public boolean getAvailableHttp(String routerUuid){
		waitWhileLoading();
		return uuidOfRoutersAvailableHttp.contains(routerUuid);
	}
	
	/**
	 * 
	 * @param routerUuid
	 * @return
	 */
	public boolean getAvailableRmi(String routerUuid){
		waitWhileLoading();
		return uuidOfRoutersAvailableRmi.contains(routerUuid);
	}


	/**
	 * 
	 * @param clientUuid
	 * @param code
	 * @param uuid
	 * @param rmi
	 * @param http
	 * @param interfaceClassName
	 * @param md5
	 * @return
	 */
	public static String register(String clientUuid,String code, String uuid, String rmi, String http,String interfaceClassName,String md5){
		waitWhileLoading();
		//log.log("uuidOfRouters:"+uuidOfRouters.size(), -1);
		for(int i=0;i<uuidOfRouters.size();i++){
			getAgent((String)uuidOfRouters.get(i)).register(clientUuid,code,uuid,rmi,http,interfaceClassName,md5);
		}
		return Constants.INVOKING_ACCEPTED;
	}


	/**
	 * 
	 * @param clientUuid
	 * @param code
	 * @param uuid
	 * @param md5
	 * @return
	 */
	public static String unregister(String clientUuid,String code,String uuid,String md5){
		waitWhileLoading();
		for(int i=0;i<uuidOfRouters.size();i++){
			getAgent((String)uuidOfRouters.get(i)).unregister(clientUuid,code,uuid,md5);
		}
		return Constants.INVOKING_ACCEPTED;
	}

	
	/**
	 * 
	 * @param clientUuid
	 * @param code
	 * @param md54Routing
	 * @return
	 * @throws Exception
	 */
	public static ServiceBase serviceRmi(String clientUuid,String code,String md54Routing) throws Exception{
		waitWhileLoading();
		int routerAvails=uuidOfRoutersAvailableRmi.size();
		if(routerAvails==0){
			throw new RemoteException("no router(rmi) available.");
		}
		String choice=(String)uuidOfRoutersAvailableRmi.get(JUtilRandom.nextInt(routerAvails));
		return getAgent(choice).serviceRmi(clientUuid,code,md54Routing);
	}
	
	/**
	 * 
	 * @param jhttp
	 * @param client
	 * @param clientUuid
	 * @param code
	 * @param md54Routing
	 * @return
	 * @throws Exception
	 */
	public static String serviceHttp(JHttp jhttp,HttpClient client,String clientUuid,String code,String md54Routing) throws Exception{
		waitWhileLoading();
		int routerAvails=uuidOfRoutersAvailableHttp.size();
		if(routerAvails==0){
			throw new RemoteException("no router(http) available.");
		}
		String choice=(String)uuidOfRoutersAvailableHttp.get(JUtilRandom.nextInt(routerAvails));
		return getAgent(choice).serviceHttp(jhttp,client,clientUuid,code,md54Routing);
	}


	
	/**
	 * 
	 * @param clientUuid
	 * @param code
	 * @param md54Routing
	 * @return
	 * @throws Exception
	 */
	public static ServiceBase[] serviceNodesRmi(String clientUuid,String code,String md54Routing) throws Exception{
		waitWhileLoading();
		int routerAvails=uuidOfRoutersAvailableRmi.size();
		if(routerAvails==0){
			throw new RemoteException("no router(rmi) available.");
		}
		String choice=(String)uuidOfRoutersAvailableRmi.get(JUtilRandom.nextInt(routerAvails));
		return getAgent(choice).serviceNodesRmi(clientUuid,code,md54Routing);
	}
	
	/**
	 * 
	 * @param jhttp
	 * @param client
	 * @param clientUuid
	 * @param code
	 * @param md54Routing
	 * @return
	 * @throws Exception
	 */
	public static String[] serviceNodesHttp(JHttp jhttp,HttpClient client,String clientUuid,String code,String md54Routing) throws Exception{
		waitWhileLoading();
		int routerAvails=uuidOfRoutersAvailableHttp.size();
		if(routerAvails==0){
			throw new RemoteException("no router(http) available.");
		}
		String choice=(String)uuidOfRoutersAvailableHttp.get(JUtilRandom.nextInt(routerAvails));
		return getAgent(choice).serviceNodesHttp(jhttp,client,clientUuid,code,md54Routing);
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * 检测service.router.xml是否修改过，如修改过重新加载配置
		 */
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			if(configLastModified<=0) continue;
			
			File configFile=new File(Properties.getConfigPath()+"service.router.xml");
			if(configLastModified<configFile.lastModified()){
				log.log("service.router.xml has been modified, so reload it.",-1);
				load();
			}
			configFile=null;
		}
	}
}
