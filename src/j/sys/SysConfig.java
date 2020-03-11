package j.sys;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import j.app.online.CssCompatible;
import j.app.webserver.Handler;
import j.app.webserver.Handlers;
import j.log.Logger;
import j.util.ConcurrentMap;
import j.util.JUtilString;


/**
 * @author 肖炯
 *
 */
public class SysConfig{	
	private static Logger log=Logger.create(SysConfig.class);
	
	private static String sysId;//系统ID，也是作为sso client的client ID
	
	private static String machineId;//物理服务器ID
	
	public static String sysEncoding="UTF-8";//字符编码
	
	public static String[] responseEncodingPages=null;//哪些页面需调用response.setContentType("text/html;charset="+SysConfig.sysEncoding)

	public static String errorPage;//发生错误时转向页面
	
	public static String databaseName;//数据库名
	public static long   minUuid;//自增uuid开始值
	public static long   maxUuid;//自增uuid最大值
	public static String   dbKeyPrefix="";
	
	
	static{
		try{
			init();//初始化系统设置	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_WARNING);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getSysId(){
		return sysId;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getMachineID(){
		return machineId;
	}

	/**
	 * 从配置文件装载系统配置信息
	 * @throws Exception
	 */
	private static void init()throws Exception{
		//文件是否存在
		File file = new File(j.Properties.getConfigPath()+"sys.xml");
        if(!file.exists()){
        	log.log("找不到配置文件："+file.getAbsolutePath(),-1);
        	return;
        }
        
		//解析
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new FileInputStream(file),"UTF-8");
		Element root = doc.getRootElement();
		
      	//系统ID
      	SysConfig.sysId=root.elementText("sys-id");
      	log.log("SysConfig.sysId:"+SysConfig.sysId,-1);
		
      	//物理服务器ID
      	SysConfig.machineId=root.elementText("machine-id");
      	if(SysConfig.machineId==null) SysConfig.machineId=SysConfig.sysId;
      	log.log("SysConfig.machineId:"+SysConfig.machineId,-1);
      	
      	//错误信息页面
      	SysConfig.errorPage=root.elementText("error-page");
      	log.log("SysConfig.errorPage:"+SysConfig.errorPage,-1);
      	   	   	
      	
      	//系统编码
      	SysConfig.sysEncoding=root.elementText("sys-encoding");
      	log.log("SysConfig.sysEncoding:"+SysConfig.sysEncoding,-1);
      	
      	//哪些页面需调用response.setContentType("text/html;charset="+SysConfig.sysEncoding)
      	String responseEncodingPagesStr=root.elementText("responseEncodingPages");		
		if(responseEncodingPagesStr!=null){
			responseEncodingPages=responseEncodingPagesStr.split(";");
		}		
      	log.log("SysConfig.responseEncodingPages:"+root.elementText("responseEncodingPages"),-1);

        
        //数据库
        Element databaseE=root.element("database");
      	SysConfig.databaseName=databaseE.attributeValue("name");
      	log.log("SysConfig.databaseName:"+SysConfig.databaseName,-1);

      	SysConfig.minUuid=Long.parseLong(databaseE.attributeValue("min-uuid"));
      	log.log("SysConfig.minUuid:"+SysConfig.minUuid,-1);

      	SysConfig.maxUuid=Long.parseLong(databaseE.attributeValue("max-uuid"));
      	log.log("SysConfig.maxUuid:"+SysConfig.maxUuid,-1);
      	
      	SysConfig.dbKeyPrefix=databaseE.attributeValue("db_key_prefix")==null?"":databaseE.attributeValue("db_key_prefix");
      	log.log("SysConfig.dbKeyPrefix:"+SysConfig.dbKeyPrefix,-1);
	}	
	
	/**
	 * 是否需要设置response的encoding
	 * @param requestURL
	 * @return
	 */
	public static boolean needSettingResponseEncoding(String requestURI){
		//RESTful 支持
		Handler handler=null;
		String pattern=Handlers.isActionPath(requestURI);
		if(pattern!=null){
			if(requestURI.endsWith(pattern)){//常规方式
				String path=requestURI.substring(0,requestURI.indexOf(pattern));
				handler=Handlers.getHandler(path);
			}else{//RESTful方式
				handler=Handlers.getHandler(requestURI);
			}
		}
		if(handler!=null){
			requestURI=handler.getPath()+handler.getPathPattern();
		}
		//RESTful 支持		
				
		for(int i=0;i<responseEncodingPages.length;i++){
			if("".equals(responseEncodingPages[i])) continue;
			
			if(JUtilString.match(requestURI,responseEncodingPages[i],"|-|")>-1){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getUserAgentType(HttpServletRequest request){
		String userAgent=request.getHeader("User-Agent");
		if(userAgent==null) userAgent="";
		
		if ((userAgent.indexOf("MSIE") >= 0) 
				&& (userAgent.indexOf("Opera") < 0) 
				&& (userAgent.indexOf("MSIE 9.0") < 0)
				&& (userAgent.indexOf("MSIE 10") < 0)
				&& (userAgent.indexOf("rv:11.0) like Gecko") < 0)){
				return "IE";
			}else if (userAgent.indexOf("MSIE 9.0") >= 0){
				return "IE9";
			}else if (userAgent.indexOf("MSIE 10") >= 0){
				return "IE10";
			}else if (userAgent.indexOf("rv:11.0) like Gecko") >= 0){
				return "IE11";
			}else if (userAgent.indexOf("Firefox") >= 0){
				return "FIREFOX";
			}else if (userAgent.indexOf("Opera") >= 0){
				return "OPERA";
			}else if (userAgent.indexOf("Chrome") >= 0){
				return "CHROME";
			}else{
				return "OTHER";	
			}
	}
} 