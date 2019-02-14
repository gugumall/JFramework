package j.dao.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import j.log.Logger;
import j.util.JUtilCompressor;

/**
 * PreparedStatement和ResultSet的一些get/set方法，保存在map中，key为数据库字段类型、value为get/set方法。以简化反射操作时代码编写。
 * @author 肖炯
 *
 */
public class Methods {
	private static Logger log=Logger.create(Methods.class);
	
	public static final Map settersOfPreparedStatement=new HashMap();//java.sql.PreparedStatement 的setXXX(int index,Object value)方法
	public static final Map gettersOfResultSetByIndex=new HashMap();//java.sql.ResultSet 的getXXX(int columnIndex)方法
	public static final Map gettersOfResultSetByName=new HashMap();//java.sql.ResultSet 的getXXX(String columnName)方法
	
	/**
	 * 
	 */
	static{
		Class[] paras=null;
		try{
			Class pstmtCls=java.sql.PreparedStatement.class;
			//java.sql.Types.BOOLEAN
			paras=new Class[]{Integer.TYPE,Boolean.TYPE}; 
			settersOfPreparedStatement.put(new Integer(Types.BOOLEAN),pstmtCls.getMethod("setBoolean",paras));
			
			//java.sql.Types.ARRAY
//			paras=new Class[]{Integer.TYPE,java.sql.Array.class}; 
//			settersOfPreparedStatement.put(new Integer(Types.ARRAY),pstmtCls.getMethod("setArray",paras));
			
			//java.sql.Types.BIT
			paras=new Class[]{Integer.TYPE,Boolean.TYPE}; 
			settersOfPreparedStatement.put(new Integer(Types.BIT),pstmtCls.getMethod("setBoolean",paras));
			
			//java.sql.Types.BLOB 
			paras=new Class[]{Integer.TYPE,java.sql.Blob.class}; 
			settersOfPreparedStatement.put(new Integer(Types.BLOB),pstmtCls.getMethod("setBlob",paras));
			
			//java.sql.Types.CHAR
			paras=new Class[]{Integer.TYPE,String.class}; 
			settersOfPreparedStatement.put(new Integer(Types.CHAR),pstmtCls.getMethod("setString",paras));
			
			//java.sql.Types.CLOB
			paras=new Class[]{Integer.TYPE,java.sql.Clob.class}; 
			settersOfPreparedStatement.put(new Integer(Types.CLOB),pstmtCls.getMethod("setClob",paras));
						
			//java.sql.Types.DATE
			paras=new Class[]{Integer.TYPE,java.sql.Date.class}; 
			settersOfPreparedStatement.put(new Integer(Types.DATE),pstmtCls.getMethod("setDate",paras));
			
			//java.sql.Types.DECIMAL
			paras=new Class[]{Integer.TYPE,BigDecimal.class}; 
			settersOfPreparedStatement.put(new Integer(Types.DECIMAL),pstmtCls.getMethod("setBigDecimal",paras));
			
			//java.sql.Types.DOUBLE
			paras=new Class[]{Integer.TYPE,Double.TYPE}; 
			settersOfPreparedStatement.put(new Integer(Types.DOUBLE),pstmtCls.getMethod("setDouble",paras));
			
			//java.sql.Types.REAL
			paras=new Class[]{Integer.TYPE,Double.TYPE}; 
			settersOfPreparedStatement.put(new Integer(Types.REAL),pstmtCls.getMethod("setDouble",paras));
			
			//java.sql.Types.FLOAT
			paras=new Class[]{Integer.TYPE,Float.TYPE}; 
			settersOfPreparedStatement.put(new Integer(Types.FLOAT),pstmtCls.getMethod("setFloat",paras));

			//java.sql.Types.INTEGER
			paras=new Class[]{Integer.TYPE,Integer.TYPE}; 
			settersOfPreparedStatement.put(new Integer(Types.INTEGER),pstmtCls.getMethod("setInt",paras));
			
			//java.sql.Types.TINYINT 
			paras=new Class[]{Integer.TYPE,Short.TYPE}; 
			settersOfPreparedStatement.put(new Integer(Types.TINYINT),pstmtCls.getMethod("setShort",paras));
			
			//java.sql.Types.SMALLINT
			paras=new Class[]{Integer.TYPE,Short.TYPE}; 
			settersOfPreparedStatement.put(new Integer(Types.SMALLINT),pstmtCls.getMethod("setShort",paras));
			
			//java.sql.Types.BIGINT
			paras=new Class[]{Integer.TYPE,Long.TYPE}; 
			settersOfPreparedStatement.put(new Integer(Types.BIGINT),pstmtCls.getMethod("setLong",paras));
			
			//java.sql.Types.JAVA_OBJECT
//			paras=new Class[]{Integer.TYPE,Object.class}; 
//			settersOfPreparedStatement.put(new Integer(Types.JAVA_OBJECT),pstmtCls.getMethod("setObject",paras));
			
			//java.sql.Types.LONGVARBINARY
			paras=new Class[]{Integer.TYPE,java.io.InputStream.class,Integer.TYPE};
			settersOfPreparedStatement.put(new Integer(Types.LONGVARBINARY),pstmtCls.getMethod("setBinaryStream",paras));
			
			//java.sql.Types.BINARY
			paras=new Class[]{Integer.TYPE,java.io.InputStream.class,Integer.TYPE}; 
			settersOfPreparedStatement.put(new Integer(Types.BINARY),pstmtCls.getMethod("setBinaryStream",paras));
			
			//java.sql.Types.VARBINARY
			paras=new Class[]{Integer.TYPE,java.io.InputStream.class,Integer.TYPE}; 
			settersOfPreparedStatement.put(new Integer(Types.VARBINARY),pstmtCls.getMethod("setBinaryStream",paras));
			
			//java.sql.Types.LONGVARCHAR
			paras=new Class[]{Integer.TYPE,String.class}; 
			settersOfPreparedStatement.put(new Integer(Types.LONGVARCHAR),pstmtCls.getMethod("setString",paras));

			//java.sql.Types.NUMERIC 
			paras=new Class[]{Integer.TYPE,BigDecimal.class}; 
			settersOfPreparedStatement.put(new Integer(Types.NUMERIC),pstmtCls.getMethod("setBigDecimal",paras));
			
			//java.sql.Types.REF
//			paras=new Class[]{Integer.TYPE,java.sql.Ref.class}; 
//			settersOfPreparedStatement.put(new Integer(Types.REF),pstmtCls.getMethod("setRef",paras));			
			
			//java.sql.Types.TIME 
			paras=new Class[]{Integer.TYPE,java.sql.Time.class}; 
			settersOfPreparedStatement.put(new Integer(Types.TIME),pstmtCls.getMethod("setTime",paras));
			
			//java.sql.Types.TIMESTAMP
			paras=new Class[]{Integer.TYPE,java.sql.Timestamp.class}; 
			settersOfPreparedStatement.put(new Integer(Types.TIMESTAMP),pstmtCls.getMethod("setTimestamp",paras));

			//java.sql.Types.VARCHAR
			paras=new Class[]{Integer.TYPE,String.class}; 
			settersOfPreparedStatement.put(new Integer(Types.VARCHAR),pstmtCls.getMethod("setString",paras));
			
			//java.sql.Types.OTHER
			paras=new Class[]{Integer.TYPE,Object.class}; 
			settersOfPreparedStatement.put(new Integer(Types.OTHER),pstmtCls.getMethod("setObject",paras));
			
			//java.sql.Types.DATALINK
			//java.sql.Types.DISTINCT
			//java.sql.Types.NULL
			//java.sql.Types.STRUCT
		}catch(Exception e){
			log.log(e,Logger.LEVEL_FATAL);
		}
		
		try{
			paras=new Class[]{Integer.TYPE}; 			
			Class rsCls=java.sql.ResultSet.class;
			
			//java.sql.Types.BOOLEAN			
			gettersOfResultSetByIndex.put(new Integer(Types.BOOLEAN),rsCls.getMethod("getBoolean",paras));
			
			//java.sql.Types.ARRAY
//			gettersOfResultSetByIndex.put(new Integer(Types.ARRAY),rsCls.getMethod("getArray",paras));
			
			//java.sql.Types.BIT
			gettersOfResultSetByIndex.put(new Integer(Types.BIT),rsCls.getMethod("getBoolean",paras));
			
			//java.sql.Types.BLOB 
			gettersOfResultSetByIndex.put(new Integer(Types.BLOB),rsCls.getMethod("getBlob",paras));
			
			//java.sql.Types.CHAR
			gettersOfResultSetByIndex.put(new Integer(Types.CHAR),rsCls.getMethod("getString",paras));
			
			//java.sql.Types.CLOB
			gettersOfResultSetByName.put(new Integer(Types.CLOB),rsCls.getMethod("getClob",paras));
						
			//java.sql.Types.DATE
			gettersOfResultSetByIndex.put(new Integer(Types.DATE),rsCls.getMethod("getDate",paras));
			
			//java.sql.Types.DECIMAL
			gettersOfResultSetByIndex.put(new Integer(Types.DECIMAL),rsCls.getMethod("getBigDecimal",paras));
			
			//java.sql.Types.DOUBLE
			gettersOfResultSetByIndex.put(new Integer(Types.DOUBLE),rsCls.getMethod("getDouble",paras));
			
			//java.sql.Types.REAL
			gettersOfResultSetByIndex.put(new Integer(Types.REAL),rsCls.getMethod("getDouble",paras));
			
			//java.sql.Types.FLOAT
			gettersOfResultSetByIndex.put(new Integer(Types.FLOAT),rsCls.getMethod("getFloat",paras));

			//java.sql.Types.INTEGER
			gettersOfResultSetByIndex.put(new Integer(Types.INTEGER),rsCls.getMethod("getInt",paras));
			
			//java.sql.Types.TINYINT 
			gettersOfResultSetByIndex.put(new Integer(Types.TINYINT),rsCls.getMethod("getShort",paras));
			
			//java.sql.Types.SMALLINT
			gettersOfResultSetByIndex.put(new Integer(Types.SMALLINT),rsCls.getMethod("getShort",paras));
			
			//java.sql.Types.BIGINT
			gettersOfResultSetByIndex.put(new Integer(Types.BIGINT),rsCls.getMethod("getLong",paras));
			
			//java.sql.Types.JAVA_OBJECT
//			gettersOfResultSetByIndex.put(new Integer(Types.JAVA_OBJECT),rsCls.getMethod("getObject",paras));
			
			//java.sql.Types.LONGVARBINARY
			gettersOfResultSetByIndex.put(new Integer(Types.LONGVARBINARY),rsCls.getMethod("getBinaryStream",paras));
			
			//java.sql.Types.BINARY
			gettersOfResultSetByIndex.put(new Integer(Types.BINARY),rsCls.getMethod("getBinaryStream",paras));
			
			//java.sql.Types.VARBINARY 
			gettersOfResultSetByIndex.put(new Integer(Types.VARBINARY),rsCls.getMethod("getBinaryStream",paras));
			
			//java.sql.Types.LONGVARCHAR 
			gettersOfResultSetByIndex.put(new Integer(Types.LONGVARCHAR),rsCls.getMethod("getString",paras));

			//java.sql.Types.NUMERIC 
			gettersOfResultSetByIndex.put(new Integer(Types.NUMERIC),rsCls.getMethod("getBigDecimal",paras));
			
			//java.sql.Types.REF
//			gettersOfResultSetByIndex.put(new Integer(Types.REF),rsCls.getMethod("getRef",paras));			
			
			//java.sql.Types.TIME 
			gettersOfResultSetByIndex.put(new Integer(Types.TIME),rsCls.getMethod("getTime",paras));
			
			//java.sql.Types.TIMESTAMP; 
			gettersOfResultSetByIndex.put(new Integer(Types.TIMESTAMP),rsCls.getMethod("getTimestamp",paras));

			//java.sql.Types.VARCHAR
			gettersOfResultSetByIndex.put(new Integer(Types.VARCHAR),rsCls.getMethod("getString",paras));
			
			//java.sql.Types.OTHER
			gettersOfResultSetByIndex.put(new Integer(Types.OTHER),rsCls.getMethod("getObject",paras));
			
			//java.sql.Types.DATALINK
			//java.sql.Types.DISTINCT
			//java.sql.Types.NULL
			//java.sql.Types.STRUCT
		}catch(Exception e){
			log.log(e,Logger.LEVEL_FATAL);
		}		
		
		try{
			paras=new Class[]{String.class}; 			
			Class rsCls=java.sql.ResultSet.class;
			
			//java.sql.Types.BOOLEAN			
			gettersOfResultSetByName.put(new Integer(Types.BOOLEAN),rsCls.getMethod("getBoolean",paras));
			
			//java.sql.Types.ARRAY
//			gettersOfResultSetByName.put(new Integer(Types.ARRAY),rsCls.getMethod("getArray",paras));
			
			//java.sql.Types.BIT
			gettersOfResultSetByName.put(new Integer(Types.BIT),rsCls.getMethod("getBoolean",paras));
			
			//java.sql.Types.BLOB 
			gettersOfResultSetByName.put(new Integer(Types.BLOB),rsCls.getMethod("getBlob",paras));
			
			//java.sql.Types.CHAR
			gettersOfResultSetByName.put(new Integer(Types.CHAR),rsCls.getMethod("getString",paras));
			
			//java.sql.Types.CLOB
			gettersOfResultSetByName.put(new Integer(Types.CLOB),rsCls.getMethod("getClob",paras));
						
			//java.sql.Types.DATE
			gettersOfResultSetByName.put(new Integer(Types.DATE),rsCls.getMethod("getDate",paras));
			
			//java.sql.Types.DECIMAL
			gettersOfResultSetByName.put(new Integer(Types.DECIMAL),rsCls.getMethod("getBigDecimal",paras));
			
			//java.sql.Types.DOUBLE
			gettersOfResultSetByName.put(new Integer(Types.DOUBLE),rsCls.getMethod("getDouble",paras));
			
			//java.sql.Types.REAL
			gettersOfResultSetByName.put(new Integer(Types.REAL),rsCls.getMethod("getDouble",paras));
			
			//java.sql.Types.FLOAT
			gettersOfResultSetByName.put(new Integer(Types.FLOAT),rsCls.getMethod("getFloat",paras));

			//java.sql.Types.INTEGER
			gettersOfResultSetByName.put(new Integer(Types.INTEGER),rsCls.getMethod("getInt",paras));
			
			//java.sql.Types.TINYINT 
			gettersOfResultSetByName.put(new Integer(Types.TINYINT),rsCls.getMethod("getShort",paras));
			
			//java.sql.Types.SMALLINT
			gettersOfResultSetByName.put(new Integer(Types.SMALLINT),rsCls.getMethod("getShort",paras));
			
			//java.sql.Types.BIGINT
			gettersOfResultSetByName.put(new Integer(Types.BIGINT),rsCls.getMethod("getLong",paras));
			
			//java.sql.Types.JAVA_OBJECT
//			gettersOfResultSetByName.put(new Integer(Types.JAVA_OBJECT),rsCls.getMethod("getObject",paras));
			
			//java.sql.Types.LONGVARBINARY
			gettersOfResultSetByName.put(new Integer(Types.LONGVARBINARY),rsCls.getMethod("getBinaryStream",paras));
			
			//java.sql.Types.BINARY
			gettersOfResultSetByName.put(new Integer(Types.BINARY),rsCls.getMethod("getBinaryStream",paras));
			
			//java.sql.Types.VARBINARY 
			gettersOfResultSetByName.put(new Integer(Types.VARBINARY),rsCls.getMethod("getBinaryStream",paras));
			
			//java.sql.Types.LONGVARCHAR 
			gettersOfResultSetByName.put(new Integer(Types.LONGVARCHAR),rsCls.getMethod("getString",paras));

			//java.sql.Types.NUMERIC 
			gettersOfResultSetByName.put(new Integer(Types.NUMERIC),rsCls.getMethod("getBigDecimal",paras));
			
			//java.sql.Types.REF
//			gettersOfResultSetByName.put(new Integer(Types.REF),rsCls.getMethod("getRef",paras));			
			
			//java.sql.Types.TIME 
			gettersOfResultSetByName.put(new Integer(Types.TIME),rsCls.getMethod("getTime",paras));
			
			//java.sql.Types.TIMESTAMP; 
			gettersOfResultSetByName.put(new Integer(Types.TIMESTAMP),rsCls.getMethod("getTimestamp",paras));

			//java.sql.Types.VARCHAR
			gettersOfResultSetByName.put(new Integer(Types.VARCHAR),rsCls.getMethod("getString",paras));
			
			//java.sql.Types.OTHER
			gettersOfResultSetByName.put(new Integer(Types.OTHER),rsCls.getMethod("getObject",paras));
			
			//java.sql.Types.DATALINK
			//java.sql.Types.DISTINCT
			//java.sql.Types.NULL
			//java.sql.Types.STRUCT
		}catch(Exception e){
			log.log(e,Logger.LEVEL_FATAL);
		}				
	}
	
