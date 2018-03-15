//$Id: DriverManagerConnectionProvider.java,v 1.12 2004/06/04 01:27:38 steveebersole Exp $
package j.dao.connection;

import j.dao.connection.helper.PropertiesHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A connection provider that uses <tt>java.sql.DriverManager</tt>. This
 * provider also implements a very rudimentary connection pool.
 * 
 * @see ConnectionProvider
 * @author JFramework
 */
public class DefaultConnectionProvider implements ConnectionProvider {
	private static final Log log = LogFactory.getLog(DefaultConnectionProvider.class);

	private String url;
	private Properties connectionProps;
	private Integer isolation;

	/*
	 *  (non-Javadoc)
	 * @see j.dao.connection.ConnectionProvider#configure(java.util.Properties)
	 */
	public void configure(Properties props) throws Exception {
		String driverClass = props.getProperty(Environment.DRIVER);
		
		//log.info("Using built-in connection pool (not for production use!)");
		//log.info("connection pool size: " + poolSize);

		isolation = PropertiesHelper.getInteger(Environment.ISOLATION, props);
		if (isolation != null){
			//log.info("JDBC isolation level: "+ Environment.isolationLevelToString(isolation.intValue()));
		}

		if (driverClass == null) {
			log.warn("no JDBC Driver class was specified by property "+ Environment.DRIVER);
		} else {
			try {
				// trying via forName() first to be as close to DriverManager's
				// semantics
				Class.forName(driverClass);
			} catch (ClassNotFoundException cnfe) {
				try {
					Class.forName(driverClass);
				} catch (ClassNotFoundException e) {
					String msg = "JDBC Driver class not found: " + driverClass;
					log.fatal(msg);
					throw new Exception(msg);
				}
			}
		}

		url = props.getProperty(Environment.URL);
		if (url == null) {
			String msg = "JDBC URL was not specified by property "+ Environment.URL;
			log.fatal(msg);
			throw new Exception(msg);
		}

		connectionProps = ConnectionProviderFactory.getConnectionProperties(props);

		//log.info("using driver: " + driverClass + " at URL: " + url);
		//log.info("connection properties: " + connectionProps);

	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.connection.ConnectionProvider#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(url, connectionProps);
		if (isolation != null){
			conn.setTransactionIsolation(isolation.intValue());
		}
		if (conn.getAutoCommit()){
			conn.setAutoCommit(false);
		}

		return conn;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.connection.ConnectionProvider#closeConnection(java.sql.Connection)
	 */
	public void closeConnection(Connection conn) throws SQLException {
		try {
			conn.close();
		} catch (SQLException sqle) {
			throw sqle;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.connection.ConnectionProvider#close()
	 */
	public void close() {
	}

	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() {
		close();
	}
}
