package j.app.sso;

import java.util.List;

/**
 * 
 * @author JFramework
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