	/**
	 * 调用PreparedStatement的setXXX(int index,Object value)方法
	 * @param colType
	 * @param obj
	 * @param paras
	 * @return
	 */
	public static Object set(int colType,boolean gzip,Object obj,Object[] paras)throws Exception{
		if(gzip&&paras!=null&&paras.length>1){
			Object value=paras[1];
			if(value!=null&&(value instanceof String)){
				try{
					//尝试解压，以避免重复执行gzip压缩
					value=JUtilCompressor.gunzipString((String)value,"UTF-8");
				}catch(Exception e){}
				
				try{
					//压缩
					value=JUtilCompressor.gzipString((String)value,"UTF-8");
					paras[1]=value;
				}catch(Exception e){}
			}
		}
		Method method=(Method)settersOfPreparedStatement.get(new Integer(colType));
		try{
			return method.invoke(obj,paras);
		}catch(Exception e){
			//log.log(colType+","+method.toString()+","+paras[0]+","+paras[1].getClass(),-1);
			throw e;
		}
	}
	
	/**
	 * 调用ResultSet的getXXX(int columnIndex)方法
	 * @param colType
	 * @param obj
	 * @param index
	 * @return
	 * @throws Exception
	 */
	public static Object get(int colType,ResultSet obj,int index)throws Exception{
		Method method=(Method)gettersOfResultSetByIndex.get(new Integer(colType));
		//log.log(method.toString(),Logger.LEVEL_DEBUG);
		Object ret= method.invoke(obj,new Object[]{new Integer(index)});
		//log.log("obj.wasNull()："+obj.wasNull()+","+ret,Logger.LEVEL_DEBUG);
		if(obj.wasNull()){
			return null;
		}else{
			return ret;
		}
	}
	
	/**
	 * 调用ResultSet的getXXX(int columnName)方法
	 * @param colType
	 * @param obj
	 * @param colName
	 * @return
	 * @throws Exception
	 */
	public static Object get(int colType,ResultSet obj,String colName)throws Exception{
		Method method=(Method)gettersOfResultSetByName.get(new Integer(colType));
		//log.log(method.toString(),Logger.LEVEL_DEBUG);
		Object ret= method.invoke(obj,new Object[]{colName});
		//log.log("obj.wasNull()："+obj.wasNull()+","+colName+","+ret,Logger.LEVEL_DEBUG);
		if(obj.wasNull()){
			return null;
		}else{
			return ret;
		}
	}
}
