package j.tool.translator;

import j.util.ConcurrentMap;

/**
 * 
 * @author JFramework
 *
 */
public abstract class Translator {
	public static final String LANG_EN="en";
	public static final String LANG_ZH_CN="zh-CN";
	public static final String LANG_ZH_FR="fr";
	
	private static ConcurrentMap instances=new ConcurrentMap();
	private static final Object lock=new Object();
	
	/**
	 * 
	 * @param instanceName
	 * @return
	 */
	public static Translator instance(String instanceName){
		synchronized(lock){
			if(instanceName==null||instanceName.equals("")) return null;
			
			instanceName=instanceName.toLowerCase();
			if("google".equalsIgnoreCase(instanceName)){
				if(!instances.containsKey(instanceName)){
					Translator translator=new TranslatorGoogle();
					instances.put(instanceName,translator);
				}
				return (Translator)instances.get(instanceName);
			}
			
			return null;
		}
	}
	

	/**
	 * 
	 * @param source
	 * @param sourceLang
	 * @param toLang
	 * @return
	 * @throws Exception
	 */
	public abstract String translate(String source,String sourceLang,String toLang) throws Exception;
	
	
	/**
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public String translate(String source) throws Exception{
		return translate(source,Translator.LANG_EN,Translator.LANG_ZH_CN);
	}
}
