package j.fs;

import j.app.webserver.JSession;
import j.common.JObject;
import j.dao.DAO;
import j.dao.DB;
import j.db.JfsTask;
import j.http.Upload;
import j.http.UploadMsg;
import j.http.UploadedFile;
import j.log.Logger;
import j.service.Constants;
import j.service.server.ServiceBaseImpl;
import j.service.server.ServiceConfig;
import j.service.server.ServiceManager;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.JUtilInputStream;
import j.util.JUtilUUID;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author 肖炯
 *
 */
public class JDFSServiceImpl extends JDFSServiceAbstract{
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.create(JDFSServiceImpl.class);
	

	/**
	 * 
	 * @throws RemoteException
	 */
	public JDFSServiceImpl()throws RemoteException{
		super();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.service.server.ServiceBase#init()
	 */
	public void init() throws RemoteException{		
		try{//创建同步线程	
			ServiceConfig[] members=ServiceManager.getServices(this.getServiceConfig().getCode(),true);
			for(int t=0;t<members.length;t++){
				if(members[t].getUuid().equals(this.getServiceConfig().getUuid())) continue;
				JDFSSynchronizer.create(members[t]);
			}
		}catch(Exception e){
			log.log(e,Logger.LEVEL_FATAL);
			throw new RemoteException(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param path
	 * @param operation
	 * @param data
	 * @throws RemoteException
	 */
	private void saveTask(String path,String operation,Object[] data) throws RemoteException{
		ServiceConfig[] members=ServiceManager.getServices(this.getServiceConfig().getCode());
		if(members.length<2) return;
		
		DAO dao=null;
		try{
			dao=DB.connect(SysConfig.databaseName,JDFSServiceImpl.class);
			dao.beginTransaction();
			
			for(int i=0;i<members.length;i++){
				if(members[i].getUuid().equalsIgnoreCase(this.getServiceConfig().getUuid())) continue;

				JfsTask task=new JfsTask();
				task.setUuid(JUtilUUID.genUUID());
				task.setTaskTime(new Timestamp(SysUtil.getNow()));
				task.setFromUuid(this.getServiceConfig().getUuid());
				task.setToUuid(members[i].getUuid());
				task.setFilePath(path);
				task.setTaskOperation(operation);
				task.setTaskData(JObject.serializable2String(data));
				dao.insert(task);
				
				task=null;
			}
			
			dao.commit();
			dao.close();
		}catch(Exception e){
			if(dao!=null){
				try{
					dao.rollback();
				}catch(Exception ex){}
				try{
					dao.close();
					dao=null;
				}catch(Exception ex){}
			}
			log.log(e,Logger.LEVEL_ERROR);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#_new(java.lang.String, java.lang.String, java.lang.String)
	 */
	public JFileMeta _new(String clientUuid, String md54Service,String path) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"_new",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}
		
		return new JFileMeta(new File(JDFS.physicalPath(path)));
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#delete(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean delete(String clientUuid, String md54Service, String path) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"delete",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}
		
		if(!path.startsWith("syn:")){
			saveTask(path,"delete",null);
		}else{
			path=path.substring(4);
		}
		
		return (new File(JDFS.physicalPath(path)).delete());
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#list(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String[] list(String clientUuid, String md54Service, String path) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"list",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}
	
		return (new File(JDFS.physicalPath(path))).list();
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#mkdir(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean mkdir(String clientUuid, String md54Service, String path) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"mkdir",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}
		
		if(!path.startsWith("syn:")){
			saveTask(path,"mkdir",null);
		}else{
			path=path.substring(4);
		}
	
		return (new File(JDFS.physicalPath(path))).mkdir();
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#mkdirs(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean mkdirs(String clientUuid, String md54Service, String path) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"mkdirs",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}

		
		if(!path.startsWith("syn:")){
			saveTask(path,"mkdirs",null);
		}else{
			path=path.substring(4);
		}
	
		return (new File(JDFS.physicalPath(path))).mkdirs();
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#renameTo(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean renameTo(String clientUuid, String md54Service, String path, String dest) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"renameTo",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}

		if(!path.startsWith("syn:")){
			saveTask(path,"renameTo",new Object[]{dest});
		}else{
			path=path.substring(4);
		}
		
		
		File destFile=new File(JDFS.mappingPath(dest));
		destFile.getParentFile().mkdirs();
		
		return (new File(JDFS.physicalPath(path))).renameTo(new File(JDFS.mappingPath(dest)));
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#setExecutable(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	public boolean setExecutable(String clientUuid, String md54Service, String path, boolean executable, boolean ownerOnly) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"setExecutable",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}

		if(!path.startsWith("syn:")){
			saveTask(path,"setExecutable",new Object[]{Boolean.valueOf(executable),Boolean.valueOf(ownerOnly)});
		}else{
			path=path.substring(4);
		}
		
		return (new File(JDFS.physicalPath(path))).setExecutable(executable,ownerOnly);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#setLastModified(java.lang.String, java.lang.String, java.lang.String, long)
	 */
	public boolean setLastModified(String clientUuid, String md54Service, String path, long time) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"setLastModified",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}

		if(!path.startsWith("syn:")){
			saveTask(path,"setLastModified",new Object[]{Long.valueOf(time)});
		}else{
			path=path.substring(4);
		}
		
		return (new File(JDFS.physicalPath(path))).setLastModified(time);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#setReadable(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	public boolean setReadable(String clientUuid, String md54Service, String path, boolean readable, boolean ownerOnly) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"setReadable",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}

		if(!path.startsWith("syn:")){
			saveTask(path,"setReadable",new Object[]{Boolean.valueOf(readable),Boolean.valueOf(ownerOnly)});
		}else{
			path=path.substring(4);
		}
		
		return (new File(JDFS.physicalPath(path))).setReadable(readable,ownerOnly);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#setReadOnly(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean setReadOnly(String clientUuid, String md54Service, String path)  throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"setReadOnly",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}

		if(!path.startsWith("syn:")){
			saveTask(path,"setReadOnly",null);
		}else{
			path=path.substring(4);
		}
		
		return (new File(JDFS.physicalPath(path))).setReadOnly();
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#setWritable(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	public boolean setWritable(String clientUuid, String md54Service, String path, boolean writable, boolean ownerOnly) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"setWritable",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}

		if(!path.startsWith("syn:")){			
			saveTask(path,"setWritable",new Object[]{Boolean.valueOf(writable),Boolean.valueOf(ownerOnly)});
		}else{
			path=path.substring(4);
		}
		
		return (new File(JDFS.physicalPath(path))).setWritable(writable,ownerOnly);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#bytes(java.lang.String, java.lang.String, java.lang.String)
	 */
	public byte[] bytes(String clientUuid, String md54Service, String path) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"bytes",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}
		
		try{
			return JUtilInputStream.bytes(new FileInputStream(new File(JDFS.physicalPath(path))));
		}catch(Exception e){
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#string(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String string(String clientUuid, String md54Service, String path, String encoding) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"string",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}
		
		try{
			if(encoding==null) return JUtilInputStream.string(new FileInputStream(new File(JDFS.physicalPath(path))));
			else return JUtilInputStream.string(new FileInputStream(new File(JDFS.physicalPath(path))),encoding);
		}catch(Exception e){
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#saveString(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String)
	 */
	public void saveString(String clientUuid, String md54Service, String path, String content, boolean append, String encoding) throws RemoteException {
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"saveString",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}

		if(!path.startsWith("syn:")){				
			saveTask(path,"saveString",new Object[]{path,Boolean.valueOf(append),encoding});
		}else{
			path=path.substring(4);
		}
		
		try{			
			File file=new File(JDFS.physicalPath(path));
			file.getParentFile().mkdirs();
			
			Writer writer=null;
			if(encoding!=null) writer=new OutputStreamWriter(new FileOutputStream(file,append),encoding);
			else writer=new OutputStreamWriter(new FileOutputStream(file,append));
			writer.write(content);
			writer.flush();
			
			try{
				writer.close();
			}catch(Exception e){}
		}catch(Exception e){
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#saveBytes(java.lang.String, java.lang.String, java.lang.String, byte[])
	 */
	public void saveBytes(String clientUuid, String md54Service, String path, byte[] bytes) throws RemoteException {
		log.log("save bytes from rmi path - "+path,Logger.LEVEL_DEBUG);
		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"saveBytes",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}

		if(!path.startsWith("syn:")){					
			saveTask(path,"saveBytes",new Object[]{path});
		}else{
			path=path.substring(4);
		}
		
		try{
			File file=new File(JDFS.physicalPath(path));
			if(file.exists()) file.delete();
			else file.getParentFile().mkdirs();
			
			InputStream is=new ByteArrayInputStream(bytes);
			OutputStream os=new FileOutputStream(file);
			
			byte[] buffer=new byte[1024];
			int readed=is.read(buffer);
			while(readed>-1){
				os.write(buffer,0,readed);
				readed=is.read(buffer);
			}
			os.flush();
			
			try{
				is.close();
			}catch(Exception e){}
			
			try{
				os.close();
			}catch(Exception e){}
		}catch(Exception e){
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#_new(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void _new(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
				
		try{
			JFileMeta meta=_new(clientUuid,md54Service,path);
			jsession.resultString=JObject.serializable2String(meta);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString="null";
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#delete(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void delete(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
		
		try{
			jsession.resultString=delete(clientUuid,md54Service,path)+"";
		}catch(Exception e){
			e.printStackTrace();
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#list(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void list(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
		
		try{
			String[] result=list(clientUuid,md54Service,path);
			
			jsession.resultString=JObject.serializable2String(result);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);					
			jsession.resultString="null";
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#mkdir(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void mkdir(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
		
		try{
			jsession.resultString=mkdir(clientUuid,md54Service,path)+"";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#mkdirs(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void mkdirs(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
		
		try{
			jsession.resultString=mkdirs(clientUuid,md54Service,path)+"";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#renameTo(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void renameTo(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
		String dest=SysUtil.getHttpParameter(request,"dest");
		
		try{
			jsession.resultString=renameTo(clientUuid,md54Service,path,dest)+"";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#setExecutable(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void setExecutable(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
		boolean executable="true".equalsIgnoreCase(SysUtil.getHttpParameter(request,"executable"));
		boolean ownerOnly="true".equalsIgnoreCase(SysUtil.getHttpParameter(request,"ownerOnly"));
	
		try{
			jsession.resultString=setExecutable(clientUuid,md54Service,path,executable,ownerOnly)+"";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#setLastModified(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void setLastModified(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
	
		try{
			long time=Long.parseLong(SysUtil.getHttpParameter(request,"true"));
			jsession.resultString=setLastModified(clientUuid,md54Service,path,time)+"";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#setReadable(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void setReadable(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
		boolean readable="true".equalsIgnoreCase(SysUtil.getHttpParameter(request,"readable"));
		boolean ownerOnly="true".equalsIgnoreCase(SysUtil.getHttpParameter(request,"ownerOnly"));
		
		try{
			jsession.resultString=setReadable(clientUuid,md54Service,path,readable,ownerOnly)+"";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#setReadOnly(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void setReadOnly(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
		
		try{
			jsession.resultString=setReadOnly(clientUuid,md54Service,path)+"";	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}	
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#setWritable(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void setWritable(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
		boolean writable="true".equalsIgnoreCase(SysUtil.getHttpParameter(request,"writable"));
		boolean ownerOnly="true".equalsIgnoreCase(SysUtil.getHttpParameter(request,"ownerOnly"));
	
		try{
			jsession.resultString=setWritable(clientUuid,md54Service,path,writable,ownerOnly)+"";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#bytes(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void bytes(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
	
		try{
			byte[] result=bytes(clientUuid,md54Service,path);			
			jsession.resultString=JObject.serializable2String(result);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#string(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void string(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
		String encoding=SysUtil.getHttpParameter(request,"encoding");
		try{
			jsession.resultString=string(clientUuid,md54Service,path,encoding);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#saveString(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void saveString(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
		String content=SysUtil.getHttpParameter(request,"content");
		boolean append="true".equalsIgnoreCase(SysUtil.getHttpParameter(request,"append"));
		String encoding=SysUtil.getHttpParameter(request,"encoding");
		try{
			saveString(clientUuid,md54Service,path,JObject.intSequence2String(content),append,encoding);	
			jsession.resultString=Constants.INVOKING_DONE;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#saveBytes(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void saveBytes(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");
		String bytes=SysUtil.getHttpParameter(request,"bytes");

		log.log("save bytes from http path - "+path,Logger.LEVEL_DEBUG);
		try{
			saveBytes(clientUuid,md54Service,path,(byte[])JObject.string2Serializable(bytes));		
			jsession.resultString=Constants.INVOKING_DONE;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.fs.JDFSService#saveFile(j.app.webserver.JSession, javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void saveFile(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String clientUuid=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_CLIENT_UUID);
		String md54Service=SysUtil.getHttpParameter(request,Constants.JSERVICE_PARAM_MD5_STRING_4SERVICE);
		String path=SysUtil.getHttpParameter(request,"path");

		try{
			if(!path.startsWith("syn:")) auth(clientUuid,"saveFile",md54Service);
		}catch(RemoteException e){
			throw new RemoteException(Constants.AUTH_FAILED);
		}

		if(!path.startsWith("syn:")){					
			saveTask(path,"saveBytes",new Object[]{path});
		}else{
			path=path.substring(4);
		}
		
		log.log("save file from http path - "+path,Logger.LEVEL_DEBUG);
		List files = null;
		try {
			Upload uploader = new Upload(request, 
					"",
					"ISO-8859-1",
					JDFS.getMaxFileSize(),
					true);
			
			UploadMsg upMsg = uploader.save();//保存附件

			/*
			 * 如果上传成功，做相应处理
			 */
			if (upMsg.isSuccessful) {
				Map p=uploader.getOtherParameters();
				for(Iterator it=p.keySet().iterator();it.hasNext();){
					Object key=it.next();
					String value=(String)p.get(key);
					value=new String(value.getBytes("ISO-8859-1"),SysConfig.sysEncoding);
					p.put(key,value);
				}
				
				boolean valid=true;
				files = uploader.getUploadedFiles();
				if(files.size()!=1) valid=false;
				
				if(!valid){//格式不正确，或者有空文件，删除残留文件
					for (int i = 0; i < files.size(); i++) {
						UploadedFile upFile = (UploadedFile) files.get(i);
						File file = new File(upFile.getAbsoluteFileName_Saved());//文件对象
						if(file.exists()){
							file.delete();
						}					
					}
					throw new Exception("数据错误（文件个数必须为1）");
				}
				
				//指定保存文件，直接保存
				for (int i = 0; i < files.size(); i++) {
					UploadedFile upFile = (UploadedFile) files.get(i);
					File file = new File(upFile.getAbsoluteFileName_Saved());//文件对象
					
					File dest=new File(path);
					if(!dest.getParentFile().exists()){
						dest.getParentFile().mkdirs();
					}
					
					file.renameTo(dest);
				}
			} else {		
				throw new Exception("文件上传不成功");
			}
		} catch (Exception e) {
			if(files!=null){//删除残留文件
				try{
					for (int i = 0; i < files.size(); i++) {
						UploadedFile upFile = (UploadedFile) files.get(i);
						File file = new File(upFile.getAbsoluteFileName_Saved());//文件对象
						if(file.exists()){
							file.delete();
						}					
					}
				}catch(Exception ex){}
			}
			log.log(e, Logger.LEVEL_ERROR);			
			throw new RemoteException(e.getMessage());
		}
	}
}
