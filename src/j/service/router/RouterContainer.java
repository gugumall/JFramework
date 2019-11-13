package j.service.router;

import j.log.Logger;
import j.util.JUtilMath;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.omg.CORBA.ORB;

public class RouterContainer implements Runnable {	
	private static Logger log=Logger.create(RouterContainer.class);
	private RouterConfig routerConfig=null;	
	private JRouter router=null;	
	private ORB orb = null;
	private Context initialNamingContext = null;
	private boolean started=false;//是否正在运行

	/**
	 * 
	 * @param config
	 * @param router
	 */
	public RouterContainer(RouterConfig config,JRouter router) {
		super();
		this.routerConfig=config;
		this.router=router;
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
	 * @param router
	 * @throws Exception
	 */
	protected void startup() throws Exception{	
		router.startup();
		
		if(routerConfig.getRmi()!=null){
			Thread thread=new Thread(this);
			thread.start();
		}
		
		routerConfig.setStarted(true);
		
		log.log("router "+routerConfig.getUuid()+" started.",-1);
	}

	
	/**
	 * 停止服务
	 * @param service
	 */
	synchronized protected void shutdown() throws RemoteException{			
		router.shutdown();
		
		if(routerConfig.getRmi()!=null){
			try{
				initialNamingContext.unbind(routerConfig.getUuid());	
			}catch(Exception e){
				log.log(e,Logger.LEVEL_WARNING);			
			}
		}
		
		log.log("router "+routerConfig.getUuid()+" shutdown.",-1);
	}
	
	/**
	 * 
	 *
	 */
	private void startRmi(){
		try {
			log.log("init rmi of service "+routerConfig.getName()+", the impl class is "+routerConfig.getClassName()+", provider url is "+routerConfig.getRmi().getConfig("java.naming.provider.url"),-1);

			initialNamingContext = new InitialContext(routerConfig.getRmi().getConfig());
			
			Remote remote=null;
			String providerUrl=routerConfig.getRmi().getConfig("java.naming.provider.url");
			try{
				int port=0;
				if(providerUrl.indexOf(":")>0) {
					String _port=providerUrl.substring(providerUrl.indexOf(":")+1);
					if(JUtilMath.isInt(_port)) port=Integer.parseInt(_port);
				}
				
				remote=UnicastRemoteObject.exportObject(router, port);
			}catch(Exception ex){
				log.log(ex,Logger.LEVEL_ERROR);
			}
			
			initialNamingContext.rebind(providerUrl+"/"+routerConfig.getUuid(),remote==null?router:remote);				
		} catch (Exception ex) {
			log.log("failed to run rmi of service "+routerConfig.getName()+", the impl class is "+routerConfig.getClassName(),Logger.LEVEL_INFO);
			log.log(ex,Logger.LEVEL_ERROR);
			
			try{
				Thread.sleep(30000);
				startRmi();
			}catch(Exception e) {}
		}
	}

	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	public void finalize(){
		try{
			this.shutdown();
		}catch (Exception e) {
			log.log(e,Logger.LEVEL_ERROR);
		}
	}


	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {		
		if(routerConfig.getRmi()!=null){
			startRmi();
		}		
	}
}
