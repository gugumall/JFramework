package j.app.sso;

import java.util.List;

/**
 * 
 * @author 肖炯
 *
 */
public interface SSOConfigLoader {
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public List loadClients() throws Exception;
}
