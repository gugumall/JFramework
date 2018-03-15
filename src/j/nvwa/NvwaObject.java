package j.nvwa;


import j.Properties;
import j.util.ConcurrentMap;
import j.util.JUtilBean;
import j.util.JUtilTimestamp;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;

/**
 * 
 * @author JFramework
 *
 */
public class NvwaObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;
	private String Interface;
	private String implementation;
	private boolean singleton;
	private ConcurrentMap parameters;
	private ConcurrentMap fields;
	private Object instance;
	private boolean renew=false;
	private boolean renewField=false;
	private String fieldsCheck="";

	public NvwaObject(){
		parameters=new ConcurrentMap();
		fields=new ConcurrentMap();
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void setParameter(String key,String value){
		parameters.put(key,value);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getParameter(String key){
		return (String)parameters.get(key);
	}
	
	/**
	 * 
	 *
	 */
	public void clearParameters(){
		parameters.clear();
	}
	

	
	/**
	 * 
	 * @param name
	 * @param type
	 * @param initValue
	 * @param keep
	 */
	public void setFiled(String name,String type,String initValue,boolean keep){
		fields.put(name,new NvwaField(name,type,initValue,keep));
	}
	
	
	/**
	 * 
	 * @param name
	 * @param type
	 * @param keep
	 */
	public void setFiled(String name,String type,boolean keep){
		fields.put(name,new NvwaField(name,type,null,keep));
	}
	
	/**
	 * 
	 * @param fieldName
	 * @return
	 */
	public NvwaField getFiled(String fieldName){
		return (NvwaField)fields.get(fieldName);
	}
	
	/**
	 * 
	 * @param fieldsCheck
	 */
	public void setFieldsCheck(String fieldsCheck){
		this.fieldsCheck=fieldsCheck;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFieldsCheck(){
		return this.fieldsCheck;
	}
	
	/**
	 * 
	 *
	 */
	public void cliearFileds(){
		fields.clear();
	}
	
	/**
	 * 
	 * @param code
	 */
	public void setCode(String code){
		this.code=code;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCode(){
		return this.code;
	}
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name){
		this.name=name;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * 
	 * @param Interface
	 */
	public void setInterface(String Interface){
		this.Interface=Interface;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getInterface(){
		return this.Interface;
	}
	
	/**
	 * 
	 * @param implementation
	 */
	synchronized public void setImplementation(String implementation){
		this.implementation=implementation;
	}
	
	/**
	 * 
	 * @return
	 */
	synchronized public String getImplementation(){
		return this.implementation;
	}
	
	/**
	 * 
	 * @param singleton
	 */
	synchronized public void setSingleton(boolean singleton){
		this.singleton=singleton;
	}
	
	/**
	 * 
	 * @return
	 */
	synchronized public boolean getSingleton(){
		return this.singleton;
	}
	
	public Object getInstance(){
		return this.instance;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Object create() throws Exception {
		try{
			if(this.singleton){
				synchronized(this){		
					NvwaClassLoader loader=Nvwa.getCustomClassLoader();
					if(instance!=null&&!renew){
						if(loader!=null&&loader.needRenew(this.implementation)){//需要更新
							System.out.println(JUtilTimestamp.timestamp()+" j.nvwa.Obj "+this.code+","+this.implementation+" need to renew.");
							renew=true;
						}
					}
					
					Object _new=null;
					if(instance==null||renew){
						Class clazz=null;
						if(loader!=null){
							clazz=loader.getInstance(Properties.getClassPath(),Properties.getJarPath()).loadClass(this.implementation);
						}else{
							clazz=Class.forName(this.implementation);
						}
						_new=clazz.getConstructor().newInstance();
						init(clazz,_new);
					}else if(renewField){
						doRenewField();
					}
					if(_new!=null) instance=_new;
					
					renew=false;			
					renewField=false;
					
					return instance;
				}
			}else{
				instance=null;
				
				NvwaClassLoader loader=Nvwa.getCustomClassLoader();
				Class clazz=null;
				if(loader!=null){
					clazz=loader.getInstance(Properties.getClassPath(),Properties.getJarPath()).loadClass(this.implementation);
				}else{
					clazz=Class.forName(this.implementation);
				}
				
				Object _new=clazz.getConstructor().newInstance();
				init(clazz,_new);
				return _new;
			}
		}catch(Exception e){
			renew=false;
			renewField=false;
			throw e;
		}
	}
	
	/**
	 * 
	 * @param parameterTypes
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public Object create(Class[] parameterTypes,Object[] parameters) throws Exception {
		try{
			if(this.singleton){
				synchronized(this){	
					NvwaClassLoader loader=Nvwa.getCustomClassLoader();
					
					if(instance!=null&&!renew){
						if(loader!=null&&loader.needRenew(this.implementation)){//需要更新
							System.out.println(JUtilTimestamp.timestamp()+" j.nvwa.Obj "+this.code+","+this.implementation+" need to renew.");
							renew=true;
						}
					}
					
					Object _new=null;
					if(instance==null||renew){
						Class clazz=null;
						if(loader!=null){
							clazz=loader.getInstance(Properties.getClassPath(),Properties.getJarPath()).loadClass(this.implementation);
						}else{
							clazz=Class.forName(this.implementation);
						}
						_new=clazz.getConstructor(parameterTypes).newInstance(parameters);
						init(clazz,_new);
					}else if(renewField){
						doRenewField();
					}
					if(_new!=null) instance=_new;
					
					renew=false;					
					renewField=false;
					
					return instance;
				}
			}else{
				NvwaClassLoader loader=Nvwa.getCustomClassLoader();
				Class clazz=null;
				if(loader!=null){
					clazz=loader.getInstance(Properties.getClassPath(),Properties.getJarPath()).loadClass(this.implementation);
				}else{
					clazz=Class.forName(this.implementation);
				}
				
				Object _new=clazz.getConstructor(parameterTypes).newInstance(parameters);
				init(clazz,_new);
				return _new;
			}
		}catch(Exception e){
			renew=false;
			renewField=false;
			throw e;
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void doRenewField() throws Exception{
		if(!singleton||instance==null) return;
		
		NvwaClassLoader loader=Nvwa.getCustomClassLoader();
		
		Class clazz=null;
		if(loader!=null){
			clazz=loader.getInstance(Properties.getClassPath(),Properties.getJarPath()).loadClass(this.implementation);
		}else{
			clazz=Class.forName(this.implementation);
		}
		
		init(clazz,instance);
	}
	
	/**
	 * 
	 * @param clazz
	 * @param _new
	 * @throws Exception
	 */
	private void init(Class clazz,Object _new) throws Exception{
		List fieldList=fields.listValues();
		for(int i=0;i<fieldList.size();i++){
			NvwaField field=(NvwaField)fieldList.get(i);//对每个配置的field赋值
			String setter="set"+JUtilBean.upperFirstChar(field.name);
			
			Method[] methods=clazz.getMethods();//所有方法
			for(int m=0;m<methods.length;m++){
				if(methods[m].getName().equals(setter)){//第一个匹配的set方法（所以此种情景下，实现类不应该设计多个同名的set方法）
					if(field.keep&&instance!=null){//单例模式下，当需要重新创建对象时，保持原对象中的某些属性值
						Object oldValue=JUtilBean.getPropertyValue(instance,field.name);
						methods[m].invoke(_new,oldValue);
						System.out.println("kept ["+oldValue+"] for "+field.name+" of "+this.getImplementation());
					}else if(field.initValue!=null){//赋值
						if(field.type.equals("ref")){//引用其它对象
							Object ref=Nvwa.create(field.initValue);
							methods[m].invoke(_new,ref);
						}else{
							if(field.type.equalsIgnoreCase("String")){
								methods[m].invoke(_new,field.initValue);
							}else if(field.type.equalsIgnoreCase("int")){
								methods[m].invoke(_new,Integer.parseInt(field.initValue));
							}else if(field.type.equalsIgnoreCase("long")){
								methods[m].invoke(_new,Long.parseLong(field.initValue));
							}else if(field.type.equalsIgnoreCase("double")){
								methods[m].invoke(_new,Double.parseDouble(field.initValue));
							}else if(field.type.equalsIgnoreCase("Timestamp")){
								methods[m].invoke(_new,Timestamp.valueOf(field.initValue));
							}else if(field.type.equalsIgnoreCase("boolean")){
								methods[m].invoke(_new,"true".equalsIgnoreCase(field.initValue));
							}
						}
					}
					break;
				}
			}
		}
	}
	
	/**
	 * 当配置发生变化且为单例模式时，标记为需要更新
	 *
	 */
	public void renew(){
		if(this.singleton&&instance!=null){
			renew=true;
		}
	}
	
	/**
	 * 
	 *
	 */
	public void renewField(){
		if(this.singleton&&instance!=null){
			renewField=true;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.code+","+this.name+","+this.implementation+","+this.singleton;
	}
}
