package j.cache;

import j.util.ConcurrentList;
import j.util.JUtilList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 肖炯
 *
 */
public class JCacheUnitList extends JCacheUnit{
	private static final long serialVersionUID = 1L;	
	private ConcurrentList container=null;
	
	/**
	 * 
	 * @param lifeCircleType
	 * @throws Exception
	 */
	public JCacheUnitList(int lifeCircleType) throws Exception{
		this.lifeCircleType=lifeCircleType;
		this.container=new ConcurrentList();
	}
	

	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#getUnitType()
	 */
	public int getUnitType(){
		return JCache.UNIT_LIST;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#getLifeCircleType()
	 */
	public int getLifeCircleType(){
		return this.lifeCircleType;
	}
	
	/**
	 * 
	 * @param initializing
	 * @throws Exception
	 */
	private void checkStatus(boolean initializing) throws Exception{
		using();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addOne(java.lang.Object)
	 */
	public void addOne(Object value) throws Exception{
		addOne(value,false);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addOne(java.lang.Object, boolean)
	 */
	public void addOne(Object value,boolean initializing) throws Exception{
		checkStatus(initializing);
		container.add(value);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addOneIfNotContains(java.lang.Object)
	 */
	public void addOneIfNotContains(Object value) throws Exception{
		addOneIfNotContains(value,false);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addOneIfNotContains(java.lang.Object, boolean)
	 */
	public void addOneIfNotContains(Object value,boolean initializing) throws Exception{
		checkStatus(initializing);
		if(!container.contains(value)) container.add(value);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addAll(java.util.Collection)
	 */
	public void addAll(Collection values) throws Exception{
		addAll(values,false);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addAll(java.util.Collection, boolean)
	 */
	public void addAll(Collection values,boolean initializing) throws Exception{
		checkStatus(initializing);
		this.container.addAll(values);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#contains(j.cache.JCacheParams)
	 */
	public boolean contains(JCacheParams params) throws Exception{
		checkStatus(false);
		
		if(params==null||(params.value==null&&params.valueFilter==null)){
			throw new Exception("no valid operating params.");
		}
		
		if(params.value!=null) return container.contains(params.value);
		
		if(params.valueFilter!=null){
			for(int i=0;i<container.size();i++){
				if(params.valueFilter.matches(container.get(i))) return true;
			}
		}
		
		return false;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#size()
	 */
	public int size() throws Exception{
		checkStatus(false);
		
		return container.size();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#size()
	 */
	public int size(JCacheParams params) throws Exception{
		ConcurrentList values=(ConcurrentList)this.sub(params);
		return values==null?0:values.size();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#get(j.cache.JCacheParams)
	 */
	public Object get(JCacheParams params) throws Exception{
		checkStatus(false);
		
		if(params==null||(params.index<0&&params.valueFilter==null)){
			throw new Exception("no valid operating params.");
		}
		
		if(params.index>=0) return container.get(params.index);
		
		if(params.valueFilter!=null){
			for(int i=0;i<container.size();i++){
				if(params.valueFilter.matches(container.get(i))) return container.remove(i);
			}
		}
		
		return null;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#remove(j.cache.JCacheParams)
	 */
	public void remove(JCacheParams params) throws Exception{
		checkStatus(false);
		
		if(params==null||(params.index<0&&params.value==null&&params.valueFilter==null)){
			throw new Exception("no valid operating params.");
		}
		
		if(params.index>=0){
			container.remove(params.index);
			return;
		}
		
		if(params.value!=null){
			container.remove(params.value);
			return;
		}
		
		if(params.valueFilter!=null){
			for(int i=0;i<container.size();i++){
				if(params.valueFilter.matches(container.get(i))){
					container.remove(i);
					i--;
				}
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#clear()
	 */
	public void clear() throws Exception{
		container.clear();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#update(j.cache.JCacheParams)
	 */
	public void update(JCacheParams params) throws Exception{
		checkStatus(false);
		
		if(params==null||(params.updater==null)){
			throw new Exception("no valid operating params.");
		}
		
		params.updater.update(container);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.cache.JCacheUnit#updateCollection(j.cache.JCacheParams)
	 */
	public void updateCollection(JCacheParams params) throws Exception{
		throw new Exception("Not Supported.");
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#sub(j.cache.JCacheParams)
	 */
	public Object sub(JCacheParams params) throws Exception{
		checkStatus(false);
		
		ConcurrentList values=container.snapshot();
		
		if(params!=null){
			if(params.valueFilter!=null){//按条件过滤		
				for(int i=0;i<values.size();i++){
					Object value=values.get(i);
					
					if(!params.valueFilter.matches(value)){
						values.remove(i);
						i--;
					}
				}		
			}
			
			if(params.fromIndex>=0&&params.toIndex>params.fromIndex){//指定起始位置
				int start=params.fromIndex;
				int to=params.toIndex;
				if(start>=0){
					if(values.size()>start){
						values=JUtilList.subConcurrentList(values,start,to>values.size()?values.size():to);
					}else{
						values.clear();
					}
				}
			}
			
			if(params.sorter!=null){//排序
				List temp=params.sorter.mergeSort(values,params.sortType);	
				values.clear();
				values.addAll(temp);
			}

			values.setTotal(values.size());
			if(params.recordsPerPage>0&&params.pageNum>0){//分页
				int start=params.recordsPerPage*(params.pageNum-1);
				int to=params.recordsPerPage*params.pageNum;
				
				if(start>=0){
					if(values.size()>start){
						values=JUtilList.subConcurrentList(values,start,to>values.size()?values.size():to);
					}else{
						values.clear();
					}
				}
			}
		}
		
		return values;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addOne(java.lang.Object, java.lang.Object)
	 */
	public void addOne(Object key, Object value) throws Exception {
		throw new Exception("NIY");
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addOne(java.lang.Object, java.lang.Object, boolean)
	 */
	public void addOne(Object key, Object value, boolean initializing) throws Exception {
		throw new Exception("Not Supported.");
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addAll(java.util.Map)
	 */
	public void addAll(Map mappings) throws Exception {
		throw new Exception("Not Supported.");
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addAll(java.util.Map, boolean)
	 */
	public void addAll(Map mappings, boolean initializing) throws Exception {
		throw new Exception("Not Supported.");
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#keys(j.cache.JCacheParams)
	 */
	public ConcurrentList keys(JCacheParams params) throws Exception {
		throw new Exception("Not Supported.");
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#values(j.cache.JCacheParams)
	 */
	public ConcurrentList values(JCacheParams params) throws Exception {
		throw new Exception("Not Supported.");
	}
}
