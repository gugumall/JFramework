package j.service.server;

import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.Context;
import javax.naming.InitialContext;

import j.log.Logger;
import j.nvwa.Nvwa;
import j.nvwa.NvwaObject;
import j.service.Manager;
import j.service.router.RouterManager;
import j.util.JUtilMD5;
import j.util.JUtilMath;

/**
 * 用户启动服务的线程
 * @author 肖炯
 *
 */
public class ServiceContainer implements Runnable{	
	private static Logger log=Logger.create(ServiceContainer.class);
	private ServiceConfig config=null;//相关联的服务
	private ServiceBase servant = null;//相关联的服务类的对象
	private Context initialNamingContext = null;//rmi naming context

	private boolean started=false;//是否正在运行
	private boolean shutdown=false;//是否已经停止
	private boolean isLocalService=true;//是否本节点服务
	
	/**
	 * 
	 * @return
	 */
	public ServiceBase getServant(){
		return servant;
	}
	
	/**
	 * 
	 * @param config
	 * @param isLocalService
	 */
	public ServiceContainer(ServiceConfig config, boolean isLocalService) {
		super();
		this.config=config;
		this.isLocalService=isLocalService;
	}
	
	/**
	 * 
	 * @return
	 */
	public ServiceConfig getServiceConfig(){
		return config;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getStarted(){
		return this.started;
	}
	
	/**
	 * 创建服务对象
	 * @param container
	 * @param service
	 * @throws Exception
	 */
	private void createServant() throws Exception{
		servant = (ServiceBase)Nvwa.entrustCreate(config.getRelatedHttpHandlerPath(),
				config.getClassName(),
				true);
		servant.setServiceConfig(config);
		servant.init();
	}

	
	/**
	 * 启动服务
	 * @param config
	 * @throws Exception
	 */
	synchronized protected void startup() throws Exception{	
		if(isLocalService) {
			//将服务实现类托管至对象工厂（实现热加载）
			NvwaObject nvwaObject=Nvwa.entrust(config.getRelatedHttpHandlerPath(),
					config.getClassName(),
					true);
			nvwaObject.setFiled("config","value",true);
			String[] fieldsKeep=config.getFieldsKeep();
			for(int i=0;fieldsKeep!=null&&i<fieldsKeep.length;i++){
				nvwaObject.setFiled(fieldsKeep[i],"value",true);
			}
	
			//创建服务对象
			createServant();
		}
		
		//启动监控线程，用于向路由节点注册/卸载服务、并在需要时自动重启服务
		Thread thread=new Thread(this);
		thread.start();
		
		log.log("service "+config.getUuid()+" started.",-1);
		
		this.started=true;
		this.shutdown=false;
	}

	
	/**
	 * 停止服务
	 * @param config
	 */
	synchronized protected void shutdown(){			
		try{
			unregister();//向路由器卸载服务
		}catch(Exception e){
			log.log(e,Logger.LEVEL_WARNING);			
		}
		
		if(isLocalService) {
			if(config.getRmi()!=null){
				try{
					initialNamingContext.unbind(config.getUuid());	
				}catch(Exception e){
					log.log(e,Logger.LEVEL_WARNING);			
				}
			}
		}
		
		this.started=false;
		this.shutdown=true;
		
		log.log("service "+config.getUuid()+" shutdown.",-1);
	}
	

	
	/**
	 * 向路由器注册服务
	 * @return
	 */
	private String register(){		
		String md5="";
		md5+=Manager.getServerNodeUuid();
		md5+=config.getCode();
		md5+=config.getUuid();
		md5+=config.getRmi()==null?"":config.getRmi().getConfig("java.naming.provider.url");
		md5+=config.getHttp()==null?"":config.getHttp().getConfig("j.service.http");
		md5+=config.getIntefaceName();
		md5+=Manager.getServerKeyToRouter();
		md5=JUtilMD5.MD5EncodeToHex(md5);
		
		return RouterManager.register(Manager.getServerNodeUuid(),
				config.getCode(),
				config.getUuid(),
				config.getRmi()==null?"":config.getRmi().getConfig("java.naming.provider.url"),
				config.getHttp()==null?"":config.getHttp().getConfig("j.service.http"),
				config.getIntefaceName(),
				md5);
	}
	
	/**
	 * 从路由器卸载服务
	 * @return
	 */
	private String unregister(){
		String md5="";
		md5+=Manager.getServerNodeUuid();
		md5+=config.getCode();
		md5+=config.getUuid();
		md5+=Manager.getServerKeyToRouter();
		md5=JUtilMD5.MD5EncodeToHex(md5);
		
		return RouterManager.unregister(Manager.getServerNodeUuid(),
				config.getCode(),
				config.getUuid(),
				md5);		 
	} 
	
	/**
	 * 启动rmi服务
	 * @param retryOnFail 如启动失败是否重试
	 */
	private void startRmi(boolean retryOnFail){
		try {
			log.log("init rmi of service "+config.getName()+", the impl class is "+config.getClassName(),Logger.LEVEL_INFO);
			
			initialNamingContext = new InitialContext(config.getRmi().getConfig());
			
			String providerUrl=config.getRmi().getConfig("java.naming.provider.url");
			int port=0;
			if(providerUrl.indexOf(":")>0) {
				String _port=providerUrl.substring(providerUrl.indexOf(":")+1);
				if(JUtilMath.isInt(_port)) port=Integer.parseInt(_port);
			}
			
			//因为服务实现类继承了ServiceBase，而java不能多继承，所以必须调用UnicastRemoteObject.exportObject方法使对象成为合法的rmi对象
			Remote remote=null;
			try{
				remote=UnicastRemoteObject.exportObject(servant, port);
			}catch(Exception ex){
				log.log(ex.getMessage(),Logger.LEVEL_ERROR);
			}
		
			initialNamingContext.rebind(providerUrl+"/"+config.getUuid(), remote==null?servant:remote);	
		} catch (Exception ex) {
			log.log("failed to run rmi of service "+config.getName()+", the impl class is "+config.getClassName(),Logger.LEVEL_INFO);
			log.log(ex,Logger.LEVEL_ERROR);
			
			if(retryOnFail) {
				try{//如果未启动成功（比如名称服务未启动），则尝试再次启动
					Thread.sleep(30000);
					startRmi(true);
				}catch(Exception e) {}
			}
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void autoRenew() throws Exception{
		if(Nvwa.needRenew(config.getClassName())
				||Nvwa.hasRenew(config.getRelatedHttpHandlerPath(),config.getClassName(),true,servant)){//需要更新
			log.log("need to renew "+config.getName()+", the impl class is "+config.getClassName(),-1);		
			
			createServant();
			
			if(config.getRmi()!=null){
				startRmi(false);
			}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {	
		if(isLocalService && config.getRmi()!=null){
			startRmi(true);
		}

		while(!this.shutdown){
			if(isLocalService) {
				try{
					autoRenew();
				}catch(Exception e){
					log.log("failed to autoRenew of service "+config.getName()+", the impl class is "+config.getClassName(),Logger.LEVEL_FATAL);		
				}
			}
			
			try{
				register();
			}catch(Exception e){
				log.log("failed to re register of service "+config.getName()+", the impl class is "+config.getClassName(),Logger.LEVEL_INFO);		
			}
			
			try{
				Thread.sleep(1000);
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
