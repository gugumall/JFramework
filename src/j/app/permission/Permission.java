package j.app.permission;

import j.Properties;
import j.app.Constants;
import j.app.sso.User;
import j.log.Logger;
import j.security.AES;
import j.security.StringEncrypt;
import j.sys.SysConfig;
import j.util.JUtilDom4j;
import j.util.JUtilRandom;
import j.util.JUtilString;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 权限控制
 * @author 肖炯
 *
 */
public class Permission implements Runnable{
	private static Logger log=Logger.create(Permission.class);
	private static List resources=new LinkedList();//需要权限控制的资源
	private static List passports=new LinkedList();//通行证（String）列表
	private static String noPermissionPage;//无权限时，默认转向页面
	private static long configLastModified=0;//配置文件上次修改时间
	private static boolean loading=false;
		
	
	
	static{
		try{
			load();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_FATAL);
		}
		
		Permission m=new Permission();
		Thread thread=new Thread(m);
		thread.start();
		log.log("Permission monitor thread started.",-1);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getNoPermissionPage(){
		waitWhileLoading();
		return noPermissionPage;
	}
	
	/**
	 * 判断用户所访问的资源是否需要身份认证
	 * @param request
	 * @param user
	 * @return
	 */
	public static Resource permission(HttpServletRequest request,User user){
		waitWhileLoading();
		for(int i=0;i<resources.size();i++){
			Resource r=(Resource)resources.get(i);	
			if(r.matchesComplete(request)&&r.isUserInRole(user)) return null;
			
			if(r.matches(request)&&!r.isUserInRole(user)) return r;//匹配且无权限
		}
		
		return null;
	}	
	
	
	/**
	 * 是否持有有效通行证
	 * @param request
	 * @return
	 */
	public static boolean hasValidPassport(HttpServletRequest request){
		waitWhileLoading();
		if(request==null){
			return false;
		}
		
		String passport=request.getParameter(Constants.SSO_PASSPORT);
		if(passport==null){
			return false;
		}
		
		passport=JUtilString.decodeURI(passport, SysConfig.sysEncoding);
		passport=AES.decrypt(passport, SysConfig.getAesKey(), SysConfig.getAesOffset());
		
		return passports.contains(passport);
	}
	
	
	/**
	 * 随机获取一个通行证
	 * @return
	 */
	public static String getSSOPassport(){
		waitWhileLoading();
		int index=JUtilRandom.nextInt(passports.size());
		String p=(String)passports.get(index);
		p=AES.encrypt(p, SysConfig.getAesKey(), SysConfig.getAesOffset());
		p=JUtilString.encodeURI(p, SysConfig.sysEncoding);
		return p;
	}
	
	
	/**
	 * 从配置文件装载系统配置信息
	 * @throws Exception
	 */
	private static void load(){
		try{
			loading=true;
			
			resources.clear();
			passports.clear();
								
			File file = new File(j.Properties.getConfigPath()+"permission.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
	        
			//create jdom document
			Document doc = JUtilDom4j.parse(file.getAbsolutePath(),"UTF-8");
			Element root = doc.getRootElement();
	        //create jdom document ends
			
			Permission.noPermissionPage=root.elementText("no-permission-page");
	 
	        //从配置文件得到需要身份认证的资源列表
	        Element urlsEle=root.element("urls");
	        List urls=urlsEle.elements("url");
	        for(int i=0;urls!=null&&i<urls.size();i++){
	        	Element rEle=(Element)urls.get(i);
	        	
	        	ResourceUrl r=new ResourceUrl();
	        	r.setUrlPattern(rEle.attributeValue("pattern"));
	        	r.setMode(rEle.attributeValue("mode"));
	        	r.setDomains(rEle.attributeValue("include-domains"), rEle.attributeValue("exclude-domains"));
	        	r.setRoles(rEle.attributeValue("roles"));
	        	r.setNoPermissionPage(rEle.attributeValue("no-permission-page"));
	        	r.setLoginPage(rEle.attributeValue("login-page"));
	        	
	        	List excludes=rEle.elements("exclude");
	        	for(int j=0;excludes!=null&&j<excludes.size();j++){
	            	Element ex=(Element)excludes.get(j);
	        		r.addExclude(ex.getText());
	        	}
	        	resources.add(r);
	        	
	        	log.log(r.toString(),-1);
	        } 
	        
	        Element actionsEle=root.element("actions");
	        List actions=actionsEle.elements("action");
	        for(int i=0;actions!=null&&i<actions.size();i++){
	        	Element rEle=(Element)actions.get(i);
	        	
	        	ResourceAction r=new ResourceAction();
	        	r.setPath(rEle.attributeValue("path"));
	        	r.setActionId(rEle.attributeValue("id"));
	        	r.setRoles(rEle.attributeValue("roles"));
	        	r.setNoPermissionPage(rEle.attributeValue("no-permission-page"));
	        	r.setLoginPage(rEle.attributeValue("login-page"));
	        	
	        	List excludes=rEle.elements("exclude");
	        	for(int j=0;excludes!=null&&j<excludes.size();j++){
	            	Element ex=(Element)excludes.get(j);
	        		r.addExclude(ex.getText());
	        	}
	        	
	        	resources.add(r);
	        	
	        	log.log(r.toString(),-1);
	        } 
	        
	        /*
	         * 客户端通过请求名为sso_info_getter.htm的页面来使得当前访问的应用从SSO SERVER获取登录信息，
	         * 任何人没有访问sso_info_getter.htm的权限，访问它均会转向名为sso_info_getter_login.htm的页面
	         */
	        ResourceUrl r=new ResourceUrl();
	        r.setUrlPattern(Constants.SSO_INFO_GETTER);
	        r.setRoles("none");
	        r.setNoPermissionPage(Constants.SSO_INFO_GETTER_LOGIN);
	        r.setLoginPage(Constants.SSO_INFO_GETTER_LOGIN);
	        resources.add(r);
	    	log.log(r.toString(),-1);
	        //从配置文件得到需要身份认证的资源列表 ends	 
	    	
	
	        
	        //生成通行证，用于服务器间通信，拥有此通行证的请求不经过任何权限认证
	        String passportTxt=root.elementText("passports");
	        String[] tokens=passportTxt.split("\\s{4}");
	        for(int i=0;i<tokens.length;i++){
	        	String[] cells=tokens[i].split("\\s{2}");
	        	passports.add(StringEncrypt.decrypt(cells[0],cells[1]));
	        }
	        //生成通行证，用于服务器间通信，拥有此通行证的请求不经过任何权限认证 ends
			

			//配置文件最近修改时间
			File configFile=new File(Properties.getConfigPath()+"permission.xml");
			configLastModified=configFile.lastModified();
			configFile=null;
			
			loading=false;
		}catch(Exception e){
			loading=false;
			log.log(e,Logger.LEVEL_ERROR);
		}
	}
	
	/**
	 * 
	 *
	 */
	private static void waitWhileLoading(){
		while(loading){
			try{
				Thread.sleep(100);
			}catch(Exception ex){}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * 检测permission.xml是否修改过，如修改过重新加载配置
		 */
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}

			File configFile=new File(Properties.getConfigPath()+"permission.xml");
			if(configLastModified<configFile.lastModified()){
				log.log("permission.xml has been modified, so reload it.",-1);
				load();
			}
			configFile=null;
		}
	}
}
