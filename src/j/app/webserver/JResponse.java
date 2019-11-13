package j.app.webserver;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import j.I18N.I18N;
import j.fs.JDFSFile;
import j.util.JUtilBean;
import j.util.JUtilJSON;
import j.util.JUtilKeyValue;
import j.util.JUtilMath;

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
		String content=JDFSFile.read(new File("f:/temp/aaa.html"), "UTF-8");
		
		System.out.println(System.currentTimeMillis());
		String[] contents=content.split("I\\{");
		System.out.println(System.currentTimeMillis()+","+contents.length);
		
		boolean startsWith=content.startsWith("I{");

		StringBuffer _content=new StringBuffer(startsWith?"":contents[0]);
		for(int i=(startsWith?0:1);i<contents.length;i++) {
			int end=contents[i].indexOf("}");
			if(end<0) {
				_content.append(contents[i]);
				continue;
			}
			
			String alt="BBB";
			_content.append(alt);
			_content.append(contents[i].substring(end+1));
		}
		
	
		content=_content.toString();
		
		System.out.println(System.currentTimeMillis());
		JDFSFile.saveString("f:/temp/bbb.html", content, false, "UTF-8");
	}
}
