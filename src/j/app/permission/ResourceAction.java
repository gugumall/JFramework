package j.app.permission;

import j.app.sso.User;
import j.app.webserver.Handler;
import j.app.webserver.Handlers;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author JFramework
 *
 */
public class ResourceAction implements Resource{
	private String path;	
	private String actionId;
	private String[] roles;//可访问该资源的角色，多个用|分隔	
	private String noPermissionPage;//当用户已经登录，但不具备访问该资源权限时转向的页面，如不设置则转向SSOServer.noRightPage
	private String loginPage;//当用户未登录时转向的页面，如不设置则转向SSOServer.LOGIN_PAGE
	
	
	/**
	 * 
	 *
	 */
	public ResourceAction(){
	}
	
	//setters	 
	public void setPath(String path){
		this.path=path;
	}
	
	public void setActionId(String actionId){
		this.actionId=actionId;
	}
	
	public void setRoles(String _roles){
		this.roles=_roles.split("\\|");
	}
	
	public void setNoPermissionPage(String noPermissionPage){
		this.noPermissionPage=noPermissionPage;
	}
	
	public void setLoginPage(String loginPage){
		this.loginPage=loginPage;
	}
	
	
	
	/*
	 *  (non-Javadoc)
	 * @see j.app.permission.Resource#getNoPermissionPage()
	 */
	public String getNoPermissionPage(){
		return this.noPermissionPage;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.app.permission.Resource#getLoginPage()
	 */
	public String getLoginPage(){
		return this.loginPage;
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see j.app.permission.Resource#matches(javax.servlet.http.HttpServletRequest)
	 */
	public boolean matches(HttpServletRequest request){
		String requestURI=request.getRequestURI();	
		
		if(Handlers.isActionPath(requestURI)!=null){
			String _path=requestURI.substring(0,requestURI.lastIndexOf("."));
			Handler handler=Handlers.getHandler(_path);
			if(handler==null) return false;
			
			if(this.actionId==null||"".equals(this.actionId)){
				return path.equals(_path);
			}else{
				String _actionId=request.getParameter(handler.getRequestBy());
				return path.equals(_path)&&actionId.equals(_actionId);
			}
		}
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.app.permission.Resource#matchesComplete(javax.servlet.http.HttpServletRequest)
	 */
	public boolean matchesComplete(HttpServletRequest request){
		return matches(request);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.app.permission.Resource#isUserInRole(j.app.sso.User)
	 */
	public boolean isUserInRole(User user){
		if(user==null){
			return false;
		}
		return user.isUserInRole(roles);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.path+";"+this.actionId+";"+this.noPermissionPage+";"+this.loginPage;
	}
}
