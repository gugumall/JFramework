package j.test;

import java.io.File;

import j.util.JUtilString;

/**
 * 
 * @author 肖炯
 *
 */
public class Temp {
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		File dir=new File("F:\\images\\时光组\\temp");
		File[] fs=dir.listFiles();
		for(int i=0;i<fs.length;i++){
			String name=fs[i].getName();
			if(name.startsWith(".")&&name.endsWith(".hwbk")) {
				String p=JUtilString.replaceAll(fs[i].getAbsolutePath(), name, name.substring(1,name.length()-5));
				
				File to=new File(p);
				
				fs[i].renameTo(to);
				
				System.out.println(name);
			}
		}
	}
}
