package j.app.sso;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.client.HttpClient;

import j.app.Constants;
import j.app.permission.Permission;
import j.http.JHttp;
import j.http.JHttpContext;
import j.log.Logger;
import j.sys.SysUtil;
import j.util.ConcurrentList;
import j.util.JUtilMD5;

/**
 * sso client 通知线程
 * @author 肖炯
 *
 */
public class SSONotifier implements Runnable{
	private static Logger log=Logger.create(SSONotifier.class);
	private static Map notifiers=new HashMap();

	public static final String type_login="login";
	public static final String type_logout="logout";
	public static final String type_logout_all="logoutall";
	
	private ConcurrentList tasks=new ConcurrentList();
	private JHttp http;
	private HttpClient httpClient;
	
	static{
		ConcurrentList ssoClients=SSOConfig.getSsoClients();
		for(int i=0;i<ssoClients.size();i++){
			Client client=(Client)ssoClients.get(i);
			
			SSONotifier notifierSet[]=new SSONotifier[SSOConfig.getNotifiersPerClient()];
			for(int j=0;j<SSOConfig.getNotifiersPerClient();j++){
				notifierSet[j]=new SSONotifier();
				
				Thread thread=new Thread(notifierSet[j]);
				thread.start();
				log.log("sso client "+client.getId()+" 通知线程 "+j+" 已经启动",-1);
			}
			notifiers.put(client.getId(),notifierSet);
		}
	}
	
	/**
	 * constructor
	 *
	 */
	private SSONotifier(){
		http=JHttp.getInstance();
		httpClient=http.createClient(6000);//6秒超时
	}
	
	/**
	 * 
	 * @param client
	 * @return
	 */
	public static SSONotifier getNotifier(Client client){
		SSONotifier[] ns=(SSONotifier[])notifiers.get(client.getId());
		
		Random r=new Random();
		int i=r.nextInt(SSOConfig.getNotifiersPerClient());
		r=null;
		return ns[i];
	}
	
	/**
	 * 通知登录
	 * @param client
	 * @param globalSessionId
	 * @param userId
	 * @param subUserId
	 * @param userIp
	 */
	public void login(Client client,String globalSessionId,String userId,String subUserId,String userIp){
		long now=SysUtil.getNow();
		String md5=JUtilMD5.MD5EncodeToHex(client.getPassport()+now+globalSessionId+userId+userIp);
		String url=client.getUrlDefault()+client.getLoginInterface();
		if(url.indexOf("?")>0){
			url+="&"+Constants.SSO_MD5_STRING+"="+md5;
		}else{
			url+="?"+Constants.SSO_MD5_STRING+"="+md5;
		}
		url+="&"+Constants.SSO_TIME+"="+now;
		url+="&"+Constants.SSO_GLOBAL_SESSION_ID+"="+globalSessionId;
		url+="&"+Constants.SSO_USER_ID+"="+userId;
		if(subUserId!=null) {
			url+="&"+Constants.SSO_SUB_USER_ID+"="+subUserId;
		}
		url+="&"+Constants.SSO_USER_IP+"="+userIp;
		url+="&"+Constants.SSO_PASSPORT+"="+Permission.getSSOPassport();
		

		int loop=0;
		while(loop<3){//最多尝试3次
			loop++;
			try{
				JHttpContext context=http.get(null,httpClient,url);
				String response=context.getStatus()==200?context.getResponseText():null;
				context.finalize();
				context=null;
				//log.log("notify "+url+",client response:"+response,-1);
				break;
			}catch(Exception e){}
		}
	}
	
