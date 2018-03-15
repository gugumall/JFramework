package j.app.webserver;

/**
 * 
 * @author JFramework
 *
 */
public class Navigate {
	/*
	 * forward，必须是本应用内部网址，可通过HttpServletRequest.setAttribute/getAttribute方法传递对象
	 */
	public final static String TYPE_FORWARD="forward";
	
	/*
	 * 直接跳转,不可通过HttpServletRequest.setAttribute/getAttribute方法传递对象
	 */
	public final static String TYPE_REDIRECT="sendRedirect";
	
	private String condition;//当业务处理类的处理结果为condition时，执行该Navigate对象所定义的导航
	private String type;//返回类型
	private String url;//返回地址
	
	//getters
	public String getCondition(){
		return this.condition;
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getUrl(){
		return this.url;
	}
	//getters end
	
	
	//setters
	public void setCondition(String condition){
		this.condition=condition;
	}
	
	public void setType(String navigateType){
		this.type=navigateType;
	}
	
	public void setUrl(String backUrl){
		this.url=backUrl;
	}
	//setters end
}