package j.I18N;

import j.sys.SysConfig;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author JStuido
 *
 */
public class I18NFilter implements Filter{    
    /*
     *  (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {  
    	HttpServletRequest httpRequest=(HttpServletRequest)request;    	
    	HttpServletResponse httpResponse=(HttpServletResponse)response;
    	try{
			HttpSession session=httpRequest.getSession(true);
	
			if(!I18N.enabled||!I18N.need(httpRequest)){
	    		chain.doFilter(request,response);
	    		return;
	    	}
			
			I18N.changeLanguage(httpRequest,session);
			
			String requestedUri=httpRequest.getRequestURI();	
	    	
			I18NResponseWrapper wrapper = new I18NResponseWrapper((HttpServletResponse)response);
			wrapper.resetBuffer(); 
			request.getRequestDispatcher(requestedUri).include(request,wrapper);
			
			String content = wrapper.getContent();  
			int start=content.indexOf("<div class=\"I18N-GROUP\">");
			int end=content.indexOf("</div>",start);
			if(start>0&&end>start){//页面中指定了多语言分组
				String group=content.substring(start+"<div class=\"I18N-GROUP\">".length(),end);
				content=I18N.convert(content,group,session);
			}else{
				content=I18N.convert(content,httpRequest,session);
			}
		       
	    	//重置响应输出的内容长度
			response.setContentLength(-1);
	    	
	    	//设置编码格式
	    	if(requestedUri.endsWith(".js")){
	    		response.setContentType("application/javascript");
	    	}else if(requestedUri.endsWith(".css")){
	    		response.setContentType("text/css");
	    	}else{
	    		response.setContentType("text/html");
	    	}
	    	response.setCharacterEncoding(SysConfig.sysEncoding);
	    	
	    	//输出最终的结果
	    	PrintWriter out = response.getWriter();
			out.print(content);
			out.flush();
    	}catch(Exception e){
    		//java.io.FileNotFoundException: The requested resource (/games/sports/js/Special/Straight.js) is not available
    		if(e instanceof java.io.FileNotFoundException){
    			httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
    		}
    	}
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
    
    /**
     * 
     * @param arg
     */
    public static void main(String[] arg){
    	String content="<TITLE>卡地亚手表(腕表) CARTIER 欧美风大盘女款中性款 - V选商城 - 我们只出售少量精品</TITLE>";
    	content=content.replaceAll("<title>[\\S\\s]{0,}</title>", "<title>哈哈哈</title>");
    	content=content.replaceAll("<TITLE>[\\S\\s]{0,}</TITLE>", "<title>哈哈哈</title>");
    	System.out.println(content);
    }
}  
