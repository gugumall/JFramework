package j.dao;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Statement和相关联的ResultSet、SQL语句，调用close方法正确关闭Statement和ResultSet
 * @author JFramework
 *
 */
public class StmtAndRs {
	private Statement stmt;
	private ResultSet rs;
	private String relatedSql;
	
	/**
	 * constructor
	 * @param _stmt
	 * @param _rs
	 * @param _relatedSql
	 */
	public StmtAndRs(Statement _stmt,ResultSet _rs,String _relatedSql){
		stmt=_stmt;
		rs=_rs;
		relatedSql=_relatedSql;
	}
	
	/**
	 * 关闭Statement和ResultSet
	 *
	 */
	public void close(){
		try{
			rs.close();
			rs=null;
		}catch(Exception e){}
		try{
			stmt.close();
			stmt=null;
		}catch(Exception e){}
	}
	
	/**
	 * getter
	 * @return
	 */
	public ResultSet resultSet(){
		return rs;
	}
	
	/**
	 * getter
	 * @return
	 */
	public Statement statemnet(){
		return stmt;
	}
	
	/**
	 * getter
	 * @return
	 */
	public String getRelatedSql(){
		return relatedSql;
	}
}
