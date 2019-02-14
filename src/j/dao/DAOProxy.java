package j.dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import j.util.JUtilString;

/**
 * @author 肖炯
 *
 */
public class DAOProxy implements InvocationHandler{
	private DAO dao;
	
	public Object bind(DAO _dao,DAOFactory _factory){
		dao=_dao;
		return Proxy.newProxyInstance(RdbmsDao.class.getClassLoader(),RdbmsDao.class.getInterfaces(),this);
	}
	
	public Object rebind(){
		return Proxy.newProxyInstance(RdbmsDao.class.getClassLoader(),RdbmsDao.class.getInterfaces(),this);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String methodName=method.getName();
		
		DBMirror m=dao.getMirror();
		if(m!=null){
			if(!m.readable){
				if(methodName.startsWith("find")
						||methodName.indexOf("getMax")>-1
						||methodName.indexOf("getMin")>-1
						||methodName.indexOf("getSum")>-1
						||methodName.equals("getRecordCnt")){
					throw new Exception("this mirror can't be read.");
				}
			}
			
			if(dao.getReadOnly()||!m.insertable){
				if(methodName.startsWith("insert")){
					throw new Exception("this mirror can't be inserted.");
				}
			}
			
			if(dao.getReadOnly()||!m.updatable){
				if(methodName.startsWith("update")||methodName.startsWith("execute")){
					throw new Exception("this mirror can't be updated.");
				}
			}
		}
		
		if(!JUtilString.contain(new String[]{"getTimeout","isClosed","isInTransaction","isUsing","getLastUsingTime"},methodName)){
			dao.begin();
			dao.beforeAnyInvocation();
		}
		Object returnValue=null;
		try{
			returnValue=method.invoke(dao,args);
			if(!JUtilString.contain(new String[]{"getTimeout","isClosed","isInTransaction","isUsing","getLastUsingTime"},methodName)){
				dao.afterAnyInvocation();
				dao.finish();
			}
		}catch(Exception e){
			dao.onException();
			if(!JUtilString.contain(new String[]{"getTimeout","isClosed","isInTransaction","isUsing","getLastUsingTime"},methodName)){
				dao.afterAnyInvocation();
				dao.finish();
			}
			throw e;
		}
		return returnValue;
	}
}
