package j.comm.rmi;

import java.rmi.registry.LocateRegistry;

import j.common.JProperties;
import j.sys.Initializer;
import j.util.JUtilMath;

public class RMI implements Initializer{
	@Override
	public void initialization() throws Exception {
		init();
	}
	
	/**
	 * 
	 */
	private static void init() {		
		String createLocateRegistryOnStartup=JProperties.getProperty("rmi", "createLocateRegistryOnStartup");
		String port=JProperties.getProperty("rmi", "port");
		
		//启动rmi注册服务
		if("true".equals(createLocateRegistryOnStartup)
				&&JUtilMath.isInt(port)) {
			try {
				LocateRegistry.createRegistry(Integer.parseInt(port));
				System.out.println("LocateRegistry Started At "+port);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
