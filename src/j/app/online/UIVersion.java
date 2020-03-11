package j.app.online;

import java.util.ArrayList;
import java.util.List;

import j.common.JObject;
import j.util.JUtilJSON;
import j.util.JUtilString;

/**
 * 
 * @author 肖炯
 *
 * 2020年3月12日
 *
 * <b>功能描述</b>
 */
public class UIVersion extends JObject{
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private List<UIPathConvert> converts=new ArrayList();
	
	/**
	 * 
	 * @param id
	 * @param name
	 */
	public UIVersion(String id, String name) {
		this.id=id;
		this.name=name;
	}
	
	public void addConvert(String original, String to) {
		this.converts.add(new UIPathConvert(original, to));
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<UIPathConvert> getConverts(){
		return this.converts;
	}
	
	/**
	 * 
	 * @param original
	 * @return
	 */
	public String convert(String original) {
		if(original==null || "".equals(original)) return null;
		
		UIPathConvert convertMathesMax=null;//路径匹配最长的
		for(int i=0; i<this.converts.size(); i++) {
			UIPathConvert convert=this.converts.get(i);
			if(original.indexOf(convert.getOriginal())>-1) {
				if(convertMathesMax==null || convertMathesMax.getOriginal().length()<convert.getOriginal().length()) {
					convertMathesMax=convert;
				}
			}
		}
		
		if(convertMathesMax==null) return null;//未匹配到
		
		return JUtilString.replaceAll(original, convertMathesMax.getOriginal(), convertMathesMax.getTo());
	}
	
	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("{\"id\":\""+JUtilJSON.convert(id)+"\"\r\n");
		sb.append(",\"name\":\""+JUtilJSON.convert(name)+"\"\r\n");
		sb.append(",\"converts\":[");
		for(int i=0; i<this.converts.size(); i++) {
			if(i>0) sb.append("\r\n,");
			sb.append(this.converts.get(i).toString()+"");
		}
		sb.append("]}");
		return sb.toString();
	}
}
