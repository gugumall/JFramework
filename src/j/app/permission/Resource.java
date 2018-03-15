package j.app.permission;

import j.app.sso.User;

import javax.servlet.http.HttpServletRequest;

public interface Resource {	
	/**
	 * 
	 * @return
	 */
	public String getNoPermissionPage();
	
	/**
	 * 
	 * @return
	 */
	public String getLoginPage();
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public boolean matches(HttpServletRequest request);
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public boolean matchesComplete(HttpServletRequest request);
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public boolean isUserInRole(User user);
}
