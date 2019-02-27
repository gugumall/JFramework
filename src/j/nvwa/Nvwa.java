package j.nvwa;

import j.common.JProperties;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilTimestamp;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 加载类配置信息，并提供获取实例的接口
 * @author 肖炯
 *
 */
public class Nvwa implements Runnable {	
	private static ConcurrentMap<String,NvwaObject> objects=new ConcurrentMap();//key：类编码   value：j.nvwa.NvwaObject
	private static ConcurrentList implementations=new ConcurrentList();//所有实现类的列表
	private static ConcurrentList classLoaderResponsibleFor=new ConcurrentList();//哪些类由自定义ClassLoader加载
	private static ConcurrentMap entrusts=new ConcurrentMap();//托管类
	private static ConcurrentList entrustClassNames=new ConcurrentList();//托管类类名列表
	private static String customClassLoaderName=null;//自定义类加载器类名
	private static NvwaClassLoader customClassLoader=null;//自定义类加载器
	private static ConcurrentMap<String,Long> configLastModified=new ConcurrentMap();//配置文件上次修改时间, key:文件名，value:修改时间
	private static volatile boolean loading=true;
	protected static ClassLoader defaultClassLoader=Thread.currentThread().getContextClassLoader();
	
	
	static{
		load();
		startMonitor();
	}
	
	/**
	 * 代管className类的加载
	 * @param code
	 * @param className
	 * @param singleton
	 */
	public static NvwaObject entrust(String code,String className,boolean singleton){
		if(code!=null&&!code.equals("")&&objects.containsKey(code)) return (NvwaObject)objects.get(code);//已经有配置，不能托管
		
		if(!entrustClassNames.contains(className)) entrustClassNames.add(className);

		String key=(code!=null&&!code.equals(""))?code:(className+singleton);
		if(!entrusts.containsKey(key)){
			NvwaObject obj=new NvwaObject();
			obj.setCode(code);
			obj.setImplementation(className);
			obj.setSingleton(singleton);
			entrusts.put(key,obj);
			
			return obj;
		}else{			
			NvwaObject obj=(NvwaObject)entrusts.get(key);
			if(!obj.getImplementation().equals(className)
					||obj.getSingleton()!=singleton){
				obj.setImplementation(className);
				obj.setSingleton(singleton);
				obj.renew();
			}
			
			return obj;
		}
	}
	
