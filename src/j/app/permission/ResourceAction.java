package j.app.permission;

import java.util.LinkedList;
import java.util.List;

import j.app.sso.User;
import j.app.webserver.Handler;
import j.app.webserver.Handlers;
import j.util.JUtilString;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author 肖炯
 *
 */
public class ResourceAction implements Resource{
	private String path;	
	private String actionId;
	private String[] roles;//可访问该资源的角色，多个用|分隔	
	private String noPermissionPage;//当用户已经登录，但不具备访问该资源权限时转向的页面，如不设置则转向SSOServer.noRightPage
	private String loginPage;//当用户未登录时转向的页面，如不设置则转向SSOServer.LOGIN_PAGE
	private List excludes;//如果匹配excludes中指定的方法，则不进行权限认证
	
	
	/**
	 * 
	 *
	 */
	public ResourceAction(){
		excludes=new LinkedList();
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
		String requestURI=request.getRequestURI();	
		
		Handler handler=null;
		String _path="";
		String pattern=Handlers.isActionPath(requestURI);
		if(pattern!=null){
			if(requestURI.endsWith(pattern)){//常规方式
				_path=requestURI.substring(0,requestURI.indexOf(pattern));
				handler=Handlers.getHandler(path);
			}else{//RESTful方式
				handler=Handlers.getHandler(requestURI);
				_path=requestURI.substring(0,requestURI.lastIndexOf("/"));
			}
		}
		
		if(handler==null) return false;

		String _actionId=request.getParameter(handler.getRequestBy());
		if(actionId==null){//RESTful
			actionId=requestURI.substring(requestURI.lastIndexOf("/")+1);
		}
		
		if(this.actionId==null||"".equals(this.actionId)){
			if(path.equals(_path)){
				for(int i=0;i<this.excludes.size();i++){
					String exclude=(String)this.excludes.get(i);
					if(exclude.equals(_actionId)) return false;
				}
				return true;
			}
		}else{
			return path.equals(_path)&&actionId.equals(_actionId);
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
