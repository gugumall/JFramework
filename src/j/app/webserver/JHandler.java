package j.app.webserver;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



/**
 * @author JFramework
 *
 * 实现该接口的业务处理类处理一个或多个用户请求，对应在actions.*.xml的一个<handler>
 */
public abstract class JHandler {	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void process(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String method=jsession.action.getMethod();

		Method handler=null;
		try{			
			handler=this.getClass().getDeclaredMethod(method, new Class<?>[]{JSession.class,HttpSession.class,HttpServletRequest.class,HttpServletResponse.class});
		}catch(Exception e){
			try{			
				handler=this.getClass().getMethod(method, new Class<?>[]{JSession.class,HttpSession.class,HttpServletRequest.class,HttpServletResponse.class});
			}catch(Exception ex){
				throw new Exception(this.getClass().getName()+" - 指定的方法不存在："+method);
			}
		}
		
		handler.invoke(this,new Object[]{jsession,session,request,response});
	}
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 */
	public void init(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response){
		//nothing to do by default.
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	public void finalize(){
	}
}
