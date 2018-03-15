package j.service.router;

import j.service.Client;
import j.service.Http;
import j.service.Rmi;
import j.util.ConcurrentMap;

import java.io.Serializable;

public class RouterConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	private String serverUuid=null;//宿主uuid
	private String uuid=null;
	private String name=null;
	private String privacy=null;
	private Rmi rmi=null;
	private Http http=null;
	private ConcurrentMap config=null;//配置信息

	//可访问此路由节点的客户端（服务节点、客户节点），键：节点uuid 值： j.service.Client对象
	//由于作为一个集群的路由节点是透明的向服务节点、客户节点提供服务的（它们并不知道有多个路由节点），所以每个路由节点上分配给客户节点的密钥必须是相同的。
	private ConcurrentMap clients=null;
	private boolean started=false;

	/**
	 * 
	 *
	 */
	public RouterConfig(){
		super();
		clients=new ConcurrentMap();
		config=new ConcurrentMap();
	}
	
	/**
	 * 
	 * @param started
	 */
	public void setStarted(boolean started){
		this.started=started;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getStarted(){
		return this.started;
	}

	/**
	 * 
	 * @param serverUuid
	 */
	public void setServerUuid(String serverUuid){
		this.serverUuid=serverUuid;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getServerUuid(){
		return this.serverUuid;
	}
	
	/**
	 * 
	 * @param uuid
	 */
	public void setUuid(String uuid){
		this.uuid=uuid;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUuid(){
		return this.uuid;
	}
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name){
		this.name=name;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * 
	 * @param privacy
	 */
	public void setPrivacy(String privacy){
		this.privacy=privacy;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPrivacy(){
		return this.privacy;
	}
	
	/**
	 * 
	 * @param rmiiiop
	 */
	public void setRmi(Rmi rmiiiop){
		this.rmi=rmiiiop;
	}
	
	/**
	 * 
	 * @return
	 */
	public Rmi getRmi(){
		return this.rmi;
	}
	
	/**
	 * 
	 * @param http
	 */
	public void setHttp(Http http){
		this.http=http;
	}
	
	/**
	 * 
	 * @return
	 */
	public Http getHttp(){
		return this.http;
	}
	
	/**
	 * 
	 * @param client
	 */
	public void addClient(Client client){
		this.clients.put(client.getUuid(),client);		
	}
	
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public Client getClient(String uuid){
		return (Client)this.clients.get(uuid);
	}
	


	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void addConfig(String key,String value){
		config.put(key,value);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getConfig(String key){
		return (String)config.get(key);
	}
	
	/**
	 * 
	 * @return
	 */
	public ConcurrentMap getConfig(){
		return config;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getIntefaceName(){
		return this.getConfig("j.service.interface");
	}
	
	/**
	 * 
	 * @return
	 */
	public String getClassName(){
		return this.getConfig("j.service.class");
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRelatedHttpHandlerPath(){
		return this.getConfig("j.service.relatedHttpHandlerPath");
	}
}
