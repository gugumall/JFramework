package j.dao.connection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 
 * @author 肖炯
 *
 */
public interface ConnectionProvider {
	/**
	 * 
	 * @param props
	 * @throws Exception
	 */
	public void configure(Properties props) throws Exception;
	
	/**
	 * Grab a connection
	 * @return a JDBC connection
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException;
	
	/**
	 * Dispose of a used connection.
	 * @param conn a JDBC connection
	 * @throws SQLException
	 */
	public void closeConnection(Connection conn) throws SQLException;
	
	/**
	 * Release all resources held by this provider. JavaDoc requires a second sentence.
	 * @throws Exception
	 */
	public void close() throws Exception;
}







