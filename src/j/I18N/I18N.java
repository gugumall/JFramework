package j.I18N;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.maxmind.geoip2.model.CountryResponse;

import j.app.Constants;
import j.app.webserver.JHandler;
import j.app.webserver.JResponse;
import j.app.webserver.JSession;
import j.common.JProperties;
import j.dao.util.SQLUtil;
import j.fs.JDFSFile;
import j.log.Logger;
import j.sys.Initializer;
import j.sys.SysUtil;
import j.tool.ip.IP;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilSorter;
import j.util.JUtilString;

/**
 * 
 * @author 肖炯
 *
 */
public class I18N extends JHandler implements Initializer,Runnable{	
	private static Logger log=Logger.create(I18N.class);
	
	private static ConcurrentMap<String, String> namesOfFiles=new ConcurrentMap();
	private static ConcurrentList I18NResourceFileNames=new ConcurrentList();//多语言资源文件名列表
	private static List urls=new LinkedList();//需要进行多语言处理的url
	private static ConcurrentMap<String, I18NResource> I18NStringCollection=new ConcurrentMap();//多语言资源

	public static ConcurrentMap languages=new ConcurrentMap();//可选语言
	public static String defaultLanguage;
	public static boolean enabled=false;
	public static boolean cookieEnabled=false;
	public static boolean showUnknownTag=false;
	public static boolean showUnknownTagIfDefaultLang=false;
	
	private static ConcurrentMap lastUpds=new ConcurrentMap();//文件最新更新时间
	
	static{
		I18N i18n=new I18N();
		Thread thread=new Thread(i18n);
		thread.start();
		log.log("I18N started.",-1);
	}
	
	/**
	 * 
	 * @return
	 */
	public static List getLanguages(){
		return languages.listValues();
	}
	
	/**
	 * 
	 * @return
	 */
	public static Language getLanguage(String key){
		return (Language)languages.get(key);
	}
	
