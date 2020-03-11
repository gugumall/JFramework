package j.app.online;

import j.common.JObject;
import j.util.JUtilJSON;

/**
 * 
 * @author 肖炯
 *
 * 2020年3月12日
 *
 * <b>功能描述</b>
 */
public class UIPathConvert extends JObject{
	private static final long serialVersionUID = 1L;

	private String original;
	private String to;
	
	/**
	 * 
	 * @param original
	 * @param to
	 */
	public UIPathConvert(String original, String to) {
		this.original=original;
		this.to=to;
	}
	
	public String getOriginal() {
		return this.original;
	}
	
	public String getTo() {
		return this.to;
	}
	
	@Override
	public String toString() {
		return "{\"original\":\""+JUtilJSON.convert(original)+"\",\"to\":\""+JUtilJSON.convert(to)+"\"}";
	}
}
