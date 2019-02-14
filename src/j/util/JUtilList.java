/*
 * Created on 2005-7-30
 *
 */
package j.util;

import java.util.LinkedList;
import java.util.List;

/**
 * 继承java.util.LinkedList并实现java.io.Serializable
 * @author 肖炯
 *
 */
public class JUtilList{
	public JUtilList(){
		super();
	}
	
	/**
	 * 将List对象清空，并将其中每一个元素设为null，清空后将List对象设为null
	 * @param lst
	 */
	public static void clear_AllNull(List lst){
		if(lst==null){
			return;
		}
		while(lst.size()>0){
			Object o=lst.remove(0);
			if(o!=null){
				o= null;
			}
		}
		lst=null;
	}
	
	/**
	 * 将List对象清空，并将其中每一个元素设为null
	 * @param lst
	 */
	public static void clear_ElementsNull(List lst){
		if(lst==null){
			return;
		}
		while(lst.size()>0){
			Object o=lst.remove(0);
			if(o!=null){
				o= null;
			}
		}
	}
	
	/**
	 * 将List对象清空，并将List对象设为null，其中的元素不动
	 * @param lst
	 */
	public static void clear_ContainerNull(List lst){
		if(lst==null){
			return;
		}
		lst.clear();
		lst=null;
	}
	
	/**
	 * 
	 * @param parent 
	 * @param from include
	 * @param to exclude
	 * @return
	 */
	public static List subList(List parent,int from,int to){
		List sub=new LinkedList();
		for(int i=from;i<parent.size()&&i<to;i++){
			sub.add(parent.get(i));
		}
		return sub;
	}
	
	/**
	 * 
	 * @param parent
	 * @param from include
	 * @param to exclude
	 * @return
	 */
	public static ConcurrentList subConcurrentList(List parent,int from,int to){
		ConcurrentList sub=new ConcurrentList();
		for(int i=from;i<parent.size()&&i<to;i++){
			sub.add(parent.get(i));
		}
		return sub;
	}
}
