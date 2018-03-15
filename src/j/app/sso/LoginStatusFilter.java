package j.app.sso;

import j.cache.JCacheFilter;

/**
 * 
 * @author 肖炯
 *
 */
public class LoginStatusFilter implements JCacheFilter{
	private static final long serialVersionUID = 1L;
	private String uid=null;

	/**
	 * 
	 *
	 */
	public LoginStatusFilter(String uid) {
		this.uid=uid;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheFilter#matches(java.lang.Object)
	 */
	public boolean matches(Object object) {
		if(object==null) return false;
		
		LoginStatus obj=(LoginStatus)object;
		
		if(uid!=null&&!"".equals(uid)&&!uid.equals(obj.getUserId())) return false;
		
		return true;
	}
}
