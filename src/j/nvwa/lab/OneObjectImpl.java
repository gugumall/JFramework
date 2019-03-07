package j.nvwa.lab;

/**
 * 
 * @author 肖炯
 *
 */
public class OneObjectImpl implements OneObject{
	private int counter=0;
	
	/**
	 * 
	 * @param counter
	 */
	public void setCounter(int counter){
		this.counter=counter;
	}

	/**
	 * 
	 * @param counter
	 */
	public int getCounter(){
		return this.counter;
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.nvwa.lab.OneObject#hello()
	 */
	public void hello(){
		this.counter++;
		System.out.println("hello - "+counter);
	}
}
