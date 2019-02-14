//$Id: UserSuppliedConnectionProvider.java,v 1.9 2004/06/04 01:27:38 steveebersole Exp $
package j.dao.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.LogFactory;

/**
 * An implementation of the <literal>ConnectionProvider</literal> interface that
 * simply throws an exception when a connection is requested. This implementation
 * indicates that the user is expected to supply a JDBC connection.
 * @see ConnectionProvider
 * @author 肖炯
 */
public class UserSuppliedConnectionProvider implements ConnectionProvider {
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.connection.ConnectionProvider#configure(java.util.Properties)
	 */
	public void configure(Properties props) throws Exception {
		LogFactory.getLog(UserSuppliedConnectionProvider.class).warn("No connection properties specified - the user must supply JDBC connections");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.connection.ConnectionProvider#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		throw new UnsupportedOperationException("The user must supply a JDBC connection");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.connection.ConnectionProvider#closeConnection(java.sql.Connection)
	 */
	public void closeConnection(Connection conn) throws SQLException {
		throw new UnsupportedOperationException("The user must supply a JDBC connection");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.connection.ConnectionProvider#close()
	 */
	public void close() {
	}
}






