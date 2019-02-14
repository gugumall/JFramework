package j.service.router;

import j.app.webserver.JSession;
import j.common.JObject;
import j.http.JHttp;
import j.http.JHttpContext;
import j.log.Logger;
import j.service.Client;
import j.service.Constants;
import j.service.server.ServiceBase;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilMD5;
import j.util.JUtilRandom;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.HttpClient;

/**
 * 路由功能的实现类。
 * 接受服务注册、卸载请求，并监测服务状态。
 * 路由节点通过服务的集群编码将同一编码的服务视为一个集群，通过监测维护每个集群中可用服务的集合。
 * 当接受客户节点获取服务的请求时，路由节点会通过客户节点提供的服务编码（即有集群编码）从对应服务集群中获取一个服务实例（通过负载均衡机制）给客户节点。
 * @author 肖炯
 *
 */
public class JRouterImpl extends JRouterAbstract implements Runnable{	
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.create(JRouterImpl.class);
	private RouterConfig routerConfig=null;
	private boolean shutdown=false;//是否停止运行
	
	//键：服务集群编码  值：ConcurrentList，包含可用服务节点的信息，格式为String[]{uuid,rmi,http,interfaceClassName,rmiType,clusterCode}
	private ConcurrentMap serviceNodesOfClusters=null;
	
	private ConcurrentList clusterCodes=null;//所有服务集群（集群编码列表）
	
	private ConcurrentList nodeInfoList=null;
	
	//每个服务集群可用的服务节点对应的Remote对象
	//键：集群编码   值：ConcurrentMap（键：服务节点uuid，值：与服务节点对应的Remote对象）
	private ConcurrentMap servantsOfClusters=null;
	
	//每个服务集群可用的服务节点对应的http接口
	//键：集群编码   值：ConcurrentMap（键：服务节点uuid，值：与服务节点对应的http接口）
	private ConcurrentMap httpsOfClusters=null;
	
	private JHttp jhttp=null;
	private HttpClient jclient=null;
	
	private boolean monitoring=false;
	
	private long update=0;
	
	
	/**
	 * 
	 * @throws RemoteException
	 */
	public JRouterImpl() throws RemoteException {
		super();
		clusterCodes=new ConcurrentList();
		serviceNodesOfClusters=new ConcurrentMap();
		nodeInfoList=new ConcurrentList();
		servantsOfClusters=new ConcurrentMap();
		httpsOfClusters=new ConcurrentMap();
		jhttp=JHttp.getInstance();
		jclient=jhttp.createClient(3000);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.service.router.RouterInterface#setConfig(j.service.router.RouterConfig)
	 */
	public void setRouterConfig(RouterConfig routerConfig){
		this.routerConfig=routerConfig;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.service.router.JRouter#getRouterConfig()
	 */
	public RouterConfig getRouterConfig(){
		return this.routerConfig;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.service.router.RouterInterface#startup()
	 */
	public void startup() throws RemoteException{
		Thread thread=new Thread(this);
		thread.start();
		
		log.log("router "+routerConfig.getUuid()+","+routerConfig.getName()+","+routerConfig+" started.",-1);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.service.router.RouterInterface#shutdown()
	 */
	public void shutdown() throws RemoteException{
		this.shutdown=true;

		log.log("router "+routerConfig.getUuid()+","+routerConfig.getName()+","+routerConfig+" is to be shutdown.",-1);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.service.router.RouterInterface#register(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void register(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String clusterCode=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_SERVICE_CODE);
		String uuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_SERVICE_UUID);
		String rmi=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_RMI_CHANNEL);
		String http=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_HTTP_CHANNEL);
		String interfaceClassName=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_INTERFACE_CLASS);
		String md54Routing=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4ROUTER);
		
		try{
			jsession.resultString=register(clientUuid, clusterCode, uuid, rmi, http, interfaceClassName, md54Routing);
		}catch(Exception ex){
			log.log(ex,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.service.router.RouterInterface#unregister(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void unregister(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String clusterCode=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_SERVICE_CODE);
		String uuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_SERVICE_UUID);
		String md54Routing=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4ROUTER);
		
