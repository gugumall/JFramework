package j.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 肖炯
 *
 */
public interface DAO{	
	public static final String DB_TYPE_MYSQL="MYSQL";
	public static final String DB_TYPE_SQLITE="SQLITE";
	public static final String DB_TYPE_DB2="DB2";
	public static final String DB_TYPE_ORACLE="ORACLE";
	public static final String DB_TYPE_SQLSERVER="SQLSERVER";
	public static final String DB_TYPE_HSQL="HSQL";
	
	/**
	 * 
	 * @return
	 */
	public long getLastTest();
	public void setLastTest(long time);
	
	/**
	 * 
	 * @return
	 */
	public boolean getReadOnly();
	public void setReadOnly(boolean readonly);
	
	/**
	 * 
	 * @return
	 */
	public DBMirror getMirror();
	
	/**
	 * 
	 * @return
	 */
	public String getCaller();
	
	/**
	 * 
	 * @param caller
	 */
	public void setCaller(String caller);
	
	/**
	 * 
	 * @return
	 */
	public long getTimeout();
	
	/**
	 * 
	 * @param timeout
	 */
	public void setTimeout(long timeout);
	
	/**
	 * 
	 * @return
	 */
	public void begin();
	
	/**
	 * 
	 *
	 */
	public void finish();
	
	/**
	 * 
	 * @return
	 */
	public long getLastUsingTime();
	
	/**
	 * 
	 * @return
	 */
	public boolean isUsing();
	
	/**
	 * 提交
	 * @throws Exception
	 */
	public void commit()throws Exception ;
	
	/**
	 * 回滚
	 * @throws Exception
	 */
	public void rollback()throws Exception ;
	
	/**
	 * 开始事务
	 * @throws Exception
	 */
	public void beginTransaction()throws Exception;

	/**
	 * 是否处于事务状态
	 * @return
	 * @throws Exception
	 */
	public boolean isInTransaction();
	
	/**
	 * 关闭
	 * @throws Exception
	 */
	public void close()throws Exception;

	
	/**
	 * 在任何调用前
	 * @throws Exception
	 */
	public void beforeAnyInvocation() throws Exception ;
	
	/**
	 * 在任何调用后
	 * @throws Exception
	 */
	public void afterAnyInvocation() throws Exception ;	
	
	/**
	 * 异常发生时候
	 * @throws Exception
	 */
	public void onException();	
	
	/**
	 * 是否已经关闭
	 * @return boolean
	 */
	public boolean isClosed() ;
	
	/**
	 * 
	 * @return
	 */
	public Connection getConnection();
	
	/**
	 * 
	 * @return
	 */
	public DAOFactory getFactory();

	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 查询
	 * @param sql 标准sql
	 * @return see StmtAndRs
	 * @throws Exception
	 */
	public StmtAndRs find(String sql)throws Exception;
	
	/**
	 * 查询
	 * @param sql 标准sql
	 * @param RPP 每页多少条，大于0的整数
	 * @param PN 第几页，大于0的整数
	 * @return see StmtAndRs
	 * @throws Exception
	 */
	public StmtAndRs find(String sql,int RPP,int PN)throws Exception;
	
	/**
	 * get the start(inclusive) to end(exclusive)
	 * @param sql
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public StmtAndRs findScale(String sql,int start,int end)throws Exception;	
	
	//////////////////////////////////////////////////////////////////////////////	
	//////////////////////////////////////////////////////////////////////////////
		
	/**
	 * @deprecated
	 * @param sql
	 * @param cls
	 * @param excludedColumns 某些不读取的列，格式：{列名/字段名1}{列名/字段名2}
	 * @return
	 * @throws Exception
	 */
	public List find(String sql,Class cls,String excludedColumns)throws Exception;
	
	
	/**
	 * 
	 * @param sql
	 * @param cls
	 * @param excludedColumns 某些不读取的列名/字段名
	 * @return
	 * @throws Exception
	 */
	public List find(String sql,Class cls,List<String> excludedColumns)throws Exception;
	
	/**
	 * @deprecated
	 * @param sql
	 * @param cls
	 * @param excludedColumns 某些不读取的列，格式：{列名/字段名1}{列名/字段名2}
	 * @param RPP
	 * @param PN
	 * @return
	 * @throws Exception
	 */
	public List find(String sql,Class cls,String excludedColumns,int RPP,int PN)throws Exception;
	
