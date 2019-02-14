package j.dao;

import java.util.Map;

/**
 * 
 * @author 肖炯
 *
 */
public interface DAOPlugin {
	/**
	 * 
	 * @param fac
	 */
	public void setFactory(DAOFactory fac);	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void onBeginTransaction() throws Exception;	
	public void onCommit() throws Exception;
	public void onRollback() throws Exception;
	
	/**
	 * 插入对象
	 * @param vo
	 * @throws Exception
	 */
	public void beforeInsert(Object vo)throws Exception;
	public void afterInsert(Object vo)throws Exception;
	
	/**
	 * 更新符合条件的记录
	 * @param tblName  表名，不区分大小写
	 * @param colsBeUpdated 要更新的列（Map），key: 列名（不区分大小写），value：列的值
	 * @param condition 标准sql的查询条件，不包含"where"，即where后的部分
	 * @throws Exception
	 */
	public void beforeUpdate(String tblName,Map colsBeUpdated,String condition)throws Exception;
	public void afterUpdate(String tblName,Map colsBeUpdated,String condition)throws Exception;
	
	/**
	 * 根据bean中指定字段（0个或多个）所组成的条件，将符合条件的记录更新成bean所表示的状态（不包括作为条件的自段）
	 * @param bean
	 * @param conditionKeys 组成的条件的字段名数组，不区分大小写
	 * @throws Exception
	 */
	public void beforeUpdateByKeys(Object vo,String[] conditionKeys)throws Exception;
	public void afterUpdateByKeys(Object vo,String[] conditionKeys)throws Exception;
	
	/**
	 * 根据bean中指定字段（0个或多个）所组成的条件，将符合条件的记录更新成bean所表示的状态（不包括作为条件的自段）
	 * @param bean
	 * @param conditionKeys 组成的条件的字段名数组，不区分大小写
	 * @throws Exception
	 */
	public void beforeUpdateByKeysIgnoreNulls(Object vo,String[] conditionKeys)throws Exception;
	public void afterUpdateByKeysIgnoreNulls(Object vo,String[] conditionKeys)throws Exception;
	
	/**
	 * 执行标准sql
	 * @param sql 标准SQL
	 * @throws Exception
	 */
	public void beforeExecuteSQL(String sql)throws Exception;
	public void afterExecuteSQL(String sql)throws Exception;
	
	/**
	 * 
	 *
	 */
	public void destroy();
}
