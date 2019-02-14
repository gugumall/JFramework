package j.fs;

import j.common.JObject;
import j.dao.DAO;
import j.dao.DB;
import j.db.JfsTask;
import j.http.JHttp;
import j.log.Logger;
import j.service.Manager;
import j.service.client.Client;
import j.service.server.ServiceConfig;
import j.sys.SysConfig;
import j.util.ConcurrentMap;
import j.util.JUtilInputStream;
import j.util.JUtilList;
import j.util.JUtilString;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;

/**
 * 
 * @author 肖炯
 *
 */
public class JDFSSynchronizer implements Runnable{	
	private static Logger log=Logger.create(JDFSSynchronizer.class);
	private static ConcurrentMap threads=new ConcurrentMap();
	private ServiceConfig config=null;
	private JHttp jhttp=null;
	private HttpClient jclient=null;
	private JDFSFile target=null;
	
	/**
	 * 
	 * @param config
	 */
	public JDFSSynchronizer(ServiceConfig config) {
		super();
		this.config=config;
		
		jhttp=JHttp.getInstance();
		jclient=jhttp.createClient();
	}
	
	/**
	 * 
	 * @param toUuid
	 * @return
	 */
	public static boolean exists(String toUuid){
		return threads.containsKey(toUuid);
	}
	
	/**
	 * 
	 * @param config
	 */
	public static void create(ServiceConfig config){
		if(exists(config.getUuid())) return;
		
		JDFSSynchronizer synchronizer=new JDFSSynchronizer(config);
		Thread thread=new Thread(synchronizer);
		thread.start();
		
		threads.put(config.getUuid(),synchronizer);
		
		log.log("JDFSSynchronizer "+config.getCode()+","+config.getUuid()+" started.",-1);
	}
	
	/**
	 * 
	 * @param httpChannel
	 * @param service
	 * @param path
	 * @throws Exception
	 */
	public void delete(String httpChannel,JDFSService service,String path) throws Exception{
		String serviceCode=config.getCode();
		if(service!=null){
			service.delete(Manager.getClientNodeUuid(),Client.md54Service(serviceCode,"delete"),path);
		}else{						
			Map params=new HashMap();
			params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
			Client.httpCallGet(null,jclient,serviceCode,httpChannel,"delete",params);
			params.clear();
			params=null;			
		}
	}
	
	/**
	 * 
	 * @param httpChannel
	 * @param service
	 * @param path
	 * @throws Exception
	 */
	public void mkdir(String httpChannel,JDFSService service,String path) throws Exception{
		String serviceCode=config.getCode();
		if(service!=null){
			service.delete(Manager.getClientNodeUuid(),Client.md54Service(serviceCode,"mkdir"),path);
		}else{		
			Map params=new HashMap();
			params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
			Client.httpCallGet(null,jclient,serviceCode,httpChannel,"mkdir",params);
			params.clear();
			params=null;
		}
	}
	
	/**
	 * 
	 * @param httpChannel
	 * @param service
	 * @param path
	 * @throws Exception
	 */
	public void mkdirs(String httpChannel,JDFSService service,String path) throws Exception{
		String serviceCode=config.getCode();
		if(service!=null){
			service.delete(Manager.getClientNodeUuid(),Client.md54Service(serviceCode,"mkdirs"),path);
		}else{
			Map params=new HashMap();
			params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
			Client.httpCallGet(null,jclient,serviceCode,httpChannel,"mkdirs",params);
			params.clear();
			params=null;			
		}
	}
	
	/**
	 * 
	 * @param httpChannel
	 * @param service
	 * @param path
	 * @param dest
	 * @throws Exception
	 */
	public void renameTo(String httpChannel,JDFSService service,String path,String dest) throws Exception{
		String serviceCode=config.getCode();
		if(service!=null){
			service.renameTo(Manager.getClientNodeUuid(),
					Client.md54Service(serviceCode,"renameTo"),
					path,
					dest);
		}else{
			Map params=new HashMap();
			params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
			params.put("dest",JUtilString.encodeURI(dest,SysConfig.sysEncoding));
			Client.httpCallGet(null,jclient,serviceCode,httpChannel,"renameTo",params);
			params.clear();
			params=null;	
		}
	}
	
	/**
	 * 
	 * @param httpChannel
	 * @param service
	 * @param path
	 * @param executable
	 * @param ownerOnly
	 * @return
	 * @throws Exception
	 */
	public void setExecutable(String httpChannel,JDFSService service,String path,boolean executable,boolean ownerOnly) throws Exception{
		String serviceCode=config.getCode();
		if(service!=null){
			service.setExecutable(Manager.getClientNodeUuid(),
					Client.md54Service(serviceCode,"setExecutable"),
					path,
					executable,
					ownerOnly);
		}else{
			Map params=new HashMap();
			params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
			params.put("executable",executable+"");
			params.put("ownerOnly",ownerOnly+"");
			Client.httpCallGet(null,jclient,serviceCode,httpChannel,"setExecutable",params);
			params.clear();
			params=null;
		}
	}
	
