package j.dao;


/**
 * 
 * @author 肖炯
 *
 */
public class Query {
	public static final int TYPE_SQL_NO_RESULT=0;
	public static final int TYPE_SQL=1;
	public static final int TYPE_TABLE_AND_CONDITION=2;
	public static final int TYPE_COUNT=3;
	public static final int TYPE_INSERT=4;
	public static final int TYPE_INSERT_IF_NOT_EXISTS=5;
	public static final int TYPE_UPDATE=6;
	public static final int TYPE_UPDATE_IGNORE_NULLS=7;
	public static final int TYPE_FIND_ONE_TBL_SINGLE=8;
	public static final int TYPE_FIND_MULTI_TBL_SINGLE=9;
	public static final int TYPE_SUM=10;
	
	/**
	 * 
	 * @param uuid
	 * @param type
	 */
	Query(String uuid,int type){
		this.uuid=uuid;
		this.type=type;
	}
	
	String uuid;
	int type;
	String[] tableNames;
	String condition;
	String[] keys=null;
	int rpp=0;
	int pn=0;
	String sql;
	String column;
	Object bean;
	int tries=0;
	Class _class;
}