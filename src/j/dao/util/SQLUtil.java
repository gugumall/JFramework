
package j.dao.util;

import j.dao.Column;
import j.dao.DAO;
import j.dao.DAOFactory;
import j.util.JUtilBean;
import j.util.JUtilString;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

/**
 * 
 * @author 肖炯
 *
 */
public final class SQLUtil {
	private final static Map javaTypeMapping=new HashMap();
	static{
		javaTypeMapping.put(new Integer(-7),"java.lang.Boolean");//BIT
		javaTypeMapping.put(new Integer(-6),"java.lang.Short");//TINYINT
		javaTypeMapping.put(new Integer(5),"java.lang.Short");//SMALLINT
		javaTypeMapping.put(new Integer(4),"java.lang.Integer");//INTEGER
		javaTypeMapping.put(new Integer(-5),"java.lang.Long");//BIGINT
		javaTypeMapping.put(new Integer(6),"java.lang.Float");//FLOAT
		javaTypeMapping.put(new Integer(7),"java.lang.Double");//REAL
		javaTypeMapping.put(new Integer(8),"java.lang.Double");//DOUBLE
		javaTypeMapping.put(new Integer(2),"java.math.BigDecimal");//NUMERIC
		javaTypeMapping.put(new Integer(3),"java.math.BigDecimal");//DECIMAL
		javaTypeMapping.put(new Integer(1),"java.lang.String");//CHAR
		javaTypeMapping.put(new Integer(12),"java.lang.String");//VARCHAR
		javaTypeMapping.put(new Integer(-1),"java.lang.String");//LONGVARCHAR
		javaTypeMapping.put(new Integer(91),"java.sql.Date");//DATE
		javaTypeMapping.put(new Integer(92),"java.sql.Time");//TIME
		javaTypeMapping.put(new Integer(93),"java.sql.Timestamp");//TIMESTAMP
		javaTypeMapping.put(new Integer(-2),"java.io.InputStream");//BINARY
		javaTypeMapping.put(new Integer(-3),"java.io.InputStream");//VARBINARY
		javaTypeMapping.put(new Integer(-4),"java.io.InputStream");//LONGVARBINARY
		javaTypeMapping.put(new Integer(2004),"java.sql.Blob");//BLOB
		javaTypeMapping.put(new Integer(2005),"java.sql.Clob");//CLOB
		javaTypeMapping.put(new Integer(16),"java.lang.Boolean");//BOOLEAN
		

		javaTypeMapping.put("java.lang.Boolean",new Integer(-7));//BIT
		javaTypeMapping.put("java.lang.Short",new Integer(-6));//TINYINT
		javaTypeMapping.put("java.lang.Short",new Integer(5));//SMALLINT
		javaTypeMapping.put("java.lang.Integer",new Integer(4));//INTEGER
		javaTypeMapping.put("java.lang.Long",new Integer(-5));//BIGINT
		javaTypeMapping.put("java.lang.Float",new Integer(6));//FLOAT
		javaTypeMapping.put("java.lang.Double",new Integer(7));//REAL
		javaTypeMapping.put("java.lang.Double",new Integer(8));//DOUBLE
		javaTypeMapping.put("java.math.BigDecimal",new Integer(2));//NUMERIC
		javaTypeMapping.put("java.math.BigDecimal",new Integer(3));//DECIMAL
		javaTypeMapping.put("java.lang.String",new Integer(1));//CHAR
		javaTypeMapping.put("java.lang.String",new Integer(12));//VARCHAR
		javaTypeMapping.put("java.lang.String",new Integer(-1));//LONGVARCHAR
		javaTypeMapping.put("java.sql.Date",new Integer(91));//DATE
		javaTypeMapping.put("java.sql.Time",new Integer(92));//TIME
		javaTypeMapping.put("java.sql.Timestamp",new Integer(93));//TIMESTAMP
		javaTypeMapping.put("java.io.InputStream",new Integer(-2));//BINARY
		javaTypeMapping.put("java.io.InputStream",new Integer(-3));//VARBINARY
		javaTypeMapping.put("java.io.InputStream",new Integer(-4));//LONGVARBINARY
		javaTypeMapping.put("java.sql.Blob",new Integer(2004));//BLOB
		javaTypeMapping.put("java.sql.Clob",new Integer(2005));//CLOB
		javaTypeMapping.put("java.lang.Boolean",new Integer(16));//BOOLEAN		
	}
	
	/**
	 * 
	 * @param columnType
	 * @return
	 */
	public static String getJavaTypeName(int columnType){
		return (String)javaTypeMapping.get(new Integer(columnType));
	}
	
