package j.service.hello;

import j.service.Manager;
import j.service.client.Client;
import j.util.JUtilUUID;

import java.util.HashMap;
import java.util.Map;

public class HelloClient implements Runnable{
	/**
	 * 
	 * @throws Exception
	 */
	public static void test() throws Exception {
		HelloClient cl=new HelloClient();
		Thread th=new Thread(cl);
		th.start();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {		
		try{
			Thread.sleep(30000);
		}catch(Exception e){}
		System.out.println("try to call service.");

		//可以通过http调用		
		try{				
			Map paras=new HashMap();
			paras.put("words","uuid - "+JUtilUUID.genUUID());
			paras.put("times","4");
			
			String entrance=Client.httpGetService(null,null,"HelloService",true);
			String result=Client.httpCallPost(null,null,"HelloService",entrance,"hello",paras);
			System.out.println("result2:"+result);			
		}catch(Exception e){
			e.printStackTrace();
		}

		//可以通过rmi调用	
		try{			
			JHello h=(JHello)Client.rmiGetService("HelloService",true);
			String result=h.hello(Manager.getClientNodeUuid(),Client.md54Service("HelloService","hello"),"hooooooo",2);
			System.out.println("result3:"+result);			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试
	 * @param args
	 */
	public static void main(String args[]) throws Exception{
		test();
	}
}
