package j.http;

/**
 * 
 * @author 肖炯
 *
 */
public class JHttpCookie {
	private String name;
	private String value;
	private int version;
	private String domain;
	private String path;
	
	/**
	 * 
	 * @param name
	 * @param value
	 * @param version
	 * @param domain
	 * @param path
	 */
	public JHttpCookie(String name, String value, int version, String domain, String path) {
		this.name=name;
		this.value=value;
		this.version=version;
		this.domain=domain;
		this.path=path;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public int getVersion() {
		return this.version;
	}
	
	public String getDomain() {
		return this.domain;
	}
	
	public String getPath() {
		return this.path;
	}
}
