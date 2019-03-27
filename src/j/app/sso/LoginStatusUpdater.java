package j.app.sso;

import j.cache.JCacheUpdater;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;

import java.util.List;

/**
 * 
 * @author 肖炯
 *
 */
public class LoginStatusUpdater implements JCacheUpdater{
	private static final long serialVersionUID = 1L;
	private ConcurrentList keys=new ConcurrentList();
	private static final Object lock=new Object();

	/**
	 * 
	 *
	 */
	public LoginStatusUpdater() {
		super();
	}
	
	/**
	 * 
	 * @param status
	 */
	public void addKey(LoginStatus status){
		synchronized(lock){
			if(status==null) return;
			if(!keys.contains(status.getGlobalSessionId())) keys.add(status.getGlobalSessionId());
			if(!keys.contains(status.getUserId())) keys.add(status.getUserId());
		}
	}
	
	/**
	 * 
	 */
	public void doUpdate(){
		synchronized(lock){
			SSOClient.updateLoginStatus(this);
			SSOServer.updateLoginStatus(this);
			
			keys.clear();
		}
	}

	@Override
	public void update(ConcurrentMap map) throws Exception {
		List values=map.listValues();
		for(int i=0;i<values.size();i++){
			LoginStatus stat=(LoginStatus)values.get(i);
			if(keys.contains(stat.getGlobalSessionId())
					||keys.contains(stat.getUserId())){
				stat.update();
			}
		}
		values.clear();
		values=null;
	}

	@Override
	public void updateCollection(ConcurrentMap collection) throws Exception {
		
	}

	@Override
	public void update(ConcurrentList list) throws Exception {
		//nothing to do
	}

	@Override
	public void updateCollection(ConcurrentList list) throws Exception {
		
	}
}
