package j.dao.dialect;

import j.dao.RdbmsDao;


/**
 * @author JFramework
 *
 */
public class HSQLDialect extends RdbmsDao{
	/**
	 * 
	 *
	 */
	public HSQLDialect() {
		super();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.RdbmsDao#getSQLWithRowSetLimit(java.lang.String, int, int)
	 */
	public String getSQLWithRowSetLimit(String sql,int start, int end) {
		String tmpSql=sql.toLowerCase();
		int point=tmpSql.indexOf("select")+6;
		String limit=" limit ";
		if(start<0){
			start=0;
		}
		if(end<0){
			end=0;
		}
		limit+=start+" "+(end-start)+" ";
		return sql.substring(0,point)+limit+sql.substring(point);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.RdbmsDao#supportsLimitOffset()
	 */
	public boolean supportsLimitOffset() {
		return true;
	}
}
