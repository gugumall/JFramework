package j.fs;

import j.common.JProperties;

import java.io.File;

/**
 * 被监视的文件
 * @author ceo
 *
 */
public class JDFSMonitorFile{
	private String path;
	private File file;
	
	/**
	 * 
	 * @param _path
	 */
	public JDFSMonitorFile(String _path){
		this.path=JProperties.getAppRoot()+_path;
		this.file=new File(this.path);
	}
	
	/**
	 * 
	 */
	public void renew(){
		this.file=new File(this.path);
	}
	
	/**
	 * 
	 * @return
	 */
	public long getLastModified(){
		return this.file.lastModified();
	}
}