	/**
	 * 创建托管类的对象
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public static Object entrustCreate(String code,String className,boolean singleton) throws Exception {
		String key=(code!=null&&!code.equals(""))?code:(className+singleton);
		if(!entrusts.containsKey(key)){
			return create(code);
		}
		
		NvwaObject obj=(NvwaObject)entrusts.get(key);
		if(!obj.getImplementation().equals(className)
				||obj.getSingleton()!=singleton){
			obj.setImplementation(className);
			obj.setSingleton(singleton);
			obj.renew();
		}

		try{
			return obj.create();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param className
	 * @param parameterTypes
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public static Object entrustCreate(String code,String className,boolean singleton,Class[] parameterTypes,Object[] parameters) throws Exception{
		String key=(code!=null&&!code.equals(""))?code:(className+singleton);
		if(!entrusts.containsKey(key)){
			return create(code);
		}
		
		NvwaObject obj=(NvwaObject)entrusts.get(key);
		if(!obj.getImplementation().equals(className)
				||obj.getSingleton()!=singleton){
			obj.setImplementation(className);
			obj.setSingleton(singleton);
			obj.renew();
		}

		try{
			return obj.create(parameterTypes,parameters);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static NvwaClassLoader getCustomClassLoader() throws Exception{
		waitWhileLoading();
		if(customClassLoaderName==null) return null;
		
		if(customClassLoader==null){
			Class clazz=Class.forName(customClassLoaderName);
			customClassLoader=(NvwaClassLoader)clazz.getConstructor().newInstance();
		}
		customClassLoader.setClasspath(JProperties.getClassPath(),JProperties.getJarPath());
		return customClassLoader;
	}

	
	/**
	 * 通过不带参数的构造函数创建对象
	 * @param code nvwa.xml中设定的类编码
	 * @return
	 */
	public static Object create(String code){
		waitWhileLoading();
		NvwaObject obj=(NvwaObject)objects.get(code);

		if(obj==null) return null;

		try{
			return obj.create();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 通过带参数的构造函数创建对象
	 * @param code 设定
	 * @param parameterTypes 构造函数参数类类型
	 * @param parameters 构造函数参数
	 * @return
	 */
	public static Object create(String code,Class[] parameterTypes,Object[] parameters){
		waitWhileLoading();
		NvwaObject obj=(NvwaObject)objects.get(code);
		if(obj==null) return null;
		try{
			return obj.create(parameterTypes,parameters);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public static boolean needRenew(String className) throws Exception{
		if(customClassLoader==null) return false;
		return customClassLoader.needRenew(className);
	}
	
	/**
	 * 
	 * @param code
	 * @param className
	 * @param singleton
	 * @param current
	 * @return
	 * @throws Exception
	 */
	public static boolean hasRenew(String code,String className,boolean singleton,Object current) throws Exception{
		if(!singleton) return false;//不是单例模式
		
		NvwaObject old=Nvwa.entrust(code,className,singleton);
		if(old==null) return false;//对象不存在
		
		if(current==null&&old.getInstance()==null) return false;//对象均为null
		
		if(current==null) return true;//当前对象null，最新对象不为null，说明已经更新

		return !current.equals(old.getInstance());//不相同说明已经更新
	}
	
	/**
	 * 
	 * @param code
	 * @param key
	 * @return
	 */
	public static String getParameter(String code,String key){
		waitWhileLoading();
		NvwaObject obj=(NvwaObject)objects.get(code);
		if(obj==null) return null;
		return obj.getParameter(key);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isDefinedImplementation(String name){
		waitWhileLoading();
		if(classLoaderResponsibleFor.contains(name)) return true;
		if(entrustClassNames.contains(name)) return true;			
		if(classLoaderResponsibleFor.contains("__all_implementation")) return implementations.contains(name);
		return false;
	}
	
	/**
	 * 启动监控线程，发现配置文件修改了就自动重新加载
	 *
	 */
	private static void startMonitor(){
		Nvwa monitor=new Nvwa();
		Thread thread=new Thread(monitor);
		thread.start();
		System.out.println(JUtilTimestamp.timestamp()+" j.nvwa.Nvwa Nvwa monitor thread started.");
	}
	
	/**
	 * 
	 */
	private static void load(){
		try{
			loading=true;
			
			implementations.clear();
			classLoaderResponsibleFor.clear();
			
			File dir = new File(JProperties.getConfigPath());
			if(!dir.exists()){
	        	throw new Exception("配置文件目录不存在");
			}

			List newCodes=new LinkedList();//所有对象的编码
			
			File[] files=dir.listFiles();
			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("nvwa")) load(files[i],newCodes);
			}
			
			//移除已经在nvwa.xml中删除的<object>
			List allCodes=objects.listKeys();
			for(int i=0;i<allCodes.size();i++){
				if(!newCodes.contains(allCodes.get(i))){
					objects.remove(allCodes.get(i));
				}
			}
			
			allCodes.clear();
			allCodes=null;
			
			newCodes.clear();
			newCodes=null;
			//移除已经在nvwa.xml中删除的<object> end

			loading=false;
			
			//更新已创建单例对象的成员值
			List allObjects=objects.listValues();
			for(int i=0;i<allObjects.size();i++){
				NvwaObject o=(NvwaObject)allObjects.get(i);
				o.doRenewField();
			}	
		}catch(Exception e){
			loading=false;
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化
	 *
	 */
	private static void load(File file,List newCodes){
		try{			
			System.out.println(JUtilTimestamp.timestamp()+" loading objects from "+file.getName());
			
			//文件是否存在
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			
			Document document=JUtilDom4j.parse(JProperties.getConfigPath()+"nvwa.xml","UTF-8");			
			Element root=document.getRootElement();
			
			Element customClassLoaderEle=root.element("custom-classloader");
			if(customClassLoaderEle!=null){//如果使用自定义类
				//从不使用自定义ClassLoader改为使用ClassLoader，或ClassLoader类名变化
				if(customClassLoaderName==null
						||!customClassLoaderName.equals(customClassLoaderEle.attributeValue("class"))){
					customClassLoader=null;
				}
				
				customClassLoaderName=customClassLoaderEle.attributeValue("class");				
				List responsibleFor=customClassLoaderEle.elements("responsible-for");
				for(int i=0;i<responsibleFor.size();i++){
					Element responsibleEle=(Element)responsibleFor.get(i);
					classLoaderResponsibleFor.add(responsibleEle.attributeValue("class"));
				}
			}else if(customClassLoaderName!=null){//从使用自定义ClassLoader改为不使用ClassLoader
				customClassLoaderName=null;
				customClassLoader=null;
			}
			
			List objs=root.elements("object");
			for(int i=0;i<objs.size();i++){
				Element objEle=(Element)objs.get(i);
				String code=objEle.elementText("code");
				
				newCodes.add(code);
				
				if(!objects.containsKey(code)){
					NvwaObject obj=new NvwaObject();
					obj.setCode(code);
					obj.setName(objEle.elementText("name"));
					obj.setImplementation(objEle.elementText("implementation"));
					obj.setSingleton("true".equalsIgnoreCase(objEle.elementText("singleton")));

					List params=objEle.elements("parameter");
					for(int j=0;j<params.size();j++){
						Element paramEle=(Element)params.get(j);
						obj.setParameter(paramEle.attributeValue("key"),paramEle.attributeValue("value"));
					}
					
					String fieldsCheck="";
					List fields=objEle.elements("field");
					for(int j=0;j<fields.size();j++){
						Element fieldEle=(Element)fields.get(j);
						obj.setFiled(fieldEle.attributeValue("name"),
								fieldEle.attributeValue("type"),
								fieldEle.attributeValue("init-value"),
								"true".equalsIgnoreCase(fieldEle.attributeValue("keep")));
						fieldsCheck+=fieldEle.attributeValue("name");
						fieldsCheck+=fieldEle.attributeValue("type");
						fieldsCheck+=fieldEle.attributeValue("init-value");
						fieldsCheck+=fieldEle.attributeValue("keep");
					}
					obj.setFieldsCheck(fieldsCheck);
					
					objects.put(code,obj);
					
					System.out.println(JUtilTimestamp.timestamp()+" "+obj);
				}else{
					boolean renew=false;
					NvwaObject obj=(NvwaObject)objects.get(code);
					if(!obj.getImplementation().equals(objEle.elementText("implementation"))
							||obj.getSingleton()!=("true".equalsIgnoreCase(objEle.elementText("singleton")))) renew=true;
					
					obj.setName(objEle.elementText("name"));
					obj.setImplementation(objEle.elementText("implementation"));
					obj.setSingleton("true".equalsIgnoreCase(objEle.elementText("singleton")));
					
					obj.clearParameters();
					List params=objEle.elements("parameter");
					for(int j=0;j<params.size();j++){
						Element paramEle=(Element)params.get(j);
						obj.setParameter(paramEle.attributeValue("key"),paramEle.attributeValue("value"));
					}
					
					obj.cliearFileds();
					String fieldsCheck="";
					List fields=objEle.elements("field");
					for(int j=0;j<fields.size();j++){
						Element fieldEle=(Element)fields.get(j);
						obj.setFiled(fieldEle.attributeValue("name"),
								fieldEle.attributeValue("type"),
								fieldEle.attributeValue("init-value"),
								"true".equalsIgnoreCase(fieldEle.attributeValue("keep")));
						fieldsCheck+=fieldEle.attributeValue("name");
						fieldsCheck+=fieldEle.attributeValue("type");
						fieldsCheck+=fieldEle.attributeValue("init-value");
						fieldsCheck+=fieldEle.attributeValue("keep");
					}
					if(!fieldsCheck.equals(obj.getFieldsCheck())){
						obj.setFieldsCheck(fieldsCheck);
						obj.renewField();
					}
					
					if(renew) obj.renew();
					
					System.out.println(JUtilTimestamp.timestamp()+" "+obj);
				}
				String className=objEle.elementText("implementation");
				if(!implementations.contains(className)) implementations.add(className);
			}				
			
			root=null;
			document=null;	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 *
	 */
	private static void waitWhileLoading(){
		while(loading){
			try{
				Thread.sleep(100);
			}catch(Exception ex){}
		}
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		/*
		 * 检测nvwa.xml是否修改过，如修改过重新加载配置
		 */
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			try{
				if(loading) continue;
				
				File dir = new File(JProperties.getConfigPath());
				if(!dir.exists()){
		        	continue;
				}
				
				boolean changed=false;
				File[] files=dir.listFiles();
				for(int i=0;i<files.length;i++){
					if(files[i].getName().startsWith("nvwa")){
						Long _configLastModified=(Long)configLastModified.get(files[i].getName());
						if(_configLastModified==null||_configLastModified<files[i].lastModified()){
							//保存文件最近修改时间
							configLastModified.put(files[i].getName(),new Long(files[i].lastModified()));	
							changed=true;
							
							System.out.println(JUtilTimestamp.timestamp()+" j.nvwa.Nvwa "+files[i]+" has been modified, so reload it.");
						}
					}
				}
				
				if(changed) load();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
