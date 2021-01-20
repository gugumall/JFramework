package j.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import j.cache.JCacheParams;
import j.dao.type.Blob;
import j.dao.type.Clob;
import j.dao.util.Methods;
import j.dao.util.SQLUtil;
import j.log.Logger;
import j.sys.SysUtil;
import j.util.JUtilBean;
import j.util.JUtilCompressor;
import j.util.JUtilInputStream;
import j.util.JUtilMap;
import j.util.JUtilMath;
import j.util.JUtilString;

/**
 * @author 肖炯
 *
 */
public class RdbmsDao implements DAO {	
	private static Logger log=Logger.create(RdbmsDao.class);
	
	protected DAOFactory factory;//工厂，包含数据库配置信息等
	protected DBMirror mirror;//使用哪个镜像
	protected Connection connection;//数据库连接
	protected volatile boolean autoCommit;//是否自动提交
	protected volatile boolean closed;//是否已经关闭
	
	protected volatile long lastUsingTime=-1;//最近一次使用时间
	protected volatile boolean using=false;//是否处于操作中
	protected long timeout=120000;//超时，默认2分钟
	protected String caller=null;//创建此对象的类的名字
	protected volatile boolean pluginEnabled=true;
	protected volatile boolean readonly=false;
	protected volatile long lastTest=SysUtil.getNow();
	
	
	/**
	 * 初始化
	 *
	 */
	public RdbmsDao(){
		autoCommit=true;
		closed=false;
		lastUsingTime=SysUtil.getNow();
		using=false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#getLastTest()
	 */
	public long getLastTest(){
		return this.lastTest;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#setLastTest(long)
	 */
	public void setLastTest(long time){
		this.lastTest=time;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#getReadOnly()
	 */
	public boolean getReadOnly(){
		return this.readonly;
	}
	
	/*
	 * 
	 */
	public void setReadOnly(boolean readonly){
		this.readonly=readonly;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#getMirror()
	 */
	public DBMirror getMirror(){
		return this.mirror;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#getCaller()
	 */
	public String getCaller(){
		return caller;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#setCaller(java.lang.String)
	 */
	public void setCaller(String caller){
		this.caller=caller;
	}

	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#getTimeout()
	 */
	public long getTimeout(){
		return timeout;		
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#setTimeout(long)
	 */
	public void setTimeout(long timeout){
		this.timeout=timeout;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#getUpdateTime()
	 */
	public long getLastUsingTime(){
		return lastUsingTime;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#isUsing()
	 */
	public boolean isUsing(){
		return using;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#begin()
	 */
	public void begin(){
		lastUsingTime=SysUtil.getNow();
		using=true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#finish()
	 */
	public void finish(){
		lastUsingTime=SysUtil.getNow();
		using=false;
	}
	

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#beginTransaction()
	 */
	public void beginTransaction() throws Exception {		
		autoCommit=false;
		connection.setAutoCommit(false);
		if(factory.getPlugin()!=null&&this.pluginEnabled){
			factory.getPlugin().onBeginTransaction();
		}
	}
		

	/* 
	 *  (non-Javadoc)
	 * @see j.dao.DAO#isInTransaction()
	 */
	public boolean isInTransaction(){
		//是否处于事务状态
		return !autoCommit;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#commit()
	 */
	public void commit() throws Exception {		
		connection.commit();
		connection.setAutoCommit(true);
		autoCommit=true;
		if(factory.getPlugin()!=null&&this.pluginEnabled){
			factory.getPlugin().onCommit();
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#rollback()
	 */
	public void rollback() throws Exception {	
		connection.rollback();
		connection.setAutoCommit(true);
		autoCommit=true;
		if(factory.getPlugin()!=null&&this.pluginEnabled){
			factory.getPlugin().onRollback();
		}
	}
	


	/* (non-Javadoc)
	 * @see j.sdk.dao.DAO#isClosed()
	 */
	public boolean isClosed() {
		return closed;
	}	

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#close()
	 */
	public void close()throws Exception{		
		if(isInTransaction()){//如果在事务中，尝试回滚并结束事务
			try{
				rollback();
			}catch(Exception e){}			
		}
		
		try{
			factory.close(this,connection);
		}catch(Exception e){}
		
		closed=true;
		using=false;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#beforeAnyInvocation()
	 */
	public void beforeAnyInvocation() throws Exception {

	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#afterAnyInvocation()
	 */
	public void afterAnyInvocation() throws Exception {

	}		
	
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#onException()
	 */
	public void onException(){
		try{
			rollback();
		}catch(Exception e){}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#getConnection()
	 */
	public Connection getConnection() {
		return connection;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#getFactory()
	 */
	public DAOFactory getFactory(){
		return factory;
	}

	/* 
	 *  (non-Javadoc)
	 * @see j.dao.DAO#getSQLWithRowSetLimit(java.lang.String, int, int)
	 */
	public String getSQLWithRowSetLimit(String sql, int start, int end) throws Exception{
		throw new Exception("由Dialect实现");
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#supportsLimitOffset()
	 */
	public boolean supportsLimitOffset() throws Exception{
		throw new Exception("由Dialect实现");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#find(java.lang.String)
	 */
	public StmtAndRs find(String sql) throws Exception {		
		return find(sql,0,0);
	} 
	

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#find(java.lang.String, int, int)
	 */
	public StmtAndRs find(String sql,int RPP, int PN) throws Exception {
		return findScale(sql,RPP*(PN-1),RPP*PN);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#findScale(java.lang.String, int, int)
	 */
	public StmtAndRs findScale(String sql,int start, int end) throws Exception {
		if(SQLUtil.sqlInjection(sql)!=null) return null;
		
		Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = null;
		try {
			if(start<0||end<=0||start>=end){
				//throw new Exception("指定的范围的参数小于零");
				//throw new Exception("指定的范围的start 大于等于 end");
				rs = stmt.executeQuery(sql);
			}else{
				stmt.setMaxRows(end-start);
				sql=getSQLWithRowSetLimit(sql,start,end);
				
				rs = stmt.executeQuery(sql);
				if(!supportsLimitOffset()){//不支持分页
					try{
						rs.absolute(start);
					}catch(Exception ex){
						throw ex;
					}
				}
			}
			
			return new StmtAndRs(stmt,rs,sql);
		} catch (Exception e) {
			try{
				rs.close();
			}catch(Exception ex){}
			try{
				stmt.close();
			}catch(Exception ex){}
			throw e;
		}
	}	
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#find(java.lang.String, java.lang.Class, java.lang.String)
	 */
	public List find(String sql,Class cls,String excludedColumns)throws Exception{
		return find(sql,cls,excludedColumns,0,0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#find(java.lang.String, java.lang.Class, java.util.List)
	 */
	public List find(String sql,Class cls,List<String> excludedColumns)throws Exception{
		return find(sql,cls,excludedColumns,0,0);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#find(java.lang.String, java.lang.Class, java.lang.String, int, int)
	 */
	public List find(String sql,Class cls,String excludedColumns,int RPP,int PN)throws Exception{
		return findScale(sql,cls,excludedColumns,RPP*(PN-1),RPP*PN);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#find(java.lang.String, java.lang.Class, java.util.List, int, int)
	 */
	public List find(String sql,Class cls,List<String> excludedColumns,int RPP,int PN)throws Exception{
		return findScale(sql,cls,excludedColumns,RPP*(PN-1),RPP*PN);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#findScale(java.lang.String, java.lang.Class, java.lang.String, int, int)
	 */
	public List findScale(String sql,Class cls,String excludedColumns,int start,int end)throws Exception{
		if(excludedColumns==null || "".equals(excludedColumns)) {
			return findScale(sql,cls,(List)null,start,end);
		}else {
			//excludedColumns 某些不读取的列，格式：{列名1}{列名2}
			List<String> _excludedColumns=new ArrayList();
			String[] temp=JUtilString.getTokens(excludedColumns, "}{");
			for(int i=0; i<temp.length; i++) {
				if(temp[i].startsWith("{")) temp[i]=temp[i].substring(1);
				if(temp[i].endsWith("}")) temp[i]=temp[i].substring(temp[i].length()-1);
				_excludedColumns.add(temp[i]);
			}
			return findScale(sql,cls,_excludedColumns,start,end);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#findScale(java.lang.String, java.lang.Class, java.util.List, int, int)
	 */
	public List findScale(String sql,Class cls,List<String> excludedColumns,int start,int end)throws Exception{
		if(SQLUtil.sqlInjection(sql)!=null) return null;
		
		StmtAndRs sr=null;
		try {			
			sr=findScale(sql,start,end);
			if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
			
			ResultSet rs = sr.resultSet();
			List results=new ArrayList();
			Field[] fields=cls.getDeclaredFields();
			String tableName=SQLUtil.retrieveTableNameFromSQL(sql);
			
			Map<String, Method> setterCache=new HashMap();
			Map<String, String> setterNameCache=new HashMap();
	        Map<String, Integer> colTypeCache=new HashMap();
	        Map<String, String> colNameCache=new HashMap();
	        Map<String, Boolean> isGzipCache=new HashMap();
	        for(int i=0;i<fields.length;i++){
				String fieldName=fields[i].getName();
				String colName=factory.getColName(tableName,fieldName);
				if(excludedColumns!=null 
						&& (excludedColumns.contains(fieldName) || excludedColumns.contains(colName))){
					continue;
				}
				
				Method setter=factory.getUnregisterSetter(cls, fieldName, new Class[]{fields[i].getType()});
				if(setter==null) continue;
				setter.setAccessible(true);
				setterCache.put(fieldName, setter);
				setterNameCache.put(fieldName, factory.getUnregisterSetterName(fieldName));
				colTypeCache.put(fieldName, factory.getColType(tableName, fieldName));
				colNameCache.put(fieldName, colName);
				isGzipCache.put(fieldName, factory.getColIsGzip(tableName,fieldName));
	        }
			
			//MethodAccess access = factory.getMethodAccessOfClass(cls);
			
			while(rs!=null&&rs.next()){
				Object object=cls.newInstance();
				for(int i=0;i<fields.length;i++){
					String fieldName=fields[i].getName();
										
					Method setter=setterCache.get(fieldName);
					if(setter==null) continue;
					
					Object obj=null;
					try{
						obj = getObject(rs, 
								colTypeCache.get(fieldName), 
								colNameCache.get(fieldName), 
								isGzipCache.get(fieldName));
					}catch(Exception e){
						//log.log("fieldName:"+fieldName,Logger.LEVEL_DEBUG);
						//log.log(e,Logger.LEVEL_DEBUG);
					}
					if(obj!=null) setter.invoke(object,new Object[]{obj});
					//access.invoke(object, setterNameCache.get(fieldName), new Object[]{obj});
				}
				results.add(object);
			}	

			sr.close();
			
			return results;
		} catch (Exception e) {
			if(sr!=null) sr.close();
			throw e;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#findSingle(java.lang.String, java.lang.Class, java.lang.String)
	 */
	public Object findSingle(String sql,Class cls,String excludedColumns)throws Exception{
		List lst=findScale(sql,cls,excludedColumns,0,1);
		return lst==null||lst.isEmpty()?null:lst.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#findSingle(java.lang.String, java.lang.Class, java.util.List)
	 */
	public Object findSingle(String sql,Class cls,List<String> excludedColumns)throws Exception{
		List lst=findScale(sql,cls,excludedColumns,0,1);
		return lst==null||lst.isEmpty()?null:lst.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see j.sdk.dao.DAO#find(java.lang.String, java.lang.String)
	 */
	public List find(String tableName, String condition) throws Exception {		
		return find(tableName,condition,0,0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#find(java.lang.String, java.lang.String, java.util.List)
	 */
	public List find(String tableName, String condition,List<String> excludedColumns) throws Exception {		
		return find(tableName,condition,excludedColumns,0,0);
	}

	/* (non-Javadoc)
	 * @see j.sdk.dao.DAO#find(java.lang.String, java.lang.String, int, int)
	 */
	public List find(String tableName, String condition, int RPP, int PN) throws Exception {
		return findScale(tableName,condition,RPP*(PN-1),RPP*PN);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#find(java.lang.String, java.lang.String, java.util.List, int, int)
	 */
	public List find(String tableName, String condition,List<String> excludedColumns, int RPP, int PN) throws Exception {
		return findScale(tableName,condition,excludedColumns,RPP*(PN-1),RPP*PN);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#findScale(java.lang.String, java.lang.String, int, int)
	 */
	public List findScale(String tableName, String condition, int start, int end) throws Exception {
		return findScale(tableName,condition,(List)null,start,end);
	}	
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#findScale(java.lang.String, java.lang.String, java.util.List, int, int)
	 */
	public List findScale(String tableName, String condition,List<String> excludedColumns, int start, int end) throws Exception {
		List results=new ArrayList();
		StmtAndRs sr=null;
		try{
			if (tableName == null || tableName.trim().equals("")) {
				throw new Exception("没有指定表名或指定的表名为空");
			}
			tableName=factory.getTrueTblName(tableName);
			
			String tblClass=factory.getTblClass(tableName);
			if(tblClass==null){
				throw new Exception("class of table "+tableName+" not found");
			}
			Class cls=Class.forName(tblClass);
			
	        String sql = "select * from " + tableName;
	        if(condition != null && condition.trim().length() >= 3){
	            String tmpcondition = condition;
	            tmpcondition = tmpcondition.toLowerCase();
	            tmpcondition = JUtilString.replaceAll(tmpcondition, " ", "");
	            if(tmpcondition.indexOf("groupby")!=-1){
	            	throw new Exception("该方法不支持 GROUP BY 子句，请直接使用find(String sql) 或 find(String sql,int rpp,int pn)");
	            }
	            
	            if(tmpcondition.startsWith("orderby")){
	                sql = sql + " " + condition;
	            } else{
	                sql = sql + " where " + condition;
	            }
	        }

			results=new ArrayList();
			
	        sr=findScale(sql, start, end);
	        if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
	        
	        ResultSet rs = sr.resultSet();
	
	        List cols=factory.getColumns(tableName);
	        String colName=null;
	        
	        Map<String, Method> setterCache=new HashMap();
	        Map<String, String> setterNameCache=new HashMap();
	        Map<String, Integer> colTypeCache=new HashMap();
	        Map<String, Boolean> isGzipCache=new HashMap();
			for(int i=0;i<cols.size();i++){
				colName=((Column)cols.get(i)).colName;
				
				if(excludedColumns!=null 
						&&(excludedColumns.contains(colName) || excludedColumns.contains(JUtilBean.colNameToVariableName(colName)))) continue;
				
				Method setter=factory.getSetter(tableName,colName);
				if(setter==null) continue;
				setter.setAccessible(true);
				setterCache.put(colName, setter);
				setterNameCache.put(colName, factory.getSetterName(tableName, colName));
				colTypeCache.put(colName, factory.getColType(tableName, colName));
				isGzipCache.put(colName, factory.getColIsGzip(tableName,colName));
	        }
			
			//MethodAccess access = factory.getMethodAccessOfClass(cls);
	        
			while(rs.next()){
				Object object=cls.newInstance();
				for(int i=0;i<cols.size();i++){
					colName=((Column)cols.get(i)).colName;
				
					Method setter=setterCache.get(colName);
					if(setter==null) continue;
					
					Object obj=null;
					try{
						obj = getObject(rs, 
								colTypeCache.get(colName), 
								colName, 
								isGzipCache.get(colName));
					}catch(Exception e){
						//log.log("fieldName:"+fieldName,Logger.LEVEL_DEBUG);
						//log.log(e,Logger.LEVEL_DEBUG);
					}
					if(obj!=null) setter.invoke(object,new Object[]{obj});
					//access.invoke(object, setterNameCache.get(colName), new Object[]{obj});
				}
				results.add(object);
			}		
			sr.close();

			return results;
		}catch(Exception e){
			if(sr!=null) sr.close();
			throw e;
		}
	}	
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#findSingle(java.lang.String, java.lang.String)
	 */
	public Object findSingle(String tableName, String condition) throws Exception {
		List lst=findScale(tableName,condition,0,1);
		return lst==null||lst.isEmpty()?null:lst.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#findSingle(java.lang.String, java.lang.String, java.util.List)
	 */
	public Object findSingle(String tableName, String condition,List<String> excludedColumns) throws Exception {
		List lst=findScale(tableName,condition,excludedColumns,0,1);
		return lst==null||lst.isEmpty()?null:lst.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see j.sdk.dao.DAO#find(java.lang.String, java.lang.String)
	 */
	public List find(String tableName, String condition,Class cls) throws Exception {		
		return find(tableName,condition,cls,0,0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#find(java.lang.String, java.lang.String, java.lang.Class, java.util.List)
	 */
	public List find(String tableName, String condition,Class cls,List<String> excludedColumns) throws Exception {		
		return find(tableName,condition,cls,excludedColumns,0,0);
	}

	/* (non-Javadoc)
	 * @see j.sdk.dao.DAO#find(java.lang.String, java.lang.String, int, int)
	 */
	public List find(String tableName, String condition,Class cls, int RPP, int PN) throws Exception {
		return findScale(tableName,condition,cls,RPP*(PN-1),RPP*PN);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#find(java.lang.String, java.lang.String, java.lang.Class, java.util.List, int, int)
	 */
	public List find(String tableName, String condition,Class cls,List<String> excludedColumns, int RPP, int PN) throws Exception {
		return findScale(tableName,condition,cls,excludedColumns,RPP*(PN-1),RPP*PN);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#findScale(java.lang.String, java.lang.String, java.lang.Class, int, int)
	 */
	public List findScale(String tableName, String condition, Class cls, int start, int end) throws Exception {
		return findScale(tableName, condition, cls,(List)null, start, end);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#findScale(java.lang.String, java.lang.String, java.lang.Class, java.util.List, int, int)
	 */
	public List findScale(String tableName, String condition, Class cls,List<String> excludedColumns, int start, int end) throws Exception {
		List results=new ArrayList();
		StmtAndRs sr=null;
		try{
			if (tableName == null || tableName.trim().equals("")) {
				throw new Exception("没有指定表名或指定的表名为空");
			}
			tableName=factory.getTrueTblName(tableName);
			
	        String sql = "select * from " + tableName;
	        if(condition != null && condition.trim().length() >= 3){
	            String tmpcondition = condition;
	            tmpcondition = tmpcondition.toLowerCase();
	            tmpcondition = JUtilString.replaceAll(tmpcondition, " ", "");
	            if(tmpcondition.indexOf("groupby")!=-1){
	            	throw new Exception("该方法不支持 GROUP BY 子句，请直接使用find(String sql) 或 find(String sql,int rpp,int pn)");
	            }
	            
	            if(tmpcondition.startsWith("orderby")){
	                sql = sql + " " + condition;
	            } else{
	                sql = sql + " where " + condition;
	            }
	        }
	        
			results=new ArrayList();
			
	        sr=findScale(sql, start, end);
	        if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
	        
	        ResultSet rs=sr.resultSet();
	
			Field[] fields=cls.getDeclaredFields();
			
			Map<String, Method> setterCache=new HashMap();
			Map<String, String> setterNameCache=new HashMap();
	        Map<String, Integer> colTypeCache=new HashMap();
	        Map<String, String> colNameCache=new HashMap();
	        Map<String, Boolean> isGzipCache=new HashMap();
	        for(int i=0;i<fields.length;i++){
				String fieldName=fields[i].getName();
				String colName=factory.getColName(tableName,fieldName);
				if(excludedColumns!=null 
						&& (excludedColumns.contains(fieldName) || excludedColumns.contains(colName))){
					continue;
				}
				
				Method setter=factory.getUnregisterSetter(cls, fieldName, new Class[]{fields[i].getType()});
				if(setter==null) continue;
				setter.setAccessible(true);
				setterCache.put(fieldName, setter);
				setterNameCache.put(fieldName, factory.getUnregisterSetterName(fieldName));
				colTypeCache.put(fieldName, factory.getColType(tableName, fieldName));
				colNameCache.put(fieldName, colName);
				isGzipCache.put(fieldName, factory.getColIsGzip(tableName,fieldName));
	        }
			
			//MethodAccess access = factory.getMethodAccessOfClass(cls);
			
			while(rs!=null&&rs.next()){
				Object object=cls.newInstance();
				for(int i=0;i<fields.length;i++){
					String fieldName=fields[i].getName();
					
					Method setter=setterCache.get(fieldName);
					if(setter==null) continue;
					
					Object obj=null;
					try{
						obj = getObject(rs, 
								colTypeCache.get(fieldName), 
								colNameCache.get(fieldName), 
								isGzipCache.get(fieldName));
					}catch(Exception e){
						//log.log("fieldName:"+fieldName,Logger.LEVEL_DEBUG);
						//log.log(e,Logger.LEVEL_DEBUG);
					}

					if(obj!=null) setter.invoke(object,new Object[]{obj});
					//access.invoke(object, setterNameCache.get(fieldName), new Object[]{obj});
				}
				results.add(object);
			}	
			sr.close();
			return results;
		}catch(Exception e){
			if(sr!=null) sr.close();
			throw e;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#findSingle(java.lang.String, java.lang.String, java.lang.Class)
	 */
	public Object findSingle(String tableName, String condition, Class cls) throws Exception {
		List lst=findScale(tableName,condition,cls,0,1);
		return lst==null||lst.isEmpty()?null:lst.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#findSingle(java.lang.String, java.lang.String, java.lang.Class, java.util.List)
	 */
	public Object findSingle(String tableName, String condition, Class cls,List<String> excludedColumns) throws Exception {
		List lst=findScale(tableName,condition,cls,excludedColumns,0,1);
		return lst==null||lst.isEmpty()?null:lst.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see j.sdk.dao.DAO#find(java.lang.String[], java.lang.String)
	 */
	public List find(String[] tableNames, String condition)throws Exception{		
		return find(tableNames,condition,0,0);
	}
	

	/* (non-Javadoc)
	 * @see j.sdk.dao.DAO#find(java.lang.String[], java.lang.String, int, int)
	 */
	public List find(String[] tableNames, String condition, int RPP, int PN) throws Exception {
		return findScale(tableNames,condition,RPP*(PN-1),RPP*PN);
	}
	

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#findScale(java.lang.String[], java.lang.String, int, int)
	 */
	public List findScale(String[] tableNames, String condition, int start, int end) throws Exception {
		List results=new ArrayList();
		StmtAndRs sr=null;
		try{
			if (tableNames == null || tableNames.length==0) {
				throw new Exception("多表查询，没有指定操作的表");
			}
			
			if(tableNames.length==1){
				return findScale(tableNames[0],condition,start,end);
			}
			
			if(condition==null||condition.trim().equals("")){
				throw new Exception("多表查询必须指定查询条件");
			}
			
			for(int i=0;i<tableNames.length;i++){
				if(tableNames[i]==null||tableNames[i].trim().equals("")){
					throw new Exception("指定了一个或多个为空的表名");
				}
			}
			
			//处理表名
			for(int i=0;i<tableNames.length;i++){
				String trueTblName=factory.getTrueTblName(tableNames[i]);
				condition=JUtilString.replaceAll(condition,tableNames[i]+".",trueTblName+".");
				tableNames[i]=trueTblName;
			}
							
			Map<String, Integer> allColsIndex=new HashMap();
			Map<String, Integer> allColsType=new HashMap();
			Map<String, Boolean> allColsIsGzip=new HashMap();
			
			//记住各表各列在结果集中的位置，同时生成sql		 
			int index=1;
			String sql="select ";
			String sql1=" from ";
			for(int i=0;i<tableNames.length;i++){
				sql1+=tableNames[i]+",";
				List cols=factory.getColumns(tableNames[i]);
				for(int j=0;j<cols.size();j++){
					Column col=(Column)cols.get(j);
					sql+=tableNames[i]+"."+col.colName+" AS C"+index+",";
					allColsIndex.put(tableNames[i]+"."+col.colName,new Integer(index));
					allColsType.put(tableNames[i]+"."+col.colName,new Integer(col.colType));
					allColsIsGzip.put(tableNames[i]+"."+col.colName,new Boolean(col.gzip));
					index++;
				}
			}
			sql=sql.substring(0,sql.length()-1);
			sql1=sql1.substring(0,sql1.length()-1);
			sql+=sql1;
			
			String order="";
			String tmpcondition=condition;
			
			condition=condition.toUpperCase();
			if(condition!=null&&condition.trim().length()>=3){
				int groupbyIndex=condition.indexOf("GROUP BY");
				if(groupbyIndex!=-1){
					throw new Exception("该方法不支持 GROUP BY 子句，请直接使用find(String sql) 或 find(String sql,int rpp,int pn)");
				}			
	
				int orderbyIndex=condition.indexOf("ORDER BY");
				if(orderbyIndex!=-1){
					order=condition.substring(orderbyIndex);
					
					tmpcondition=tmpcondition.substring(0,orderbyIndex);
	
					for(Iterator keys=allColsIndex.keySet().iterator();keys.hasNext();){
						String tableAndCol=(String)keys.next();
						String as="C"+(Integer)allColsIndex.get(tableAndCol);						
						order=JUtilString.replaceAll(order,tableAndCol.toUpperCase(),as);
					}			
				}
				condition=condition.trim();
				if(condition.startsWith("ORDER BY")){
					sql+=" "+tmpcondition+order;
				}else{
					sql+=" where "+tmpcondition+order;
				}
			}//记住各表各列在结果集中的位置，同时生成sql end	

			results=new ArrayList();
			
			//得到与数据库表名对应的类名
			//log.log("sql:"+sql,Logger.LEVEL_DEBUG);
			sr=findScale(sql,start,end);
	        if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
	        
			ResultSet rs=sr.resultSet();
			Class[] classes=new Class[tableNames.length];
			for(int i=0;i<tableNames.length;i++){
				classes[i]=Class.forName(factory.getTblClass(tableNames[i]));
				if(classes[i]==null){
					throw new Exception("class of table "+classes[i]+" not found");
				}
			}//得到与数据库表名对应的类名 end
			
			List[] colsOfClasses=new ArrayList[classes.length];
			for(int i=0;i<classes.length;i++){
				colsOfClasses[i]=factory.getColumns(factory.getTrueTblNameOfCls(classes[i]));
			}

			Map<String, Method> setterCache=new HashMap();
			Map<String, String> setterNameCache=new HashMap();
			for(int i=0;i<classes.length;i++){
				for(int j=0;j<colsOfClasses[i].size();j++){
					String colName=((Column)colsOfClasses[i].get(j)).colName;
					
					Method setter=factory.getSetter(tableNames[i],colName);
					if(setter==null) continue;
					setter.setAccessible(true);
					setterCache.put(tableNames[i]+"."+colName, setter);
					setterNameCache.put(tableNames[i]+"."+colName, factory.getSetterName(tableNames[i],colName));
				}
			}
			
			while(rs.next()){
				Object[] objects=new Object[classes.length];
				for(int i=0;i<classes.length;i++){
					objects[i]=classes[i].newInstance();
					
					//MethodAccess access = factory.getMethodAccessOfClass(classes[i]);
					
					for(int j=0;j<colsOfClasses[i].size();j++){
						String colName=((Column)colsOfClasses[i].get(j)).colName;
						
						String colNameOfTbl=tableNames[i]+"."+colName;
						
						Method setter=setterCache.get(colNameOfTbl);
						if(setter==null) continue;
						
						Object obj=null;
						try{
							obj=getObject(rs,
									allColsType.get(colNameOfTbl),
									allColsIndex.get(colNameOfTbl),
									allColsIsGzip.get(colNameOfTbl));
						}catch(Exception e){}
						if(obj!=null) setter.invoke(objects[i],new Object[]{obj});
						//access.invoke(objects[i], setterNameCache.get(colNameOfTbl), new Object[]{obj});
					}
				}
				results.add(objects);
			}
			sr.close();			
			return results;
		}catch(Exception e){
			if(sr!=null) sr.close();
			throw e;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#findSingle(java.lang.String[], java.lang.String)
	 */
	public Object findSingle(String[] tableNames, String condition)throws Exception{
		List lst=findScale(tableNames,condition,0,1);
		return lst==null||lst.isEmpty()?null:lst.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see j.sdk.dao.DAO#find(java.lang.String[], java.lang.String)
	 */
	public List find(String[] tableNames,Class[] CLSs, String condition)throws Exception{		
		return find(tableNames,CLSs,condition,0,0);
	}
	

	/* (non-Javadoc)
	 * @see j.sdk.dao.DAO#find(java.lang.String[], java.lang.String, int, int)
	 */
	public List find(String[] tableNames,Class[] CLSs, String condition, int RPP, int PN) throws Exception {
		return findScale(tableNames,CLSs,condition,RPP*(PN-1),RPP*PN);
	}	

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#findScale(java.lang.String[], java.lang.Class[], java.lang.String, int, int)
	 */
	public List findScale(String[] tableNames, Class[] CLSs, String condition, int start, int end) throws Exception {
		List results=new ArrayList();
		StmtAndRs sr=null;
		try{
			if (tableNames == null || tableNames.length==0) {
				throw new Exception("多表查询，没有指定操作的表");
			}
			
			if(tableNames.length==1){
				return findScale(tableNames[0],CLSs[0],condition,start,end);
			}
			
			if(condition==null||condition.trim().equals("")){
				throw new Exception("多表查询必须指定查询条件");
			}
			
			for(int i=0;i<tableNames.length;i++){
				if(tableNames[i]==null||tableNames[i].trim().equals("")){
					throw new Exception("指定了一个或多个为空的表名");
				}
			}
			
			//处理表名
			for(int i=0;i<tableNames.length;i++){
				String trueTblName=factory.getTrueTblName(tableNames[i]);
				condition=JUtilString.replaceAll(condition,tableNames[i]+".",trueTblName+".");
				tableNames[i]=trueTblName;
			}
							
			Map<String, Integer> allColsIndex=new HashMap();
			Map<String, Integer> allColsType=new HashMap();
			Map<String, Boolean> allColsIsGzip=new HashMap();
			
			//记住各表各列在结果集中的位置，同时生成sql		 
			int index=1;
			String sql="select ";
			String sql1=" from ";
			for(int i=0;i<tableNames.length;i++){
				sql1+=tableNames[i]+",";
				List cols=factory.getColumns(tableNames[i]);
				for(int j=0;j<cols.size();j++){
					Column col=(Column)cols.get(j);
					sql+=tableNames[i]+"."+col.colName+" AS C"+index+",";
					allColsIndex.put(tableNames[i]+"."+col.colName,new Integer(index));
					allColsType.put(tableNames[i]+"."+col.colName,new Integer(col.colType));
					allColsIsGzip.put(tableNames[i]+"."+col.colName,new Boolean(col.gzip));
					index++;
				}
			}
			sql=sql.substring(0,sql.length()-1);
			sql1=sql1.substring(0,sql1.length()-1);
			sql+=sql1;
			
			String order="";
			String tmpcondition=condition;
			
			condition=condition.toUpperCase();
			if(condition!=null&&condition.trim().length()>=3){
				//int groupbyIndex=condition.indexOf("GROUP BY");
				//if(groupbyIndex!=-1){
				//	throw new Exception("该方法不支持 GROUP BY 子句，请直接使用find(String sql) 或 find(String sql,int rpp,int pn)");
				//}			
	
				int orderbyIndex=condition.indexOf("ORDER BY");
				if(orderbyIndex!=-1){
					order=condition.substring(orderbyIndex);
					
					tmpcondition=tmpcondition.substring(0,orderbyIndex);
	
					for(Iterator keys=allColsIndex.keySet().iterator();keys.hasNext();){
						String tableAndCol=(String)keys.next();
						String as="C"+(Integer)allColsIndex.get(tableAndCol);						
						order=JUtilString.replaceAll(order,tableAndCol.toUpperCase(),as);
					}			
				}
				condition=condition.trim();
				if(condition.startsWith("ORDER BY")){
					sql+=" "+tmpcondition+order;
				}else{
					sql+=" where "+tmpcondition+order;
				}
			}//记住各表各列在结果集中的位置，同时生成sql end	

			results=new ArrayList();
			
			//得到与数据库表名对应的类名
			//log.log("sql:"+sql,Logger.LEVEL_DEBUG);
			sr=find(sql,start,end);
	        if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
	        
			ResultSet rs=sr.resultSet();
			
			Class[] classes=CLSs;
			
			Map[] colsOfClasses=new HashMap[classes.length];
			for(int i=0;i<classes.length;i++){
				colsOfClasses[i]=new HashMap();
				List cols=factory.getColumns(factory.getTrueTblName(tableNames[i]));
				for(int j=0;j<cols.size();j++){
					Column c=(Column)cols.get(j);
					colsOfClasses[i].put(JUtilBean.colNameToVariableName(c.colName),c.colName);
				}
			}
			
			List fieldsOfClasses=new ArrayList();
			for(int i=0;i<classes.length;i++){
				fieldsOfClasses.add(classes[i].getDeclaredFields());
			}
			
			Map<String, Method> setterCache=new HashMap();
			Map<String, String> setterNameCache=new HashMap();
			for(int i=0;i<classes.length;i++){
				Field[] fields=(Field[])fieldsOfClasses.get(i);
				for(int j=0;j<fields.length;j++){
					String fieldName=(fields[j]).getName();
					String colName=(String)colsOfClasses[i].get(fieldName);
					if(colName==null) continue;
					
					Method setter=factory.getUnregisterSetter(classes[i], fieldName, new Class[]{fields[j].getType()});
					if(setter==null) continue;
					setter.setAccessible(true);
					setterCache.put(tableNames[i]+"."+colName, setter);
					setterNameCache.put(tableNames[i]+"."+colName, factory.getSetterName(tableNames[i],colName));
				}
			}
	        
			while(rs.next()){
				Object[] objects=new Object[classes.length];;
				for(int i=0;i<classes.length;i++){
					objects[i]=classes[i].newInstance();
					
					//MethodAccess access = factory.getMethodAccessOfClass(classes[i]);
					
					Field[] fields=(Field[])fieldsOfClasses.get(i);
					for(int j=0;j<fields.length;j++){
						String fieldName=(fields[j]).getName();
						String colName=(String)colsOfClasses[i].get(fieldName);
						if(colName==null) continue;
						
						String colNameOfTbl=tableNames[i]+"."+colName;
						
						Method setter=setterCache.get(colNameOfTbl);
						if(setter==null) continue;
						
						Object obj=null;
						try{
							obj=getObject(rs,
									allColsType.get(colNameOfTbl),
									allColsIndex.get(colNameOfTbl),
									allColsIsGzip.get(colNameOfTbl));
						}catch(Exception e){}
						if(obj!=null) setter.invoke(objects[i],new Object[]{obj});		
						//access.invoke(objects[i], setterNameCache.get(colNameOfTbl), new Object[]{obj});				
					}
				}
				results.add(objects);
			}
			sr.close();			
			return results;
		}catch(Exception e){
			if(sr!=null) sr.close();
			throw e;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#findSingle(java.lang.String[], java.lang.Class[], java.lang.String)
	 */
	public Object findSingle(String[] tableNames, Class[] CLSs, String condition) throws Exception {
		List lst=findScale(tableNames,CLSs,condition,0,1);
		return lst==null||lst.isEmpty()?null:lst.get(0);
	}	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see j.sdk.dao.DAO#insert(java.lang.Object)
	 */
	public void insert(Object vo) throws Exception{		
		String tableName=factory.getTrueTblName(vo);
		insert(tableName,vo);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#insertIfNotExists(java.lang.Object)
	 */
	public void insertIfNotExists(Object vo) throws Exception{		
		insertIfNotExists(vo,new String[]{factory.getPkColumnName(vo)});
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#insertIfNotExists(java.lang.Object, java.lang.String[])
	 */
	public void insertIfNotExists(Object vo,String[] conditionKeys) throws Exception{	
		String tableName=factory.getTrueTblName(vo);
		insertIfNotExists(tableName,vo,conditionKeys);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#insert(java.lang.String, java.lang.Object)
	 */
	public void insert(String tableName,Object vo) throws Exception{		
		PreparedStatement pstmt=null;		
		try{
			if(factory.getPlugin()!=null&&pluginEnabled){
				factory.getPlugin().beforeInsert(vo);
			}
			if(vo==null){
				throw new Exception("待插入对象为空");
			}
			Class cls=vo.getClass();
			tableName=factory.getTrueTblName(tableName);
			List cols=factory.getColumns(tableName);	
			
			String sql="insert into "+tableName+"(";
			String sqlValues=")VALUES(";
			List values=new ArrayList();
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
					sqlValues+="?,";
				}
				values.add(i,value);
			}
			sql=sql.substring(0,sql.length()-1);
			sql+=sqlValues;
			sql=sql.substring(0,sql.length()-1);
			sql+=")";			
			//log.log("JDAO SQL: "+sql,Logger.LEVEL_DEBUG);
			pstmt=connection.prepareStatement(sql);
			
			int i=1;
			for(int index=1;index<=cols.size();index++){
				Column col=(Column)cols.get(index-1);
				int    colType=col.colType;
				Object[] paras=null;
				//从vo得到对应字段的值
				Object value=values.get(index-1);
				if(value==null){
					continue;
				}
				if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
					paras=new Object[3];
					InputStream is=(InputStream)value;				
					paras[0]=new Integer(i);
					paras[1]=is;
					paras[2]=new Integer(is.available());	
				}else if(colType==Types.BLOB){
					Blob blob=(Blob)value;
					paras=new Object[2];
					paras[0]=new Integer(i);
					paras[1]=blob;
				}else if(colType==Types.CLOB){
					Clob clob=(Clob)value;

					paras=new Object[2];
					paras[0]=new Integer(i);
					paras[1]=clob;
				}else{
					paras=new Object[2];
					paras[0]=new Integer(i);
					paras[1]=value;
				}
				Methods.set(colType,col.gzip,pstmt,paras);
				i++;
			}
			
			pstmt.execute();
			pstmt.close();
			if(factory.getPlugin()!=null&&pluginEnabled){
				factory.getPlugin().afterInsert(vo);
			}
		}catch(Exception e){
			try{
				pstmt.close();
			}catch(Exception ex){}
			throw e;		
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#insertIfNotExists(java.lang.String, java.lang.Object)
	 */
	public void insertIfNotExists(String tableName,Object vo) throws Exception{		
		insertIfNotExists(vo,new String[]{factory.getPkColumnName(vo)});
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#insertIfNotExists(java.lang.String, java.lang.Object, java.lang.String[])
	 */
	public void insertIfNotExists(String tableName,Object vo,String[] conditionKeys) throws Exception{		
		PreparedStatement pstmt=null;		
		try{
			if(factory.getPlugin()!=null&&pluginEnabled){
				factory.getPlugin().beforeInsert(vo);
			}
			if(vo==null){
				throw new Exception("待插入对象为空");
			}
			if(conditionKeys==null||conditionKeys.length==0){
				throw new Exception("没有指定主键");
			}
			
			Class cls=vo.getClass();
			tableName=factory.getTrueTblName(tableName);
			
			String condition="";
			for(int i=0;i<conditionKeys.length;i++){
				conditionKeys[i]=factory.getColName(tableName,conditionKeys[i]);
				Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(conditionKeys[i]),null);
				Object keyValue=method.invoke(vo,(Object[])null);
				if(keyValue==null){
					condition+=conditionKeys[i]+" is null and ";
				}else{
					if(keyValue instanceof Integer||keyValue instanceof Float||keyValue instanceof Double){
						condition+=conditionKeys[i]+"="+keyValue+" and ";
					}else{
						condition+=conditionKeys[i]+"='"+keyValue+"' and ";
					}
				}
			}
			condition=condition.substring(0,condition.length()-5);
			int exists=getRecordCnt(tableName,condition);
			if(exists>0) return;
			
			List cols=factory.getColumns(tableName);	
			
			String sql="insert into "+tableName+"(";
			String sqlValues=")VALUES(";
			List values=new ArrayList();
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
					sqlValues+="?,";
				}
				values.add(i,value);
			}
			sql=sql.substring(0,sql.length()-1);
			sql+=sqlValues;
			sql=sql.substring(0,sql.length()-1);
			sql+=")";			
			//log.log("JDAO SQL: "+sql,Logger.LEVEL_DEBUG);
			pstmt=connection.prepareStatement(sql);
			
			int i=1;
			for(int index=1;index<=cols.size();index++){
				Column col=(Column)cols.get(index-1);
				int    colType=col.colType;
				Object[] paras=null;
				//从vo得到对应字段的值
				Object value=values.get(index-1);
				if(value==null){
					continue;
				}
				if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
					paras=new Object[3];
					InputStream is=(InputStream)value;				
					paras[0]=new Integer(i);
					paras[1]=is;
					paras[2]=new Integer(is.available());	
				}else if(colType==Types.BLOB){
					Blob blob=(Blob)value;
					paras=new Object[2];
					paras[0]=new Integer(i);
					paras[1]=blob;
				}else if(colType==Types.CLOB){
					Clob clob=(Clob)value;

					paras=new Object[2];
					paras[0]=new Integer(i);
					paras[1]=clob;
				}else{
					paras=new Object[2];
					paras[0]=new Integer(i);
					paras[1]=value;
				}
				Methods.set(colType,col.gzip,pstmt,paras);
				i++;
			}
			
			pstmt.execute();
			pstmt.close();
			if(factory.getPlugin()!=null&&pluginEnabled){
				factory.getPlugin().afterInsert(vo);
			}
		}catch(Exception e){
			try{
				pstmt.close();
			}catch(Exception ex){}
			throw e;		
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#update(java.lang.String, java.util.Map, java.lang.String)
	 */
	public void update(String tableName,Map colsBeUpdated,String condition) throws Exception {
		PreparedStatement pstmt=null;
		try{
			if(factory.getPlugin()!=null&&pluginEnabled){
				factory.getPlugin().beforeUpdate(tableName, colsBeUpdated, condition);
			}
			if(colsBeUpdated==null||colsBeUpdated.isEmpty()){
				return;
			}
			tableName=factory.getTrueTblName(tableName);
			
			//生成sql
			String sql="update "+tableName+" set ";
			List cols=JUtilMap.keys(colsBeUpdated);
			for(int i=0;i<cols.size();i++){
				String colName=(String)cols.get(i);
				Object value=colsBeUpdated.get(colName);
				if(value==null){
					sql+=factory.getColName(tableName,colName)+"=null,";
					cols.remove(i);
					i--;
				}else{
					sql+=factory.getColName(tableName,colName)+"=?,";
				}
			}
			sql=sql.substring(0,sql.length()-1);
			if(condition!=null&&condition.trim().length()>=3){
				sql+=" where "+condition;
			}//生成sql end	
			//log.log("JDAO SQL: "+sql,Logger.LEVEL_DEBUG);
			

			if(factory.isSynchronized(tableName)){
				Object lock=factory.getTableLock(tableName);
				synchronized(lock){			
					pstmt=connection.prepareStatement(sql);
					
					int index=1;
					for(int i=0;i<cols.size();i++){
						String colName=(String)cols.get(i);
						Object value=colsBeUpdated.get(colName);
						
						int colType=factory.getColType(tableName,colName);
						Object[] paras=null;				
						
						if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
							InputStream is=(InputStream)value;
							paras=new Object[3];
							paras[0]=new Integer(index);
							paras[1]=is;
							paras[2]=new Integer(is.available());
						}else if(colType==Types.BLOB){
							Blob blob=(Blob)value;
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=blob;
						}else if(colType==Types.CLOB){
							Clob clob=(Clob)value;
		
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=clob;
						}else{
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=value;
						}
						Methods.set(colType,factory.getColIsGzip(tableName,colName),pstmt,paras);
						index++;
					}
					
					pstmt.execute();
					pstmt.close();
				}
			}else{
				pstmt=connection.prepareStatement(sql);
		
				int index=1;
				for(int i=0;i<cols.size();i++){
					String colName=(String)cols.get(i);
					Object value=colsBeUpdated.get(colName);
					
					int colType=factory.getColType(tableName,colName);
					Object[] paras=null;				
					
					if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
						InputStream is=(InputStream)value;
						paras=new Object[3];
						paras[0]=new Integer(index);
						paras[1]=is;
						paras[2]=new Integer(is.available());
					}else if(colType==Types.BLOB){
						Blob blob=(Blob)value;
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=blob;
					}else if(colType==Types.CLOB){
						Clob clob=(Clob)value;
	
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=clob;
					}else{
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=value;
					}
					Methods.set(colType,factory.getColIsGzip(tableName,colName),pstmt,paras);
					index++;
				}
				
				pstmt.execute();
				pstmt.close();	
			}
			if(factory.getPlugin()!=null&&pluginEnabled){
				factory.getPlugin().afterUpdate(tableName, colsBeUpdated, condition);
			}
		}catch(Exception e){
			try{
				pstmt.close();
			}catch(Exception ex){
			}
			throw e;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#updateByKeys(java.lang.Object)
	 */
	public void updateByKeys(Object vo) throws Exception{		
		updateByKeys(vo,new String[]{factory.getPkColumnName(vo)});
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#updateByKeys(java.lang.Object, java.lang.String[])
	 */
	public void updateByKeys(Object vo,String[] conditionKeys)throws Exception{
		String tableName=factory.getTrueTblName(vo);
		updateByKeys(tableName,vo,conditionKeys);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#updateByKeys(java.lang.String, java.lang.Object)
	 */
	public void updateByKeys(String tableName,Object vo) throws Exception{		
		updateByKeys(tableName,vo,new String[]{factory.getPkColumnName(vo)});
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#updateByKeys(java.lang.String, java.lang.Object, java.lang.String[])
	 */
	public void updateByKeys(String tableName,Object vo,String[] conditionKeys)throws Exception{
		PreparedStatement pstmt=null;
		try{
			if(factory.getPlugin()!=null&&pluginEnabled){
				factory.getPlugin().beforeUpdateByKeys(vo, conditionKeys);
			}
			if(vo==null){
				throw new Exception("指定的对象为空");
			}
			if(conditionKeys==null||conditionKeys.length==0){
				throw new Exception("没有指定主键");
			}
			
			tableName=factory.getTrueTblName(tableName);
			Class cls=vo.getClass();
			
			//生成sql
			String sql="update "+tableName+" set ";
		
			List cols=factory.getColumns(tableName);
			for(int i=0;i<cols.size();i++){
				String colName=((Column)cols.get(i)).colName;
				if(JUtilString.containIgnoreCase(conditionKeys,colName)||factory.isColIgnoredWhileUpdating(tableName, colName)){
					continue;
				}
				Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
				Object value=method.invoke(vo,(Object[])null);
				if(value==null){
					sql+=colName+"=null,";
				}else{
					sql+=colName+"=?,";
				}
			}
			sql=sql.substring(0,sql.length()-1);
			String condition="";
			for(int i=0;i<conditionKeys.length;i++){
				conditionKeys[i]=factory.getColName(tableName,conditionKeys[i]);
				Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(conditionKeys[i]),null);
				Object keyValue=method.invoke(vo,(Object[])null);
				if(keyValue==null){
					condition+=conditionKeys[i]+" is null and ";
				}else{
					if(keyValue instanceof Integer||keyValue instanceof Float||keyValue instanceof Double){
						condition+=conditionKeys[i]+"="+keyValue+" and ";
					}else{
						condition+=conditionKeys[i]+"='"+keyValue+"' and ";
					}
				}
			}
			condition=condition.substring(0,condition.length()-5);
			sql+=" where "+condition;
			//生成sql end	
			//log.log("JDAO SQL: "+sql,Logger.LEVEL_DEBUG);
			if(factory.isSynchronized(tableName)){
				Object lock=factory.getTableLock(tableName);
				synchronized(lock){	
					pstmt=connection.prepareStatement(sql);
					
					int index=1;
					for(int i=0;i<cols.size();i++){
						String colName=((Column)cols.get(i)).colName;
						if(JUtilString.containIgnoreCase(conditionKeys,colName)||factory.isColIgnoredWhileUpdating(tableName, colName)){
							continue;
						}
						
						int colType=factory.getColType(tableName,colName);
						Object[] paras=null;	
						Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
						Object value=method.invoke(vo,(Object[])null);
						if(value==null){
							continue;
						}
						
						if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
							InputStream is=(InputStream)value;
							paras=new Object[3];
							paras[0]=new Integer(index);
							paras[1]=is;
							paras[2]=new Integer(is.available());
						}else if(colType==Types.BLOB){
							Blob blob=(Blob)value;
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=blob;
						}else if(colType==Types.CLOB){
							Clob clob=(Clob)value;
		
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=clob;
						}else{
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=value;
						}
						Methods.set(colType,factory.getColIsGzip(tableName,colName),pstmt,paras);
						index++;
					}
					
					pstmt.execute();
					pstmt.close();
				}
			}else{
				pstmt=connection.prepareStatement(sql);
				
				int index=1;
				for(int i=0;i<cols.size();i++){
					String colName=((Column)cols.get(i)).colName;
					if(JUtilString.containIgnoreCase(conditionKeys,colName)||factory.isColIgnoredWhileUpdating(tableName, colName)){
						continue;
					}
					
					int colType=factory.getColType(tableName,colName);
					Object[] paras=null;	
					Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
					Object value=method.invoke(vo,(Object[])null);
					if(value==null){
						continue;
					}
					
					if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
						InputStream is=(InputStream)value;
						paras=new Object[3];
						paras[0]=new Integer(index);
						paras[1]=is;
						paras[2]=new Integer(is.available());
					}else if(colType==Types.BLOB){
						Blob blob=(Blob)value;
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=blob;
					}else if(colType==Types.CLOB){
						Clob clob=(Clob)value;
	
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=clob;
					}else{
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=value;
					}
					Methods.set(colType,factory.getColIsGzip(tableName,colName),pstmt,paras);
					index++;
				}
				
				pstmt.execute();
				pstmt.close();
			}
			if(factory.getPlugin()!=null&&pluginEnabled){
				factory.getPlugin().afterUpdateByKeys(vo, conditionKeys);
			}
		}catch(Exception e){
			try{
				pstmt.close();
			}catch(Exception ex){
			}
			throw e;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#updateByKeys(java.lang.Object)
	 */
	public void updateByKeysIgnoreNulls(Object vo) throws Exception{	
		updateByKeysIgnoreNulls(vo,new String[]{factory.getPkColumnName(vo)});
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#updateByKeysIgnoreNulls(java.lang.Object, java.lang.String[])
	 */
	public void updateByKeysIgnoreNulls(Object vo,String[] conditionKeys)throws Exception{
		String tableName=factory.getTrueTblName(vo);
		updateByKeysIgnoreNulls(tableName,vo,conditionKeys);
	}	
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#updateByKeysIgnoreNulls(java.lang.String, java.lang.Object)
	 */
	public void updateByKeysIgnoreNulls(String tableName,Object vo) throws Exception{	
		updateByKeysIgnoreNulls(tableName,vo,new String[]{factory.getPkColumnName(vo)});
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#updateByKeysIgnoreNulls(java.lang.String, java.lang.Object, java.lang.String[])
	 */
	public void updateByKeysIgnoreNulls(String tableName,Object vo,String[] conditionKeys)throws Exception{
		updateByKeysIgnoreNulls(tableName,vo,conditionKeys,null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#updateByKeysIgnoreNulls(java.lang.Object, java.util.List)
	 */
	public void updateByKeysIgnoreNulls(Object vo,List<String> updateNullCols) throws Exception{	
		updateByKeysIgnoreNulls(vo,new String[]{factory.getPkColumnName(vo)},updateNullCols);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#updateByKeysIgnoreNulls(java.lang.Object, java.lang.String[], java.util.List)
	 */
	public void updateByKeysIgnoreNulls(Object vo,String[] conditionKeys,List<String> updateNullCols)throws Exception{
		String tableName=factory.getTrueTblName(vo);
		updateByKeysIgnoreNulls(tableName,vo,conditionKeys,updateNullCols);
	}	
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#updateByKeysIgnoreNulls(java.lang.String, java.lang.Object, java.util.List)
	 */
	public void updateByKeysIgnoreNulls(String tableName,Object vo,List<String> updateNullCols) throws Exception{	
		updateByKeysIgnoreNulls(tableName,vo,new String[]{factory.getPkColumnName(vo)},updateNullCols);
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#updateByKeysIgnoreNulls(java.lang.String, java.lang.Object, java.lang.String[], java.util.List)
	 */
	public void updateByKeysIgnoreNulls(String tableName,Object vo,String[] conditionKeys,List<String> updateNullCols)throws Exception{
		PreparedStatement pstmt=null;
		try{
			if(factory.getPlugin()!=null&&pluginEnabled){
				factory.getPlugin().beforeUpdateByKeysIgnoreNulls(vo, conditionKeys);
			}
			if(vo==null){
				throw new Exception("指定的对象为空");
			}
			if(conditionKeys==null||conditionKeys.length==0){
				throw new Exception("没有指定主键");
			}
			
			tableName=factory.getTrueTblName(tableName);
			Class cls=vo.getClass();
			
			//生成sql
			String sql="update "+tableName+" set ";
			List cols=factory.getColumns(tableName);
			for(int i=0;i<cols.size();i++){
				String colName=((Column)cols.get(i)).colName;
				String fieldName=JUtilBean.colNameToVariableName(colName);
				
				//作为条件的字段或JDAO.xml中配置的不可通过对象操作更新的字段
				if(JUtilString.containIgnoreCase(conditionKeys,colName)||factory.isColIgnoredWhileUpdating(tableName, colName)){
					continue;
				}
				
				Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
				Object value=method.invoke(vo,(Object[])null);
				if(value==null){//如果值为null
					if(updateNullCols==null) {
						//未指定即使为null也更新的字段列表
						continue;
					}else if(!updateNullCols.contains(fieldName)
							&&!updateNullCols.contains(colName.toUpperCase())
							&&!updateNullCols.contains(colName.toLowerCase())) {
						//未包含在指定的即使为null也更新的字段列表中
						continue;
					}
					sql+=colName+"=null,";
				}else{
					sql+=colName+"=?,";
				}
			}
			if(sql.indexOf("?")==-1 && sql.indexOf("=null,")<0){
				//throw new Exception("没有值需要更新！");
				if(factory.getPlugin()!=null&&pluginEnabled){
					factory.getPlugin().afterUpdateByKeysIgnoreNulls(vo, conditionKeys);
				}
				return;
			}
			
			
			sql=sql.substring(0,sql.length()-1);
			String condition="";
			for(int i=0;i<conditionKeys.length;i++){
				conditionKeys[i]=factory.getColName(tableName,conditionKeys[i]);
				Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(conditionKeys[i]),null);
				Object keyValue=method.invoke(vo,(Object[])null);
				if(keyValue==null){
					condition+=conditionKeys[i]+" is null and ";
				}else{
					if(keyValue instanceof Integer||keyValue instanceof Float||keyValue instanceof Double){
						condition+=conditionKeys[i]+"="+keyValue+" and ";
					}else{
						condition+=conditionKeys[i]+"='"+keyValue+"' and ";
					}
				}
			}
			condition=condition.substring(0,condition.length()-5);
			sql+=" where "+condition;
			//生成sql end	
			//log.log("JDAO SQL: "+sql,-1);
			if(factory.isSynchronized(tableName)){
				Object lock=factory.getTableLock(tableName);
				synchronized(lock){	
					pstmt=connection.prepareStatement(sql);
					
					int index=1;
					for(int i=0;i<cols.size();i++){
						String colName=((Column)cols.get(i)).colName;
						
						//作为条件的字段或JDAO.xml中配置的不可通过对象操作更新的字段
						if(JUtilString.containIgnoreCase(conditionKeys,colName)||factory.isColIgnoredWhileUpdating(tableName, colName)){
							continue;
						}
						
						int colType=factory.getColType(tableName,colName);
						Object[] paras=null;	
						Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
						Object value=method.invoke(vo,(Object[])null);
						if(value==null){//如果值为null
							continue;
						}
						
						if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
							InputStream is=(InputStream)value;
							paras=new Object[3];
							paras[0]=new Integer(index);
							paras[1]=is;
							paras[2]=new Integer(is.available());
						}else if(colType==Types.BLOB){
							Blob blob=(Blob)value;
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=blob;
						}else if(colType==Types.CLOB){
							Clob clob=(Clob)value;
		
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=clob;
						}else{
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=value;
						}
						Methods.set(colType,factory.getColIsGzip(tableName,colName),pstmt,paras);
						index++;
					}
					
					pstmt.execute();
					pstmt.close();
				}
			}else{
				pstmt=connection.prepareStatement(sql);
				
				int index=1;
				for(int i=0;i<cols.size();i++){
					String colName=((Column)cols.get(i)).colName;
					if(JUtilString.containIgnoreCase(conditionKeys,colName)||factory.isColIgnoredWhileUpdating(tableName, colName)){
						continue;
					}
					
					int colType=factory.getColType(tableName,colName);
					Object[] paras=null;	
					Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
					Object value=method.invoke(vo,(Object[])null);
					if(value==null){
						continue;
					}
					
					if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
						InputStream is=(InputStream)value;
						paras=new Object[3];
						paras[0]=new Integer(index);
						paras[1]=is;
						paras[2]=new Integer(is.available());
					}else if(colType==Types.BLOB){
						Blob blob=(Blob)value;
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=blob;
					}else if(colType==Types.CLOB){
						Clob clob=(Clob)value;
	
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=clob;
					}else{
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=value;
					}
					Methods.set(colType,factory.getColIsGzip(tableName,colName),pstmt,paras);
					index++;
				}
				
				pstmt.execute();
				pstmt.close();
			}
			if(factory.getPlugin()!=null&&pluginEnabled){
				factory.getPlugin().afterUpdateByKeysIgnoreNulls(vo, conditionKeys);
			}
		}catch(Exception e){
			try{
				pstmt.close();
			}catch(Exception ex){
			}
			throw e;
		}
	}	
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#executeSQLList(java.util.List)
	 */
	public void executeSQLList(List sqls) throws Exception {
		for(int i=0;i<sqls.size();i++){
			executeSQL((String)sqls.get(i));
		}	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see j.sdk.dao.DAO#executeSQL(java.lang.String)
	 */
	public void executeSQL(String sql) throws Exception {
		Statement stmt = null;
		try {
			if(factory.getPlugin()!=null&&pluginEnabled){
				factory.getPlugin().beforeExecuteSQL(sql);
			}
			String tableName=SQLUtil.retrieveTableNameFromSQL(sql);
			String trueTblName=factory.getTrueTblName(tableName);
			sql=JUtilString.replaceAll(sql,tableName, trueTblName);
			if(SQLUtil.sqlInjection(sql)!=null) return;
			
			if(factory.isSynchronized(tableName)){
				Object lock=factory.getTableLock(tableName);
				synchronized(lock){
					stmt = connection.createStatement();
					stmt.execute(sql);
					stmt.close();
				}
			}else{
				stmt = connection.createStatement();
				stmt.execute(sql);
				stmt.close();
			}
			if(factory.getPlugin()!=null&&pluginEnabled){
				factory.getPlugin().afterExecuteSQL(sql);
			}
		} catch (Exception e) {
			try{
				stmt.close();
			}catch(Exception ex){}
			throw e;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#executeBatchSQL(java.util.List)
	 */
	public void executeBatchSQL(List sqls)throws Exception{
		if(sqls==null||sqls.size()==0){
			return;
		}
		if(factory.getPlugin()!=null&&pluginEnabled){
			for(int i=0;i<sqls.size();i++){
				factory.getPlugin().beforeExecuteSQL((String)sqls.get(i));
			}			
		}
		Statement stmt = null;
		try {			
			beginTransaction();
			stmt = connection.createStatement();
			for(int i=0;i<sqls.size();i++){
				String sql=(String)sqls.get(i);
				String tableName=SQLUtil.retrieveTableNameFromSQL(sql);
				String trueTblName=factory.getTrueTblName(tableName);
				sql=JUtilString.replaceAll(sql,tableName, trueTblName);
				//log.log("sql:"+sql,Logger.LEVEL_DEBUG);
				
				if(SQLUtil.sqlInjection(sql)!=null) continue;
				
				stmt.addBatch(sql);
			}	
			stmt.executeBatch();
			commit();
			stmt.close();
			
			if(factory.getPlugin()!=null&&pluginEnabled){
				for(int i=0;i<sqls.size();i++){
					factory.getPlugin().afterExecuteSQL((String)sqls.get(i));
				}					
			}
		} catch (Exception e) {
			try{
				rollback();
			}catch(Exception ex){}
			try{
				stmt.close();
			}catch(Exception ex){}
			throw e;
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see j.sdk.dao.DAO#getCatalogs()
	 */
	public List getCatalogs() throws Exception {	
		ResultSet rs=null;
		try {	
			List catalogs = new ArrayList();
			DatabaseMetaData dbmd = connection.getMetaData();
			rs = dbmd.getCatalogs();
			while (rs.next()) {
				catalogs.add(rs.getString(1));
			}
			rs.close();
			return catalogs;
		} catch (Exception e) {
			try{
				rs.close();
			}catch(Exception ex){}
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see j.sdk.dao.DAO#getSchemas()
	 */
	public List getSchemas() throws Exception {		
		ResultSet rs=null;
		try {
			List schemas = new ArrayList();
			DatabaseMetaData dbmd = connection.getMetaData();
			rs = dbmd.getSchemas();
			while (rs.next()) {
				schemas.add(rs.getString(1));
			}
			rs.close();
			return schemas;
		} catch (Exception e) {
			try{
				rs.close();
			}catch(Exception ex){}
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see j.sdk.dao.DAO#getTables(java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String[])
	 */
	public List getTables(String catalog, String schemaPattern,String tblPattern, String[] tblTypes) throws Exception {		
		List tables = new ArrayList();
		ResultSet rs =null;
		try{
			DatabaseMetaData dmd = connection.getMetaData();
			rs = dmd.getTables(catalog, schemaPattern, tblPattern,tblTypes);
			while (rs.next()) {	
				String table = rs.getString("TABLE_NAME");
				if(table.indexOf("$")>-1
						||table.indexOf("=")>-1
						||table.indexOf("/")>-1
						||table.indexOf("+")>-1){
					continue;
				}
				tables.add(table);
			}
			rs.close();
			return tables;
		}catch(Exception e){
			try{
				rs.close();
			}catch(Exception ex){}
			throw e;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#getColumns(java.lang.String)
	 */
	public List getColumns(String tableName) throws Exception {		
		Statement stmt=null;
		ResultSet rs=null;
		try{
			tableName=factory.getTrueTblName(tableName);

			List columns = new ArrayList();
			stmt=connection.createStatement();
			stmt.setMaxRows(1);
			rs=stmt.executeQuery("select * from "+tableName);
			ResultSetMetaData rsmd=rs.getMetaData();
			int colCount=rsmd.getColumnCount();
			for(int i=1;i<=colCount;i++){
				try{
					String colName=rsmd.getColumnName(i);
					int colType=rsmd.getColumnType(i);
					String colTypeName=rsmd.getColumnTypeName(i);
					boolean notnull=(rsmd.isNullable(i)==ResultSetMetaData.columnNoNulls)?true:false;
					Column column=new Column(colName,
							SQLUtil.adjustDataType(factory.getDbType(),colType,colTypeName),
							notnull,
							false,
							rsmd.getColumnDisplaySize(i));
					columns.add(column);
				}catch(Exception ex){}
			}
			rs.close();
			stmt.close();
			return columns;
		}catch(Exception e){
			try{
				rs.close();	
			}catch(Exception colseException){}
			try{
				stmt.close();	
			}catch(Exception colseException){}
			throw e;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#getPrimaryKeyColumns(java.lang.String)
	 */
	public Column[] getPrimaryKeyColumns(String tableName) throws Exception {		
		Statement stmt=null;
		ResultSet rs=null;
		ResultSet pkrs=null;		
		try{
			tableName=factory.getTrueTblName(tableName);
			stmt=connection.createStatement();
			stmt.setMaxRows(1);
			rs=stmt.executeQuery("select * from "+tableName);
			ResultSetMetaData rsmd=rs.getMetaData();
			
			Map typeOfColumnMap=new HashMap();
			Map notnullOfColumnMap=new HashMap();
			Map lengthOfColumnMap=new HashMap();
			int colCount=rsmd.getColumnCount();
			for(int i=1;i<=colCount;i++){
				try{
					String colName=rsmd.getColumnName(i);
					int colType=rsmd.getColumnType(i);
					String colTypeName=rsmd.getColumnTypeName(i);
					boolean notnull=rsmd.isNullable(i)==ResultSetMetaData.columnNoNulls?true:false;
					int length=rsmd.getColumnDisplaySize(i);
					
					typeOfColumnMap.put(colName,new Integer(SQLUtil.adjustDataType(factory.getDbType(),colType,colTypeName)));
					lengthOfColumnMap.put(colName,new Integer(length));
					notnullOfColumnMap.put(colName,new Boolean(notnull));
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_DEBUG);
				}
			}
			
			pkrs=connection.getMetaData().getPrimaryKeys(null,null,tableName);
			
			List pks=new ArrayList();
			while(pkrs.next()){
				try{
					String colName=pkrs.getString(4);
					Column col=new Column(colName,
							((Integer)typeOfColumnMap.get(colName)).intValue(),
							((Boolean)notnullOfColumnMap.get(colName)).booleanValue(),
							false,
							((Integer)lengthOfColumnMap.get(colName)).intValue());
					pks.add(col);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_DEBUG);
				}
			}
			
			rs.close();
			pkrs.close();
			stmt.close();
			
			Column[] cols=new Column[pks.size()];
			pks.toArray(cols);
			return cols;
		}catch(Exception e){	
			try{
				rs.close();	
			}catch(Exception colseException){}
			try{
				pkrs.close();	
			}catch(Exception colseException){}
			try{	
				stmt.close();
			}catch(Exception colseException){}
			throw e;
		}
	}	
	

	
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#getRecordCnt(java.lang.String)
	 */
	public int getRecordCnt(String sql) throws Exception{
		StmtAndRs sr=null;
		try {
			sql="select count(*) from ("+sql+") row_";
			sr=find(sql);
	        if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
	        
			ResultSet rs=sr.resultSet();
			int cnt=0;
			while(rs.next()){
				String cntString=rs.getString(1);
				cnt= Integer.parseInt(cntString);
				break;
			}
			sr.close();
			return cnt;
		} catch (Exception e) {
			if(sr!=null) sr.close();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see j.sdk.dao.DAO#getRecordCnt(java.lang.String,
	 *      java.lang.String)
	 */
	public int getRecordCnt(String tableName, String condition)throws Exception {		
		StmtAndRs sr=null;
		try{
			tableName=factory.getTrueTblName(tableName);

			String sql="select count(*) from "+tableName;
			if(condition!=null&&condition.length()>0){
				String tmpcondition=condition.toLowerCase();
				int orderByIndex=JUtilString.match(tmpcondition," order*by","*");
				if(orderByIndex>0){
					condition=condition.substring(0,orderByIndex);
	            }
				
				condition=condition.trim();
				if(!"".equals(condition)){
					sql+=" where "+condition;
				}
			}
			sr=find(sql);
	        if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
	        
			ResultSet rs=sr.resultSet();
	        
			int cnt=0;
			while(rs.next()){
				String cntString=rs.getString(1);
				cnt= Integer.parseInt(cntString);
				break;
			}
			sr.close();
			return cnt;
		}catch(Exception e){
			if(sr!=null) sr.close();
			throw e;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see j.sdk.dao.DAO#getRecordCnt(java.lang.String[],
	 *      java.lang.String)
	 */
	public int getRecordCnt(String[] tableNames, String condition)throws Exception {		
		StmtAndRs sr=null;
		try{
			if(tableNames.length==1){
				return getRecordCnt(tableNames[0],condition);
			}
			if(condition==null||condition.trim().equals("")){
				throw new Exception("多表查询必须指定查询条件");
			}
			//处理表名
			for(int i=0;i<tableNames.length;i++){
				String trueTblName=factory.getTrueTblName(tableNames[i]);
				condition=JUtilString.replaceAll(condition,tableNames[i]+".",trueTblName+".");
				tableNames[i]=trueTblName;
			}
			
			String sql="select count(*) from ";
			for(int i=0;i<tableNames.length;i++){
				sql+=tableNames[i]+",";
			}
			sql=sql.substring(0,sql.length()-1);
			
			if(condition!=null&&condition.length()>0){
				String tmpcondition=condition.toLowerCase();
				int orderByIndex=JUtilString.match(tmpcondition," order*by","*");
				if(orderByIndex>0){
					condition=condition.substring(0,orderByIndex);
	            }
				
				condition=condition.trim();
				if(!"".equals(condition)){
					sql+=" where "+condition;
				}
			}
			//System.out.println(sql);
			
			sr=find(sql);
			if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
			
			ResultSet rs=sr.resultSet();
			int cnt=0;
			while(rs.next()){
				String cntString=rs.getString(1);
				cnt= Integer.parseInt(cntString);
				break;
			}
			sr.close();
			return cnt;
		}catch(Exception e){
			if(sr!=null) sr.close();
			throw e;
		}
	}
	
	/* (non-Javadoc)
	 * @see j.sdk.dao.DAO#getMaxValue(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getMaxValue(String tableName, String colName, String condition) throws Exception {
		StmtAndRs sr=null;
		try{
			tableName=factory.getTrueTblName(tableName);

			String sql="select max("+colName+") from "+tableName;

			if(condition!=null&&condition.trim().length()>0){
				sql+=" where "+condition;	
			}
			sr=find(sql);
			if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
			
			ResultSet rs=sr.resultSet();
			String ret="";
			while(rs.next()){
				ret=rs.getString(1);
			}
			sr.close();
			return ret;
		}catch(Exception e){
			if(sr!=null) sr.close();		
			throw e;
		}
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#getMaxNumber(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getMaxNumber(String tableName, String colName, String condition) throws Exception {
		StmtAndRs sr=null;
		try{
			tableName=factory.getTrueTblName(tableName);

			String sql="select max("+colName+"*1) from "+tableName;

			if(condition!=null&&condition.trim().length()>0){
				sql+=" where "+condition;	
			}
			sr=find(sql);
			if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
			
			ResultSet rs=sr.resultSet();
			String ret="";
			while(rs.next()){
				ret=rs.getString(1);
			}
			sr.close();
			
			if(ret==null) ret="";
			if(ret.indexOf(".")>0){
				String tmp=ret.substring(ret.indexOf(".")+1);
				if("".equals(tmp.replaceAll("0",""))) ret=ret.substring(0,ret.indexOf("."));
			}
			
			return JUtilMath.convertScientificNotation(ret);
		}catch(Exception e){
			if(sr!=null) sr.close();		
			throw e;
		}
	}
	
	/* (non-Javadoc)
	 * @see j.sdk.dao.DAO#getMaxValue(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getMinValue(String tableName, String colName, String condition) throws Exception {
		StmtAndRs sr=null;
		try{
			tableName=factory.getTrueTblName(tableName);

			String sql="select min("+colName+") from "+tableName;
			if(condition!=null&&condition.trim().length()>0){
				sql+=" where "+condition;			
			}
			sr=find(sql);
			if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
			
			ResultSet rs=sr.resultSet();
			String ret="";
			while(rs.next()){
				ret=rs.getString(1);
			}
			sr.close();
			return ret;
		}catch(Exception e){
			if(sr!=null) sr.close();
			throw e;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#getMinNumber(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getMinNumber(String tableName, String colName, String condition) throws Exception {
		StmtAndRs sr=null;
		try{
			tableName=factory.getTrueTblName(tableName);

			String sql="select min("+colName+"*1) from "+tableName;
			if(condition!=null&&condition.trim().length()>0){
				sql+=" where "+condition;			
			}
			sr=find(sql);
			if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
			
			ResultSet rs=sr.resultSet();
			String ret="";
			while(rs.next()){
				ret=rs.getString(1);
			}
			sr.close();

			if(ret==null) ret="";
			if(ret.indexOf(".")>0){
				String tmp=ret.substring(ret.indexOf(".")+1);
				if("".equals(tmp.replaceAll("0",""))) ret=ret.substring(0,ret.indexOf("."));
			}
			
			return JUtilMath.convertScientificNotation(ret);
		}catch(Exception e){
			if(sr!=null) sr.close();
			throw e;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see j.sdk.dao.DAO#getSum(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getSum(String tableName, String colName, String condition) throws Exception {
		StmtAndRs sr=null;
		try{
			tableName=factory.getTrueTblName(tableName);

			String sql="select sum("+colName+") from "+tableName;
			if(condition!=null&&condition.trim().length()>0){
				sql+=" where "+condition;		
			}
			sr=find(sql);
			if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
			
			ResultSet rs=sr.resultSet();
			String ret="";
			while(rs.next()){
				Object obj=null;
				try{
					obj=rs.getObject(1);
				}catch(Exception e){
					obj=null;
				}
				if(obj==null){
					ret= "";
				}else{
					ret = obj.toString();
				}
				
				break;
			}
			sr.close();
			return ret;
		}catch(Exception e){
			if(sr!=null) sr.close();
			throw e;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#getSum(java.lang.String, java.lang.String[], java.lang.String)
	 */
	public String[] getSum(String tableName, String[] colNames, String condition) throws Exception {
		StmtAndRs sr=null;
		try{
			tableName=factory.getTrueTblName(tableName);

			String sql="select";
			for(int i=0;i<colNames.length;i++){
				sql+=" sum("+colNames[i]+"),";
			}
			
			sql=sql.substring(0,sql.length()-1);
			
			sql+=" from "+tableName;
			
			if(condition!=null&&condition.trim().length()>0){
				sql+=" where "+condition;		
			}
			sr=find(sql);
			if(sr==null) throw new Exception("SQL Exception(Injection?) "+sql);
			
			ResultSet rs=sr.resultSet();
			String[] ret=new String[colNames.length];
			while(rs.next()){
				for(int i=0;i<colNames.length;i++){
					Object obj=null;
					try{
						obj=rs.getObject(i+1);
					}catch(Exception e){
						obj=null;
					}
					if(obj==null){
						ret[i]= "";
					}else{
						ret[i] = obj.toString();
					}
				}
				
				break;
			}
			sr.close();
			return ret;
		}catch(Exception e){
			if(sr!=null) sr.close();
			throw e;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#autoIncreaseKey(java.lang.String, java.lang.String)
	 */
	public String autoIncreaseKey(String table,String column) throws Exception{
		return autoIncreaseKey(table,column,0);
	}
	public String autoIncreaseKey(String table,String column,long addition) throws Exception{
		String key=(factory.getDbName().toLowerCase()+"."+table.toLowerCase()+"."+column.toLowerCase()).intern();
		
		synchronized(key){
			Long max=null;
			if(DB.isCluster){
				max=(Long)factory.getMaxColumnValues().get(new JCacheParams(key));
			}else{
				max=(Long)factory.getMaxColumnValuesLocal().get(key);
			}
			
			if(max==null){
				DAO dao=null;
				try{
					dao=DB.connect(factory.getDbName(),DB.class);
					String strMax=dao.getMaxNumber(table,column,"");
					max=new Long(JUtilMath.isLong(strMax)?strMax:"0");
				}catch(Exception e){
					if(dao!=null){
						try{
							dao.close();
							dao=null;
						}catch(Exception ex){}
					}
					throw e;
				}				
			}
			long ret=0;
			ret=max.longValue()+1+addition;
			
			if(DB.isCluster){
				factory.getMaxColumnValues().addOne(key,new Long(ret));
			}else{
				factory.getMaxColumnValuesLocal().put(key,new Long(ret));
			}
			
			return ""+ret;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.dao.DAO#autoIncreaseKeyLargerThan(java.lang.String, java.lang.String, long)
	 */
	public String autoIncreaseKeyLargerThan(String table,String column,long min) throws Exception{
		return autoIncreaseKeyLargerThan(table,column,min,0);
	}
	public String autoIncreaseKeyLargerThan(String table,String column,long min,long addition) throws Exception{
		String key=(factory.getDbName().toLowerCase()+"."+table.toLowerCase()+"."+column.toLowerCase()).intern();
		
		synchronized(key){
			Long max=null;
			if(DB.isCluster){
				max=(Long)factory.getMaxColumnValues().get(new JCacheParams(key));
			}else{
				max=(Long)factory.getMaxColumnValuesLocal().get(key);
			}
			
			if(max==null){
				DAO dao=null;
				try{
					dao=DB.connect(factory.getDbName(),DB.class);
					String strMax=dao.getMaxNumber(table,column,"");
					max=new Long(JUtilMath.isLong(strMax)?strMax:"0");
				}catch(Exception e){
					if(dao!=null){
						try{
							dao.close();
							dao=null;
						}catch(Exception ex){}
					}
					throw e;
				}				
			}
			long ret=0;
			
			ret=max.longValue()+1+addition;
			if(ret<min) ret=min;
			
			if(DB.isCluster){
				factory.getMaxColumnValues().addOne(key,new Long(ret));
			}else{
				factory.getMaxColumnValuesLocal().put(key,new Long(ret));
			}
			
			return ""+ret;
		}
	}
	
	/**
	 * 
	 * @param sql
	 * @param RPP
	 * @param PN
	 * @return
	 * @throws Exception
	 */
	protected String getFindSQL(String sql,int RPP,int PN)throws Exception{
		if(RPP<0||PN<0){
			throw new Exception("指定的分页相关的参数小于零");
		}
		if(RPP>0&&PN>0){
			sql=getSQLWithRowSetLimit(sql,RPP*(PN-1),RPP*PN);
		}
		return sql;
	}
	
	/**
	 * 
	 * @param sql
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	protected String getFindSQLScale(String sql,int start,int end)throws Exception{
		if(start<0||end<=0){
			throw new Exception("指定的范围的参数小于零");
		}
		
		if(start>=end){
			throw new Exception("指定的范围的start 大于等于 end");
		}
		sql=getSQLWithRowSetLimit(sql,start,end);
		return sql;
	}	
	
	
	
	/**
	 * 
	 * @param rs
	 * @param colType
	 * @param colName
	 * @param isGzip
	 * @return
	 * @throws Exception
	 */
	protected Object getObject(ResultSet rs,int colType,String colName,boolean isGzip)throws Exception{
		if(colType==Types.BLOB){
			java.sql.Blob blob=rs.getBlob(colName);
			if(blob==null){
				return null;
			}
			return new j.dao.type.Blob(blob.getBinaryStream());
		}else if(colType==Types.CLOB){
			java.sql.Clob clob=rs.getClob(colName);
			if(clob==null){
				return null;
			}
			return new j.dao.type.Clob(clob.getCharacterStream());
		}else if(colType==Types.BINARY||colType==Types.VARBINARY||colType==Types.LONGVARBINARY){
			InputStream in=rs.getBinaryStream(colName);
			if(in==null){
				return null;
			}
			return new ByteArrayInputStream(JUtilInputStream.bytes(in));
		}else{
			Object obj=Methods.get(colType,rs,colName);
			if(isGzip&&obj!=null&&(obj instanceof String)){
				try{
					obj=JUtilCompressor.gunzipString((String)obj,"UTF-8");
				}catch(Exception e){}
			}
			return obj;
		}
	}
	
	
	/**
	 * 
	 * @param rs
	 * @param colType
	 * @param index
	 * @param isGzip
	 * @return
	 * @throws Exception
	 */
	protected Object getObject(ResultSet rs,int colType,int index,boolean isGzip)throws Exception{
		if(colType==Types.BLOB){
			java.sql.Blob blob=rs.getBlob(index);
			if(blob==null){
				return null;
			}	
			return new j.dao.type.Blob(blob.getBinaryStream());
		}else if(colType==Types.CLOB){
			java.sql.Clob clob=rs.getClob(index);	
			if(clob==null){
				return null;
			}
			return new j.dao.type.Clob(clob.getCharacterStream());
		}else if(colType==Types.BINARY||colType==Types.VARBINARY||colType==Types.LONGVARBINARY){
			InputStream in=rs.getBinaryStream(index);
			if(in==null){
				return null;
			}
			return new ByteArrayInputStream(JUtilInputStream.bytes(in));
		}else{
			Object obj=Methods.get(colType,rs,index);
			if(isGzip&&obj!=null&&(obj instanceof String)){
				try{
					obj=JUtilCompressor.gunzipString((String)obj,"UTF-8");
				}catch(Exception e){}
			}
			return obj;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	public void finalize(){
		if(!closed){
			log.log("finalize DAO......",Logger.LEVEL_DEBUG);
			try{
				close();
			}catch(Exception e){}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#disablePlugin()
	 */
	public void disablePlugin() {
		pluginEnabled=false;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#enablePlugin()
	 */
	public void enablePlugin() {
		pluginEnabled=true;
	}
	
	public boolean isPluginEnabled(){
		return pluginEnabled;
	}
}