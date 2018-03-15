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
		String s="4s,6l,0,5,37,36,0,n,2y,1a,34,2p,3d,1a,39,37,2t,36,1a,22,28,2p,3d,2d,37,2t,36,21,32,2u,33,0,0,0,0,0,0,0,1,2,0,5,24,0,8,2r,30,2x,2t,32,38,21,2s,38,0,i,24,2y,2p,3a,2p,1b,30,2p,32,2v,1b,2b,38,36,2x,32,2v,1n,24,0,7,2r,33,31,26,2p,31,2t,35,0,3i,0,1,2j,0,5,36,33,30,2t,37,38,0,j,2j,24,2y,2p,3a,2p,1b,30,2p,32,2v,1b,2b,38,36,2x,32,2v,1n,24,0,3,39,2x,2s,35,0,3i,0,1,24,0,5,39,32,2p,31,2t,35,0,3i,0,1,3c,34,38,0,r,1f,2p,2t,1h,2q,1g,1e,1l,1d,1g,1d,1f,1j,2t,1h,1f,2q,2p,1l,25,2z,3b,2s,1w,2y,2e,1l,38,0,0,39,36,0,j,2j,24,2y,2p,3a,2p,1a,30,2p,32,2v,1a,2b,38,36,2x,32,2v,1n,4t,5u,2e,6f,6h,t,3f,1z,2,0,0,3c,34,0,0,0,1,38,0,k,2a,27,24,1x,2n,28,1t,2h,2n,1v,24,21,1x,26,2c,2n,2d,2b,1x,2a,38,0,8,2u,2u,2u,2u,2u,2u,2u,2u,38,0,d,5f,4n,5e,50,5e,4v,5f,4l,5e,45,5e,3u,1t";
		System.out.println(s.length());
		
		String s1=JObject.serializable2String(s);
		System.out.println(s1.length());
		System.out.println(JObject.string2Serializable(s1));
		
		String s2=JObject.serializable2String(s,false);
		System.out.println(s2.length());
		System.out.println(JObject.string2Serializable(s2));
	}
}
