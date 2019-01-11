package j.fs;

import j.common.JObject;
import j.http.JHttp;
import j.log.Logger;
import j.service.Manager;
import j.service.client.Client;
import j.sys.SysConfig;
import j.util.JUtilInputStream;
import j.util.JUtilString;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;

/**
 * 
 * @author JFramework
 *
 */
public class JDFSFile extends JFile{
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.create(JDFSFile.class);
	private JDFSMapping mapping=null;
	private JDFSMappingRule rule=null;
	private JFileMeta meta=null;
	private String path=null;
	private String physicalPath=null;
	private String serviceCode=null;
	private String serviceChannel="rmi";
	private JDFSService service=null;
	private JHttp jhttp=null;
	private HttpClient jclient=null;
	private String httpChannel=null;
	
	/**
	 * 将文件读取成指定编码的字符串
	 * @param encoding
	 * @return
	 */
	public static String read(File file,String encoding){
		try{
	    	if(file.exists()){
	    		if(encoding!=null) return JUtilInputStream.string(new FileInputStream(file),encoding);
	        	else return JUtilInputStream.string(new FileInputStream(file));
	    	}else{
	    		return null;
	    	}
		}catch(Exception e){
			return null;
		}
	}
	
	
	/**
	 * 将文件读取成字符串
	 * @return
	 */
	public static String read(File file){
		return read(file,null);
	}
	
	/**
	 * 
	 * @param is
	 * @param path
	 */
	public static void saveStream(InputStream is,String path){		
		try{
			File file=new File(path);
			if(file.exists()) file.delete();
			else file.getParentFile().mkdirs();
			
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
			try{
				is.close();
			}catch(Exception ex){}
			log.log(e,Logger.LEVEL_ERROR);
		}
	}
	
	/**
	 * 
	 * @param path
	 * @param content
	 * @param append
	 * @param encoding
	 */
	public static void saveString(String path, String content, boolean append, String encoding){
		try{			
			File file=new File(path);
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
			log.log(e,Logger.LEVEL_ERROR);
		}
	}
	
