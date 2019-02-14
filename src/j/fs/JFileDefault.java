package j.fs;

import j.util.JUtilInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 
 * @author 肖炯
 *
 */
public class JFileDefault extends JFile {	
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param path
	 */
	public JFileDefault(String path) {
		super(path);
	}


	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#bytes()
	 */
	public byte[] bytes() throws Exception{
		return JUtilInputStream.bytes(new FileInputStream(this));
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
		try{
	    	if(exists()){
	    		
	        	if(encoding!=null) return JUtilInputStream.string(new FileInputStream(this),encoding);
	        	else return JUtilInputStream.string(new FileInputStream(this));
	    	}else{
	    		return null;
	    	}
		}catch(Exception e){
			return null;
		}
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#save(java.lang.String, boolean)
	 */
	public void save(String content,boolean append)throws Exception{
		save(content,append,null);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#save(java.lang.String, boolean, java.lang.String)
	 */
	public void save(String content,boolean append,String encoding)throws Exception{
		this.getParentFile().mkdirs();
		
		Writer writer=null;
		if(encoding!=null) writer=new OutputStreamWriter(new FileOutputStream(this,append),encoding);
		else writer=new OutputStreamWriter(new FileOutputStream(this,append));
		writer.write(content);
		writer.flush();
		
		try{
			writer.close();
		}catch(Exception e){}
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#save(byte[])
	 */
	public void save(byte[] bytes)throws Exception{
		save(new ByteArrayInputStream(bytes));
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.infrastructure.fs.JFile#save(java.io.InputStream)
	 */
	public void save(InputStream is)throws Exception{
		if(exists()) delete();
		else getParentFile().mkdirs();
		
		OutputStream os=new FileOutputStream(this);
		
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
	}


	/*
	 *  (non-Javadoc)
	 * @see j.fs.JFile#save(java.io.File)
	 */
	public void save(File file) throws Exception {
		save(new FileInputStream(file));
	}
}
