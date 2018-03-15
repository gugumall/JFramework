package j.sys;

/**
 * 
 * @author 肖炯
 *
 */
public class AppPara {
	private String key;
	private String value;
	private String desc;
	private boolean canBeUpdated;
	private String fileName;
	
	public AppPara(String key,String value,String desc,boolean canBeUpdated,String fileName){
		this.key=key;
		this.value=value;
		this.desc=desc;
		this.canBeUpdated=canBeUpdated;
		this.fileName=fileName;
	}
	
	public void setKey(String key){
		this.key=key;
	}
	public void setValue(String value){
		this.value=value;
	}
	public void setDesc(String desc){
		this.desc=desc;
	}
	public void setCanBeUpdated(boolean canBeUpdated){
		this.canBeUpdated=canBeUpdated;
	}
	public void setFileName(String fileName){
		this.fileName=fileName;
	}
	
	public String getKey(){
		return this.key;
	}
	public String getValue(){
		return this.value;
	}
	public String getDesc(){
		return this.desc;
	}
	public boolean getCanBeUpdated(){
		return this.canBeUpdated;
	}
	public String getFileName(){
		return this.fileName;
	}
}
