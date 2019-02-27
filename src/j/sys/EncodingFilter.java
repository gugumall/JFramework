package j.sys;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * 
 * @author 肖炯
 *
 */
public class EncodingFilter implements Filter{	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest=(HttpServletRequest)request;
		HttpServletResponse httpResponse=(HttpServletResponse)response;	
		
		if(!httpRequest.getRequestURI().endsWith(".service")){
			httpRequest.setCharacterEncoding(SysConfig.sysEncoding);
		}		
		if(SysConfig.needSettingResponseEncoding(httpRequest.getRequestURI())){
			httpResponse.setContentType("text/html;charset="+SysConfig.sysEncoding);
		}
		
		/**
		 * doFilter
		 */
		try{
			chain.doFilter(request,response);
		}catch(Exception e){}
	}
	

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig conf) throws ServletException {		
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {		
	}
}