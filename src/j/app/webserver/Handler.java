package j.app.webserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author JFramework
 *
 */
public class Handler{
	private String path;
	private String pathPattern;
	private String clazz;
	private String requestBy;
	private boolean nonNvwaObj;
	private boolean singleton;
	private Map actions;
	
	/**
	 * constructor
	 *
	 */
	public Handler(){
		this.actions=new HashMap();
	}
	
	//getters
	public String getPath(){
		return this.path;
	}
	
	public String getPathPattern(){
		return this.pathPattern==null?Handlers.getActionPathPattern():this.pathPattern;
	}
	
	public String getClazz(){
		return this.clazz;
	}
	
	public String getRequestBy(){
		return this.requestBy;
	}
	
	public boolean getNonNvwaObj(){
		return this.nonNvwaObj;
	}
	
	public boolean getSingleton(){
		return this.singleton;
	}
	
	public Action getAction(String id){
		return (Action)actions.get(id);
	}
	
	public List getActions(){
		List<Action> temp=new ArrayList();
		temp.addAll(actions.values());
		return temp;
	}
	//getters end
	
	//setters
	public void setPath(String path){
		this.path=path;
	}
	
	public void setPathPattern(String pathPattern){
		this.pathPattern=pathPattern;
	}
	
	public void setClazz(String clazz){
		this.clazz=clazz;
	}
	
	public void setRequestBy(String requestBy){
		this.requestBy=requestBy;
	}
	
	public void setNonNvwaObj(boolean nonNvwaObj){
		this.nonNvwaObj=nonNvwaObj;
	}
	
	public void setSingleton(boolean singleton){
		this.singleton=singleton;
	}
	
	public void addAction(Action action){
		this.actions.put(action.getId(),action);
	}
	//setters end
}
