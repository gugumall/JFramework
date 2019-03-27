package j.cache;

import j.service.server.ServiceConfig;
import j.service.server.ServiceManager;

/**
 * 
 * @author 肖炯
 *
 */
public class JDCacheMapping {
	private String selector;
	private String serviceCode;
	private String serviceChannel;
	
	/**
	 * 
	 * @param selector
	 * @param serviceCode
	 * @param serviceChannel
	 */
	protected JDCacheMapping(String selector,String serviceCode,String serviceChannel) {
		super();
		this.selector=selector;
		this.serviceCode=serviceCode;
		this.serviceChannel=serviceChannel;
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
	public ServiceConfig[] getServiceNodes() {
		return ServiceManager.getServices(serviceCode);
	}
}
