package j.service.router;

import java.io.Serializable;

/**
 * 
 * @author 肖炯
 *
 */
public class Service implements Serializable{	
	private static final long serialVersionUID = 1L;
	public String uuid;
	public String rmi;
	public String http;
	public String interfaceClassName;
	public String clusterCode;
	
	/**
	 * 
	 * @param uuid
	 * @param rmiiiop
	 * @param http
	 * @param interfaceClassName
	 * @param clusterCode
	 */
	public Service(String uuid,String rmiiiop,String http,String interfaceClassName,String clusterCode) {
		this.uuid=uuid;
		this.rmi=rmiiiop;
		this.http=http;
		this.interfaceClassName=interfaceClassName;
		this.clusterCode=clusterCode;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return uuid+","+rmi+","+http+","+interfaceClassName+","+clusterCode;
	}
}
