package j.dao.connection;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Instantiates a connection provider given either <tt>System</tt> properties
 * or a <tt>java.util.Properties</tt> instance. The
 * <tt>ConnectionProviderFactory</tt> first attempts to find a name of a
 * <tt>ConnectionProvider</tt> subclass in the property
 * <tt>hibernate.connection.provider_class</tt>. If missing, heuristics are
 * used to choose either <tt>DriverManagerConnectionProvider</tt>,
 * <tt>DatasourceConnectionProvider</tt>, <tt>C3P0ConnectionProvider</tt>
 * or <tt>DBCPConnectionProvider</tt>.
 * 
 * @see ConnectionProvider
 * @author JFramework
 */

public final class ConnectionProviderFactory {
	private static final Log log = LogFactory.getLog(ConnectionProviderFactory.class);

	private static final Set SPECIAL_PROPERTIES;
	static {
		SPECIAL_PROPERTIES = new HashSet();
		SPECIAL_PROPERTIES.add(Environment.DATASOURCE);
		SPECIAL_PROPERTIES.add(Environment.URL);
		SPECIAL_PROPERTIES.add(Environment.CONNECTION_PROVIDER);
		SPECIAL_PROPERTIES.add(Environment.POOL_SIZE);
		SPECIAL_PROPERTIES.add(Environment.ISOLATION);
		SPECIAL_PROPERTIES.add(Environment.DRIVER);
		SPECIAL_PROPERTIES.add(Environment.USER);
	}

	/**
	 * Instantiate a <tt>ConnectionProvider</tt> using <tt>System</tt>
	 * properties.
	 * 
	 * @return ConnectionProvider
	 * @throws Exception
	 */
	public static ConnectionProvider newConnectionProvider() throws Exception {
		return newConnectionProvider(Environment.getProperties());
	}

	/**
	 * Instantiate a <tt>ConnectionProvider</tt> using given properties.
	 * Method newConnectionProvider.
	 * 
	 * @param properties
	 *            hibernate <tt>SessionFactory</tt> properties
	 * @return ConnectionProvider
	 * @throws Exception
	 */
	public static ConnectionProvider newConnectionProvider(Properties properties) throws Exception {
		ConnectionProvider connections;
		String providerClass = properties.getProperty(Environment.CONNECTION_PROVIDER);
		if (providerClass != null) {
			try {
				log.info("Initializing connection provider: " + providerClass);
				connections = (ConnectionProvider) Class.forName(providerClass).newInstance();
			} catch (Exception e) {
				log.fatal("Could not instantiate connection provider", e);
				throw new Exception("Could not instantiate connection provider: "+ providerClass);
			}
		} else if (properties.getProperty(Environment.DATASOURCE) != null) {
			connections = new DatasourceConnectionProvider();
		} else if (properties.getProperty(Environment.C3P0_MAX_SIZE) != null) {
			connections = new C3P0ConnectionProvider();
		} else if (properties.getProperty(Environment.DBCP_MAXACTIVE) != null) {
			connections = new DBCPConnectionProvider();
		} else if (properties.getProperty(Environment.PROXOOL_XML) != null
				|| properties.getProperty(Environment.PROXOOL_PROPERTIES) != null
				|| properties.getProperty(Environment.PROXOOL_EXISTING_POOL) != null) {
			connections = new ProxoolConnectionProvider();
		} else if (properties.getProperty(Environment.URL) != null) {
			connections = new DriverManagerConnectionProvider();
		} else {
			connections = new DefaultConnectionProvider();
		}
		connections.configure(properties);
		return connections;
	}

	/**
	 * Transform JDBC connection properties.
	 * 
	 * Passed in the form <tt>hibernate.connection.*</tt> to the format
	 * accepted by <tt>DriverManager</tt> by triming the leading "<tt>hibernate.connection</tt>".
	 */
	public static Properties getConnectionProperties(Properties properties) {
		Iterator iter = properties.keySet().iterator();
		Properties result = new Properties();
		while (iter.hasNext()) {
			String prop = (String) iter.next();
		
			//c3p0.max_size
			if (!SPECIAL_PROPERTIES.contains(prop)) {
				if(prop.startsWith(Environment.CONNECTION_PREFIX)){
					String key=prop.substring(Environment.CONNECTION_PREFIX.length() + 1);
					if(key.equals("c3p0.max_size")) key="c3p0.maxPoolSize";
					result.setProperty(key, properties.getProperty(prop));
				}else if(prop.startsWith(Environment.CONNECTION_PREFIX2)){
					String key=prop.substring(Environment.CONNECTION_PREFIX2.length() + 1);
					if(key.equals("c3p0.max_size")) key="c3p0.maxPoolSize";
					result.setProperty(key, properties.getProperty(prop));
				}
			}
		}
		String userName = properties.getProperty(Environment.USER);
		if (userName != null) result.setProperty("user", userName);
		return result;
	}
}
