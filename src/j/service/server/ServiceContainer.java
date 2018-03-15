package j.service.server;

import j.log.Logger;
import j.nvwa.Nvwa;
import j.nvwa.NvwaObject;
import j.service.Manager;
import j.service.router.RouterManager;
import j.util.ConcurrentMap;
import j.util.JUtilMD5;

import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * 用户启动服务的线程
 * @author JFramework
 *
 */
public class ServiceContainer implements Runnable{	
	private static Logger log=Logger.create(ServiceContainer.class);
	private ConcurrentMap servantOfServices=new ConcurrentMap();
	private ServiceConfig serviceConfig=null;//相关联的服务
	private ServiceBase servant = null;//相关联的服务类的对象
	private Context initialNamingContext = null;

	private boolean started=false;//是否正在运行
	private boolean shutdown=false;
	
	/**
	 * 
	 * @param codeOrUuid
	 * @return
	 */
	public ServiceBase getServantOfService(String codeOrUuid){
		return (ServiceBase)servantOfServices.get(codeOrUuid);
	}
	
	/**
	 * 
	 * @param config
	 */
	public ServiceContainer(ServiceConfig config) {
		super();
		this.serviceConfig=config;
	}
	
	/**
	 * 
	 * @return
	 */
	public ServiceConfig getServiceConfig(){
		return serviceConfig;
	}
	
	
	/**
	 * 
	 * @param started
	 */
	public void setStarted(boolean started){
		this.started=started;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getStarted(){
		return this.started;
	}
	
	/**
	 * 
	 * @param container
	 * @param service
	 * @throws Exception
	 */
	private void createServant() throws Exception{
		servant = (ServiceBase)Nvwa.entrustCreate(serviceConfig.getRelatedHttpHandlerPath(),serviceConfig.getClassName(),true);
		servant.setServiceConfig(serviceConfig);
		servant.init();
		servantOfServices.put(serviceConfig.getUuid(),servant);
		servantOfServices.put(serviceConfig.getCode(),servant);
	}

	
	/**
	 * 启动服务
	 * @param serviceConfig
	 * @throws Exception
	 */
	synchronized protected void startup() throws Exception{	
		NvwaObject nvwaObject=Nvwa.entrust(serviceConfig.getRelatedHttpHandlerPath(),serviceConfig.getClassName(),true);
		nvwaObject.setFiled("serviceConfig","value",true);
		String[] fieldsKeep=serviceConfig.getFieldsKeep();
		for(int i=0;fieldsKeep!=null&&i<fieldsKeep.length;i++){
			nvwaObject.setFiled(fieldsKeep[i],"value",true);
		}

		createServant();
		
		Thread thread=new Thread(this);
		thread.start();
		
		setStarted(true);
		
		log.log("service "+serviceConfig.getUuid()+" started.",-1);
	}

	
	/**
	 * 停止服务
	 * @param serviceConfig
	 */
	synchronized protected void shutdown(){			
		try{
			unregister();//向路由器卸载服务
		}catch(Exception e){
			log.log(e,Logger.LEVEL_WARNING);			
		}
		
		if(serviceConfig.getRmi()!=null){
			try{
				initialNamingContext.unbind(serviceConfig.getUuid());	
			}catch(Exception e){
				log.log(e,Logger.LEVEL_WARNING);			
			}
		}
		
		setStarted(false);
		
		shutdown=true;
		
		log.log("service "+serviceConfig.getUuid()+" shutdown.",-1);
	}
	

	
	/**
	 * 向路由器注册服务
	 * @return
	 */
	private String register(){		
		String md5="";
		md5+=Manager.getServerNodeUuid();
		md5+=serviceConfig.getCode();
		md5+=serviceConfig.getUuid();
		md5+=serviceConfig.getRmi()==null?"":serviceConfig.getRmi().getConfig("java.naming.provider.url");
		md5+=serviceConfig.getHttp()==null?"":serviceConfig.getHttp().getConfig("j.service.http");
		md5+=serviceConfig.getIntefaceName();
		md5+=Manager.getServerKeyToRouter();
		md5=JUtilMD5.MD5EncodeToHex(md5);
		
		return RouterManager.register(Manager.getServerNodeUuid(),
				serviceConfig.getCode(),
				serviceConfig.getUuid(),
				serviceConfig.getRmi()==null?"":serviceConfig.getRmi().getConfig("java.naming.provider.url"),
				serviceConfig.getHttp()==null?"":serviceConfig.getHttp().getConfig("j.service.http"),
				serviceConfig.getIntefaceName(),
				md5);
	}
	
	/**
	 * 从路由器卸载服务
	 * @return
	 */
	private String unregister(){
		String md5="";
		md5+=Manager.getServerNodeUuid();
		md5+=serviceConfig.getCode();
		md5+=serviceConfig.getUuid();
		md5+=Manager.getServerKeyToRouter();
		md5=JUtilMD5.MD5EncodeToHex(md5);
		
		return RouterManager.unregister(Manager.getServerNodeUuid(),
				serviceConfig.getCode(),
				serviceConfig.getUuid(),
				md5);		 
	} 
	
	/**
	 * 启动rmi服务
	 *
	 */
	private void startRmi(){
		try {
			log.log("init rmi of service "+serviceConfig.getName()+", the impl class is "+serviceConfig.getClassName(),Logger.LEVEL_INFO);
			
			initialNamingContext = new InitialContext(serviceConfig.getRmi().getConfig());
			
			//因为服务实现类继承了ServiceBase，而java不能多继承，所以必须调用UnicastRemoteObject.exportObject方法使对象成为合法的rmi对象
			Remote remote=null;
			try{
				remote=UnicastRemoteObject.exportObject(servant,0);
			}catch(Exception ex){
				log.log(ex.getMessage(),Logger.LEVEL_ERROR);
			}
		
			initialNamingContext.rebind(serviceConfig.getUuid(), remote==null?servant:remote);	
		} catch (Exception ex) {
			log.log("failed to run rmi of service "+serviceConfig.getName()+", the impl class is "+serviceConfig.getClassName(),Logger.LEVEL_INFO);
			log.log(ex.getMessage(),Logger.LEVEL_ERROR);
			
			try{//如果未启动成功（比如名称服务未启动），则尝试再次启动
				Thread.sleep(30000);
				startRmi();
			}catch(Exception e) {}
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void autoRenew() throws Exception{
		if(Nvwa.needRenew(serviceConfig.getClassName())
				||Nvwa.hasRenew(serviceConfig.getRelatedHttpHandlerPath(),serviceConfig.getClassName(),true,servant)){//需要更新
			log.log("need to renew "+serviceConfig.getName()+", the impl class is "+serviceConfig.getClassName(),-1);		
			
			createServant();
			
			if(serviceConfig.getRmi()!=null){
				initialNamingContext = new InitialContext(serviceConfig.getRmi().getConfig());
				
				Remote remote=null;
				try{
					remote=UnicastRemoteObject.exportObject(servant,0);
				}catch(Exception ex){
					log.log(ex.getMessage(),Logger.LEVEL_ERROR);
				}
			
				initialNamingContext.rebind(serviceConfig.getUuid(), remote==null?servant:remote);	
			}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {	
		if(serviceConfig.getRmi()!=null){
			startRmi();
		}

		while(!this.shutdown){
			try{
				autoRenew();
			}catch(Exception e){
				log.log("failed to autoRenew of service "+serviceConfig.getName()+", the impl class is "+serviceConfig.getClassName(),Logger.LEVEL_FATAL);		
			}
			
			try{
				register();
			}catch(Exception e){
				log.log("failed to re register of service "+serviceConfig.getName()+", the impl class is "+serviceConfig.getClassName(),Logger.LEVEL_INFO);		
			}
			
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	public void finalize(){
		this.shutdown();
	}
}
