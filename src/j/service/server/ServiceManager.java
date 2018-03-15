package j.service.server;

import j.Properties;
import j.log.Logger;
import j.service.Client;
import j.service.Http;
import j.service.Manager;
import j.service.Rmi;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilList;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 加载服务信息，并启动服务，将服务注册到路由器
 * @author JFramework
 *
 */
public class ServiceManager implements Runnable {
	private static Logger log=Logger.create(ServiceManager.class);
	
	/*
	 * 键: 服务uuid  值: j.service.server.ServiceConfig
	 */
	private static ConcurrentMap services=new ConcurrentMap();
	
	/*
	 * 键: 服务code 值: ConcurrentList[ServiceConfig]
	 * 用于得到同一服务集群的所有节点（当各节点间需要协作、同步等时）
	 */
	private static ConcurrentMap servicesOfClusters=new ConcurrentMap();
	
	/*
	 * 键: 服务uuid或服务集群编码  值: j.service.server.ServiceContainer
	 */
	private static ConcurrentMap serviceContainers=new ConcurrentMap();
	
	private static long configLastModified=0;//配置文件上次修改时间
	private static volatile boolean loading=true;//是否正在加载配置文件
	
	static{
		ServiceManager m=new ServiceManager();
		Thread thread=new Thread(m);
		thread.start();
		log.log("ServiceManager monitor thread started.",-1);
	}
	
	/**
	 * 
	 *
	 */
	public ServiceManager(){
	}
	
	/**
	 * 
	 * @param codeOrUuid
	 * @return
	 */
	public static ServiceContainer getServiceContainer(String codeOrUuid){
		waitWhileLoading();
		return (ServiceContainer)serviceContainers.get(codeOrUuid);
	}
	
	/**
	 * 
	 * @param code
	 * @return
	 */
	public static ServiceConfig[] getServices(String code){
		return getServices(code,false);
	}
	
	/**
	 * 
	 * @param code
	 * @param initializing
	 * @return
	 */
	public static ServiceConfig[] getServices(String code,boolean initializing){
		if(!initializing) waitWhileLoading();
		ConcurrentList ls=(ConcurrentList)servicesOfClusters.get(code);
		if(ls==null) return null;
		else{
			ServiceConfig[] array=new ServiceConfig[ls.size()];
			ls.toArray(array);
			
			return array;
		}
	}
	
