package j.dao;

import j.Properties;
import j.app.sso.Client;
import j.app.sso.SSOConfig;
import j.http.JHttp;
import j.http.JHttpContext;
import j.log.Logger;
import j.service.Constants;
import j.sys.SysConfig;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilMath;
import j.util.JUtilString;

import java.io.File;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 
 * @author 肖炯
 *
 */
public class DB implements Runnable {
	private static Logger log=Logger.create(DB.class);
	private static Document config=null;
	private static ConcurrentMap databases=new ConcurrentMap();
	private static long configLastModified=0;//配置文件上次修改时间
	private static volatile boolean loading=true;
	private static final Object lock=new Object();
	
	private static JHttp jhttp=JHttp.getInstance();
	private static HttpClient jclient=jhttp.createClient();
	public static volatile boolean isCluster=false;
	
	static{
		if(j.Properties.getProperty("org.sqlite.lib.path")!=null){
			System.setProperty("org.sqlite.lib.path",j.Properties.getProperty("org.sqlite.lib.path"));
		}
		
		load();
		
		DB m=new DB();
		Thread thread=new Thread(m);
		thread.start();
		log.log("DB monitor thread started.",-1);
	}

	/**
	 * 
	 *
	 */
	private DB(){
		super();
	}
	
	/**
	 * 
	 * @param dbId
	 * @return
	 * @throws Exception
	 */
	public static DAO connect(String dbId,Class caller) throws Exception{
		return connect(dbId,caller,false);
	}
	
	/**
	 * 
	 * @param dbId
	 * @param caller
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static DAO connect(String dbId,Class caller,long timeout) throws Exception{
		return connect(dbId,caller,timeout,false);
	}
	
	/**
	 * 
	 * @param dbname
	 * @param caller
	 * @param readonly
	 * @return
	 * @throws Exception
	 */
	public static DAO connect(String dbId,Class caller,boolean readonly) throws Exception{
		waitWhileLoading();
		Database db=DB.database(dbId);
		if(db==null){
			throw new Exception("database "+dbId+" is not exists.");
		}
		DAO dao=db.connect(caller);
		dao.setReadOnly(readonly);
		return dao;
	}
	
	/**
	 * 
	 * @param dbId
	 * @param caller
	 * @param timeout
	 * @param readonly
	 * @return
	 * @throws Exception
	 */
	public static DAO connect(String dbId,Class caller,long timeout,boolean readonly) throws Exception{
		waitWhileLoading();
		Database db=DB.database(dbId);
		if(db==null){
			throw new Exception("database "+dbId+" is not exists.");
		}
		DAO dao=db.connect(caller,timeout);
		dao.setReadOnly(readonly);
		return dao;
	}
	
	/**
	 * 
	 * @param dbId
	 * @return
	 */
	public static Database database(String dbId){
		waitWhileLoading();
		return (Database)databases.get(dbId);
	}
	
	/**
	 * 
	 * @return
	 */
	public static List getDatabases(){
		waitWhileLoading();
		return databases.listValues();
	}
	
	/**
	 * 
	 * @param dbname
	 * @param mirrorUuid
	 * @param avail
	 * @throws Exception
	 */
	public static void setAvail(String dbname,String mirrorUuid,boolean avail) throws Exception{
		Database db=DB.database(dbname);
		DBMirror mirror=db.mirror(mirrorUuid);

    	Client client=SSOConfig.getSsoClientByIdOrUrl(SysConfig.getSysId());
    	String url=mirror.monitor;
    	if(url==null||url.equals("")){
    		url=client.getUrlDefault()+"DataSource.service";
    	}else if(!url.matches(JUtilString.RegExpHttpUrl)){
    		if(url.startsWith("/")){
    			url=client.getUrlDefault()+url.substring(1);
    		}else{
    			url=client.getUrlDefault()+url;
    		}
    	}
    	
		if(url.indexOf("?")>0) url+="&request=setAvail";
		else url+="?request=setAvail";	
	
		url+="&db_name="+dbname;
		url+="&mirror_uuid="+mirrorUuid;
		url+="&avail="+avail;
		
		//System.out.println(url);
		
		JHttpContext context=jhttp.get(null,jclient,url,SysConfig.sysEncoding);
		if(context==null
				||context.getStatus()!=200
				||Constants.AUTH_FAILED.equals(context.getResponseText())
				||Constants.INVOKING_FAILED.equals(context.getResponseText())){
			if(context!=null){							
				context.finalize();
				context=null;
			}
			throw new Exception("failed to setAvail of - "+dbname+","+mirrorUuid+","+avail+" on "+url);
		}
		
		context.finalize();
		context=null;
	}
	

