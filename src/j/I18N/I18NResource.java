package j.I18N;

import j.util.ConcurrentMap;

import java.io.Serializable;

/**
 * 
 * @author JFramework
 *
 */
public class I18NResource implements Serializable {
	private static final long serialVersionUID = 1L;
	private String file;
	private String group;
	private String key;
	private ConcurrentMap languages=new ConcurrentMap();
	
	/**
	 * 
	 * @param file
	 * @param group
	 * @param key
	 */
	public I18NResource(String file,String group,String key){
		this.file=file;
		this.group=group;
		this.key=key;
	}
	
	/**
	 * 
	 * @param language
	 * @param text
	 */
	public void addLanguage(String language,String text){
		languages.put(language,text);
	}
	
	/**
	 * 
	 * @param language
	 * @return
	 */
	public String getLanguage(String language){
		String text=(String)languages.get(language);
		if(text==null){
			text=(String)languages.get(I18N.defaultLanguage);
		}
		return text;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFile(){
		return this.file;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getGroup(){
		return this.group;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getKey(){
		return this.key;
	}
}