package j.test;

import j.util.JUtilInputStream;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TestJar {
	public static void main(String[] args) throws Exception{
		JarFile jar=new JarFile("D:\\tomcat\\webapps\\jme\\WEB-INF\\lib\\j.jar");
		Enumeration es=jar.entries();
		while(es.hasMoreElements()){
			JarEntry en=(JarEntry)es.nextElement();
			if(!en.isDirectory()&&en.getName().indexOf("log")>0){
				System.out.println(en.getName()+","+en.isDirectory()+","+en.getTime());
				byte[] bs=JUtilInputStream.bytes(jar.getInputStream(en));
				System.out.println("jar file size " + en.getName() + " "+bs.length);
			}
		}
	}
}
