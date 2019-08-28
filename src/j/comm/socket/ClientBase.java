package j.comm.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import j.log.Logger;
import j.sys.SysUtil;
import j.util.JUtilUUID;

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

	protected Server server=null;
	protected String uuid=null;//唯一ID
	protected Socket socket;
	protected InetAddress addr;
	protected long maxIdle;//最大空闲时间，超过自动关闭连接，单位ms
	protected long mustSendAfterConnectedWithin;//建立连接后多久内必须发生交互，否则关闭连接，单位ms
	protected Object[] args;//自定义参数
	protected long createAt;
	protected long lastActive;//最近交互时间，单位ms
	protected long interactions=0;//交互次数
	protected long lastValidCommunication=0;//最近有效交互时间
	protected boolean end=false;//是否已经结束
	
	/**
	 * 
	 */
	public ClientBase() {
		this.uuid=JUtilUUID.genUUID();
		lastActive=SysUtil.getNow();
		createAt=SysUtil.getNow();
	}
	
	/**
	 * 
	 * @param socket 客户端socket
	 * @param maxIdle 最大空闲时间，超过此时间未收到客户端消息将强制关闭连接，单位毫秒
	 */
	public ClientBase(Socket socket,long maxIdle) {
		this.uuid=JUtilUUID.genUUID();
		this.socket=socket;
		this.addr=socket.getInetAddress();
		this.mustSendAfterConnectedWithin=3000;//默认三秒
		this.maxIdle=maxIdle;
		lastActive=SysUtil.getNow();
		createAt=SysUtil.getNow();
	}
	
	/**
	 * 
	 * @param socket 客户端socket
	 * @param maxIdle 最大空闲时间，超过此时间未收到客户端消息将强制关闭连接，单位毫秒
	 * @param mustSendAfterConnectedWithin 建立连接后多久内必须发生交互，否则关闭连接，单位ms
	 */
	public ClientBase(Socket socket,long maxIdle,long mustSendAfterConnectedWithin) {
		this.uuid=JUtilUUID.genUUID();
		this.socket=socket;
		this.addr=socket.getInetAddress();
		this.mustSendAfterConnectedWithin=mustSendAfterConnectedWithin;
		this.maxIdle=maxIdle;
		lastActive=SysUtil.getNow();
		createAt=SysUtil.getNow();
	}
	
	/**
	 * 
	 * @param socket 客户端socket
	 * @param maxIdle 最大空闲时间，超过此时间未收到客户端消息将强制关闭连接，单位毫秒
	 * @param mustSendAfterConnectedWithin 建立连接后多久内必须发生交互，否则关闭连接，单位ms
	 * @param args 自定义业务参数
	 */
	public ClientBase(Socket socket,long maxIdle,long mustSendAfterConnectedWithin,Object[] args) {
		this.uuid=JUtilUUID.genUUID();
		this.socket=socket;
		this.addr=socket.getInetAddress();
		this.mustSendAfterConnectedWithin=mustSendAfterConnectedWithin;
		this.maxIdle=maxIdle;
		this.args=args;
		lastActive=SysUtil.getNow();
		createAt=SysUtil.getNow();
	}
	
	/**
	 * 
	 * @param server
	 */
	public void setServer(Server server) {
		this.server=server;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUuid() {
		return this.uuid;
	}
	
	/**
	 * 获得区分于其它客户端的ID，特定业务中可能需要根据此ID来获得此Client对象，并通过其与客户端进行交互
	 * @return
	 */
	public String getId() {
		return this.addr.getHostAddress();
	}
	
	/**
	 * 
	 * @return
	 */
	public InetAddress getAddress() {
		return this.addr;
	}
	
	/**
	 * 
	 * @return
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * 是否禁用Nagle's algorithm
	 * @return
	 */
	public boolean disableNagleAlgorithm() {
		return false;
	}
	
	/**
	 * 当连接上时
	 * @throws Exception
	 */
	public void onConnect() throws Exception{
	}
	
	/**
	 * 当收到信息时
	 * @throws Exception
	 */
	public void onReceive(Object data) throws Exception{
	}
	
	/**
	 * 当关闭连接时
	 * @throws Exception
	 */
	public void onClose() throws Exception{
		//if(server.getDebug()) {
			log.log("关闭连接：("+socket.getInetAddress().getHostName()+":"+socket.getLocalPort()+","+socket.getPort()+","+getUuid()+")", -1);
		//}
	}
	
	/**
	 * 当发生异常时
	 * @throws Exception
	 */
	public void onError() throws Exception{
		//if(server.getDebug()) {
			log.log("连接异常：("+addr.getHostName()+":"+socket.getLocalPort()+","+socket.getPort()+","+getUuid()+") 是否空闲:"+isIdle()+", 连接后是否超时未收到数据："+notActiveAfterConnectedWithin(), -1);
		//}
	}
	
	/**
	 * 连接
	 * @throws Exception
	 */
	public void connect() throws Exception{
		this.onConnect();
	}
	
	/**
	 * 
	 * @return
	 */
	public OutputStream getOutputStream() {
		try {
			return socket==null?null:socket.getOutputStream();
		}catch(Exception e) {
			log.log(e,Logger.LEVEL_ERROR);
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public InputStream getInputStream() {
		try {
			return socket==null?null:socket.getInputStream();
		}catch(Exception e) {
			log.log(e,Logger.LEVEL_ERROR);
			return null;
		}
	}
	
	/**
	 * 是否可以开始读取客户端发送过来的数据
	 * @return
	 * @throws Exception
	 */
	public boolean readyToRead() throws Exception{
		InputStream in=this.getInputStream();
		if(in==null) return false;
		return in.available()>0;
	}
	
	/**
	 * 接收客户端发送过来的数据
	 * @return
	 * @throws Exception
	 */
	public void receive() throws Exception{
		byte[] data=null;
		synchronized(this) {
			InputStream in=this.getInputStream();
			if(in==null) return;
			
			data=new byte[in.available()];
			in.read(data);
		}
		
		if(data!=null) this.onReceive(data);
	}
	
	/**
	 * 发送内容可客户端
	 * @param data 需要发送的内容
	 * @throws Exception
	 */
	public void send(Object data) throws Exception{
		
	}
	
	/**
	 * 是否超过最大空闲时间
	 * @return
	 */
	public boolean isIdle() {
		return SysUtil.getNow()-this.lastActive>this.maxIdle;
	}

	
	/**
	 * 是否建立连接后在规定时间内未发生交互
	 * @return
	 */
	public boolean notActiveAfterConnectedWithin() {
		return interactions==0&&SysUtil.getNow()-this.lastActive>this.mustSendAfterConnectedWithin;
	}	
	
	/**
	 * 
	 * @param force 是否强制关闭
	 * @return
	 */
	protected boolean end(boolean force) {
		synchronized(this) {
			if(this.end) return true;
			
			if(!this.isIdle()
					&&!this.notActiveAfterConnectedWithin()
					&&!force) return false;//未满足两个需关闭连接的条件之一，且不是强制关闭
			
			try {
				this.onClose();
			}catch(Exception ex) {
				log.log(ex, Logger.LEVEL_ERROR);
			}
			
			this.end=true;
			try {
				this.socket.close();
				this.socket=null;
			}catch(Exception e) {
				log.log(e, Logger.LEVEL_ERROR);
			}
			
			//从client列表中移除
			server.removeClient(this);
			
			return true;
		}
	}
	
	/**
	 * 
	 */
	public void setLastValidCommunication() {
		this.lastValidCommunication=SysUtil.getNow();
	}
	
	/**
	 * 
	 * @return
	 */
	public long getLastValidCommunication() {
		return this.lastValidCommunication;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getCreateAt() {
		return createAt;
	}

	@Override
	public void run() {
		try {
			this.connect();
			
			while(!end) {
				try {
					Thread.sleep(100);
				}catch(Exception e) {}
				
				try {					
					synchronized(this) {	
						if(end) break;
						
						if(!this.readyToRead()) continue;
						
						//记录最新活动时间
						this.lastActive=SysUtil.getNow();
						
						//交互次数
						interactions++;
					}
					
					//接收
					this.receive();
				}catch(Exception e) {
					log.log(e, Logger.LEVEL_ERROR);
					try {
						this.onError();
					}catch(Exception ex) {
						log.log(ex, Logger.LEVEL_ERROR);
					}
				}
			} 
		}catch(Exception e) {
			log.log(e, Logger.LEVEL_ERROR);
			try {
				this.onError();
			}catch(Exception ex) {
				log.log(ex, Logger.LEVEL_ERROR);
			}
		}
	}
	
	@Override
	public String toString() {
		return this.addr.getHostAddress();
	}
}
