package j.app.webserver.demo;

import j.app.webserver.JHandler;
import j.app.webserver.JSession;
import j.sys.SysUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author JFramework
 *
 */
public class DemoHandler extends JHandler{
	/**
	 * constructor
	 *
	 */
	public DemoHandler() {
		super();
	}


	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void test11(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws Exception {
		jsession.resultString="hello";
	}


	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void test22(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String type=SysUtil.getHttpParameter(request,"type");
		String error=SysUtil.getHttpParameter(request,"error");
		if(error!=null){
			throw new Exception("这是抛出异常的情况");
		}
		if(type==null){
			jsession.setIsBackToGlobalNavigation(true);
			jsession.result="error";
		}else if("jsp".equalsIgnoreCase(type)){
			request.setAttribute("msg","ok!!!");
			jsession.result="ok";
		}else{
			request.setAttribute("msg","err!!!");
			jsession.result="err";
		}
	}
}
