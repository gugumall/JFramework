package j.common;

import java.util.List;

/**
 * 
 * @author 肖炯
 *
 * 2019年4月7日
 *
 * <b>功能描述</b>
 */
public final class JArray {
	
	/**
	 * 截取数组的一部分
	 * @param objects
	 * @param sub
	 * @param from
	 * @param to
	 * @return
	 */
	public static Object[] subArray(Object[] objects,Object[] sub,int from,int to) {
		if(objects==null) return null;
		if(from<0||from>to) return null;
		for(int i=from;i<to&&i<objects.length;i++) {
			sub[i-from]=objects[i];
		}
		return sub;
	}
	
	/**
	 * 
	 * @param all
	 * @param from
	 * @param to
	 * @return
	 */
	public static byte[] sub(byte[] all,int from,int to) {
		if(all==null) return null;
		if(from<0||from>to) return null;
		if(from==to) return new byte[0];
		
		byte[] sub=new byte[to-from];
		for(int i=from;i<to&&i<all.length;i++) {
			sub[i-from]=all[i];
		}
		return sub;
	}
	
	/**
	 * 
	 * @param all
	 * @param from
	 * @param to
	 * @return
	 */
	public static int[] sub(int[] all,int from,int to) {
		if(all==null) return null;
		if(from<0||from>to) return null;
		if(from==to) return new int[0];
		
		int[] sub=new int[to-from];
		for(int i=from;i<to&&i<all.length;i++) {
			sub[i-from]=all[i];
		}
		return sub;
	}
	
	/**
	 * 
	 * @param all
	 * @param from
	 * @param to
	 * @return
	 */
	public static long[] sub(long[] all,int from,int to) {
		if(all==null) return null;
		if(from<0||from>to) return null;
		if(from==to) return new long[0];
		
		long[] sub=new long[to-from];
		for(int i=from;i<to&&i<all.length;i++) {
			sub[i-from]=all[i];
		}
		return sub;
	}
	
	/**
	 * 
	 * @param all
	 * @param from
	 * @param to
	 * @return
	 */
	public static double[] sub(double[] all,int from,int to) {
		if(all==null) return null;
		if(from<0||from>to) return null;
		if(from==to) return new double[0];
		
		double[] sub=new double[to-from];
		for(int i=from;i<to&&i<all.length;i++) {
			sub[i-from]=all[i];
		}
		return sub;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	public static boolean duplicatedElements(Object[] array) {
		if(array==null||array.length==0) return false;
		
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array.length; j++) {
				if(i==j) continue;
				
				if(array[i]==null && array[j]==null) return true;
				else if(array[i]!=null && array[j]!=null && array[i].equals(array[j])) return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param objects
	 * @return
	 */
	public static String toString(List objects) {
		return toString(objects, null);
	}
	
	/**
	 * 
	 * @param objects
	 * @param splitter
	 * @return
	 */
	public static String toString(List objects, String splitter) {
		if(objects==null) return null;
		if(objects.isEmpty()) return "";
		
		StringBuffer sb=new StringBuffer();
		
		for(int i=0; i<objects.size(); i++) {
			if(splitter!=null&&i>0) sb.append(splitter);
			sb.append(objects.get(i).toString());
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String[] arr=new String[] {"b", "b", "c", null};
		System.out.println(duplicatedElements(arr));
	}
}
