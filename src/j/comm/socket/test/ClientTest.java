package j.comm.socket.test;

import java.net.Socket;

import j.comm.socket.ClientBase;
import j.util.JUtilMath;

/**
 * 
 * @author 肖炯
 *
 * 2019年4月3日
 *
 * <b>功能描述</b>
 */
public class ClientTest extends ClientBase{
	/**
	 * 
	 * @param socket
	 * @param maxIdle
	 */
	public ClientTest(Socket socket,long maxIdle) {
		super(socket,maxIdle);
	}
	
	@Override
	public void onConnect() throws Exception{
		System.out.println(this.addr.getHostAddress()+" connected.");
	}

	@Override
	public void onReceive(Object received) throws Exception{
		byte[] bytes=(byte[])received;
		System.out.println("receive data from "+this.toString()+": "+JUtilMath.bytesToString(bytes, true, 16, true));
	}
	
	@Override
	public void onClose() throws Exception{
		System.out.println(this.addr.getHostAddress()+" closed.");
	}
	
	@Override
	public void onError() throws Exception{
		System.out.println(this.addr.getHostAddress()+" error occurred.");
	}

	@Override
	public void send(Object data) throws Exception{
		out.write("Bingo".getBytes());
		out.flush();
	}
}
