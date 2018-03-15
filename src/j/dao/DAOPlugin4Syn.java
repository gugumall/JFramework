package j.dao;

import j.dao.util.SQLUtil;
import j.log.Logger;

import java.util.Map;

/**
 * DAOPlugin实现范例，用于输出sql
 * @author JFramework
 *
 */
public class DAOPlugin4Syn implements DAOPlugin{
	private static Logger log=Logger.create(DAOPlugin4Syn.class);
	private DAOFactory fac;

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#setFactory(j.dao.DAOFactory)
	 */
	public void setFactory(DAOFactory fac) {
		this.fac=fac;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#onBeginTransaction()
	 */
	public void onBeginTransaction() throws Exception {
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#onRollback()
	 */
	public void onRollback() throws Exception{
		
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#onCommit()
	 */
	public void onCommit() throws Exception {
		
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#beforeInsert(java.lang.Object)
	 */
	public void beforeInsert(Object vo) throws Exception {
		String sql=SQLUtil.retrieveInsertSQL(vo,fac);
		log.log("before insert(Object vo) - "+sql,Logger.LEVEL_INFO);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#afterInsert(java.lang.Object)
	 */
	public void afterInsert(Object vo) throws Exception {
		String sql=SQLUtil.retrieveInsertSQL(vo,fac);
		log.log("after insert(Object vo) - "+sql,Logger.LEVEL_INFO);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#beforeUpdate(java.lang.String, java.util.Map, java.lang.String)
	 */
	public void beforeUpdate(String tblName, Map colsBeUpdated, String condition) throws Exception {
		String sql=SQLUtil.retrieveUpdateSQL(tblName,colsBeUpdated,condition);
		log.log("before update(String tblName,Map colsBeUpdated,String condition) - "+sql,Logger.LEVEL_INFO);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#afterUpdate(java.lang.String, java.util.Map, java.lang.String)
	 */
	public void afterUpdate(String tblName, Map colsBeUpdated, String condition) throws Exception {
		String sql=SQLUtil.retrieveUpdateSQL(tblName,colsBeUpdated,condition);
		log.log("after update(String tblName,Map colsBeUpdated,String condition) - "+sql,Logger.LEVEL_INFO);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#beforeUpdateByKeys(java.lang.Object, java.lang.String[])
	 */
	public void beforeUpdateByKeys(Object vo, String[] conditionKeys) throws Exception {
		String sql=SQLUtil.retrieveUpdateSQL(vo,conditionKeys,fac);
		log.log("before updateByKeys(Object vo,String[] conditionKeys) - "+sql,Logger.LEVEL_INFO);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#afterUpdateByKeys(java.lang.Object, java.lang.String[])
	 */
	public void afterUpdateByKeys(Object vo, String[] conditionKeys) throws Exception {
		String sql=SQLUtil.retrieveUpdateSQL(vo,conditionKeys,fac);
		log.log("after updateByKeys(Object vo,String[] conditionKeys) - "+sql,Logger.LEVEL_INFO);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#beforeUpdateByKeysIgnoreNulls(java.lang.Object, java.lang.String[])
	 */
	public void beforeUpdateByKeysIgnoreNulls(Object vo, String[] conditionKeys) throws Exception {
		String sql=SQLUtil.retrieveUpdateSQLIgnoreNulls(vo,conditionKeys,fac);
		log.log("before updateByKeysIgnoreNulls(Object vo,String[] conditionKeys) - "+sql,Logger.LEVEL_INFO);		
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#afterUpdateByKeysIgnoreNulls(java.lang.Object, java.lang.String[])
	 */
	public void afterUpdateByKeysIgnoreNulls(Object vo, String[] conditionKeys) throws Exception {
		String sql=SQLUtil.retrieveUpdateSQLIgnoreNulls(vo,conditionKeys,fac);
		log.log("after updateByKeysIgnoreNulls(Object vo,String[] conditionKeys) - "+sql,Logger.LEVEL_INFO);
	}

	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#beforeExecuteSQL(java.lang.String)
	 */
	public void beforeExecuteSQL(String sql) throws Exception {
		log.log("before executeSQL(String sql) - "+sql,Logger.LEVEL_INFO);	
	}

	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#afterExecuteSQL(java.lang.String)
	 */
	public void afterExecuteSQL(String sql) throws Exception {
		log.log("after executeSQL(String sql) - "+sql,Logger.LEVEL_INFO);			
	}

	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAOPlugin#destroy()
	 */
	public void destroy() {		
	}
}
