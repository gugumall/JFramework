package j.ext.workflow;

import j.common.JObject;
import j.util.ConcurrentMap;

/**
 * 
 * @author 肖炯
 *
 */
public class JWFFlowConfig extends JObject{
	private static final long serialVersionUID = 1L;

	private JWFEngineConfig engine;
	private ConcurrentMap tasks=new ConcurrentMap();
	
	public JWFEngineConfig getEngine(){
		return this.engine;
	}
	public void setEngine(JWFEngineConfig engine){
		this.engine=engine;
	}
	
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public JWFTaskConfig getTaskConfig(String uuid){
		return (JWFTaskConfig)tasks.get(uuid);
	}
	
	/**
	 * 
	 * @param uuids
	 * @return
	 */
	public JWFTaskConfig[] getNexts(String[] uuids){
		if(uuids==null||uuids.length==0) return null;
		
		JWFTaskConfig[] ns=new JWFTaskConfig[uuids.length];
		for(int i=0;i<uuids.length;i++){
			ns[i]=getTaskConfig(uuids[i]); 
		}
		
		return ns;
	}
}
