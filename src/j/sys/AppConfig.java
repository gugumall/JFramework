package j.sys;


import j.log.Logger;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author JFramework
 *
 * 应用系统配置信息，用来装载配置文件app.xml中的信息
 * app.xml的格式如下：
 * <?xml version="1.0" encoding="GBK"?>
 *	<root>
 *		<group name="roar">
 *			<para name="DB">jdbc/roar</para>
 *		</group>
 *	<root>
 *	
 *	每个<group>项代表一组配置信息，可以有多个<group>项
 *	每个<group>项中，可以配置多个<para>项
 *	<para>项的name属性指定了该参数的名字，其文本(如上面的“jdbc/roar”)表示该参数的值
 *  可以用如下的方法得到某个参数的值：
 * 	String para=AppConfig.getPara("roar","DB")
 */
public class AppConfig implements Runnable{
	private static Logger log=Logger.create(AppConfig.class);
	private static AppConfig _instance=null;
	private static ConcurrentMap lastUpds=new ConcurrentMap();

	private ConcurrentMap groups=new ConcurrentMap();
	private ConcurrentMap params=new ConcurrentMap();//全部参数
	private static final Object lock=new Object();
	
	static{
		try{
			load();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_DEBUG);
		}
		
		AppConfig c=new AppConfig();
		Thread t=new Thread(c);
		t.start();
		log.log("AppConfig thread started.",-1);
	}
	
	/**
	 * 
	 * @param fileName
	 */
	private AppConfig(){
	}
	
	/**
	 * 
	 * @return
	 */
	private static AppConfig getInstance(){
		if(_instance==null) _instance=new AppConfig();
		return _instance;
	}
	
	/**
	 * 
	 * @param group
	 * @return
	 */
	public static AppParaGroup getGroup(String group){
		AppConfig instance=getInstance();
		return (AppParaGroup)instance.groups.get(group);
	}
	
	/**
	 * 返回组名为groupName的全部参数的List，每个参数以KeyValue对象返回，key为参数名，value为参数值
	 * @param groupName
	 * @return
	 */
	public static List getParas(String groupName){
		AppConfig instance=getInstance();
		if(instance==null||instance.params==null) return null;
		List lst=new LinkedList();
		List keys=instance.params.listKeys();
		for(int i=0;i<keys.size();i++){
			String key=(String)keys.get(i);
			if(key.startsWith(groupName+"*")){
				AppPara bean=(AppPara)instance.params.get(key);
				lst.add(bean);
			}
		}
		return lst;
	}
	
	/**
	 * 得到某个参数的值
	 * 
	 * @param groupName
	 * @param paraName
	 * @return
	 */
	public static String getPara(String groupName,String paraName){
		if(groupName==null||paraName==null){
			return null;
		}
		
		AppConfig instance=getInstance();
		
		String key=groupName+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			return bean.getValue().toString();
		}else{
			return null;
		}
	}
	
	/**
	 * 设置参数值
	 * @param saver 执行额外的保存任务，比如保存到数据库
	 * @param groupName
	 * @param paraName
	 * @param value
	 */
	public static void setPara(String groupName,String paraName,String value){
		setParaInCertainFile(groupName,paraName,value,"para.xml");
	}
	
	/**
	 * 设置参数值
	 * @param saver 执行额外的保存任务，比如保存到数据库
	 * @param groupName
	 * @param paraName
	 * @param value
	 */
	public static void setParaInCertainFile(String groupName,String paraName,String value,String fileName){
		if(groupName==null||paraName==null||value==null){
			return;
		}
		AppConfig instance=getInstance();
		if(!instance.groups.containsKey(groupName)){
			instance.groups.put(groupName,new AppParaGroup(groupName,""));
		}
		String key=groupName+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			bean.setValue(value);
		}else{			
			instance.params.put(key,new AppPara(paraName,value,"",true,fileName));
		}
	}	
	
	/**
	 * 设置参数值
	 * @param saver 执行额外的保存任务，比如保存到数据库
	 * @param groupName
	 * @param paraName
	 * @param value
	 */
	public static void removePara(String groupName,String paraName){
		if(groupName==null||paraName==null){
			return;
		}
		AppConfig instance=getInstance();
		instance.params.remove(groupName+"*"+paraName);
	}		
	
	/**
	 * 
	 * @throws Exception
	 */
	public static void save()throws Exception{
		synchronized(lock){
			List files=new LinkedList();
			AppConfig instance=AppConfig.getInstance();
			List values=instance.params.listValues();
			for(int i=0;i<values.size();i++){
				AppPara obj=(AppPara)values.get(i);
				if(!files.contains(obj.getFileName())){
					files.add(obj.getFileName());
				}
			}
			
			for(int i=0;i<files.size();i++){
				save((String)files.get(i));
			}
		}
	}
	
	/**
	 * 
	 * @param flieName
	 * @throws Exception
	 */
	private static void save(String flieName)throws Exception{
		Document doc=DocumentHelper.createDocument();
		Element root=doc.addElement("root");
		
		AppConfig instance=AppConfig.getInstance();
		List keys=instance.params.listKeys();
		
		List _groups=instance.groups.listValues();
		for(int i=0;i<_groups.size();i++){
			AppParaGroup group=(AppParaGroup)_groups.get(i);
			
			boolean groupIn=false;
			for(int j=0;j<keys.size();j++){
				String key=(String)keys.get(j);
				if(key.startsWith(group.getName()+"*")){
					AppPara obj=(AppPara)instance.params.get(key);
					
					if(obj.getFileName()==null&&!"para.xml".equals(flieName)) continue;
					
					if(obj.getFileName()!=null&&!obj.getFileName().equals(flieName)) continue;
					
					groupIn=true;
					break;
				}
			}
			
			if(!groupIn) continue;
			
			Element app=root.addElement("group");
			app.addAttribute("name",group.getName());
			app.addAttribute("desc",group.getDesc());
			for(int j=0;j<keys.size();j++){
				String key=(String)keys.get(j);
				if(key.startsWith(group.getName()+"*")){
					AppPara obj=(AppPara)instance.params.get(key);
					
					if(obj.getFileName()==null&&!"para.xml".equals(flieName)) continue;
					
					if(obj.getFileName()!=null&&!obj.getFileName().equals(flieName)) continue;
					
					Element para=app.addElement("para");
					para.addAttribute("name",obj.getKey().toString());
					para.addAttribute("desc",obj.getDesc().toString());
					para.addAttribute("can-be-updated",obj.getCanBeUpdated()?"true":"false");
					para.setText(obj.getValue().toString());
				}
			}
		}
		keys.clear();
		keys=null;
		
		JUtilDom4j.save(doc, j.Properties.getConfigPath()+flieName, "utf-8");
		File file=new File(j.Properties.getConfigPath()+flieName);
		lastUpds.put(file.getName(),new Long(file.lastModified()));
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	private static void load()throws Exception{
		synchronized(lock){
			if(j.Properties.getConfigPath()==null) return;
			File dir = new File(j.Properties.getConfigPath());
			File[] files=dir.listFiles();
			for(int i=0;files!=null&&i<files.length;i++){
				String fname=files[i].getName();
				
				if(fname.startsWith("para.")
						&&fname.endsWith(".xml")){
					loadInstance(files[i]);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param instance
	 * @param file
	 * @throws Exception
	 */
	private static void loadInstance(File file) throws Exception{
		if(lastUpds.containsKey(file.getName())){
			long lastUpd=((Long)lastUpds.get(file.getName())).longValue();
			if(lastUpd==file.lastModified()){//未修改
				return;
			}else{
				log.log("file "+file.getAbsolutePath()+" has been modified, so reload it.",-1);
			}
		}		
		lastUpds.put(file.getName(),new Long(file.lastModified()));
		
		log.log("loading parameters of application from file: "+file.getAbsolutePath(),-1);
	
		//create dom document
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new FileInputStream(file),"UTF-8");
		Element root = doc.getRootElement();
        //create dom document end

		AppConfig instance=AppConfig.getInstance();
		//instance.params.clear();
		//instance.groups.clear();
      	 
        List appElements=root.elements("group");
        for(int i=0;appElements!=null&&i<appElements.size();i++){
        	Element app=(Element)appElements.get(i);
        	String appName=app.attributeValue("name");
        	String appDesc=app.attributeValue("desc");
        	instance.groups.put(appName,new AppParaGroup(appName,appDesc));
        	List paraElements=app.elements("para");
        	for(int j=0;paraElements!=null&&j<paraElements.size();j++){
        		Element para=(Element)paraElements.get(j);
        		String paraName=para.attributeValue("name");
        		String paraValue=para.getTextTrim();
        		String paraDesc=para.attributeValue("desc");
        		boolean canBeUpdated=!"false".equals(para.attributeValue("can-be-updated"));
        		String key=appName+"*"+paraName;
        		instance.params.put(key,new AppPara(paraName,paraValue,paraDesc==null?"":paraDesc,canBeUpdated,file.getName()));
        	}
        }
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			try{
				load();
			}catch(Exception e){
				log.log(e,Logger.LEVEL_FATAL);
			}
		}
	}
}
