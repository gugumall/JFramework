package j.service.router;

import j.app.webserver.JHandler;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * 路由功能的实现类。
 * 接受服务注册、卸载请求，并监测服务状态。
 * 路由节点通过服务的集群编码将同一编码的服务视为一个集群，通过监测维护每个集群中可用服务的集合。
 * 当接受客户节点获取服务的请求时，路由节点会通过客户节点提供的服务编码（即有集群编码）从对应服务集群中获取一个服务实例（通过负载均衡机制）给客户节点。
 * @author JFramework
 *
 */
public abstract class JRouterAbstract extends JHandler implements JRouter,Serializable {	
	/**
	 * 
	 * @throws RemoteException
	 */
	public JRouterAbstract() throws RemoteException {
		super();// invoke rmi linking and remote object initialization
	}
}
