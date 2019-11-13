package j.dao;

import j.log.Logger;
import j.sys.SysConfig;
import j.util.ConcurrentMap;
import j.util.JUtilMath;

import java.util.List;

/**
 * 
 * @author 肖炯
 *
 */
public class QueryExecutor implements Runnable{
	private static Logger log=Logger.create(QueryExecutor.class);
	private String databaseName=null;
	private DAO dao;
	private ConcurrentMap tasks=new ConcurrentMap();
	private ConcurrentMap results=new ConcurrentMap();
	private String executorName;
	private String poolName;
	private boolean running=false;
	
	/**
	 * 
	 * @param executorName
	 * @param poolName
	 */
	protected QueryExecutor(String executorName,String poolName){
		this.poolName=poolName;
		running=true;
		this.ensureDAO();
	}
	
	/**
	 * 
	 * @param databaseName
	 * @param executorName
	 * @param poolName
	 */
	protected QueryExecutor(String databaseName,String executorName,String poolName){
		this.databaseName=databaseName;
		this.poolName=poolName;
		running=true;
		this.ensureDAO();
	}
	
	/**
	 * 
	 * @return
	 */
	protected int getTasks(){
		return tasks.size();
	}
	
	/**
	 * 待处理任务
	 * @return
	 */
	protected int getQueueLength(){
		return this.tasks.size();
	}
	
	/**
	 * 未取走结果数
	 * @return
	 */
	protected int getResults(){
		return this.results.size();
	}
	
