package j.app.webserver;

public class JSession {
	public Action action=null;//用户请求的哪个操作
	public String result=null;//处理结果
	
	/*
	 * 如果在<request>中定义了属性print-directly="true"，通过PrintWriter.out直接将字符串内容返回给用户
	 */
	public String resultString=null;
	
	//取代resultString，提供json格式的返回内容
	public JResponse jresponse=null;
	
	/*
	 * 有时需要根据处理结果返回不同的地址，而且这个地址是动态的，这时可设置dynamicBackUrl，
	 * 而忽略<request>中定义的navigate-url，如dynamicBackUrl为null则根据<request>中定义的navigate-url进行跳转
	 */
	private String dynamicBackUrl=null;
	
	private boolean isBackToGlobalNavigation=false;//是否根据全局导航配置进行跳转（而不是按对应<request>中定义的）
	
	JSession(Action action){
		this.action=action;
	}
	

	
	/**
	 * 动态设置返回URL
	 * @param url
	 */
	public void setDynamicBackUrl(String url){
		dynamicBackUrl=url;
	}
	
	public String getDynamicBackUrl(){
		return this.dynamicBackUrl;
	}
	
	/**
	 * 是否按全局导航配置返回
	 * @return
	 */
	public boolean getIsBackToGlobalNavigation(){
		return this.isBackToGlobalNavigation;
	}	
	public void setIsBackToGlobalNavigation(boolean _isBackToGlobalNavigation){
		this.isBackToGlobalNavigation=_isBackToGlobalNavigation;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	public void finalize(){
		//System.out.println("JSession - calling me...^_^");
		this.action=null;
		this.resultString=null;
		this.result=null;
		this.dynamicBackUrl=null;
		this.isBackToGlobalNavigation=false;
	}
}