	/**
	 * 
	 * @param dbname
	 * @param mirrorUuid
	 * @param readable
	 * @throws Exception
	 */
	public static void setReadable(String dbname,String mirrorUuid,boolean readable) throws Exception{
		Database db=DB.database(dbname);
		DBMirror mirror=db.mirror(mirrorUuid);
		
		String url=mirror.monitor;
		if(url.indexOf("?")>0) url+="&request=setReadable";
		else url+="?request=setReadable";	
	
		url+="&db_name="+dbname;
		url+="&mirror_uuid="+mirrorUuid;
		url+="&readable="+readable;
		
		//System.out.println(url);
		
		JHttpContext context=jhttp.get(null,jclient,url,SysConfig.sysEncoding);
		if(context==null
				||context.getStatus()!=200
				||Constants.AUTH_FAILED.equals(context.getResponseText())
				||Constants.INVOKING_FAILED.equals(context.getResponseText())){
			if(context!=null){							
				context.finalize();
				context=null;
			}
			throw new Exception("failed to setReadable of - "+dbname+","+mirrorUuid+","+readable+" on "+url);
		}
		
		context.finalize();
		context=null;
	}
	

	/**
	 * 
	 * @param dbname
	 * @param mirrorUuid
	 * @param insertable
	 * @throws Exception
	 */
	public static void setInsertable(String dbname,String mirrorUuid,boolean insertable) throws Exception{
		Database db=DB.database(dbname);
		DBMirror mirror=db.mirror(mirrorUuid);
		
		String url=mirror.monitor;
		if(url.indexOf("?")>0) url+="&request=setInsertable";
		else url+="?request=setInsertable";	
	
		url+="&db_name="+dbname;
		url+="&mirror_uuid="+mirrorUuid;
		url+="&insertable="+insertable;
		
		//System.out.println(url);
		
		JHttpContext context=jhttp.get(null,jclient,url,SysConfig.sysEncoding);
		if(context==null
				||context.getStatus()!=200
				||Constants.AUTH_FAILED.equals(context.getResponseText())
				||Constants.INVOKING_FAILED.equals(context.getResponseText())){
			if(context!=null){							
				context.finalize();
				context=null;
			}
			throw new Exception("failed to setInsertable of - "+dbname+","+mirrorUuid+","+insertable+" on "+url);
		}
		
		context.finalize();
		context=null;
	}
	