	/**
	 * 
	 * @param sql
	 * @param cls
	 * @param excludedColumns 某些不读取的列名/字段名
	 * @param RPP
	 * @param PN
	 * @return
	 * @throws Exception
	 */
	public List find(String sql,Class cls,List<String> excludedColumns,int RPP,int PN)throws Exception;
	
	/**
	 * @deprecated
	 * get the start(inclusive) to end(exclusive)
	 * @param sql
	 * @param cls
	 * @param excludedColumns 某些不读取的列，格式：{列名/字段名1}{列名/字段名2}
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public List findScale(String sql,Class cls,String excludedColumns,int start,int end)throws Exception;
	
	/**
	 * get the start(inclusive) to end(exclusive)
	 * @param sql
	 * @param cls
	 * @param excludedColumns 某些不读取的列名/字段名
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public List findScale(String sql,Class cls,List<String> excludedColumns,int start,int end)throws Exception;
	

	/**
	 * @deprecated
	 * @param sql
	 * @param cls
	 * @param excludedColumns 某些不读取的列，格式：{列名/字段名1}{列名/字段名2}
	 * @return
	 * @throws Exception
	 */
	public Object findSingle(String sql,Class cls,String excludedColumns)throws Exception;

	/**
	 * 
	 * @param sql
	 * @param cls
	 * @param excludedColumns 某些不读取的列名/字段名
	 * @return
	 * @throws Exception
	 */
	public Object findSingle(String sql,Class cls,List<String> excludedColumns)throws Exception;
	
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * 根据表名和查询条件进行单表查询
	 * @param tableName 表名，不区分大小写
	 * @param condition 标准sql的查询条件，不包含"where"，即where后的部分
	 * @return List 与表对应的Bean的列表
	 * @throws Exception
	 */
	public List find(String tableName,String condition) throws Exception;
	
	/**
	 * 根据表名和查询条件进行单表查询
	 * @param tableName 表名，不区分大小写
	 * @param condition 标准sql的查询条件，不包含"where"，即where后的部分
	 * @param excludedColumns 不读取的列名/字段名
	 * @return List 与表对应的Bean的列表
	 * @throws Exception
	 */
	public List find(String tableName,String condition,List<String> excludedColumns) throws Exception;
	

	/**
	 * 根据表名和查询条件进行单表查询
	 * @param tableName 表名，不区分大小写
	 * @param condition 标准sql的查询条件，不包含"where"，即where后的部分
	 * @param RPP 每页多少条，大于0的整数
	 * @param PN 第几页，大于0的整数
	 * 
	 * @return List 与表对应的Bean的列表
	 * @throws Exception
	 */
	public List find(String tableName,String condition,int RPP,int PN) throws Exception;
	


	/**
	 * 根据表名和查询条件进行单表查询
	 * @param tableName 表名，不区分大小写
	 * @param condition 标准sql的查询条件，不包含"where"，即where后的部分
	 * @param excludedColumns 不读取的列名/字段名
	 * @param RPP 每页多少条，大于0的整数
	 * @param PN 第几页，大于0的整数
	 * 
	 * @return List 与表对应的Bean的列表
	 * @throws Exception
	 */
	public List find(String tableName,String condition,List<String> excludedColumns,int RPP,int PN) throws Exception;
	
	/**
	 * get the start(inclusive) to end(exclusive)
	 * @param tableName
	 * @param condition
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public List findScale(String tableName,String condition,int start,int end) throws Exception;

	/**
	 * get the start(inclusive) to end(exclusive)
	 * @param tableName
	 * @param condition
	 * @param excludedColumns 不读取的列名/字段名
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public List findScale(String tableName,String condition,List<String> excludedColumns,int start,int end) throws Exception;


	/**
	 * 返回符合条件的第一条记录(if exists)
	 * @param tableName 表名，不区分大小写
	 * @param condition 标准sql的查询条件，不包含"where"，即where后的部分
	 * 
	 * @return 与表对应的Bean
	 * @throws Exception
	 */
	public Object findSingle(String tableName,String condition)throws Exception;
	


	/**
	 * 返回符合条件的第一条记录(if exists)
	 * @param tableName 表名，不区分大小写
	 * @param condition 标准sql的查询条件，不包含"where"，即where后的部分
	 * @param excludedColumns 不读取的列名/字段名
	 * 
	 * @return 与表对应的Bean
	 * @throws Exception
	 */
	public Object findSingle(String tableName,String condition,List<String> excludedColumns)throws Exception;
	
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * 
	 * @param tableName
	 * @param condition
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public List find(String tableName,String condition,Class cls)throws Exception;
	

