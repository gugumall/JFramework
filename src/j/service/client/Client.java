package j.service.client;

import j.Properties;
import j.http.JHttp;
import j.http.JHttpContext;
import j.log.Logger;
import j.service.Constants;
import j.service.Manager;
import j.service.router.RouterManager;
import j.service.server.ServiceBase;
import j.service.server.ServiceContainer;
import j.service.server.ServiceManager;
import j.sys.SysConfig;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilMD5;
import j.util.JUtilRandom;

import java.io.File;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 应用可通过此类获取服务并调用相关方法
 * @author 肖炯
 *
 */
public class Client implements Runnable{
	private static Logger log=Logger.create(Client.class);
	
	private static ConcurrentMap services=new ConcurrentMap();
	private static String callLocalServiceIfExists="true";//如果服务存在于本应用中，是否直接调用本地服务
	private static long configLastModified=0;//配置文件上次修改时间
	private static volatile boolean loading=true;//是否正在加载配置文件
	
	static{
		Client m=new Client();
		Thread thread=new Thread(m);
		thread.start();
		log.log("Client monitor thread started.",-1);
	}
	
	/**
	 * 
	 *
	 */
	private Client(){
	}
	
	/**
	 * 
	 *
	 */
	public static void load(){
		try{
			loading=true;
			
			services.clear();
			
			//文件是否存在
			File file = new File(Properties.getConfigPath()+"service.client.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			
			Document document=JUtilDom4j.parse(Properties.getConfigPath()+"service.client.xml",SysConfig.sysEncoding);
			Element root=document.getRootElement();
			
			callLocalServiceIfExists=root.elementText("callLocalServiceIfExists");
			log.log(SysConfig.getSysId()+" - callLocalServiceIfExists - "+callLocalServiceIfExists,-1);
			
			List servs=root.elements("service");
			for(int i=0;i<servs.size();i++){
				Element serv=(Element)servs.get(i);
				
				Service service=new Service();
				service.setCode(serv.elementText("code"));
				service.setName(serv.elementText("name"));
				
				List props=serv.elements("property");
				for(int j=0;j<props.size();j++){
					Element pEle=(Element)props.get(j);
					service.addConfig(pEle.attributeValue("key"),pEle.attributeValue("value"));
				}
				
				services.put(service.getCode(),service);
			}
			
			root=null;
			document=null;

			//配置文件最近修改时间
			File configFile=new File(Properties.getConfigPath()+"service.client.xml");
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
				Thread.sleep(1000);
			}catch(Exception ex){}
		}
	}
	
