package j.app.permission;

import j.app.sso.User;
import j.sys.SysUtil;
import j.util.JUtilString;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author 肖炯
 *
 */
public class ResourceUrl implements Resource{
	/*
	 * url模式，如：/roar/user/i*.jsp，其中*表示0个或多个任意字符，
	 * 如果用户请求的URL中包含符合该模式的子串，则表示匹配该模式，
	 * 只有具备roles中的一个或多个角色才可访问
	 */
	private String urlPattern;	
	private String[] roles;//可访问该资源的角色，多个用|分隔	
	private String noPermissionPage;//当用户已经登录，但不具备访问该资源权限时转向的页面，如不设置则转向SSOServer.noRightPage
	private String loginPage;//当用户未登录时转向的页面，如不设置则转向SSOServer.LOGIN_PAGE
	private List excludes;//如果匹配excludes中指定url模式的，则不进行权限认证
	
	
	/**
	 * 
	 *
	 */
	public ResourceUrl(){
		excludes=new LinkedList();
	}
	
	//setters	 
	public void setUrlPattern(String urlPattern){
		this.urlPattern=urlPattern;
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
	
	public void addExclude(String exclude){
		this.excludes.add(exclude);
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
		String requestUrl=SysUtil.getRequestURL(request,"sso_");
		
		requestUrl=JUtilString.replaceAll(requestUrl,"https://","");
		requestUrl=JUtilString.replaceAll(requestUrl,"http://","");
		
		if(JUtilString.match(requestUrl,this.urlPattern,"*")<0) return false;
		
		for(int i=0;i<this.excludes.size();i++){
			String exclude=(String)this.excludes.get(i);
			if(JUtilString.match(requestUrl,exclude,"*")>-1) return false;
		}
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.app.permission.Resource#matchesComplete(javax.servlet.http.HttpServletRequest)
	 */
	public boolean matchesComplete(HttpServletRequest request){
		String requestUrl=request.getRequestURI();
		
		return requestUrl.equals(this.urlPattern);
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
		return this.urlPattern+";"+this.noPermissionPage+";"+this.loginPage;
	}
}
