package j.app.webserver;


import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import j.common.JProperties;
import j.log.Logger;
import j.nvwa.Nvwa;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilMath;
import j.util.JUtilString;

/**
 * 配置信息
 * @author 肖炯
 */
public class Handlers implements Runnable{
	private static Logger log=Logger.create(Handlers.class);//日志输出	
	
	private static ConcurrentList actionDefinitionFileNames=new ConcurrentList();//动作定义文件名列表
	
	private static ConcurrentMap<String,Handler> handlersByPath=new ConcurrentMap();//动作列表	
	private static ConcurrentMap<String,Handler> handlersByRESTPath=new ConcurrentMap();//动作列表	
	private static ConcurrentMap<String,JResponser> responsers=new ConcurrentMap();//响应节点	
	private static String responserId=null;//本地作为响应节点的id
	private static String responserKey=null;//本地作为响应节点的key
	private static ConcurrentList<String> responsersClusterActions=new ConcurrentList();//需要同步的响应节点的请求地址
	
	private static ConcurrentMap globalNavigates=new ConcurrentMap();//全局导航配置（global-navigate）
	
	private static String[] actionPathPatterns=null;//action请求路径模式

	private static volatile boolean loggerOn=true;//是否默认开启日志（action中未配置时）
	private static volatile int loggerCount=1;//日志处理器个数
	private static volatile int loggerSelector=0;//当前使用哪个日志处理器
	private static ConcurrentList loggers=new ConcurrentList();//日志处理器
	
	private static volatile long actionTimeout=60000;//请求处理超时时间，如果超过此时间，日志系统将记录为“响应超时”

	private static ConcurrentMap<String,Long> lastModifiedOfFiles=new ConcurrentMap();//各文件最近修改时间
	
