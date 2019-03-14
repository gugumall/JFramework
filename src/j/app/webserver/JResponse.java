package j.app.webserver;

import j.I18N.I18N;
import j.util.JUtilBean;
import j.util.JUtilJSON;
import j.util.JUtilKeyValue;
import j.util.JUtilMath;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

/**
 * 
 * @author ceo
 *
 */
public class JResponse{
	private boolean success;
	private String code;
	private String message;
	private List<JUtilKeyValue> datas=new LinkedList<JUtilKeyValue>();
	
	public JResponse(boolean success,String code,String message){
		this.success=success;
		this.code=code;
		this.message=message;
	}
	
	/**
	 * 
	 * @param success
	 * @param code
	 * @param message
	 * @param session
	 */
	public JResponse(boolean success,String code,String message,HttpSession session){
		this.success=success;
		this.code=code;
		message=I18N.convert(message,I18N.getCurrentLanguage(session));
		this.message=message;
		
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void putData(Object key,Object value){
		datas.add(new JUtilKeyValue(key,value));
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
	 * @return
	 */
	public boolean isSuccess(){
		return this.success;
	}
	
	/**
	 * 
	 * @param message
	 */
	public void setMessage(String message){
		this.message=message;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMessage(){
		return this.message;
	}
	
	/**
	 * 
	 * @return
	 */
	public List getDatas(){
		return this.datas;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		if(code==null) code="";
		if(message==null) message="";
		
		StringBuffer s=new StringBuffer();
		s.append("{\"success\":\""+success+"\",");
		s.append("\"code\":\""+JUtilJSON.format(code)+"\",");
		s.append("\"message\":\""+JUtilJSON.format(message)+"\",\"datas\":{");
		for(int i=0;i<datas.size();i++){
			JUtilKeyValue data=datas.get(i);
			if(data==null) continue;
			
			s.append("\""+data.getKey()+"\":");
			if(data.getValue() instanceof List){
				s.append(JUtilBean.beans2Json((List)data.getValue()));
			}else if(data.getValue() instanceof String){
				s.append("\""+JUtilJSON.format(data.getValue().toString())+"\"");
			}else if(data.getValue() instanceof Integer){
				s.append("\""+data.getValue().toString()+"\"");
			}else if(data.getValue() instanceof Long){
				s.append("\""+data.getValue().toString()+"\"");
			}else if(data.getValue() instanceof Double){
				s.append("\""+JUtilMath.formatPrintWithoutZero((double)data.getValue(),20)+"\"");
			}else if(data.getValue() instanceof Timestamp){
				s.append("\""+data.getValue().toString()+"\"");
			}else{
				s.append(JUtilBean.bean2Json(data.getValue()));
			}
			s.append(",");
		}
		if(s.charAt(s.length()-1)==',') s=s.deleteCharAt(s.length()-1);
		s.append("}}");
		return s.toString();
	}
	
	public static void main(String[] args){
		JResponse r=new JResponse(true,"done","操作\"成功");
		r.putData("aaa","哈哈\"DFD");
		r.putData("bbb","cc");
		System.out.println(r);
	}
}