	/**
	 * 初始化
	 *
	 */
	public static void load(){
		try{
			loading=true;
			
			servicesOfClusters.clear();
			
			List currentServices=new LinkedList();
			
			//文件是否存在
			File file = new File(Properties.getConfigPath()+"service.server.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			
			Document document=JUtilDom4j.parse(Properties.getConfigPath()+"service.server.xml","UTF-8");
			Element root=document.getRootElement();

			List servs=root.elements("service");
			for(int i=0;i<servs.size();i++){
				Element serv=(Element)servs.get(i);
				List nodes=serv.elements("node");
				for(int n=0;n<nodes.size();n++){
					Element nodeE=(Element)nodes.get(n);
					
					ServiceConfig service=new ServiceConfig();
					String _toString="";
					
					//公共信息
					service.setCode(serv.elementText("code"));
					service.setName(serv.elementText("name"));
					service.setPrivacy(serv.elementText("privacy"));
					
					_toString+=serv.elementText("code");
					_toString+=" - "+serv.elementText("name");
					_toString+=" - "+serv.elementText("privacy");
					
					List serviceProps=serv.elements("property");
					for(int j=0;j<serviceProps.size();j++){
						Element pEle=(Element)serviceProps.get(j);
						service.addConfig(pEle.attributeValue("key"),pEle.attributeValue("value"));

						_toString+=" - {"+pEle.attributeValue("key")+"="+pEle.attributeValue("value")+"}";
					}
					
					List methods=serv.elements("method");
					for(int j=0;j<methods.size();j++){
						Element methodEle=(Element)methods.get(j);
						
						Method method=new Method();
						method.setName(methodEle.elementText("name"));
						method.setPrivacy(methodEle.elementText("privacy"));

						_toString+=" - ["+methodEle.elementText("name")+";"+methodEle.elementText("privacy")+"]";
						
						service.addMethod(method);
					}
					
					List clients=serv.elements("client");
					for(int j=0;j<clients.size();j++){
						Element clientEle=(Element)clients.get(j);
						
						Client client=new Client();
						client.setUuid(clientEle.attributeValue("uuid"));
						client.setName(clientEle.attributeValue("name"));
						client.setKey(clientEle.attributeValue("key"));

						_toString+=" - <"+clientEle.attributeValue("uuid")+";"+clientEle.attributeValue("name")+";"+clientEle.attributeValue("key")+">";

						service.addClient(client);
					}
					//公共信息 END
					
					
					//节点信息
					service.setServerUuid(nodeE.attributeValue("server-uuid"));

					_toString+=" - "+nodeE.attributeValue("server-uuid");
					
					service.setUuid(nodeE.elementText("uuid"));

					_toString+=" - "+nodeE.elementText("uuid");
					
					Element rmiEle=nodeE.element("rmi");
					if(rmiEle!=null&&"true".equalsIgnoreCase(rmiEle.attributeValue("available"))){
						Rmi rmi=new Rmi(Properties.getProperties("rmi"));
						
						List props=rmiEle.elements("property");
						for(int j=0;j<props.size();j++){
							Element pEle=(Element)props.get(j);
							rmi.addConfig(pEle.attributeValue("key"),pEle.attributeValue("value"));
							
							_toString+=" - <"+pEle.attributeValue("key")+"="+pEle.attributeValue("value")+">";
						}
						
						service.setRmi(rmi);
					}
					
					Element httpEle=nodeE.element("http");
					if(httpEle!=null&&"true".equalsIgnoreCase(httpEle.attributeValue("available"))){
						Http http=new Http();
						
						List props=httpEle.elements("property");
						for(int j=0;j<props.size();j++){
							Element pEle=(Element)props.get(j);
							http.addConfig(pEle.attributeValue("key"),pEle.attributeValue("value"));
							
							_toString+=" - <"+pEle.attributeValue("key")+"="+pEle.attributeValue("value")+">";
						}
						
						service.setHttp(http);
					}
					
					service.setToString(_toString);
					//节点信息 END		
					
					currentServices.add(service);	
					
					//分组保存
					ConcurrentList ls=(ConcurrentList)servicesOfClusters.get(service.getCode());
					if(ls==null){
						ls=new ConcurrentList();
						servicesOfClusters.put(service.getCode(),ls);
					}
					ls.add(service);
				}
			}
			root=null;
			document=null;
			
			
			//停止已启动服务
			List olds=services.listValues();
			for(int i=0;i<olds.size();i++){
				ServiceConfig service=(ServiceConfig)olds.get(i);
				
				ServiceContainer containter=(ServiceContainer)serviceContainers.get(service.getUuid());
				if(containter!=null&&containter.getStarted()){					
					ServiceConfig _new=null;
					for(int j=0;j<currentServices.size();j++){
						ServiceConfig s=(ServiceConfig)currentServices.get(j);
						if(s.getUuid().equals(service.getUuid())){
							_new=s;
							break;
						}
					}
					
					if(_new!=null&&_new.equals(service)){
						log.log("重启服务（停止阶段）- "+service.getCode()+" - "+service.getName()+" 没有变动，不需要重启",-1);
					}else{
						log.log("重启服务（停止阶段）- "+service.getCode()+" - "+service.getName()+" 有变动，需要重启，正尝试停止",-1);
						
						containter.shutdown();
					}
				}
			}
			services.clear();
			serviceContainers.clear();
			//停止已启动服务 end
			
			
			//处理最新服务
			for(int i=0;i<currentServices.size();i++){
				ServiceConfig serviceConfig=(ServiceConfig)currentServices.get(i);
				
				services.put(serviceConfig.getUuid(),serviceConfig);//保存服务，可通过uuid找到
				
				if(serviceConfig.getServerUuid().equals(Manager.getServerNodeUuid())){//本地服务才启动					
					ServiceConfig _old=null;
					for(int j=0;j<olds.size();j++){
						ServiceConfig s=(ServiceConfig)olds.get(j);
						if(s.getUuid().equals(serviceConfig.getUuid())){
							_old=s;
							break;
						}
					}
					
					if(_old!=null&&_old.equals(serviceConfig)){
						log.log("重启服务（启动阶段） - "+serviceConfig.getCode()+" - "+serviceConfig.getName()+" 没有变动，不需要重启",-1);
					}else{						
						if(_old!=null){
							log.log("重启服务（启动阶段）- "+serviceConfig.getCode()+" - "+serviceConfig.getName()+" 有变动，需要重启，正尝试启动",-1);
						}else{
							log.log("启动服务 - "+serviceConfig.getCode()+" - "+serviceConfig.getName()+"，正尝试启动",-1);
						}
						
						//启动服务
						ServiceContainer container=new ServiceContainer(serviceConfig);
						serviceContainers.put(serviceConfig.getUuid(),container);
						serviceContainers.put(serviceConfig.getCode(),container);
						container.startup();
					}
				}
			}
			currentServices.clear();
			currentServices=null;
			JUtilList.clear_AllNull(olds);
			//处理最新服务 END
			

			//配置文件最近修改时间
			File configFile=new File(Properties.getConfigPath()+"service.server.xml");
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

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * 检测service.server.xml是否修改过，如修改过重新加载配置
		 */
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			if(configLastModified<=0) continue;

			File configFile=new File(Properties.getConfigPath()+"service.server.xml");
			if(configLastModified<configFile.lastModified()){
				log.log("service.server.xml has been modified, so reload it.",-1);
				load();
			}
			configFile=null;
		}
	}
}
