package j.app.webserver;

import j.log.Logger;
import j.sys.SysConfig;
import j.sys.SysUtil;

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
 * 捕获请求，如果是预定义的action，则转发到controller
 * @author JStuido
 *
 */
public class Router implements Filter{	
	private static Logger log=Logger.create(Router.class);
	
	/**
	 * 
	 *
	 */
	public Router() {
		super();
	}

	/*
	 *  (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		
	}

	/*
	 *  (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		
	}

	/*
	 *  (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest=(HttpServletRequest)request;
		HttpServletResponse httpResponse=(HttpServletResponse)response;
		
		String requestURI=httpRequest.getRequestURI();	
		if(requestURI.endsWith(".jhtml")){
			SysUtil.forwardI18N(httpRequest,httpResponse,"/WEB-INF/pages"+requestURI.replaceAll(".jhtml",".jsp"));
			return;
		}else if(requestURI.endsWith(".jsp")){
			SysUtil.forwardI18N(httpRequest,httpResponse,"/WEB-INF/pages"+requestURI);
			return;
		}
		
		if(Handlers.isActionPath(requestURI)!=null){
			String path=requestURI.substring(0,requestURI.lastIndexOf("."));
			String pattern=requestURI.substring(requestURI.lastIndexOf("."));
			Handler handler=Handlers.getHandler(path);
			if(handler!=null&&handler.getPathPattern().equals(pattern)){
				Server.service(handler,httpRequest,httpResponse);
				return;
			}
		}
		
		try{
			chain.doFilter(request,response);
		}catch(Exception e){
			log.log("errors occur on url:"+SysUtil.getRequestURL(httpRequest),Logger.LEVEL_ERROR);
			log.log(e,Logger.LEVEL_ERROR);
			SysUtil.redirect(httpRequest,httpResponse,SysConfig.errorPage);
		}
	}
}
