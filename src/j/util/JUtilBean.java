package j.util;

import j.dao.DAOFactory;
import j.db.JactionLog;
import j.log.Logger;
import j.sys.SysUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author JFramework
 * 
 */
public class JUtilBean {
	private static Logger log = Logger.create(JUtilBean.class);

	/**
	 * 根据字段名和参数，得到setter方法
	 * 
	 * @param cls
	 * @param propertyName
	 * @param paras
	 * @return Method
	 * @throws Exception
	 */
	public static Method getSetter(Class cls, String propertyName, Class[] paras)throws Exception {
		Method method = null;
		String methodName = "set" + upperFirstChar(propertyName);
		try{
			method = cls.getDeclaredMethod(methodName, paras);
		}catch(Exception e){
			method = cls.getMethod(methodName, paras);
		}

		return method;
	}

	/**
	 * 根据字段名和参数，得到getter方法
	 * 
	 * @param cls
	 * @param propertyName
	 * @param paras
	 * @return Method
	 * @throws Exception
	 */
	public static Method getGetter(Class cls, String propertyName, Class[] paras)throws Exception {
		Method method = null;
		String methodName = "get" + upperFirstChar(propertyName);
		try{
			method = cls.getMethod(methodName, paras);
		}catch(Exception e){}
		return method;
	}

	/**
	 * 得到obj对象，propertyName变量的值，int等基本类型会返回为Integer等对应的类类型
	 * 
	 * @param obj
	 * @param propertyName
	 * @return
	 * @throws Exception
	 */
	public static Object getPropertyValue(Object obj, String propertyName)throws Exception {
		if(obj==null) return null;
		
		Method m = JUtilBean.getGetter(obj.getClass(), propertyName, null);
		return m==null?null:m.invoke(obj,(Object[])null);
	}

	/**
	 * 设置obj对象，propertyName变量的值为value，并指定value的类类型
	 * 
	 * @param obj
	 * @param propertyName
	 * @param value
	 * @param valueType
	 * @return 当setter方法返回值为void时，返回null
	 * @throws Exception
	 */
	public static Object setPropertyValue(Object obj, String propertyName,Object[] value, Class[] valueType) throws Exception {
		if(obj==null) return null;
		
		Method m = JUtilBean.getSetter(obj.getClass(), propertyName, valueType);
		return m==null?null:m.invoke(obj, value);
	}

	/**
	 * 是否全部大写
	 * 
	 * @param src
	 * @return
	 */
	private static boolean isAllUpperCase(String src){
		for (int i = 0; i < src.length(); i++){
			if (src.charAt(i) < 'A' || src.charAt(i) > 'Z'){
				return false;
			}
		}
		return true;
	}

	
	/**
	 * 将第一个字符小写
	 * @param str
	 * @return
	 */
	public static String lowerFirstChar(String str){
		StringBuffer sb = new StringBuffer(str);
		if (((int) 'A' <= (int) sb.charAt(0))
				&& ((int) sb.charAt(0) <= (int) 'Z')){
			sb.setCharAt(0, (char) ((int) sb.charAt(0) + 32));
		}

		return sb.toString();
	}

	/**
	 * 将第一个字符大写
	 * @param str
	 * @return
	 */
	public static String upperFirstChar(String str){
		StringBuffer sb = new StringBuffer(str);
		if (((int) 'a' <= (int) sb.charAt(0))
				&& ((int) sb.charAt(0) <= (int) 'z')){
			sb.setCharAt(0, (char) ((int) sb.charAt(0) - 32));
		}

		return sb.toString();
	}

