package j.I18N;

import j.log.Logger;
import j.sys.SysConfig;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * 
 * @author 肖炯
 * 
 */
public class I18NResponseWrapper extends HttpServletResponseWrapper {
	private static Logger log=Logger.create(I18NResponseWrapper.class);
	protected HttpServletResponse wrappedResponse = null;
	protected ServletOutputStream stream = null;
	protected PrintWriter writer = null;

	/**
	 * 
	 * @param response
	 */
	public I18NResponseWrapper(HttpServletResponse response) {
		super(response);
		wrappedResponse = response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#getOutputStream()
	 */
	public ServletOutputStream getOutputStream() throws IOException {
		if (writer != null) {
			throw new IllegalStateException("getWriter() has already been called!");
		}

		if (stream == null){
			stream = createOutputStream();
		}
		
		return stream;
	}

	/*
	 *  (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getWriter()
	 */
	public PrintWriter getWriter() throws IOException {
		if (writer != null) {
			return (writer);
		}

		if (stream != null) {
			throw new IllegalStateException("getOutputStream() has already been called!");
		}

		stream = createOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(stream, SysConfig.sysEncoding));
		return writer;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private ServletOutputStream createOutputStream() throws IOException {
		return (new I18NOutputStream(wrappedResponse));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#flushBuffer()
	 */
	public void flushBuffer() throws IOException {
		if(writer!=null) writer.flush();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getContent(){
		try {
			flushBuffer();
		} catch (Exception e){
			log.log(e,Logger.LEVEL_ERROR);
		}
		
		if(stream==null) return "";
		
		try {
			return ((I18NOutputStream)stream).getContent(SysConfig.sysEncoding);
		} catch (Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return null;
		}
	}
}