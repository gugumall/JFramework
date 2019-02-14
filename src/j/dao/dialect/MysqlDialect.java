package j.dao.dialect;

import j.dao.RdbmsDao;


/**
 * @author 肖炯
 *
 */
public class MysqlDialect extends RdbmsDao{
	/**
	 * 
	 *
	 */
	public MysqlDialect() {
		super();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.RdbmsDao#getSQLWithRowSetLimit(java.lang.String, int, int)
	 */
	public String getSQLWithRowSetLimit(String sql,int start, int end) {
		String limit=" limit ";
		if(start<0){
			start=0;
		}
		if(end<0){
			end=0;
		}
		limit+=start+","+(end-start)+" ";
		String tmp=sql.toUpperCase();
		
		if(tmp.indexOf("FOR UPDATE")>0){
			return sql.substring(0,tmp.indexOf("FOR UPDATE"))+limit+" for update";
		}else{
			return sql+limit;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.RdbmsDao#supportsLimitOffset()
	 */
	public boolean supportsLimitOffset() {
		return true;
	}
}