	/**
	 * 
	 * @param code
	 * @return
	 */
	private static Service getService(String code){
		waitWhileLoading();
		return (Service)services.get(code);
	}
	
	
	/**
	 * 获得服务入口
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static ServiceBase rmiGetService(String code) throws Exception{
		return rmiGetService(code,false);
	}
	
	
	/**
	 * 获得服务入口
	 * @param code
	 * @param local
	 * @return
	 * @throws Exception
	 */
	public static ServiceBase rmiGetService(String code,boolean local) throws Exception{
		boolean callLocal=local?true:false;
		if(!local){
			if("true".equalsIgnoreCase(Client.callLocalServiceIfExists)) callLocal=true;
			else if("random".equalsIgnoreCase(Client.callLocalServiceIfExists)){
				int x=JUtilRandom.nextInt(3);
				if(x==0) callLocal=true;
			}
		}
		
		if(callLocal){//调用本地服务
			ServiceContainer container=ServiceManager.getServiceContainer(code);
			if(container!=null){
				ServiceBase servant=container.getServant();
				if(servant!=null){
					return servant;
				}
			}
		}
		
		if(local) return null;
		
		String md54Routing="";
		md54Routing+=Manager.getClientNodeUuid();
		md54Routing+=Manager.getClientKeyToRouter();
		md54Routing=JUtilMD5.MD5EncodeToHex(md54Routing);
		
		return (ServiceBase)RouterManager.serviceRmi(Manager.getClientNodeUuid(),
				code,
				md54Routing);
	}
	
	
	/**
	 * 获得服务入口
	 * @param code
	 * @param local
	 * @return
	 * @throws Exception
	 */
	public static ServiceBase[] rmiGetAllServiceNodes(String code) throws Exception{
		String md54Routing="";
		md54Routing+=Manager.getClientNodeUuid();
		md54Routing+=Manager.getClientKeyToRouter();
		md54Routing=JUtilMD5.MD5EncodeToHex(md54Routing);
		
		return RouterManager.serviceNodesRmi(Manager.getClientNodeUuid(),
				code,
				md54Routing);
	}
	
	

	
	/**
	 * 调用方法
	 * @param code
	 * @param servant
	 * @param methodName
	 * @param parameterTypes
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public static Object rmiCall(String code,ServiceBase servant,String methodName,Class[] parameterTypes,Object[] parameters) throws Exception{
		Class<?>[] parameterTypesFull=new Class[parameterTypes.length+2];
		parameterTypesFull[0]=String.class;
		parameterTypesFull[1]=String.class;
		for(int i=0;i<parameterTypes.length;i++){
			parameterTypesFull[i+2]=parameterTypes[i];
		}
		
		Object[] parametersFull=new Object[parameters.length+2];
		
		parametersFull[0]=Manager.getClientNodeUuid();
		
		String md54Service=md54Service(code,methodName);		
		parametersFull[1]=md54Service;
		
		for(int i=0;i<parameters.length;i++){
			parametersFull[i+2]=parameters[i];
		}
		
		Method method = servant.getClass().getMethod(methodName,parameterTypesFull);
				
		return method.invoke(servant,parametersFull);
	}

	
	/**
	 * 获得服务入口
	 * @param code
	 * @return
	 * @throws RemoteException
	 */
	public static String httpGetService(JHttp jhttp,HttpClient client,String code) throws Exception{
		return httpGetService(jhttp,client,code,false);
	}

	
	/**
	 * 获得服务入口
	 * @param code
	 * @return
	 * @throws RemoteException
	 */
	public static String httpGetService(JHttp jhttp,HttpClient client,String code,boolean local) throws Exception{
		boolean callLocal=local?true:false;
		if(!local){
			if("true".equalsIgnoreCase(Client.callLocalServiceIfExists)) callLocal=true;
			else if("random".equalsIgnoreCase(Client.callLocalServiceIfExists)){
				int x=JUtilRandom.nextInt(3);
				if(x==0) callLocal=true;
			}
		}
		
		if(callLocal){//调用本地服务
			ServiceContainer container=ServiceManager.getServiceContainer(code);
			if(container!=null){
				ServiceBase servant=container.getServant();
				if(servant!=null&&servant.getServiceConfig().getHttp()!=null){
					return servant.getServiceConfig().getHttp().getEntrance();
				}
			}
		}
		
		if(local) return null;
		
		String md54Routing="";
		md54Routing+=Manager.getClientNodeUuid();
		md54Routing+=Manager.getClientKeyToRouter();
		md54Routing=JUtilMD5.MD5EncodeToHex(md54Routing);
		
		String entrance=RouterManager.serviceHttp(jhttp,
				client,
				Manager.getClientNodeUuid(),
				code,
				md54Routing);
		
		if(Constants.AUTH_FAILED.equals(entrance)
				||Constants.SERVICE_NOT_FOUND.equals(entrance)
				||Constants.SERVICE_NOT_AVAIL.equals(entrance)){
			return null;
			//throw new Exception("无可用服务(http) - "+entrance+" - "+code);
		} 
		
		if(entrance==null
				||!entrance.startsWith("http")){
			return null;
			//throw new Exception("无可用服务(http) - "+entrance+" - "+code);
		}
		
		return entrance;
	}

	
	/**
	 * 获得服务入口
	 * @param code
	 * @return
	 * @throws RemoteException
	 */
	public static String[] httpGetAllServiceNodes(JHttp jhttp,HttpClient client,String code) throws Exception{
		String md54Routing="";
		md54Routing+=Manager.getClientNodeUuid();
		md54Routing+=Manager.getClientKeyToRouter();
		md54Routing=JUtilMD5.MD5EncodeToHex(md54Routing);
		
		String[] entrance=RouterManager.serviceNodesHttp(jhttp,
				client,
				Manager.getClientNodeUuid(),
				code,
				md54Routing);
		
		return entrance;
	}
	
