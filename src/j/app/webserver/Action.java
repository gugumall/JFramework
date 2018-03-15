package j.app.webserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author JFramework
 *
 */
public class Action{
	private String id;//操作id
	private String name;//操作名称
	private String method;//处理类中对应的方法名
	private boolean respondWithString;//是否通过PrintWriter.out方法直接返回内容给用户（而不是通过跳转）
	private String roles;//可访问此action的用户角色ID，多个用|分隔，不设置roles属性或属性值为空表示任何人都可访问
	private String onError;//发生错误时转向哪个<navigate>中配置的页面
	private Map navigates;//返回地址,key condition,value Navigate（根据业务处理类的处理结果来决定返回那个地址）
	private int logEnabled=-1;//-1，默认；0，关闭；1，开启
	private boolean logAllParameters=false;
	private boolean isBrowserOnly=false;
	private List logParams;
	
	/**
	 * constructor
	 *
	 */
	public Action(){
		this.navigates=new HashMap();
		this.logParams=new ArrayList();
	}
	
	//getters
	public String getId(){
		return this.id;
	}	
	
	public String getName(){
		return this.name;
	}
	
	public String getMethod(){
		return this.method;
	}
	
	public boolean getRespondWithString(){
		return this.respondWithString;
	}
	
	public boolean getIsBrowserOnly(){
		return this.isBrowserOnly;
	}
	
	public String getRoles(){
		return this.roles;
	}
	
	public String getOnError(){
		return this.onError;
	}
	
	public Navigate getNavigate(String condition){
		return (Navigate)this.navigates.get(condition);
	}
	
	public int isLogEnabled(){
		return this.logEnabled;
	}
	
	public boolean isLogAllParameters(){
		return this.logAllParameters;
	}
	
	public List getLogParams(){
		return this.logParams;
	}
	//getters end
	
	
	//setters
	public void setId(String id){
		this.id=id;
	}	
	
	public void setName(String name){
		this.name=name;
	}
	
	public void setMethod(String method){
		this.method=method;
	}
	
	public void setRespondWithString(boolean respondWithString){
		this.respondWithString=respondWithString;
	}
	
	public void setIsBrowserOnly(boolean isBrowserOnly){
		this.isBrowserOnly=isBrowserOnly;
	}
	
	public void setRoles(String roles){
		this.roles=roles;
	}
	
	public void setOnError(String onError){
		this.onError=onError;
	}
	
	public void addNavigate(Navigate navigate){
		this.navigates.put(navigate.getCondition(),navigate);
	}
	
	public void setLogEnabled(int logEnabled){
		this.logEnabled=logEnabled;
	}
	
	public void setLogAllParameters(boolean logAllParameters){
		this.logAllParameters=logAllParameters;
	}
	
	public void addLogParam(String logParam){
		if(!this.logParams.contains(logParam)) this.logParams.add(logParam);
	}
	//setters end
	
	/**
	 * 得到返回地址，如未找到对应信息则返回系统定义的错误页面地址
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public String getNavigateUrl(String condition)throws Exception{
		if(condition==null||condition.equals("")) return null;
		
		Navigate navigate=this.getNavigate(condition);
		return navigate==null?null:navigate.getUrl();
	}	
	
	/**
	 * 得到返回类型，如未找到对应信息则返回Navigate.TYPE_REDIRECT
	 * @param condition
	 * @return String
	 * @throws Exception
	 */
	public String getNavigateType(String condition)throws Exception{
		if(condition==null||condition.equals("")) return null;

		Navigate navigate=this.getNavigate(condition);
		return navigate==null?null:navigate.getType();
	}
}