package j.app.online;

import j.log.Logger;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;

import java.util.List;

/**
 * 
 * @author 肖炯
 *
 */
public class OnlineUpdater implements Runnable{
	private static Logger log=Logger.create(OnlineUpdater.class);
	private static ConcurrentList updaters=new ConcurrentList();
	private static volatile int selector=0;
	
	private ConcurrentMap tasks=new ConcurrentMap();
	
	/**
	 * 
	 */
	public static void startup(){
		while(updaters.size()<Onlines.getUpdaters()){
			OnlineUpdater updater=new OnlineUpdater();
			Thread thread=new Thread(updater);
			thread.start();
			updaters.add(updater);
			
			log.log("Online updater "+updaters.size()+" started.", -1);
		}
	}
	
	/**
	 * 
	 * @param online
	 */
	public static void update(Online online){
		if(online.getGlobalSessionId()!=null&&!"".equals(online.getGlobalSessionId())){
			for(int i=0;i<updaters.size();i++){
				OnlineUpdater updater=(OnlineUpdater)updaters.get(i);
				if(updater.tasks.containsKey(online.getGlobalSessionId())){
					updater.tasks.put(online.getGlobalSessionId(), online);
					return;
				}
			}
		}else{
			for(int i=0;i<updaters.size();i++){
				OnlineUpdater updater=(OnlineUpdater)updaters.get(i);
				if(updater.tasks.containsKey(online.getCurrentSessionId())){
					updater.tasks.put(online.getCurrentSessionId(), online);
					return;
				}
			}
		}
		
		if(selector>=updaters.size()){
			selector=0;
		}
		
		if(online.getGlobalSessionId()!=null&&!"".equals(online.getGlobalSessionId())){
			((OnlineUpdater)updaters.get(selector)).tasks.put(online.getGlobalSessionId(), online);
		}else{
			((OnlineUpdater)updaters.get(selector)).tasks.put(online.getCurrentSessionId(), online);
		}
		
		selector++;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			try{
				Thread.sleep(Onlines.getUpdateInterval());
			}catch(Exception ex){}
			
			try{
				List keys=tasks.listKeys();
				for(int i=0;i<keys.size();i++){
					String key=(String)keys.get(i);
					Online online=(Online)tasks.remove(key);
					if(online!=null){
						Onlines.update(online);
						online=null;
					}
				}
				keys.clear();
				keys=null;
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
		}
	}
}
