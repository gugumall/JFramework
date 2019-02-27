package j.I18N;

import j.app.webserver.Handler;
import j.app.webserver.Handlers;
import j.app.webserver.Server;
import j.util.JUtilString;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author 肖炯
 *
 */
public class I18NUrl{
	private String urlPattern;	
	private String extension;
	private String match;
	private List excludes;
	
	
	/**
	 * 
	 *
	 */
	public I18NUrl(){
		excludes=new LinkedList();
	}
	
	//setters	 
	public void setUrlPattern(String urlPattern){
		this.urlPattern=urlPattern;
	}
	
	public void setExtension(String noPermissionPage){
		this.extension=noPermissionPage;
	}
	
	public void setMatch(String loginPage){
		this.match=loginPage;
	}
	
	public void addExclude(String exclude){
		this.excludes.add(exclude);
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public String getExtension(){
		return this.extension;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMatch(){
		return this.match;
	}
	
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public boolean matches(HttpServletRequest request){
		String requestURI=request.getRequestURI();
		
		if(!"".equals(extension)&&!requestURI.endsWith(extension)) return false;
		
		if("alike".equalsIgnoreCase(match)){
			if(JUtilString.match(requestURI,urlPattern,"*")<0) return false;
		}else{
			if(!requestURI.equals(urlPattern)) return false;
		}
		
		for(int i=0;i<this.excludes.size();i++){
			String exclude=(String)this.excludes.get(i);
			if(JUtilString.match(requestURI,exclude,"*")>-1) return false;
		}
		
		return true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.urlPattern+";"+this.extension+";"+this.match;
	}
}
