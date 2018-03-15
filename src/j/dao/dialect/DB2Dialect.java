package j.dao.dialect;

import j.dao.RdbmsDao;



/**
 * 
 * @author JFramework
 *
 */
public class DB2Dialect extends RdbmsDao {
	/**
	 * 
	 *
	 */
	public DB2Dialect() {
		super();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.RdbmsDao#getSQLWithRowSetLimit(java.lang.String, int, int)
	 */
	public String getSQLWithRowSetLimit(String sql,int start, int end) {
		StringBuffer pageSelectSQL=new StringBuffer("select * from ( select row_.*,");
		pageSelectSQL.append(this.getRowNumber(sql));
		pageSelectSQL.append(" from (");
		pageSelectSQL.append(sql);
		pageSelectSQL.append(" ) as row_ ) as temp_ where rownumber_ between ");
		pageSelectSQL.append((start+1)+" and "+end);
		return pageSelectSQL.toString();
	}
	//select * from(select row_.*,rownumber() over() as rownumber_ from (select * from table) as row_) as temp_ where rownumber_ between 1 and 2


	/*
	 *  (non-Javadoc)
	 * @see j.dao.RdbmsDao#supportsLimitOffset()
	 */
	public boolean supportsLimitOffset() {
		return true;
	}
	

	/**
	 * 
	 * @param sql
	 * @return boolean
	 */
	private boolean hasDistinct(String sql) {
		String tmp=sql.toLowerCase();
		tmp=tmp.replaceAll(" ","");
		if(tmp.indexOf("selectdistinct")!=-1){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param sql
	 * @return
	 */
	private String getRowNumber(String sql) {
		String tmp=sql.toLowerCase();
		
		StringBuffer rownumber = new StringBuffer(50).append("rownumber() over(");

		int orderByIndex = tmp.indexOf("order by");
		
		if ( orderByIndex>0 && !hasDistinct(sql) ) {
			//rownumber.append( StringUtil.replaceAll(sql.substring(orderByIndex),".","_"));
			rownumber.append(sql.substring(orderByIndex));
		}
		rownumber.append(") as rownumber_");
		
		return rownumber.toString();
	}
}






