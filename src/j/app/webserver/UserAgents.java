package j.app.webserver;

import javax.servlet.http.HttpServletRequest;

import j.sys.SysUtil;
import j.util.JUtilString;

/**
 * 
 * @author 肖炯
 *
 * 2020年2月3日
 *
 * <b>功能描述</b>
 */
public class UserAgents {
	public static final String UA_UNKNOWN="UA_UNKNOWN";
	
	public static final String UA_PC="UA_PC";
	
	public static final String UA_IOS="UA_IOS";
	public static final String[] UA_IOS_KEYWORDS=new String[]{"iphone", "ipod", "ipad"};
	
	public static final String UA_ANDROID="UA_ANDROID";
	public static final String[] UA_ANDROID_KEYWORDS=new String[]{"android"};
	
	public static final String UA_WECHAT="UA_WECHAT";
	public static final String[] UA_WECHAT_KEYWORDS=new String[]{"micromessenger"};
	
	public static final String UA_MOBILE="UA_MOBILE";
	public static final String[] UA_MOBILE_KEYWORDS=new String[]{"iphone", "ipod", "ipad", "android", "mobile", "blackberry", "webos", "incognito", "webmate", "bada", "nokia", "lg", "ucweb", "skyfire"};
	
	public static final String UA_BROWSER="UA_BROWSER";
	public static final String[] UA_BROWSER_KEYWORDS=new String[]{"chrome", "qqbrowser", "ucbrowser", "sogou", "firefox", "edge", "safari", "opera", "taobrowser", "lbbrowser", "maxthon"};

	public final static String DOMAIN_UNKNOWN="DOMAIN_UNKNOWN";
	public final static String DOMAIN_WECHAT="DOMAIN_WECHAT";
	public final static String DOMAIN_MOBILE="DOMAIN_MOBILE";
	public final static String DOMAIN_IOS="DOMAIN_IOS";
	public final static String DOMAIN_ANDROID="DOMAIN_ANDROID";
	public final static String DOMAIN_PC="DOMAIN_PC";
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getUserAgentType(HttpServletRequest request) {
		if(request==null) return UA_UNKNOWN;
		
		String ua=request.getHeader("User-Agent");
		if(ua==null) return UA_UNKNOWN;
		
		ua=ua.toLowerCase();
		if(JUtilString.existsIgnoreCase(ua,UA_WECHAT_KEYWORDS)){
			return UA_WECHAT;
		}else if(JUtilString.existsIgnoreCase(ua,UA_IOS_KEYWORDS)){
			return UA_IOS;
		}else if(JUtilString.existsIgnoreCase(ua,UA_ANDROID_KEYWORDS)){
			return UA_ANDROID;
		}else if(JUtilString.existsIgnoreCase(ua,UA_MOBILE_KEYWORDS)
				&&JUtilString.existsIgnoreCase(ua,UA_BROWSER_KEYWORDS)){
			return UA_MOBILE;
		}else{
			return UA_PC;
		}
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getDomainType(HttpServletRequest request) {
		if(request==null) return UA_UNKNOWN;
		
		return getDomainType(SysUtil.getHttpDomain(request));
	}
	
	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static String getDomainType(String domain) {
		if(domain==null || "".equals(domain)) return DOMAIN_UNKNOWN;
		
		domain=domain.toLowerCase();
		
		if(domain.startsWith("w.") || domain.startsWith("wechat")) return DOMAIN_WECHAT;
		else if(domain.startsWith("ios.") || domain.startsWith("ios")) return DOMAIN_IOS;
		else if(domain.startsWith("app.") || domain.startsWith("app")) return DOMAIN_ANDROID;
		else if(domain.startsWith("m.") || domain.startsWith("mob")) return DOMAIN_MOBILE;
		else return DOMAIN_PC;		
	}
	
	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static boolean isWechat(String domain) {
		return DOMAIN_WECHAT.equals(getDomainType(domain));
	}
	
	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static boolean isAndroid(String domain) {
		return DOMAIN_ANDROID.equals(getDomainType(domain));
	}
	
	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static boolean isIos(String domain) {
		return DOMAIN_IOS.equals(getDomainType(domain));
	}
	
	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static boolean isMobile(String domain) {
		return DOMAIN_MOBILE.equals(getDomainType(domain));
	}
	
	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static boolean isPC(String domain) {
		return DOMAIN_PC.equals(getDomainType(domain));
	}
	
	/**
	 * 
	 * @param domain
	 * @param alt
	 * @return
	 */
	public static String replaceFirstDomainCell(String domain, String alt) {
		String[] cells=JUtilString.getTokens(domain, ".");
		if(cells.length<3) return alt+"."+domain;
		return alt+domain.substring(domain.indexOf("."));
	}
	
	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static String changeDomainForWechat(String domain) {
		if(UserAgents.isWechat(domain)) return domain;
		
		String[] cells=JUtilString.getTokens(domain, ".");
		if(cells.length<3) return "w."+domain;
		
		if(domain.startsWith("ios.")
				||domain.startsWith("app.")
				||domain.startsWith("m.")) {
			return UserAgents.replaceFirstDomainCell(domain, "w");
		}else if(domain.startsWith("ios")
				||domain.startsWith("app")
				||domain.startsWith("mob")) {
			return "wechat"+domain.substring(3);
		}else {
			return UserAgents.replaceFirstDomainCell(domain, "w");
		}
	}
	
	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static String changeDomainForIos(String domain) {
		if(UserAgents.isIos(domain)) return domain;
		
		String[] cells=JUtilString.getTokens(domain, ".");
		if(cells.length<3) return "ios."+domain;
		
		if(domain.startsWith("m.")
				||domain.startsWith("app.")
				||domain.startsWith("w.")) {
			return UserAgents.replaceFirstDomainCell(domain, "ios");
		}else if(domain.startsWith("mob")
				||domain.startsWith("app")) {
			return "ios"+domain.substring(3);
		}else if(domain.startsWith("wechat")) {
			return "ios"+domain.substring(6);
		}else {
			return UserAgents.replaceFirstDomainCell(domain, "ios");
		}
	}
	

	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static String changeDomainForAndroid(String domain) {
		if(UserAgents.isIos(domain)) return domain;
		
		String[] cells=JUtilString.getTokens(domain, ".");
		if(cells.length<3) return "app."+domain;
		
		if(domain.startsWith("m.")
				||domain.startsWith("ios.")
				||domain.startsWith("w.")) {
			return UserAgents.replaceFirstDomainCell(domain, "app");
		}else if(domain.startsWith("mob")
				||domain.startsWith("ios")) {
			return "app"+domain.substring(3);
		}else if(domain.startsWith("wechat")) {
			return "app"+domain.substring(6);
		}else {
			return UserAgents.replaceFirstDomainCell(domain, "app");
		}
	}
	
	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static String changeDomainForMobile(String domain) {
		if(UserAgents.isMobile(domain)) return domain;
		
		String[] cells=JUtilString.getTokens(domain, ".");
		if(cells.length<3) return "m."+domain;
		
		if(domain.startsWith("ios.")
				||domain.startsWith("app.")
				||domain.startsWith("w.")) {
			return UserAgents.replaceFirstDomainCell(domain, "m");
		}else if(domain.startsWith("ios")
				||domain.startsWith("app")) {
			return "mob"+domain.substring(3);
		}else if(domain.startsWith("wechat")) {
			return "mob"+domain.substring(6);
		}else {
			return UserAgents.replaceFirstDomainCell(domain, "m");
		}
	}
}
