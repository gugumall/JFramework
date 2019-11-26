package j.app.webserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import j.app.Constants;
import j.http.JHttpContext;
import j.log.Logger;
import j.sys.SysConfig;
import j.sys.SysUtil;

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
		HttpSession session=httpRequest.getSession(true);	
		String requestURI=httpRequest.getRequestURI();	
		String requestURL=SysUtil.getRequestURL(httpRequest);

		
		String requestFrom=SysUtil.getHttpParameter(httpRequest, Constants.J_ACTION_RESPONSER_FROM);
		
		if(Handlers.isResponserClusterAction(requestURL)) {//处理需要群发的请求
			List<JResponser> responsers=Handlers.getResponsers();
			for(int i=0; i<responsers.size(); i++) {
				try {
					JHttpContext httpContext=responsers.get(i).call(session, httpRequest, requestURI, null);
					if(httpContext==null || httpContext.getStatus()!=200) {
						try {
							SysUtil.outHttpResponse(httpResponse, new JResponse(false, "invalid_http_response_state", "I{同步调用远程站点失败}", session).toString());
						}catch(Exception ex) {}
					}
				}catch(Exception e) {
					log.log(e, Logger.LEVEL_ERROR);
					try {
						SysUtil.outHttpResponse(httpResponse, new JResponse(false, "call_remote_failed", "I{同步调用远程站点出错}", session).toString());
					}catch(Exception ex) {}
					return;
				}
			}
		}else if(requestFrom==null) {//处理发往远程节点的调用
			String responserId=(String)session.getAttribute(Constants.J_ACTION_RESPONSER);
			JResponser responser=responserId==null?null:Handlers.getResponser(responserId);
			
			//指定了当前操作的远程节点，且当前uri需要调用远程节点
			if(responser!=null && responser.matches(requestURI)) {
				try {
					JHttpContext httpContext=responser.call(session, httpRequest, requestURI, null);
					String resp=httpContext.getResponseText();
					httpContext.finalize();
					httpContext=null;
					
					//重置响应输出的内容长度
					response.setContentLength(-1);
			    	
			    	//设置编码格式
			    	if(requestURI.endsWith(".js")){
			    		response.setContentType("application/javascript");
			    	}else if(requestURI.endsWith(".css")){
			    		response.setContentType("text/css");
			    	}else{
			    		response.setContentType("text/html");
			    	}
			    	response.setCharacterEncoding(SysConfig.sysEncoding);
			    	
			    	//输出最终的结果
			    	PrintWriter out = response.getWriter();
					out.print(resp);
					out.flush();
				}catch(Exception e) {
					log.log(e, Logger.LEVEL_ERROR);
					try {
						SysUtil.outHttpResponse(httpResponse, new JResponse(false, "call_remote_failed", "I{调用远程站点出错}", session).toString());
					}catch(Exception ex) {}
				}
				
				return;
			}
		}
		//处理发往远程节点的调用 end
		
		
		if(requestURI.endsWith(".jhtml")){
			SysUtil.forwardI18N(httpRequest,httpResponse,"/WEB-INF/pages"+requestURI.replaceAll(".jhtml",".jsp"));
			return;
		}else if(requestURI.endsWith(".jsp")){
			SysUtil.forwardI18N(httpRequest,httpResponse,"/WEB-INF/pages"+requestURI);
			return;
		}
		
		Handler handler=null;
		String pattern=Handlers.isActionPath(requestURI);
		if(pattern!=null){
			if(requestURI.endsWith(pattern)){//常规方式
				String path=requestURI.substring(0,requestURI.indexOf(pattern));
				handler=Handlers.getHandler(path);
			}else{//RESTful方式
				handler=Handlers.getHandler(requestURI);
			}
		}
		if(handler!=null){
			Server.service(handler,httpRequest,httpResponse);
			return;
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
