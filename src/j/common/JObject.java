package j.common;

import j.cache.CachedList;
import j.cache.CachedMap;
import j.util.ConcurrentList;
import j.util.ConcurrentMap;
import j.util.JUtilZip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 
 * @author 肖炯
 *
 */
public class JObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private static ConcurrentMap statics=new ConcurrentMap();
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static ConcurrentMap getStaticMap(String key){
		if(statics.containsKey(key)){
			return (ConcurrentMap)statics.get(key);
		}else{
			ConcurrentMap s=new ConcurrentMap();
			statics.put(key, s);
			return s;
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static ConcurrentList getStaticList(String key){
		if(statics.containsKey(key)){
			return (ConcurrentList)statics.get(key);
		}else{
			ConcurrentList s=new ConcurrentList();
			statics.put(key, s);
			return s;
		}
	}
	

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static CachedMap getStaticCachedMap(String key){
		if(statics.containsKey(key)){
			return (CachedMap)statics.get(key);
		}else{
			try{
				CachedMap s=new CachedMap(key);
				statics.put(key, s);
				return s;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static CachedList getStaticCachedList(String key){
		if(statics.containsKey(key)){
			return (CachedList)statics.get(key);
		}else{
			try{
				CachedList s=new CachedList(key);
				statics.put(key, s);
				return s;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
	}
	
	/**
	 * 
	 *
	 */
	public JObject() {
		super();
	}
	/**
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String serializable2String(Serializable obj)throws Exception{
		if(obj==null){
			return "jserializable:null";
		}
		ByteArrayOutputStream byteOS=new ByteArrayOutputStream();
		ObjectOutputStream oos=new ObjectOutputStream(byteOS);
		oos.writeObject(obj);
		oos.flush();
		String ret= byteOS.toString("ISO-8859-1");

		try{
			oos.close();
		}catch(Exception e){}
		
		try{
			byteOS.close();
		}catch(Exception e){}	

		ret="jserializable:"+JObject.string2IntSequence(ret);
		ret=JUtilZip.gzipString(ret,"ISO-8859-1");
		
		return ret;
	}
	
	/**
	 * 
	 * @param obj
	 * @param gzip
	 * @return
	 * @throws Exception
	 */
	public static String serializable2String(Serializable obj,boolean gzip)throws Exception{
		if(obj==null){
			return "jserializable:null";
		}
		ByteArrayOutputStream byteOS=new ByteArrayOutputStream();
		ObjectOutputStream oos=new ObjectOutputStream(byteOS);
		oos.writeObject(obj);
		oos.flush();
		String ret= byteOS.toString("ISO-8859-1");

		try{
			oos.close();
		}catch(Exception e){}
		
		try{
			byteOS.close();
		}catch(Exception e){}	

		ret="jserializable:"+JObject.string2IntSequence(ret);
		if(gzip) ret=JUtilZip.gzipString(ret,"ISO-8859-1");
		
		return ret;
	}
	
	/**
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Object string2Serializable(String str) throws Exception{
		if(str==null||"jserializable:null".equals(str)){
			return null;
		}

		if(!str.startsWith("jserializable:")){
			str=JUtilZip.readGzipString(str,"ISO-8859-1");
		}
		str=JObject.intSequence2String(str.substring(14));
		
		ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
		Object obj=ois.readObject();
		
		try{
			ois.close();
		}catch(Exception e){}
		
		return obj;
	}
	
	/**
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Object inputStream2Serializable(InputStream in) throws Exception{
		if(in==null){
			return null;
		}
		
		ObjectInputStream ois=new ObjectInputStream(in);
		Object obj=ois.readObject();
		
		try{
			ois.close();
		}catch(Exception e){}
		
		return obj;
	}
	

	
	/**
	 * 
	 * @param string
	 * @return
	 */
	public static String string2IntSequence(String string){
		if(string==null) return null;
		StringBuffer writer=new StringBuffer();
		for(int i=0;i<string.length();i++){
			String cha=Integer.toString((int)string.charAt(i),Character.MAX_RADIX);
			if(i==0) writer.append(cha);
			else writer.append(","+cha);
		}
		string = writer.toString();
		writer=null;
		
		return string;
	}
	
	/**
	 * 
	 * @param sequence
	 * @return
	 */
	public static String intSequence2String(String sequence){
		if(sequence==null||sequence.equals("")) return sequence;
		
		if(sequence.startsWith("jis:")) sequence=sequence.substring(4);
		if(sequence.equals("")) return sequence;
	
		StringBuffer writer=new StringBuffer();
		String[] arr=sequence.split(",");
		for(int i=0;i<arr.length;i++){
			writer.append((char)Integer.parseInt(arr[i],Character.MAX_RADIX));
		}
		sequence=writer.toString();
		writer=null;
		arr=null;
		return sequence;
	}
	
	/**
	 * 测试
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		String s1="jis:2p,3a,2p,2x,30,2p,2q,30,2t";
		System.out.println(JObject.intSequence2String(s1));
	}
}
