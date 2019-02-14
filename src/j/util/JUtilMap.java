/*
 * Created on 2005-7-18
 *
 */
package j.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 肖炯
 *
 */
public class JUtilMap{
	/**
	 * 
	 *
	 */
	public JUtilMap(){
		super();
	}
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	public static List keys(Map map){
		List list=new LinkedList();
		if(map!=null) list.addAll(map.keySet());
		return list;
	}
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	public static List values(Map map){
		List list=new LinkedList();
		if(map!=null) list.addAll(map.values());
		return list;
	}
	
	
	/**
	 * 移除所有元素，并将每个元素设为null，最后将Map对象设为null
	 * @param map
	 */
	public static void clear_AllNull(Map map){
		if(map==null){
			return;
		}
		map.clear();
		map=null;
	}
	
	

	/**
	 * 移除所有元素，并将每个元素设为null
	 * @param map
	 */
	public static void clear_ElementsNull(Map map){
		if(map==null){
			return;
		}
		map.clear();
	}
	
	

	/**
	 * 移除所有元素，将Map对象设为null
	 * @param map
	 */
	public static void clear_ContainerNull(Map map){
		if(map==null){
			return;
		}
		map.clear();
		map=null;
	}
	
	/**
	 * 
	 * @param parent
	 * @param from include
	 * @param to exclude
	 * @return
	 */
	public static ConcurrentMap subConcurrentMap(ConcurrentMap parent,int from,int to){
		ConcurrentMap sub=new ConcurrentMap();
		
		List keys=parent.listKeys();
		for(int i=from;i<keys.size()&&i<to;i++){
			Object key=keys.get(i);
			sub.put(key,parent.get(key));
		}
		return sub;
	}
}