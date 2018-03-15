package j.nvwa;

public abstract class NvwaClassLoader extends java.lang.ClassLoader {
	/**
	 * 
	 *
	 */
	public NvwaClassLoader() {
		super();
	}

	/**
	 * 
	 * @param path
	 * @param jarpath
	 * @return
	 * @throws Exception
	 */
	public abstract NvwaClassLoader getInstance(String path,String jarpath)throws Exception;
	
	/**
	 * 
	 * @param classpath
	 * @param jarpath
	 */
	public abstract void setClasspath(String classpath,String jarpath);
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected abstract boolean needRenew(String name)throws Exception;
}
