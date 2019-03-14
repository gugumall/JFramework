package j.fs;

import j.service.server.ServiceBaseImpl;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * 
 * @author JFramework
 *
 */
public abstract class JDFSServiceAbstract extends ServiceBaseImpl implements JDFSService,Serializable {	
	/**
	 * 
	 * @throws RemoteException
	 */
	public JDFSServiceAbstract() throws RemoteException{
		super();
	}
}