	/**
	 * 
	 * @param dbname
	 * @param mirrorUuid
	 * @param updatable
	 * @throws Exception
	 */
	public static void setUpdatable(String dbname,String mirrorUuid,boolean updatable) throws Exception{
		Database db=DB.database(dbname);
		DBMirror mirror=db.mirror(mirrorUuid);
		
		String url=mirror.monitor;
		if(url.indexOf("?")>0) url+="&request=setUpdatable";
		else url+="?request=setUpdatable";	
	
		url+="&db_name="+dbname;
		url+="&mirror_uuid="+mirrorUuid;
		url+="&updatable="+updatable;
		
		//System.out.println(url);
		
		JHttpContext context=jhttp.get(null,jclient,url,SysConfig.sysEncoding);
		if(context==null
				||context.getStatus()!=200
				||Constants.AUTH_FAILED.equals(context.getResponseText())
				||Constants.INVOKING_FAILED.equals(context.getResponseText())){
			if(context!=null){							
				context.finalize();
				context=null;
			}
			throw new Exception("failed to setUpdatable of - "+dbname+","+mirrorUuid+","+updatable+" on "+url);
		}
		
		context.finalize();
		context=null;
	}
	
	
	/**
	 * 
	 *
	 */
	public static void load(){
		try{
			loading=true;
			
			List dbs=databases.listValues();
			for(int i=0;i<dbs.size();i++){
				Database db=(Database)dbs.get(i);
				db.shutdown();
			}
			databases.clear();
			
			
			//文件是否存在
			File file = new File(Properties.getConfigPath()+"JDAO.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			
			config=JUtilDom4j.parse(Properties.getConfigPath()+"JDAO.xml","UTF-8");
			Element root=config.getRootElement();
			
			DB.isCluster="true".equalsIgnoreCase(root.elementTextTrim("is-cluster"));
			
			List dbEles=root.elements("database");
			for(int i=0;i<dbEles.size();i++){
				Element dbEle=(Element)dbEles.get(i);
				
				String id=dbEle.attributeValue("id");
				if(id==null||"".equals(id)){
					id=dbEle.attributeValue("name");
				}
				
				Database db=new Database(id,
						dbEle.attributeValue("name"),
						dbEle.attributeValue("desc"),
						dbEle.attributeValue("TEST-SQL"));
								
				List mirrorEles=dbEle.elements("mirror");
				for(int j=0;j<mirrorEles.size();j++){
					Element mirrorEle=(Element)mirrorEles.get(j);
					DBMirror m=new DBMirror(db,mirrorEle.attributeValue("config"),mirrorEle);
					
					m.isMonitor="true".equalsIgnoreCase(mirrorEle.attributeValue("is-monitor"));
					m.monitor=mirrorEle.attributeValue("monitor");
					m.uuid=mirrorEle.attributeValue("uuid");

					m.avail=!"false".equalsIgnoreCase(mirrorEle.attributeValue("avail"));
					m.readable=!"false".equalsIgnoreCase(mirrorEle.attributeValue("read"));
					m.insertable=!"false".equalsIgnoreCase(mirrorEle.attributeValue("insert"));
					m.updatable=!"false".equalsIgnoreCase(mirrorEle.attributeValue("update"));
				
					if(JUtilMath.isInt(mirrorEle.attributeValue("priority"))){
						m.priority=Integer.parseInt(mirrorEle.attributeValue("priority"));
					}
					db.addMirror(m); 
				}
				
				List colEles=dbEle.elements("ignoreColWhileUpdateViaBean");
				for(int j=0;j<colEles.size();j++){
					Element colEle=(Element)colEles.get(j);
					db.ignoreColWhileUpdateViaBean(colEle.getText());
				}
				
				List metaEles=dbEle.elements("meta");
				for(int j=0;j<metaEles.size();j++){
					Element metaEle=(Element)metaEles.get(j);
					db.setMetaTable(metaEle.attributeValue("selector"),metaEle.getTextTrim());
				}
				
				databases.put(db.id,db);
				db.monitor();
				
				Thread thread=new Thread(db);
				thread.start();
				log.log("db "+db.id+","+db.name+","+db.desc+" started!",-1);
			}

			//配置文件最近修改时间
			File configFile=new File(Properties.getConfigPath()+"JDAO.xml");
			configLastModified=configFile.lastModified();
			configFile=null;
			
			loading=false;
		}catch(Exception e){
			loading=false;
			log.log(e,Logger.LEVEL_FATAL);
		}
	}
	
	/**
	 * 此处保存文件不应该导致自动重新加载
	 *
	 */
	public static void save(){
		synchronized(lock){
			configLastModified=0;//停止自动更新文件
			if(config!=null){
				try{
					JUtilDom4j.save(config,Properties.getConfigPath()+"JDAO.xml","UTF-8");
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_FATAL);
				}
			}
			File configFile=new File(Properties.getConfigPath()+"JDAO.xml");
			configLastModified=configFile.lastModified();//记录最新更新时间
			configFile=null;
		}
	}
	
	/**
	 * 
	 *
	 */
	private static void waitWhileLoading(){
		while(loading){
			try{
				Thread.sleep(100);
			}catch(Exception ex){}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * 检测JDAO.xml是否修改过，如修改过重新加载配置
		 */
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			if(configLastModified<=0) continue;

			File configFile=new File(Properties.getConfigPath()+"JDAO.xml");
			if(configLastModified<configFile.lastModified()){
				log.log("JDAO.xml has been modified, so reload it.",-1);
				load();
			}
			configFile=null;
		}
	}
	
	
	public static final String sqliteSynchronousFull="FULL";
	public static final String sqliteSynchronousNormal="NORMAL";
	public static final String sqliteSynchronousOff="OFF";
	public static void sqliteSetSynchronous(DAO dao,String synchronous) throws Exception{
		if(sqliteSynchronousFull.equalsIgnoreCase(synchronous)){
			dao.executeSQL("PRAGMA synchronous = FULL");
		}else if(sqliteSynchronousNormal.equalsIgnoreCase(synchronous)){
			dao.executeSQL("PRAGMA synchronous = NORMAL");
		}else if(sqliteSynchronousOff.equalsIgnoreCase(synchronous)){
			dao.executeSQL("PRAGMA synchronous = OFF");
		}
	}
}