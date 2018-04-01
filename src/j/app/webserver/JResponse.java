package j.app.webserver;

import j.I18N.I18N;
import j.util.JUtilKeyValue;
import j.util.JUtilString;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

/**
 * 
 * @author ceo
 *
 */
public class JResponse{
	private boolean succuss;
	private String code;
	private String message;
	private List<JUtilKeyValue> datas=new LinkedList();
	
	public JResponse(boolean succuss,String code,String message){
		this.succuss=succuss;
		this.code=code;
		this.message=message;
	}
	
	/**
	 * 
	 * @param succuss
	 * @param code
	 * @param message
	 * @param session
	 */
	public JResponse(boolean succuss,String code,String message,HttpSession session){
		this.succuss=succuss;
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		if(code==null) code="";
		if(message==null) message="";
		
		String s="{\"succuss\":\""+succuss+"\",";
		s+="\"code\":\""+format(code)+"\",";
		s+="\"message\":\""+format(message)+"\",\"datas\":{";
		for(int i=0;i<datas.size();i++){
			JUtilKeyValue data=datas.get(i);
			s+="\""+data.getKey()+"\":\""+format(data.getValue().toString())+"\",";
		}
		if(s.endsWith(",")) s=s.substring(0,s.length()-1);
		s+="}}";
		return s;
	}
	
	private String format(String s){
		s=JUtilString.replaceAll(s,"\"","\\\"");
		return s;
	}
	
	public static void main(String[] args){
		JResponse r=new JResponse(true,"done","操作\"成功");
		r.putData("aaa","哈哈\"DFD");
		r.putData("bbb","cc");
		System.out.println(r);
	}
}
