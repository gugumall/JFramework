package j.nvwa;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 
 * @author 肖炯
 *
 */
public class NvwaProxy implements InvocationHandler{
	private Object realBody;//实际要调用的对象
	
	/**
	 * 调用实际方法前执行的操作
	 * @param method
	 * @param args
	 * @return
	 */
	protected Object beforeInvoke(Method method, Object[] args){
		return null;
	}
	
	/**
	 * 调用实际方法后执行的操作
	 * @param method
	 * @param args
	 * @param returnValue
	 * @return
	 */
	protected Object afterInvoke(Method method, Object[] args,Object returnValue){
		return null;
	}
	
	/**
	 * 调用实际方法出错时执行的操作
	 * @param method
	 * @param args
	 * @param e
	 * @return
	 */
	protected Object onException(Method method, Object[] args,Exception e){
		return null;
	}
	
	/**
	 * 
	 * @param realBody
	 * @return
	 */
	public Object bind(Object realBody){
		this.realBody=realBody;
		return Proxy.newProxyInstance(realBody.getClass().getClassLoader(),realBody.getClass().getInterfaces(),this);
	}
	
	/**
	 * 
	 * @return
	 */
	public Object getRealObject(){
		return this.realBody;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object returnValue=null;
		try{
			this.beforeInvoke(method,args);
			returnValue=method.invoke(this.realBody,args);
			this.afterInvoke(method,args,returnValue);
		}catch(Exception e){
			this.onException(method,args,e);
			throw e;
		}
		return returnValue;
	}
}