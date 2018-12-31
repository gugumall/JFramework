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
	private String placeholder;
	private boolean canBeUpdated;
	private String fileName;
	
	public AppPara(String key,String value,String desc,String placeholder,boolean canBeUpdated,String fileName){
		this.key=key;
		this.value=value;
		this.desc=desc;
		this.placeholder=placeholder;
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
	public void setPlaceholder(String placeholder){
		this.placeholder=placeholder;
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
	public String getPlaceholder(){
		return this.placeholder;
	}
	public boolean getCanBeUpdated(){
		return this.canBeUpdated;
	}
	public String getFileName(){
		return this.fileName;
	}
}
