package j.util;

import j.common.JProperties;
import j.dao.DAOFactory;
import j.db.JactionLog;
import j.log.Logger;
import j.sys.SysUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONObject;

/**
 * @author 肖炯
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
	public static String bean2Json(Object bean){
		if(bean==null) return "{}";
		
		StringBuffer jsonString=new StringBuffer();
		jsonString.append("{");
		
		Class beanClass = bean.getClass();
		Field[] fields=beanClass.getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			try{
				String name=fields[i].getName();
				Object o=JUtilBean.getPropertyValue(bean,name);
				if(o==null){
					jsonString.append("\""+name+"\":null,");
				}else{
					jsonString.append("\""+name+"\":\""+JUtilJSON.format(o.toString())+"\",");
				}
			} catch (Exception e){
				log.log(e, Logger.LEVEL_ERROR);
				return null;
			}
		}
		
		if(jsonString.length()>1){
			jsonString.deleteCharAt(jsonString.length()-1);
		}
		jsonString.append("}");
		
		return jsonString.toString();
	}
	
	/**
	 * 
	 * @param bean
	 * @return
	 */
	public static String beans2Json(List beans){
		if(beans==null||beans.isEmpty()) return "[]";
		
		StringBuffer jsonString=new StringBuffer();
		jsonString.append("[");
		
		for(int b=0;b<beans.size();b++){
			Object bean=beans.get(b);
			if(bean==null){
				jsonString.append("{},");
			}else{
				jsonString.append(bean2Json(bean)+",");
			}
		}
		
		if(jsonString.charAt(jsonString.length()-1)==','){
			jsonString.deleteCharAt(jsonString.length()-1);
		}
		jsonString.append("]");
		
		return jsonString.toString();
	}
	
	/**
	 * 
	 * @param datas
	 * @return
	 */
	public static String map2Json(Map datas){
		StringBuffer s=new StringBuffer();
		s.append("{");
		
		
		for(Iterator it=datas.keySet().iterator();it.hasNext();){
			Object key=it.next();
			Object val=datas.get(key);
			
			s.append("\""+key+"\":");
			if(val instanceof List){
				s.append(JUtilBean.beans2Json((List)val));
			}else if(val instanceof String){
				s.append("\""+JUtilJSON.format(val.toString())+"\"");
			}else if(val instanceof Integer){
				s.append("\""+val.toString()+"\"");
			}else if(val instanceof Long){
				s.append("\""+val.toString()+"\"");
			}else if(val instanceof Double){
				s.append("\""+JUtilMath.formatPrintWithoutZero((double)val,20)+"\"");
			}else if(val instanceof Timestamp){
				s.append("\""+val.toString()+"\"");
			}else{
				s.append(JUtilBean.bean2Json(val));
			}
			s.append(",");
		}
		if(s.charAt(s.length()-1)==',') s=s.deleteCharAt(s.length()-1);
		s.append("}");
		return s.toString();
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
		return beans2Xml(beans,encoding,null);
	}
	
	/**
	 * 
	 * @param beans
	 * @param encoding
	 * @return
	 */
	public static String beans2Xml(List beans,String encoding,Class beanClass){
		if(beans==null) return "";
		
		if(encoding==null||"".equals(encoding)) encoding="UTF-8";
		

		try{
			Document doc=DocumentHelper.createDocument();
			doc.setXMLEncoding(encoding);
			
			Element root=doc.addElement("root");
			
			for(int i=0;i<beans.size();i++){
				Object bean=beans.get(i);
				if(bean==null) continue;
				
				Class _beanClass=beanClass==null?bean.getClass():beanClass;
				
				Element b=root.addElement("bean");
				b.addAttribute("clz",_beanClass.getName());
				
				Field[] fields=_beanClass.getDeclaredFields();
				for(int f=0;f<fields.length;f++){
					String name=fields[f].getName();
					Object o=JUtilBean.getPropertyValue(bean,name);
					if(o==null) continue;
					
					Element ele=b.addElement("field");
					ele.addAttribute("name",name);
					ele.addAttribute("type",fields[f].getType().getName());
					
					ele.setText(o.toString());
					
					//if(o!=null) ele.setText(o.toString());
					//else ele.setText("_N_U_L_L_");
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
	 * @param encoding
	 * @return
	 */
	public static List xml2Beans(String xml,String encoding){
		return xml2Beans(xml,encoding,null);
	}
	
	/**=
	 * 
	 * @param xml
	 * @param encoding
	 * @param bean
	 * @return
	 */
	public static List xml2Beans(String xml,String encoding,Class beanClass){
		List beans=new LinkedList();
		
		if(xml==null||"".equals(xml)) return beans;
		
		if(encoding==null||"".equals(encoding)) encoding="UTF-8";
		
		try{
			Document doc=JUtilDom4j.parseString(xml,encoding);
			Element root=doc.getRootElement();
			List bs=root.elements("bean");
			for(int b=0;b<bs.size();b++){
				Element be=(Element)bs.get(b);
				
				Class _beanClass=beanClass==null?Class.forName(be.attributeValue("clz")):beanClass;
				
				Object bean=_beanClass.newInstance();
				
				List fields=be.elements("field");
				for(int f=0;f<fields.size();f++){
					Element fe=(Element)fields.get(f);
					String fieldName=fe.attributeValue("name");
					String type=fe.attributeValue("type");
					String value=fe.getText();
					
					Field field=null;
					try{
						field=_beanClass.getDeclaredField(fieldName);
					}catch (Exception e){
						field=null;
					}
					if(field==null) continue;
					
					if(value==null
							||value.equals("_N_U_L_L_")
							||(value.equals("")&&!type.equals("java.lang.String"))){
						continue;
					}
					
					if(type.equals("java.lang.String")){
						JUtilBean.getSetter(_beanClass,fieldName,new Class[]{java.lang.String.class}).invoke(bean,new Object[]{value});
					}else if(type.equals("java.lang.Long")){
						if(JUtilMath.isLong(value)){
							JUtilBean.getSetter(_beanClass,fieldName,new Class[]{java.lang.Long.class}).invoke(bean,new Object[]{new Long(value)});
						}
					}else if(type.equals("java.lang.Integer")){
						if(JUtilMath.isInt(value)){
							JUtilBean.getSetter(_beanClass,fieldName,new Class[]{java.lang.Integer.class}).invoke(bean,new Object[]{new Integer(value)});
						}
					}else if(type.equals("java.lang.Short")){
						if(JUtilMath.isShort(value)){
							JUtilBean.getSetter(_beanClass,fieldName,new Class[]{java.lang.Short.class}).invoke(bean,new Object[]{new Short(value)});
						}
					}else if(type.equals("java.lang.Float")){
						if(JUtilMath.isNumber(value)){
							JUtilBean.getSetter(_beanClass,fieldName,new Class[]{java.lang.Float.class}).invoke(bean,new Object[]{new Float(value)});
						}
					}else if(type.equals("java.lang.Double")){
						if(JUtilMath.isNumber(value)){
							JUtilBean.getSetter(_beanClass,fieldName,new Class[]{java.lang.Double.class}).invoke(bean,new Object[]{new Double(value)});
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
							JUtilBean.getSetter(_beanClass,fieldName,new Class[]{java.sql.Timestamp.class}).invoke(bean,new Object[]{Timestamp.valueOf(value)});
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
	
	public static void main(String[] args) throws Exception{
		JactionLog log1=new JactionLog();
		log1.setActionHandler("1\"11");
		log1.setEventTime(new Timestamp(SysUtil.getNow()));
		
		JactionLog log2=new JactionLog();
		log2.setActionHandler("3\"22[]1");
		log2.setEventTime(new Timestamp(SysUtil.getNow()));
		
		List temp=new LinkedList();
		temp.add(log1);
		temp.add(log2);
		System.out.println(JUtilBean.beans2Json(temp));
		
		Map datas=new HashMap();
		datas.put("total",100);
		datas.put("list",temp);
		
		String s=map2Json(datas);
		JSONObject js=new JSONObject(s);

		System.out.println(JUtilString.encodeURI("aa[]<>ddd	ccc\nxxx","UTF-8"));
		System.out.println(js.getJSONArray("list").length());
		//System.out.println(js.get("asvrId"));
		
//		JactionLog log2=new JactionLog();
//		log2.setActionHandler("222");
//		log2.setEventTime(new Timestamp(SysUtil.getNow()));
//		
//		List beans=new LinkedList();
//		beans.add(log1);
//		beans.add(log2);
//		
//		String xml=JUtilBean.beans2Xml(beans,null);
//		
//		System.out.println(xml);
//		
//		List beans2=JUtilBean.xml2Beans(xml,null);
//		for(int i=0;i<beans2.size();i++){
//			JactionLog o=(JactionLog)beans2.get(i);
//
//			System.out.println(o.getActionHandler());
//			System.out.println(o.getEventTime());
//		}
		
		JUtilKeyValue[] kvs=JProperties.getPropertiesAsArray("event_status");
		for(int i=0;i<kvs.length;i++){
			System.out.println(kvs[i].getKey()+","+kvs[i].getValue());
		}
		
		System.exit(0);
	}
}
