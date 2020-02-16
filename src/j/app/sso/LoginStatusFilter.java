package j.app.sso;

import j.cache.JCacheFilter;

/**
 * 
 * @author 肖炯
 *
 */
public class LoginStatusFilter implements JCacheFilter{
	private static final long serialVersionUID = 1L;
	private String userId=null;
	private String subUserId=null;
	private boolean includeSubUsers=false;

	/**
	 * 
	 * @param userId
	 */
	public LoginStatusFilter(String userId) {
		this.userId=userId;
	}
	
	/**
	 * 
	 * @param userId
	 * @param subUserId
	 */
	public LoginStatusFilter(String userId, String subUserId) {
		this.userId=userId;
		this.subUserId=subUserId;
	}
	
	/**
	 * 
	 * @param userId
	 * @param subUserId
	 * @param includeSubUsers
	 */
	public LoginStatusFilter(String userId, String subUserId, boolean includeSubUsers) {
		this.userId=userId;
		this.subUserId=subUserId;
		this.includeSubUsers=includeSubUsers;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheFilter#matches(java.lang.Object)
	 */
	public boolean matches(Object object) {
		if(object==null) return false;
		
		LoginStatus obj=(LoginStatus)object;
		
		if(userId!=null&&!"".equals(userId)&&!userId.equals(obj.getUserId())) return false;
		
		if(subUserId!=null&&!"".equals(subUserId)) {
			if(!subUserId.equals(obj.getSubUserId())) return false;
		}else if(!includeSubUsers && obj.getSubUserId()!=null && !"".equals(obj.getSubUserId())) {
			return false;
		}
		
		return true;
	}
}
