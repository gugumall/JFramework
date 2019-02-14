package j.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author 肖炯
 *
 */
public class Download {
    private HttpServletResponse response;
    private String contentType;

    /**
     * constructor
     * @param _context
     * @param _response
     * @param _contentType
     */
    public Download(HttpServletResponse _response, String _contentType){
        response = _response;
        contentType = _contentType;
    }
    
    /**
     * 下载字节数组
     * @param response
     * @param content
     * @param fileName
     * @throws IOException
     * @throws ServletException
     */
	public void downloadBytes(byte content[], String fileName) throws IOException,ServletException {
		if (content == null || content.length == 0){
			throw new ServletException("Content length is zero");
		}
		response.setContentType(contentType);
		response.setContentLength(content.length);
		
		//set header		
		String header="attachment; filename=\"" + fileName + "\"";
		response.setHeader("Content-Disposition",header);
		//end
		
		ServletOutputStream servletoutputstream = response.getOutputStream();
		servletoutputstream.write(content);
		servletoutputstream.flush();
		return;
	}

	/**
     * 下载文件
	 * @param filePath
	 * @param fileName
	 * @throws ServletException
	 * @throws IOException
	 */
	public void downloadFile(String filePath,String fileName) throws ServletException,IOException {
		File file = new File(filePath);
		if (!file.exists()){
			throw new IOException("Can not find the file");
		}
		if (!file.canRead()){
			throw new IOException("The file is not readable");
		}
		if (fileName == null || fileName.equals("")){
			fileName = file.getName();
		}
		downloadInputStream(new FileInputStream(file),fileName);
	}
	
	/**
     * 下载输入流
	 * @param inputstream
	 * @param fileName
	 * @throws IOException
	 * @throws ServletException
	 */
	public void downloadInputStream(InputStream inputstream, String fileName)throws IOException, ServletException {
		response.setContentType(contentType);
		response.setContentLength(inputstream.available());
		
		//set header		
		String header="attachment; filename=\"" + fileName + "\"";
		response.setHeader("Content-Disposition",header);
		//end
		
		ServletOutputStream servletoutputstream = response.getOutputStream();
		
		byte[] buffer=new byte[1024];
		
		int total=0;
		int readed=inputstream.read(buffer);
		while(readed>-1){
			total+=readed;
			servletoutputstream.write(buffer,0,readed);
			readed=inputstream.read(buffer);
		}
		
		response.setContentLength(total);
		
		try{
			inputstream.close();
			inputstream=null;
		}catch(Exception e){}
		
		servletoutputstream.flush();
		return;
	}
}