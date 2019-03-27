package j.cache;

import java.io.Serializable;

import j.util.JUtilSorter;

/**
 * 用于查询、添加/更新/删除缓存中对象的参数集合，缓存模块其它源码中统称“缓存操作参数”
 * @author 肖炯
 *
 */
public class JCacheParams implements Serializable{
	private static final long serialVersionUID = 1L;
	public Object key;//缓存单元为Map时，如果该条件不为null，则仅当key-value中的key与该条件一致时为符合缓存操作参数
	public Object value;//如果该条件不为null，则仅当Map中key-value的value、List中的对象与该条件一致是为符合缓存操作参数
	public int index=-1;//根据对象在缓存单元中所处的位置进行查找
	public int fromIndex=-1;//查找一个子集时，指定对象在缓存单元中的起始位置（包含），仅对List类型缓存单元有效
	public int toIndex=-1;//查找一个子集时，指定对象在缓存单元中的结束位置（不包含），仅对List类型缓存单元有效
	public JCacheFilter keyFilter;//根据key进行匹配，仅对Map类型缓存单元有效，详见JCacheFilter
	public JCacheFilter valueFilter;//根据value进行匹配，详见JCacheFilter
	public JUtilSorter sorter;//指定排序器对符合条件的集合继续排序
	public String sortType;//排序方式，升序（JUtilSorter.ASC）、或降序（JUtilSorter.DESC）
	public int recordsPerPage=0;//分页-每页记录数
	public int pageNum=0;//分页-第几页
	public JCacheUpdater updater;//指定更新器对缓存单元进行自定义的更新
	public JCacheUpdater collectionUpdater;//指定更新器对缓存单元中的特定集合进行自定义的更新
	
	/**
	 * 
	 */
	public JCacheParams(){
	}
	
	/**
	 * 设置缓存操作参数：索引
	 * @param index 索引
	 */
	public JCacheParams(int index){
		this.index=index;
	}
	
	/**
	 * 设置缓存操作参数：起始索引
	 * @param fromIndex 开始索引（包含）
	 * @param toIndex 结束索引（不包含）
	 */
	public JCacheParams(int fromIndex,int toIndex){
		this.fromIndex=fromIndex;
		this.toIndex=toIndex;
	}
	
	/**
	 * 设置缓存操作参数：key或者值过滤器
	 * @param keyOrValueFilter 如果为JCacheFilter实例则指定为valueFilter,否则指定为key
	 */
	public JCacheParams(Object keyOrValueFilter){
		if(keyOrValueFilter!=null&&(keyOrValueFilter instanceof JCacheFilter)){
			this.valueFilter=(JCacheFilter)keyOrValueFilter;
		}else{
			this.key=keyOrValueFilter;
		}
	}
	
	/**
	 * 设置缓存操作参数：value
	 * @param value 
	 * @param nothing 无实际意义，仅用来标记是对value赋值
	 */
	public JCacheParams(Object value,int nothing){
		this.value=value;
	}
	
	/**
	 * 设置缓存操作参数：过滤器
	 * @param filter 过滤器
	 * @param type 过滤器类型 key 或 value，默认为valueFilter
	 */
	public JCacheParams(JCacheFilter filter,String type){
		if("key".equalsIgnoreCase(type)) this.keyFilter=filter;
		else this.valueFilter=filter;
	}
}