	/**
	 * 
	 * @param httpChannel
	 * @param service
	 * @param path
	 * @param time
	 * @throws Exception
	 */
	public void setLastModified(String httpChannel,JDFSService service,String path,long time) throws Exception{
		String serviceCode=config.getCode();
		if(service!=null){
			service.setLastModified(Manager.getClientNodeUuid(),
					Client.md54Service(serviceCode,"setLastModified"),
					path,
					time);
		}else{
			Map params=new HashMap();
			params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
			params.put("time",time+"");
			Client.httpCallGet(null,jclient,serviceCode,httpChannel,"setLastModified",params);
			params.clear();
			params=null;
		}
	}
	
	/**
	 * 
	 * @param httpChannel
	 * @param service
	 * @param path
	 * @param readable
	 * @param ownerOnly
	 * @throws Exception
	 */
	public void setReadable(String httpChannel,JDFSService service,String path,boolean readable,boolean ownerOnly) throws Exception{
		String serviceCode=config.getCode();
		if(service!=null){
			service.setExecutable(Manager.getClientNodeUuid(),
					Client.md54Service(serviceCode,"setReadable"),
					path,
					readable,
					ownerOnly);
		}else{
			Map params=new HashMap();
			params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
			params.put("readable",readable+"");
			params.put("ownerOnly",ownerOnly+"");
			Client.httpCallGet(null,jclient,serviceCode,httpChannel,"setReadable",params);
			params.clear();
			params=null;
		}
	}
	
	/**
	 * 
	 * @param httpChannel
	 * @param service
	 * @param path
	 * @throws Exception
	 */
	public void setReadOnly(String httpChannel,JDFSService service,String path) throws Exception{
		String serviceCode=config.getCode();
		if(service!=null){
			service.setReadOnly(Manager.getClientNodeUuid(),
					Client.md54Service(serviceCode,"setReadOnly"),
					path);
		}else{
			Map params=new HashMap();
			params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
			Client.httpCallGet(null,jclient,serviceCode,httpChannel,"setReadOnly",params);
			params.clear();
			params=null;
		}
	}
	
	/**
	 * 
	 * @param httpChannel
	 * @param service
	 * @param path
	 * @param writable
	 * @param ownerOnly
	 * @throws Exception
	 */
	public void setWritable(String httpChannel,JDFSService service,String path,boolean writable,boolean ownerOnly) throws Exception{
		String serviceCode=config.getCode();
		if(service!=null){
			service.setWritable(Manager.getClientNodeUuid(),
					Client.md54Service(serviceCode,"setWritable"),
					path,
					writable,
					ownerOnly);
		}else{
			Map params=new HashMap();
			params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
			params.put("writable",writable+"");
			params.put("ownerOnly",ownerOnly+"");
			Client.httpCallGet(null,jclient,serviceCode,httpChannel,"setWritable",params);
			params.clear();
			params=null;
		}
	}
	
	/**
	 * 
	 * @param httpChannel
	 * @param service
	 * @param path
	 * @param content
	 * @param append
	 * @param encoding
	 * @throws Exception
	 */
	public void saveString(String httpChannel,JDFSService service,String path,String content,boolean append,String encoding) throws Exception{
		String serviceCode=config.getCode();
		if(service!=null){			
			service.saveString(Manager.getClientNodeUuid(),
					Client.md54Service(serviceCode,"saveString"),
					path,
					content,
					append,
					encoding);
		}else{
			Map params=new HashMap();
			params.put("path",path);
			params.put("content",JObject.string2IntSequence(content));
			params.put("append",append+"");
			if(encoding!=null) params.put("encoding",encoding);
			Client.httpCallPost(null,jclient,serviceCode,httpChannel,"saveString",params);
			params.clear();
			params=null;
		}
	}
	
	/**
	 * 
	 * @param httpChannel
	 * @param service
	 * @param path
	 * @param bytes
	 * @throws Exception
	 */
	public void saveBytes(String httpChannel,JDFSService service,String path,byte[] bytes) throws Exception{
		String serviceCode=config.getCode();
		if(service!=null){			
			service.saveBytes(Manager.getClientNodeUuid(),
					Client.md54Service(serviceCode,"saveBytes"),
					path,
					bytes);
		}else{
			Map params=new HashMap();
			params.put("path",path);
			params.put("bytes",JObject.serializable2String(bytes));
			Client.httpCallPost(null,jclient,serviceCode,httpChannel,"saveBytes",params);
			params.clear();
			params=null;
		}
	}

