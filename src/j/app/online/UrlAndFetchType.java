package j.app.online;

import j.common.JObject;

/**
 * 
 * @author 肖炯
 *
 * 2020年3月12日
 *
 * <b>功能描述</b>
 */
public class UrlAndFetchType extends JObject{
	private static final long serialVersionUID = 1L;
	public static final int TYPE_REDIRECT=1;
	public static final int TYPE_FORWARD=2;
	
	private String url;
	private int fetchType;
	
	/**
	 * 
	 * @param url
	 * @param fetchType
	 */
	public UrlAndFetchType(String url, int fetchType) {
		this.url=url;
		if(fetchType!=TYPE_REDIRECT && fetchType!=TYPE_FORWARD) fetchType=TYPE_FORWARD;
		this.fetchType=fetchType;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public int getFetchType() {
		return this.fetchType;
	}
}
