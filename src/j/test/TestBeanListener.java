package j.test;

import j.app.sso.LoginStatus;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TestBeanListener implements PropertyChangeListener{

	public TestBeanListener() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void propertyChange(PropertyChangeEvent evt) {
		TestBean b=(TestBean)evt.getSource();
		System.out.println(b.getName());
	}
}
