package j.comm.socket;

import j.util.ConcurrentList;

/**
 * 
 * @author 肖炯
 *
 * 2019年3月30日
 *
 * <b>功能描述</b> 监控与Server建立连接的客户端，并在其超时时关闭
 */
public class Monitor implements Runnable{
	private Server server;
	
	/**
	 * 
	 * @param server
	 */
	public Monitor(Server server) {
		this.server=server;
	}
	
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(100);
			}catch(Exception e) {}
			
			try {
				ConcurrentList clients=server.getClients();
				for(int i=0;i<clients.size();i++) {
					ClientBase c=(ClientBase)clients.get(i);
					c.end(false);
				}
			}catch(Exception e) {}
		}
	}
}
