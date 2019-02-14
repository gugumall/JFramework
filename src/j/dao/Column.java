package j.dao;

/**
 * @author 肖炯
 *
 */
public class Column {
	public String colName;
	public int    colType;
	public boolean notNull;
	public boolean gzip;
	public int length;
	
	/**
	 * 
	 * @param _colName
	 * @param _colType
	 * @param _notNull
	 * @param gzip
	 * @param _length
	 */
	public Column(String _colName,int _colType,boolean _notNull,boolean _gzip,int _length){
		colName=_colName;
		colType=_colType;
		notNull=_notNull;
		gzip=_gzip;
		length=_length;
	}
	
	/**
	 * 
	 * @param _colName
	 * @param _colType
	 * @param _gzip
	 */
	public Column(String _colName,int _colType,boolean _gzip){
		colName=_colName;
		colType=_colType;
		gzip=_gzip;
	}
}
