package j.dao.dialect;

import j.dao.RdbmsDao;

/**
 * @author JFramework
 *
 */
public class SqlServerDialect  extends RdbmsDao {
	/**
	 * 
	 *
	 */
	public SqlServerDialect() {
		super();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.RdbmsDao#getSQLWithRowSetLimit(java.lang.String, int, int)
	 */
	public String getSQLWithRowSetLimit(String sql, int start, int end) {
		StringBuffer pageSelectSQL=new StringBuffer("");
		pageSelectSQL.append(sql);
		pageSelectSQL.insert( getAfterSelectInsertPoint(sql), " top " + end);
		/*
		pageSelectSQL.append(" as row_");
		pageSelectSQL.insert(0,"select top "+(end-start)+" row_.* from (");

		SELECT TOP 页大小 * 
		FROM TestTable 
		WHERE (ID NOT IN 
		(SELECT TOP 页大小*页数 id 
		FROM 表 
		ORDER BY id)) 
		ORDER BY ID 
   	    */
		return pageSelectSQL.toString();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.RdbmsDao#supportsLimitOffset()
	 */
	public boolean supportsLimitOffset() {
		return false;
	}
	
	/**
	 * 
	 * @param sql
	 * @return int
	 */
	private int getAfterSelectInsertPoint(String sql) {
		return sql.startsWith("select distinct") ? 15 : 6;
	}
	
	/**
	 * test
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args){
		SqlServerDialect s=new SqlServerDialect();
		System.out.println(s.getSQLWithRowSetLimit("select * from roar where id=88",2,10));
	}
}
