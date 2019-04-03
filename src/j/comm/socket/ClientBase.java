package j.comm.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import j.log.Logger;
import j.sys.SysUtil;
import j.util.JUtilMath;

/**
 * 
 * @author 肖炯
 *
 * 2019年3月30日
 *
 * <b>功能描述</b> 当启动一个Server在指定端口监听客户端连接时，必须指定一个类来处理特定业务相关的客户端交互，该类必须是ClientBase的子类
 */
public class ClientBase implements Runnable{
	private static Logger log=Logger.create(ClientBase.class);
	
	protected Socket socket;
	protected InetAddress addr;
	protected InputStream in;
	protected OutputStream out;
	protected long maxIdle;
	protected long lastActive;
	protected boolean end=false;
	
	/**
	 * 
	 * @param socket 客户端socket
	 * @param maxIdle 最大空闲时间，超过此时间未收到客户端消息将强制关闭连接，单位毫秒
	 */
	public ClientBase(Socket socket,long maxIdle) {
		this.socket=socket;
		this.addr=socket.getInetAddress();
		this.maxIdle=maxIdle;
		lastActive=SysUtil.getNow();
	}
	
	/**
	 * 当连接上时
	 * @throws Exception
	 */
	public void onConnect() throws Exception{
	}
	
	/**
	 * 当关闭连接时
	 * @throws Exception
	 */
	public void onClose() throws Exception{
	}
	
	/**
	 * 当发生异常时
	 * @throws Exception
	 */
	public void onError() throws Exception{
	}
	
	/**
	 * 连接
	 * @throws Exception
	 */
	public void connect() throws Exception{
		in = socket.getInputStream();
		out = socket.getOutputStream();
		
		this.onConnect();
	}
	
	/**
	 * 是否可以开始读取客户端发送过来的数据
	 * @return
	 * @throws Exception
	 */
	public boolean readyToRead() throws Exception{
		return in.available()>0;
	}
	
	/**
	 * 接收客户端发送过来的数据
	 * @return
	 * @throws Exception
	 */
	public Object receive() throws Exception{
		byte[] data=new byte[in.available()];
		in.read(data);
		log.log("receive data from "+this.toString()+": "+JUtilMath.bytesToString(data, true, 16, true), -1);
		
		return data;
	}
	
	/**
	 * 根据收到的内容响应客户端
	 * @param received 收到的内容
	 * @throws Exception
	 */
	public void respond(Object received) throws Exception{
		out.write((byte[])received);
		out.flush();
	}
	
	/**
	 * 是否超过最大空闲时间
	 * @return
	 */
	private boolean isIdle() {
		return SysUtil.getNow()-this.lastActive>this.maxIdle;
	}	
	
	/**
	 * 
	 * @param force 是否强制关闭
	 * @return
	 */
	protected boolean end(boolean force) {
		synchronized(this) {
			if(!this.isIdle()&&!force) return false;//空闲为超过最大允许时间，且不是强制关闭
			
			this.end=true;
			try {
				this.socket.close();
				this.socket=null;
			}catch(Exception e) {
				log.log(e, Logger.LEVEL_ERROR);
			}
			
			try {
				this.onClose();
			}catch(Exception ex) {
				log.log(ex, Logger.LEVEL_ERROR);
			}
			
			return true;
		}
	}

	@Override
	public void run() {
		try {
			while(!end) {
				synchronized(this) {
					if(end) break;
					
					try {
						Thread.sleep(10);
					}catch(Exception e) {}
					
					if(!this.readyToRead()) continue;
					
					//记录最新活动时间
					this.lastActive=SysUtil.getNow();
					
					//接收
					Object received=this.receive();
					
					//响应
					this.respond(received);
				}
			} 
		}catch(Exception e) {
			log.log(e, Logger.LEVEL_ERROR);
			try {
				this.onError();
			}catch(Exception ex) {
				log.log(ex, Logger.LEVEL_ERROR);
			}
			
			this.end(true);
		}
	}
	
	@Override
	public String toString() {
		return this.addr.getHostAddress();
	}
}
