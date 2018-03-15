
package j.http;

/**
 * 包含上传文件的信息
 * @author JFramework
 *
 */
public class UploadedFile {
	private String absoluteFileName_Uploading;//文件在客户端电脑上的绝对路径
	private String fileName_Uploading;//文件在客户端电脑上的文件名（包括后缀名）
	private String fileExt_Uploading;//文件在客户端电脑上的文件名后缀
	private String absoluteFileName_Saved;//文件保存到服务器上的绝对路径
	private String fileName_Saved;//文件保存到服务器上的文件名（包括后缀名）
	private String fileExt_Saved;//文件保存到服务器上的文件名后缀
	private String parameterName;//上传文件网页上改文件浏览框所对应的name值
	
	public void setAbsoluteFileName_Uploading(String _absoluteFileName_Uploading){
		this.absoluteFileName_Uploading=_absoluteFileName_Uploading;
	}
	public void setFileName_Uploading(String _fileName_Uploading){
		this.fileName_Uploading=_fileName_Uploading;
	}
	public void setFileExt_Uploading(String _fileExt_Uploading){
		this.fileExt_Uploading=_fileExt_Uploading;
	}
	public void setAbsoluteFileName_Saved(String _absoluteFileName_Saved){
		this.absoluteFileName_Saved=_absoluteFileName_Saved;
	}
	public void setFileName_Saved(String _fileName_Saved){
		this.fileName_Saved=_fileName_Saved;
	}
	public void setFileExt_Saved(String _fileExt_Saved){
		this.fileExt_Saved=_fileExt_Saved;
	}
	public void setParameterName(String _parameterName){
		this.parameterName=_parameterName;
	}
	
	
	
	public String getAbsoluteFileName_Uploading(){
		return absoluteFileName_Uploading;
	}
	public String getFileName_Uploading(){
		return fileName_Uploading;
	}
	public String getFileExt_Uploading(){
		return fileExt_Uploading;
	}
	public String getAbsoluteFileName_Saved(){
		return absoluteFileName_Saved;
	}
	public String getFileName_Saved(){
		return fileName_Saved;
	}
	public String getFileExt_Saved(){
		return fileExt_Saved;
	}
	public String getParameterName(){
		return this.parameterName;
	}
		
}
