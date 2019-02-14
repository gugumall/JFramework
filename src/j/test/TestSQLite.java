package j.test;

import j.dao.DAO;
import j.dao.DAOFactory;
import j.dao.DB;

/**
 * 
 * @author 肖炯
 *
 */
public class TestSQLite {
	public static DAOFactory daoFactory;
	
	/**
	 * 
	 *
	 */
	public TestSQLite() {
		super();
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{		 
		DAO dao=DB.connect("JFS",TestSQLite.class);
		dao.executeSQL("create table IF NOT EXISTS fs_task (uuid,time Timestamp,from_uuid,to_uuid,path,operation,data,status)");
		dao.close();
		

		dao=DB.connect("JCache",TestSQLite.class);
		dao.executeSQL("create table IF NOT EXISTS cache_task (uuid,time Timestamp,from_uuid,to_uuid,operation,data,status)");
		
		
		for(int i=0;i<100;i++){
			String key=dao.autoIncreaseKey("fs_task","uuid");
			System.out.println(key);
		}
		
		dao.close();
	}
}
