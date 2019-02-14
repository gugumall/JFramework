package j.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 肖炯
 * 
 */
public abstract class JUtilSorter implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String EQUAL = "__EQUAL";// ==

	public static final String SMALLER = "__SMALLER";// <

	public static final String BIGGER = "__BIGGER";// >

	public static final String ASC = "__ASC";// 升序

	public static final String DESC = "__DESC";// >降序

	// ///////////////////冒泡排序////////////////////////////////
	/**
	 * 冒泡排序
	 * 
	 * @param in
	 * @param sortType
	 * @return
	 */
	public List bubble(List in, String sortType) {
		if (in == null || in.size() == 0)
			return in;
		int cnt = in.size();
		for (int j = 1; j < cnt; j++) {
			for (int i = 0; i < cnt - j; i++) {
				Object pre = in.get(i);
				Object after = in.get(i + 1);

				if (sortType.equals(JUtilSorter.DESC)) {
					if (compare(pre, after).equals(JUtilSorter.SMALLER)) {
						in.set(i, after);
						in.set(i + 1, pre);
					}
				} else {
					if (compare(pre, after).equals(JUtilSorter.BIGGER)) {
						in.set(i, after);
						in.set(i + 1, pre);
					}
				}
			}
		}
		return in;
	}

	/**
	 * 冒泡排序
	 * 
	 * @param in
	 * @param sortType
	 * @return
	 */
	public Object[] bubble(Object[] in, String sortType) {
		if (in == null || in.length == 0)
			return in;
		int cnt = in.length;
		for (int j = 1; j < cnt; j++) {
			for (int i = 0; i < cnt - j; i++) {
				Object pre = in[i];
				Object after = in[i + 1];

				if (sortType.equals(JUtilSorter.DESC)) {
					if (compare(pre, after).equals(JUtilSorter.SMALLER)) {
						in[i] = after;
						in[i + 1] = pre;
					}
				} else {
					if (compare(pre, after).equals(JUtilSorter.BIGGER)) {
						in[i] = after;
						in[i + 1] = pre;
					}
				}
			}
		}
		return in;
	}

	/**
	 * 冒泡排序
	 * 
	 * @param in
	 * @param sortType
	 * @param extra
	 * @return
	 */
	public List bubble(List in, String sortType, Object extra) {
		if (in == null || in.size() == 0)
			return in;
		int cnt = in.size();
		for (int j = 1; j < cnt; j++) {
			for (int i = 0; i < cnt - j; i++) {
				Object pre = in.get(i);
				Object after = in.get(i + 1);

				if (sortType.equals(JUtilSorter.DESC)) {
					if (compare(pre, after, extra).equals(JUtilSorter.SMALLER)) {
						in.set(i, after);
						in.set(i + 1, pre);
					}
				} else {
					if (compare(pre, after, extra).equals(JUtilSorter.BIGGER)) {
						in.set(i, after);
						in.set(i + 1, pre);
					}
				}
			}
		}
		return in;
	}

	/**
	 * 冒泡排序
	 * 
	 * @param in
	 * @param sortType
	 * @param extra
	 * @return
	 * @throws Exception
	 */
	public Object[] bubble(Object[] in, String sortType, Object extra) {
		if (in == null || in.length == 0)
			return in;
		int cnt = in.length;
		for (int j = 1; j < cnt; j++) {
			for (int i = 0; i < cnt - j; i++) {
				Object pre = in[i];
				Object after = in[i + 1];

				if (sortType.equals(JUtilSorter.DESC)) {
					if (compare(pre, after, extra).equals(JUtilSorter.SMALLER)) {
						in[i] = after;
						in[i + 1] = pre;
					}
				} else {
					if (compare(pre, after, extra).equals(JUtilSorter.BIGGER)) {
						in[i] = after;
						in[i + 1] = pre;
					}
				}
			}
		}
		return in;
	}

	/**
	 * 
	 * @param in
	 * @param sortType
	 * @return
	 */
	public int[] bubble(int[] in, String sortType) {
		if (in == null || in.length == 0)
			return in;
		int cnt = in.length;
		for (int j = 1; j < cnt; j++) {
			for (int i = 0; i < cnt - j; i++) {
				int pre = in[i];
				int after = in[i + 1];

				if (sortType.equals(JUtilSorter.DESC)) {
					if (pre < after) {
						in[i] = after;
						in[i + 1] = pre;
					}
				} else {
					if (pre > after) {
						in[i] = after;
						in[i + 1] = pre;
					}
				}
			}
		}
		return in;
	}

	/**
	 * 
	 * @param in
	 * @param sortType
	 * @return
	 */
	public long[] bubble(long[] in, String sortType) {
		if (in == null || in.length == 0)
			return in;
		int cnt = in.length;
		for (int j = 1; j < cnt; j++) {
			for (int i = 0; i < cnt - j; i++) {
				long pre = in[i];
				long after = in[i + 1];

				if (sortType.equals(JUtilSorter.DESC)) {
					if (pre < after) {
						in[i] = after;
						in[i + 1] = pre;
					}
				} else {
					if (pre > after) {
						in[i] = after;
						in[i + 1] = pre;
					}
				}
			}
		}
		return in;
	}

	/**
	 * 
	 * @param in
	 * @param sortType
	 * @return
	 */
	public float[] bubble(float[] in, String sortType) {
		if (in == null || in.length == 0)
			return in;
		int cnt = in.length;
		for (int j = 1; j < cnt; j++) {
			for (int i = 0; i < cnt - j; i++) {
				float pre = in[i];
				float after = in[i + 1];

				if (sortType.equals(JUtilSorter.DESC)) {
					if (pre < after) {
						in[i] = after;
						in[i + 1] = pre;
					}
				} else {
					if (pre > after) {
						in[i] = after;
						in[i + 1] = pre;
					}
				}
			}
		}
		return in;
	}

	/**
	 * 
	 * @param in
	 * @param sortType
	 * @return
	 */
	public double[] bubble(double[] in, String sortType) {
		if (in == null || in.length == 0)
			return in;
		int cnt = in.length;
		for (int j = 1; j < cnt; j++) {
			for (int i = 0; i < cnt - j; i++) {
				double pre = in[i];
				double after = in[i + 1];

				if (sortType.equals(JUtilSorter.DESC)) {
					if (pre < after) {
						in[i] = after;
						in[i + 1] = pre;
					}
				} else {
					if (pre > after) {
						in[i] = after;
						in[i + 1] = pre;
					}
				}
			}
		}
		return in;
	}

	// ///////////////////冒泡排序 END////////////////////////////////

	
	// ///////////////////归并排序////////////////////////////////	
	/**
	 * 
	 * @param list
	 * @param sortType
	 * @return
	 */
	public List mergeSort(List list,String sortType) {
		if(list==null||list.isEmpty()) return list;
		Object[] obj=list.toArray();
		
		Object[] bridge = new Object[obj.length]; // 初始化中间数组
		mergeSort(bridge,obj, 0, obj.length - 1); // 归并排序
		bridge = null;
		
		list.clear();
		if(JUtilSorter.DESC.equals(sortType)){
			for(int i=obj.length-1;i>=0;i--) list.add(obj[i]);
		}else{
			for(int i=0;i<obj.length;i++) list.add(obj[i]);
		}
		
		return list;
	}
	
	/**
	 * 
	 * @param obj
	 * @param sortType
	 */
	public Object[] mergeSort(Object[] obj,String sortType) {
		if (obj == null||obj.length==0) return obj;
		
		Object[] bridge = new Object[obj.length]; // 初始化中间数组
		mergeSort(bridge,obj, 0, obj.length - 1); // 归并排序
		bridge = null;
		
		List list=new ArrayList();
		if(JUtilSorter.DESC.equals(sortType)){
			for(int i=obj.length-1;i>=0;i--) list.add(obj[i]);
		}else{
			for(int i=0;i<obj.length;i++) list.add(obj[i]);
		}
		list.toArray(obj);
		
		list.clear();
		list=null;
		
		return obj;
	}

	/**
	 * 将下标从left到right的数组进行归并排序
	 * 
	 * @param obj 要排序的数组的句柄
	 * @param left  要排序的数组的第一个元素下标
	 * @param right 要排序的数组的最后一个元素的下标
	 */
	private void mergeSort(Object[] bridge,Object[] obj, int left, int right) {
		if (left < right) {
			int center = (left + right) / 2;
			mergeSort(bridge,obj, left, center);
			mergeSort(bridge,obj, center + 1, right);
			merge(bridge,obj, left, center, right);
		}
	}

	/**
	 * 将两个对象数组进行归并，并使归并后为升序。归并前两个数组分别有序
	 * 
	 * @param obj 对象数组的句柄
	 * @param left 左数组的第一个元素的下标
	 * @param center 左数组的最后一个元素的下标
	 * @param right 右数组的最后一个元素的下标
	 */
	private void merge(Object[] bridge,Object[] obj, int left, int center, int right) {
		int mid = center + 1;
		int third = left;
		int tmp = left;
		while (left <= center && mid <= right) { // 从两个数组中取出小的放入中间数组
			if (!this.compare(obj[left], obj[mid]).equals(JUtilSorter.BIGGER)) {
				bridge[third++] = obj[left++];
			} else
				bridge[third++] = obj[mid++];
		}
		
		// 剩余部分依次置入中间数组
		while (mid <= right) {
			bridge[third++] = obj[mid++];
		}
		while (left <= center) {
			bridge[third++] = obj[left++];
		}
		
		// 将中间数组的内容拷贝回原数组
		copy(bridge,obj, tmp, right);
	}

	/**
	 * 将中间数组bridge中的内容拷贝到原数组中
	 * 
	 * @param obj 原数组的句柄
	 * @param left 要拷贝的第一个元素的下标
	 * @param right 要拷贝的最后一个元素的下标
	 */
	private void copy(Object[] bridge,Object[] obj, int left, int right) {
		while (left <= right) {
			obj[left] = bridge[left];
			left++;
		}
	}

	// ///////////////////归并排序 END////////////////////////////////

	/**
	 * 比较两个对象的大小
	 * 
	 * @param pre
	 * @param after
	 * @return Sort.SMALLER: 前者<后者； Sort.EQUAL: 相等； Sort.BIGGER: 前者>后者
	 */
	public abstract String compare(Object pre, Object after);

	/**
	 * 
	 * @param pre
	 * @param after
	 * @param extra
	 * @return
	 */
	public String compare(Object pre, Object after, Object extra) {
		return compare(pre, after);
	}
}
