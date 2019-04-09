package j.ui.form;

import j.common.JObject;
import j.util.ConcurrentMap;
import j.util.JUtilString;

/**
 * 
 * @author 肖炯
 *
 * 2019年4月9日
 *
 * <b>功能描述</b> form元素
 */
public class FormElement extends JObject{
	private static final long serialVersionUID = 1L;
	public static final String ELEMENT_TYPE_TEXT_SINGLE_LINE="SINGLE_LINE";//单行文本框
	public static final String ELEMENT_TYPE_TEXT_MULTI_LINES="MULTI_LINES";//多行文本框（textarea）
	public static final String ELEMENT_TYPE_LIST="LIST";//列表（select）
	
	private String type;//元素类型
	private String id;
	private String name;
	private String label;
	private String placeholder;
	private String value;
	private int maxValueChars=0;//值最大字符数，小于等于0表示不限制
	private int maxValueBytes=0;//值最大字节数（按UTF-8编码计算)，小于等于0表示不限制
	private String validator;//验证值有效性的正则表达式，不设置表示不需验证
	private ConcurrentMap<String,String> options=new ConcurrentMap<String,String>();//可选值（仅当类型为LIST时有效）
	
	/**
	 * 
	 * @param type
	 * @param id
	 * @param name
	 * @param label
	 * @param placeholder
	 * @param value
	 * @param maxValueChars
	 * @param maxValueBytes
	 * @param validator
	 */
	public FormElement(String type,
			String id,
			String name,
			String label,
			String placeholder,
			String value,
			int maxValueChars,
			int maxValueBytes,
			String validator) {
		this.type=type;
		this.id=id;
		this.name=name;
		this.label=label;
		this.placeholder=placeholder;
		this.value=value;
		this.maxValueChars=maxValueChars;
		this.maxValueBytes=maxValueBytes;
		this.validator=validator;
		
		if(this.value==null) value="";
	}
	
	/**
	 * 
	 * @param value
	 * @param text
	 */
	public void addOption(String value,String text) {
		options.put(value,text);
	}
	
	//getters
	public String getType() {return this.type;}
	public String getId() {return this.id;}
	public String getName() {return this.name;}
	public String getLabel() {return this.label;}
	public String getPlaceholder() {return this.placeholder;}
	public String getValue() {return this.value;}
	public int getMaxValueChars() {return this.maxValueChars;}
	public int getMaxValueBytes() {return this.maxValueBytes;}
	public String getValidator() {return this.validator;}
	public ConcurrentMap getOptions() {return this.options;}
	
	//setters
	public void setValue(String value) {
		this.value=value;
		if(this.value==null) value="";
	}
	
	//验证
	public boolean valid() {
		if(this.maxValueChars>0&&this.value.length()>this.maxValueChars) return false;
		if(this.maxValueBytes>0&&JUtilString.bytes(this.value, "UTF-8")>this.maxValueBytes) return false;
		if(this.validator!=null&&!"".equals(this.validator)&&!this.value.matches(this.validator)) return false;
		return true;
	}
}
