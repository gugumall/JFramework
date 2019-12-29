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
	
	public Client() {
		super();
	}
	
	public Client(URI serverUri) {
		super(serverUri);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		connected=true;
		
	}

	@Override
	public void onMessage(String message) {
		
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		disconnected=true;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getConnected() {
		return this.connected;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getDisconnected() {
		return this.disconnected;
	}
	
	/**
	 * 
	 * @return
	 */
	synchronized public boolean heartbeat() {
		if(this.getConnected() && !this.getDisconnected()) {
			this.send("{\"heartbeat\":\""+System.currentTimeMillis()+"\"}");
			return true;
		}else {
			return false;
		}
	}

	@Override
	public void onError(Exception ex) {
		
	}
}
