package j.dao;

/**
 * @author JFramework
 *
 */
public class Column {
	public String colName;
	public int    colType;
	public boolean notNull;
	public int length;
	
	/**
	 * 
	 * @param _colName
	 * @param _colType
	 * @param _notNull
	 * @param _length
	 */
	public Column(String _colName,int _colType,boolean _notNull,int _length){
		colName=_colName;
		colType=_colType;
		notNull=_notNull;
		length=_length;
	}
	
	/**
	 * 
	 * @param _colName
	 * @param _colType
	 */
	public Column(String _colName,int _colType){
		colName=_colName;
		colType=_colType;
	}
}
