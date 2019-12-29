package j.comm.websocket;

/**
 * 
 * @author 肖炯
 *
 * 2019年12月29日
 *
 * <b>功能描述</b> 包装Websocket客户端对象，提供心跳和自动重连机制
 */
public class ClientContainer implements Runnable{
	private Client client=null;
	private String clientClass=null;
	private String serverUri=null;
	private boolean reconnect=false;
	
	/**
	 * 
	 * @param clientClass
	 * @param serverUri
	 * @param reconnect 是否自动重连
	 */
	public ClientContainer(String clientClass, String serverUri, boolean reconnect) throws Exception{
		this.client=(Client)Class.forName(clientClass).getConstructor(new Class[] {String.class}).newInstance(new Object[] {serverUri});
		
		this.clientClass=clientClass;
		this.serverUri=serverUri;
		this.reconnect=reconnect;
		
		Thread thread=new Thread(this);
		thread.start();
	}
	
	/**
	 * 
	 * @return
	 */
	synchronized public Client getClient() {
		return this.client;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getServerUri() {
		return this.serverUri;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getReconnect() {
		return this.reconnect;
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	synchronized public void reconnect() throws Exception{
		this.client=(Client)Class.forName(clientClass).getConstructor(new Class[] {String.class}).newInstance(new Object[] {serverUri});
	}

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(5000);
			}catch(Exception e) {}
		
			try {
				if(client==null) break;
				
				//如果已经断开连接且设置为自动重连
				if(client.disconnected() && this.getReconnect()) {
					this.reconnect();
				}
				
				client.heartbeat();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
