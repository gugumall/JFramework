package j.test;

import j.Properties;
import j.app.webserver.JHandler;
import j.app.webserver.JSession;
import j.http.JHttp;
import j.http.Upload;
import j.http.UploadMsg;
import j.http.UploadedFile;
import j.sys.SysUtil;
import j.util.JUtilImage;
import j.util.JUtilList;
import j.util.JUtilUUID;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class TestUpload extends JHandler {	
	public static final String RESULT_SUCCESS="1";//上传结果-上传成功
	public static final String RESULT_FAIL="0";//上传结果-上传失败
	public static final String RESULT_ERROR="-1";//上传结果-系统错误
	
	public static Map businessCfg=new HashMap();
	static{
		businessCfg.put("PUBLISH",new String[]{"1024","F",",JPG,PNG,GIF,SWF,JSP,HTML,HTM,XML,JS,CSS,TXT,CLASS,","160"});//允许文件大小（KB），是否生成缩略图，允许文件格式，缩略图大小 
		businessCfg.put("APPROVE",new String[]{"1024","F",",JPG,PNG,GIF,SWF,JSP,HTML,HTM,XML,JS,CSS,TXT,CLASS,","160"});//允许文件大小（KB），是否生成缩略图，允许文件格式，缩略图大小 
	}
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void pipe(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception {
		JHttp jhttp=JHttp.getInstance();
		jsession.resultString=jhttp.pipe(request,"POST","www.jstudio.me",80,"/jstudio/upload.handler?request=upload&from="+request.getParameter("from"));
	}
	

	/**
	 * 
	 * @param jsession
	 * @param sessiond
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void upload(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception {
		String from=request.getParameter("from");
		String configFileName=request.getParameter("config_file_name");
		if(from==null||businessCfg.get(from)==null){			
			jsession.resultString="-2";
			return;
		}
		String[] cfg=(String[])businessCfg.get(from);
		
		List files = null;
		try {
			Upload uploader = new Upload(request, 
					"",
					null,
					Integer.parseInt(cfg[0]),
					true);
			
			UploadMsg upMsg = uploader.save();//保存附件

			/*
			 * 如果上传成功，做相应处理
			 */
			if (upMsg.isSuccessful) {
				boolean valid=true;
				files = uploader.getUploadedFiles();
				if(files.size()==0){
					System.out.println("invalid 1");
					valid=false;
				}
				else{
					for (int i = 0; i < files.size(); i++) {
						UploadedFile upFile = (UploadedFile) files.get(i);
						String fileName = upFile.getAbsoluteFileName_Saved();//文件保存在临时文件夹的绝对路径
						String ext=upFile.getFileExt_Uploading();
						
						if(ext==null||cfg[2].indexOf(","+ext.toUpperCase()+",")<0){
							valid=false;
							System.out.println("invalid 2");
							break;
						}else{
							File file = new File(fileName);//文件对象
							if(file.length()==0){//空文件
								valid=false;
								System.out.println("invalid 3");
								break;
							}
						}					
					}
				}
				
				if(!valid){//格式不正确，或者有空文件，删除残留文件
					for (int i = 0; i < files.size(); i++) {
						UploadedFile upFile = (UploadedFile) files.get(i);
						File file = new File(upFile.getAbsoluteFileName_Saved());//文件对象
						if(file.exists()){
							file.delete();
						}					
					}
					jsession.resultString="-3";
					return;
				}
				
				//指定保存文件，直接保存
				if(configFileName!=null&&!"".equals(configFileName)&&files.size()==1){
					for (int i = 0; i < files.size(); i++) {
						UploadedFile upFile = (UploadedFile) files.get(i);
						File file = new File(upFile.getAbsoluteFileName_Saved());//文件对象
						
						File newFile=new File(Properties.getWebRoot() + configFileName);
						
						if(!newFile.getParentFile().exists()){//如果目录不存在,创建
							newFile.getParentFile().mkdirs();
						}else if(newFile.exists()){
							newFile.delete();
						}
						file.renameTo(newFile);//将文件从临时目录移动到正式目录下
					}
					jsession.resultString="1";
				}else{					
					String urls="";
					for (int i = 0; i < files.size(); i++) {
						String key=JUtilUUID.genUUID();
						UploadedFile upFile = (UploadedFile) files.get(i);
						File file = new File(upFile.getAbsoluteFileName_Saved());//文件对象
						String ext=upFile.getFileExt_Uploading();
						
						String newPath=null;
						if(ext!=null) newPath="file/"+ (new Timestamp(SysUtil.getNow())).toString().substring(0,10)+ "/"+key+"."+ upFile.getFileExt_Uploading();
						else newPath="file/"+ (new Timestamp(SysUtil.getNow())).toString().substring(0,10)+ "/"+key;
						
						//文件存放路径: 系统根目录下的file目录+年月日+附件保存在数据库中的ID+.+后缀名
						File newFile=new File(Properties.getWebRoot() + newPath);
						
						if(!newFile.getParentFile().exists()){//如果目录不存在,创建
							newFile.getParentFile().mkdirs();
						}
						Thread.sleep(10000);
						file.renameTo(newFile);//将文件从临时目录移动到正式目录下
						
						urls+="/"+newPath+";";
						
						if(cfg[1].equals("T")
								&&ext!=null&&
								(ext.equalsIgnoreCase("JPG")
										||ext.equalsIgnoreCase("GIF")
										||ext.equalsIgnoreCase("PNG"))){
							//生成缩略图					
							File logoFile=new File(Properties.getWebRoot() 
									+ "file/"
									+ (new Timestamp(SysUtil.getNow())).toString().substring(0,10)+ "/"+key+".small."
									+ upFile.getFileExt_Uploading());
							
							JUtilImage iu=new JUtilImage();
							iu.zoomToSize(newFile,logoFile,Integer.parseInt(cfg[3]),JUtilImage.FORMAT_JPEG);
							iu=null;
						}
						
						file=null;
						newFile=null;
					}
					JUtilList.clear_AllNull(files);
					
					if(urls.length()>0) urls=urls.substring(0,urls.length()-1);
					
					jsession.resultString="1|"+urls;
				}
			} else {
				jsession.resultString="0";
			}
		} catch (Exception e) {
			if(files!=null){//删除残留文件
				try{
					for (int i = 0; i < files.size(); i++) {
						UploadedFile upFile = (UploadedFile) files.get(i);
						File file = new File(upFile.getAbsoluteFileName_Saved());//文件对象
						if(file.exists()){
							file.delete();
						}					
					}
				}catch(Exception ex){}
			}
			jsession.resultString="-1";
		}
	}
}
