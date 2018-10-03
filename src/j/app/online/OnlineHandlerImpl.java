package j.app.online;

import j.app.sso.User;

import java.io.IOException;

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
public class OnlineHandlerImpl implements OnlineHandler{
	/*
	 * (non-Javadoc)
	 * @see j.app.online.OnlineHandler#adjustUrl(javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	public String adjustUrl(HttpSession session,HttpServletRequest request,String url){
		return url;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.app.online.OnlineHandler#canPass(javax.servlet.http.HttpServletRequest)
	 */
	public boolean canPass(HttpServletRequest request) {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.app.online.OnlineHandler#canPass(javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest)
	 */
	public boolean canPass(HttpSession session,HttpServletRequest request){
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.app.online.OnlineHandler#onManySessionsOnIp(java.lang.String)
	 */
	public void onManySessionsOnIp(String ip) {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.app.online.OnlineHandler#onInit(j.app.online.Online, j.app.sso.User, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest)
	 */
	public void onInit(Online online, 
			User user, 
			HttpSession session,
			HttpServletRequest request) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.app.online.OnlineHandler#onLogin(j.app.online.Online, j.app.sso.User, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest)
	 */
	public void onLogin(Online online, 
			User user, 
			HttpSession session,
			HttpServletRequest request) {
		//nothing to do by default
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.app.online.OnlineHandler#onLogout(j.app.online.Online, j.app.sso.User, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest)
	 */
	public void onLogout(Online online, 
			User user, 
			HttpSession session,
			HttpServletRequest request) {
		//nothing to do by default
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.app.online.OnlineHandler#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest _request, 
			ServletResponse _response,
			FilterChain chain) throws IOException, ServletException {
		//nothing but...
		try{
			chain.doFilter(_request,_response);
		}catch(Exception e){}
	}
}
