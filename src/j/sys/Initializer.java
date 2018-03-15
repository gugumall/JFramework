package j.sys;

/**
 * 用于在系统启动时完成某些初始化操作。
 * 在sys.xml的<Initializers>中指定一个或多个Initializer的实现类，
 * 系统启动时，按指定顺序依次调用其initialization()方法。
 * @author JFramework
 *
 */
public interface Initializer {
	/**
	 * 初始化，子类重写
	 * @throws Exception
	 */
	public void initialization()throws Exception;
}
