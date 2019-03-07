package j.nvwa.lab;

import j.nvwa.Nvwa;

/**
 * 
 * @author 肖炯
 *
 */
public class One {
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
    	OneObject oo=(OneObject)Nvwa.create("HelloWorld");
		oo.hello();
		oo.hello();
		oo.hello();
		oo=(OneObject)Nvwa.create("HelloWorld");
		oo.hello();
		Thread.sleep(10000);
		oo.hello();
	}
}
