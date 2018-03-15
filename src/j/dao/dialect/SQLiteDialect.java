package j.dao.dialect;

import java.sql.ResultSet;
import java.sql.Statement;

import j.dao.RdbmsDao;
import j.dao.StmtAndRs;


/**
 * @author JFramework
 * select * from messages limit 10,100;
 * 表示跳过10行，取100行的返回结果。
 */
public class SQLiteDialect extends RdbmsDao{
	/**
	 * 
	 *
	 */
	public SQLiteDialect() {
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
		
		return sql+limit;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.RdbmsDao#supportsLimitOffset()
	 */
	public boolean supportsLimitOffset() {
		return true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#findScale(java.lang.String, int, int)
	 */
	public StmtAndRs findScale(String sql,int start, int end) throws Exception {
		//log.log("ROAR DAO SQL(before paging): "+sql,Logger.LEVEL_DEBUG);
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
				//log.log("ROAR DAO SQL(after paging): "+sql,Logger.LEVEL_DEBUG);
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
}
