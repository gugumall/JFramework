package j.dao;

import j.http.JHttp;
import j.http.JHttpContext;
import j.log.Logger;
import j.sys.SysConfig;
import j.util.ConcurrentList;
import j.util.JUtilString;

import org.apache.http.client.HttpClient;
import org.dom4j.Element;


/**
 * 
 * @author 肖炯
 *
 */
public class DBMirror{
	private static Logger log=Logger.create(DBMirror.class);
	public static final int STATUS_AVAILABLE=1;
	public static final int STATUS_UNAVAILABLE=0;
	public Element relatedXmlElement;
	public DAOFactory factory;
	public Database db;
	
	public String uuid;
	public String dbname;
	public String config;	
	public String monitor;

	public boolean isMonitor=false;
	public boolean avail=true;
	public boolean readable=true;
	public boolean insertable=true;
	public boolean updatable=true;
	public int priority=0;
	
	public volatile boolean shutdown=false;
	public volatile int status=0;
	
	public JHttp jhttp=JHttp.getInstance();
	public HttpClient jclient=jhttp.createClient(30000);
	
	/**
	 * 
	 * @param db
	 * @param config
	 * @param relatedXmlElement
	 */
	DBMirror(Database db,String config,Element relatedXmlElement){
		this.db=db;
		this.dbname=db.name;
		this.config=config;
		this.relatedXmlElement=relatedXmlElement;
	}
	
	/**
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	DAO connect(Class clazz) throws Exception{		
		synchronized(this){
			if(factory==null){
				factory=DAOFactory.getInstance(dbname,config);
				factory.resetIgnoreColsWhileUpdating();

				ConcurrentList cols=db.getIgnoreColsWhileUpdateViaBean();
				for(int i=0;i<cols.size();i++){
					String col=(String)cols.get(i);
					factory.ignoreColWhileUpdating(col.substring(0,col.indexOf(",")).trim(),
							col.substring(col.indexOf(",")+1).trim());
				}
			}
		}
		
		return factory.createDAO(clazz,this);
	}
	
	/**
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	DAO connect(Class clazz,long timeout) throws Exception{		
		synchronized(this){
			if(factory==null){
				factory=DAOFactory.getInstance(dbname,config);
				factory.resetIgnoreColsWhileUpdating();

				ConcurrentList cols=db.getIgnoreColsWhileUpdateViaBean();
				for(int i=0;i<cols.size();i++){
					String col=(String)cols.get(i);
					factory.ignoreColWhileUpdating(col.substring(0,col.indexOf(",")).trim(),
							col.substring(col.indexOf(",")+1).trim());
				}
			}
		}
		
		return factory.createDAO(clazz,timeout,this);
	}
	
	/**
	 * 
	 * @return
	 */
	boolean available(){
		if(shutdown){
			status=DBMirror.STATUS_UNAVAILABLE;
			return false;
		}

		if(!avail){
			status=DBMirror.STATUS_UNAVAILABLE;
			return false;
		}
		
		if(!isMonitor&&(monitor!=null&&monitor.matches(JUtilString.RegExpHttpUrl))){
			synchronized(this){
				String url=monitor;
				if(url.indexOf("?")>0) url+="&request=isAvail";
				else url+="?request=isAvail";	
			
				url+="&db_name="+dbname;
				url+="&mirror_uuid="+uuid;
				
				JHttpContext context=null;
				int loop=0;
				while((context==null||context.getStatus()!=200)&&loop<10){
					try{
						context=jhttp.get(null,jclient,url,SysConfig.sysEncoding);
						loop++;
					}catch(Exception e){
						log.log(e,Logger.LEVEL_ERROR);
						try{
							Thread.sleep(6000);
						}catch(Exception ex){}
					}
				}
				String result=context==null?"false":context.getResponseText();
				
				//log.log("status from monitor - "+url+","+result, Logger.LEVEL_INFO);
				
				context.finalize();
				context=null;
				
				if("true".equalsIgnoreCase(result)){
					status=DBMirror.STATUS_AVAILABLE;
				}else{
					status=DBMirror.STATUS_UNAVAILABLE;
				}
				
				return "true".equalsIgnoreCase(result);
			}
		}else{			
			synchronized(this){
				if(factory==null){
					try{
						factory=DAOFactory.getInstance(dbname,config);
						factory.resetIgnoreColsWhileUpdating();
				
						ConcurrentList cols=db.getIgnoreColsWhileUpdateViaBean();
						for(int i=0;i<cols.size();i++){
							String col=(String)cols.get(i);
							factory.ignoreColWhileUpdating(col.substring(0,col.indexOf(",")).trim(),
									col.substring(col.indexOf(",")+1).trim());
						}
					}catch(Exception e){
						log.log(e,Logger.LEVEL_ERROR);
						return false;
					}
				}
			}
		}

		DAO dao=null;
		try{
			dao=factory.createDAO(DBMirror.class,this);
			dao.close();
			
			status=DBMirror.STATUS_AVAILABLE;
			
			return true;
		}catch(Exception e){
			status=DBMirror.STATUS_UNAVAILABLE;
			log.log(e,Logger.LEVEL_INFO);
			if(dao!=null){
				try{
					dao.close();
					dao=null;
				}catch(Exception ex){}
			}
			return false;
		}
	}
	
	/**
	 * 
	 *
	 */
	void shutdown(){
		shutdown=true;
		if(factory!=null){
			try{
				factory.finalize();
				factory=null;
			}catch(Exception e){}
		}
	}
}