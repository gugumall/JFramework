package j.dao.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mchange.v2.c3p0.DataSources;

/**
 * 
 * @author JFramework
 * 
 */
public class C3P0ConnectionProvider implements ConnectionProvider {
	private static final Log log = LogFactory.getLog(C3P0ConnectionProvider.class);
	private DataSource ds;
	private Integer isolation;

	/*
	 *  (non-Javadoc)
	 * @see j.dao.connection.ConnectionProvider#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		final Connection c = ds.getConnection();
		if (isolation != null){
			c.setTransactionIsolation(isolation.intValue());
		}
		if (c.getAutoCommit()){
			c.setAutoCommit(false);
		}
		return c;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.connection.ConnectionProvider#closeConnection(java.sql.Connection)
	 */
	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.connection.ConnectionProvider#configure(java.util.Properties)
	 */
	public void configure(Properties props) throws Exception {
		String jdbcDriverClass = props.getProperty(Environment.DRIVER);
		String jdbcUrl = props.getProperty(Environment.URL);
		Properties connectionProps = ConnectionProviderFactory.getConnectionProperties(props);

		System.out.println("C3P0 using driver: " + jdbcDriverClass + " at URL: "+ jdbcUrl);
		System.out.println("Connection properties: " + connectionProps);

		if (jdbcDriverClass == null) {
			log.warn("No JDBC Driver class was specified by property " + Environment.DRIVER);
		} else {
			try {
				Class.forName(jdbcDriverClass);
			} catch (ClassNotFoundException cnfe) {
				String msg = "JDBC Driver class not found: " + jdbcDriverClass;
				log.fatal(msg);
				throw new Exception(msg);
			}
		}

		try {
			DataSource unpooled = DataSources.unpooledDataSource(jdbcUrl,connectionProps);
			ds = DataSources.pooledDataSource(unpooled, connectionProps);
		} catch (Exception e) {
			log.fatal("could not instantiate C3P0 connection pool", e);
			throw new Exception("Could not instantiate C3P0 connection pool", e);
		}

		String i = props.getProperty(Environment.ISOLATION);
		if (i == null) {
			isolation = null;
		} else {
			isolation = new Integer(i);
			//log.info("JDBC isolation level: " + Environment.isolationLevelToString(isolation.intValue()));
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.connection.ConnectionProvider#close()
	 */
	public void close() {
		try {
			DataSources.destroy(ds);
		} catch (SQLException sqle) {
			log.warn("could not destroy C3P0 connection pool", sqle);
		}
	}
}
