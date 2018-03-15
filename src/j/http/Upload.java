package j.http;

import j.log.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

public class Upload {
	private static Logger log=Logger.create(Upload.class);//output log
	
	private int totalLength;//上传内容总长度

	private int totalRead;//已经读取内容长度

	private HttpServletRequest request;//上传文件的http请求

	private ServletInputStream sis;//上传文件的输入流

	private int allowFileSize;//允许上传的文件大小

	private String encoding;//文件内容编码，不指定则为null

	private String absoluteFileName_Uploading;//当前解析的文件在客户端电脑上的绝对路径

	private String fileName_Uploading;//当前解析的文件在客户端电脑上的文件名

	private String fileExt_Uploading;//当前解析的文件在客户端电脑上的后缀名
	
	private String parameterName;//当前解析的文件在上传文件网页中的文件浏览框的name值

	private String absoluteFileName_Saved;//当前解析的文件存储到服务器上的绝对路径

	private String fileName_Saved;//当前解析的文件存储到服务器上的文件名

	private String fileExt_Saved;//当前解析的文件存储到服务器上的后缀名

	private List uploadedFiles;//解析出的文件

	private String mimeBoundary="";//上传内容分界线

	private String destinationPath;//保存到服务器的路径
	
	private boolean saveInTempFile;//是否保存到临时文件
	
	private Map otherParameters;//key - value pair。从上传内容中解析出的非文件字符串参数，即网页中和file一起使用的input、textarea等输入元素，不包括form的action中带的参数，form的action中带的参数直接用request.getParameter获得

	/**
	 * constructor
	 * 
	 * @param _request
	 * @param _path
	 * @param _encoding
	 * @param _allowFileSize 单位K
	 * @param saveInTempFile
	 * @throws IOException
	 */
	public Upload(HttpServletRequest _request,String _path, String _encoding,int _allowFileSize,boolean saveInTempFile) throws IOException {
		this.otherParameters=new HashMap();
		this.saveInTempFile=saveInTempFile;
		if(_allowFileSize<0){
			allowFileSize=0x100000;//1M
		}else{
			allowFileSize = _allowFileSize*1024;//_allowFileSize K
		}
		
		request = _request;
		encoding = _encoding;
		destinationPath = _path;
		
		if(destinationPath!=null&&!"".equals(destinationPath)){
			if (!destinationPath.endsWith("\\")&&!destinationPath.endsWith("/")) {
				destinationPath += "/";
			}
			File file=new File(destinationPath);
			if(!file.exists()) file.mkdirs();
		}
		
		
		totalLength = _request.getContentLength();
		sis = _request.getInputStream();
		totalRead = 0;
		String contentType = request.getContentType();

		if (contentType != null&& contentType.indexOf(",") != -1) {
			contentType = contentType.substring(0, contentType.indexOf(","));
		}if (contentType != null&& contentType.startsWith("multipart/form-data")) {
			mimeBoundary = "--" + contentType.substring(contentType.indexOf("boundary=") + "boundary=".length());
		}

		uploadedFiles = new LinkedList();
		
		//Olog.log(JUtilInputStream.string(sis),-1);
	}		