	/**
	 * 
	 * @return
	 */
	public static List getI18NResourceFileNames(){
		I18NFileSorter sorter=new I18NFileSorter();
		return sorter.bubble(I18NResourceFileNames,JUtilSorter.ASC);
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getNameOfFile(String fileName) {
		return (String)namesOfFiles.get(fileName);
	}
	
	/**
	 * 
	 * @param request
	 * @param key
	 * @param language
	 * @return
	 */
	//private static String getText(HttpServletRequest request,String key,String language){
	//	return getText(request.getRequestURI(),key,language);
	//}
	
	
	/**
	 * 
	 * @param key
	 * @param language
	 * @return
	 */
	private static String getText(String key,String language){
		if(key.indexOf(",")>-1){
			int i=key.indexOf(",");
			return getText(key.substring(0,i),key.substring(i+1),language);
		}else{
			return getText((String)null,key,language);
		}
	}
	
	
	/**
	 * 
	 * @param group
	 * @param key
	 * @param language
	 * @return
	 */
	private static String getText(String group,String key,String language){
		if(language==null||language.equals("")){
			language=I18N.defaultLanguage;
		}
		
		boolean nonJs=false;
		if(key.startsWith("NON-JS")){
			key=key.substring(6);
			nonJs=true;
		}

		I18NResource resource=null;
		if(group==null||group.equals("")){
			resource=(I18NResource)I18N.I18NStringCollection.get(key+"<SPL>"+language);
		}else{
			resource=(I18NResource)I18N.I18NStringCollection.get(group+"<SPL>"+key+"<SPL>"+language);
			if(resource==null){
				resource=(I18NResource)I18N.I18NStringCollection.get(key+"<SPL>"+language);
			}
		}
		
		String string=resource==null?null:resource.getLanguage(language);
		if(!nonJs&&string!=null) {
			string=string.replaceAll("'", "\\\\'");
		}
		
		return string;
	}
	
	/**
	 * 
	 * @param request
	 * @param key
	 * @param language
	 * @return
	 */
	//private static String getTextIgnoreGlobal(HttpServletRequest request,String key,String language){
	//	return getTextIgnoreGlobal(request.getRequestURI(),key,language);
	//}
	
	
	/**
	 * 
	 * @param group
	 * @param key
	 * @param language
	 * @return
	 */
	private static String getTextIgnoreGlobal(String group,String key,String language){
		if(language==null||language.equals("")){
			language=I18N.defaultLanguage;
		}
		
		boolean nonJs=false;
		if(key.startsWith("NON-JS")){
			key=key.substring(6);
			nonJs=true;
		}

		I18NResource resource=null;
		if(group==null||group.equals("")){
			resource=(I18NResource)I18N.I18NStringCollection.get(key+"<SPL>"+language);
		}else{
			resource=(I18NResource)I18N.I18NStringCollection.get(group+"<SPL>"+key+"<SPL>"+language);
		}
		
		String string=resource==null?null:resource.getLanguage(language);
		if(!nonJs&&string!=null) {
			string=string.replaceAll("'", "\\\\'");
		}
		
		return string;
	}
	
	/**
	 * 
	 * @param group
	 * @param lang
	 * @return
	 */
	private static ConcurrentMap getTexts(String group, String lang){
		ConcurrentMap texts=new ConcurrentMap();
		List keys=I18N.I18NStringCollection.listKeys();
		for(int i=0;i<keys.size();i++){
			String key=(String)keys.get(i);
			String[] keyCells=key.split("<SPL>");
			
			if(group==null||group.equals("")){
				if(keyCells.length==2){
					if(I18N.I18NStringCollection.get(key)!=null) texts.put(key, I18N.I18NStringCollection.get(key).getLanguage(lang));
				}
			}else{
				if(keyCells.length==3&&keyCells[0].equals(group)){
					if(I18N.I18NStringCollection.get(key)!=null) texts.put(key, I18N.I18NStringCollection.get(key).getLanguage(lang));
				}
			}
		}
		
		return texts;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.sys.Initializer#initialization()
	 */
	public void initialization()throws Exception{      	
      	//加载配置信息
      	loadConfig();
        //加载配置信息 end
	}
	
	/**
	 * 
	 * @param fileName
	 * @param remark
	 * @return
	 */
	private static boolean addModule(String fileName,String remark){
		try{
			if(rootForConfig==null) return false;
			
			Element modulesElement=rootForConfig.element("modules");
			if(modulesElement==null) return false;
			
			boolean exists=false;
			List modules=modulesElement.elements("module");
			for(int i=0;i<modules.size();i++){
				Element module=(Element)modules.get(i);
				String _fileName=module.getTextTrim();
				if(_fileName.equals(fileName)){
					exists=true;
					break;
				}
			}
			
			if(!exists){
				Element module=modulesElement.addElement("module");
				module.addAttribute("remark",remark);
				module.setText(fileName);
				I18NResourceFileNames.add(fileName);
				namesOfFiles.put(fileName, remark);
				
				JUtilDom4j.save(docForConfig,JProperties.getI18NPath()+"config.xml", "UTF-8");
			}
			
			return true;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return false;
		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	private static boolean delModule(String fileName){
		try{
			if(rootForConfig==null) return false;
			
			Element modulesElement=rootForConfig.element("modules");
			if(modulesElement==null) return false;
			
			Element exists=null;
			List modules=modulesElement.elements("module");
			for(int i=0;i<modules.size();i++){
				Element module=(Element)modules.get(i);
				String _fileName=module.getTextTrim();
				if(_fileName.equals(fileName)){
					exists=module;
					break;
				}
			}
			
			if(exists!=null){
				modulesElement.remove(exists);
				JUtilDom4j.save(docForConfig,JProperties.getI18NPath()+"config.xml", "UTF-8");
				
				File i18nConfigFile=new File(JProperties.getI18NPath()+fileName);
		      	if(i18nConfigFile.exists()){
		      		i18nConfigFile.delete();
		      	}
		      	
		      	loadConfig();
				
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return false;
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private static SAXReader readerForConfig=null;
  	private static Document docForConfig=null;
	private static Element rootForConfig=null;
	private static void loadConfig() throws Exception{
      	File i18nConfigFile=new File(JProperties.getI18NPath()+"config.xml");
      	//log.log("I18N.i18nConfigFile:"+i18nConfigFile,-1);
      	
		if(lastUpds.containsKey(i18nConfigFile.getName())){
			long lastUpd=((Long)lastUpds.get(i18nConfigFile.getName())).longValue();
			if(lastUpd==i18nConfigFile.lastModified()){//未修改
				return;
			}else{
				log.log("file "+i18nConfigFile.getAbsolutePath()+" has been modified, so reload it.",-1);
			}
		}		
		lastUpds.put(i18nConfigFile.getName(),new Long(i18nConfigFile.lastModified()));	
		
		languages.clear();
      	I18NResourceFileNames.clear();
      	urls.clear();
      	
      	readerForConfig=new SAXReader();
		docForConfig=readerForConfig.read(new FileInputStream(i18nConfigFile),"UTF-8");
		rootForConfig=docForConfig.getRootElement();
		
		I18N.enabled="true".equalsIgnoreCase(rootForConfig.elementText("enabled"));
      	log.log("I18N.enabled:"+enabled,-1);
		
		I18N.cookieEnabled="true".equalsIgnoreCase(rootForConfig.elementText("cookieEnabled"));
      	log.log("I18N.cookieEnabled:"+cookieEnabled,-1);
        
      	I18N.showUnknownTag="true".equalsIgnoreCase(rootForConfig.elementText("showUnknownTag"));
      	log.log("I18N.showUnknownTag:"+showUnknownTag,-1);
        
      	I18N.showUnknownTagIfDefaultLang="true".equalsIgnoreCase(rootForConfig.elementText("showUnknownTagIfDefaultLang"));
      	log.log("I18N.showUnknownTagIfDefaultLang:"+showUnknownTagIfDefaultLang,-1);
      	
      	
      	
        Element languagesE=rootForConfig.element("languages");
        I18N.defaultLanguage=languagesE.attributeValue("default");
      	log.log("I18N.defaultLanguage:"+defaultLanguage,-1);
        
        List languages=languagesE.elements("language");
        for(int i=0;languages!=null&&i<languages.size();i++){
        	Element languageElement=(Element)languages.get(i);
        	Language language=new Language();
        	language.setCode(languageElement.attributeValue("code"));
        	language.setName(languageElement.attributeValue("name"));
        	String countries=languageElement.attributeValue("countries");
        	if(countries!=null&&!"".equals(countries)){
        		language.setCountries(countries.split(","));
        	}
        	I18N.languages.put(language.getCode(),language);
          	log.log("I18N.language:"+language.getCode()+","+language.getName(),-1);
        }
        
        Element modules=rootForConfig.element("modules");
        List strs=modules.elements("module");
        for(int i=0;i<strs.size();i++){
      		Element str=(Element)strs.get(i);
      		I18N.I18NResourceFileNames.add(str.getText());
			namesOfFiles.put(str.getText(), str.attributeValue("remark"));
          	log.log("I18N.I18NResourceFileName:"+str.attributeValue("remark")+" -> "+str.getText(),-1);
      	}
        
        Element urlsEle=rootForConfig.element("urls");
        List urlEles=urlsEle==null?null:urlsEle.elements("url");
        for(int i=0;urlEles!=null&&i<urlEles.size();i++){
        	Element rEle=(Element)urlEles.get(i);
        	
        	I18NUrl r=new I18NUrl();
        	r.setUrlPattern(rEle.attributeValue("pattern"));
        	r.setExtension(rEle.attributeValue("extension"));
        	r.setMatch(rEle.attributeValue("match"));
        	
        	List excludes=rEle.elements("exclude");
        	for(int j=0;excludes!=null&&j<excludes.size();j++){
            	Element ex=(Element)excludes.get(j);
        		r.addExclude(ex.getText());
        	}
        	urls.add(r);
        	
        	log.log(r.toString(),-1);
        } 
		
        
        //加载多语言资源
		for(int i=0;i<I18N.I18NResourceFileNames.size();i++){
			File file = new File(JProperties.getI18NPath()+I18N.I18NResourceFileNames.get(i));
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			
		    process(file);
		}
		//加载多语言资源 end
	}
	
	/**
	 * 
	 * @param file
	 * @throws Exception
	 */
	private static void process(File file) throws Exception{
		if(lastUpds.containsKey(file.getName())){
			long lastUpd=((Long)lastUpds.get(file.getName())).longValue();
			if(lastUpd==file.lastModified()){//未修改
				return;
			}else{
				log.log("file "+file.getAbsolutePath()+" has been modified, so reload it.",-1);
			}
		}		
		lastUpds.put(file.getName(),new Long(file.lastModified()));
		
		log.log("loading strings from file: "+file.getAbsolutePath(),-1);
		
		//移除旧记录
		List allResources=I18NStringCollection.listKeys();
		for(int i=0;i<allResources.size();i++){
			Object key=allResources.get(i);
			I18NResource r=(I18NResource)I18NStringCollection.get(key);
			if(r.getFile().equals(file.getName())){
				I18NStringCollection.remove(key);
			}
		}
		//移除旧记录  end
		
		//解析
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new FileInputStream(file),"UTF-8");
		Element root = doc.getRootElement();
		
		List children=root.elements("group");
		for(int i=0;children!=null&&i<children.size();i++){
			Element child=(Element)children.get(i);
			String group=child.attributeValue("name");
			
			List resources=child.elements("string");
			for(int j=0;j<resources.size();j++){
				Element resElement=(Element)resources.get(j);
				String key=resElement.attributeValue("key");	
				I18NResource resource=new I18NResource(file.getName(),group,key);
				
				List languages=resElement.elements("language");
				for(int k=0;k<languages.size();k++){
					Element languageElement=(Element)languages.get(k);				
					String language=languageElement.attributeValue("code");
					String text=languageElement.getText();
					resource.addLanguage(language,text);
					
					if("".equals(group)){						
						I18N.I18NStringCollection.put(key+"<SPL>"+language,resource);
						//log.log(key+","+language+" = "+value,-1);
					}else{
						I18N.I18NStringCollection.put(group+"<SPL>"+key+"<SPL>"+language,resource);
						//log.log(group+","+key+","+language+" = "+value,-1);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param lang
	 * @return
	 */
	private static boolean _showUnknownTag(String lang){
		if(I18N.defaultLanguage.equals(lang)) return showUnknownTagIfDefaultLang;
		else return showUnknownTag;
	}

	/**
	 * 
	 * @param session
	 * @return
	 */
	public static String getCurrentLanguage(HttpSession session){
		String lang=session==null?null:(String)session.getAttribute(Constants.I18N_LANGUAGE);
		return lang==null?I18N.defaultLanguage:lang;
	}
	
	/**
	 * 
	 * @param httpRequest
	 * @param session
	 */
	public static void changeLanguage(HttpServletRequest httpRequest,HttpSession session){
		//请求改变系统语言的参数
		String language=httpRequest.getParameter(Constants.I18N_LANGUAGE);
		if(language==null||!I18N.languages.containsKey(language)){
			if(cookieEnabled) language=SysUtil.getCookie(httpRequest, Constants.I18N_LANGUAGE);
		}
		
		//保存在session中的当前系统语言对象
		Object languageObj=session.getAttribute(Constants.I18N_LANGUAGE);
		
		if(language==null||!I18N.languages.containsKey(language)){
			if(languageObj!=null) return;//不是初次访问
			
			//初次访问，根据国家确定语言
			String ip=IP.getRemoteIp(httpRequest);
			CountryResponse country=IP.geoIpGetCountry(ip);
			if(country==null
					||country.getCountry()==null
					||country.getCountry().getIsoCode()==null){//未获得国家数据，使用默认语言
				return;
			}
			
			List langs=languages.listValues();
			for(int i=0;i<langs.size();i++){
				Language lang=(Language)langs.get(i);
				if(lang.matches(country.getCountry().getIsoCode())){
					language=lang.getCode();
					break;
				}
			}
			//初次访问，根据国家确定语言 end
			
			if(language==null){//未匹配多国家，使用默认语言
				return;
			}
		}
				
		/**
		 * 如果系统使用的当前语言为空，则设置为默认语言
		 */
		if(language!=null&&I18N.languages.containsKey(language)){
			session.setAttribute(Constants.I18N_LANGUAGE,language);
		}else if(languageObj==null){
			session.setAttribute(Constants.I18N_LANGUAGE,I18N.defaultLanguage);
		}
	}
	
	/**
	 * 
	 * @param language
	 * @param session
	 */
	public static void changeLanguage(String language,HttpSession session){
		//请求改变系统语言的参数
		if(language==null||!I18N.languages.containsKey(language)){
			return;
		}
		
		//保存在session中的当前系统语言对象
		Object languageObj=session.getAttribute(Constants.I18N_LANGUAGE);
				
		/**
		 * 如果系统使用的当前语言为空，则设置为默认语言
		 */
		if(language!=null&&I18N.languages.containsKey(language)){
			session.setAttribute(Constants.I18N_LANGUAGE,language);
		}else if(languageObj==null){
			session.setAttribute(Constants.I18N_LANGUAGE,I18N.defaultLanguage);
		}
	}
	
	/**
	 * 
	 * @param _content
	 * @param request
	 * @param session
	 * @return
	 */
	public static String convert(String _content,HttpServletRequest request,HttpSession session){
		return convert(_content,request.getRequestURI(),session);
	}
	
	/**
	 * 
	 * @param _content
	 * @param group
	 * @param session
	 * @return
	 */
	public static String convert(String content,String group,HttpSession session){		
		//log.log("convert "+group, -1);
		
		if(content==null||content.indexOf("I{")<0) return content;
		
		String lang=getCurrentLanguage(session);
		
		List<String> cells=new ArrayList<String>();
		
		String[] contents=content.split("I\\{");

		boolean startsWith=content.startsWith("I{");

		StringBuffer _content=new StringBuffer(startsWith?"":contents[0]);
		for(int i=(startsWith?0:1);i<contents.length;i++) {
			int end=contents[i].indexOf("}");
			if(end<0) {
				_content.append(contents[i]);
				continue;
			}
			
			String key=contents[i].substring(0,end);
			
			String theKey=key;
			if(key.startsWith(".")){
				theKey=key.substring(1);
			}else if(key.indexOf(",")>0){
				theKey=key.substring(key.indexOf(",")+1);
			}else{
				theKey=key;
			}
			
			String alt=getTextIgnoreGlobal(group,theKey,lang);//强制优先获取针对本网页定义的多语言资源
			if(alt==null){
				if(key.startsWith(".")) alt=getText(group,theKey,lang);
				else alt=getText(key,lang);
			}
			
			if(alt==null) {
				String thisGroup="";
				if(key.startsWith(".")){
					alt=key.substring(1);
					thisGroup=".";
				}else if(key.indexOf(",")>0){
					alt=key.substring(key.indexOf(",")+1);
					thisGroup=key.substring(0,key.indexOf(",")+1);
				}else{
					alt=key;
				}
				if(!alt.startsWith("NON-JS")){
					alt=alt.replaceAll("'", "\\\\'");
				}
				
				if(_showUnknownTag(lang)) {
					alt="I{"+thisGroup+alt+"}";
				}
			}
			
			_content.append(alt);
			_content.append(contents[i].substring(end+1));
		}
		
		cells.clear();
		cells=null;
		
		//引入到js
		content=_content.toString();
		int start=content.indexOf("<import-i1n8>")+13;
		int end=content.indexOf("</import-i1n8>",start);
		while(start>=13){			
			group=content.substring(start,end);
			String _src=content.substring(start-13,end+14);
			
			ConcurrentMap strings=getTexts(group, lang);
			String js="";
			List keys=strings.listKeys();
			for(int i=0;i<keys.size();i++){
				String key=(String)keys.get(i);
				String[] keyCells=key.split("<SPL>");
				
				if(keyCells.length==2){
					js+="Lang.a('"+keyCells[0]+"','"+keyCells[1]+"','"+strings.get(key)+"');\r\n";
				}else{
					js+="Lang.a('"+keyCells[1]+"','"+keyCells[2]+"','"+strings.get(key)+"');\r\n";
				}
			}
			
			content=JUtilString.replaceAll(content, _src, js);
			
			start=content.indexOf("<import-i1n8>",end)+13;
			end=content.indexOf("</import-i1n8>",start);
		}
		//引入到js end
		
		_content=null;
		
		return content;
	}
	
	/**
	 * 
	 * @param _content
	 * @param lang
	 * @return
	 */
	public static String convert(String content,String lang){
		////log.log("convert "+group, -1);
		
		if(content==null||content.indexOf("I{")<0) return content;
		
		List<String> cells=new ArrayList<String>();
		
		String[] contents=content.split("I\\{");

		boolean startsWith=content.startsWith("I{");

		StringBuffer _content=new StringBuffer(startsWith?"":contents[0]);
		for(int i=(startsWith?0:1);i<contents.length;i++) {
			int end=contents[i].indexOf("}");
			if(end<0) {
				_content.append(contents[i]);
				continue;
			}
			
			String key=contents[i].substring(0,end);
			
			String alt=null;
			if(key.startsWith(".")){
				alt=getText(key.substring(1),lang);
			}else{
				alt=getText(key,lang);
			}
			
			if(alt==null) {
				String thisGroup="";
				if(key.startsWith(".")){
					alt=key.substring(1);
					thisGroup=".";
				}else if(key.indexOf(",")>0){
					alt=key.substring(key.indexOf(",")+1);
					thisGroup=key.substring(0,key.indexOf(",")+1);
				}else{
					alt=key;
				}
				if(!alt.startsWith("NON-JS")){
					alt=alt.replaceAll("'", "\\\\'");
				}
				
				if(_showUnknownTag(lang)) {
					alt="I{"+thisGroup+alt+"}";
				}
			}
			
			_content.append(alt);
			_content.append(contents[i].substring(end+1));
		}
		
		cells.clear();
		cells=null;
		
		//引入到js
		content=_content.toString();
		int start=content.indexOf("<import-i1n8>")+13;
		int end=content.indexOf("</import-i1n8>",start);
		while(start>=13){			
			String group=content.substring(start,end);
			String _src=content.substring(start-13,end+14);
			
			ConcurrentMap strings=getTexts(group, lang);
			String js="";
			List keys=strings.listKeys();
			for(int i=0;i<keys.size();i++){
				String key=(String)keys.get(i);
				String[] keyCells=key.split("<SPL>");
				
				if(keyCells.length==2){
					js+="Lang.a('"+keyCells[0]+"','"+keyCells[1]+"','"+strings.get(key)+"');\r\n";
				}else{
					js+="Lang.a('"+keyCells[1]+"','"+keyCells[2]+"','"+strings.get(key)+"');\r\n";
				}
			}
			
			content=JUtilString.replaceAll(content, _src, js);
			
			start=content.indexOf("<import-i1n8>",end)+13;
			end=content.indexOf("</import-i1n8>",start);
		}
		//引入到js end
		
		_content=null;
		
		return content;
	}
	
	/**
	 * 
	 * @param sb
	 * @param substring
	 * @param alt
	 * @return 替换前后长度差
	 */
	private static String replaceAll(String content, String substring, String alt) {
		substring=substring.replaceAll("\\{", "\\\\{").replaceAll("\\}", "\\\\}").replaceAll("\\+", "\\\\+");
		return content.replaceAll(substring, alt);
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static boolean need(HttpServletRequest request){
		for(int i=0;i<urls.size();i++){
			I18NUrl r=(I18NUrl)urls.get(i);				
			if(r.matches(request)) return true;
		}
		
		return false;
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			try{
				Thread.sleep(30000);
			}catch(Exception e){}
			
			try{
		        //加载配置信息	
				loadConfig();
		        //加载配置信息 end
				
		        //加载多语言资源
				for(int i=0;i<I18N.I18NResourceFileNames.size();i++){
					File file = new File(JProperties.getI18NPath()+I18N.I18NResourceFileNames.get(i));
			        if(file.exists()){
			        	process(file);
			        }
				}
				//加载多语言资源 end
			}catch(Exception e){
				log.log(e,Logger.LEVEL_ERROR);
			}
		}
	}
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void addFile(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		String name=SysUtil.getHttpParameter(request, "name","");
		if(!name.matches("^[a-zA-Z0-9\\u4E00-\\u9FA5_.\\-]{1,90}$")){
			jsession.jresponse=new JResponse(false,"invalid_file_name","I{.文件名称格式错误}");
		}
		
		String remark=SysUtil.getHttpParameter(request, "remark","");
		if(!remark.matches("^[a-zA-Z0-9\\u4E00-\\u9FA5_.\\-]{1,90}$")){
			jsession.jresponse=new JResponse(false,"invalid_remark","I{.备注格式错误}");
		}
		
		if(!name.endsWith(".xml")){
			name+=".xml";
		}
		
      	File i18nConfigFile=new File(JProperties.getI18NPath()+name);
      	if(!i18nConfigFile.exists()){
      		String s="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
      		s+="<root>\r\n";
      		s+="</root>\r\n";
      		
      		JDFSFile.saveString(JProperties.getI18NPath()+name,s,false,"UTF-8");
      		
      		if(addModule(name,remark)){
    			jsession.jresponse=new JResponse(true,"1","I{.添加成功}");
      		}else{
    			jsession.jresponse=new JResponse(false,"add_module_failed","I{.添加失败}");
      		}
      	}else{
			jsession.jresponse=new JResponse(false,"exists","I{.文件已存在}");
      	}
	}
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void delFile(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		String name=SysUtil.getHttpParameter(request, "name","");
		if(!name.matches("^[a-zA-Z0-9\\u4E00-\\u9FA5_.\\-]{1,90}$")){
			jsession.jresponse=new JResponse(false,"invalid_file_name","I{.文件名称格式错误}");
		}
		
		if(!name.endsWith(".xml")){
			name+=".xml";
		}
		
		if(delModule(name)){
			jsession.jresponse=new JResponse(true,"1","I{.删除成功}");
  		}else{
			jsession.jresponse=new JResponse(false,"del_module_failed","I{.删除失败}");
  		}
	}
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void addGroup(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		try{
			String name=SysUtil.getHttpParameter(request, "name","");
			if(!name.matches("^[a-zA-Z0-9\\u4E00-\\u9FA5_.\\-]{1,90}$")){
				jsession.jresponse=new JResponse(false,"invalid_file_name","I{.文件名称格式错误}");
			}
			
			String group=SQLUtil.deleteCriminalChars(SysUtil.getHttpParameter(request, "group",""));
			if(!group.matches("^[\\S ]{0,128}$")){
				jsession.jresponse=new JResponse(false,"invalid_group","I{.分组名称格式错误}");
			}
			
			String desc=SQLUtil.deleteCriminalChars(SysUtil.getHttpParameter(request, "desc",""));
			if(!desc.matches("^[\\S ]{1,90}$")){
				jsession.jresponse=new JResponse(false,"invalid_desc","I{.分组描述格式错误}");
			}
			
			if(!name.endsWith(".xml")){
				name+=".xml";
			}
			
	      	File i18nConfigFile=new File(JProperties.getI18NPath()+name);
	      	if(!i18nConfigFile.exists()){
				jsession.jresponse=new JResponse(false,"file_not_exists","I{.文件不存在}");
	      		return;
	      	}
			
			//解析
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new FileInputStream(i18nConfigFile),"UTF-8");
			Element root = doc.getRootElement();
			
			Element groupElement=root.addElement("group");
			groupElement.addAttribute("name",group);
			groupElement.addAttribute("desc",desc);
			
			JUtilDom4j.save(doc,JProperties.getI18NPath()+name, "UTF-8");

			jsession.jresponse=new JResponse(true,"1","I{.添加成功}");
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.jresponse=new JResponse(false,"ERR","I{.系统错误}");
		}
	}
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void delGroup(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		try{
			String name=SysUtil.getHttpParameter(request, "name","");
			if(!name.matches("^[a-zA-Z0-9\\u4E00-\\u9FA5_.\\-]{1,90}$")){
				jsession.jresponse=new JResponse(false,"invalid_file_name","I{.文件名称格式错误}");
			}
			
			String group=SQLUtil.deleteCriminalChars(SysUtil.getHttpParameter(request, "group",""));
			if(!group.matches("^[\\S ]{0,128}$")){
				jsession.jresponse=new JResponse(false,"invalid_group","I{.分组名称格式错误}");
			}
			
			if(!name.endsWith(".xml")){
				name+=".xml";
			}
			
	      	File i18nConfigFile=new File(JProperties.getI18NPath()+name);
	      	if(!i18nConfigFile.exists()){
				jsession.jresponse=new JResponse(false,"file_not_exists","I{.文件不存在}");
	      		return;
	      	}
			
			//解析
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new FileInputStream(i18nConfigFile),"UTF-8");
			Element root = doc.getRootElement();
			
			Element exists=null;
			List groups=root.elements("group");
			for(int i=0;i<groups.size();i++){
				Element groupElement=(Element)groups.get(i);
				if(groupElement.attributeValue("name").equals(group)){
					exists=groupElement;
					break;
				}
			}
			
			if(exists!=null){
				root.remove(exists);
				JUtilDom4j.save(doc,JProperties.getI18NPath()+name, "UTF-8");
				
				process(i18nConfigFile);
				
				jsession.jresponse=new JResponse(true,"1","I{.删除成功}");
			}else{
				jsession.jresponse=new JResponse(false,"group_not_exists","I{.分组不存在}");
			}
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			jsession.jresponse=new JResponse(false,"ERR","I{.系统错误}");
		}
	}
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void save(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response)throws Exception{
		String content=SysUtil.getHttpParameter(request, "content","");
		String[] elements=content.split("I18N_SPLITTER_A");
		
		Map temp=new HashMap();
		
		Document doc=DocumentHelper.createDocument();
		Element root=doc.addElement("root");
		

		for(int i=0;i<elements.length;i++){
			if(elements[i].equals("")) continue;
			
			String[] cells=elements[i].split("I18N_SPLITTER_B");
			if(cells.length!=5) continue;
			
			String groupName=cells[0];//SysUtil.getHttpParameter(request, "group_name_"+i);
			String groupDesc=cells[1];//SysUtil.getHttpParameter(request, "group_desc_"+i);
			String key=cells[2];//SysUtil.getHttpParameter(request, "key_"+i);
			String lang=cells[3];//SysUtil.getHttpParameter(request, "lang_"+i);
			String value=cells[4];//SysUtil.getHttpParameter(request, "value_"+i);
			
			Element group=null;
			if(!temp.containsKey(groupName)){
				group=root.addElement("group");
				group.addAttribute("name", groupName);
				group.addAttribute("desc", groupDesc);
				temp.put(groupName,group);
			}else{
				group=(Element)temp.get(groupName);
			}

			Element string=null;
			if(!temp.containsKey(groupName+","+key)){
				string=group.addElement("string");
				string.addAttribute("key", key);
				temp.put(groupName+","+key,string);
			}else{
				string=(Element)temp.get(groupName+","+key);
			}
			
			
			Element resource=string.addElement("language");
			resource.addAttribute("code", lang);
			resource.setText(value);
		}
		
		String fileName=SysUtil.getHttpParameter(request, "file");
		JUtilDom4j.save(doc, JProperties.getI18NPath()+fileName, "UTF-8");

		jsession.jresponse=new JResponse(true,"1","I{.保存成功}");
	}
}
