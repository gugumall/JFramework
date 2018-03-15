/*
 * Created on 2004-6-19
 *
 */

package j.dao.type;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.sql.SQLException;

/**
 * 
 * @author JFramework
 *
 */
public class Clob implements java.sql.Clob,Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private char chars[];
	
	public String getCharacters(){
		return new String(chars);
	}
	
	public String getCharacters(String encoding)throws Exception{
		String tmp=new String(chars);
		return new String(tmp.getBytes(),encoding);
	}

	/**
	 * 
	 * @param in
	 * @throws Exception
	 */
	public Clob(InputStream in) throws Exception {
		Reader reader=new InputStreamReader(in);
		char _chars[] = new char[1024];
		CharArrayWriter writer = new CharArrayWriter(0x500000);
		for (int i = -1; (i = reader.read(_chars)) > 0;){
			writer.write(_chars, 0, i);
		}
		writer.flush();
		writer.close();
		reader.close();
		this.chars = writer.toCharArray();
	}
	
	/**
	 * 
	 * @param in
	 * @param encoding
	 * @throws Exception
	 */
	public Clob(InputStream in,String encoding) throws Exception {
		Reader reader=new InputStreamReader(in,encoding);
		char _chars[] = new char[1024];
		CharArrayWriter writer = new CharArrayWriter(0x500000);
		for (int i = -1; (i = reader.read(_chars)) > 0;){
			writer.write(_chars, 0, i);
		}
		writer.flush();
		writer.close();
		reader.close();
		this.chars = writer.toCharArray();
	}
	
	/**
	 * 
	 * @param reader
	 * @throws IOException
	 */
	public Clob(Reader reader) throws IOException {
		char _chars[] = new char[1024];
		CharArrayWriter writer = new CharArrayWriter(0x500000);
		for (int i = -1; (i = reader.read(_chars)) > 0;){
			writer.write(_chars, 0, i);
		}
		writer.flush();
		writer.close();
		reader.close();
		this.chars = writer.toCharArray();
	}


	public long length() throws SQLException {
		return (long) chars.length;
	}

	public String getSubString(long offset, int length) throws SQLException {
		return new String(chars, (int) offset, length);
	}

	public Reader getCharacterStream() throws SQLException {
		return new CharArrayReader(chars);
	}

	public InputStream getAsciiStream() throws SQLException {
		return new ByteArrayInputStream((new String(chars)).getBytes());
	}

	public long position(String s, long l) throws SQLException {
		throw new UnsupportedOperationException("Method position() not yet implemented.");
	}

	public long position(Clob clob, long l) throws SQLException {
		throw new UnsupportedOperationException("Method position() not yet implemented.");
	}

	public Writer setCharacterStream(long l) throws SQLException {
		throw new UnsupportedOperationException("Method setCharacterStream() not yet implemented.");
	}

	public int setString(long l, String s, int i, int j) throws SQLException {
		throw new UnsupportedOperationException("Method setString() not yet implemented.");
	}

	public int setString(long l, String s) throws SQLException {
		throw new UnsupportedOperationException("Method setString() not yet implemented.");
	}

	public void truncate(long l) throws SQLException {
		throw new UnsupportedOperationException("Method truncate() not yet implemented.");
	}

	public OutputStream setAsciiStream(long l) throws SQLException {
		throw new UnsupportedOperationException("Method setAsciiStream() not yet implemented.");
	}

	public long position(java.sql.Clob searchstr, long start) {
		throw new UnsupportedOperationException("Method position() not yet implemented.");
	}

	public void free() throws SQLException {
		
	}

	public Reader getCharacterStream(long pos, long length) throws SQLException {
		return null;
	}
}