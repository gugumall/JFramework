package j.fs;

import j.util.JUtilString;

/**
 * 
 * @author JFramework
 *
 */
public class JDFSMappingRule {
	private String selector;
	private String virtualRoot;
	private String physicalRoot;
	private boolean local;
	
	/**
	 * 
	 * @param selector
	 * @param virtualRoot
	 * @param physicalRoot
	 * @param local
	 */
	protected JDFSMappingRule(String selector,String virtualRoot,String physicalRoot,boolean local){
		this.selector=selector;
		this.virtualRoot=virtualRoot;
		this.physicalRoot=physicalRoot;
		this.local=local;
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
	 * @param virtualRoot
	 */
	public void setVirtualRoot(String virtualRoot){
		this.virtualRoot=virtualRoot;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getVirtualRoot(){
		return virtualRoot;
	}
	


	
	/**
	 * 
	 * @param physicalRoot
	 */
	public void setPhysicalRoot(String physicalRoot){
		this.physicalRoot=physicalRoot;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPhysicalRoot(){
		return physicalRoot;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isLocal(){
		return this.local;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public boolean matches(String path){
		return path.matches(selector);
	}
	
	
	/**
	 * 
	 * @param path
	 * @param os
	 * @return
	 */
	public String virtual2Physical(String path,String os){
		String temp=JFile.adjustFileSeperator(path,"linux");
		return JFile.adjustFileSeperator(JUtilString.replaceAll(temp,virtualRoot,physicalRoot),os);
	}
	
	/**
	 * 
	 * @param path
	 * @param os
	 * @return
	 */
	public String physical2Virtual(String path){
		String temp=JFile.adjustFileSeperator(path,"linux");
		return JFile.adjustFileSeperator(JUtilString.replaceAll(temp,physicalRoot,virtualRoot),"linux");
	}
}
