package j.cache;

import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilList;
import j.util.JUtilMap;
import j.util.JUtilSorter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 肖炯
 *
 */
public class JCacheUnitMap extends JCacheUnit{
	private static final long serialVersionUID = 1L;	
	private ConcurrentMap container=null;
	
	/**
	 * 
	 * @param lifeCircleType
	 * @throws Exception
	 */
	public JCacheUnitMap(int lifeCircleType) throws Exception{
		this.lifeCircleType=lifeCircleType;
		this.container=new ConcurrentMap();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#getUnitType()
	 */
	public int getUnitType(){
		return JCache.UNIT_MAP;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.cache.JCacheUnit#addOne(java.lang.Object, java.lang.Object)
	 */
	public void addOne(Object key,Object value) throws Exception{
		using();
		container.put(key,value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.cache.JCacheUnit#addAll(java.util.Map)
	 */
	public void addAll(Map mappings) throws Exception{
		using();
		this.container.putAll(mappings);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#contains(j.cache.JCacheParams)
	 */
	public boolean contains(JCacheParams params) throws Exception{
		using();
		
		if(params==null||(params.key==null&&params.value==null&&params.keyFilter==null&&params.valueFilter==null)){
			return false;
		}
		
		if(params.key!=null) return container.containsKey(params.key);
		
		if(params.value!=null) return container.containsValue(params.value);
		
		if(params.keyFilter!=null){//是否有匹配指定key的
			List keys=container.listKeys();
			for(int i=0;i<keys.size();i++){
				Object key=(Object)keys.get(i);
				if(params.keyFilter.matches(key)){
					JUtilList.clear_AllNull(keys);
					return true;
				}
			}
			JUtilList.clear_AllNull(keys);
		}
		
		if(params.valueFilter!=null){//是否有匹配指定value的
			List keys=container.listKeys();
			for(int i=0;i<keys.size();i++){
				Object key=(Object)keys.get(i);
				Object value=(Object)container.get(key);
				if(params.valueFilter.matches(value)){
					JUtilList.clear_AllNull(keys);
					return true;
				}
			}
			JUtilList.clear_AllNull(keys);
		}
		
		return false;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#size()
	 */
	public int size() throws Exception{
		using();
		
		return container.size();
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.cache.JCacheUnit#size(j.cache.JCacheParams)
	 */
	public int size(JCacheParams params) throws Exception{
		return this.values(params).size();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#get(j.cache.JCacheParams)
	 */
	public Object get(JCacheParams params) throws Exception{
		using();
		
		if(params==null
				||(params.key==null&&params.keyFilter==null&&params.valueFilter==null&&params.index<0)){
			return null;
		}

		if(params.key!=null) return container.get(params.key);
		
		if(params.index>-1){
			List values=container.listValues();
			return values.get(params.index);
		}
		
		if(params.keyFilter!=null){//是否有匹配指定key的
			List keys=container.listKeys();
			for(int i=0;i<keys.size();i++){
				Object key=(Object)keys.get(i);
				if(params.keyFilter.matches(key)){
					keys.clear();
					keys=null;
					return container.get(key);
				}
			}
			keys.clear();
			keys=null;
		}
		
		if(params.valueFilter!=null){//是否有匹配指定value的
			List keys=container.listKeys();
			for(int i=0;i<keys.size();i++){
				Object key=(Object)keys.get(i);
				Object value=(Object)container.get(key);
				if(params.valueFilter.matches(value)){
					keys.clear();
					keys=null;
					return value;
				}
			}
			keys.clear();
			keys=null;
		}
		
		return null;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#remove(j.cache.JCacheParams)
	 */
	public void remove(JCacheParams params) throws Exception{
		using();
		
		if(params==null||(params.key==null&&params.keyFilter==null&&params.valueFilter==null)){
			throw new Exception("no valid operating params.");
		}
		
		if(params.key!=null){
			container.remove(params.key);
			return;
		}
		
		if(params.keyFilter!=null){//是否有匹配指定key的
			List keys=container.listKeys();
			for(int i=0;i<keys.size();i++){
				Object key=(Object)keys.get(i);
				if(params.keyFilter.matches(key)) container.remove(key);
			}
			JUtilList.clear_AllNull(keys);
		}
		
		if(params.valueFilter!=null){//是否有匹配指定value的
			List keys=container.listKeys();
			for(int i=0;i<keys.size();i++){
				Object key=(Object)keys.get(i);
				Object value=(Object)container.get(key);
				if(params.valueFilter.matches(value)) container.remove(key);
			}
			JUtilList.clear_AllNull(keys);
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
		using();
		
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
		using();
		
		if(params==null||(params.collectionUpdater==null)){
			throw new Exception("no valid operating params.");
		}
		
		params.collectionUpdater.updateCollection(container);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#sub(j.cache.JCacheParams)
	 */
	public Object sub(JCacheParams params) throws Exception{
		using();
		
		JCacheFilter keyFileter=params==null?null:params.keyFilter;
		JCacheFilter valueFilter=params==null?null:params.valueFilter;
		
		ConcurrentMap mappings=container.snapshot();
		
		if(keyFileter==null&&valueFilter==null) return mappings;
		
		List keys=mappings.listKeys();
		for(int i=0;i<keys.size();i++){
			Object key=keys.get(i);
			Object value=mappings.get(key);
			
			boolean matches=true;
			if(keyFileter!=null&&!keyFileter.matches(key)){
				matches=false;
			}else if(valueFilter!=null&&!valueFilter.matches(value)){
				matches=false;
			}
			if(!matches) mappings.remove(key);
		}		
		keys.clear();
		keys=null;
		
		mappings.setTotal(mappings.size());
		if(params.recordsPerPage>0&&params.pageNum>0){//分页
			int start=params.recordsPerPage*(params.pageNum-1);
			int to=params.recordsPerPage*params.pageNum;
			
			if(start>=0){
				if(mappings.size()>start){
					mappings=JUtilMap.subConcurrentMap(mappings,start,to>mappings.size()?mappings.size():to);
				}else{
					mappings.clear();
				}
			}
		}
		
		return mappings;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#keys(j.cache.JCacheParams)
	 */
	public ConcurrentList keys(JCacheParams params) throws Exception{
		using();
		
		JCacheFilter keyFileter=params==null?null:params.keyFilter;
		JCacheFilter valueFilter=params==null?null:params.valueFilter;
		int recordsPerPage=params==null?0:params.recordsPerPage;
		int pageNum=params==null?0:params.pageNum;
	
		ConcurrentMap mappings=container.snapshot();

		List keys=mappings.listKeys();
		if(keyFileter!=null||valueFilter!=null){	
			for(int i=0;i<keys.size();i++){
				Object key=keys.get(i);
				Object value=mappings.get(key);
				
				boolean remove=false;
				if(keyFileter!=null&&!keyFileter.matches(key)){
					remove=true;
				}else if(valueFilter!=null&&!valueFilter.matches(value)){
					remove=true;
				}
				if(remove){
					keys.remove(i);
				}
			}
		}
		mappings.clear();
		mappings=null;
		

		ConcurrentList result=new ConcurrentList();
		result.setTotal(keys.size());
		if(recordsPerPage>0&&pageNum>0){//分页
			int start=recordsPerPage*(pageNum-1);
			int to=recordsPerPage*pageNum;
			
			if(start>=0){
				if(keys.size()>start){
					keys=JUtilList.subConcurrentList(keys,start,to>keys.size()?keys.size():to);
				}else{
					keys.clear();
				}
			}
		}
		
		result.addAll(keys);
		return result;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#values(j.cache.JCacheParams)
	 */
	public ConcurrentList values(JCacheParams params) throws Exception{
		using();
		
		JCacheFilter keyFileter=params==null?null:params.keyFilter;
		JCacheFilter valueFilter=params==null?null:params.valueFilter;
		JUtilSorter sorter=params==null?null:params.sorter;
		String sortType=params==null?null:params.sortType;
		int recordsPerPage=params==null?0:params.recordsPerPage;
		int pageNum=params==null?0:params.pageNum;
		
		ConcurrentMap mappings=container.snapshot();

		List values=null;
		if(keyFileter!=null||valueFilter!=null){	
			values=new ConcurrentList();
			
			List keys=mappings.listKeys();
			for(int i=0;i<keys.size();i++){
				Object key=keys.get(i);
				Object value=mappings.get(key);
				
				boolean remove=false;
				if(keyFileter!=null&&!keyFileter.matches(key)){
					remove=true;
				}else if(valueFilter!=null&&!valueFilter.matches(value)){
					remove=true;
				}
				if(!remove){
					values.add(value);
				}
			}
			keys.clear();
			keys=null;
		}else{
			values=mappings.listValues();
		}
		mappings.clear();
		mappings=null;
		
		if(sorter!=null){//排序
			values=sorter.mergeSort(values,sortType);		
		}
		
		ConcurrentList result=new ConcurrentList();
		result.setTotal(values.size());
		if(recordsPerPage>0&&pageNum>0){//分页
			int start=recordsPerPage*(pageNum-1);
			int to=recordsPerPage*pageNum;
			
			if(start>=0){
				if(values.size()>start){
					values=JUtilList.subConcurrentList(values,start,to>values.size()?values.size():to);
				}else{
					values.clear();
				}
			}
		}
		
		result.addAll(values);
		return result;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addOne(java.lang.Object)
	 */
	public void addOne(Object value) throws Exception {
		throw new Exception("Not Supported.");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addOneIfNotContains(java.lang.Object)
	 */
	public void addOneIfNotContains(Object value) throws Exception{
		throw new Exception("Not Supported.");
	}

	/*
	 *  (non-Javadoc)
	 * @see j.cache.JCacheUnit#addAll(java.util.Collection)
	 */
	public void addAll(Collection values) throws Exception {
		throw new Exception("Not Supported.");
	}
}
