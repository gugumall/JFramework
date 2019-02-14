package j.fs;

import j.util.ConcurrentList;

/**
 * 
 * @author 肖炯
 *
 */
public class JDFSMapping {
	private String serviceCode;
	private String serviceChannel;
	private String os;
	private ConcurrentList rules=new ConcurrentList();
	
	/**
	 * 
	 * @param serviceCode
	 */
	protected JDFSMapping(String serviceCode,String serviceChannel,String os) {
		super();
		this.serviceCode=serviceCode;
		this.serviceChannel=serviceChannel;
		this.os=os;
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
	
	/**
	 * 
	 * @param rule
	 */
	public void addRule(JDFSMappingRule rule){
		this.rules.add(rule);
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public JDFSMappingRule getRule(String path){
		for(int i=0;i<rules.size();i++){
			JDFSMappingRule rule=(JDFSMappingRule)rules.get(i);
			if(rule.matches(path)) return rule;
		}
		return null;
	}
	
	/**
	 * 
	 */
	public void clearRule(){
		this.rules.clear();
	}
}