	/**
	 * 
	 * @param jhttp
	 * @param client
	 * @param code
	 * @param entrance
	 * @param methodName
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String httpCallGet(JHttp jhttp,HttpClient client,String code,String entrance,String methodName,Map params) throws Exception{
		 String url=entrance;
		 if(url.indexOf("?")>0) url+="&request="+methodName;
		 else url+="?request="+methodName;
		 
		 String md54Service=md54Service(code,methodName);
		 url+="&"+Constants.JSERVICE_PARAM_CLIENT_UUID+"="+Manager.getClientNodeUuid();
		 url+="&"+Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE+"="+md54Service;
		 
		 if(params!=null){
			 for(Iterator keys=params.keySet().iterator();keys.hasNext();){
				 Object key=keys.next();
				 Object val=params.get(key);
				 url+="&"+key+"="+val;
			 }
		 }
		 
		 try{
			 if(jhttp==null) jhttp=JHttp.getInstance();
			 JHttpContext context=jhttp.get(null,client,url,SysConfig.sysEncoding);
			 
			 if(context==null
						||context.getStatus()!=200
						||Constants.INVOKING_FAILED.equals(context.getResponseText())){
				if(context!=null){							
						context.finalize();
						context=null;
				}
				throw new Exception("failed to call service on "+url);
			}
			 
			 String responseText=context.getResponseText();
			 context.finalize();
			 context=null;
				
			 return responseText;
		 }catch(Exception ex){
			 log.log(ex,Logger.LEVEL_ERROR);
			 throw ex;
		 }
	}
	
	/**
	 * 
	 * @param jhttp
	 * @param client
	 * @param code
	 * @param entrance
	 * @param methodName
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String httpCallPost(JHttp jhttp,HttpClient client,String code,String entrance,String methodName,Map params) throws Exception{
		 String url=entrance;
		 if(url.indexOf("?")>0) url+="&request="+methodName;
		 else url+="?request="+methodName;
		 
		 if(params==null) params=new HashMap();
		 String md54Service=md54Service(code,methodName);
		 params.put(Constants.JSERVICE_PARAM_CLIENT_UUID,Manager.getClientNodeUuid());
		 params.put(Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE,md54Service);
		 
		 try{
			 if(jhttp==null) jhttp=JHttp.getInstance();
			 JHttpContext context=jhttp.post(null,client,url,params,SysConfig.sysEncoding);
			 
			 if(context==null
						||context.getStatus()!=200
						||Constants.INVOKING_FAILED.equals(context.getResponseText())){
				if(context!=null){							
						context.finalize();
						context=null;
				}
				throw new Exception("failed to call service on "+url);
			}
			 
			 String responseText=context.getResponseText();
			 context.finalize();
			 context=null;
				
			 return responseText;
		 }catch(Exception ex){
			 log.log(ex,Logger.LEVEL_ERROR);
			 throw ex;
		 }
	}
	
	/**
	 * 
	 * @param jhttp
	 * @param client
	 * @param code
	 * @param entrance
	 * @param methodName
	 * @param params
	 * @param parts
	 * @return
	 * @throws Exception
	 */
	public static String httpCallMultiPart(JHttp jhttp,HttpClient client,String code,String entrance,String methodName,Map params,Map parts) throws Exception{
		 String url=entrance;
		 if(url.indexOf("?")>0) url+="&request="+methodName;
		 else url+="?request="+methodName;
		 
		 if(params==null) params=new HashMap();
		 String md54Service=md54Service(code,methodName);
		 params.put(Constants.JSERVICE_PARAM_CLIENT_UUID,Manager.getClientNodeUuid());
		 params.put(Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE,md54Service);
		 
		 try{
			 if(jhttp==null) jhttp=JHttp.getInstance();
			 JHttpContext context=jhttp.postMultipartData(null,client,url,parts,params,SysConfig.sysEncoding);
			 
			 if(context==null
						||context.getStatus()!=200
						||Constants.INVOKING_FAILED.equals(context.getResponseText())){
				if(context!=null){							
						context.finalize();
						context=null;
				}
				throw new Exception("failed to call service on "+url);
			}
			 
			 String responseText=context.getResponseText();
			 context.finalize();
			 context=null;
				
			 return responseText;
		 }catch(Exception ex){
			 log.log(ex,Logger.LEVEL_ERROR);
			 throw ex;
		 }
	}
	
	/**
	 * 
	 * @param code
	 * @param methodName
	 * @return
	 */
	public static String md54Service(String code,String methodName){
		Service service=getService(code);
		if(service==null) return null;//配置不存在，可能是忘记配置，或不需要审核
		
		String md54Service="";
		md54Service+=Manager.getClientNodeUuid();
		md54Service+=methodName;
		md54Service+=service.getClientKey4Service();
		md54Service=JUtilMD5.MD5EncodeToHex(md54Service);	
		
		return md54Service;
	}


	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * 检测service.client.xml是否修改过，如修改过重新加载配置
		 */
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			if(configLastModified<=0) continue;

			File configFile=new File(Properties.getConfigPath()+"service.client.xml");
			if(configLastModified<configFile.lastModified()){
				log.log("service.client.xml has been modified, so reload it.",-1);
				load();
			}
			configFile=null;
		}
	}
}