	/**
	 * 通知注销
	 * @param client
	 * @param globalSessionId
	 * @param userId
	 * @param subUserId
	 * @param userIp
	 */
	public void logout(Client client,String globalSessionId,String userId,String subUserId,String userIp){
		long now=SysUtil.getNow();
		String md5=JUtilMD5.MD5EncodeToHex(client.getPassport()+now+globalSessionId+userId+userIp);
		String url=client.getUrlDefault()+client.getLogoutInterface();
		if(url.indexOf("?")>0){
			url+="&"+Constants.SSO_MD5_STRING+"="+md5;
		}else{
			url+="?"+Constants.SSO_MD5_STRING+"="+md5;
		}
		url+="&"+Constants.SSO_TIME+"="+now;
		url+="&"+Constants.SSO_GLOBAL_SESSION_ID+"="+globalSessionId;
		url+="&"+Constants.SSO_USER_ID+"="+userId;
		if(subUserId!=null) {
			url+="&"+Constants.SSO_SUB_USER_ID+"="+subUserId;
		}
		url+="&"+Constants.SSO_USER_IP+"="+userIp;
		url+="&"+Constants.SSO_PASSPORT+"="+Permission.getSSOPassport();

		int loop=0;
		while(loop<3){//最多尝试3次
			loop++;
			try{
				JHttpContext context=http.get(null,httpClient,url);
				if(context.getStatus()==200){
					String response=context.getResponseText();
					context.finalize();
					context=null;
					log.log("notify "+url+",client response:"+response,-1);
					break;
				}
			}catch(Exception e){
				log.log("failed to notify "+url,Logger.LEVEL_ERROR);
				log.log(e,Logger.LEVEL_ERROR);
			}
		}
	}
	
	/**
	 * 通知某个sso client注销全部用户
	 *
	 */
	private void logout(Client client){
		long now=SysUtil.getNow();
		
		String md5=JUtilMD5.MD5EncodeToHex(client.getPassport()+now);
		String url=client.getUrlDefault()+client.getLogoutInterface();
		if(url.indexOf("?")>0){
			url+="&"+Constants.SSO_MD5_STRING+"="+md5;
		}else{
			url+="?"+Constants.SSO_MD5_STRING+"="+md5;
		}
		url+="&"+Constants.SSO_TIME+"="+now;
		url+="&"+Constants.SSO_PASSPORT+"="+Permission.getSSOPassport();
		

		int loop=0;
		while(loop<3){//最多尝试3次
			loop++;
			try{
				JHttpContext context=http.get(null,httpClient,url);
				String response=context.getStatus()==200?context.getResponseText():null;
				context.finalize();
				context=null;
				log.log("notify "+url+",client response:"+response,Logger.LEVEL_DEBUG);
				break;
			}catch(Exception e){
				log.log("failed to notify "+url,Logger.LEVEL_ERROR);
				log.log(e,Logger.LEVEL_ERROR);
			}
		}
	}
	
	/**
	 * 添加注销或登录任务
	 * @param client
	 * @param globalSessionId
	 * @param userId
	 * @param type
	 */
	public static void addTask(Client client,String globalSessionId,String userId,String subUserId,String userIp,String type){
		SSONotifier notifier=getNotifier(client);
		notifier.tasks.add(new Object[]{client,globalSessionId,userId,subUserId,userIp,type});
	}
	
	/**
	 * 添加注销全部的任务
	 * @param client
	 */
	public static void addTask(Client client){
		SSONotifier notifier=getNotifier(client);
		notifier.tasks.add(new Object[]{client,null,null,null,SSONotifier.type_logout_all});
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		while(true){
			try{
				Thread.sleep(500);
			}catch(Exception ex){}
			
			for(int i=0;i<tasks.size();i++){
				Object[] cells=(Object[])tasks.remove(i);
				Client client=(Client)cells[0];
				String globalSessionId=(String)cells[1];
				String userId=(String)cells[2];
				String subUserId=(String)cells[3];
				String userIp=(String)cells[4];
				String type=(String)cells[5];
				
				if(type.equals(type_login)){
					this.login(client,globalSessionId,userId,subUserId,userIp);
				}else if(type.equals(type_logout)){
					this.logout(client,globalSessionId,userId,subUserId,userIp);
				}else{
					this.logout(client);
				}
				i--;
			}
		}
	}	
}
