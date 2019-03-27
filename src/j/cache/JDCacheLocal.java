package j.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import j.log.Logger;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilList;

/**
 * 
 * @author 肖炯
 *
 */
public class JDCacheLocal extends JCache implements Runnable{
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.create(JDCacheLocal.class);	
	protected ConcurrentMap<String,JCacheUnit> units=new ConcurrentMap<String,JCacheUnit>();//key-缓存单元ID，value-缓存单元

	/**
	 * 
	 *
	 */
	public JDCacheLocal() {
		super();
		
		Thread thread=new Thread(this);
		thread.start();
		log.log("JDCacheLocal monitor thread started.",-1);
	}
	
	/**
	 * 
	 * @param cacheId
	 * @return
	 * @throws Exception
	 */
	private JCacheUnit checkStatus(String cacheId) throws Exception{
		if(cacheId==null){
			throw new Exception("the cache id is null.");
		}
		
		if(units.get(cacheId)==null){
			throw new Exception("the cache unit is not exists.");
		}
		
		return (JCacheUnit)units.get(cacheId);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#create(java.lang.String, int, int)
	 */
	public void createUnit(String cacheId, int unitType, int lifeCircleType) throws Exception {		
		if(units.containsKey(cacheId)) return;
		if(unitType==JCache.UNIT_MAP){
			units.put(cacheId,new JCacheUnitMap(lifeCircleType));
		}else if(unitType==JCache.UNIT_LIST){
			units.put(cacheId,new JCacheUnitList(lifeCircleType));
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#setActiveTime(java.lang.String)
	 */
	public void setActiveTime(String cacheId) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		unit.using();
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addOne(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void addOne(String cacheId, Object key, Object value) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		unit.addOne(key,value);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addAll(java.lang.String, java.util.Map)
	 */
	public void addAll(String cacheId, Map mappings) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		unit.addAll(mappings);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addOne(java.lang.String, java.lang.Object)
	 */
	public void addOne(String cacheId, Object value) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		unit.addOne(value);	
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addAll(java.lang.String, java.util.Collection)
	 */
	public void addAll(String cacheId, Collection values) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);
		unit.addAll(values);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#addOneIfNotContains(java.lang.String, java.lang.Object)
	 */
	public void addOneIfNotContains(String cacheId, Object value) throws Exception{
		JCacheUnit unit=checkStatus(cacheId);	
		unit.addOneIfNotContains(value);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#contains(java.lang.String, j.cache.JCacheParams)
	 */
	public boolean contains(String cacheId, JCacheParams jdcParams) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		return unit.contains(jdcParams);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#size(java.lang.String)
	 */
	public int size(String cacheId) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		return unit.size();
	}

	/*
	 * (non-Javadoc)
	 * @see j.cache.JCache#size(java.lang.String, j.cache.JCacheParams)
	 */
	public int size(String cacheId, JCacheParams jdcParams) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		return unit.size(jdcParams);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#get(java.lang.String, j.cache.JCacheParams)
	 */
	public Object get(String cacheId, JCacheParams jdcParams) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		return unit.get(jdcParams);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#remove(java.lang.String, j.cache.JCacheParams)
	 */
	public void remove(String cacheId, JCacheParams jdcParams) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		unit.remove(jdcParams);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#clear(java.lang.String)
	 */
	public void clear(String cacheId) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		unit.clear();		
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#update(java.lang.String, j.cache.JCacheParams)
	 */
	public void update(String cacheId, JCacheParams jdcParams) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		unit.update(jdcParams);
	}

	/*
	 * (non-Javadoc)
	 * @see j.cache.JCache#updateCollection(java.lang.String, j.cache.JCacheParams)
	 */
	public void updateCollection(String cacheId, JCacheParams jdcParams) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		unit.updateCollection(jdcParams);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#sub(java.lang.String, j.cache.JCacheParams)
	 */
	public Object sub(String cacheId, JCacheParams jdcParams) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		return unit.sub(jdcParams);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#keys(java.lang.String, j.cache.JCacheParams)
	 */
	public ConcurrentList keys(String cacheId, JCacheParams jdcParams) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		return unit.keys(jdcParams);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCache#values(java.lang.String, j.cache.JCacheParams)
	 */
	public ConcurrentList values(String cacheId, JCacheParams jdcParams) throws Exception {
		JCacheUnit unit=checkStatus(cacheId);	
		return unit.values(jdcParams);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		while(true){
			try{
				Thread.sleep(30000);
			}catch(Exception e){}
			
			//清除过期未使用的临时缓存单元
			try{
				List keys=units.listKeys();
				for(int i=0;i<keys.size();i++){
					Object key=keys.get(i);
					JCacheUnit unit=(JCacheUnit)units.get(key);
					if(unit.isTimeout()){
						unit.clear();
						units.remove(key);
						unit=null;
					}
				}
				JUtilList.clear_AllNull(keys);
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
		}
	}
}