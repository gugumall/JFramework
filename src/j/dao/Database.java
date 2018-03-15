package j.dao;

import j.log.Logger;
import j.util.ConcurrentList;
import j.util.JUtilRandom;
import j.util.JUtilString;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author 肖炯
 *
 */
public class Database implements Runnable{
	private static Logger log=Logger.create(Database.class);
	public String id;
	public String name;
	public String desc;
	public String testSql;
	public ConcurrentList mirrors;
	public ConcurrentList availables;
	public ConcurrentList availableUuids;
	public ConcurrentList ignoreColsWhileUpdateViaBean;
	public ConcurrentList metaOfTable;
	public volatile boolean shutdown=false;
	
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param desc
	 * @param testSql
	 */
	Database(String id,String name,String desc,String testSql){
		this.id=id;
		this.name=name;
		this.desc=desc;
		this.testSql=testSql;
		mirrors=new ConcurrentList();
		availables=new ConcurrentList();
		availableUuids=new ConcurrentList();
		ignoreColsWhileUpdateViaBean=new ConcurrentList();
		metaOfTable=new ConcurrentList();
	}
	
	/**
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	DAO connect(Class clazz) throws Exception{
		DBMirror mirror=null;
		synchronized(this){
			mirror=select();
		}
		if(mirror==null){
			throw new Exception("no mirror avail.");
		}
		return mirror.connect(clazz);
	}
	
	/**
	 * 
	 * @param clazz
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	DAO connect(Class clazz,long timeout) throws Exception{
		DBMirror mirror=null;
		synchronized(this){
			mirror=select();
		}
		if(mirror==null){
			throw new Exception("no mirror avail.");
		}
		return mirror.connect(clazz,timeout);
	}
	
	/**
	 * 
	 * @return
	 */
	DBMirror select(){
		if(availables.size()==0){
			return null;
		}
		int maxPriority=0;
		int maxPriorityIndex=0;
		boolean allEqual=true;
		for(int i=0;i<availables.size();i++){
			DBMirror mirror=(DBMirror)availables.get(i);
			if(mirror.priority>maxPriority){
				maxPriorityIndex=i;
				maxPriority=mirror.priority;
				if(i>0) allEqual=false;
			}
		}
		
		if(allEqual){//如果全部镜像的优先级都相同，随机选取
			return (DBMirror)availables.get(JUtilRandom.nextInt(availables.size()));
		}else{//选用优先级最高的
			return (DBMirror)availables.get(maxPriorityIndex);
		}
	}
	
	/**
	 * 
	 * @param mirror
	 */
	void addMirror(DBMirror mirror){
		mirrors.add(mirror);
	}
	
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public DBMirror mirror(String uuid){
		for(int i=0;i<mirrors.size();i++){
			DBMirror mirror=(DBMirror)mirrors.get(i);
			if(mirror.uuid.equalsIgnoreCase(uuid)) return mirror;
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public List getMirros(){
		return this.mirrors;
	}
	
	/**
	 * 
	 * @param mirrorUuid
	 * @return
	 */
	public boolean isAvail(String mirrorUuid){
		return availableUuids.contains(mirrorUuid);
	}
	
	/**
	 * 
	 * @param colInfo
	 */
	void ignoreColWhileUpdateViaBean(String colInfo){
		ignoreColsWhileUpdateViaBean.add(colInfo);
	}
	
	/**
	 * 
	 * @return
	 */
	ConcurrentList getIgnoreColsWhileUpdateViaBean(){
		return ignoreColsWhileUpdateViaBean;
	}
	
	/**
	 * 
	 * @param selector
	 * @param metaTableName
	 */
	void setMetaTable(String selector,String metaTableName){
		metaOfTable.add(new String[]{selector,metaTableName});
	}
	
	/**
	 * 
	 * @param tableName
	 * @return
	 */
	String getMetaTable(String tableName){
		for(int i=0;i<metaOfTable.size();i++){
			String[] a=(String[])metaOfTable.get(i);
			if(a[0].equalsIgnoreCase(tableName)) return a[1];
		}
		
		for(int i=0;i<metaOfTable.size();i++){
			String[] a=(String[])metaOfTable.get(i);
			if(a[0].indexOf("*")>-1&&JUtilString.matchIgnoreCase(tableName,a[0],"*")>-1) return a[1];
		}
		
		return tableName;
	}
	
	
	/**
	 * 
	 *
	 */
	void shutdown(){
		shutdown=true;
		
		for(int i=0;i<mirrors.size();i++){
			DBMirror mirror=(DBMirror)mirrors.get(i);
			mirror.shutdown();
		}
	}
	
	/**
	 * 
	 *
	 */
	void monitor(){
		try{
			List temp=new LinkedList();
			List tempUuids=new LinkedList();
			for(int i=0;i<mirrors.size();i++){
				DBMirror mirror=(DBMirror)mirrors.get(i);

				boolean avail=mirror.available();
				if(avail){
					temp.add(mirror);
					tempUuids.add(mirror.uuid);
				}else if(mirror.isMonitor&&mirror.avail){
					mirror.avail=false;
					mirror.relatedXmlElement.attribute("avail").setValue("false");
					DB.save();
				}
			}
			
			synchronized(this){
				availables.clear();
				availables.addAll(temp);
				
				availableUuids.clear();
				availableUuids.addAll(tempUuids);
			}
			
			temp.clear();
			temp=null;
			
			tempUuids.clear();
			tempUuids=null;
		}catch(Exception e){
			log.log(e, Logger.LEVEL_INFO);
		}
	}

	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(!shutdown){
			monitor();
			
			try{
				Thread.sleep(3000);
			}catch(Exception e){}
		}
	}
}
