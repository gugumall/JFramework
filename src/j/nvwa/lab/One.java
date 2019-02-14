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
    	Nvwa.entrust("oo", "j.nvwa.lab.OneObjectImpl", true);
		OneObject o=(OneObject)Nvwa.entrustCreate("oo", "j.nvwa.lab.OneObjectImpl", false);
		o.hello();
		System.out.println(o.getClass().getClassLoader().getParent());
		
		OneObjectImpl oo=new OneObjectImpl();
		oo.hello();
		System.out.println(oo.getClass().getClassLoader());
	}
}
