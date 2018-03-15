package j.dao.connection.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * 
 * @author JFramework
 * 
 */
public final class PropertiesHelper {
	/**
	 * 
	 * @param property
	 * @param properties
	 * @return
	 */
	public static boolean getBoolean(String property, Properties properties) {
		return Boolean.valueOf(properties.getProperty(property)).booleanValue();
	}

	/**
	 * 
	 * @param property
	 * @param properties
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBoolean(String property, Properties properties,
			boolean defaultValue) {
		String setting = properties.getProperty(property);
		return (setting == null) ? defaultValue : Boolean.valueOf(setting)
				.booleanValue();
	}

	/**
	 * 
	 * @param property
	 * @param properties
	 * @param defaultValue
	 * @return
	 */
	public static int getInt(String property, Properties properties,
			int defaultValue) {
		String propValue = properties.getProperty(property);
		return (propValue == null) ? defaultValue : Integer.parseInt(propValue);
	}

	/**
	 * 
	 * @param property
	 * @param properties
	 * @param defaultValue
	 * @return
	 */
	public static long getLong(String property, Properties properties,
			long defaultValue) {
		String propValue = properties.getProperty(property);
		return (propValue == null) ? defaultValue : Long.parseLong(propValue);
	}

	/**
	 * 
	 * @param property
	 * @param properties
	 * @param defaultValue
	 * @return
	 */
	public static String getString(String property, Properties properties,
			String defaultValue) {
		String propValue = properties.getProperty(property);
		return (propValue == null) ? defaultValue : propValue;
	}

	/**
	 * 
	 * @param property
	 * @param properties
	 * @return
	 */
	public static Integer getInteger(String property, Properties properties) {
		String propValue = properties.getProperty(property);
		return (propValue == null) ? null : Integer.valueOf(propValue);
	}

	/**
	 * 
	 * @param property
	 * @param properties
	 * @param defaultValue
	 * @return
	 */
	public static byte getByte(String property, Properties properties,byte defaultValue) {
		String propValue = properties.getProperty(property);
		return (propValue == null) ? defaultValue : Byte.parseByte(propValue);
	}

	/**
	 * 
	 * @param property
	 * @param delim
	 * @param properties
	 * @return
	 */
	public static Map toMap(String property, String delim, Properties properties) {
		Map map = new HashMap();
		String propValue = properties.getProperty(property);
		if (propValue != null) {
			StringTokenizer tokens = new StringTokenizer(propValue, delim);
			while (tokens.hasMoreTokens()) {
				map.put(tokens.nextToken(), tokens.hasMoreElements() ? tokens
						.nextToken() : StringHelper.EMPTY_STRING);
			}
		}
		return map;
	}

	/**
	 * 
	 * @param property
	 * @param delim
	 * @param properties
	 * @return
	 */
	public static String[] toStringArray(String property, String delim,
			Properties properties) {
		return toStringArray(properties.getProperty(property), delim);
	}

	
	/**
	 * 
	 * @param propValue
	 * @param delim
	 * @return
	 */
	public static String[] toStringArray(String propValue, String delim) {
		if (propValue != null) {
			return StringHelper.split(delim, propValue);
		} else {
			return ArrayHelper.EMPTY_STRING_ARRAY;
		}
	}
}
