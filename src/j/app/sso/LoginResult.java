package j.app.sso;

import j.common.JObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @author 肖炯
 *
 */
public class LoginResult implements Serializable{
	private static final long serialVersionUID = 1L;
	/*
	 * 认证结果
	 */
	public final static int RESULT_PASSED=1;//登录成功
	public final static int RESULT_FAILED=0;//登录失败（未知错误）
	
	public final static int RESULT_SERVICE_UNAVAILABLE=-1;//服务不可用（比如不是sso server）
	public final static int RESULT_BAD_CLIENT=-2;//非法的SSO Client
	public final static int RESULT_BAD_AGENT=-3;//登录代理不存在或不受理
	public final static int RESULT_BAD_REQUEST=-4;//登录信息无效（不符合规定）
	
	public final static int RESULT_VERIFIER_CODE_INCORRECT=-11;//验证码无效
	public final static int RESULT_USER_NOT_EXISTS=-12;//账号不存在
	public final static int RESULT_PASSWORD_INCORRECT=-13;//密码不正确
	public final static int RESULT_USER_INVALID=-14;//用户无效（冻结等）
	
	public final static int RESULT_ERROR=-100;//其它错误（登录未成功），可用resultMsg进一步指明错误原因

	private String sysId=null;
	private String machineId=null;
	private String userId=null;//用户ID，登录成功时必须正确设置
	private int    result=0;//登录结果
	private String resultMsg="";//登录结果提示信息
	private Map messages=new HashMap();//自定义键值对
		
	
	//getters
	public String getSysId(){
		return this.sysId;
	}
	public String getMachineId(){
		return this.machineId;
	}
	public String getUserId(){
		return this.userId;
	}
	public int getResult(){
		return this.result;
	}
	public String getResultMsg(){
		return this.resultMsg;
	}
	public Object getMessage(Object key){
		return messages.get(key);
	}
	public Map getMessages(){
		return messages;
	}
	
	//setters
	public void setSysId(String loginSysId){
		this.sysId=loginSysId;
	}
	public void setMachineId(String loginMachineId){
		this.machineId=loginMachineId;
	}
	public void setUserId(String userId){
		this.userId=userId;
	}
	public void setResult(int result){
		this.result=result;
	}
	public void setResultMsg(String resultMsg){
		this.resultMsg=resultMsg;
	}
	public void setMessage(Object key,Object value){
		messages.put(key,value);
	}
	
	public static void main(String[] args){
		Map parameters=new HashMap();//自定义键值对
		parameters.put("verify", "");
		
		String redirect="";
		for(Iterator keys=parameters.keySet().iterator();keys.hasNext();){
			Object key=keys.next();
			Object val=parameters.get(key);
			
			redirect+="<input type=\"hidden\" name=\""+key+"\" value=\"jis:"+JObject.string2IntSequence(val.toString())+"\">\r\n";
			
		}
		
		System.out.println(redirect);
	}
}
