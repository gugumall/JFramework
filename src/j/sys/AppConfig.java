package j.sys;


import j.app.sso.User;
import j.common.JProperties;
import j.log.Logger;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilMD5;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author 肖炯
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
	private static AppConfig _instance=null;//全局实例
	private static ConcurrentMap _instances=new ConcurrentMap();//针对每个用户的实例
	private static ConcurrentMap lastUpds=new ConcurrentMap();

	private ConcurrentMap groups=new ConcurrentMap();//参数分组
	private ConcurrentMap params=new ConcurrentMap();//全部参数
	private long latestUsed=0;//最近使用使用时间
	private boolean loaded=false;//是否已加载
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
	 * 返回组名为group的全部参数的List，每个参数以KeyValue对象返回，key为参数名，value为参数值
	 * @param group
	 * @return
	 */
	public static List getParas(String group){
		AppConfig instance=getInstance();
		if(instance==null||instance.params==null) return null;
		List lst=new LinkedList();
		List keys=instance.params.listKeys();
		for(int i=0;i<keys.size();i++){
			String key=(String)keys.get(i);
			if(key.startsWith(group+"*")){
				AppPara bean=(AppPara)instance.params.get(key);
				lst.add(bean);
			}
		}
		return lst;
	}
	
	/**
	 * 得到某个参数的值
	 * 
	 * @param group
	 * @param paraName
	 * @return
	 */
	public static String getPara(String group,String paraName){
		if(group==null||paraName==null){
			return null;
		}
		
		AppConfig instance=getInstance();
		
		String key=group+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			return bean.getValue().toString();
		}else{
			return null;
		}
	}
	
	/**
	 * 设置参数值
	 * @param group
	 * @param paraName
	 * @param value
	 */
	public static void setPara(String group,String paraName,String value){
		setParaInCertainFile(group,paraName,value,"para.xml");
	}
	
	/**
	 * 设置参数值
	 * @param group
	 * @param paraName
	 * @param value
	 * @param desc
	 * @param placeholder
	 */
	public static void setPara(String group,String paraName,String value,String desc){
		setParaInCertainFile(group,paraName,value,desc,"para.xml");
	}
	
	/**
	 * 
	 * @param group
	 * @param paraName
	 * @param value
	 * @param desc
	 * @param placeholder
	 * @throws Exception
	 */
	public static void setPara(String group,String paraName,String value,String desc,String placeholder) throws Exception{
		setParaInCertainFile(group,paraName,value,desc,placeholder,"");
	}
	

	
	/**
	 * 设置参数值
	 * @param group
	 * @param paraName
	 * @param value
	 */
	public static void setParaInCertainFile(String group,String paraName,String value,String fileName){
		if(group==null||paraName==null||value==null){
			return;
		}
		AppConfig instance=getInstance();
		if(!instance.groups.containsKey(group)){
			instance.groups.put(group,new AppParaGroup(group,""));
		}
		String key=group+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			bean.setValue(value);
		}else{			
			instance.params.put(key,new AppPara(paraName,value,"","",true,fileName));
		}
	}
	
	/**
	 * 设置参数值
	 * @param group
	 * @param paraName
	 * @param value
	 * @param desc
	 * @param placeholder
	 * @param fileName
	 */
	public static void setParaInCertainFile(String group,String paraName,String value,String desc,String fileName){
		if(group==null||paraName==null||value==null){
			return;
		}
		AppConfig instance=getInstance();
		if(!instance.groups.containsKey(group)){
			instance.groups.put(group,new AppParaGroup(group,""));
		}
		String key=group+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			bean.setValue(value);
		}else{			
			instance.params.put(key,new AppPara(paraName,value,desc,"",true,fileName));
		}
	}		
	
	/**
	 * 
	 * @param group
	 * @param paraName
	 * @param value
	 * @param desc
	 * @param placeholder
	 * @param fileName
	 * @throws Exception
	 */
	public static void setParaInCertainFile(String group,String paraName,String value,String desc,String placeholder,String fileName) throws Exception{
		if(group==null||paraName==null||value==null){
			return;
		}
		AppConfig instance=getInstance();
		if(!instance.groups.containsKey(group)){
			instance.groups.put(group,new AppParaGroup(group,""));
		}
		String key=group+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			bean.setValue(value);
		}else{			
			instance.params.put(key,new AppPara(paraName,value,desc,placeholder,true,fileName));
		}
	}
	
	/**
	 * 设置参数值
	 * @param group
	 * @param paraName
	 * @param value
	 */
	public static void removePara(String group,String paraName){
		if(group==null||paraName==null){
			return;
		}
		AppConfig instance=getInstance();
		instance.params.remove(group+"*"+paraName);
	}		
	
	///////////////////针对每个域名的设置///////////////////////////
	/**
	 * 
	 * @param group
	 * @return
	 */
	public static AppParaGroup getGroupX(String domain,String group){
		AppConfig instance=getInstance();
		AppParaGroup _group=(AppParaGroup)instance.groups.get(group+"."+domain);
		if(_group==null) return getGroup(group);
		else return _group;
	} 
	
	/**
	 * 返回组名为group的全部参数的List，每个参数以KeyValue对象返回，key为参数名，value为参数值
	 * @param group
	 * @return
	 */
	public static List getParasX(String domain,String group){
		AppConfig instance=getInstance();
		if(instance==null||instance.params==null) return null;
		List lst=new LinkedList();
		List keys=instance.params.listKeys();
		for(int i=0;i<keys.size();i++){
			String key=(String)keys.get(i);
			if(key.startsWith(group+"."+domain+"*")){
				AppPara bean=(AppPara)instance.params.get(key);
				lst.add(bean);
			}
		}
		
		if(lst.isEmpty()) return getParas(group);
		else return lst;
	}
	
	/**
	 * 得到某个参数的值
	 * 
	 * @param group
	 * @param paraName
	 * @return
	 */
	public static String getParaX(String domain,String group,String paraName){
		if(group==null||paraName==null){
			return null;
		}
		
		AppConfig instance=getInstance();
		
		String key=group+"."+domain+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			return bean.getValue().toString();
		}else{
			return getPara(group,paraName);
		}
	}
	
	/**
	 * 设置参数值
	 * @param group
	 * @param paraName
	 * @param value
	 */
	public static void setParaX(String domain,String group,String paraName,String value){
		setParaInCertainFileX(domain,group,paraName,value,"para.xml");
	}
	
	/**
	 * 设置参数值
	 * @param group
	 * @param paraName
	 * @param value
	 * @param desc
	 * @param placeholder
	 */
	public static void setParaX(String domain,String group,String paraName,String value,String desc){
		setParaInCertainFileX(domain,group,paraName,value,desc,"para.xml");
	}
	
	/**
	 * 
	 * @param group
	 * @param paraName
	 * @param value
	 * @param desc
	 * @param placeholder
	 * @throws Exception
	 */
	public static void setParaX(String domain,String group,String paraName,String value,String desc,String placeholder) throws Exception{
		setParaInCertainFileX(domain,group,paraName,value,desc,placeholder,"para.xml");
	}
	

	
	/**
	 * 设置参数值
	 * @param group
	 * @param paraName
	 * @param value
	 */
	public static void setParaInCertainFileX(String domain,String group,String paraName,String value,String fileName){
		if(group==null||paraName==null||value==null){
			return;
		}
		
		AppConfig instance=getInstance();
		AppPara beanDefault=(AppPara)instance.params.get(group+"*"+paraName);
		AppParaGroup groupDefault=(AppParaGroup)instance.groups.get(group);
		
		group+="."+domain;
		
		if(!instance.groups.containsKey(group)){
			instance.groups.put(group,new AppParaGroup(group,groupDefault==null?"":groupDefault.getDesc()));
		}
		String key=group+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			bean.setValue(value);
		}else{		
			
			instance.params.put(key,new AppPara(paraName,value,beanDefault==null?"":beanDefault.getDesc(),"",true,fileName));
		}
	}
	
	/**
	 * 设置参数值
	 * @param group
	 * @param paraName
	 * @param value
	 * @param desc
	 * @param placeholder
	 * @param fileName
	 */
	public static void setParaInCertainFileX(String domain,String group,String paraName,String value,String desc,String fileName){
		if(group==null||paraName==null||value==null){
			return;
		}
		
		AppConfig instance=getInstance();
		AppPara beanDefault=(AppPara)instance.params.get(group+"*"+paraName);
		AppParaGroup groupDefault=(AppParaGroup)instance.groups.get(group);
		
		group+="."+domain;
		
		if(!instance.groups.containsKey(group)){
			instance.groups.put(group,new AppParaGroup(group,groupDefault==null?"":groupDefault.getDesc()));
		}
		String key=group+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			bean.setValue(value);
		}else{			
			instance.params.put(key,new AppPara(paraName,value,desc,"",true,fileName));
		}
	}		
	
	/**
	 * 
	 * @param group
	 * @param paraName
	 * @param value
	 * @param desc
	 * @param placeholder
	 * @param fileName
	 * @throws Exception
	 */
	public static void setParaInCertainFileX(String domain,String group,String paraName,String value,String desc,String placeholder,String fileName) throws Exception{
		if(group==null||paraName==null||value==null){
			return;
		}
		
		AppConfig instance=getInstance();
		AppPara beanDefault=(AppPara)instance.params.get(group+"*"+paraName);
		AppParaGroup groupDefault=(AppParaGroup)instance.groups.get(group);
		
		group+="."+domain;
		
		if(!instance.groups.containsKey(group)){
			instance.groups.put(group,new AppParaGroup(group,groupDefault==null?"":groupDefault.getDesc()));
		}
		String key=group+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			bean.setValue(value);
		}else{			
			instance.params.put(key,new AppPara(paraName,value,desc,placeholder,true,fileName));
		}
	}
	
	/**
	 * 
	 * @param domain
	 * @param group
	 * @param paraName
	 */
	public static void removePara(String domain,String group,String paraName){
		if(group==null||paraName==null){
			return;
		}
		AppConfig instance=getInstance();
		instance.params.remove(group+"."+domain+"*"+paraName);
	}		
	///////////////////针对每个域名的设置 end////////////////////////
	
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
		
		JUtilDom4j.save(doc, JProperties.getConfigPath()+flieName, "utf-8");
		File file=new File(JProperties.getConfigPath()+flieName);
		lastUpds.put(file.getName(),new Long(file.lastModified()));
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	private static void load()throws Exception{
		synchronized(lock){
			if(JProperties.getConfigPath()==null) return;
			File dir = new File(JProperties.getConfigPath());
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
        		String paraPlaceholder=para.attributeValue("placeholder");
        		boolean canBeUpdated=!"false".equals(para.attributeValue("can-be-updated"));
        		String key=appName+"*"+paraName;
        		instance.params.put(key,new AppPara(paraName,
        				paraValue,
        				paraDesc==null?"":paraDesc,
        				paraPlaceholder==null?"":paraPlaceholder,
        				canBeUpdated,
        				file.getName()));
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
				clearIdles();
			}catch(Exception e){
				log.log(e,Logger.LEVEL_FATAL);
			}
			
			try{
				load();
			}catch(Exception e){
				log.log(e,Logger.LEVEL_FATAL);
			}
		}
	}
	
	//////////////////针对每个用户的配置////////////////////////////////////////////
	
	private static void clearIdles(){
		List instances=_instances.listKeys();
		for(int i=0;i<instances.size();i++){
			String userId=(String)instances.get(i);
			
			AppConfig instance=(AppConfig)_instances.get(userId);
			if(instance==null){
				_instances.remove(userId);
			}else if(instance.idle()){
				_instances.remove(userId);
				instance.clear();
				instance=null;
			}
		}
	}
	
	/**
	 * 
	 */
	private void clear(){
		this.groups.clear();
		this.groups=null;
		this.params.clear();
		this.params=null;
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean idle(){
		return SysUtil.getNow()-this.latestUsed>300000L;
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private static AppConfig getInstance(User user) throws Exception{
		if(user==null||user.getUserId()==null||"".equals(user.getUserId())){
			throw new Exception("invalid user");
		}
		
		AppConfig instance=null;
		if(_instances.containsKey(user.getUserId())){
			instance=(AppConfig)_instances.get(user.getUserId());
		}else{
			instance=new AppConfig();
		}
		instance.latestUsed=SysUtil.getNow();
		
		_instances.put(user.getUserId(),instance);
		
		return instance;
	}
	
	/**
	 * 
	 * @param user
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static AppParaGroup getGroup(User user,String group) throws Exception{
		AppConfig instance=getInstance(user);
		if(!instance.loaded) AppConfig.load(user);
		
		return (AppParaGroup)instance.groups.get(group);
	}
	
	/**
	 * 返回组名为group的全部参数的List，每个参数以KeyValue对象返回，key为参数名，value为参数值
	 * @param user
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static List getParas(User user,String group) throws Exception{
		AppConfig instance=getInstance(user);
		if(!instance.loaded) AppConfig.load(user);
		
		List lst=new LinkedList();
		List keys=instance.params.listKeys();
		for(int i=0;i<keys.size();i++){
			String key=(String)keys.get(i);
			if(key.startsWith(group+"*")){
				AppPara bean=(AppPara)instance.params.get(key);
				lst.add(bean);
			}
		}
		return lst;
	}
	
	/**
	 * 得到某个参数的值
	 * @param user
	 * @param group
	 * @param paraName
	 * @return
	 * @throws Exception
	 */
	public static String getPara(User user,String group,String paraName) throws Exception{
		if(group==null||paraName==null){
			return null;
		}
		
		AppConfig instance=getInstance(user);
		if(!instance.loaded) AppConfig.load(user);
		
		String key=group+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			return bean.getValue().toString();
		}else{
			return null;
		}
	}
	
	/**
	 * 
	 * @param user
	 * @param group
	 * @param paraName
	 * @param value
	 * @throws Exception
	 */
	public static void setPara(User user,String group,String paraName,String value) throws Exception{
		setParaInCertainFile(user,group,paraName,value,"");
	}
	
	/**
	 * 
	 * @param user
	 * @param group
	 * @param paraName
	 * @param value
	 * @param desc
	 * @throws Exception
	 */
	public static void setPara(User user,String group,String paraName,String value,String desc) throws Exception{
		setParaInCertainFile(user,group,paraName,value,desc,"");
	}
	
	/**
	 * 
	 * @param user
	 * @param group
	 * @param paraName
	 * @param value
	 * @param desc
	 * @param placeholder
	 * @throws Exception
	 */
	public static void setPara(User user,String group,String paraName,String value,String desc,String placeholder) throws Exception{
		setParaInCertainFile(user,group,paraName,value,desc,placeholder,"");
	}
	
	/**
	 * 
	 * @param user
	 * @param group
	 * @param paraName
	 * @param value
	 * @param fileName
	 * @throws Exception
	 */
	public static void setParaInCertainFile(User user,String group,String paraName,String value,String fileName) throws Exception{
		if(group==null||paraName==null||value==null){
			return;
		}
		AppConfig instance=getInstance(user);
		if(!instance.loaded) AppConfig.load(user);
		
		if(!instance.groups.containsKey(group)){
			instance.groups.put(group,new AppParaGroup(group,""));
		}
		String key=group+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			bean.setValue(value);
		}else{			
			instance.params.put(key,new AppPara(paraName,value,"","",true,fileName));
		}
		_instances.put(user.getUserId(),instance);
	}	
	
	/**
	 * 
	 * @param user
	 * @param group
	 * @param paraName
	 * @param value
	 * @param desc
	 * @param fileName
	 * @throws Exception
	 */
	public static void setParaInCertainFile(User user,String group,String paraName,String value,String desc,String fileName) throws Exception{
		if(group==null||paraName==null||value==null){
			return;
		}
		AppConfig instance=getInstance(user);
		if(!instance.loaded) AppConfig.load(user);
		
		if(!instance.groups.containsKey(group)){
			instance.groups.put(group,new AppParaGroup(group,""));
		}
		String key=group+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			bean.setValue(value);
		}else{			
			instance.params.put(key,new AppPara(paraName,value,desc,"",true,fileName));
		}
		_instances.put(user.getUserId(),instance);
	}
	
	/**
	 * 
	 * @param user
	 * @param group
	 * @param paraName
	 * @param value
	 * @param desc
	 * @param placeholder
	 * @param fileName
	 * @throws Exception
	 */
	public static void setParaInCertainFile(User user,String group,String paraName,String value,String desc,String placeholder,String fileName) throws Exception{
		if(group==null||paraName==null||value==null){
			return;
		}
		AppConfig instance=getInstance(user);
		if(!instance.loaded) AppConfig.load(user);
		
		if(!instance.groups.containsKey(group)){
			instance.groups.put(group,new AppParaGroup(group,""));
		}
		String key=group+"*"+paraName;
		if(instance.params.containsKey(key)){
			AppPara bean=(AppPara)instance.params.get(key);
			bean.setValue(value);
		}else{			
			instance.params.put(key,new AppPara(paraName,value,desc,placeholder,true,fileName));
		}
		_instances.put(user.getUserId(),instance);
	}
	
	/**
	 * 
	 * @param user
	 * @param group
	 * @param paraName
	 * @throws Exception
	 */
	public static void removePara(User user,String group,String paraName) throws Exception{
		if(group==null||paraName==null){
			return;
		}
		AppConfig instance=getInstance(user);
		if(!instance.loaded) AppConfig.load(user);
		
		instance.params.remove(group+"*"+paraName);
		_instances.put(user.getUserId(),instance);
	}		
	
	/**
	 * 
	 * @throws Exception
	 */
	public static void save(User user)throws Exception{
		if(JProperties.getWebRoot()==null) return;
		
		AppConfig instance=AppConfig.getInstance(user);
		if(!instance.loaded) AppConfig.load(user);;
		
		synchronized(user.getUserId().intern()){	
			
			String mask=JUtilMD5.MD5EncodeToHex(user.getUserId());
			String config=JProperties.getWebRoot()
					+"WEB-INF"
					+File.separator
					+"config"
					+File.separator
					+mask.substring(0,2)
					+File.separator
					+mask.substring(2,4)
					+File.separator
					+mask.substring(4,6);
			
			File configFile=new File(config);
			configFile.mkdirs();
			
			config+=File.separator;
			config+=mask;
			config+=".xml";
	
			log.log("save paras of user:"+user.getUserId()+","+config,-1);
			
			Document doc=DocumentHelper.createDocument();
			Element root=doc.addElement("root");
			
			List keys=instance.params.listKeys();
			
			List _groups=instance.groups.listValues();
			for(int i=0;i<_groups.size();i++){
				AppParaGroup group=(AppParaGroup)_groups.get(i);
				
				boolean groupIn=false;
				for(int j=0;j<keys.size();j++){
					String key=(String)keys.get(j);
					if(key.startsWith(group.getName()+"*")){						
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
			
			JUtilDom4j.save(doc, config, "utf-8");
			doc=null;
		}
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	public static void load(User user)throws Exception{
		synchronized(user.getUserId().intern()){
			if(JProperties.getWebRoot()==null) return;
			
			String mask=JUtilMD5.MD5EncodeToHex(user.getUserId());
			String config=JProperties.getWebRoot()
					+"WEB-INF"
					+File.separator
					+"config"
					+File.separator
					+mask.substring(0,2)
					+File.separator
					+mask.substring(2,4)
					+File.separator
					+mask.substring(4,6)
					+File.separator
					+mask
					+".xml";
			
			File configFile = new File(config);			
			loadInstance(user,configFile);
		}
	}
	
	/**
	 * 
	 * @param instance
	 * @param file
	 * @throws Exception
	 */
	private static void loadInstance(User user,File file) throws Exception{
		log.log("loading parameters of application from file: "+file.getAbsolutePath(),-1);
		
		AppConfig instance=AppConfig.getInstance(user);
		
		if(!file.exists()){
			instance.loaded=true;
			_instances.put(user.getUserId(),instance);
			return;
		}
	
		//create dom document
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new FileInputStream(file),"UTF-8");
		Element root = doc.getRootElement();
        //create dom document end

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
        		String paraPlaceholder=para.attributeValue("placeholder");
        		boolean canBeUpdated=!"false".equals(para.attributeValue("can-be-updated"));
        		String key=appName+"*"+paraName;
        		instance.params.put(key,new AppPara(paraName,
        				paraValue,
        				paraDesc==null?"":paraDesc,
        				paraPlaceholder==null?"":paraPlaceholder,
        				canBeUpdated,
        				file.getName()));
        	}
        }
        
		instance.loaded=true;
		_instances.put(user.getUserId(),instance);
	}
}
