package j.service.hello;

import j.app.webserver.JSession;
import j.service.server.ServiceBase;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 每个服务接口类都必须继承ServiceBaseInterface
 * @author 肖炯
 *
 */
public interface JHello extends ServiceBase{
	/**
	 *  http接口实现
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void hello(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws Exception;
	
	/**
	 * 每个方法的前两个参数是必须且固定不变的
	 * @param clientUuid  客户节点传过来的它的uuid
	 * @param md54Service 客户节点传过来的md5校验串
	 * @param words
	 * @param times
	 * @return
	 * @throws RemoteException
	 */
	public String hello(String clientUuid, String md54Service,String words,int times) throws RemoteException;
}
