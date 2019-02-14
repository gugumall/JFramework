package j.test;

import j.log.Logger;

/**
 * 
 * @author 肖炯
 *
 */
public class TestNvwaAndLogger {	
	private static Logger log=Logger.create(TestNvwaAndLogger.class);
	
	/**
	 * 
	 *
	 */
	public TestNvwaAndLogger() {
		super();
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		while(true){
			Logger logg=Logger.create(TestNvwaAndLogger.class);
			logg.log("........",Logger.LEVEL_DEBUG);
			Thread.sleep(5000);
		}
	}
}
