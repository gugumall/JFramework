package j.app.sso;

import j.log.Logger;
import j.sys.SysUtil;
import j.util.ConcurrentMap;

import java.util.List;

import javax.servlet.http.HttpSession;

/**
 * 
 * @author 肖炯
 *
 */
public class SSOContext implements Runnable{
	private static Logger log=Logger.create(SSOContext.class);
	private static ConcurrentMap sessions=new ConcurrentMap();
	private static ConcurrentMap tokens=new ConcurrentMap();

	static{
		SSOContext instance=new SSOContext();
		Thread thread=new Thread(instance);
		thread.start();
		log.log("SSOContext started",-1);
	}
	
	/**
	 * 
	 * @param globalSessionId
	 * @param token
	 * @param ssoUserId
	 */
	public static void addToken(String globalSessionId,String token,String ssoUserId){
		tokens.put(globalSessionId,new Object[]{token,new Long(System.currentTimeMillis()),ssoUserId});
	}
	
	/**
	 * 
	 * @param globalSessionId
	 * @return
	 */
	public static Object[] getToken(String globalSessionId){
		if(globalSessionId==null) return null;
		Object[] objs=(Object[])tokens.get(globalSessionId);
		return objs;
	}
	
	/**
	 * 
	 * @param globalSessionId
	 */
	public static void removeToken(String globalSessionId){
		if(globalSessionId==null) return;
		tokens.remove(globalSessionId);
	}
	
	/**
	 * 
	 * @param session
	 */
	public static void addSession(HttpSession session){
		if(!sessions.containsKey(session.getId())){
			sessions.put(session.getId(),session);
		}
	}
	
	/**
	 * 
	 * @param sessionId
	 * @return
	 */
	public static HttpSession getSession(String sessionId){
		return (HttpSession)sessions.get(sessionId);
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			try{
				Thread.sleep(15000);
			}catch(Exception ex){}
			
			//清除过期token（15秒）
			List keys=tokens.listKeys();
			for(int i=0;i<keys.size();i++){
				String key=(String)keys.get(i);
				Object[] objs=(Object[])tokens.get(key);
				Long time=(Long)objs[1];
				if(System.currentTimeMillis()-time>15000){
					tokens.remove(key);
				}
			}
			
			//清除过期session记录
			try{			
				long now=SysUtil.getNow();
				keys=sessions.listKeys();
				for(int i=0;i<keys.size();i++){
					Object key=keys.get(i);
					HttpSession session=(HttpSession)sessions.get(key);
					try{
						if(session==null||now-session.getLastAccessedTime()>SSOConfig.getSessionTimeout()*1000L){//超时
							sessions.remove(key);
							if(session!=null) session=null;
						}
					}catch(Exception e){
						sessions.remove(key);
						if(session!=null) session=null;
					}
				}
				
				keys.clear();
				keys=null;
			}catch(Exception ex){
				log.log(ex,Logger.LEVEL_ERROR);
			}
		}
	}
}