	/**
	 * 
	 * @param tableName
	 * @param condition
	 * @param cls
	 * @param excludedColumns 不读取的列名/字段名
	 * @return
	 * @throws Exception
	 */
	public List find(String tableName,String condition,Class cls,List<String> excludedColumns)throws Exception;
	
	
	/**
	 * 
	 * @param tableName
	 * @param condition
	 * @param cls
	 * @param RPP
	 * @param PN
	 * @return
	 * @throws Exception
	 */
	public List find(String tableName,String condition,Class cls,int RPP,int PN)throws Exception;
	
	
	/**
	 * 
	 * @param tableName
	 * @param condition
	 * @param cls
	 * @param excludedColumns 不读取的列名/字段名
	 * @param RPP
	 * @param PN
	 * @return
	 * @throws Exception
	 */
	public List find(String tableName,String condition,Class cls,List<String> excludedColumns,int RPP,int PN)throws Exception;
	

	/**
	 * 
	 * @param tableName
	 * @param condition
	 * @param cls
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public List findScale(String tableName,String condition,Class cls,int start,int end)throws Exception;
	

	/**
	 * 
	 * @param tableName
	 * @param condition
	 * @param cls
	 * @param excludedColumns 不读取的列名/字段名
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public List findScale(String tableName,String condition,Class cls,List<String> excludedColumns,int start,int end)throws Exception;
	

	
	/**
	 * 
	 * @param tableName
	 * @param condition
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public Object findSingle(String tableName,String condition,Class cls)throws Exception;

	
	/**
	 * 
	 * @param tableName
	 * @param condition
	 * @param cls
	 * @param excludedColumns 不读取的列名/字段名
	 * @return
	 * @throws Exception
	 */
	public Object findSingle(String tableName,String condition,Class cls,List<String> excludedColumns)throws Exception;
	
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * 根据表名和查询条件进行多表查询
	 * @param tableNames 参与联查的表名的数组
	 * @param condition 标准sql的查询条件，不包含"where"，即where后的部分
	 * 
	 * @return List 对象数组（Object[]）的列表，对象数组中的对象是与表对应的对象，
	 * 其顺序与tableNames中指定的表名顺序一致。例如：
	 * List lst=find(new String[]{"TBL_A","TBL_B"},"TBL_A.ID=TBL_B.ID");
	 * for(int i=0;i<lst.size();i++){
	 * 	Object[] objs=(Object[])lst.get(i);
	 *  TblA a=(TblA)objs[0];
	 *  TblB b=(TblB)objs[1];
	 * }
	 * @throws Exception
	 */
	public List find(String[] tableNames,String condition) throws Exception;
	
	
	/**
	 * 根据表名和查询条件进行多表查询（翻页）
	 * @param tableNames 参与联查的表名的数组
	 * @param condition 标准sql的查询条件，不包含"where"，即where后的部分
	 * @param RPP 每页多少条，大于0的整数
	 * @param PN 第几页，大于0的整数
	 * 
	 * @return List 对象数组（Object[]）的列表，对象数组中的对象是与表对应的对象，
	 * 其顺序与tableNames中指定的表名顺序一致。例如：
	 * List lst=find(new String[]{"TBL_A","TBL_B"},"TBL_A.ID=TBL_B.ID");
	 * for(int i=0;i<lst.size();i++){
	 * 	Object[] objs=(Object[])lst.get(i);
	 *  TblA a=(TblA)objs[0];
	 *  TblB b=(TblB)objs[1];
	 * }
	 * @throws Exception
	 */
	public List find(String[] tableNames,String condition,int RPP,int PN) throws Exception;
	
	
	/**
	 * for the numbers of records 0 to n ,get the start(inclusive) to end(exclusive)
	 * @param tableNames
	 * @param condition
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public List findScale(String[] tableNames,String condition,int start,int end) throws Exception;

	
	/**
	 * 
	 * @param tableNames
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public Object findSingle(String[] tableNames,String condition)throws Exception;
	
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * 
	 * @param tableNames
	 * @param CLSs
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List find(String[] tableNames,Class[] CLSs,String condition) throws Exception;
	
	/**
	 * 
	 * @param tableNames
	 * @param CLSs
	 * @param condition
	 * @param RPP
	 * @param PN
	 * @return
	 * @throws Exception
	 */
	public List find(String[] tableNames,Class[] CLSs,String condition,int RPP,int PN) throws Exception;