	/**
	 * 
	 * @param typeName
	 * @return
	 */
	public static int getJavaTypeValue(String typeName){
		return ((Integer)javaTypeMapping.get(typeName)).intValue();
	}
	
	/**
	 * 从sql解析得到表名
	 * @param sql
	 * @return
	 */
	public static String retrieveTableNameFromSQL(String sql){
		String tableName="";
		String tmpSQL=sql;	

		sql=sql.trim();
		tmpSQL=tmpSQL.trim();
		
		sql=sql.toUpperCase();
		if(sql.indexOf("UPDATE")>-1){
			sql=sql.substring(6);
			sql=sql.trim();
			
			tmpSQL=tmpSQL.substring(6);
			tmpSQL=tmpSQL.trim();
			
			tableName=tmpSQL.substring(0,sql.indexOf(" "));
		}else if(sql.indexOf("INSERT")>-1){
			int index=sql.indexOf("INTO");
			sql=sql.substring(index+4);
			sql=sql.trim();
			
			tmpSQL=tmpSQL.substring(index+4);
			tmpSQL=tmpSQL.trim();
			
			tableName=tmpSQL.substring(0,sql.indexOf(" "));
		}else if(sql.indexOf("DELETE")>-1){
			int index=sql.indexOf("FROM");
			sql=sql.substring(index+4);
			sql=sql.trim();
			
			tmpSQL=tmpSQL.substring(index+4);
			tmpSQL=tmpSQL.trim();
			if(sql.indexOf(" ")==-1){
				tableName=tmpSQL;
			}else{
				tableName=tmpSQL.substring(0,sql.indexOf(" "));
			}
		}else if(sql.indexOf("SELECT")>-1){
			int index=sql.indexOf("FROM");
			sql=sql.substring(index+4);
			sql=sql.trim();
			
			tmpSQL=tmpSQL.substring(index+4);
			tmpSQL=tmpSQL.trim();
			if(sql.indexOf(" ")==-1){
				tableName=tmpSQL;
			}else{
				tableName=tmpSQL.substring(0,sql.indexOf(" "));
			}
		}
		return tableName;
	}
	
	/**
	 * 得到数据库连接
	 * @param DBUrl
	 * @param DBUser
	 * @param DBPassword
	 * @return Connection
	 * @throws Exception
	 * @throws ClassNotFoundException
	 */
	public static Connection getConnection(String DBUrl, String DBUser,String DBPassword) throws Exception, ClassNotFoundException {
		return DriverManager.getConnection(DBUrl, DBUser, DBPassword);
	}

	/**
	 * 得到数据库连接
	 * @param DBUrl
	 * @return Connection
	 * @throws Exception
	 * @throws ClassNotFoundException
	 */
	public static Connection getConnection(String DBUrl) throws Exception,ClassNotFoundException {
		return DriverManager.getConnection(DBUrl);
	}

	/**
	 * 得到数据库连接
	 * @param JNDI
	 * @return Connection
	 * @throws Exception
	 * @throws NamingException
	 * @throws ClassNotFoundException
	 */
	public static Connection getConnectionByJNDI(String JNDI) throws Exception,NamingException, ClassNotFoundException {
		Context ctx = new InitialContext();
		Object dataSrcObj = ctx.lookup(JNDI);
		DataSource dataSrc = (DataSource) PortableRemoteObject.narrow(dataSrcObj, javax.sql.DataSource.class);
		return dataSrc.getConnection();
	}

	/**
	 * 得到与DAO.update(String tblName,Map colsBeUpdated,String condition)等同效果的SQL
	 * @param tblName 表名
	 * @param colsBeUpdated 待更新字段map。 key，列名  value，待更新值
	 * @param condition 更新条件
	 * @return
	 * @throws Exception
	 */
	public static String retrieveUpdateSQL(String tblName, Map colsBeUpdated,String condition) throws Exception {
		if (colsBeUpdated == null || colsBeUpdated.isEmpty()) {
			return null;
		}

		// 生成sql
		String sql = "update " + tblName + " set ";

		Set keySet = colsBeUpdated.keySet();
		Iterator colNames = keySet.iterator();

		while (colNames.hasNext()) {
			String colName = (String) colNames.next();
			Object value = colsBeUpdated.get(colName);
			String colType = value.getClass().getName();

			if (colType.equals("java.io.InputStream")) {
				throw new Exception("retrieveUpdateSQL 不支持大字段");
			} else if (colType.equals("java.lang.String")
					|| colType.equals("java.sql.Timestamp")
					|| colType.equals("java.sql.Date")) {
				sql += colName + "='"+ JUtilString.replaceAll(value.toString(), "'", "\'")+ "',";
			} else {
				sql += colName + "=" + value.toString() + ",";
			}
		}
		sql = sql.substring(0, sql.length() - 1);
		if (condition != null && condition.trim().length() >= 3) {
			sql += " where " + condition;
		}
		return sql;
	}

