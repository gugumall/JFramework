package j.util;


import j.common.JObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author 肖炯
 *
 */
public class ConcurrentMap<K,V> implements Map<K,V>,Serializable{
	private static final long serialVersionUID = 1L;
	private JObject lock=null;
	private Map container=null;//实际数据存储对象
	private int total=-1;//当container为某个子集时，total表示父集元素总数
	
	/**
	 * 
	 *
	 */
	public ConcurrentMap(){
		lock=new JObject();
		container=new LinkedHashMap<K,V>();
	}
	
	/**
	 * 同步锁，即实际存储数据的LinkedList对象，当外部程序需要与该LinkedList对象的操作同步的话，需通过getLock()得到锁
	 * @return
	 */
	public Object getLock(){
		return lock;
	}
	
	/**
	 * 
	 * @param total
	 */
	public void setTotal(int total){
		this.total=total;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getTotal(){
		return this.total==-1?this.size():this.total;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear(){
		synchronized(lock){
			container.clear();
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key){
		synchronized(lock){
			return container.containsKey(key);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value){
		synchronized(lock){
			return container.containsValue(value);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet(){
		synchronized(lock){
			return container.entrySet();
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public V get(Object key){
		synchronized(lock){
			return (V)container.get(key);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty(){
		synchronized(lock){
			return container.isEmpty();
		}
	}	

	/*
	 *  (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set keySet(){
		synchronized(lock){
			return container.keySet();
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object,java.lang.Object)
	 */
	public V put(Object key,Object value){
		synchronized(lock){
			return (V)container.put(key,value);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map mappings){
		synchronized(lock){
			container.putAll(mappings);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public V remove(Object key){
		synchronized(lock){
			return (V)container.remove(key);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size(){
		synchronized(lock){
			return container.size();
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection values(){
		synchronized(lock){
			return container.values();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ConcurrentList listKeys(){
		synchronized(lock){
			ConcurrentList keys=new ConcurrentList();
			keys.addAll(container.keySet());
			return keys;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ConcurrentList listValues(){
		synchronized(lock){
			ConcurrentList values=new ConcurrentList();
			values.addAll(container.values());
			return values;
		}
	}	
	
	/**
	 * 移除cache中的全部元素但保留leftKeys所包含的key
	 * @param leftKeys
	 */
	public void removeExcept(List leftKeys){
		List keys=listKeys();
		for(int i=0;i<keys.size();i++){
			Object key=keys.get(i);
			if(!leftKeys.contains(key)){
				remove(key);
			}
		}
	}	
	
	/**
	 * 
	 * @return
	 */
	public ConcurrentMap snapshot() {
		ConcurrentMap _snapshot=new ConcurrentMap();
		synchronized(lock){
			_snapshot.putAll(container);
		}
		return _snapshot;
	}
	
	/**
	 * 
	 * @return
	 */
	public String toXml(){
		StringBuffer sb=new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		sb.append("<root>\r\n");
		List keys=listKeys();
		for(int i=0;i<keys.size();i++){
			Object key=keys.get(i);
			Object val=get(key);
			
			sb.append("<"+key+"><![CDATA["+val+"]]></"+key+">\r\n");	
		}
		sb.append("</root>");
		return sb.toString();
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		ConcurrentMap m=new ConcurrentMap();
		m.put("a","a");
		m.put("b",new Integer(2));
		m.put("c","a");
		
		System.out.println(m.toXml());
	}
}