	/**
	 * 
	 * @param httpChannel
	 * @param service
	 * @param path
	 * @param file
	 * @throws Exception
	 */
	public void saveFile(String httpChannel,JDFSService service,String path,File file) throws Exception {
		String serviceCode=config.getCode();
		if(service!=null){			
			service.saveBytes(Manager.getClientNodeUuid(),
					Client.md54Service(serviceCode,"saveBytes"),
					path,
					JUtilInputStream.bytes(new FileInputStream(file)));
		}else{
			Map params=new HashMap();
			params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));

			Map parts=new HashMap();
			parts.put("bytes",file);

			Client.httpCallMultiPart(null,jclient,serviceCode,httpChannel,"saveFile",params,parts);
			
			params.clear();
			params.clear();
			
			parts.clear();
			parts=null;	
		}
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try{
			Thread.sleep(30000);
		}catch(Exception e){}
		
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}

			String httpChannel=null;
			JDFSService service=null;
			try{
				if("http".equalsIgnoreCase(JDFS.getServiceChannel())){
					try{
						httpChannel=Client.httpGetService(jhttp,jclient,config.getUuid());
					}catch(Exception ex){
						log.log(ex,Logger.LEVEL_WARNING);
						service=(JDFSService)Client.rmiGetService(config.getUuid());
					}
				}else{
					try{
						service=(JDFSService)Client.rmiGetService(config.getUuid());
					}catch(Exception ex){
						log.log(ex,Logger.LEVEL_WARNING);
						httpChannel=Client.httpGetService(jhttp,jclient,config.getUuid());
					}
				}
			}catch(Exception ex){
				log.log(ex,Logger.LEVEL_WARNING);
			}
			
			if(httpChannel==null&&service==null){
				log.log("http and rmi- channel to "+config.getCode()+","+config.getUuid()+" are both unavailable.",Logger.LEVEL_ERROR);
				continue;
			}
			
			DAO dao=null;
			try{
				dao=DB.connect(SysConfig.databaseName,JDFSSynchronizer.class);
				List tasks=dao.find("j_fs_task","to_uuid='"+this.config.getUuid()+"' order by task_time asc",100,1);
				for(int i=0;i<tasks.size();i++){
					JfsTask task=(JfsTask)tasks.get(i);
					String uuid=task.getUuid();
					String path="syn:"+task.getFilePath();
					String operation=task.getTaskOperation();
		
					Object[] datas=(Object[])JObject.string2Serializable(task.getTaskData());
					
					try{
						if("delete".equals(operation)){
							this.delete(httpChannel,service,path);
						}else if("mkdir".equals(operation)){
							this.mkdir(httpChannel,service,path);
						}else if("mkdirs".equals(operation)){
							this.mkdirs(httpChannel,service,path);
						}else if("renameTo".equals(operation)){
							this.renameTo(httpChannel,service,path,(String)datas[0]);
						}else if("setExecutable".equals(operation)){
							this.setExecutable(httpChannel,service,path,((Boolean)datas[0]).booleanValue(),((Boolean)datas[1]).booleanValue());
						}else if("setLastModified".equals(operation)){
							this.setLastModified(httpChannel,service,path,((Long)datas[0]).longValue());
						}else if("setReadable".equals(operation)){
							this.setReadable(httpChannel,service,path,((Boolean)datas[0]).booleanValue(),((Boolean)datas[1]).booleanValue());
						}else if("setReadOnly".equals(operation)){
							this.setReadOnly(httpChannel,service,path);
						}else if("setWritable".equals(operation)){
							this.setWritable(httpChannel,service,path,((Boolean)datas[0]).booleanValue(),((Boolean)datas[1]).booleanValue());
						}else if("saveString".equals(operation)){
							String string=JUtilInputStream.string(new FileInputStream(new File((String)datas[0])),(String)datas[2]);
							this.saveString(httpChannel,service,path,string,((Boolean)datas[1]).booleanValue(),(String)datas[2]);
						}else if("saveBytes".equals(operation)){
							this.saveFile(httpChannel,service,path,new File((String)datas[0]));
						}else if("saveFile".equals(operation)){
							this.saveFile(httpChannel,service,path,new File((String)datas[0]));
						}
						dao.executeSQL("delete from j_fs_task where uuid='"+uuid+"'");
						
						log.log(uuid+","+path+" to "+this.config.getUuid()+" has syn successfully.",Logger.LEVEL_DEBUG);
					}catch(Exception ex){
						log.log(ex,Logger.LEVEL_ERROR);
					}						
				}
				JUtilList.clear_AllNull(tasks);
				
				dao.close();
				dao=null;
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
	}
}