	/**
	 * 解析并保存文件
	 * 
--AEAoI1YFLSqY00gzaSXTeUySEzUQAnwiaWb
Content-Disposition: form-data; name="myfile"; filename="1.jpg"
Content-Type: application/octet-stream; charset=utf-8
Content-Transfer-Encoding: binary

..............................
--AEAoI1YFLSqY00gzaSXTeUySEzUQAnwiaWb
Content-Disposition: form-data; name="name1"
Content-Type: text/plain; charset=US-ASCII
Content-Transfer-Encoding: 8bit

xxxxxxxxxxx
--AEAoI1YFLSqY00gzaSXTeUySEzUQAnwiaWb
Content-Disposition: form-data; name="name2"
Content-Type: text/plain; charset=US-ASCII
Content-Transfer-Encoding: 8bit

yyyyyyyyyyy
--AEAoI1YFLSqY00gzaSXTeUySEzUQAnwiaWb--
	 * @throws IOException
	 */
	public UploadMsg save() {
		UploadMsg msg = new UploadMsg();
		msg.allowK=allowFileSize/1024;
		try {
			//文件大小超过限制
			if (request.getContentLength() > allowFileSize) {
				msg.isSuccessful=false;
				msg.result=UploadMsg.RESULT_TOO_LARGE;
				return msg;
			}

			String line = readLine();
			//System.out.print("j.http.Upload << [1]"+line);
			do {
				if (line==null) {//结束
					break;
				}

				//格式不正确，文件没有正确的边界
				if (line.indexOf(mimeBoundary)<0) {
					msg.isSuccessful=false;
					msg.result=UploadMsg.RESULT_BAD_FORMAT;
					return msg;
				} else {
					line=readLine();//Content-Disposition
					//System.out.print("j.http.Upload << "+line);
					if (line==null) {//结束
						break;
					}
					//Content-Disposition: form-data; name="attach"; filename="C:\Documents and Settings\Administrator\桌面\新建 文本文档.txt"
					//得到文件名
					String disposition = "content-disposition: form-data;";
					
					if(line.toLowerCase().indexOf("filename")==-1
							&&line.toLowerCase().startsWith(disposition)){//如果不是文件，是普通参数
						int paraNamePosition=line.indexOf("name=\"")+"name=\"".length();
						String paraName=line.substring(paraNamePosition,line.indexOf("\"",paraNamePosition));
						String paraValue="";
					
						do{
							line=readLine();
							//System.out.print("j.http.Upload << [2]"+line);
						}while(line!=null
								&&!line.equals("\r")
								&&!line.equals("\r\n"));
						
						do{
							line=readLine();
							//System.out.print("j.http.Upload << [3]"+line);
							if(line!=null&&line.indexOf(mimeBoundary)<0){
								paraValue+=line;
							}							
						}while(line!=null
								&&line.indexOf(mimeBoundary)<0);
						
						if(paraValue.endsWith("\r\n")){
							paraValue=paraValue.substring(0,paraValue.length()-2);
						}else if(paraValue.endsWith("\r")){
							paraValue=paraValue.substring(0,paraValue.length()-1);
						}
						
						this.otherParameters.put(paraName,paraValue);
					}else if (line.toLowerCase().startsWith(disposition)) {//如果是文件						
						this.getAbsoluteFileName(line);
						this.getParameterName(line);
						this.getFileName();
						this.getFileExt();
	
						//上传文件基本信息
						UploadedFile uploadedFile = new UploadedFile();
						uploadedFile.setAbsoluteFileName_Uploading(this.absoluteFileName_Uploading);
						uploadedFile.setFileName_Uploading(this.fileName_Uploading);
						uploadedFile.setFileExt_Uploading(this.fileExt_Uploading);
						uploadedFile.setParameterName(this.parameterName);
						//上传文件基本信息 end
	
						do{
							line=readLine();
							//System.out.print("j.http.Upload << [4]"+line);
						}while(line!=null
								&&!line.equals("\r")
								&&!line.equals("\r\n"));
	
						//创建目标文件
						File file =null;
									
						if(!this.saveInTempFile){
							File dir = new File(destinationPath);
							if (!dir.exists()) {
								dir.mkdir();
							}	
							fileName_Saved = calculateFileName_Saved();
							file = new File(destinationPath + fileName_Saved);
							file.createNewFile();					
						}else{
							if(this.fileExt_Saved==null) file=File.createTempFile("jframework.","");
							else file=File.createTempFile("jframework.","."+this.fileExt_Saved);
						}
						this.fileName_Saved=file.getName();
						this.absoluteFileName_Saved = file.getAbsolutePath();//保存文件的绝对路径
						//创建目标文件 end
	
						//保存文件基本信息
						uploadedFile.setAbsoluteFileName_Saved(this.absoluteFileName_Saved);
						uploadedFile.setFileName_Saved(this.fileName_Saved);
						uploadedFile.setFileExt_Saved(this.fileExt_Saved);
						//end
	
						//写文件
						BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(new FileOutputStream(file));
						byte buffer[] = new byte[1024];
						String isEnter = null;//是否回车换行，去掉最后一个回车换行
						do {//读取每一行
							int i = read(buffer, buffer.length);
							if (i <= 0) {
								break;
							}
							line = new String(buffer, 0, i);
							//System.out.print("j.http.Upload << [5]"+line);
	
							if (line.indexOf(mimeBoundary)<0) {
								if (isEnter!=null) {
									bufferedoutputstream.write(isEnter.getBytes());
									isEnter = null;
								}
								if (line.endsWith("\r\n")) {
									isEnter = "\r\n";
									bufferedoutputstream.write(buffer, 0, i - 2);
								}else if (line.endsWith("\r")) {
									isEnter = "\r";
									bufferedoutputstream.write(buffer, 0, i - 1);
								} else {
									bufferedoutputstream.write(buffer, 0, i);
								}
							}
						} while (line.indexOf(mimeBoundary)<0);
	
						
						bufferedoutputstream.flush();
						bufferedoutputstream.close();
						uploadedFiles.add(uploadedFile);
						//写文件 ends
					}
				}
			} while (true);
			
			msg.isSuccessful = true;
			msg.result=UploadMsg.RESULT_SUCCESS;
			
			return msg;
		} catch (Exception e) {
			log.log(e,Logger.LEVEL_ERROR);
			msg.isSuccessful = false;
			msg.result=UploadMsg.RESULT_EXCEPTION;
			msg.e=e;
			return msg;
		}
	}

