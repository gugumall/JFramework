package j.app.sso;

import j.cache.JCacheFilter;

/**
 * 
 * @author 肖炯
 *
 */
public class LoginStatusRemover implements JCacheFilter{
	private static final long serialVersionUID = 1L;
	private static LoginStatusRemover instance;

	/**
	 * 
	 *
	 */
	private LoginStatusRemover() {
		super();
	}
	
	/**
	 * 
	 * @return
	 */
	public static LoginStatusRemover getInstance(){
		if(instance==null){
			instance=new LoginStatusRemover();
		}
		return instance;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheFilter#matches(java.lang.Object)
	 */
	public boolean matches(Object object) {
		if(object==null) return true;
		
		LoginStatus obj=(LoginStatus)object;
		return obj.isTimeout();
	}
}
