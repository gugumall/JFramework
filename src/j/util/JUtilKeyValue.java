package j.util;

/**
 * 
 * @author JFramework
 *
 */
public class JUtilKeyValue {
	private Object key;
	private Object value;
	private Object desc;
	
	public JUtilKeyValue(Object key,Object value){
		this.setKey(key);
		this.setValue(value);
	}
	
	public JUtilKeyValue(Object key,Object value,Object desc){
		this.key=key;
		this.value=value;
		this.desc=desc;
	}
	
	public void setKey(Object key){
		this.key=key;
	}
	public void setValue(Object value){
		this.value=value;
	}
	public void setExtra(Object desc){
		this.desc=desc;
	}
	
	public Object getKey(){
		return this.key;
	}
	public Object getValue(){
		return this.value;
	}
	public Object getDesc(){
		return this.desc;
	}
}
