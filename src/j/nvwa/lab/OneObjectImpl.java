package j.nvwa.lab;

/**
 * 
 * @author JFramework
 *
 */
public class OneObjectImpl implements OneObject{
	public static int x=0;
	
	/*
	 * (non-Javadoc)
	 * @see j.nvwa.lab.OneObject#hello()
	 */
	public void hello(){
		x++;
		System.out.println("hello - "+x);
	}
}