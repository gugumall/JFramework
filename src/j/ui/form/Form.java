package j.ui.form;

import j.common.JObject;
import j.util.ConcurrentList;

/**
 * 
 * @author 肖炯
 *
 * 2019年4月9日
 *
 * <b>功能描述</b> 模拟网页Form
 */
public class Form extends JObject{
	private static final long serialVersionUID = 1L;
	public static final String ENCTYPE_MULTIPART="multipart/form-data";

	private String id;
	private String name;
	private String method;
	private String action;
	private String enctype;
	private ConcurrentList<FormElement> elements=new ConcurrentList<FormElement>();
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param method
	 * @param action
	 * @param enctype
	 */
	public Form(String id,String name,String method,String action,String enctype) {
		this.id=id;
		this.name=name;
		this.method=method;
		this.action=action;
		this.enctype=enctype;
	}
	
	/**
	 * 
	 * @param element
	 */
	public void addElement(FormElement element) {
		this.elements.add(element);
	}
	
	//getters
	public String getId() {return this.id;}
	public String getName() {return this.name;}
	public String getMethod() {return this.method;}
	public String getAction() {return this.action;}
	public String getEnctype() {return this.enctype;}
	public ConcurrentList getElements() {return this.elements;}
	public FormElement getElement(String idOrName) {
		for(int i=0;i<this.elements.size();i++) {
			FormElement e=this.elements.get(i);
			if(idOrName.equalsIgnoreCase(e.getId())
					||idOrName.equalsIgnoreCase(e.getName())) {
				return e;
			}
		}
		return null;
	}
	
	//setters
	public void setMthod(String method) {
		this.method=method;
	}
	public void setAction(String action) {
		this.action=action;
	}
	public void setEnctype(String enctype) {
		this.enctype=enctype;
	}
	
	//valid
	public boolean valid() {
		for(int i=0;i<this.elements.size();i++) {
			FormElement e=this.elements.get(i);
			if(!e.valid()) return false;
		}
		return true;
	}
}
