package j.test;

import java.beans.PropertyChangeSupport;

public class TestBean {
	
	private PropertyChangeSupport changes=null;
	private String name;
	
	public void setName(String name){
		String old=this.name;
		this.name=name;
		changes.firePropertyChange("xx",old,name);
	}
	public String getName(){
		return this.name;
	}
	
	/**
	 * 
	 *
	 */
	private TestBean(){
		changes = new PropertyChangeSupport(this);
		changes.addPropertyChangeListener(new TestBeanListener());
	}
	
	public static void main(String[] args){
		System.out.println("============");
		TestBean b=new TestBean();
		b.setName("xxxx");
	}
}
