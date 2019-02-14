package j.service.router;

import j.common.JObject;
import j.http.JHttp;
import j.http.JHttpContext;
import j.log.Logger;
import j.service.Constants;
import j.service.server.ServiceBase;
import j.sys.SysConfig;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilString;

import java.rmi.RemoteException;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.http.client.HttpClient;

/**
 * 运行于服务节点（用于向对应路由节点注册和卸载服务）
 * 运行于路由节点（用于监视对应路由节点状态）
 * 运行于客户节点（用于监视对应路由节点状态已确定可用路由节点集合，并负责从相关节点获取服务入口）
 * @author 肖炯
 *
 */
public class RouterAgent implements Runnable {
	private static Logger log=Logger.create(RouterAgent.class);
	
	//接收服务节点提交的注册、卸载请求，并通过任务线程（run方法）异步向关联路由节点提交请求
	//因为关联路由节点有时不可用，但注册、卸载请求必须执行成功，所以采取异步方式，不断尝试向关联路由节点提交请求
	private ConcurrentList tasks=new ConcurrentList();

	private RouterConfig routerConfig=null;
	private JRouter servant=null;//代表关联的路由节点的远程对象
	private Context initialNamingContext=null;
	private boolean routerRmiAvailable=true;
	private boolean routerHttpAvailable=true;
	private boolean shutdown=false;
	
	private JHttp jhttp=null;
	private HttpClient jclient=null;
	
	private long update=0;
	
	private ConcurrentMap cache=new ConcurrentMap();
	
	/**
	 * 
	 * @param routerConfig
	 */
	protected RouterAgent(RouterConfig routerConfig) {
		super();
		this.routerConfig=routerConfig;
		jhttp=JHttp.getInstance();
		jclient=jhttp.createClient();
	}
	
	
	/**
	 * 
	 * @param router
	 * @throws RemoteException 
	 */
	protected void startup() throws RemoteException{
		Thread thread=new Thread(this);
		thread.start();
		
		log.log("monitor for service "+routerConfig.getUuid()+","+routerConfig.getName()+" started.",Logger.LEVEL_DEBUG);
	}
	
	/**
	 * 
	 * 
	 */
	protected void shutdown(){
		this.shutdown=true;
	}
	

	/**
	 * 接受服务节点提交的注册任务
	 * @param clientUuid
	 * @param code
	 * @param uuid
	 * @param rmi
	 * @param http
	 * @param interfaceClassName
	 * @param md5
	 * @return
	 */
	public String register(String clientUuid,String code, String uuid, String rmi, String http, String interfaceClassName, String md5) {
		if(!routerHttpAvailable&&!routerRmiAvailable){
			return Constants.INVOKING_FAILED;
		}
		tasks.add(new String[]{"register",clientUuid,code,uuid,rmi,http,interfaceClassName,md5});
		return Constants.INVOKING_ACCEPTED;
	}