	/**
	 * 
	 * @param path must be absolute path and mapping to absolute path
	 */
	public JDFSFile(String path) throws Exception {
		super(JDFS.mappingPath(path));
		this.path=path;
		log.log("new JDFS file - "+path,Logger.LEVEL_DEBUG);
		
		mapping=JDFS.mapping(path);
		rule=mapping==null?null:mapping.getRule(path);
		if(mapping==null||rule.isLocal()){
			if(mapping==null) log.log("JDFSMapping can't be found,so it's local file - "+path,Logger.LEVEL_DEBUG);
			path=JDFS.mappingPath(path);
			physicalPath=path;
			meta=new JFileMeta(new File(path));
		}else{
			physicalPath=rule.virtual2Physical(path,mapping.getOs());
			serviceCode=mapping.getServiceCode();
			serviceChannel=mapping.getServiceChannel();

			log.log("JDFSMapping found,the related service is - "+serviceCode+"("+serviceChannel+"), and physical path is - "+physicalPath+"- "+path,Logger.LEVEL_DEBUG);
			
			service=(JDFSService)Client.rmiGetService(serviceCode,true);
			if(service!=null){
				log.log("JDFSService(rmi) found local - "+service+" - "+path,Logger.LEVEL_DEBUG);
				
				meta=service._new(Manager.getClientNodeUuid(),Client.md54Service(serviceCode,"_new"),physicalPath);
			}else if("rmi".equalsIgnoreCase(mapping.getServiceChannel())){
				service=(JDFSService)Client.rmiGetService(serviceCode);			
				log.log("JDFSService(rmi) found - "+service+" - "+path,Logger.LEVEL_DEBUG);
				
				meta=service._new(Manager.getClientNodeUuid(),Client.md54Service(serviceCode,"_new"),physicalPath);
			}else{
				jhttp=JHttp.getInstance();
				jclient=jhttp.createClient();
				
				httpChannel=Client.httpGetService(jhttp,jclient,serviceCode);
				log.log("JDFSService(http) found - "+httpChannel+" - "+path,Logger.LEVEL_DEBUG);				
				
				Map params=new HashMap();
				params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
				String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"_new",params);
				params.clear();
				params=null;
				try{
					meta=(JFileMeta)JObject.string2Serializable(result);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}	
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#canExecute()
	 */
	public boolean canExecute(){
		return meta==null?false:meta.canExecute;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#canRead()
	 */
	public boolean canRead(){
		return meta==null?false:meta.canRead;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#canWrite()
	 */
	public boolean canWrite(){
		return meta==null?false:meta.canWrite;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#delete()
	 */
	public boolean delete(){
		if(mapping==null||rule.isLocal()){
			return super.delete();
		}else{
			if(service!=null){
				try{
					return service.delete(Manager.getClientNodeUuid(),Client.md54Service(serviceCode,"delete"),path);
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_ERROR);
					return false;
				}
			}else{
				try{
					Map params=new HashMap();
					params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
					String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"delete",params);
					params.clear();
					params=null;
					
					return "true".equalsIgnoreCase(result);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					return false;
				}				
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#deleteOnExit()
	 */
	public void deleteOnExit(){
		delete();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#exists()
	 */
	public boolean exists(){
		return meta==null?false:meta.exists;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#getAbsoluteFile()
	 */
	public File getAbsoluteFile(){
		return this;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#getAbsolutePath()
	 */
	public String getAbsolutePath(){
		if(meta==null) return null;
		else if(rule==null||rule.isLocal()) return meta.absolutePath;//local file
		else{
			if(this.isDirectory()) return rule.physical2Virtual(meta.absolutePath+"/");//transfer to JDFS virtual path
			else return rule.physical2Virtual(meta.absolutePath);//transfer to JDFS virtual path
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see java.io.File#getCanonicalFile()
	 */
	public java.io.File getCanonicalFile() throws IOException{
		return JFile.create(this.getCanonicalPath());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#getCanonicalPath()
	 */
	public String getCanonicalPath() throws IOException{
		if(meta==null) return null;
		else if(rule==null||rule.isLocal()) return meta.canonicalPath;//local file
		else return rule.physical2Virtual(meta.canonicalPath);//transfer to JDFS virtual path
	}


	/*
	 *  (non-Javadoc)
	 * @see java.io.File#getFreeSpace()
	 */
	public long getFreeSpace(){
		return meta==null?-1:meta.freeSpace;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#getName()
	 */
	public String getName(){
		if(meta==null) return null;
		else if(rule==null||rule.isLocal()) return meta.name;//local file
		else return rule.physical2Virtual(meta.name);//transfer to JDFS virtual path
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#getParent()
	 */
	public String getParent(){
		if(meta==null) return null;
		else if(rule==null||rule.isLocal()) return meta.parent;//local file
		else return rule.physical2Virtual(meta.parent+"/");//transfer to JDFS virtual path
	}
	

	/*
	 *  (non-Javadoc)
	 * @see java.io.File#getParentFile()
	 */
	public java.io.File getParentFile(){
		return JFile.create(this.getParent());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#getPath()
	 */
	public String getPath(){
		if(meta==null) return null;
		else if(rule==null||rule.isLocal()) return meta.path;//local file
		else return rule.physical2Virtual(meta.path);//transfer to JDFS virtual path
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#getTotalSpace()
	 */
	public long getTotalSpace(){
		return meta==null?-1:meta.totalSpace;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#getUsableSpace()
	 */
	public long getUsableSpace(){
		return meta==null?-1:meta.usableSpace;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#isAbsolute()
	 */
	public boolean isAbsolute(){
		return meta==null?false:meta.isAbsolute;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#isDirectory()
	 */
	public boolean isDirectory(){
		return meta==null?false:meta.isDirectory;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#isFile()
	 */
	public boolean isFile(){
		return meta==null?false:meta.isFile;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#isHidden()
	 */
	public boolean isHidden(){
		return meta==null?false:meta.isHidden;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#lastModified()
	 */
	public long lastModified(){
		return meta==null?-1:meta.lastModified;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#length()
	 */
	public long length(){
		return meta==null?-1:meta.length;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#list()
	 */
	public String[] list(){
		String[] ls=null;
		if(mapping==null||rule.isLocal()){
			ls=super.list();
		}else{
			if(service!=null){
				try{
					ls = service.list(Manager.getClientNodeUuid(),Client.md54Service(serviceCode,"list"),path);					
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_ERROR);
					return null;
				}
			}else{
				try{						
					Map params=new HashMap();
					params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
					String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"list",params);
					params.clear();
					params=null;
					
					ls=(String[])JObject.string2Serializable(result);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					return null;
				}				
			}
		}
		
		if(ls!=null){
			for(int i=0;i<ls.length;i++){
				String ppath=this.getAbsolutePath();
				if(!ppath.endsWith("/")) ppath+="/";
				ls[i]=ppath+ls[i];
			}
		}
		
		return ls;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#list(java.io.FilenameFilter)
	 */
	public String[] list(FilenameFilter filter){
		String names[] = list();
		if ((names == null) || (filter == null)) {
		    return names;
		}
		ArrayList v = new ArrayList();
		for (int i = 0 ; i < names.length ; i++) {
		    if (filter.accept(this, names[i])) {
		    	v.add(names[i]);
		    }
		}
		return (String[])(v.toArray(new String[v.size()]));
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#listFiles()
	 */
	public java.io.File[] listFiles(){
		String[] ss = list();
		if (ss == null) return null;
		int n = ss.length;
		JFile[] fs = new JFile[n];
		for (int i = 0; i < n; i++) {
		    fs[i] = JFile.create(ss[i]);
		}
		return fs;
	}

	/*
	 *  (non-Javadoc)
	 * @see java.io.File#listFiles(java.io.FileFilter)
	 */
	public java.io.File[] listFiles(FileFilter filter){
		String ss[] = list();
		if (ss == null) return null;
		ArrayList<JFile> v = new ArrayList();
		for (int i = 0 ; i < ss.length ; i++) {
			JFile f = JFile.create(ss[i]);
		    if ((filter == null) || filter.accept(f)) {
		    	v.add(f);
		    }
		}
		return (JFile[])(v.toArray(new JFile[v.size()]));
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#listFiles(java.io.FilenameFilter)
	 */
	public java.io.File[] listFiles(FilenameFilter filter){
		String ss[] = list();
		if (ss == null) return null;
		ArrayList<JFile> v = new ArrayList();
		for (int i = 0 ; i < ss.length ; i++) {
		    if ((filter == null) || filter.accept(this, ss[i])) {
		    	v.add(JFile.create(ss[i]));
		    }
		}
		return (JFile[])(v.toArray(new JFile[v.size()]));
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#mkdir()
	 */
	public boolean mkdir(){
		if(mapping==null||rule.isLocal()){
			return super.mkdir();
		}else{
			if(service!=null){
				try{
					return service.mkdir(Manager.getClientNodeUuid(),
							Client.md54Service(serviceCode,"mkdir"),
							path);
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_ERROR);
					return false;
				}
			}else{
				try{				
					Map params=new HashMap();
					params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
					String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"mkdir",params);
					params.clear();
					params=null;
					
					return "true".equalsIgnoreCase(result);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					return false;
				}				
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#mkdirs()
	 */
	public boolean mkdirs(){
		if(mapping==null||rule.isLocal()){
			return super.mkdirs();
		}else{
			if(service!=null){
				try{
					return service.mkdirs(Manager.getClientNodeUuid(),
							Client.md54Service(serviceCode,"mkdirs"),
							path);
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_ERROR);
					return false;
				}
			}else{
				try{							
					Map params=new HashMap();
					params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
					String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"mkdirs",params);
					params.clear();
					params=null;
					
					return "true".equalsIgnoreCase(result);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					return false;
				}				
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#renameTo(java.io.File)
	 */
	public boolean renameTo(File dest){
		if(mapping==null||rule.isLocal()){
			return super.renameTo(dest);
		}else{
			if(service!=null){
				try{
					return service.renameTo(Manager.getClientNodeUuid(),
							Client.md54Service(serviceCode,"renameTo"),
							path,
							rule.virtual2Physical(dest.getAbsolutePath(),mapping.getOs()));
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_ERROR);
					return false;
				}
			}else{
				try{											
					Map params=new HashMap();
					params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
					params.put("dest",JUtilString.encodeURI(dest.getAbsolutePath(),SysConfig.sysEncoding));
					String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"renameTo",params);
					params.clear();
					params=null;
					
					return "true".equalsIgnoreCase(result);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					return false;
				}				
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#setExecutable(boolean)
	 */
	public boolean setExecutable(boolean executable){
		return this.setExecutable(executable,true);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#setExecutable(boolean, boolean)
	 */
	public boolean setExecutable(boolean executable,boolean ownerOnly){
		if(mapping==null||rule.isLocal()){
			return super.setExecutable(executable);
		}else{
			if(service!=null){
				try{
					return service.setExecutable(Manager.getClientNodeUuid(),
							Client.md54Service(serviceCode,"setExecutable"),
							path,
							executable,
							ownerOnly);
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_ERROR);
					return false;
				}
			}else{
				try{									
					Map params=new HashMap();
					params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
					params.put("executable",executable+"");
					params.put("ownerOnly",ownerOnly+"");
					String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"setExecutable",params);
					params.clear();
					params=null;
					
					return "true".equalsIgnoreCase(result);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					return false;
				}				
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#setLastModified(long)
	 */
	public boolean setLastModified(long time){
		if(mapping==null||rule.isLocal()){
			return super.setLastModified(time);
		}else{
			if(service!=null){
				try{
					return service.setLastModified(Manager.getClientNodeUuid(),
							Client.md54Service(serviceCode,"setLastModified"),
							path,time);
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_ERROR);
					return false;
				}
			}else{
				try{								
					Map params=new HashMap();
					params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
					params.put("time",time+"");
					String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"setLastModified",params);
					params.clear();
					params=null;
					
					return "true".equalsIgnoreCase(result);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					return false;
				}				
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#setReadable(boolean)
	 */
	public boolean setReadable(boolean readable){
		return setReadable(readable,true);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#setReadable(boolean, boolean)
	 */
	public boolean setReadable(boolean readable,boolean ownerOnly){
		if(mapping==null||rule.isLocal()){
			return super.setExecutable(readable,ownerOnly);
		}else{
			if(service!=null){
				try{
					return service.setReadable(Manager.getClientNodeUuid(),
							Client.md54Service(serviceCode,"setReadable"),
							path,
							readable,
							ownerOnly);
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_ERROR);
					return false;
				}
			}else{
				try{							
					Map params=new HashMap();
					params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
					params.put("readable",readable+"");
					params.put("ownerOnly",ownerOnly+"");
					String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"setReadable",params);
					params.clear();
					params=null;
					
					return "true".equalsIgnoreCase(result);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					return false;
				}				
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#setReadOnly()
	 */
	public boolean setReadOnly(){
		if(mapping==null||rule.isLocal()){
			return super.setReadOnly();
		}else{
			if(service!=null){
				try{
					return service.setReadOnly(Manager.getClientNodeUuid(),
							Client.md54Service(serviceCode,"setReadOnly"),
							path);
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_ERROR);
					return false;
				}
			}else{
				try{				
					Map params=new HashMap();
					params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
					String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"setReadOnly",params);
					params.clear();
					params=null;
					
					return "true".equalsIgnoreCase(result);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					return false;
				}				
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#setWritable(boolean)
	 */
	public boolean setWritable(boolean writable){
		return setWritable(writable,true);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.io.File#setWritable(boolean, boolean)
	 */
	public boolean setWritable(boolean writable,boolean ownerOnly){
		if(mapping==null||rule.isLocal()){
			return super.setExecutable(writable,ownerOnly);
		}else{
			if(service!=null){
				try{
					return service.setWritable(Manager.getClientNodeUuid(),
							Client.md54Service(serviceCode,"setWritable"),
							path,
							writable,
							ownerOnly);
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_ERROR);
					return false;
				}
			}else{
				try{			
					Map params=new HashMap();
					params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
					params.put("writable",writable+"");
					params.put("ownerOnly",ownerOnly+"");
					String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"setWritable",params);
					params.clear();
					params=null;
					
					return "true".equalsIgnoreCase(result);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					return false;
				}				
			}
		}
	}


	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#bytes()
	 */
	public byte[] bytes() throws Exception{
		if(mapping==null||rule.isLocal()){
			return JUtilInputStream.bytes(new FileInputStream(this));
		}else{
			if(service!=null){
				return service.bytes(Manager.getClientNodeUuid(),
						Client.md54Service(serviceCode,"bytes"),
						path);
			}else{
				try{				
					Map params=new HashMap();
					params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
					String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"bytes",params);
					params.clear();
					params=null;
					
					return (byte[])JObject.string2Serializable(result);
				}catch(Exception e){
					log.log(e,Logger.LEVEL_ERROR);
					return null;
				}				
			}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#string()
	 */
	public String string() throws Exception{
		return string(null);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#string(java.lang.String)
	 */
	public String string(String encoding) throws Exception{
		if(!exists()) return null;
		
		if(mapping==null||rule.isLocal()){
			if(encoding!=null) return JUtilInputStream.string(new FileInputStream(this),encoding);
        	else return JUtilInputStream.string(new FileInputStream(this));
		}else{
			if(service!=null){
				return service.string(Manager.getClientNodeUuid(),
						Client.md54Service(serviceCode,"string"),
						path,
						encoding);
			}else{
				Map params=new HashMap();
				params.put("path",JUtilString.encodeURI(path,SysConfig.sysEncoding));
				if(encoding!=null) params.put("encoding",encoding);
				String result=Client.httpCallGet(null,jclient,serviceCode,httpChannel,"string",params);
				params.clear();
				params=null;
				
				return result;				
			}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#save(java.lang.String, boolean)
	 */
	public void save(String content, boolean append) throws Exception {
		save(content,append,null);
	}

	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#save(java.lang.String, boolean, java.lang.String)
	 */
	public void save(String content, boolean append, String encoding) throws Exception {
		if(mapping==null||rule.isLocal()){
			this.getParentFile().mkdirs();
			
			Writer writer=null;
			if(encoding!=null) writer=new OutputStreamWriter(new FileOutputStream(this,append),encoding);
			else writer=new OutputStreamWriter(new FileOutputStream(this,append));
			writer.write(content);
			writer.flush();
			
			try{
				writer.close();
			}catch(Exception e){}	
		}else{
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
	}

	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#save(byte[])
	 */
	public void save(byte[] bytes) throws Exception {
		if(mapping==null||rule.isLocal()){
			save(new ByteArrayInputStream(bytes));
		}else{
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
	}

	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#save(java.io.InputStream)
	 */
	public void save(InputStream is) throws Exception {
		if(mapping==null||rule.isLocal()){
			File target=this;
			
			if(target.exists()) target.delete();
			else target.getParentFile().mkdirs();
			
			OutputStream os=new FileOutputStream(target);
			
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
		}else{
			save(JUtilInputStream.bytes(is));
		}		
	}

	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#save(java.io.InputStream)
	 */
	public void save(File file) throws Exception {
		if(mapping==null||rule.isLocal()){
			save(new FileInputStream(file));
		}else{
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

				Client.httpCallMultiPart(null,
						jclient,serviceCode,httpChannel,"saveFile",params,parts);
				
				params.clear();
				params.clear();
				
				parts.clear();
				parts=null;				
			}
		}	
	}
}
