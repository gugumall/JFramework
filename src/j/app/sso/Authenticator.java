package j.app.sso;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;



/**
 * @author 肖炯
 *
 * 处理用户登录，基于登录框架的应用必须实现此接口
 */
public interface Authenticator extends Serializable{
	/**
	 * 
	 * @param request
	 * @param session
	 * @param clientIp
	 * @return
	 * @throws Exception
	 */
	public LoginResult login(HttpServletRequest request,HttpSession session,String clientIp) throws Exception;
	
	/**
	 * 注销
	 * @param session
	 * @throws Exception
	 */
	public void logout(HttpSession session)throws Exception;
}
