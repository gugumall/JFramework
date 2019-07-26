package j.comm.socket.test;

import j.comm.socket.Server;

/**
 * 
 * @author 肖炯
 *
 * 2019年4月3日
 *
 * <b>功能描述</b>
 */
public class Test {
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		Server server=Server.start(1900, ClientTest.class, 30000,3000,10,1,(Object[])null);
		
		
	}
}
