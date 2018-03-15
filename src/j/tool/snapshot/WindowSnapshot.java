package j.tool.snapshot;

/**
 * 
 * @author 肖炯
 *
 */
public class WindowSnapshot {
	static {
		System.loadLibrary("WindowSnapshot64");
	}

	/**
	 * 
	 * @param windowTitle
	 * @param filePath
	 * @return
	 */
	public native static String snapshot(String windowTitle,String filePath);
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		System.out.println("start...");
		String ret=WindowSnapshot.snapshot(new String("simulator".getBytes("UTF-8")), "d:\\axb.bmp");
		System.out.println("end");
	}
}