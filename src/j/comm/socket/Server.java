package j.comm.socket;

import java.net.ServerSocket;
import java.net.Socket;

import j.log.Logger;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;

/**
 * 
 * @author 肖炯
 *
 * 2019年3月30日
 *
 * <b>功能描述</b>
 */
public class Server implements Runnable{
	private static Logger log=Logger.create(Server.class);
	private static ConcurrentMap servers=new ConcurrentMap();

	private Integer port;
	private Class client;
	private long clientMaxIdle;
	private int maxClients=100;
	private ConcurrentList<ClientBase> clients=new ConcurrentList<ClientBase>();
	private ServerSocket serverSocket=null;
	private Monitor monitor=null;
	
	/**
	 * 
	 * @param port 端口
	 * @param client 处理客户端交互的类
	 * @param clientMaxIdle 最大空闲时间，超过此时间未收到客户端消息将强制关闭连接，单位毫秒
	 * @param maxClients 最大同时连接客户端数
	 * @return
	 * @throws Exception
	 */
	public static Server start(Integer port,Class client,long clientMaxIdle,int maxClients) throws Exception{
		Server instance=(Server)servers.get(port);
		if(instance!=null) return instance;
		
		//纠正参数
		if(clientMaxIdle<=0) clientMaxIdle=30000;
		
		//启动服务端socket
		instance =new Server(port,client,clientMaxIdle);
		Thread serverThread=new Thread(instance);
		serverThread.start();
		
		//启动监控线程
		instance.monitor =new Monitor(instance);
		Thread monitorThread=new Thread(instance.monitor);
		monitorThread.start();
		
		servers.put(port,instance);
		
		return instance;
	}
	
	/**
	 * 
	 * @param port
	 * @param client
	 * @param clientMaxIdle
	 */
	private Server(Integer port,Class client,long clientMaxIdle) {
		this.port=port;
		this.client=client;
		this.clientMaxIdle=clientMaxIdle;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getPort() {
		return this.port;
	}
	
	/**
	 * 
	 * @return
	 */
	public Class getClient() {
		return this.client;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getClientMaxIdle() {
		return this.clientMaxIdle;
	}
	
	/**
	 * 
	 * @return
	 */
	public ConcurrentList getClients() {
		return clients;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public ClientBase getClient(String id) {
		for(int i=0;i<clients.size();i++) {
			ClientBase c=(ClientBase)clients.get(i);
			if(c.getId().equals(id)) return c;
		}
		return null;
	}

	@Override
	public void run() {
		try {
			log.log("try to listen on port "+this.getPort(),-1);
			this.serverSocket = new ServerSocket(this.getPort());
			while(true) {
				try {
					while(this.maxClients>0&&this.clients.size()>=this.maxClients) {//超出允许最大连接数
						try {
							Thread.sleep(100);
						}catch(Exception e) {}
					}
					
					Socket socket=serverSocket.accept();
					
					ClientBase client=(ClientBase)this.getClient().getConstructor(new Class[] {Socket.class,long.class}).newInstance(new Object[] {socket,this.getClientMaxIdle()});
					Thread task=new Thread(client);
					task.start();
					
					clients.add(client);
				}catch(Exception e) {
					log.log(e, Logger.LEVEL_ERROR);
				}
				
				try {
					Thread.sleep(10);
				}catch(Exception e) {}
			}
		}catch(Exception e) {
			log.log(e, Logger.LEVEL_ERROR);
		}
	}
}
