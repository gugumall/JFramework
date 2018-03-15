package j.fs.big;

import j.fs.JDFSFile;
import j.util.JUtilTextWriter;

import java.io.File;

public class Temp {
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		System.out.println("start");
		String s=JDFSFile.read(new File("D:\\tomcat\\logs\\tomcat8-stderr.2014-04-29.log"));
		
		JUtilTextWriter w=new JUtilTextWriter(new File("f:\\temp\\test\\in.log"),"utf-8");
		
		for(int i=0;i<2000;i++){
			w.add(s);
			w.flush();
		}
		w.close();
		System.out.println("end");
	}
}
