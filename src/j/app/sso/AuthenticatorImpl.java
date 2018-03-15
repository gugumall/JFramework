package j.app.sso;

import j.Properties;
import j.app.Constants;
import j.log.Logger;
import j.sys.SysUtil;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 
 * @author 肖炯
 *
 */
public class AuthenticatorImpl implements Authenticator,Runnable{
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.create(AuthenticatorImpl.class);
	private static ConcurrentMap users=new ConcurrentMap();
	private static long configLastModified=0;//配置文件上次修改时间
	
	static{
		load();
		
		AuthenticatorImpl m=new AuthenticatorImpl();
		Thread thread=new Thread(m);
		thread.start();
		log.log("AuthenticatorImpl monitor thread started.",-1);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public static UserInXml getUser(String userId){
		return (UserInXml)users.get(userId);
	}
	
	/**
	 * 
	 */
	public static void load(){
		try{
			users.clear();
			
			//文件是否存在
			File file = new File(Properties.getConfigPath()+"users.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			
			Document config=JUtilDom4j.parse(Properties.getConfigPath()+"users.xml","UTF-8");
			Element root=config.getRootElement();
			
			List userEles=root.elements("user");
			for(int i=0;i<userEles.size();i++){
				Element e=(Element)userEles.get(i);
				UserInXml user=new UserInXml(e.attributeValue("id"),
						e.attributeValue("pw"),
						e.attributeValue("name"),
						e.attributeValue("roles"));
				users.put(user.id, user);
			}

			//配置文件最近修改时间
			File configFile=new File(Properties.getConfigPath()+"users.xml");
			configLastModified=configFile.lastModified();
			configFile=null;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_FATAL);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see j.app.sso.Authenticator#login(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpSession, java.lang.String)
	 */
	public LoginResult login(HttpServletRequest request, HttpSession session,String clientIp) throws Exception {
		/*
		 * 登录流程
		 * 1，判断验证码是否正确
		 * 2，登录信息是否完整
		 * 3，用户是否存在（实际应用中可能需要判断用户状态是否有效等）
		 * 4，密码是否正确
		 */
		String userId=SysUtil.getHttpParameter(request,Constants.SSO_USER_ID);
		String userPwd=SysUtil.getHttpParameter(request,Constants.SSO_USER_PWD);
		
		LoginResult result=new LoginResult();
		result.setUserId(userId);
		
		//2，登录信息是否完整
		if(userId==null||userPwd==null){
			result.setResult(LoginResult.RESULT_BAD_REQUEST);
			return result;
		}
		
		//用户不存在
		if(!users.containsKey(userId)){
			result.setResult(LoginResult.RESULT_USER_NOT_EXISTS);
			return result;
		}
		
		UserInXml user=(UserInXml)users.get(userId);
		if(!userPwd.equals(user.pw)){
			result.setResult(LoginResult.RESULT_PASSWORD_INCORRECT);
			return result;
		}
		
		result.setResult(LoginResult.RESULT_PASSED);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see j.framework.sso.Authenticator#logout(javax.servlet.http.HttpSession)
	 */
	public void logout(HttpSession session) throws Exception {
	}



	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * 检测users.xml是否修改过，如修改过重新加载配置
		 */
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			if(configLastModified<=0) continue;

			File configFile=new File(Properties.getConfigPath()+"users.xml");
			if(configLastModified<configFile.lastModified()){
				log.log("users.xml has been modified, so reload it.",-1);
				load();
			}
			configFile=null;
		}
	}
}

/**
 * 
 * @author 肖炯
 *
 */
class UserInXml{
	String id;
	String pw;
	String name;
	String[] roles;
	
	/**
	 * 
	 * @param id
	 * @param pw
	 * @param name
	 * @param roles
	 */
	UserInXml(String id,String pw,String name,String roles){
		this.id=id;
		this.pw=pw;
		this.name=name;
		this.roles=roles.split(",");
	}
}
