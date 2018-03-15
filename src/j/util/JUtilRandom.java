package j.util;

import java.util.Random;

public class JUtilRandom {
	private static Random random=new Random();
	private static final Object lock=new Object();
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	public static int nextInt(int n){
		synchronized(lock){
			return random.nextInt(n);
		}
	}	
	
	/**
	 * 
	 * @return
	 */
	public static int nextInt(){
		synchronized(lock){
			return random.nextInt();
		}
	}	
}
