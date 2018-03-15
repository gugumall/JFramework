package j.service.server;

import j.service.Client;
import j.service.Http;
import j.service.Rmi;
import j.util.ConcurrentMap;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * 某个服务的配置信息
 * @author JFramework
 *
 */
public class ServiceConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	//同一个服务多点部署时（即服务集群），以code值来标示同一服务。
	//客户端调用服务时，路由器会通过某种策略将同组服务中的一个实例返回给客户应用，以达到均衡负载的效果。
	private String serverUuid=null;//宿主uuid
	private String code=null;
	private String uuid=null;//服务的uuid
	private String name=null;//服务名称
	private String privacy=null;//隐私（认证）策略
	private Rmi rmi=null;//rmi服务接口配置信息
	private Http http=null;//http服务接口配置信息
	private ConcurrentMap methods=null;//键: 方法名  值: j.service.Server.Method对象
	private ConcurrentMap clients=null;//键: 客户节点uuid  值: j.service.Client对象
	private ConcurrentMap config=null;//配置信息
	private String _toString="";
	
	/**
	 * 
	 *
	 */
	public ServiceConfig() throws RemoteException{
		super();
		methods=new ConcurrentMap();
		clients=new ConcurrentMap();
		config=new ConcurrentMap();
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
	 * @param code
	 */
	public void setCode(String code){
		this.code=code;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCode(){
		return this.code;
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
	 * @param rmi
	 */
	public void setRmi(Rmi rmi){
		this.rmi=rmi;
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
	 * @param method
	 */
	public void addMethod(Method method){
		this.methods.put(method.getName(),method);		
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Method getMethod(String name){
		return (Method)this.methods.get(name);
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
	 * 服务接口类
	 * @return
	 */
	public String getIntefaceName(){
		return this.getConfig("j.service.interface");
	}
	
	/**
	 * 服务实现类
	 * @return
	 */
	public String getClassName(){
		return this.getConfig("j.service.class");
	}
	
	/**
	 * 相关联的http handler的path(action*.xml中定义）
	 * @return
	 */
	public String getRelatedHttpHandlerPath(){
		return this.getConfig("j.service.relatedHttpHandlerPath");
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getFieldsKeep(){
		String p=this.getConfig("j.service.fieldsKeep");
		if(p!=null&&!p.equals("")) return p.split(",");
		return null;
	}
	
	/**
	 * 
	 * @param s
	 */
	public void setToString(String s){
		this._toString=s;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return _toString;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other){
		if(other==null||!(other instanceof ServiceConfig)){
			return false;
		}
		
		ServiceConfig otherConfig=(ServiceConfig)other;
		
		return this.toString().equals(otherConfig.toString());
	}
}