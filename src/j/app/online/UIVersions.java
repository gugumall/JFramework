package j.app.online;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.dom4j.Element;

import j.app.Constants;
import j.common.JObject;
import j.log.Logger;
import j.sys.AppConfig;

/**
 * 
 * @author 肖炯
 *
 * 2020年3月12日
 *
 * <b>功能描述</b>
 */
public class UIVersions extends JObject{
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.create(UIVersions.class);
	
	private static List<UIVersion> _UIVersions=new ArrayList();
	
	/**
	 * 
	 * @param E
	 */
	public static void parse(Element E){
		_UIVersions.clear();
		
		if(E==null) return;
		
		List<Element> versionsE=E.elements("UI-version");
		for(int i=0; i<versionsE.size(); i++) {
			Element vE=versionsE.get(i);
			UIVersion version=new UIVersion(vE.attributeValue("id"), vE.attributeValue("name"));
			
			List<Element> conversE=vE.elements("path-convert");
			for(int j=0; j<conversE.size(); j++) {
				Element cE=conversE.get(j);
				version.addConvert(cE.attributeValue("original"), cE.attributeValue("to"));
			}
			
			_UIVersions.add(version);
			
			log.log("UI version:\r\n"+version.toString(), -1);
		}
	}
	
	/**
	 * 
	 * @param session
	 * @return
	 */
	public static UIVersion getVersion(HttpSession session) {
		String versionId=null;
		if(session!=null) versionId=(String)session.getAttribute(Constants.J_UI_VERSION);
		if(versionId==null) versionId=AppConfig.getPara("SYSTEM", Constants.J_UI_VERSION);
		return getVersion(versionId);
	}
	
	/**
	 * 
	 * @param versionId
	 * @return
	 */
	public static UIVersion getVersion(String versionId) {
		for(int i=0; i<_UIVersions.size(); i++) {
			if(_UIVersions.get(i).getId().equals(versionId)) return _UIVersions.get(i);
		}
		return null;
	}
	
	/**
	 * 
	 * @param session
	 * @param original
	 * @return
	 */
	public static String convert(HttpSession session, String original) {
		UIVersion version=getVersion(session);
		return version==null?null:version.convert(original);
	}
	
	/**
	 * 
	 * @param versionId
	 * @param original
	 * @return
	 */
	public static String convert(String versionId, String original) {
		UIVersion version=getVersion(versionId);
		return version==null?null:version.convert(original);
	}
}