	/**
	 * 
	 * @param tableNames
	 * @param CLSs
	 * @param condition
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public List findScale(String[] tableNames,Class[] CLSs,String condition,int start,int end) throws Exception;



	/**
	 * 
	 * @param tableNames
	 * @param CLSs
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public Object findSingle(String[] tableNames,Class[] CLSs,String condition) throws Exception;
	
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////

	
	
	/**
	 * 插入对象
	 * @param vo 与数据表记录对应的value object
	 * @throws Exception
	 */
	public void insert(Object vo)throws Exception;
	public void insertIfNotExists(Object vo)throws Exception;
	public void insertIfNotExists(Object vo,String[] conditionKeys)throws Exception;
	
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////

	
	
	/**
	 * 插入对象
	 * @param vo 与数据表记录对应的value object
	 * @throws Exception
	 */
	public void insert(String tableName,Object vo)throws Exception;
	public void insertIfNotExists(String tableName,Object vo)throws Exception;
	public void insertIfNotExists(String tableName,Object vo,String[] conditionKeys)throws Exception;
	
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 更新符合条件的记录
	 * @param tableName  表名，不区分大小写
	 * @param colsBeUpdated 要更新的列（Map），key: 列名（不区分大小写），value：列的值
	 * @param condition 标准sql的查询条件，不包含"where"，即where后的部分
	 * @throws Exception
	 */
	public void update(String tableName,Map colsBeUpdated,String condition)throws Exception;
	
	/**
	 * 根据vo中指定字段（0个或多个）所组成的条件，将符合条件的记录更新成vo所表示的状态（不包括作为条件的自段）
	 * @param vo
	 * @param conditionKeys 组成的条件的字段名数组，不区分大小写
	 * @throws Exception
	 */
	public void updateByKeys(Object vo,String[] conditionKeys)throws Exception;
	public void updateByKeys(Object vo)throws Exception;

	public void updateByKeys(String tableName,Object vo,String[] conditionKeys)throws Exception;
	public void updateByKeys(String tableName,Object vo)throws Exception;
	
	/**
	 * 根据vo中指定字段（0个或多个）所组成的条件，将符合条件的记录更新成vo所表示的状态（不包括作为条件的自段和为null的字段）
	 * @param vo
	 * @param conditionKeys 组成的条件的字段名数组，不区分大小写
	 * @throws Exception
	 */
	public void updateByKeysIgnoreNulls(Object vo,String[] conditionKeys)throws Exception;
	public void updateByKeysIgnoreNulls(Object vo)throws Exception;
	
	public void updateByKeysIgnoreNulls(String tableName,Object vo,String[] conditionKeys)throws Exception;
	public void updateByKeysIgnoreNulls(String tableName,Object vo)throws Exception;
	
	//updateNullCols指定的列即时为null也更新
	public void updateByKeysIgnoreNulls(Object vo,String[] conditionKeys,List<String> updateNullCols)throws Exception;
	public void updateByKeysIgnoreNulls(Object vo,List<String> updateNullCols)throws Exception;
	
	public void updateByKeysIgnoreNulls(String tableName,Object vo,String[] conditionKeys,List<String> updateNullCols)throws Exception;
	public void updateByKeysIgnoreNulls(String tableName,Object vo,List<String> updateNullCols)throws Exception;
	
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 执行标准sql
	 * @param sql 标准SQL
	 * @throws Exception
	 */
	public void executeSQL(String sql)throws Exception;
	public void executeSQLList(List sqls)throws Exception;
	/**
	 * 
	 * @param sql 标准SQL
	 * @throws Exception
	 */
	public void executeBatchSQL(List sqls)throws Exception;
	
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 得到符合指定标准SQL的记录条数
	 * @param sql 标准、完整的SQL
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getRecordCnt(String sql) throws Exception;
	
	/**
	 * 得到指定表名的，符合condition所指定条件的记录条数
	 * @param tableName 表名，不区分大小写
	 * @param condition 标准sql的查询条件，不包含"where"，即where后的部分
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getRecordCnt(String tableName,String condition) throws Exception;
	
	
	
	/**
	 * 多表联查时，得到符合condition所指定条件的记录数
	 * @param tableNames 参与联查的表名的数组
	 * @param condition 标准sql的查询条件，不包含"where"，即where后的部分
	 * 
	 * @return 
	 * @throws Exception
	 */
	public int getRecordCnt(String[] tableNames,String condition) throws Exception;
	
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
		
