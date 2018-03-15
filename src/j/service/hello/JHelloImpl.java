package j.service.hello;

import j.app.webserver.JSession;
import j.service.Constants;
import j.sys.SysUtil;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 每个服务实现类都必须继承ServiceBase
 * @author JFramework
 *
 */
public class JHelloImpl extends JHelloAbstract {
	private static final long serialVersionUID = 1L;
	private int counter=0;
	private JHelloWords words;
	
	 
	/**
	 * 
	 * @throws RemoteException
	 */
	public JHelloImpl() throws RemoteException {
		super(); // invoke rmi linking and remote object initialization
	}
	
	/**
	 * 
	 * @param counter
	 */
	public void setCounter(int counter){
		this.counter=counter;
	}
	 
	/**
	 * 
	 * @return
	 */
	public int getCounter(){
		return this.counter;
	}
	
	/**
	 * 
	 * @param words
	 */
	public void setWords(JHelloWords words){
		this.words=words;
	}
	
	/**
	 * 
	 * @return
	 */
	public JHelloWords getWords(){
		return this.words;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.service.hello.HelloInterface#hello(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void hello(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws Exception {
		//如果需要认证，必须包含jservice_client_uuid,jservice_md5_4service两个参数
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		
		//每个服务方法都必须以这段代码开头
		try{
			auth(clientUuid,"hello",md54Service);//hello为本方法的名称
		}catch(RemoteException e){
			jsession.resultString=Constants.AUTH_FAILED;
		}
		//每个服务方法都必须以这段代码开头 end

		String pwords=SysUtil.getHttpParameter(request,"words");
		String ptimes=SysUtil.getHttpParameter(request,"times");
		int times=Integer.parseInt(ptimes);
		
		counter++;
		System.out.println(counter);
		for(int i=0;i<times;i++){
			System.out.println(pwords);
		}
		jsession.resultString="9999 http got your words:"+pwords;
	}

	/*
	 * 每个方法的前两个参数是必须且固定不变的
	 *  (non-Javadoc)
	 * @see j.service.hello.HelloInterface#hello(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public String hello(String clientUuid, String md54Service,String words,int times) throws RemoteException {
		//每个服务方法都必须以这段代码开头
		try{
			auth(clientUuid,"hello",md54Service);//hello为本方法的名称
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}
		//每个服务方法都必须以这段代码开头 end
		
		counter++;
		System.out.println(counter);
		for(int i=0;i<times;i++){
			System.out.println(words);
		}
		return "oooooo rmi got your words:"+words+" and I said - "+this.words.getWords();
	}
}