package j.nvwa;

/**
 * 
 * @author JStuido
 *
 */
public class NvwaField {
	protected String name;
	protected String type;
	protected String initValue;
	protected boolean keep;
	
	/**
	 * 
	 * @param name
	 * @param type
	 * @param initValue
	 * @param keep
	 */
	NvwaField(String name,String type,String initValue,boolean keep){
		this.name=name;
		this.type=type;
		this.initValue=initValue;
		this.keep=keep;
	}
}
