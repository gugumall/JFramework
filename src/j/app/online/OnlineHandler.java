package j.app.online;

import java.io.IOException;

import j.app.sso.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author 肖炯
 *
 */
public interface OnlineHandler {	
	/**
	 * 
	 * @param session
	 * @param request
	 * @param uri
	 * @return
	 */
	public UrlAndFetchType adjustUrl(HttpSession session,HttpServletRequest request,String uri);
	
	/**
	 * @deprecated
	 * @param request
	 * @return
	 */
	public boolean canPass(HttpServletRequest request);
	
	/**
	 * 
	 * @param session
	 * @param request
	 * @return
	 */
	public boolean canPass(HttpSession session,HttpServletRequest request);
	
	/**
	 * 
	 * @param ip
	 */
	public void onManySessionsOnIp(String ip);
	
	/**
	 * 
	 * @param online
	 * @param user
	 * @param session
	 * @param request
	 */
	public void onInit(Online online,User user,HttpSession session,HttpServletRequest request);
	
	/**
	 * 
	 * @param online
	 * @param user
	 * @param session
	 * @param request
	 */
	public void onLogin(Online online,User user,HttpSession session,HttpServletRequest request);
	
	/**
	 * 
	 * @param online
	 * @param user
	 * @param session
	 * @param request
	 */
	public void onLogout(Online online,User user,HttpSession session,HttpServletRequest request);

	/**
	 * 
	 * @param _request
	 * @param _response
	 * @param chain
	 */
	public boolean doFilterBefore(ServletRequest _request, 
			ServletResponse _response,
			FilterChain chain) throws IOException, ServletException;
	

	/**
	 * 
	 * @param _request
	 * @param _response
	 * @param chain
	 */
	public boolean doFilterAfter(ServletRequest _request, 
			ServletResponse _response,
			FilterChain chain) throws IOException, ServletException;
	
	/**
	 * 
	 * @param _request
	 * @param _response
	 * @param chain
	 */
	public void doFilter(ServletRequest _request, 
			ServletResponse _response,
			FilterChain chain) throws IOException, ServletException;
}