	private static volatile boolean loading=true;
	
	
	static{
		try{
			load();
			startMonitor();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_FATAL);
		}
	}
	
	/**
	 * 启动监控线程，发现配置文件修改了就自动重新加载
	 *
	 */
	private static void startMonitor(){
		Handlers monitor=new Handlers();
		Thread thread=new Thread(monitor);
		thread.start();
		log.log("Handlers monitor thread started.",-1);
	}
	
	/**
	 * 
	 * @param requestURI
	 * @return
	 */
	public static String isActionPath(String requestURI){
		waitWhileLoading();
		
		for(int i=0;i<actionPathPatterns.length;i++){
			if(requestURI.endsWith(actionPathPatterns[i])) return actionPathPatterns[i];
		}

		if(requestURI.endsWith("/")) requestURI=requestURI.substring(0, requestURI.length()-1);
		if(requestURI.lastIndexOf("/")>1){
			if(requestURI.indexOf(".")>0) return null;//RESTful风格的路径不能包含点(.)
			
			String RESTPath=requestURI.substring(0,requestURI.lastIndexOf("/"));
			//String action=requestURI.substring(requestURI.lastIndexOf("/")+1);
			Handler handler=Handlers.getHandlerByRESTPath(RESTPath);
			if(handler!=null) return handler.getPathPattern();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getActionPathPattern(){
		waitWhileLoading();
		return actionPathPatterns[0];
	}
	
	/**
	 * 得到返回地址，如未找到对应信息则返回系统定义的错误页面地址
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public static String getGlobalNavigateUrl(String condition)throws Exception{
		waitWhileLoading();
		if(condition==null||condition.equals("")) return null;

		Navigate navigate=(Navigate)globalNavigates.get(condition);
		return navigate==null?null:navigate.getUrl();
	}	
	
	/**
	 * 得到返回类型，如未找到对应信息则返回Navigate.TYPE_REDIRECT
	 * @param condition
	 * @return String
	 * @throws Exception
	 */
	public static String getGlobalNavigateType(String condition)throws Exception{
		waitWhileLoading();
		if(condition==null||condition.equals("")) return null;
		
		Navigate navigate=(Navigate)globalNavigates.get(condition);
		return navigate==null?null:navigate.getType();
	}
	
	/**
	 * 
	 * @param pathOrRESTPath
	 * @return
	 */
	public static Handler getHandler(String pathOrRESTPath){
		waitWhileLoading();
		if(pathOrRESTPath.endsWith("/")) pathOrRESTPath=pathOrRESTPath.substring(0, pathOrRESTPath.length()-1);
		Handler handler=(Handler)handlersByPath.get(pathOrRESTPath);
		if(handler==null&&pathOrRESTPath.lastIndexOf("/")>1){
			handler=(Handler)handlersByRESTPath.get(pathOrRESTPath.substring(0,pathOrRESTPath.lastIndexOf("/")));
		}
		return handler;
	}
	
	/**
	 * 
	 * @param RESTPath
	 * @return
	 */
	public static Handler getHandlerByRESTPath(String RESTPath){
		waitWhileLoading();
		return (Handler)handlersByRESTPath.get(RESTPath);
	}
	
	/**
	 * 
	 * @return
	 */
	public static List getHandlers(){
		waitWhileLoading();
		return handlersByPath.listValues();
	}
	
	/**
	 * 
	 * @return
	 */
	public static List<JResponser> getResponsers(){
		return responsers.listValues();
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static JResponser getResponser(String id) {
		return responsers.get(id);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getResponserId() {
		return responserId;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getResponserKey() {
		return responserKey;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isResponserClusterAction(String url) {
		for(int i=0; i<responsersClusterActions.size(); i++) {
			if(JUtilString.match(url, responsersClusterActions.get(i), "*")>-1) return true;
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public static ActionLogger selectLogger(){
		waitWhileLoading();
		synchronized(loggers){
			if(loggerSelector>=loggers.size()) loggerSelector=0;
			ActionLogger logger=(ActionLogger)loggers.get(loggerSelector);
			loggerSelector++;
			return logger;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean isLoggerOn(){
		return loggerOn;
	}
	

	/**
	 * 
	 * @return
	 */
	public static long getActionTimeout(){
		waitWhileLoading();
		return actionTimeout;
	}
	
	/**
	 * 加载配置信息
	 * @throws Exception
	 */
	private static void load()throws Exception{
		try{
			loading=true;
			
	      	actionDefinitionFileNames.clear();
			handlersByPath.clear();
			globalNavigates.clear();
			
			//文件是否存在
			File file = new File(JProperties.getConfigPath()+"actions.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
	        lastModifiedOfFiles.put(file.getAbsolutePath(),new Long(file.lastModified()));
	        
			Document doc = JUtilDom4j.parse(file.getAbsolutePath(),"UTF-8");
			Element root = doc.getRootElement();
			
			//loggerCount
			loggerOn=!"false".equalsIgnoreCase(root.elementText("logger-on"));
			String loggerCountSetting=root.elementText("loggers");
			if(JUtilMath.isInt(loggerCountSetting)){
				loggerCount=Integer.parseInt(loggerCountSetting);
			}
			
			//actionTimeout
			String actionTimeoutSetting=root.elementText("action-timeout");
			if(JUtilMath.isLong(actionTimeoutSetting)){
				actionTimeout=Long.parseLong(actionTimeoutSetting);
			}
			
			
			//action请求路径模式
			List patters=root.elements("action-path-pattern");
			actionPathPatterns=new String[patters.size()];
			for(int i=0;i<patters.size();i++){
				Element e=(Element)patters.get(i);
				actionPathPatterns[i]=e.getTextTrim();
			}
	     
			//配置文件列表
	      	Element actionsEle=root.element("actions");
	      	List modules=actionsEle.elements("module");
	      	for(int i=0;i<modules.size();i++){
	      		Element module=(Element)modules.get(i);
	      		actionDefinitionFileNames.add(module.getText());
	          	log.log("Actions Definition File:"+module.getText(),-1);
	      	}
	      	
	      	//响应节点
	      	responsers.clear();
	      	responserId=null;
	      	responserKey=null;
	      	responsersClusterActions.clear();
	      	
	      	Element responsersEle=root.element("responsers");
	      	if(responsersEle!=null) {
	      		responserId=responsersEle.attributeValue("id");
		      	responserKey=responsersEle.attributeValue("key");
		      	
		      	Element responsersClusterEle=responsersEle.element("cluster");
		      	if(responsersClusterEle!=null) {
			      	List urls=responsersClusterEle.elements("url");
		      		if(urls!=null&&urls.size()>0) {
		      			for(int j=0; j<urls.size(); j++) {
		    	      		Element urlEle=(Element)urls.get(j);
		    	      		responsersClusterActions.add(urlEle.getTextTrim());
		      			}
		      		}
		      	}
		      	
		      	List responserEles=responsersEle.elements("responser");
		      	for(int i=0;i<responserEles.size();i++){
		      		Element responserEle=(Element)responserEles.get(i);
		      		List urls=responserEle.elements("url");
		      		String[] urlPatterns=null;
		      		if(urls!=null&&urls.size()>0) {
		      			urlPatterns=new String[urls.size()];
		      			for(int j=0; j<urls.size(); j++) {
		    	      		Element urlEle=(Element)urls.get(j);
		    	      		urlPatterns[j]=urlEle.getTextTrim();
		      			}
		      		}
		      		
		      		JResponser responser=new JResponser(responserEle.elementText("id"),
		      				responserEle.elementText("name"),
		      				responserEle.elementText("urlBase"),
		      				responserEle.elementText("key"),
		      				urlPatterns);
		      		responsers.put(responser.getId(), responser);
		      		
		          	log.log("JResponser:"+responser,-1);
		      	}
	      	}
			
			for(int x=0;x<actionDefinitionFileNames.size();x++){
				file = new File(JProperties.getConfigPath()+actionDefinitionFileNames.get(x));
		        if(!file.exists()){
		        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
		        }
		        lastModifiedOfFiles.put(file.getAbsolutePath(),new Long(file.lastModified()));
		        
				doc = JUtilDom4j.parse(file.getAbsolutePath(),"UTF-8");
				root = doc.getRootElement();  
				
		        List handlerEles=root.elements("handler");
		        for(int i=0;handlerEles!=null&&i<handlerEles.size();i++){
		        	Element handlerEle=(Element)handlerEles.get(i);
		        	
		        	Handler handler= new Handler();
		        	handler.setPath(handlerEle.attributeValue("path"));
		        	
		        	String RESTStylePath=handlerEle.attributeValue("REST-style-path");
		        	if(RESTStylePath==null||"".equals(RESTStylePath)){
		        		RESTStylePath=handler.getPath();
		        	}
		        	handler.setRESTStylePath(RESTStylePath);
		        	
		        	handler.setPathPattern(handlerEle.attributeValue("path-pattern"));
		        	handler.setClazz(handlerEle.attributeValue("class"));
		        	handler.setRequestBy(handlerEle.attributeValue("request-by"));
	        		handler.setSingleton("false".equalsIgnoreCase(handlerEle.attributeValue("singleton"))?false:true);
		        	
		        	List actions=handlerEle.elements("action");
		        	for(int j=0;j<actions.size();j++){
			        	Element actionEle=(Element)actions.get(j);
			        	
			        	Action action=new Action();
			        	action.setId(actionEle.attributeValue("id"));
			        	action.setName(actionEle.attributeValue("name"));
			        	action.setMethod(actionEle.attributeValue("method"));
			        	action.setRespondWithString("true".equalsIgnoreCase(actionEle.attributeValue("respond-with-string")));
			        	action.setRoles(actionEle.attributeValue("roles"));
			        	action.setOnError(actionEle.attributeValue("on-error"));
			        	action.setIsBrowserOnly("true".equalsIgnoreCase(actionEle.attributeValue("is-browser-only")));
			        	
			        	List navigates=actionEle.elements("navigate");
			        	for(int k=0;k<navigates.size();k++){
			        		Element navigateEle=(Element)navigates.get(k);
			        		
			        		Navigate navigate=new Navigate();		        		
			        		navigate.setCondition(navigateEle.attributeValue("condition"));	        		
			        		navigate.setType(navigateEle.attributeValue("type"));
			        		navigate.setUrl(navigateEle.getTextTrim());
			        		
			        		action.addNavigate(navigate);
			        	}	
			        	
			        	Element logEle=actionEle.element("log");
			        	if(logEle!=null){
			        		if("true".equalsIgnoreCase(logEle.attributeValue("avail"))) {//action显示申明为“true”时，不管主配置是否开启日志，该action日志都将开启
				        		action.setLogEnabled(1);
			        		}else if("false".equalsIgnoreCase(logEle.attributeValue("avail"))) {//action显示申明为“false”时，不管主配置是否开启日志，该action日志都将关闭
				        		action.setLogEnabled(0);
			        		}else {//否则，以主配置为准
				        		action.setLogEnabled(-1);
			        		}
			        		
			        		//需要保存的参数
			        		List logParams=logEle.elements("p");
			        		if(logParams!=null && logParams.size()>0){//指定了需要保存的参数
			        			action.setLogAllParameters(false);

				        		for(int k=0;k<logParams.size();k++){
				        			Element logParamEle=(Element)logParams.get(k);
				        			action.addLogParam(logParamEle.getTextTrim());
				        		}
			        		}else{
			        			//是否保存全部参数
			        			action.setLogAllParameters(!"false".equalsIgnoreCase(logEle.attributeValue("save-all-parameters")));
				        	}
			        	}else{
			        		action.setLogEnabled(-1);
		        			action.setLogAllParameters(true);
			        	}
			        	
			        	handler.addAction(action);
		        	}
		        	
		        	handlersByPath.put(handler.getPath(),handler);
		        	if(handler.getRESTStylePath()!=null&&!"".equals(handler.getRESTStylePath())){
		        		handlersByRESTPath.put(handler.getRESTStylePath(),handler);
		        	}
		        	
		        	//托管给Nvwa
		        	if(handlerEle.attributeValue("non-nvwa-obj")==null){
		        		handler.setNonNvwaObj(false);
			        	Nvwa.entrust(handler.getPath(),handler.getClazz(),handler.getSingleton());
		        	}else{
		        		handler.setNonNvwaObj(true);
		        	}
		        }
		        
	
				
				//global navigates
				List globalNavigateElements=root.elements("global-navigate");
				for(int i=0;globalNavigateElements!=null&&i<globalNavigateElements.size();i++){
		        	Element navigateElement=(Element)globalNavigateElements.get(i);
		        	
		        	Navigate navigate=new Navigate();        		
		        	navigate.setCondition(navigateElement.attributeValue("condition"));	        		
	        		navigate.setType(navigateElement.attributeValue("type"));
	        		navigate.setUrl(navigateElement.getTextTrim());
	        		globalNavigates.put(navigate.getCondition(),navigate);
	        	}	
			}
			
			//启动ActionLogger
			for(int i=loggers.size();i<loggerCount;i++){
				ActionLogger logger=new ActionLogger("ACTION_LOGGER_"+i);
				Thread thread=new Thread(logger,logger.getSn());
				thread.start();
				log.log("Thread "+"ACTION_LOGGER_"+i+" started.",-1);
				
				loggers.add(logger);
			}
			
			//停止多余的ActionLogger
			while(loggers.size()>loggerCount){
				ActionLogger logger=(ActionLogger)loggers.remove(loggers.size()-1);
				logger.shutdown();
				log.log("Thread "+logger.getSn()+" stopped.",-1);
			}

			loading=false;
		}catch(Exception ex){
			loading=false;
			log.log(ex,Logger.LEVEL_FATAL);
		}
	}
	
	/**
	 * 
	 *
	 */
	private static void waitWhileLoading(){
		while(loading){
			try{
				Thread.sleep(100);
			}catch(Exception ex){}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * 检测actions*.xml是否修改过，如修改过重新加载配置
		 */
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			try{
				boolean changed=false;
	
				File file = new File(JProperties.getConfigPath()+"actions.xml");
				Long configLastModified=(Long)lastModifiedOfFiles.get(file.getAbsolutePath());
				if(configLastModified==null){
					log.log(file.getAbsolutePath()+" is a newly added file.",-1);
					changed=true;
				}else{
					if(configLastModified.longValue()<file.lastModified()){
						log.log(file.getAbsolutePath()+" has been modified.",-1);
						changed=true;
					}else{
						Document doc = JUtilDom4j.parse(file.getAbsolutePath(),"UTF-8");
						Element root = doc.getRootElement();
				     
				      	Element actionsEle=root.element("actions");
				      	List modules=actionsEle.elements("module");//配置文件列表
				      	for(int i=0;i<modules.size();i++){
				      		Element module=(Element)modules.get(i);
				      		
				      		file=new File(JProperties.getConfigPath()+module.getText());
				      		configLastModified=(Long)lastModifiedOfFiles.get(file.getAbsolutePath());
				      		if(configLastModified==null){
								log.log(file.getAbsolutePath()+" is a newly added file.",-1);
								changed=true;
							}else if(configLastModified.longValue()<file.lastModified()){
								log.log(file.getAbsolutePath()+" has been modified.",-1);
								changed=true;
							}
				      		file=null;
				      	}
					}
				}
				
				if(changed){
					log.log("some files have been modified, so reload the config.",-1);
					Handlers.load();
				}
			}catch(Exception ex){
				log.log(ex,Logger.LEVEL_FATAL);
			}
		}
	}
}
