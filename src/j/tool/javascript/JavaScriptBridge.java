package j.tool.javascript;

import java.io.File;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import j.fs.JDFSFile;
import j.util.ConcurrentMap;

/**
 * 
 * @author 肖炯
 *
 */
public class JavaScriptBridge {
	private static ConcurrentMap engines=new ConcurrentMap();
	private Invocable engine;
	
	/**
	 * 
	 * @param engine
	 */
	public JavaScriptBridge(Invocable engine){
		this.engine=engine;
	}
	
	/**
	 * 
	 * @param filePath
	 * @param fileEncoding
	 * @return
	 * @throws Exception
	 */
	public static JavaScriptBridge getInstanceOfJsFile(String filePath,String fileEncoding) throws Exception{
		if(engines.containsKey(filePath)){
			return (JavaScriptBridge)engines.get(filePath);
		}
		
		try{
			String script=JDFSFile.read(new File(filePath),fileEncoding);
		
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine _engine = manager.getEngineByName("javascript");
			
			_engine.eval(script);
			
			JavaScriptBridge instance=new JavaScriptBridge((Invocable)_engine);
			engines.put(filePath, instance);
			
			return instance;
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * 
	 * @param engineName
	 * @param script
	 * @return
	 * @throws Exception
	 */
	public static JavaScriptBridge getInstanceOfJsString(String engineName,String script) throws Exception{
		if(engines.containsKey(engineName)){
			return (JavaScriptBridge)engines.get(engineName);
		}
		
		try{
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine _engine = manager.getEngineByName("javascript");
			
			_engine.eval(script);
			
			JavaScriptBridge instance=new JavaScriptBridge((Invocable)_engine);
			engines.put(engineName, instance);
			
			return instance;
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * 
	 * @param name
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Object call(String name,Object... params) throws Exception{
		return engine.invokeFunction(name,params);
	}
}