		try{
			jsession.resultString=unregister(clientUuid,clusterCode,uuid, md54Routing);
		}catch(Exception ex){
			log.log(ex,Logger.LEVEL_ERROR);
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.service.router.RouterInterface#service(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void service(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		waitWhileMonitoring();
		
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String clusterCode=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_SERVICE_CODE);
		String md54Routing=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4ROUTER);
		
		if(Constants.PRIVICY_PUBLIC.equalsIgnoreCase(routerConfig.getPrivacy())){
			//is public, nothing to do
		}else if(Constants.PRIVICY_MD5.equalsIgnoreCase(routerConfig.getPrivacy())){
			Client client=routerConfig.getClient(clientUuid);
			if(client==null){
				log.log("client "+clientUuid+" is not exists.",Logger.LEVEL_ERROR);
				jsession.resultString=Constants.AUTH_FAILED;
				return;
			}
		
			String md5="";
			md5+=clientUuid;
			md5+=client.getKey();
			md5=JUtilMD5.MD5EncodeToHex(md5);
			if(!md5.equalsIgnoreCase(md54Routing)){
				jsession.resultString=Constants.AUTH_FAILED;
				return;
			}
		}else {//未实现的隐私策略
			jsession.resultString=Constants.AUTH_FAILED;
			return;
		}
		
		String codeOfUuid=null;
		for(int i=0;i<nodeInfoList.size();i++){//按uuid获得
			String[] node=((String)nodeInfoList.get(i)).split(",");
			if(node[0].equals(clusterCode)){
				codeOfUuid=node[4];
				break;
			}
		}

		ConcurrentMap httpsOfCluster=(ConcurrentMap)httpsOfClusters.get(codeOfUuid==null?clusterCode:codeOfUuid);
		if(httpsOfCluster==null){
			log.log("the service(http channel) "+clusterCode +" is not found.",Logger.LEVEL_WARNING);
			jsession.resultString=Constants.SERVICE_NOT_FOUND;
			return;
		}
		
		List nodes=httpsOfCluster.listValues();
		if(nodes.size()==0){
			log.log("no valid node(http channel) for the service "+clusterCode +".",Logger.LEVEL_WARNING);
			jsession.resultString=Constants.SERVICE_NOT_AVAIL;
			return;
		}
		
		if(httpsOfCluster.containsKey(clusterCode)){//按uuid获得
			nodes.clear();
			nodes=null;

			jsession.resultString=((Service)httpsOfCluster.get(clusterCode)).http;
			return;
		}else{			
			String entrance=((Service)nodes.get(JUtilRandom.nextInt(nodes.size()))).http;
			nodes.clear();
			nodes=null;
			
			jsession.resultString=entrance;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.service.router.JRouter#getAllServiceNode(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void getAllServiceNodeAvailable(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		waitWhileMonitoring();
		
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String clusterCode=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_SERVICE_CODE);
		String md54Routing=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4ROUTER);
		
		if(Constants.PRIVICY_PUBLIC.equalsIgnoreCase(routerConfig.getPrivacy())){
			//is public, nothing to do
		}else if(Constants.PRIVICY_MD5.equalsIgnoreCase(routerConfig.getPrivacy())){
			Client client=routerConfig.getClient(clientUuid);
			if(client==null){
				log.log("client "+clientUuid+" is not exists.",Logger.LEVEL_ERROR);
				jsession.resultString=Constants.AUTH_FAILED;
				return;
			}
		
			String md5="";
			md5+=clientUuid;
			md5+=client.getKey();
			md5=JUtilMD5.MD5EncodeToHex(md5);
			if(!md5.equalsIgnoreCase(md54Routing)){
				jsession.resultString=Constants.AUTH_FAILED;
				return;
			}
		}else {//未实现的隐私策略
			jsession.resultString=Constants.AUTH_FAILED;
			return;
		}

		ConcurrentMap httpsOfCluster=(ConcurrentMap)httpsOfClusters.get(clusterCode);
		if(httpsOfCluster==null){
			log.log("the service(http channel) "+clusterCode +" is not found.",Logger.LEVEL_WARNING);
			jsession.resultString=Constants.SERVICE_NOT_FOUND;
			return;
		}
		
		List nodes=httpsOfCluster.listValues();
		if(nodes.size()==0){
			log.log("no valid node(http channel) for the service "+clusterCode +".",Logger.LEVEL_WARNING);
			jsession.resultString=Constants.SERVICE_NOT_AVAIL;
			return;
		}
		
		String[] entrances=new String[nodes.size()];
		for(int i=0;i<nodes.size();i++){
			Service service=(Service)nodes.get(i);
			entrances[i]=service.http;
		}
		nodes.clear();
		nodes=null;
		
		try{
			jsession.resultString=JObject.serializable2String(entrances,false);
		}catch(Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			jsession.resultString="ERR";
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.service.router.RouterInterface#register(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String register(String clientUuid, String clusterCode, String uuid, String rmi, String http, String interfaceClassName, String md54Routing) throws RemoteException {
		if(Constants.PRIVICY_PUBLIC.equalsIgnoreCase(routerConfig.getPrivacy())){
			//is public, nothing to do
		}else if(Constants.PRIVICY_MD5.equalsIgnoreCase(routerConfig.getPrivacy())){
			Client client=routerConfig.getClient(clientUuid);
			if(client==null){
				log.log("client "+clientUuid+" is not exists.",Logger.LEVEL_DEBUG);
				return Constants.AUTH_FAILED;
			}
			
			String md5="";
			md5+=clientUuid;
			md5+=clusterCode;
			md5+=uuid;
			md5+=rmi;
			md5+=http;
			md5+=interfaceClassName;
			md5+=client.getKey();
			md5=JUtilMD5.MD5EncodeToHex(md5);
			
			if(!md5.equalsIgnoreCase(md54Routing)){
				return Constants.AUTH_FAILED;
			}
		}else{//未实现的隐私策略
			return Constants.AUTH_FAILED;
		}
		
		ConcurrentList clusterNodes=(ConcurrentList)serviceNodesOfClusters.get(clusterCode);
		if(clusterNodes==null){
			clusterNodes=new ConcurrentList();			
		}
		
		Service node=new Service(uuid,rmi,http,interfaceClassName,clusterCode);
		if(!nodeInfoList.contains(node.toString())){
			log.log("register new service - "+node.toString(),-1);
			nodeInfoList.add(node.toString());
			
			clusterNodes.add(node);
			
			update=SysUtil.getNow();
		}
		serviceNodesOfClusters.put(clusterCode,clusterNodes);
		
		if(!clusterCodes.contains(clusterCode)) clusterCodes.add(clusterCode);
				
		return Constants.INVOKING_DONE;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.service.router.RouterInterface#unregister(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String unregister(String clientUuid,String clusterCode,String uuid, String md54Routing) throws RemoteException {
		if(Constants.PRIVICY_PUBLIC.equalsIgnoreCase(routerConfig.getPrivacy())){
			//is public, nothing to do
		}else if(Constants.PRIVICY_MD5.equalsIgnoreCase(routerConfig.getPrivacy())){
			Client client=routerConfig.getClient(clientUuid);
			if(client==null){
				log.log("client "+clientUuid+" is not exists.",Logger.LEVEL_DEBUG);
				return Constants.AUTH_FAILED;
			}
			
			String md5="";
			md5+=clientUuid;
			md5+=clusterCode;
			md5+=uuid;
			md5+=client.getKey();
			md5=JUtilMD5.MD5EncodeToHex(md5);
			
			if(!md5.equalsIgnoreCase(md54Routing)){
				return Constants.AUTH_FAILED;
			}
		}else{//未实现的隐私策略
			return Constants.AUTH_FAILED;
		}
		
		ConcurrentList clusterNodes=(ConcurrentList)serviceNodesOfClusters.get(clusterCode);
		ConcurrentMap servantsOfCluster=(ConcurrentMap)servantsOfClusters.get(clusterCode);
		ConcurrentMap httpsOfCluster=(ConcurrentMap)httpsOfClusters.get(clusterCode);
		if(clusterNodes!=null){
			for(int i=0;i<clusterNodes.size();i++){
				Service node=(Service)clusterNodes.get(i);
				if(node.uuid.equals(uuid)){
					if(nodeInfoList.contains(node.toString())) nodeInfoList.remove(node.toString());
					
					clusterNodes.remove(i);
					
					if(servantsOfCluster!=null) servantsOfCluster.remove(node.uuid);
					if(httpsOfCluster!=null) httpsOfCluster.remove(node.uuid);
					
					update=SysUtil.getNow();
					
					break;
				}
			}
		}
		
		if(clusterNodes==null||clusterNodes.isEmpty()){
			serviceNodesOfClusters.remove(clusterCode);
			servantsOfClusters.remove(clusterCode);
			httpsOfClusters.remove(clusterCode);
			clusterCodes.remove(clusterCode);
		}
		
		return Constants.INVOKING_DONE;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.service.router.JRouter#service(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ServiceBase service(String clientUuid, String clusterCode, String md54Routing) throws RemoteException {
		waitWhileMonitoring();
		if(Constants.PRIVICY_PUBLIC.equalsIgnoreCase(routerConfig.getPrivacy())){
			//is public, nothing to do
		}else if(Constants.PRIVICY_MD5.equalsIgnoreCase(routerConfig.getPrivacy())){
			Client client=routerConfig.getClient(clientUuid);
			if(client==null){
				log.log("client "+clientUuid+" is not exists.",Logger.LEVEL_DEBUG);
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
			String md5="";
			md5+=clientUuid;
			md5+=client.getKey();
			md5=JUtilMD5.MD5EncodeToHex(md5);
			
			if(!md5.equalsIgnoreCase(md54Routing)){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
		}else{//未实现的隐私策略
			throw new RemoteException(Constants.AUTH_FAILED);
		}		
		
		String codeOfUuid=null;
		for(int i=0;i<nodeInfoList.size();i++){//按uuid获得
			String[] node=((String)nodeInfoList.get(i)).split(",");
			if(node[0].equals(clusterCode)){
				codeOfUuid=node[4];
				break;
			}
		}

		ConcurrentMap servantsOfCluster=(ConcurrentMap)servantsOfClusters.get(codeOfUuid==null?clusterCode:codeOfUuid);
		if(servantsOfCluster==null){
			throw new RemoteException("the service(rmi) "+clusterCode +" is not found.");
		}

		List nodes=servantsOfCluster.listValues();
		if(nodes.size()==0){
			throw new RemoteException("no valid node for the service(rmi) "+clusterCode +".");
		}
		
		if(servantsOfCluster.containsKey(clusterCode)){//按uuid获得
			nodes.clear();
			nodes=null;
			return (ServiceBase)servantsOfCluster.get(clusterCode);
		}else{			
			ServiceBase servant=(ServiceBase)nodes.get(JUtilRandom.nextInt(nodes.size()));
			nodes.clear();
			nodes=null;

			return servant;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.service.router.JRouter#getAllServiceNode(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ServiceBase[] getAllServiceNodeAvailable(String clientUuid, String clusterCode, String md54Routing) throws RemoteException {
		waitWhileMonitoring();
		if(Constants.PRIVICY_PUBLIC.equalsIgnoreCase(routerConfig.getPrivacy())){
			//is public, nothing to do
		}else if(Constants.PRIVICY_MD5.equalsIgnoreCase(routerConfig.getPrivacy())){
			Client client=routerConfig.getClient(clientUuid);
			if(client==null){
				log.log("client "+clientUuid+" is not exists.",Logger.LEVEL_DEBUG);
				throw new RemoteException(Constants.AUTH_FAILED);
			}
			
			String md5="";
			md5+=clientUuid;
			md5+=client.getKey();
			md5=JUtilMD5.MD5EncodeToHex(md5);
			
			if(!md5.equalsIgnoreCase(md54Routing)){
				throw new RemoteException(Constants.AUTH_FAILED);
			}
		}else{//未实现的隐私策略
			throw new RemoteException(Constants.AUTH_FAILED);
		}		

		ConcurrentMap servantsOfCluster=(ConcurrentMap)servantsOfClusters.get(clusterCode);
		if(servantsOfCluster==null){
			throw new RemoteException("the service(rmi) "+clusterCode +" is not found.");
		}

		List nodes=servantsOfCluster.listValues();
		if(nodes.size()==0){
			throw new RemoteException("no valid node for the service(rmi) "+clusterCode +".");
		}
		
		return (ServiceBase[])nodes.toArray(new ServiceBase[nodes.size()]);
	}	
	
	/*
	 *  (non-Javadoc)
	 * @see j.service.router.RouterInterface#heartbeat()
	 */
	public String heartbeat() throws RemoteException {
		if(routerConfig==null||!routerConfig.getStarted()) return Constants.STATUS_OFF;
		else return Constants.STATUS_OK+":"+update;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.service.server.ServiceBase#heartbeat(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void heartbeat(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		if(routerConfig==null||!routerConfig.getStarted()) jsession.resultString=Constants.STATUS_OFF;
		else jsession.resultString=Constants.STATUS_OK+":"+update;;
	}
	
	/**
	 * 监测每个服务集群
	 * @param clusterCode 服务集群编码
	 */
	private void monitor(String clusterCode){			
		ConcurrentList clusterNodes=(ConcurrentList)serviceNodesOfClusters.get(clusterCode);
		if(clusterNodes==null||clusterNodes.size()==0){//无服务节点
			log.log("no node for service "+clusterCode,Logger.LEVEL_DEBUG);
			return;
		}
		
		for(int i=0;i<clusterNodes.size();i++){
			Service node=(Service)clusterNodes.get(i);
			monitor(node);
		}
	}
	
	/**
	 * 
	 * @param node
	 */
	private void monitor(Service node){
		try{
			monitoring=true;
			
			ConcurrentMap servantsOfCluster=(ConcurrentMap)servantsOfClusters.get(node.clusterCode);
			ConcurrentMap httpsOfCluster=(ConcurrentMap)httpsOfClusters.get(node.clusterCode);
			if(servantsOfCluster==null){
				servantsOfCluster=new ConcurrentMap();
				servantsOfClusters.put(node.clusterCode,servantsOfCluster);
			}
			if(httpsOfCluster==null){
				httpsOfCluster=new ConcurrentMap();
				httpsOfClusters.put(node.clusterCode,httpsOfCluster);
			}
			
			boolean serviceRmiAvailable=true;	
			if(node.rmi!=null&&!"".equals(node.rmi)){
				Context context= null;
				try {
					Properties props=null;
					props=j.Properties.getProperties("rmi");
					props.put("java.naming.provider.url",node.rmi);
				
					context = new InitialContext(props);
				} catch (Exception e) {		
					serviceRmiAvailable=false;
					context=null;
					//log.log(e,Logger.LEVEL_DEBUG);
				}
				
				ServiceBase servant=(ServiceBase)servantsOfCluster.get(node.uuid);
				if(serviceRmiAvailable){
					try {
						servant=(ServiceBase)context.lookup(node.uuid);					
						servantsOfCluster.put(node.uuid,servant);
						
						if(!Constants.STATUS_OK.equals(servant.heartbeat())){
							throw new Exception("心跳不正常(rmi)");
						}
					} catch (Exception e) {
						servantsOfCluster.remove(node.uuid);	
						serviceRmiAvailable=false;
						servant=null;
						//log.log(e,Logger.LEVEL_DEBUG);
					}
				}else{
					servantsOfCluster.remove(node.uuid);	
					serviceRmiAvailable=false;
					servant=null;
				}
			}else{
				servantsOfCluster.remove(node.uuid);	
				serviceRmiAvailable=false;
			}
	
			if(!serviceRmiAvailable){
				log.log("service(rmi) "+node.clusterCode+","+node.uuid+" is unavailable.",Logger.LEVEL_DEBUG);
			}else{
				log.log("service(rmi) "+node.clusterCode+","+node.uuid+" is available("+servantsOfCluster.size()+" nodes).",Logger.LEVEL_DEBUG);
			}
			
	
			boolean serviceHttpAvailable=true;	
			if(node.http!=null&&!"".equals(node.http)){
				try {
					String url=node.http;
					if(url.indexOf("?")>0) url+="&request=heartbeat";
					else url+="?request=heartbeat";
													
					JHttpContext context=jhttp.get(null,jclient,url,SysConfig.sysEncoding);
					String result=context.getResponseText();
					context.finalize();
					context=null;
					
					if(!Constants.STATUS_OK.equals(result)){
						throw new Exception("心跳不正常(http)");
					}else{
						httpsOfCluster.put(node.uuid,node);
					}
				} catch (Exception e) {
					httpsOfCluster.remove(node.uuid);	
					serviceHttpAvailable=false;
					//log.log(e,Logger.LEVEL_DEBUG);
				}
			}else{
				httpsOfCluster.remove(node.uuid);	
				serviceHttpAvailable=false;
			}
	
			if(!serviceHttpAvailable){
				log.log("service(http) "+node.clusterCode+","+node.uuid+" is unavailable.",Logger.LEVEL_DEBUG);
			}else{
				log.log("service(http) "+node.clusterCode+","+node.uuid+" is available("+httpsOfCluster.size()+" nodes).",Logger.LEVEL_DEBUG);
			} 
			
			monitoring=false;
		}catch(Exception e){
			monitoring=false;
			log.log(e,Logger.LEVEL_ERROR);
		}
	}
	
	
	/**
	 * 
	 *
	 */
	private void waitWhileMonitoring(){
		while(monitoring){
			try{
				Thread.sleep(100);
			}catch(Exception e){}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(!this.shutdown){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			for(int i=0;i<clusterCodes.size();i++){
				monitor((String)clusterCodes.get(i));
			}
		}
	}
}
