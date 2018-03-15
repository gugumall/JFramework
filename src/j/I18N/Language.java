package j.I18N;

import j.util.JUtilString;

import java.io.Serializable;

/**
 * 
 * @author JFramework
 *
 */
public class Language implements Serializable {
	private static final long serialVersionUID = 1L;
	private String code;//语言标识
	private String name;//语言名字
	private String[] countries;//适用国家编码
	
	//getters
	public String getCode(){
		return this.code;
	}
	public String getName(){
		return this.name;
	}
	public String[] getCountries(){
		return this.countries;
	}
	public boolean matches(String countryCode){
		if(this.countries==null) return false;
		return JUtilString.containIgnoreCase(countries,countryCode);
	}
	//getters ends
	
	//setters
	public void setCode(String code){
		this.code=code;
	}
	public void setName(String name){
		this.name=name;
	}
	public void setCountries(String[] countries){
		this.countries=countries;
	}
	//setters ends
}