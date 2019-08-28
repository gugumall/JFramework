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
	private long mustSendAfterConnectedWithin;
	private int maxClients=100;
	private int maxClientsPerIp=1;
	private int soTimeout=3000;
	private Object[] args=null;
	private ConcurrentList<ClientBase> clients=new ConcurrentList<ClientBase>();
	private ServerSocket serverSocket=null;
	private Monitor monitor=null;
	private boolean debug=false;
	
	/**
	 * 
	 * @param port 端口
	 * @param client 处理客户端交互的类
	 * @param clientMaxIdle 最大空闲时间，超过此时间未收到客户端消息将强制关闭连接，单位毫秒
	 * @param mustSendAfterConnectedWithin 建立连接后多久内必须发生交互，否则关闭连接，单位ms
	 * @param maxClients 最大同时连接客户端数
	 * @param maxClientsPerIp 每个IP最大同时连接客户端数
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static Server start(Integer port,Class client,long clientMaxIdle,long mustSendAfterConnectedWithin,int maxClients,int maxClientsPerIp,int soTimeout,Object[] args) throws Exception{
		Server instance=(Server)servers.get(port);
		if(instance!=null) return instance;
		
		//纠正参数
		if(clientMaxIdle<=0) clientMaxIdle=30000;
		if(mustSendAfterConnectedWithin<=0) mustSendAfterConnectedWithin=3000;
		if(maxClients<=0) maxClients=1;
		if(maxClientsPerIp<=0) maxClientsPerIp=1;
		if(soTimeout<=0) soTimeout=3000;
		
		//启动服务端socket
		instance =new Server(port,client,clientMaxIdle,mustSendAfterConnectedWithin,maxClients,maxClientsPerIp,soTimeout,args);
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
	 * @deprecated
	 * @param port 端口
	 * @param client 处理客户端交互的类
	 * @param clientMaxIdle 最大空闲时间，超过此时间未收到客户端消息将强制关闭连接，单位毫秒
	 * @param mustSendAfterConnectedWithin 建立连接后多久内必须发生交互，否则关闭连接，单位ms
	 * @param maxClients 最大同时连接客户端数
	 * @param maxClientsPerIp 每个IP最大同时连接客户端数
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static Server start(Integer port,Class client,long clientMaxIdle,long mustSendAfterConnectedWithin,int maxClients,int maxClientsPerIp,Object[] args) throws Exception{
		return start(port,client,clientMaxIdle,mustSendAfterConnectedWithin,maxClients,maxClientsPerIp,3000,args);
	}
	
	/**
	 * 
	 * @param debug
	 */
	public void setDebug(boolean debug) {
		this.debug=debug;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getDebug() {
		return this.debug;
	}

	/**
	 * 
	 * @param port
	 * @param client
	 * @param clientMaxIdle
	 * @param mustSendAfterConnectedWithin
	 * @param maxClients
	 * @param maxClientsPerIp
	 * @param soTimeout
	 * @param args
	 */
	private Server(Integer port,Class client,long clientMaxIdle,long mustSendAfterConnectedWithin,int maxClients,int maxClientsPerIp,int soTimeout,Object[] args) {
		this.port=port;
		this.client=client;
		this.clientMaxIdle=clientMaxIdle;
		this.mustSendAfterConnectedWithin=mustSendAfterConnectedWithin;
		this.maxClients=maxClients;
		this.maxClientsPerIp=maxClientsPerIp;
		this.args=args;
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
		return clients.snapshot();
	}
	
	/**
	 * 返回ID为指定值的Client，如有多个ID相同的，返回最后建立的那个
	 * @param id
	 * @return
	 */
	public ClientBase getClient(String id) {
		ClientBase client=null;
		for(int i=0;i<clients.size();i++) {
			ClientBase c=(ClientBase)clients.get(i);
			if(c.getId().equals(id)) client=c;
		}
		return client;
	}
	

	/**
	 * 返回ID为指定值的Client，如有多个ID相同的，返回最后有效交互的那个
	 * @param id
	 * @return
	 */
	public ClientBase getClientLatestComm(String id) {
		ClientBase client=null;
		for(int i=0;i<clients.size();i++) {
			ClientBase c=(ClientBase)clients.get(i);
			if(c.getId().equals(id)) {
				if(client==null
						||c.getLastValidCommunication()>client.getLastValidCommunication()) client=c;
			}
		}
		return client;
	}
	
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public ClientBase getClientOfUuid(String uuid) {
		ClientBase client=null;
		for(int i=0;i<clients.size();i++) {
			ClientBase c=(ClientBase)clients.get(i);
			if(c.getUuid().equals(uuid)) client=c;
		}
		return client;
	}
	
	/**
	 * 
	 * @param client
	 */
	public void removeClient(ClientBase client) {
		for(int i=0;i<clients.size();i++) {
			ClientBase c=(ClientBase)clients.get(i);
			if(c.getUuid().equals(client.getUuid())) {
				clients.remove(i);
				return;
			}
		}
	}
	
	/**
	 * 关闭指定ID的连接，除去指定uuid
	 * @param id
	 * @param exceptUuid
	 */
	public void closeClient(String id,String exceptUuid) {
		for(int i=0;i<clients.size();i++) {
			ClientBase c=(ClientBase)clients.get(i);
			if(c.getId().equals(id)
					&&(exceptUuid==null||!exceptUuid.equals(c.getUuid()))) {
				clients.remove(i);
				c.end(true);
				i--;
			}
		}
	}
	
	/**
	 * 关闭指定ID且建立时间在指定uuid之前的连接
	 * @param id
	 * @param exceptUuid
	 */
	public void closeClientBefore(String id,String beforeUuid) {
		ClientBase client=getClientOfUuid(beforeUuid);
		for(int i=0;i<clients.size();i++) {
			ClientBase c=(ClientBase)clients.get(i);
			if(c.getId().equals(id)
					&&(client==null||c.getCreateAt()<client.getCreateAt())) {
				clients.remove(i);
				c.end(true);
				i--;
			}
		}
	}
	
	/**
	 * 
	 * @param ip
	 * @return
	 */
	private int clientOnIP(String ip) {
		int count=0;
		ConcurrentList clients=getClients();
		for(int i=0;i<clients.size();i++) {
			ClientBase c=(ClientBase)clients.get(i);
			if(c.getAddress().getHostAddress().equals(ip)) count++;
		}
		return count;
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
					
					//同一IP上的连接超出最大允许数
					if(clientOnIP(socket.getInetAddress().getHostAddress())>=this.maxClientsPerIp) {
						if(debug) {
							log.log("同一IP上的连接超出最大允许数（"+this.maxClientsPerIp+"），关闭连接：("+socket.getInetAddress().getHostName()+":"+socket.getLocalPort()+","+socket.getPort()+")", -1);
						}
						socket.close();
					}else {
						socket.setSoTimeout(this.soTimeout);
						
						ClientBase client=(ClientBase)this.getClient().getConstructor(new Class[] {Socket.class,long.class,long.class,Object[].class}).newInstance(new Object[] {socket,this.getClientMaxIdle(),this.mustSendAfterConnectedWithin,this.args});
						client.setServer(this);
						
						socket.setTcpNoDelay(client.disableNagleAlgorithm());
						
						//if(debug) {
							log.log("建立连接：("+socket.getInetAddress().getHostName()+":"+socket.getLocalPort()+","+socket.getPort()+","+client.getUuid()+",TCP_NO_DELAY:"+socket.getTcpNoDelay()+")", -1);
						//}
						
						Thread task=new Thread(client);
						task.start();
						
						clients.add(client);
					}
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
