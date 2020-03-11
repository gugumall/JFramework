package j.app.online;

import java.util.List;

import j.util.ConcurrentMap;

/**
 * 
 * @author one
 *
 */
public class CssCompatible {
	private String uri;
	private ConcurrentMap compatibles=new ConcurrentMap();
	
	/**
	 * 
	 * @param uri
	 */
	public CssCompatible(String uri){
		this.uri=uri;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUri(){
		return this.uri;
	}
	
	/**
	 * 
	 * @param uri
	 * @return
	 */
	public boolean matches(String uri){
		return this.uri.equals(uri);
	}
	
	/**
	 * 
	 * @param userAgent
	 * @param uri
	 */
	public void setCompatible(String userAgent,String uri){
		compatibles.put(userAgent, uri);
	}
	
	/**
	 * 
	 * @param userAgent
	 * @return
	 */
	public String getCompatible(String userAgent){
		return (String)compatibles.get(userAgent);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String s=this.uri;
		List keys=compatibles.listKeys();
		for(int i=0;i<keys.size();i++){
			String key=(String)keys.get(i);
			String val=(String)compatibles.get(key);
			s+="\r\n"+key+"="+val;
		}
		
		return s;
	}
}
