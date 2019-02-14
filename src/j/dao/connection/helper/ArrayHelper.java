package j.dao.connection.helper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author 肖炯
 *
 */
public final class ArrayHelper {
	public static final String[] EMPTY_STRING_ARRAY = {};
	public static final Class[] EMPTY_CLASS_ARRAY = {};
	public static final Object[] EMPTY_OBJECT_ARRAY = {};
	public static int[] EMPTY_INT_ARRAY = {};

	/**
	 * 
	 * @param objects
	 * @return
	 */
	public static String[] toStringArray(Object[] objects) {
		int length = objects.length;
		String[] result = new String[length];
		for (int i = 0; i < length; i++) {
			result[i] = objects[i].toString();
		}
		return result;
	}

	/**
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String[] fillArray(String str, int length) {
		String[] result = new String[length];
		Arrays.fill(result, str);
		return result;
	}

	/**
	 * 
	 * @param coll
	 * @return
	 */
	public static String[] toStringArray(Collection coll) {
		return toStringArray(coll.toArray());
	}

	/**
	 * 
	 * @param coll
	 * @return
	 */
	public static int[] toIntArray(Collection coll) {
		Iterator iter = coll.iterator();
		int[] arr = new int[coll.size()];
		int i = 0;
		while (iter.hasNext()) {
			arr[i++] = ((Integer) iter.next()).intValue();
		}
		return arr;
	}

	/**
	 * 
	 * @param array
	 * @param to
	 * @return
	 */
	public static Object[] typecast(Object[] array, Object[] to) {
		return java.util.Arrays.asList(array).toArray(to);
	}

	/**
	 * 
	 * @param array
	 * @return
	 */
	public static List toList(Object array) {
		if (array instanceof Object[])
			return Arrays.asList((Object[]) array); // faster?
		int size = Array.getLength(array);
		ArrayList list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			list.add(Array.get(array, i));
		}
		return list;
	}

	/**
	 * 
	 * @param strings
	 * @param begin
	 * @param length
	 * @return
	 */
	public static String[] slice(String[] strings, int begin, int length) {
		String[] result = new String[length];
		for (int i = 0; i < length; i++) {
			result[i] = strings[begin + i];
		}
		return result;
	}

	/**
	 * 
	 * @param objects
	 * @param begin
	 * @param length
	 * @return
	 */
	public static Object[] slice(Object[] objects, int begin, int length) {
		Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = objects[begin + i];
		}
		return result;
	}

	/**
	 * 
	 * @param iter
	 * @return
	 */
	public static List toList(Iterator iter) {
		List list = new ArrayList();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		return list;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static String[] join(String[] x, String[] y) {
		String[] result = new String[x.length + y.length];
		for (int i = 0; i < x.length; i++)
			result[i] = x[i];
		for (int i = 0; i < y.length; i++)
			result[i + x.length] = y[i];
		return result;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static int[] join(int[] x, int[] y) {
		int[] result = new int[x.length + y.length];
		for (int i = 0; i < x.length; i++)
			result[i] = x[i];
		for (int i = 0; i < y.length; i++)
			result[i + x.length] = y[i];
		return result;
	}

	/**
	 * 
	 * @param os
	 * @return
	 */
	public static String asString(Object os[]) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < os.length; i++) {
			sb.append(os[i]);
			if (i < os.length - 1)
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isAllNegative(int[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] >= 0)
				return false;
		}
		return true;
	}

	
	/**
	 * 
	 * @param collection
	 * @param array
	 */
	public static void addAll(Collection collection, Object[] array) {
		for (int i = 0; i < array.length; i++) {
			collection.add(array[i]);
		}
	}
}
