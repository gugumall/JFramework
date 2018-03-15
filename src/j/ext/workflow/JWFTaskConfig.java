package j.ext.workflow;

import j.util.ConcurrentMap;

public class JWFTaskConfig {
	private String uuid;
	private String name;
	private String desc;
	private boolean first=false;
	private ConcurrentMap nexts=new ConcurrentMap();
	
	
	public String getUuid(){
		return this.uuid;
	}
	public void setUuid(String uuid){
		this.uuid=uuid;
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
	
	public boolean getFirst(){
		return this.first;
	}
	public void setFirst(boolean first){
		this.first=first;
	}
	
	public JWFTaskNextConfig getNext(String condition){
		return (JWFTaskNextConfig)nexts.get(condition);
	}
}
