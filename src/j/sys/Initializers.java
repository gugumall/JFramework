package j.sys;

import j.util.JUtilInputStream;
import j.util.JUtilMath;
import j.util.JUtilTimestamp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServlet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * @author 肖炯 
 *
 */
public class Initializers extends HttpServlet{	
	private static final long serialVersionUID = 1L;
	private static List initialCommands=new LinkedList();
	private static List initializersClasses=new LinkedList();
	
	static{
		try{					
			File file = new File(j.Properties.getConfigPath()+"sys.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			//create jdom document
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new FileInputStream(file),"UTF-8");
			Element root = doc.getRootElement();
	        //create jdom document ends
			
			List commands=root.selectNodes("//Initializers/command");
			for(int i=0;commands!=null&&i<commands.size();i++){
				Element ele=(Element)commands.get(i);
				initialCommands.add(new String[]{ele.getTextTrim(),ele.attributeValue("retries")});
			}
			
			List initializers=root.selectNodes("//Initializers/Initializer");
			for(int i=0;initializers!=null&&i<initializers.size();i++){
				Element ele=(Element)initializers.get(i);
				initializersClasses.add(ele.attributeValue("init-handler"));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param os
	 * @param runtime
	 * @param command
	 * @return
	 */
	private static boolean executeCommand(String os,Runtime runtime,String command){
		try{				
			System.out.println(JUtilTimestamp.timestamp()+" try to execute command: "+command);
			
			Process process = null;
			if(os!=null&&os.indexOf("Windows")>-1) process=runtime.exec(command+"\r\n");//调用系统命令	
			else process=runtime.exec(command);//调用系统命令
			
			Thread.sleep(5000);
			
			InputStream errInputstream=process.getErrorStream();
			if(errInputstream.available()>0){
				byte[] bytes=JUtilInputStream.bytes(errInputstream);
				String err=new String(bytes);	
				//String err=JUtilBytes.readString(errInputstream);			
				System.out.println(JUtilTimestamp.timestamp()+" "+command+" - "+err);
				return false;
			}else{
				System.out.println(JUtilTimestamp.timestamp()+" command - "+command+" has been executed successfully.");
				return true;
			}
		}catch(Exception ex){				
			System.out.println(JUtilTimestamp.timestamp()+" errors occurred while executing command: "+command);
			ex.printStackTrace();
			
			return false;
		}
	}
	
	/**
	 * 初始化
	 *
	 */
	private static void initialize(){
		try{
			String os=System.getProperty("os.name");
			System.out.println("os - "+os);
			
			Runtime runtime = Runtime.getRuntime(); 		
			for(int i=0;i<initialCommands.size();i++){
				String[] command=(String[])initialCommands.get(i);
				
				//最多重试次数
				int retries=1;
				if(JUtilMath.isInt(command[1])) retries=Integer.parseInt(command[1]);
	
				//已经尝试执行次数
				int hasRetried=1;
				
				//第一次执行
				boolean success=executeCommand(os,runtime,command[0]);
				while(!success&&hasRetried<retries){
					try{
						Thread.sleep(10000);
					}catch(Exception ex){}
					hasRetried++;
					success=executeCommand(os,runtime,command[0]);
				}
			}
			
			for(int i=0;i<initializersClasses.size();i++){
				try{
					String cls=(String)initializersClasses.get(i);
					Initializer init=(Initializer)Class.forName(cls).newInstance();
					System.out.println("初始化 - "+init.getClass().getName());
					init.initialization();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}	

	/*
	 *  (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init(){
		System.out.println("系统初始化....这可能需要一段时间");
		initialize();
		System.out.println("系统初始化系统完毕.");
	}
}
