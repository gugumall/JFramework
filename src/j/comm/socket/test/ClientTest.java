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
	public void onClose() throws Exception{
		System.out.println(this.addr.getHostAddress()+" closed.");
	}
	
	@Override
	public void onError() throws Exception{
		System.out.println(this.addr.getHostAddress()+" error occurred.");
	}
	
	@Override
	public Object receive() throws Exception{
		byte[] data=new byte[in.available()];
		in.read(data);
		System.out.println("receive data from "+this.toString()+": "+JUtilMath.bytesToString(data, true, 16, false));
		
		return "Bingo";
	}

	@Override
	public void respond(Object received) throws Exception{
		out.write(((String)received).getBytes());
		out.flush();
	}
}
