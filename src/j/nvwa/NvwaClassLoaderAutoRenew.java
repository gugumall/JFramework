package j.nvwa;

import j.util.ConcurrentMap;
import j.util.JUtilInputStream;
import j.util.JUtilMath;
import j.util.JUtilString;
import j.util.JUtilTimestamp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 自动检查类文件是否更新，如更新则自动重新加载
 * 
 * @author 肖炯
 * 
 */
public class NvwaClassLoaderAutoRenew extends NvwaClassLoader {	
	private String classpath;// 类文件存放根目录
	private String jarpath;// jar文件存放根目录

	private static ConcurrentMap classes = new ConcurrentMap();// 已加载的类
	private static ConcurrentMap lastModified = new ConcurrentMap();// 类文件上次修改时时间
	private static ConcurrentMap jarFileLocations=new ConcurrentMap();
	private static ConcurrentMap instances = new ConcurrentMap();// 键：类文件存放根目录 值：与之对应的ClassLoaderAutoUpdate实例

	/**
	 * 
	 *
	 */
	public NvwaClassLoaderAutoRenew() {
		super();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.nvwa.NvwaClassLoader#setClasspath(java.lang.String, java.lang.String)
	 */
	public void setClasspath(String classpath,String jarpath){
		this.classpath=classpath;
		this.jarpath=jarpath;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.nvwa.NvwaClassLoader#getInstance(java.lang.String, java.lang.String)
	 */
	public NvwaClassLoader getInstance(String path,String jarpath)throws Exception {
		if (!instances.containsKey(path+jarpath)) {
			NvwaClassLoaderAutoRenew loader = new NvwaClassLoaderAutoRenew();
			
			//jdk1.8及以下才能设置parent，否则报错
			String javaVersion=System.getProperty("java.class.version");
			if(JUtilMath.isNumber(javaVersion)
					&&Double.parseDouble(javaVersion)<=52) {
				Field field = ClassLoader.class.getDeclaredField("parent");  
				field.setAccessible(true);  
				field.set(loader,Nvwa.defaultClassLoader);  
			}
			//jdk1.8及以下才能设置parent，否则报错 end
			
			loader.setClasspath(path,jarpath);
			instances.put(path+jarpath, loader);
		}
		return (NvwaClassLoader) instances.get(path+jarpath);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.nvwa.NvwaClassLoader#renew(java.lang.String)
	 */
	protected boolean needRenew(String name)throws Exception{
		if (!responsible(name)){
			return false;
		}
		
		try {
			name = name.intern();
			synchronized (name) {
				String filePath = getFile(this.classpath,name);
				
				File file = new File(filePath);
				if (!file.exists()){
					return false;
				}

				long latestUpd = file.lastModified();
				file = null;
				
				if (classes.containsKey(name) && lastModified.containsKey(name)) {// 已经加载过，判断是否已更新
					Long lastUpd = (Long) lastModified.get(name);

					return (lastUpd.longValue() != latestUpd);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param path
	 * @param jarpath
	 * @return
	 * @throws Exception
	 */
	private NvwaClassLoader getNewInstance(String path,String jarpath)throws Exception {
		NvwaClassLoaderAutoRenew loader = new NvwaClassLoaderAutoRenew();
		loader.setClasspath(path,jarpath);
		return loader;
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (!responsible(name)){
			return Nvwa.defaultClassLoader.loadClass(name);
		}
		
		try {
			name = name.intern();
			synchronized (name) {
				String filePath = getFile(this.classpath,name);

				boolean inJar=false;
				
				File file = new File(filePath);
				if (!file.exists()){
					String inJarFilePath=getJarFile(name);
					if(inJarFilePath==null){
						throw new ClassNotFoundException("class file is not exists - "+name);
					}else{
						file=new File(inJarFilePath);
						inJar=true;
					}
				}

				long latestUpd = file.lastModified();
				file = null;

				if (classes.containsKey(name) && lastModified.containsKey(name)) {// 已经加载过，判断是否已更新
					Long lastUpd = (Long) lastModified.get(name);

					if (lastUpd.longValue() == latestUpd) {// 未更新且已经加载
						return (Class) classes.get(name);
					}else{//更新了，重新加载
						System.out.println(JUtilTimestamp.timestamp()+" j.nvwa.NvwaClassLoaderAutoRenew renew "+name);
						NvwaClassLoaderAutoRenew newLoader=(NvwaClassLoaderAutoRenew)getNewInstance(this.classpath,this.jarpath);
						Class clazz=defineClazz(newLoader,resolve,latestUpd,name,inJar);
						return clazz;
					}
				}else{
					Class clazz=defineClazz(this,resolve,latestUpd,name,inJar);
					return clazz;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		throw new ClassNotFoundException();
	}
	
	/**
	 * 
	 * @param loader
	 * @param resolve
	 * @param latestUpd
	 * @param name
	 * @param inJar
	 * @return
	 * @throws Exception
	 */
	private Class defineClazz(NvwaClassLoaderAutoRenew loader,boolean resolve,long latestUpd,String name,boolean inJar) throws Exception{
		byte[] classData = loader.loadClassData(name,inJar);
		
		Class clazz = loader.defineClass(name, classData, 0,classData.length);
		if(resolve) loader.resolveClass(clazz);
		
		lastModified.put(name,new Long(latestUpd));
		classes.put(name,clazz);
		
		return clazz;
	}
	
	/**
	 * 判断是否由ClassLoaderAutoUpdate进行加载
	 * @param name
	 * @return
	 */
	private boolean responsible(String name){		
		return Nvwa.isDefinedImplementation(name);
	}

	/**
	 * get class file path from full name of class
	 * 
	 * @param root - root path of classes
	 * @param classname - full name of class
	 * @return
	 */
	private String getFile(String root, String classname) {
		return root + JUtilString.replaceAll(classname, ".", File.separator)+ ".class";
	}

	/**
	 * 
	 * @param name
	 * @param inJar
	 * @return
	 * @throws IOException
	 */
	private byte[] loadClassData(String name,boolean inJar) throws Exception {
		if(!inJar){
			String filePath = getFile(this.classpath, name);
	
			File file = new File(filePath);
			if (file.exists() && file.isFile()) {
				return JUtilInputStream.bytes(new FileInputStream(filePath));
			} else {
				throw new IOException("file of class " + name + " not found.");
			}
		}else{
			String entryName=JUtilString.replaceAll(name, ".", "/")+".class";
			String jarFilePath=getJarFile(name);
			JarFile jar=new JarFile(getJarFile(name));
			JarEntry en=jar.getJarEntry(entryName);
			
			byte[] bs=JUtilInputStream.bytes(jar.getInputStream(en));
			System.out.println("data of jar file "+name+" in "+jarFilePath+" loaded,the size is - "+bs.length);
			
			jar.close();
			return bs;
		}
	}

	/**
	 * get class file path from full name of class
	 * 
	 * @param root - root path of classes
	 * @param classname - full name of class
	 * @return
	 */
	private String getJarFile(String classname) throws Exception{		
		if(jarpath==null) return null;
		
		if(jarFileLocations.containsKey(classname)) return (String)jarFileLocations.get(classname);
		
		File jarDir=new File(jarpath);
		if(!jarDir.isDirectory()||!jarDir.exists()) return null;
		
		File[] jars=jarDir.listFiles();
		for(int i=0;i<jars.length;i++){
			if(!jars[i].getName().toLowerCase().endsWith(".jar")) continue;
			
			String entryName=JUtilString.replaceAll(classname, ".", "/")+".class";
			
			JarFile jar=new JarFile(jars[i]);
			JarEntry en=jar.getJarEntry(entryName);
			if(en!=null&&!en.isDirectory()){
				System.out.println("find "+classname+" in jar "+jars[i].getAbsolutePath()+","+en.getCompressedSize());
				jarFileLocations.put(classname,jars[i].getAbsolutePath());

				jar.close();
				return jars[i].getAbsolutePath();
			}
			
			jar.close();
		}
		
		return null;
	}
}
