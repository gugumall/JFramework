package j.dao;

public interface DAOCreator {
	public DAO create() throws Exception;
	public DAO create(Class caller) throws Exception;
}
