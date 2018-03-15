/*
 * Created on 2005-4-25
 *
 */
package j.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * @author JFramework
 *
 */
public class JUtilTextWriter {
	private File destFile;
	private Writer writer;
	
	/**
	 * 
	 * @param file
	 * @param encode 字符编码
	 * @throws Exception
	 */
	public JUtilTextWriter(File file,String encode)throws Exception{
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		if(!file.exists()){
			file.createNewFile();
		}
		destFile=file;
		if(encode!=null){
			writer = new OutputStreamWriter(new FileOutputStream(destFile, true), encode);
		}else{
			writer = new OutputStreamWriter(new FileOutputStream(destFile, true));
		}
	}
	
	/**
	 * 写入一行，不添加回车换行
	 * @param logTxt
	 * @throws Exception
	 */
	public void add(String logTxt)throws Exception{
		writer.write(logTxt);
		writer.flush();
	}
	
	/**
	 * 写入一行，自动加回车换行
	 * @param logTxt
	 * @throws Exception
	 */
	public void addLine(String logTxt)throws Exception{
		writer.write(logTxt+"\r\n");
		writer.flush();
	}
	
	/**
	 * 写入一行，不添加回车换行
	 * @param logTxt
	 * @throws Exception
	 */
	public void add2Buffer(String logTxt)throws Exception{
		writer.write(logTxt);
	}
	
	/**
	 * 写入一行，自动加回车换行
	 * @param logTxt
	 * @throws Exception
	 */
	public void addLine2Buffer(String logTxt)throws Exception{
		writer.write(logTxt+"\r\n");
	}
	
	/**
	 * flush to disk
	 * @throws Exception
	 */
	public void flush()throws Exception{
		writer.flush();
	}
	
	/**
	 * 关闭io
	 * @throws Exception
	 */
	public void close()throws Exception{
		writer.flush();
		writer.close();
	}
	
	/**
	 * test
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		JUtilTextWriter l=new JUtilTextWriter(new File("e:/log.txt"),"utf-8");
		l.addLine("我是英雄");
		l.close();
	}
}
