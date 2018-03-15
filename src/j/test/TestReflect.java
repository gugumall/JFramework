package j.test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestReflect {
	/**
	 * 
	 *
	 */
	public TestReflect() {
		super();
	}
	
	public static void main(String[] args) throws Exception{
		Method method=TestReflectBean.class.getMethod("p");
		int m=method.getModifiers();
		System.out.println(Modifier.toString(m));
	}
}
