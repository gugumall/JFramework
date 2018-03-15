package j.ext.workflow;

import j.common.JObject;


/**
 * 
 * @author 肖炯
 *
 */
public class JWFEngineConfig extends JObject {
	private static final long serialVersionUID = 1L;
	
	private String clazz;
	private int threads=1;
	private String name;
	private String desc;
	
	
	public String getClazz(){
		return this.clazz;
	}
	public void setClazz(String clazz){
		this.clazz=clazz;
	}
	
	public int getThreads(){
		return this.threads;
	}
	public void setThreads(int threads){
		this.threads=threads;
	}
	
	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name=name;
	}
	
	public String getDesc(){
		return this.desc;
	}
	public void setDesc(String desc){
		this.desc=desc;
	}
}
