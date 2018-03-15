package j.cache;


/**
 * 
 * @author JFramework
 *
 */
public class JDCacheMapping {
	private String selector;
	private String serviceCode;
	private String serviceChannel;
	private String os;
	
	/**
	 * 
	 * @param selector
	 * @param serviceCode
	 * @param serviceChannel
	 * @param os
	 */
	protected JDCacheMapping(String selector,String serviceCode,String serviceChannel,String os) {
		super();
		this.selector=selector;
		this.serviceCode=serviceCode;
		this.serviceChannel=serviceChannel;
		this.os=os;
	}
	
	/**
	 * 
	 * @param selector
	 */
	public void setSelector(String selector){
		this.selector=selector;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSelector(){
		return selector;
	}
	
	/**
	 * 
	 * @param cacheId
	 * @return
	 */
	public boolean matches(String cacheId){
		return cacheId.matches(selector);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getServiceCode(){
		return serviceCode;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getServiceChannel(){
		return serviceChannel;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getOs(){
		return os;
	}
}