	/**
	 * 得到指定表、指定列的，符合condition所指定条件的最小值
	 * @param tableName 表名，不区分大小写
	 * @param colName 列名
	 * @param condition  标准sql的查询条件，不包含"where"，即where后的部分
	 * 
	 * @return 如果没有最小值，返回空字符串 
	 * @throws Exception
	 */
	public String getMinValue(String tableName,String colName,String condition) throws Exception;
	
	/**
	 * 得到指定表、指定列的，符合condition所指定条件的最小值
	 * @param tableName 表名，不区分大小写
	 * @param colName 列名
	 * @param condition  标准sql的查询条件，不包含"where"，即where后的部分
	 * 
	 * @return 如果没有最小值，返回空字符串 
	 * @throws Exception
	 */
	public String getMinNumber(String tableName,String colName,String condition) throws Exception;
	
	/**
	 * 得到指定表、指定列的，符合condition所指定条件的最大值
	 * @param tableName 表名，不区分大小写
	 * @param colName 列名
	 * @param condition  标准sql的查询条件，不包含"where"，即where后的部分
	 * 
	 * @return 如果没有最大值，返回空字符串 
	 * @throws Exception
	 */
	public String getMaxValue(String tableName,String colName,String condition) throws Exception;	
	
	
	/**
	 * 得到指定表、指定列的，符合condition所指定条件的最大值
	 * @param tableName 表名，不区分大小写
	 * @param colName 列名
	 * @param condition  标准sql的查询条件，不包含"where"，即where后的部分
	 * 
	 * @return 如果没有最大值，返回空字符串 
	 * @throws Exception
	 */
	public String getMaxNumber(String tableName,String colName,String condition) throws Exception;	
	
		
	/**
	 * 得到指定表、指定列的，符合condition所指定条件的值的和
	 * @param tableName 表名，不区分大小写
	 * @param colName 列名
	 * @param condition  标准sql的查询条件，不包含"where"，即where后的部分
	 * 
	 * @return 如果没有相关值，返回空字符串 
	 * @throws Exception
	 */
	public String getSum(String tableName,String colName,String condition) throws Exception;
	
	/**
	 * 分别得到指定表、多个列的，符合condition所指定条件的值的和
	 * @param tableName 表名，不区分大小写
	 * @param colNames 列名
	 * @param condition  标准sql的查询条件，不包含"where"，即where后的部分
	 * 
	 * @return 如果没有相关值，返回空字符串 
	 * @throws Exception
	 */
	public String[] getSum(String tableName,String[] colNames,String condition) throws Exception;
	
	/**
	 * 
	 * @param tableName
	 * @param colName
	 * @return
	 * @throws Exception
	 */
	public String autoIncreaseKey(String tableName,String colName) throws Exception;
	public String autoIncreaseKey(String tableName,String colName,long addition) throws Exception;
	public String autoIncreaseKeyLargerThan(String tableName,String colName,long min) throws Exception;
	public String autoIncreaseKeyLargerThan(String tableName,String colName,long min,long addition) throws Exception;
	
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
		
	/**
	 * 得到数据库编目列表
	 * @return List 编目名称的列表
	 * @throws Exception
	 */
	public List getCatalogs()throws Exception;
	
	/**
	 * 得到数据库模式列表
	 * @return List 模式名称的列表
	 * @throws Exception
	 */
	public List getSchemas()throws Exception;
	
	/**
	 * 得到数据库中的表、视图等对象
	 * @param catalog 编目名（可包含通配符?和*，null表示全部）
	 * @param schemaPattern 编目名（可包含通配符?和*，null表示全部）
	 * @param tableNamePattern 表名（可包含通配符?和*，null表示全部）
	 * @param tableNameTypes 取值范围：TABLE, VIEW, SYSTEM TABLE, GLOBAL TEMPORARY, LOCAL TEMPORARY, ALIAS, SYNONYM
	 * 
	 * @return 相关对象名称的列表
	 * @throws Exception
	 */
	public List getTables(String catalog, String schemaPattern,String tblPattern, String[] tblTypes)throws Exception;
	
	/**
	 * 得到指定表的列
	 * @param tableName 表名，不区分大小写
	 * 
	 * @return Column对象的列表
	 * @throws Exception
	 */
	public List getColumns(String tableName)throws Exception;
	
	/**
	 * 得到指定表的主键列
	 * @param tableName 表名，不区分大小写
	 * 
	 * @return Column对象的数组
	 * @throws Exception
	 */
	public Column[] getPrimaryKeyColumns(String tableName)throws Exception;
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 *
	 */
	public void disablePlugin();
	public void enablePlugin();
	public boolean isPluginEnabled();
}



