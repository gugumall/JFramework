package j.cache;

import j.service.server.ServiceBaseImpl;

import java.io.Serializable;

/**
 * 
 * @author JFramework
 *
 */
public abstract class JDCacheServiceAbstract extends ServiceBaseImpl implements JDCacheService,Serializable {	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 *
	 */
	public JDCacheServiceAbstract() {
		super();
	}
}
