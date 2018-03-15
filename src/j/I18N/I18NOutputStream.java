package j.I18N;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

public class I18NOutputStream extends ServletOutputStream {
	protected ByteArrayOutputStream bos = null;

	/**
	 * 
	 * @param response
	 * @throws IOException
	 */
	public I18NOutputStream(HttpServletResponse response) throws IOException {
		bos = new ByteArrayOutputStream();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		bos.write((byte) b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void write(byte b[], int off, int len) throws IOException {
		bos.write(b, off, len);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	public void close() throws IOException {
		bos.flush();
		bos.close();
	}

	/**
	 * 
	 * @param encoding
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected String getContent(String encoding) throws UnsupportedEncodingException {
		return bos.toString(encoding);
	}

	/**
	 * 
	 * @return
	 */
	protected String getContent() {
		return bos.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletOutputStream#isReady()
	 */
	public boolean isReady() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletOutputStream#setWriteListener(javax.servlet.WriteListener)
	 */
	public void setWriteListener(WriteListener listener) {
	}
}