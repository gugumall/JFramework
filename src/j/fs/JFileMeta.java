package j.fs;

import java.io.File;
import java.io.Serializable;

/**
 * 
 * @author 肖炯
 *
 */
public class JFileMeta implements Serializable{
	private static final long serialVersionUID = 1L;
	public boolean canExecute;
	public boolean canRead;
	public boolean canWrite;
	public boolean exists;
	public String absolutePath;
	public String canonicalPath;
	public long freeSpace;
	public String name;
	public String parent;
	public String path;
	public long totalSpace;
	public long usableSpace;
	public boolean isAbsolute;
	public boolean isDirectory;
	public boolean isFile;
	public boolean isHidden;
	public long lastModified;
	public long length;
	
	/**
	 * 
	 * @param file
	 */
	public JFileMeta(File file) {
		super();
		getMeta(file);
	}
	
	/**
	 * 
	 * @param file
	 */
	public void getMeta(File file) {
		this.canExecute=file.canExecute();
		this.canRead=file.canRead();
		this.canWrite=file.canWrite();
		this.exists=file.exists();
		this.absolutePath=file.getAbsolutePath();
		try{
			this.canonicalPath=file.getCanonicalPath();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.freeSpace=file.getFreeSpace();
		this.name=file.getName();
		this.parent=file.getParent();
		this.path=file.getPath();
		this.totalSpace=file.getTotalSpace();
		this.usableSpace=file.getUsableSpace();
		this.isAbsolute=file.isAbsolute();
		this.isDirectory=file.isDirectory();
		this.isFile=file.isFile();
		this.isHidden=file.isHidden();
		this.lastModified=file.lastModified();
		this.length=file.length();
	}
}
