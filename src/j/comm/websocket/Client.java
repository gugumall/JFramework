package j.comm.websocket;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * 
 * @author 肖炯
 *
 * 2019年12月29日
 *
 * <b>功能描述</b>
 */
public class Client extends WebSocketClient{
	protected boolean connected=false;
	protected boolean disconnected=false;
	
	public Client(URI serverUri) {
		super(serverUri);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		connected=true;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		disconnected=true;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean connected() {
		return this.connected;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean disconnected() {
		return this.disconnected;
	}
	
	/**
	 * 
	 * @return
	 */
	synchronized public boolean heartbeat() {
		if(this.connected && this.disconnected) {
			this.send("{\"heartbeat\":\""+System.currentTimeMillis()+"\"}");
			return true;
		}else {
			return false;
		}
	}

	@Override
	public void onError(Exception ex) {
		// TODO Auto-generated method stub
		
	}
}
