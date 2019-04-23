package j.dao;

import j.log.Logger;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilMath;
import j.util.JUtilUUID;

import java.util.List;

/**
 * 
 * @author 肖炯
 *
 */
public class QueryPool implements Runnable{
	private static Logger log=Logger.create(QueryPool.class);
	private static ConcurrentMap pools=new ConcurrentMap();
	private String databasename=null;
	private String poolName;
	private int executors=1;
	private ConcurrentList threads=new ConcurrentList();
	private ConcurrentMap executorOfuuid=new ConcurrentMap();
	//private int selector=0;
	private volatile long commands=0;
	private static final Object lock=new Object();
	
	private static final String COMMON_POOL="COMMON_POOL";
	private static int COMMON_POOL_SIZE=10;
	
	static{
		String temp=j.Properties.getProperty("QueryPool.Common.Size");
		if(JUtilMath.isInt(temp)&&Integer.parseInt(temp)>0){
			COMMON_POOL_SIZE=Integer.parseInt(temp);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static QueryPool getPool(){
		return getPool(COMMON_POOL,COMMON_POOL_SIZE);
	}

	/**
	 * 
	 * @param poolName
	 * @param executors
	 * @return
	 */
	public static QueryPool getPool(String poolName,int executors){
		synchronized(lock){
			QueryPool pool=(QueryPool)pools.get(poolName);
			if(pool==null){
				pool=new QueryPool(poolName,executors);
				pool.init();
				pools.put(poolName, pool);
				

				Thread monitor=new Thread(pool);
				monitor.start();
				log.log("monitor for Query Pool "+poolName+" started.",-1);
			}else{
				log.log("pool "+poolName+" exists, parameter executors["+executors+"] is being ignored.", Logger.LEVEL_INFO);
			}
			
			return pool;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static QueryPool getPool(String databasename){
		return getPool(databasename,COMMON_POOL,COMMON_POOL_SIZE);
	}

	/**
	 * 
	 * @param poolName
	 * @param executors
	 * @return
	 */
	public static QueryPool getPool(String databasename,String poolName,int executors){
		synchronized(lock){
			QueryPool pool=(QueryPool)pools.get(poolName);
			if(pool==null){
				pool=new QueryPool(databasename,poolName,executors);
				pool.init();
				pools.put(poolName, pool);
				

				Thread monitor=new Thread(pool);
				monitor.start();
				log.log("monitor for Query Pool "+poolName+" started.",-1);
			}else{
				log.log("pool "+poolName+" exists, parameter executors["+executors+"] is being ignored.", Logger.LEVEL_INFO);
			}
			
			return pool;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public QueryExecutor selectExecutor(){
		synchronized(lock){
			//if(selector>=threads.size()) selector=0;
			
			QueryExecutor executorOfMinTasks=null;
			for(int i=0;i<threads.size();i++){
				QueryExecutor e=(QueryExecutor)threads.get(i);
				if(executorOfMinTasks==null||executorOfMinTasks.getTasks()>e.getTasks()){
					executorOfMinTasks=e;
				}
			}
			
			return executorOfMinTasks;
		}
	}
	
	/**
	 * 
	 * @param uuid
	 * @param sql
	 */
	public void execute(String uuid,String sql){
		commands++;
		selectExecutor().execute(uuid!=null?uuid:JUtilUUID.genUUID(),sql,true);
	}

	
	/**
	 * 
	 * @param uuid
	 * @param sql
	 */
	public void executeAsyn(String uuid,String sql){
		commands++;
		selectExecutor().execute(uuid!=null?uuid:JUtilUUID.genUUID(),sql,false);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 */
	public void insert(String uuid,Object bean){
		commands++;
		selectExecutor().insert(uuid!=null?uuid:JUtilUUID.genUUID(),bean,true);
	}

	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 */
	public void insertAsyn(String uuid,Object bean){
		commands++;
		selectExecutor().insert(uuid!=null?uuid:JUtilUUID.genUUID(),bean,false);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 */
	public void insertIfNotExists(String uuid,Object bean){
		commands++;
		selectExecutor().insertIfNotExists(uuid!=null?uuid:JUtilUUID.genUUID(),bean,true);
	}

	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 */
	public void insertIfNotExistsAsyn(String uuid,Object bean){
		commands++;
		selectExecutor().insertIfNotExists(uuid!=null?uuid:JUtilUUID.genUUID(),bean,false);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 */
	public void update(String uuid,Object bean){
		commands++;
		selectExecutor().update(uuid!=null?uuid:JUtilUUID.genUUID(),bean,true);
	}

	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 */
	public void updateAsyn(String uuid,Object bean){
		commands++;
		selectExecutor().update(uuid!=null?uuid:JUtilUUID.genUUID(),bean,false);
	}

	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 */
	public void updateIgnoreNulls(String uuid,Object bean){
		commands++;
		selectExecutor().updateIgnoreNulls(uuid!=null?uuid:JUtilUUID.genUUID(),bean,true);
	}

	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 */
	public void updateIgnoreNullsAsyn(String uuid,Object bean){
		commands++;
		selectExecutor().updateIgnoreNulls(uuid!=null?uuid:JUtilUUID.genUUID(),bean,false);
	}

	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 * @param keys
	 */
	public void update(String uuid,Object bean,String[] keys){
		commands++;
		selectExecutor().update(uuid!=null?uuid:JUtilUUID.genUUID(),bean,keys,true);
	}

	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 * @param keys
	 */
	public void updateAsyn(String uuid,Object bean,String[] keys){
		commands++;
		selectExecutor().update(uuid!=null?uuid:JUtilUUID.genUUID(),bean,keys,false);
	}

	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 * @param keys
	 */
	public void updateIgnoreNulls(String uuid,Object bean,String[] keys){
		commands++;
		selectExecutor().updateIgnoreNulls(uuid!=null?uuid:JUtilUUID.genUUID(),bean,keys,true);
	}

	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 * @param keys
	 */
	public void updateIgnoreNullsAsyn(String uuid,Object bean,String[] keys){
		commands++;
		selectExecutor().updateIgnoreNulls(uuid!=null?uuid:JUtilUUID.genUUID(),bean,keys,false);
	}
	
	
	/**
	 * 
	 * @param uuid
	 * @param sql
	 * @return
	 */
	public StmtAndRs query(String uuid,String sql){
		commands++;
		return selectExecutor().query(uuid!=null?uuid:JUtilUUID.genUUID(),sql,true);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param sql
	 * @param rpp
	 * @param pn
	 * @return
	 */
	public StmtAndRs query(String uuid,String sql,int rpp,int pn){
		commands++;
		return selectExecutor().query(uuid!=null?uuid:JUtilUUID.genUUID(),sql,rpp,pn,true);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param condition
	 * @return
	 */
	public List query(String uuid,String table,String condition){
		commands++;
		return selectExecutor().query(uuid!=null?uuid:JUtilUUID.genUUID(),table,condition,true);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param condition
	 * @param rpp
	 * @param pn
	 * @return
	 */
	public List query(String uuid,String table,String condition,int rpp,int pn){
		commands++;
		return selectExecutor().query(uuid!=null?uuid:JUtilUUID.genUUID(),table,condition,rpp,pn,true);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param tables
	 * @param condition
	 * @return
	 */
	public List query(String uuid,String[] tables,String condition){
		commands++;
		return selectExecutor().query(uuid!=null?uuid:JUtilUUID.genUUID(),tables,condition,true);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param tables
	 * @param condition
	 * @param rpp
	 * @param pn
	 * @return
	 */
	public List query(String uuid,String[] tables,String condition,int rpp,int pn){
		commands++;
		return selectExecutor().query(uuid!=null?uuid:JUtilUUID.genUUID(),tables,condition,rpp,pn,true);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param condition
	 * @return
	 */
	public Object querySingle(String uuid,String table,String condition){
		commands++;
		List temp=query(uuid,table,condition,1,1);
		return temp.size()==0?null:temp.get(0);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param tables
	 * @param condition
	 * @param rpp
	 * @param pn
	 * @return
	 */
	public Object querySingle(String uuid,String[] tables,String condition){
		commands++;
		List temp=query(uuid,tables,condition,1,1);
		return temp.size()==0?null:temp.get(0);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param sql
	 * @return
	 */
	public Integer count(String uuid,String sql){
		commands++;
		return selectExecutor().count(uuid!=null?uuid:JUtilUUID.genUUID(),sql,true);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param condition
	 * @return
	 */
	public Integer count(String uuid,String table,String condition){
		commands++;
		return selectExecutor().count(uuid!=null?uuid:JUtilUUID.genUUID(),table,condition,true);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param tables
	 * @param condition
	 * @return
	 */
	public Integer count(String uuid,String[] tables,String condition){
		commands++;
		return selectExecutor().count(uuid!=null?uuid:JUtilUUID.genUUID(),tables,condition,true);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param column
	 * @param condition
	 * @return
	 */
	public Double sum(String uuid,String table,String column,String condition){
		commands++;
		return selectExecutor().sum(uuid!=null?uuid:JUtilUUID.genUUID(),table,column,condition,true);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param sql
	 */
	public String queryAsyn(String uuid,String sql){
		commands++;
		QueryExecutor exe=selectExecutor();
		if(uuid==null) uuid=JUtilUUID.genUUID();
		
		exe.query(uuid,sql,false);
		executorOfuuid.put(uuid, exe);
		
		return uuid;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param sql
	 * @param rpp
	 * @param pn
	 */
	public String queryAsyn(String uuid,String sql,int rpp,int pn){
		commands++;
		QueryExecutor exe=selectExecutor();
		if(uuid==null) uuid=JUtilUUID.genUUID();
		
		exe.query(uuid,sql,rpp,pn,false);
		executorOfuuid.put(uuid, exe);
		
		return uuid;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param condition
	 */
	public String queryAsyn(String uuid,String table,String condition){
		commands++;
		QueryExecutor exe=selectExecutor();
		if(uuid==null) uuid=JUtilUUID.genUUID();
		
		exe.query(uuid,table,condition,false);
		executorOfuuid.put(uuid, exe);
		
		return uuid;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param condition
	 * @param rpp
	 * @param pn
	 */
	public String queryAsyn(String uuid,String table,String condition,int rpp,int pn){
		commands++;
		QueryExecutor exe=selectExecutor();
		if(uuid==null) uuid=JUtilUUID.genUUID();
		
		exe.query(uuid,table,condition,rpp,pn,false);
		executorOfuuid.put(uuid, exe);
		
		return uuid;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param tables
	 * @param condition
	 */
	public String queryAsyn(String uuid,String[] tables,String condition){
		commands++;
		QueryExecutor exe=selectExecutor();
		if(uuid==null) uuid=JUtilUUID.genUUID();
		
		exe.query(uuid,tables,condition,false);
		executorOfuuid.put(uuid, exe);
		
		return uuid;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param tables
	 * @param condition
	 * @param rpp
	 * @param pn
	 */
	public String queryAsyn(String uuid,String[] tables,String condition,int rpp,int pn){
		commands++;
		QueryExecutor exe=selectExecutor();
		if(uuid==null) uuid=JUtilUUID.genUUID();
		
		exe.query(uuid,tables,condition,rpp,pn,false);
		executorOfuuid.put(uuid, exe);
		
		return uuid;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param condition
	 * @return
	 */
	public String querySingleAsyn(String uuid,String table,String condition){
		commands++;
		QueryExecutor exe=selectExecutor();
		if(uuid==null) uuid=JUtilUUID.genUUID();
		
		exe.querySingle(uuid,table,condition,false);
		executorOfuuid.put(uuid, exe);
		
		return uuid;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param tables
	 * @param condition
	 * @return
	 */
	public Object querySingleAsyn(String uuid,String[] tables,String condition){
		commands++;
		QueryExecutor exe=selectExecutor();
		if(uuid==null) uuid=JUtilUUID.genUUID();
		
		exe.querySingle(uuid,tables,condition,false);
		executorOfuuid.put(uuid, exe);
		
		return uuid;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param sql
	 * @return
	 */
	public String countAsyn(String uuid,String sql){
		commands++;
		QueryExecutor exe=selectExecutor();
		if(uuid==null) uuid=JUtilUUID.genUUID();
		
		selectExecutor().count(uuid,sql,false);
		executorOfuuid.put(uuid, exe);
		
		return uuid;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param condition
	 * @return
	 */
	public String countAsyn(String uuid,String table,String condition){
		QueryExecutor exe=selectExecutor();
		if(uuid==null) uuid=JUtilUUID.genUUID();
		
		selectExecutor().count(uuid,table,condition,false);
		executorOfuuid.put(uuid, exe);
		
		return uuid;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param tables
	 * @param condition
	 * @return
	 */
	public String countAsyn(String uuid,String[] tables,String condition){
		commands++;
		QueryExecutor exe=selectExecutor();
		if(uuid==null) uuid=JUtilUUID.genUUID();
		
		selectExecutor().count(uuid,tables,condition,false);
		executorOfuuid.put(uuid, exe);
		
		return uuid;
	}
	
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public Object result(String uuid){
		if(!executorOfuuid.containsKey(uuid)) return null;
		
		QueryExecutor exe=(QueryExecutor)executorOfuuid.remove(uuid);
		
		if(exe==null) return null;
		
		return exe.result(uuid);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDatabaseName(){
		return this.databasename;
	}
	
	/**
	 * 
	 * @param poolName
	 * @param executors
	 */
	private QueryPool(String poolName,int executors){
		if(executors<=0) executors=1;
		this.poolName=poolName;
		this.executors=executors;
	}
	
	/**
	 * 
	 * @param databasename
	 * @param poolName
	 * @param executors
	 */
	private QueryPool(String databasename,String poolName,int executors){
		if(executors<=0) executors=1;
		this.databasename=databasename;
		this.poolName=poolName;
		this.executors=executors;
	}
	
	/**
	 * 
	 */
	private void init(){
		for(int i=0;i<executors;i++){
			QueryExecutor exe=new QueryExecutor(this.databasename,"excutor-"+i, poolName);
			Thread thread=new Thread(exe);
			thread.start();
			log.log("excutor-"+i+" of pool "+poolName+" is started.", -1);
			
			threads.add(exe);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			try{
				Thread.sleep(30000);
			}catch(Exception e){}
			
			try{
				//log.log("db executor pool "+poolName+", commands - "+commands,-1);
				for(int i=0;i<threads.size();i++){
					QueryExecutor exe=(QueryExecutor)threads.get(i);
					if(exe.getQueueLength()>10||exe.getResults()>10){
						//log.log("excutor-"+i+" of pool "+poolName+",queue - "+exe.getQueueLength()+",results - "+exe.getResults(), -1);
					}
				}
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
			
			try{
				Thread.sleep(300000);
			}catch(Exception e){}
		}
	}
}
