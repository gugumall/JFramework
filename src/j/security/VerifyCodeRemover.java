package j.security;

import j.cache.JCacheFilter;
import j.sys.SysUtil;

/**
 * 
 * @author 肖炯
 *
 */
public class VerifyCodeRemover implements JCacheFilter{
	private static final long serialVersionUID = 1L;
	private long now;
	
	/**
	 * 
	 */
	public VerifyCodeRemover(){
		now=SysUtil.getNow();
	}

	/*
	 * (non-Javadoc)
	 * @see j.cache.JCacheFilter#matches(java.lang.Object)
	 */
	public boolean matches(Object object) {
		if(object==null||!(object instanceof VerifyCodeBean)) return true;
		
		VerifyCodeBean o=(VerifyCodeBean)object;
		if(this.now-o.getTime()>o.getTimeout()) return true;
		
		return false;
	}
}