	/**
	 * 读取一行
	 * 
	 * @return @throws IOException
	 */
	private String readLine() throws IOException {
		int readBytes = -1;
		byte buffer[] = new byte[10240];//10k
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		//读取一行
		do {
			readBytes = read(buffer, buffer.length);//如果当前读取行小于buffer.length，则读取该行全部，否则读取buffer.length个字节
			if (readBytes != -1) {
				os.write(buffer, 0, readBytes);
			}
		} while (readBytes == buffer.length);
		//读取一行 end

		os.flush();
		byte content[] = os.toByteArray();

		if (content.length == 0) {
			return null;
		} else {
			String str="";
			if(encoding!=null&&encoding.length()>0){
				str= new String(content, 0, content.length, encoding);
			}else{
				str= new String(content, 0, content.length);
			}

			return str;
		}
	}

	/**
	 * 读取length个字节
	 * 
	 * @param buffer
	 * @param length
	 * @return @throws IOException
	 */
	private int read(byte buffer[], int length)throws IOException {
		if (totalRead >= totalLength) {
			return -1;
		}
		if (length > totalLength - totalRead) {
			length = totalLength - totalRead;
		}
		int readBytes = sis.readLine(buffer, 0, length);
		totalRead += readBytes;
		return readBytes;
	}	
	
	/**
	 * 文件的参数名
	 * 
	 * @param line
	 */
	private void getParameterName(String line){
		String disposition = "content-disposition: form-data;";
		String paraNameFlag = "form-data; name=\"";
		int i = -1;

		//如果不是以"content-disposition: form-data"开头
		if (!line.toLowerCase().startsWith(disposition)) {
			return;
		}//end

		//如果没有"filename="串
		i = line.indexOf(paraNameFlag);
		if (i == -1) {
			return;
		}//end

		i += paraNameFlag.length();
		int k = line.indexOf("\"", i);
		this.parameterName=line.substring(i, k);
	}	

	/**
	 * 文件绝对路径名
	 * 
	 * @param line
	 */
	private void getAbsoluteFileName(String line) {
		String disposition = "content-disposition: form-data;";
		String filenameFlag = "filename=\"";
		int i = -1;

		//如果不是以"content-disposition: form-data"开头
		if (!line.toLowerCase().startsWith(disposition)) {
			return;
		}//end

		//如果没有"filename="串
		i = line.indexOf(filenameFlag);
		if (i == -1) {
			return;
		}//end

		i += filenameFlag.length();
		int k = line.indexOf("\"", i);
		absoluteFileName_Uploading = line.substring(i, k);
	}

	/**
	 * 得到文件名
	 *  
	 */
	private void getFileName() {
		int i = absoluteFileName_Uploading.lastIndexOf("\\");
		fileName_Uploading = absoluteFileName_Uploading.substring(i + 1,absoluteFileName_Uploading.length());
	}

	/**
	 * 得到文件扩展名
	 * 
	 * @return
	 */
	private void getFileExt() {
		int i = fileName_Uploading.lastIndexOf(".") + 1;
		if (i > 0) {
			fileExt_Uploading = fileName_Uploading.substring(i).toLowerCase();
			fileExt_Saved = fileExt_Uploading;//保存文件的扩展名
		}
	}

	/**
	 * 计算保存文件名，如果文件存在，在文件名前加"#"，如果仍然存在，继续在前面加"#"...
	 *  
	 */
	private String calculateFileName_Saved() {
		String ret = "";
		ret = fileName_Uploading;
		while (true) {
			File file = new File(destinationPath + ret);
			if (file.exists()) {
				ret = "#" + ret;
			} else {
				return ret;
			}
		}
	}

	/**
	 * 得到上传文件基本信息（一个或多个）的列表，元素为UploadFile
	 * 
	 * @return
	 */
	public List getUploadedFiles() {
		return uploadedFiles;
	}
	
	/**
	 * 得到上传文件基本信息（一个或多个）的列表，元素为UploadFile，其在上传文件网页的文件浏览框的name值为paraName
	 * @param paraName
	 * @return
	 */
	public UploadedFile[] getUploadedFiles(String paraName){
		List ret=new LinkedList();
		for(int i=0;i<uploadedFiles.size();i++){
			UploadedFile f=(UploadedFile)uploadedFiles.get(i);
			if(paraName.equals(f.getParameterName())) ret.add(f);
		}
		return (UploadedFile[])ret.toArray();
	}
	
	/**
	 * 从上传内容中解析出的非文件字符串参数，即网页中和file一起使用的input、textarea等输入元素，不包括form的action中带的参数，
	 * form的action中带的参数直接用request.getParameter获得
	 * @return
	 */
	public Map getOtherParameters(){
		return this.otherParameters;
	}
}