	/**
	 * 
	 * @param uuid
	 * @param sql
	 * @param syn
	 * @return
	 */
	protected void execute(String uuid,String sql,boolean syn){
		if(tasks.containsKey(uuid)) return;
		
		Query query=new Query(uuid,Query.TYPE_SQL_NO_RESULT);
		query.sql=sql;
		tasks.put(uuid,query);
		
		if(!syn) return;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 * @param syn
	 */
	protected void insert(String uuid,Object bean,boolean syn){
		if(tasks.containsKey(uuid)) return;
		
		Query query=new Query(uuid,Query.TYPE_INSERT);
		query.bean=bean;
		tasks.put(uuid,query);
		
		if(!syn) return;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 * @param syn
	 */
	protected void insertIfNotExists(String uuid,Object bean,boolean syn){
		if(tasks.containsKey(uuid)) return;
		
		Query query=new Query(uuid,Query.TYPE_INSERT_IF_NOT_EXISTS);
		query.bean=bean;
		tasks.put(uuid,query);
		
		if(!syn) return;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 * @param syn
	 */
	protected void update(String uuid,Object bean,boolean syn){
		if(tasks.containsKey(uuid)) return;
		
		Query query=new Query(uuid,Query.TYPE_UPDATE);
		query.bean=bean;
		tasks.put(uuid,query);
		
		if(!syn) return;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 * @param syn
	 */
	protected void updateIgnoreNulls(String uuid,Object bean,boolean syn){
		if(tasks.containsKey(uuid)) return;
		
		Query query=new Query(uuid,Query.TYPE_UPDATE_IGNORE_NULLS);
		query.bean=bean;
		tasks.put(uuid,query);
		
		if(!syn) return;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 * @param keys
	 * @param syn
	 */
	protected void update(String uuid,Object bean,String[] keys,boolean syn){
		if(tasks.containsKey(uuid)) return;
		
		Query query=new Query(uuid,Query.TYPE_UPDATE);
		query.bean=bean;
		query.keys=keys;
		tasks.put(uuid,query);
		
		if(!syn) return;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param bean
	 * @param keys
	 * @param syn
	 */
	protected void updateIgnoreNulls(String uuid,Object bean,String[] keys,boolean syn){
		if(tasks.containsKey(uuid)) return;
		
		Query query=new Query(uuid,Query.TYPE_UPDATE_IGNORE_NULLS);
		query.bean=bean;
		query.keys=keys;
		tasks.put(uuid,query);
		
		if(!syn) return;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param sql
	 * @param syn
	 * @return
	 */
	protected StmtAndRs query(String uuid,String sql,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_SQL);
		query.sql=sql;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (StmtAndRs)results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param sql
	 * @param rpp
	 * @param pn
	 * @param syn
	 * @return
	 */
	protected StmtAndRs query(String uuid,String sql,int rpp,int pn,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_SQL);
		query.rpp=rpp;
		query.pn=pn;
		query.sql=sql;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (StmtAndRs)results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param condition
	 * @param syn
	 * @return
	 */
	protected List query(String uuid,String table,String condition,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_TABLE_AND_CONDITION);
		query.tableNames=new String[]{table};
		query.condition=condition;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (List)results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param condition
	 * @param rpp
	 * @param pn
	 * @param syn
	 * @return
	 */
	protected List query(String uuid,String table,String condition,int rpp,int pn,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_TABLE_AND_CONDITION);
		query.tableNames=new String[]{table};
		query.condition=condition;
		query.rpp=rpp;
		query.pn=pn;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (List)results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param tables
	 * @param condition
	 * @param syn
	 * @return
	 */
	protected List query(String uuid,String[] tables,String condition,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_TABLE_AND_CONDITION);
		query.tableNames=tables;
		query.condition=condition;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (List)results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param tables
	 * @param condition
	 * @param rpp
	 * @param pn
	 * @param syn
	 * @return
	 */
	protected List query(String uuid,String[] tables,String condition,int rpp,int pn,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_TABLE_AND_CONDITION);
		query.tableNames=tables;
		query.rpp=rpp;
		query.pn=pn;
		query.condition=condition;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (List)results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param condition
	 * @param syn
	 * @return
	 */
	protected List querySingle(String uuid,String table,String condition,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_FIND_ONE_TBL_SINGLE);
		query.tableNames=new String[]{table};
		query.condition=condition;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (List)results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param tables
	 * @param condition
	 * @param rpp
	 * @param pn
	 * @param syn
	 * @return
	 */
	protected List querySingle(String uuid,String[] tables,String condition,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_FIND_MULTI_TBL_SINGLE);
		query.tableNames=tables;
		query.condition=condition;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (List)results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param sql
	 * @param syn
	 * @return
	 */
	protected Integer count(String uuid,String sql,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_COUNT);
		query.sql=sql;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (Integer)results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param condition
	 * @param syn
	 * @return
	 */
	protected Integer count(String uuid,String table,String condition,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_COUNT);
		query.tableNames=new String[]{table};
		query.condition=condition;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (Integer)results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param tables
	 * @param condition
	 * @param syn
	 * @return
	 */
	protected Integer count(String uuid,String[] tables,String condition,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_COUNT);
		query.tableNames=tables;
		query.condition=condition;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (Integer)results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param column
	 * @param condition
	 * @param syn
	 * @return
	 */
	protected String sum(String uuid,String table,String column,String condition,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_SUM);
		query.tableNames=new String[]{table};
		query.condition=condition;
		query.column=column;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (String)results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @param table
	 * @param columns
	 * @param condition
	 * @param syn
	 * @return
	 */
	protected String[] sum(String uuid,String table,String[] columns,String condition,boolean syn){
		if(tasks.containsKey(uuid)) return null;
		
		Query query=new Query(uuid,Query.TYPE_SUM);
		query.tableNames=new String[]{table};
		query.condition=condition;
		query.columns=columns;
		tasks.put(uuid,query);
		
		if(!syn) return null;
		
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return (String[])results.remove(uuid);
	}
	
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	protected Object result(String uuid){
		while(tasks.containsKey(uuid)){
			//try{
			//	Thread.sleep(100);
			//}catch(Exception e){}
		}
		return results.remove(uuid);
	}
	
	/**
	 * 
	 * @return
	 */
	protected DAO getDAO(){
		ensureDAO();
		return dao;
	}
	
	/**
	 * 
	 */
	protected void shutdown(){
		running=false;
		tasks.clear();
		results.clear();
		
		if(dao!=null){
			try{
				dao.close();
				dao=null;
			}catch(Exception e){}
		}
	}
	
	/**
	 * 
	 */
	private void ensureDAO(){
		try{
			if(dao==null||dao.isClosed()){
				if(this.databaseName==null){
					dao=DB.connect(SysConfig.databaseName, this.getClass(),-1);
				}else{
					dao=DB.connect(this.databaseName, this.getClass(),-1);
				}
			}
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			if(dao!=null){
				try{
					dao.close();
					dao=null;
				}catch(Exception ex){}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(running){
			List temp=tasks.listValues();
			
			if(temp.isEmpty()){
				try{
					Thread.sleep(100);
				}catch(Exception e){}
				continue;
			}

			while(!temp.isEmpty()){
				ensureDAO();
				
				Query query=(Query)temp.get(0);
				try{
					if(query.type==Query.TYPE_SQL_NO_RESULT){
						dao.executeSQL(query.sql);
						//results.put(query.uuid, "");
					}else if(query.type==Query.TYPE_SQL){
						if(query.rpp>0&&query.pn>0){
							StmtAndRs sr=dao.find(query.sql,query.rpp,query.pn);
							results.put(query.uuid, sr);
						}else{
							StmtAndRs sr=dao.find(query.sql);
							results.put(query.uuid, sr);
						}
					}else if(query.type==Query.TYPE_TABLE_AND_CONDITION){
						if(query.tableNames.length==1){
							if(query.rpp>0&&query.pn>0){
								List result=dao.find(query.tableNames[0],query.condition,query.rpp,query.pn);
								results.put(query.uuid, result);
							}else{
								List result=dao.find(query.tableNames[0],query.condition);
								results.put(query.uuid, result);
							}
						}else{
							if(query.rpp>0&&query.pn>0){
								List result=dao.find(query.tableNames,query.condition,query.rpp,query.pn);
								results.put(query.uuid, result);
							}else{
								List result=dao.find(query.tableNames,query.condition);
								results.put(query.uuid, result);
							}
						}
					}else if(query.type==Query.TYPE_COUNT){
						int count=0;
						if(query.sql!=null){
							count=dao.getRecordCnt(query.sql);
						}else{
							if(query.tableNames.length==1){
								count=dao.getRecordCnt(query.tableNames[0],query.condition);
							}else{
								count=dao.getRecordCnt(query.tableNames,query.condition);
							}
						}
						results.put(query.uuid, new Integer(count));
					}else if(query.type==Query.TYPE_SUM){
						if(query.columns!=null) {
							String[] sum=dao.getSum(query.tableNames[0],query.columns,query.condition);
							results.put(query.uuid, sum);
						}else {
							String sum=dao.getSum(query.tableNames[0],query.column,query.condition);
							results.put(query.uuid, sum);
						}
					}else if(query.type==Query.TYPE_INSERT){
						dao.insert(query.bean);
						//results.put(query.uuid, "");
					}else if(query.type==Query.TYPE_INSERT_IF_NOT_EXISTS){
						dao.insertIfNotExists(query.bean);
						//results.put(query.uuid, "");
					}else if(query.type==Query.TYPE_UPDATE){
						if(query.keys!=null){
							dao.updateByKeys(query.bean,query.keys);
						}else{
							dao.updateByKeys(query.bean);
						}
						//results.put(query.uuid, "");
					}else if(query.type==Query.TYPE_UPDATE_IGNORE_NULLS){
						if(query.keys!=null){
							dao.updateByKeysIgnoreNulls(query.bean,query.keys);
						}else{
							dao.updateByKeysIgnoreNulls(query.bean);
						}
						//results.put(query.uuid, "");
					}else if(query.type==Query.TYPE_FIND_ONE_TBL_SINGLE){
						Object result=dao.findSingle(query.tableNames[0],query.condition);
						results.put(query.uuid, result);
					}else if(query.type==Query.TYPE_FIND_MULTI_TBL_SINGLE){
						Object result=dao.findSingle(query.tableNames,query.condition);
						results.put(query.uuid, result);
					}
					
					temp.remove(0);
					tasks.remove(query.uuid);
					query=null;
				}catch(Exception e){
					try{
						dao.close();
					}catch(Exception ex){};
					try{
						dao=null;
					}catch(Exception ex){};
					log.log("errors occur in pool "+poolName+", executor "+executorName+","+query.type+","+query.sql+","+query.bean+":\r\n", Logger.LEVEL_ERROR);
					log.log(e, Logger.LEVEL_ERROR);
					
					query.tries++;
					//if(query.tries>1){
						temp.remove(0);
						tasks.remove(query.uuid);
						query=null;
					//}
				}
			}
			temp=null;
		}
	}
}
