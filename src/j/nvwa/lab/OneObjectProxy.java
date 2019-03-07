package j.nvwa.lab;

import java.lang.reflect.Method;

import j.nvwa.NvwaProxy;

/**
 * 
 * @author ceo
 *
 */
public class OneObjectProxy extends NvwaProxy{
	/**
	 * 调用实际方法前执行的操作
	 * @param method
	 * @param args
	 * @return
	 */
	protected Object beforeInvoke(Method method, Object[] args){
		System.out.println("beforeInvoke...");
		return "beforeInvoke";
	}
	
	/**
	 * 调用实际方法后执行的操作
	 * @param method
	 * @param args
	 * @param returnValue
	 * @return
	 */
	protected Object afterInvoke(Method method, Object[] args,Object returnValue){
		System.out.println("afterInvoke...");
		return "afterInvoke";
	}
	
	/**
	 * 调用实际方法出错时执行的操作
	 * @param method
	 * @param args
	 * @param e
	 * @return
	 */
	protected Object onException(Method method, Object[] args,Exception e){
		System.out.println("onException...");
		e.printStackTrace();
		return "onException";
	}
}
