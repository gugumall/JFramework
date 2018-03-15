package j.service.hello;

import j.service.server.ServiceBaseImpl;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * 
 * @author JFramework
 *
 */
public abstract class JHelloAbstract extends ServiceBaseImpl implements JHello,Serializable {	
	/**
	 * 
	 * @throws RemoteException
	 */
	public JHelloAbstract() throws RemoteException {
		super();
	}
}