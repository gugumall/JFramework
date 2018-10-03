package j.tool;

import j.fs.JDFSFile;
import j.util.JUtilString;

import java.io.File;

/**
 * 
 * @author 肖炯
 *
 */
public class FileReplace {
	private static int count=0;
	
	/**
	 * 		
	 */
	public FileReplace() {
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
//		File dir=new File("F:\\work\\JShop_v2.0");
//		count(dir,
//				"\r\n",
//				".java");
//		
//		count(dir,
//				"\r\n",
//				".jsp");
		
		System.out.println(count);
		
		File dir=new File("F:\\work\\JShop_v2.1\\WebContent\\WEB-INF\\pages\\deposit");
		replace(dir,"'订单不存在'","'I{fund,订单不存在}'",".jsp");
		replace(dir,"'非法请求'","'I{fund,非法请求}'",".jsp");
		replace(dir,"'充值成功'","'I{fund,充值成功}'",".jsp");
	}
	
	/**
	 * 
	 * @param file
	 * @param src
	 * @param alt
	 */
	private static void replace(File file,String src,String alt,String ext){
		if(file.isDirectory()){
			File[] fs=file.listFiles();
			for(int i=0;i<fs.length;i++){
				replace(fs[i],src,alt,ext);
			}
		}else{
			if(!file.getName().endsWith(ext)) return;
			
			String s=JDFSFile.read(file, "UTF-8");
			if(s.indexOf(src)>-1){
				
				s=JUtilString.replaceAll(s, src, alt);
				
				JDFSFile.saveString(file.getAbsolutePath(), s, false, "UTF-8");
				
				System.out.println(file.getAbsolutePath());
			}
		}		
	}
	
	/**
	 * 
	 * @param file
	 * @param src
	 */
	private static void count(File file,String src,String ext){
		if(file.isDirectory()){
			File[] fs=file.listFiles();
			for(int i=0;i<fs.length;i++){
				count(fs[i],src,ext);
			}
		}else{
			if(!file.getName().endsWith(ext)) return;
			
			System.out.println(file.getAbsolutePath()+","+count);
			
			String s=JDFSFile.read(file, "UTF-8");
			
			int index=s.indexOf(src);
			while(index>-1){
				count++;
				index=s.indexOf(src,index+src.length());
			}
		}		
	}
}