	/**
	 * 数据库相关名字到java名字的转换(为与hibernate兼容,直接从Middlegen-Hibernate-r5拷贝下来的代码)
	 * 
	 * @param s
	 * @return
	 */
	public static String dbNameToVariableName(String s){
		if ("".equals(s)){
			return s;
		}
		if (s.indexOf("_") < 0 && !isAllUpperCase(s)){
			return s;
		}
		StringBuffer result = new StringBuffer();
		boolean capitalize = true;
		boolean lastCapital = false;
		boolean lastDecapitalized = false;
		String p = null;
		for (int i = 0; i < s.length(); i++){
			String c = s.substring(i, i + 1);
			if ("_".equals(c) || " ".equals(c)){
				capitalize = true;
			}else{
				if (c.toUpperCase().equals(c)){
					if (lastDecapitalized && !lastCapital){
						capitalize = true;
					}
					lastCapital = true;
				} else {
					lastCapital = false;
				}
				if (capitalize){
					if (p != null && i == 2 && !p.equals("_")){
						result.append(c.toLowerCase());
						capitalize = false;
						p = c;
					} else {
						result.append(c.toUpperCase());
						capitalize = false;
						p = c;
					}
				} else {
					result.append(c.toLowerCase());
					lastDecapitalized = true;
					p = c;
				}
			}
		}
		return result.toString();
	}

	/**
	 * 数据库列名转换为变量名
	 * @param s
	 * @return
	 */
	public static String colNameToVariableName(String s){
		return lowerFirstChar(dbNameToVariableName(s));
	}