	/**
	 * 接受服务节点提交的卸载任务
	 * @param clientUuid
	 * @param code
	 * @param uuid
	 * @param md5
	 * @return
	 */
	public String unregister(String clientUuid,String code,String uuid,String md5){
		if(!routerHttpAvailable&&!routerRmiAvailable){
			return Constants.INVOKING_FAILED;
		}
		tasks.add(new String[]{"unregister",clientUuid,code,uuid,md5});
		return Constants.INVOKING_ACCEPTED;
	}

	
	/**
	 * RouterManager通过均衡策略选择本路由节点代理，调用此方法从关联路由节点获得服务入口
	 * @param clientUuid
	 * @param code
	 * @param md54Routing
	 * @return
	 * @throws Exception
	 */
	protected ServiceBase serviceRmi(String clientUuid,String code,String md54Routing) throws Exception{
		String cacheKey="rmi|"+clientUuid+"|"+code;
		if(cache.containsKey(cacheKey)){
			return (ServiceBase)cache.get(cacheKey);
		}
		
		if(this.routerConfig.getRmi()==null){
			throw new Exception("router(rmi) is not supported.");
		}
		if(!routerRmiAvailable||servant==null){
			throw new Exception("router(rmi) "+routerConfig.getUuid()+" is unavailable.");
		}
		ServiceBase obj=servant.service(clientUuid,code,md54Routing);
		cache.put(cacheKey,obj);
		
		return obj;
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
	protected String serviceHttp(JHttp jhttp,HttpClient client,String clientUuid,String code,String md54Routing) throws Exception{
		String cacheKey="http|"+clientUuid+"|"+code;
		if(cache.containsKey(cacheKey)){
			return (String)cache.get(cacheKey);
		}
		
		if(this.routerConfig.getHttp()==null){
			throw new Exception("router(http) is not supported.");
		}
		if(!routerHttpAvailable){
			throw new Exception("router(http) "+routerConfig.getUuid()+" is unavailable.");
		}
		
		String url=this.routerConfig.getHttp().getEntrance();
		if(url.indexOf("?")>0) url+="&request=service";
		else url+="?request=service";		
		url+="&"+Constants.JSERVICE_PARAM_CLIENT_UUID+"="+clientUuid;
		url+="&"+Constants.JSERVICE_PARAM_SERVICE_CODE+"="+code;
		url+="&"+Constants.JSERVICE_PARAM_MD5_STRING_4ROUTER+"="+md54Routing;
	
		if(jhttp==null) jhttp=JHttp.getInstance();
		JHttpContext context=null;
		try{
			context=jhttp.get(null,client,url);
		}catch(Exception e){
			throw e;
		}
		
		if(context!=null&&context.getStatus()!=200){
			throw new Exception("failed to get http channel, the http response code is - "+context.getStatus());
		}

		String response=context.getResponseText();
		context.finalize();
		context=null;
		
//		if(Constants.AUTH_FAILED.equals(response)){
//			throw new Exception("failed to get http channel due to - "+Constants.AUTH_FAILED);
//		}else if(Constants.SERVICE_NOT_FOUND.equals(response)){
//			throw new Exception("failed to get http channel due to - "+Constants.SERVICE_NOT_FOUND);
//		}else if(Constants.SERVICE_NOT_AVAIL.equals(response)){
//			throw new Exception("failed to get http channel due to - "+Constants.SERVICE_NOT_AVAIL);
//		}
		
		cache.put(cacheKey,response);
		return response;
	}

	
	/**
	 * RouterManager通过均衡策略选择本路由节点代理，调用此方法从关联路由节点获得服务入口
	 * @param clientUuid
	 * @param code
	 * @param md54Routing
	 * @return
	 * @throws Exception
	 */
	protected ServiceBase[] serviceNodesRmi(String clientUuid,String code,String md54Routing) throws Exception{
		String cacheKey="rmi|"+clientUuid+"|"+code+"|nodes";
		if(cache.containsKey(cacheKey)){
			return (ServiceBase[])cache.get(cacheKey);
		}
		
		if(this.routerConfig.getRmi()==null){
			throw new Exception("router(rmi) is not supported.");
		}
		if(!routerRmiAvailable||servant==null){
			throw new Exception("router(rmi) "+routerConfig.getUuid()+" is unavailable.");
		}
		ServiceBase[] obj=servant.getAllServiceNodeAvailable(clientUuid,code,md54Routing);
		cache.put(cacheKey,obj);
		
		return obj;
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
	protected String[] serviceNodesHttp(JHttp jhttp,HttpClient client,String clientUuid,String code,String md54Routing) throws Exception{
		String cacheKey="http|"+clientUuid+"|"+code+"|nodes";
		if(cache.containsKey(cacheKey)){
			return (String[])cache.get(cacheKey);
		}
		
		if(this.routerConfig.getHttp()==null){
			throw new Exception("router(http) is not supported.");
		}
		if(!routerHttpAvailable){
			throw new Exception("router(http) "+routerConfig.getUuid()+" is unavailable.");
		}
		
		String url=this.routerConfig.getHttp().getEntrance();
		if(url.indexOf("?")>0) url+="&request=getAllServiceNodeAvailable";
		else url+="?request=getAllServiceNodeAvailable";		
		url+="&"+Constants.JSERVICE_PARAM_CLIENT_UUID+"="+clientUuid;
		url+="&"+Constants.JSERVICE_PARAM_SERVICE_CODE+"="+code;
		url+="&"+Constants.JSERVICE_PARAM_MD5_STRING_4ROUTER+"="+md54Routing;
	
		if(jhttp==null) jhttp=JHttp.getInstance();
		JHttpContext context=null;
		try{
			context=jhttp.get(null,client,url);
		}catch(Exception e){
			throw e;
		}
		
		if(context!=null&&context.getStatus()!=200){
			throw new Exception("failed to get http channel, the http response code is - "+context.getStatus());
		}

		String response=context.getResponseText();
		context.finalize();
		context=null;
		
		if(Constants.AUTH_FAILED.equals(response)){
			throw new Exception("failed to get http channel due to - "+Constants.AUTH_FAILED);
		}else if(Constants.SERVICE_NOT_FOUND.equals(response)){
			throw new Exception("failed to get http channel due to - "+Constants.SERVICE_NOT_FOUND);
		}else if(Constants.SERVICE_NOT_AVAIL.equals(response)){
			throw new Exception("failed to get http channel due to - "+Constants.SERVICE_NOT_AVAIL);
		}
		
		String[] channels=(String[])JObject.string2Serializable(response);
		
		cache.put(cacheKey,channels);
		return channels;
	}
	
	/**
	 * 监视关联路由节点状态，并报告RouterManager
	 * @throws RemoteException
	 */
	private void monitor() throws RemoteException{	
		boolean available=true;
		if(this.routerConfig.getRmi()!=null){//监测rmi接口状态
			try {
				synchronized(Constants.GLOBAL_LOCK){
					initialNamingContext = new InitialContext(this.routerConfig.getRmi().getConfig());
				}
			} catch (Exception e) {			
				available=false;
				initialNamingContext=null;
				log.log(e,Logger.LEVEL_DEBUG_ADV);
			}
			
			if(available){		
				try {
					servant=(JRouter)initialNamingContext.lookup(this.routerConfig.getUuid());
					
					String heartbeat=servant.heartbeat();
					if(!heartbeat.startsWith(Constants.STATUS_OK)){
						throw new Exception("心跳不正常");
					}else{
						long upd=Long.parseLong(heartbeat.substring(heartbeat.indexOf(":")+1));
						if(upd>update){
							cache.clear();
							update=upd;
						}
					}
				} catch (Exception e) {
					available=false;
					servant=null;
					log.log(e,Logger.LEVEL_DEBUG_ADV);
				}
			}
		}else{
			available=false;
			servant=null;
		}
		routerRmiAvailable=available;
		
		
		available=true;
		if(this.routerConfig.getHttp()!=null){//监测http接口状态
			String url=this.routerConfig.getHttp().getEntrance();
			if(url.indexOf("?")>0) url+="&request=heartbeat";
			else url+="?request=heartbeat";
		
			JHttp jhttp=JHttp.getInstance();
			JHttpContext context=null;
			try{
				context=jhttp.get(null,null,url);
			}catch(Exception e){
				available=false;
				log.log(e,Logger.LEVEL_DEBUG);
			}

			if(context==null
					||context.getStatus()!=200
					||!context.getResponseText().startsWith(Constants.STATUS_OK)){
				available=false;
			}else{
				String heartbeat=context.getResponseText();
				long upd=Long.parseLong(heartbeat.substring(heartbeat.indexOf(":")+1));
				if(upd>update){
					cache.clear();
					update=upd;
				}
			}
		}else{
			available=false;
		}
		routerHttpAvailable=available;
		
		log.log("rmi of router "+routerConfig.getUuid()+","+routerConfig.getName()+" is "+(routerRmiAvailable?"available":"unavailable")+".",Logger.LEVEL_DEBUG);
		log.log("http of router "+routerConfig.getUuid()+","+routerConfig.getName()+" is "+(routerHttpAvailable?"available":"unavailable")+".",Logger.LEVEL_DEBUG);

		if(routerRmiAvailable){
			RouterManager.setAvailableRmi(this.routerConfig.getUuid(),true);
		}else{
			RouterManager.setAvailableRmi(this.routerConfig.getUuid(),false);
		}
		
		if(routerHttpAvailable){
			RouterManager.setAvailableHttp(this.routerConfig.getUuid(),true);
		}else{
			RouterManager.setAvailableHttp(this.routerConfig.getUuid(),false);
		}
	}

	/**
	 * 监视状态并提交服务注册、卸载请求给关联的路由节点
	 */
	public void run() {
		int loop=0;
		while(!this.shutdown){			
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			try{
				monitor();
			}catch(Exception e){
				log.log(e,Logger.LEVEL_DEBUG);
			}
			
			try{//每5分钟清除缓存
				if(loop>60){
					cache.clear();
					loop=0;
				}
				loop++;
			}catch(Exception e){}
			
			try{				
				while(!tasks.isEmpty()){
					if(!routerRmiAvailable&&!routerHttpAvailable){//rmi与http接口均不可用
						break;
					}
					
					String[] task=(String[])tasks.remove(0);
					if("register".equals(task[0])){//注册
						Object result=null;
						if(routerRmiAvailable){//优先使用rmi
							result=servant.register(task[1],task[2],task[3],task[4],task[5],task[6],task[7]);
						}else if(routerHttpAvailable){
							String url=this.routerConfig.getHttp().getEntrance();
							if(url.indexOf("?")>0) url+="&request=register";
							else url+="?request=register";
							
							url+="&"+Constants.JSERVICE_PARAM_CLIENT_UUID+"="+task[1];
							url+="&"+Constants.JSERVICE_PARAM_SERVICE_CODE+"="+task[2];
							url+="&"+Constants.JSERVICE_PARAM_SERVICE_UUID+"="+task[3];
							url+="&"+Constants.JSERVICE_PARAM_RMI_CHANNEL+"="+JUtilString.encodeURI(task[4],SysConfig.sysEncoding);
							url+="&"+Constants.JSERVICE_PARAM_HTTP_CHANNEL+"="+JUtilString.encodeURI(task[5],SysConfig.sysEncoding);
							url+="&"+Constants.JSERVICE_PARAM_INTERFACE_CLASS+"="+task[6];
							url+="&"+Constants.JSERVICE_PARAM_MD5_STRING_4ROUTER+"="+task[7];
															
							JHttpContext context=jhttp.get(null,jclient,url,SysConfig.sysEncoding);
							result=context.getResponseText();
							context.finalize();
							context=null;
							
							if(!Constants.AUTH_FAILED.equals(result)
									&&!Constants.INVOKING_DONE.equals(result)){
								throw new Exception("通过调用http接口注册服务失败");
							}
						}
						
						if(Constants.AUTH_FAILED.equals(result)){
							log.log("service "+task[1]+","+task[2]+","+task[3]+" has not been registered to "+this.routerConfig.getUuid()+" caused by auth failure.",Logger.LEVEL_DEBUG);
						}else{
							log.log("service "+task[1]+","+task[2]+","+task[3]+" has been registered to "+this.routerConfig.getUuid()+" successfully.",Logger.LEVEL_DEBUG);
						}
						task=null;
					}else if("unregister".equals(task[0])){//卸载
						Object result=null;
						if(routerRmiAvailable){//优先使用rmi
							result=servant.unregister(task[1],task[2],task[3],task[4]);
						}else if(routerHttpAvailable){
							String url=this.routerConfig.getHttp().getEntrance();
							if(url.indexOf("?")>0) url+="&request=unregister";
							else url+="?request=unregister";

							url+="&"+Constants.JSERVICE_PARAM_CLIENT_UUID+"="+task[1];
							url+="&"+Constants.JSERVICE_PARAM_SERVICE_CODE+"="+task[2];
							url+="&"+Constants.JSERVICE_PARAM_SERVICE_UUID+"="+task[3];
							url+="&"+Constants.JSERVICE_PARAM_MD5_STRING_4ROUTER+"="+task[4];
							url+="&"+Constants.JSERVICE_PARAM_MACHINE_ID+"="+SysConfig.getMachineID();
														
							JHttpContext context=jhttp.get(null,jclient,url,SysConfig.sysEncoding);
							result=context.getResponseText();
							context.finalize();
							context=null;
							
							if(!Constants.AUTH_FAILED.equals(result)
									&&!Constants.INVOKING_DONE.equals(result)){
								throw new Exception("通过调用http接口卸载服务失败");
							}
						}
					
						if(Constants.AUTH_FAILED.equals(result)){//认证失败
							log.log("service "+task[1]+","+task[2]+","+task[3]+" has not been unregistered from "+this.routerConfig.getUuid()+" caused by auth failure.",Logger.LEVEL_DEBUG);
						}else{
							log.log("service "+task[1]+","+task[2]+","+task[3]+" has been unregistered from "+this.routerConfig.getUuid()+" successfully.",Logger.LEVEL_DEBUG);
						}
						task=null;
					}
				}
			}catch(Exception e){
				log.log(e,Logger.LEVEL_WARNING);
			}
			
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
		}
	}
}
