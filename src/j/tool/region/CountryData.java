package j.tool.region;

import j.common.JObject;

public class CountryData extends JObject{
	private static final long serialVersionUID = 1L;
	public String code;
	public String mobileCode;
	public String cnName;
	public String enName;
	public String group;
	public String RE;
	
	/**
	 * 
	 * @param code
	 * @param mobileCode
	 * @param cnName
	 * @param enName
	 * @param group
	 * @param RE
	 */
	public CountryData(String code,String mobileCode,String cnName,String enName,String group,String RE){
		this.code=code;
		this.mobileCode=mobileCode;
		this.cnName=cnName;
		this.enName=enName;
		this.group=group;
		this.RE=RE;
	}
}