	/**
	 * 通过命名规则对应，将客户端提交的数据转换为beanClass的对象
	 * @param beanClass
	 * @param request
	 * @return
	 */
	public static Object form2Bean(Class beanClass, HttpServletRequest request){
		if (request == null||beanClass == null){
			return null;
		}
		
		try {
			return form2Bean(beanClass.newInstance(), request);
		} catch (Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
	}

	/**
	 * 通过命名规则对应，将客户端提交的数据赋值给bean的相应变量
	 * @param bean
	 * @param request
	 * @return
	 */
	public static Object form2Bean(Object bean, HttpServletRequest request){
		if (request == null||bean == null){
			return null;
		}
		
		Class beanClass = bean.getClass();
		try {
			Enumeration paraNames = request.getParameterNames();
			while (paraNames.hasMoreElements()){
				String name =(String)paraNames.nextElement();
				Field field=null;
				try {
					field = beanClass.getDeclaredField(JUtilBean.colNameToVariableName(name));
				} catch (Exception e){
					field = null;
				}
				if (field == null){
					continue;
				}
				String value = SysUtil.getHttpParameter(request,name);

				String type = field.getType().getName();
				String fieldName = field.getName();

				if (value == null|| (value.equals("") && !type.equals("java.lang.String"))){
					continue;
				}
				if (type.equals("java.lang.String")){
					JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.String.class }).invoke(bean, new Object[] { value });
				} else if (type.equals("java.lang.Long")){
					if(JUtilMath.isLong(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Long.class }).invoke(bean,new Object[] { new Long(value) });
					}
				} else if (type.equals("java.lang.Integer")){
					if(JUtilMath.isInt(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Integer.class }).invoke(bean, new Object[] { new Integer(value) });
					}
				} else if (type.equals("java.lang.Short")){
					if(JUtilMath.isShort(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Short.class }).invoke(bean, new Object[] { new Short(value) });
					}
				} else if (type.equals("java.lang.Float")){
					if(JUtilMath.isNumber(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Float.class }).invoke(bean,new Object[] { new Float(value) });
					}
				} else if (type.equals("java.lang.Double")){
					if(JUtilMath.isNumber(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Double.class }).invoke(bean, new Object[] { new Double(value) });
					}
				} else if (type.equals("java.sql.Timestamp")){
					value = value.trim();
					if (value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$")){
						value += ":00";
					} else if (value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}$")){
						value += ":00:00";
					} else if (JUtilString.isDate(value)){
						value += " 00:00:00";
					}
					if (JUtilTimestamp.isTimestamp(value)){
						JUtilBean.getSetter(beanClass,fieldName,new Class[] { java.sql.Timestamp.class }).invoke(bean,new Object[] { Timestamp.valueOf(value) });
					}
				}
			}

			return bean;
		} catch (Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
	}
	


	/**
	 * 通过命名规则对应，将客户端提交的数据转换为beanClass的对象
	 * @param beanClass
	 * @param request
	 * @return
	 */
	public static Object form2BeanTrim(Class beanClass, HttpServletRequest request){
		if (request == null||beanClass == null){
			return null;
		}
		
		try {
			return form2BeanTrim(beanClass.newInstance(), request);
		} catch (Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
	}

	/**
	 * 通过命名规则对应，将客户端提交的数据赋值给bean的相应变量
	 * @param bean
	 * @param request
	 * @return
	 */
	public static Object form2BeanTrim(Object bean, HttpServletRequest request){
		if (request == null||bean == null){
			return null;
		}
		
		Class beanClass = bean.getClass();
		try {
			Enumeration paraNames = request.getParameterNames();
			while (paraNames.hasMoreElements()){
				String name = (String) paraNames.nextElement();
				Field field = null;
				try {
					field = beanClass.getDeclaredField(JUtilBean.colNameToVariableName(name));
				} catch (Exception e){
					field = null;
				}
				if (field == null){
					continue;
				}
				
				String value = SysUtil.getHttpParameter(request,name);
				if(value!=null) value=value.trim();				

				String type = field.getType().getName();
				String fieldName = field.getName();

				if (value == null|| (value.equals("") && !type.equals("java.lang.String"))){
					continue;
				}
				if (type.equals("java.lang.String")){
					JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.String.class }).invoke(bean, new Object[] { value });
				} else if (type.equals("java.lang.Long")){
					if(JUtilMath.isLong(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Long.class }).invoke(bean,new Object[] { new Long(value) });
					}
				} else if (type.equals("java.lang.Integer")){
					if(JUtilMath.isInt(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Integer.class }).invoke(bean, new Object[] { new Integer(value) });
					}
				} else if (type.equals("java.lang.Short")){
					if(JUtilMath.isShort(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Short.class }).invoke(bean, new Object[] { new Short(value) });
					}
				} else if (type.equals("java.lang.Float")){
					if(JUtilMath.isNumber(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Float.class }).invoke(bean,new Object[] { new Float(value) });
					}
				} else if (type.equals("java.lang.Double")){
					if(JUtilMath.isNumber(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Double.class }).invoke(bean, new Object[] { new Double(value) });
					}
				} else if (type.equals("java.sql.Timestamp")){
					value = value.trim();
					if (value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$")){
						value += ":00";
					} else if (value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}$")){
						value += ":00:00";
					} else if (JUtilString.isDate(value)){
						value += " 00:00:00";
					}
					if (JUtilTimestamp.isTimestamp(value)){
						JUtilBean.getSetter(beanClass,fieldName,new Class[] { java.sql.Timestamp.class }).invoke(bean,new Object[] { Timestamp.valueOf(value) });
					}
				}
			}

			return bean;
		} catch (Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
	}

	/**
	 * 通过命名规则对应(map的key对应字段名)，将map中的数据转换为beanClass的对象
	 * @param beanClass
	 * @param m
	 * @return
	 */
	public static Object map2Bean(Class beanClass, Map m){
		if (m == null){
			return null;
		}
		if (beanClass == null){
			return null;
		}
		try {
			return map2Bean(beanClass.newInstance(), m);
		} catch (Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
	}

	/**
	 * 通过命名规则对应(map的key对应字段名)，将map中的数据赋值给bean的相应变量
	 * @param bean
	 * @param request
	 * @return
	 */
	public static Object map2Bean(Object bean, Map m){
		if (m == null){
			return null;
		}
		if (bean == null){
			return null;
		}
		Class beanClass = bean.getClass();
		try {
			for (Iterator it = m.keySet().iterator(); it.hasNext();){
				String name = (String) it.next();
				Field field = null;
				try {
					field = beanClass.getDeclaredField(JUtilBean.colNameToVariableName(name));
				} catch (Exception e){
					field = null;
				}
				if (field == null){
					continue;
				}
				String value = (String) m.get(name);

				String type = field.getType().getName();
				String fieldName = field.getName();

				if (value == null|| (value.equals("") && !type.equals("java.lang.String"))){
					continue;
				}
				if (type.equals("java.lang.String")){
					JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.String.class }).invoke(bean, new Object[] { value });
				}else if (type.equals("java.lang.Long")){
					if(JUtilMath.isLong(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Long.class }).invoke(bean,new Object[] { new Long(value) });
					}
				} else if (type.equals("java.lang.Integer")){
					if(JUtilMath.isInt(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Integer.class }).invoke(bean, new Object[] { new Integer(value) });
					}
				} else if (type.equals("java.lang.Short")){
					if(JUtilMath.isShort(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Short.class }).invoke(bean, new Object[] { new Short(value) });
					}
				}  else if (type.equals("java.lang.Float")){
					if(JUtilMath.isNumber(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Float.class }).invoke(bean,new Object[] { new Float(value) });
					}
				} else if (type.equals("java.lang.Double")){
					if(JUtilMath.isNumber(value)){
						JUtilBean.getSetter(beanClass, fieldName,new Class[] { java.lang.Double.class }).invoke(bean, new Object[] { new Double(value) });
					}
				} else if (type.equals("java.sql.Timestamp")){
					boolean valid = false;
					value = value.trim();
					if (value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")){
						valid = true;
					} else if (value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$")){
						value += ":00";
						valid = true;
					} else if (value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}$")){
						value += ":00:00";
						valid = true;
					} else if (JUtilString.isDate(value)){
						value += " 00:00:00";
						valid = true;
					}
					if (valid){
						JUtilBean.getSetter(beanClass,fieldName,new Class[] { java.sql.Timestamp.class }).invoke(bean,new Object[] { Timestamp.valueOf(value) });
					}
				}
			}

			return bean;
		} catch (Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
	}
	
	/**
	 * 
	 * @param bean
	 * @return
	 */
	public static Map bean2Map(Object bean){
		Map map=new LinkedHashMap();
		if(bean!=null){
			Class beanClass = bean.getClass();
			Field[] fields=beanClass.getDeclaredFields();
			for(int i=0;i<fields.length;i++){
				try{
					String name=fields[i].getName();
					Object o=JUtilBean.getPropertyValue(bean,name);
					if(o!=null) map.put(name,o);
				} catch (Exception e){
					log.log(e, Logger.LEVEL_ERROR);
					return null;
				}
			}
		}
		return map;
	}
	
	/**
	 * 
	 * @param bean
	 * @return
	 */
	public static Map bean2MapString(Object bean){
		Map map=new LinkedHashMap();
		if(bean!=null){
			Class beanClass = bean.getClass();
			Field[] fields=beanClass.getDeclaredFields();
			for(int i=0;i<fields.length;i++){
				try{
					String name=fields[i].getName();
					Object o=JUtilBean.getPropertyValue(bean,name);
					if(o!=null) map.put(name,o.toString());
				} catch (Exception e){
					log.log(e, Logger.LEVEL_ERROR);
					return null;
				}
			}
		}
		return map;
	}
	
	/**
	 * 
	 * @param factory
	 * @param table
	 * @param bean
	 * @return
	 */
	public static Map bean2MapWithDbColKey(DAOFactory factory,String table,Object bean){
		Map map=new LinkedHashMap();
		if(bean!=null){
			Class beanClass = bean.getClass();
			Field[] fields=beanClass.getDeclaredFields();
			for(int i=0;i<fields.length;i++){
				try{
					String name=fields[i].getName();
					Object o=JUtilBean.getPropertyValue(bean,name);
					if(o!=null) map.put(factory.getColName(table,name).toLowerCase(),o);
				} catch (Exception e){
					log.log(e, Logger.LEVEL_ERROR);
					return null;
				}
			}
		}
		return map;
	}
	
	/**
	 * 
	 * @param factory
	 * @param table
	 * @param bean
	 * @return
	 */
	public static Map bean2MapStringWithDbColKey(DAOFactory factory,String table,Object bean){
		Map map=new LinkedHashMap();
		if(bean!=null){
			Class beanClass = bean.getClass();
			Field[] fields=beanClass.getDeclaredFields();
			for(int i=0;i<fields.length;i++){
				try{
					String name=fields[i].getName();
					Object o=JUtilBean.getPropertyValue(bean,name);
					if(o!=null) map.put(factory.getColName(table,name).toLowerCase(),o.toString());
				} catch (Exception e){
					log.log(e, Logger.LEVEL_ERROR);
					return null;
				}
			}
		}
		return map;
	}
	
	/**
	 * 
	 * @param beans
	 * @param encoding
	 * @return
	 */
	public static String beans2Xml(List beans,String encoding){
		if(beans==null) return null;
		
		if(encoding==null||"".equals(encoding)) encoding="UTF-8";

		try{
			Document doc=DocumentHelper.createDocument();
			doc.setXMLEncoding(encoding);
			
			Element root=doc.addElement("root");
			
			for(int i=0;i<beans.size();i++){
				Object bean=beans.get(i);
				if(bean==null) continue;
				
				Element b=root.addElement("bean");
				b.addAttribute("clz",bean.getClass().getName());
				
				Class beanClass = bean.getClass();
				Field[] fields=beanClass.getDeclaredFields();
				for(int f=0;f<fields.length;f++){
					String name=fields[f].getName();
					Object o=JUtilBean.getPropertyValue(bean,name);
					Element ele=b.addElement("field");
					ele.addAttribute("name",name);
					ele.addAttribute("type",fields[f].getType().getName());
					
					if(o!=null) ele.setText(o.toString());
				}
			}
			
			return JUtilDom4j.toString(doc,encoding);
		}catch (Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 */
	public static List xml2Beans(String xml,String encoding){
		if(xml==null||"".equals(xml)) return null;
		
		if(encoding==null||"".equals(encoding)) encoding="UTF-8";
		
		List beans=new LinkedList();
		
		try{
			Document doc=JUtilDom4j.parseString(xml,encoding);
			Element root=doc.getRootElement();
			List bs=root.elements("bean");
			for(int b=0;b<bs.size();b++){
				Element be=(Element)bs.get(b);
				
				Class beanClass=Class.forName(be.attributeValue("clz"));
				
				Object bean=beanClass.newInstance();
				
				List fields=be.elements("field");
				for(int f=0;f<fields.size();f++){
					Element fe=(Element)fields.get(f);
					String fieldName=fe.attributeValue("name");
					String type=fe.attributeValue("type");
					String value=fe.getText();
					
					Field field=null;
					try{
						field=beanClass.getDeclaredField(fieldName);
					}catch (Exception e){
						field=null;
					}
					if(field==null){
						continue;
					}

					if(value==null||(value.equals("")&&!type.equals("java.lang.String"))){
						continue;
					}
					
					if(type.equals("java.lang.String")){
						JUtilBean.getSetter(beanClass,fieldName,new Class[]{java.lang.String.class}).invoke(bean,new Object[]{value});
					}else if(type.equals("java.lang.Long")){
						if(JUtilMath.isLong(value)){
							JUtilBean.getSetter(beanClass,fieldName,new Class[]{java.lang.Long.class}).invoke(bean,new Object[]{new Long(value)});
						}
					}else if(type.equals("java.lang.Integer")){
						if(JUtilMath.isInt(value)){
							JUtilBean.getSetter(beanClass,fieldName,new Class[]{java.lang.Integer.class}).invoke(bean,new Object[]{new Integer(value)});
						}
					}else if(type.equals("java.lang.Short")){
						if(JUtilMath.isShort(value)){
							JUtilBean.getSetter(beanClass,fieldName,new Class[]{java.lang.Short.class}).invoke(bean,new Object[]{new Short(value)});
						}
					}else if(type.equals("java.lang.Float")){
						if(JUtilMath.isNumber(value)){
							JUtilBean.getSetter(beanClass,fieldName,new Class[]{java.lang.Float.class}).invoke(bean,new Object[]{new Float(value)});
						}
					}else if(type.equals("java.lang.Double")){
						if(JUtilMath.isNumber(value)){
							JUtilBean.getSetter(beanClass,fieldName,new Class[]{java.lang.Double.class}).invoke(bean,new Object[]{new Double(value)});
						}
					}else if(type.equals("java.sql.Timestamp")){
						value=value.trim();
						if(value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$")){
							value+=":00";
						}else if(value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}$")){
							value+=":00:00";
						}else if(JUtilString.isDate(value)){
							value+=" 00:00:00";
						}
						
						if(JUtilTimestamp.isTimestamp(value)){
							JUtilBean.getSetter(beanClass,fieldName,new Class[]{java.sql.Timestamp.class}).invoke(bean,new Object[]{Timestamp.valueOf(value)});
						}
					}
				}
				
				beans.add(bean);
			}
		}catch (Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
		
		return beans;
	}

	/**
	 * 将from中变量值拷贝到to对应的变量中，但忽略from中为null的变量
	 * @param bean
	 * @param request
	 * @return
	 */
	public static Object copyObjectIgnoreNulls(Object from, Object to){
		if (from == null || to == null){
			return null;
		}
		try {
			Class beanClass = from.getClass();
			Field[] fs = beanClass.getDeclaredFields();
			for (int i = 0; i < fs.length; i++){
				String name = fs[i].getName();
				try{
					Object v = JUtilBean.getPropertyValue(from, name);
					if (v != null){
						JUtilBean.setPropertyValue(to, name, new Object[] { v },new Class[] { v.getClass() });
					}
				}catch(Exception e){}
			}

			return to;
		} catch (Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
	}
	


	/**
	 * 将from中变量值拷贝到to对应的变量中
	 * @param bean
	 * @param request
	 * @return
	 */
	public static Object copyObject(Object from, Object to){
		if (from == null || to == null){
			return null;
		}
		try {
			Class beanClass = from.getClass();
			Field[] fs = beanClass.getDeclaredFields();
			for (int i = 0; i < fs.length; i++){
				String name = fs[i].getName();
				try{
					Object v = JUtilBean.getPropertyValue(from, name);
					//System.out.println(name+"="+v);
					JUtilBean.setPropertyValue(to, name, new Object[] { v },new Class[] { v.getClass() });
				}catch(Exception e){
					//e.printStackTrace();
				}
			}

			return to;
		} catch (Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
	}

	/**
	 * 打印出bean各变量的值
	 * @param bean
	 * @return
	 */
	public static String toString(Object bean){
		if (bean == null){
			return null;
		}
		String s = "";
		try {
			Class beanClass = bean.getClass();
			Field[] fs = beanClass.getDeclaredFields();
			int x = 0;
			for (int i = 0; i < fs.length; i++){
				String name = fs[i].getName();
				try{
					Object v = JUtilBean.getPropertyValue(bean, name);
					if (x == 0){
						s += "{" + name + "}:{" + v + "}";
						x = 1;
					} else {
						s += "\r\n{" + name + "}:{" + v + "}";
					}
				}catch(Exception e){}
			}
		} catch (Exception e){
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
		return s;
	}
	
	
	/**
	 * 打印出客户端提交的各参数的值
	 * @param request
	 * @return
	 */
	public static String retrieveHttpRequest(HttpServletRequest request){
		if (request == null){
			return null;
		}
		int x = 0;
		String s = SysUtil.getRequestURLWithoutParams(request)+"\r\n\r\n";
		try {
			Enumeration parameters = request.getParameterNames();
			while (parameters.hasMoreElements()){
				String parameter = (String) parameters.nextElement();
				String v = SysUtil.getHttpParameter(request, parameter);
				if (x==0){
					s += "{" + parameter + "}:{" + v + "}";
					x = 1;
				} else {
					s += "\r\n{" + parameter + "}:{" + v + "}";
				}
			}
		}catch(Exception e){
			log.log(e, Logger.LEVEL_ERROR);
		}
		return s;
	}
	
	public static void main(String[] args){
		JactionLog log1=new JactionLog();
		log1.setActionHandler("111");
		log1.setEventTime(new Timestamp(SysUtil.getNow()));
		
		JactionLog log2=new JactionLog();
		log2.setActionHandler("222");
		log2.setEventTime(new Timestamp(SysUtil.getNow()));
		
		List beans=new LinkedList();
		beans.add(log1);
		beans.add(log2);
		
		String xml=JUtilBean.beans2Xml(beans,null);
		
		System.out.println(xml);
		
		List beans2=JUtilBean.xml2Beans(xml,null);
		for(int i=0;i<beans2.size();i++){
			JactionLog o=(JactionLog)beans2.get(i);

			System.out.println(o.getActionHandler());
			System.out.println(o.getEventTime());
		}
	}
}