	/**
	 * 得到与DAO.updateByKeys(Object vo,String[] conditionKeys)等同效果的SQL
	 * @param bean 根据bean中指定字段（0个或多个）所组成的条件，将符合条件的记录更新成bean所表示的状态（不包括作为条件的自段）
	 * @param conditionKeys 组成的条件的字段名数组，不区分大小写
	 * @param factory
	 * @return
	 * @throws Exception
	 */
	public static String retrieveUpdateSQL(Object vo, String[] conditionKeys,DAOFactory factory) throws Exception {
		if (vo == null) {
			throw new Exception("指定的对象为空");
		}
		if (conditionKeys == null || conditionKeys.length == 0) {
			throw new Exception("没有指定主键");
		}

		Class cls = vo.getClass();
		String tblName=factory.getTrueTblName(vo);
		List cols=factory.getColumns(tblName);	

		//生成sql
		String sql="update "+tblName+" set ";
		for(int i=0;i<cols.size();i++){
			String colName=((Column)cols.get(i)).colName;
			if(JUtilString.containIgnoreCase(conditionKeys,colName)||factory.isColIgnoredWhileUpdating(tblName, colName)){
				continue;
			}
			Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
			Object value=method.invoke(vo,(Object[])null);
			if(value==null){
				sql+=colName+"=null,";
			}else{
				String colType=value.getClass().getName();
				if (colType.equals("java.io.InputStream")
						|| colType.equals("java.sql.Blob")
						|| colType.equals("java.sql.Clob")) {
					throw new Exception("retrieveUpdateSQL 不支持大字段");
				} else if (colType.equals("java.lang.String")
						|| colType.equals("java.sql.Timestamp")
						|| colType.equals("java.sql.Date")) {
					sql += colName + "='"+ JUtilString.replaceAll(value.toString(), "'", "\'")+ "',";
				} else {
					sql += colName + "=" + value.toString() + ",";
				}
			}
		}
		sql=sql.substring(0,sql.length()-1);
		String condition="";
		for(int i=0;i<conditionKeys.length;i++){
			conditionKeys[i]=factory.getColName(tblName,conditionKeys[i]);
			Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(conditionKeys[i]),null);
			Object keyValue=method.invoke(vo,(Object[])null);
			if(keyValue==null){
				throw new Exception("主键值不能为空"+tblName+"->"+conditionKeys[i]);
			}
			if(keyValue instanceof Integer||keyValue instanceof Float||keyValue instanceof Double){
				condition+=conditionKeys[i]+"="+keyValue+" and ";
			}else{
				condition+=conditionKeys[i]+"='"+keyValue+"' and ";
			}
		}
		condition=condition.substring(0,condition.length()-5);
		sql+=" where "+condition;
		//生成sql end
		
