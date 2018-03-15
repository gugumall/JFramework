package j.sys;

/**
 * 
 * @author 肖炯
 *
 */
public class AppParaGroup {
	private String name;
	private String desc;
	
	/**
	 * 
	 * @param name
	 * @param desc
	 */
	public AppParaGroup(String name,String desc){
		this.name=name;
		this.desc=desc;
	}
	
	public void setName(String name){
		this.name=name;
	}
	public void setDesc(String desc){
		this.desc=desc;
	}
	
	public String getName(){
		return this.name;
	}
	public String getDesc(){
		return this.desc==null?"":this.desc;
	}
}
