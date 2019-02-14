package j.cache;

import java.io.Serializable;

import j.util.JUtilSorter;

/**
 * 
 * @author 肖炯
 *
 */
public class JCacheParams implements Serializable{
	private static final long serialVersionUID = 1L;
	public Object key;
	public Object value;
	public int index=-1;
	public int fromIndex=-1;
	public int toIndex=-1;
	public JCacheFilter keyFilter;
	public JCacheFilter valueFilter;
	public JUtilSorter sorter;
	public String sortType;
	public int recordsPerPage=0;
	public int pageNum=0;
	public JCacheUpdater updater;
	public JCacheUpdater collectionUpdater;
	
	/**
	 * 
	 */
	public JCacheParams(){
	}
	
	/**
	 * 
	 * @param index
	 */
	public JCacheParams(int index){
		this.index=index;
	}
	
	/**
	 * 
	 * @param fromIndex
	 * @param toIndex
	 */
	public JCacheParams(int fromIndex,int toIndex){
		this.fromIndex=fromIndex;
		this.toIndex=toIndex;
	}
	
	/**
	 * 
	 * @param key or value filter
	 */
	public JCacheParams(Object key){
		if(key!=null&&(key instanceof JCacheFilter)){
			this.valueFilter=(JCacheFilter)key;
		}else{
			this.key=key;
		}
	}
	
	/**
	 * 
	 * @param value
	 * @param nothing 用来标记是对value赋值
	 */
	public JCacheParams(Object value,int nothing){
		this.value=value;
	}
	
	/**
	 * 
	 * @param keyFilter
	 * @param nothing
	 */
	public JCacheParams(JCacheFilter filter,String type){
		if("key".equalsIgnoreCase(type)) this.keyFilter=filter;
		else this.valueFilter=filter;
	}
}