		return sql;
	}

	/**
	 * 得到与DAO.updateByKeysIgnoreNulls(String tblName,Map colsBeUpdated,String condition)等同效果的SQL
	 * @param bean 根据bean中指定字段（0个或多个）所组成的条件，将符合条件的记录更新成bean所表示的状态（不包括作为条件的自段和为null的字段）
	 * @param conditionKeys 组成的条件的字段名数组，不区分大小写
	 * @param factory
	 * @return
	 * @throws Exception
	 */
	public static String retrieveUpdateSQLIgnoreNulls(Object vo,String[] conditionKeys,DAOFactory factory) throws Exception {
		if (vo == null) {
			throw new Exception("指定的对象为空");
		}
		if (conditionKeys == null || conditionKeys.length == 0) {
			throw new Exception("没有指定主键");
		}

		Class cls = vo.getClass();
		String tblName=factory.getTrueTblName(vo);
		List cols=factory.getColumns(tblName);	

		//生成sql
		String sql="update "+tblName+" set ";
		for(int i=0;i<cols.size();i++){
			String colName=((Column)cols.get(i)).colName;
			if(JUtilString.containIgnoreCase(conditionKeys,colName)||factory.isColIgnoredWhileUpdating(tblName, colName)){
				continue;
			}
			Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
			Object value=method.invoke(vo,(Object[])null);
			if(value==null){
				continue;
			}else{
				String colType=value.getClass().getName();
				if (colType.equals("java.io.InputStream")
						|| colType.equals("java.sql.Blob")
						|| colType.equals("java.sql.Clob")) {
					throw new Exception("retrieveUpdateSQL 不支持大字段");
				} else if (colType.equals("java.lang.String")
						|| colType.equals("java.sql.Timestamp")
						|| colType.equals("java.sql.Date")) {
					sql += colName + "='"+ JUtilString.replaceAll(value.toString(), "'", "\'")+ "',";
				} else {
					sql += colName + "=" + value.toString() + ",";
				}
			}
		}
		sql=sql.substring(0,sql.length()-1);
		String condition="";
		for(int i=0;i<conditionKeys.length;i++){
			conditionKeys[i]=factory.getColName(tblName,conditionKeys[i]);
			Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(conditionKeys[i]),null);
			Object keyValue=method.invoke(vo,(Object[])null);
			if(keyValue==null){
				throw new Exception("主键值不能为空"+tblName+"->"+conditionKeys[i]);
			}
			if(keyValue instanceof Integer||keyValue instanceof Float||keyValue instanceof Double){
				condition+=conditionKeys[i]+"="+keyValue+" and ";
			}else{
				condition+=conditionKeys[i]+"='"+keyValue+"' and ";
			}
		}
		condition=condition.substring(0,condition.length()-5);
		sql+=" where "+condition;
		//生成sql end
		
		return sql;
	}

	/**
	 * 得到与将DAO.insert(Object vo)等同效果的SQL
	 * @param vo 与数据表记录对应的value object
	 * @param factory
	 * @return
	 * @throws Exception
	 */
	public static String retrieveInsertSQL(Object vo,DAOFactory factory)throws Exception {
		if (vo == null) {
			throw new Exception("待插入对象为空");
		}

		Class cls = vo.getClass();
		String tblName=factory.getTrueTblName(vo);
		List cols=factory.getColumns(tblName);	
		String sql="insert into "+tblName+" (";
		String sqlValues=") VALUES (";
		for(int i=0;i<cols.size();i++){
			Column col=(Column)cols.get(i);
			String colName=col.colName;
			Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
			Object value=method.invoke(vo,(Object[])null);
			
			if(value==null){
				sql+=colName+",";
				sqlValues+="null,";
			}else{
				sql+=colName+",";

				String colType=value.getClass().getName();
				if (colType.equals("java.io.InputStream")
						|| colType.equals("java.sql.Blob")
						|| colType.equals("java.sql.Clob")) {
					throw new Exception("retrieveUpdateSQL 不支持大字段");
				} else if (colType.equals("java.lang.String")
						|| colType.equals("java.sql.Timestamp")
						|| colType.equals("java.sql.Date")) {
					sqlValues += "'" + JUtilString.replaceAll(value.toString(), "'", "\'")+ "',";
				} else {
					sqlValues += value.toString() + ",";
				}
			}
		}
		sql=sql.substring(0,sql.length()-1);
		sql+=sqlValues;
		sql=sql.substring(0,sql.length()-1);
		sql+=")";
		
		sqlValues=null;
		
		return sql;
	}

	/**
	 * 调整数据类型（有些RDBMS表示数据类型的整型值于java.sql.Types中的定义不一致，进行转换）
	 * 
	 * @param dbType 数据库类型，目前取值范围是DAO.DB_TYPE_DB2,DAO.DB_TYPE_ORACLE,DAO.DB_TYPE_SQLSERVER,DAO.DB_TYPE_MYSQL
	 * @param type 表示字段类型的整型值
	 * @param colTypeName  字段名
	 * 
	 * @return 经过调整的表示字段类型的整型值
	 * @throws Exception
	 */
	public static int adjustDataType(String dbType, int type, String colTypeName)throws Exception {
		if (dbType == null) {
			return type;
		}
		if (dbType.equals(DAO.DB_TYPE_ORACLE)) {
			if (type == -100) {
				type = 93;
			}
		} else if (dbType.equals(DAO.DB_TYPE_MYSQL)) {
			if (type == 12 && colTypeName.equals("UNKNOWN")) {
				type = 3;
			}
		}
		return type;
	}
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	public static String deleteCriminalChars(String string){
		if(string==null||string.equals("")) return string;
		
		string=JUtilString.replaceAll(string, "'", "\\'");
		return string;
	}
	
	public static void main(String[] args){
		System.out.println(deleteCriminalChars("d'a"));
	}
}
