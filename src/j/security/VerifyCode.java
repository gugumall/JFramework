package j.security;

import j.Properties;
import j.common.JObject;
import j.log.Logger;
import j.service.Manager;
import j.service.client.Client;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 
 * @author 肖炯
 *
 */
public class VerifyCode implements Runnable{
	private static Logger log=Logger.create(VerifyCode.class);
	public static final int TYPE_NUMBER=1;
	public static final int TYPE_CHAR=2;
	private static volatile long TIMEOUT_A=300000;
	private static volatile long TIMEOUT_B=900000;
	private static volatile long TIMEOUT_C=1800000;
	private static volatile long TIMEOUT_D=3600000;
	private static volatile long INTERVAL_A=60000;
	private static volatile long INTERVAL_B=300000;
	private static volatile long INTERVAL_C=1800000;
	private static volatile long INTERVAL_D=3600000;
	
	private static String serviceChannel="http";
	private static String serviceCode="";
	private static String cacheId="VerifyCodes";
	
	private static ConcurrentMap records=new ConcurrentMap();//发送记录
	
	private static long configLastModified=0;//配置文件上次修改时间
	private static volatile boolean loading=true;
	
	static{
		load();
		
		VerifyCode vc=new VerifyCode();
		Thread thread=new Thread(vc);
		thread.start();
		log.log("VerifyCode monitor thread started.",-1);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getServiceChannel(){
		waitWhileLoading();
		return serviceChannel;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getServiceCode(){
		waitWhileLoading();
		return serviceCode;
	}
	
	/**
	 * 
	 * @return
	 */
	public static long getTimeoutA(){
		waitWhileLoading();
		return TIMEOUT_A;
	}
	
	/**
	 * 
	 * @return
	 */
	public static long getTimeoutB(){
		waitWhileLoading();
		return TIMEOUT_B;
	}
	
	/**
	 * 
	 * @return
	 */
	public static long getTimeoutC(){
		waitWhileLoading();
		return TIMEOUT_C;
	}
	
	/**
	 * 
	 * @return
	 */
	public static long getTimeoutD(){
		waitWhileLoading();
		return TIMEOUT_D;
	}
	
	/**
	 * 
	 * @return
	 */
	public static long getIntervalA(){
		waitWhileLoading();
		return INTERVAL_A;
	}
	
	/**
	 * 
	 * @return
	 */
	public static long getIntervalB(){
		waitWhileLoading();
		return INTERVAL_B;
	}
	
	/**
	 * 
	 * @return
	 */
	public static long getIntervalC(){
		waitWhileLoading();
		return INTERVAL_C;
	}
	
	/**
	 * 
	 * @return
	 */
	public static long getIntervalD(){
		waitWhileLoading();
		return INTERVAL_D;
	}
	
	/**
	 * 
	 * @param uuid
	 * @param interval
	 * @return
	 */
	public static long can(String uuid,long interval){
		VerifyCodeSendRecord record=(VerifyCodeSendRecord)records.get(uuid);
		if(record==null) return 0;
		return (SysUtil.getNow()-record.sendTime>=interval)?0:(interval+record.sendTime-SysUtil.getNow());
	}
	
	/**
	 * 
	 * @param _cacheId
	 * @param uuid
	 * @param related
	 * @param type
	 * @param length
	 * @param _timeout
	 * @param interval
	 * @return
	 * @throws Exception
	 */
	public static String get(String _cacheId,String uuid,String related,int type,int length,long _timeout,long interval) throws Exception{
		if(_cacheId==null
				||"".equals(_cacheId)
				||uuid==null
				||"".equals(uuid)){
			return null;
		}
		
		//VerifyCodeSendRecord record=(VerifyCodeSendRecord)records.get(uuid);
		//if(record!=null
		//		&&(related==null||"".equals(related)||related.equals(record.related))
		//		&&!record.removable()){
		//		return record.code;
		//}
		
		if(can(uuid,interval)>0){
			throw new Exception("too frequent to get verify code by uuid - "+uuid+",interval - "+interval);
		}
		try{
			VerifyCodeService service=(VerifyCodeService)Client.rmiGetService(serviceCode,true);
			String httpChannel=null;
			if(service==null){
				if("rmi".equalsIgnoreCase(serviceChannel)){
					service=(VerifyCodeService)Client.rmiGetService(serviceCode);			
				}else{
					httpChannel=Client.httpGetService(null,null,serviceCode);
				}
			} 
			
			if(service!=null){
				String code= service.g(Manager.getClientNodeUuid(),
						Client.md54Service(serviceCode,"g"),
						_cacheId==null?cacheId:_cacheId,
						uuid, 
						type, 
						length, 
						_timeout);	
				records.put(uuid, new VerifyCodeSendRecord(uuid,related,code,_timeout));
				return code;
			}else if("http".equalsIgnoreCase(serviceChannel)){
				Map params=new HashMap();
				params.put("uuid",uuid);
				params.put("type",""+type);
				params.put("length",""+length);
				params.put("timeout",""+_timeout);
				String code=Client.httpCallPost(null,null,serviceCode,httpChannel,"g",params);
				params.clear();
				params=null;
				

				records.put(uuid, new VerifyCodeSendRecord(uuid,related,code,_timeout));
				return code;			
			}else{
				throw new Exception("no service channel avail.");
			}
		}catch(Exception ex){
			log.log(ex,Logger.LEVEL_ERROR);
			return null;
		}
	}
	
	/**
	 * 
	 * @param _cacheId
	 * @param uuid
	 * @param related
	 * @param code
	 * @param remove
	 * @return
	 */
	public static boolean check(String _cacheId,String uuid,String related,String code,boolean remove){
		if(_cacheId==null
				||"".equals(_cacheId)
				||uuid==null
				||"".equals(uuid)
				||code==null
				||"".equals(code)){
			return false;
		}
		
		VerifyCodeSendRecord record=(VerifyCodeSendRecord)records.get(uuid);
		if(record==null) return false;
		if(related!=null&&!"".equals(related)&&!related.equals(record.related)) return false;
		
		try{
			VerifyCodeService service=(VerifyCodeService)Client.rmiGetService(serviceCode,true);
			String httpChannel=null;
			if(service==null){
				if("rmi".equalsIgnoreCase(serviceChannel)){
					service=(VerifyCodeService)Client.rmiGetService(serviceCode);			
				}else{
					httpChannel=Client.httpGetService(null,null,serviceCode);
				}
			} 
			
			if(service!=null){
				boolean ok="true".equalsIgnoreCase(service.c(Manager.getClientNodeUuid(),
						Client.md54Service(serviceCode,"c"),
						_cacheId==null?cacheId:_cacheId,
						uuid, 
						code));		
				
				if(remove){
					records.remove(record.uuid);
					record=null;
				}						
				
				return ok;
			}else if("http".equalsIgnoreCase(serviceChannel)){
				Map params=new HashMap();
				params.put("uuid",uuid);
				params.put("code",code);
				params.put("cacheId",_cacheId==null?cacheId:_cacheId);
				String result=Client.httpCallPost(null,null,serviceCode,httpChannel,"c",params);
				params.clear();
				params=null;
				
				if(remove){
					records.remove(record.uuid);
					record=null;
				}
				
				boolean ok="true".equalsIgnoreCase(result);	
				return ok;
			}else{
				return false;
			}
		}catch(Exception ex){
			log.log(ex,Logger.LEVEL_ERROR);
			return false;
		}
	}
	
	/**
	 * 
	 * @param _cacheId
	 * @param uuid
	 * @return
	 */
	public static VerifyCodeBean exists(String _cacheId,String uuid){
		if(_cacheId==null
				||"".equals(_cacheId)
				||uuid==null
				||"".equals(uuid)){
			return null;
		}
		
		try{
			VerifyCodeService service=(VerifyCodeService)Client.rmiGetService(serviceCode,true);
			String httpChannel=null;
			if(service==null){
				if("rmi".equalsIgnoreCase(serviceChannel)){
					service=(VerifyCodeService)Client.rmiGetService(serviceCode);			
				}else{
					httpChannel=Client.httpGetService(null,null,serviceCode);
				}
			} 
			
			if(service!=null){
				return service.exists(Manager.getClientNodeUuid(),
						Client.md54Service(serviceCode,"exists"),
						_cacheId==null?cacheId:_cacheId,
						uuid);			
			}else if("http".equalsIgnoreCase(serviceChannel)){
				Map params=new HashMap();
				params.put("uuid",uuid);
				params.put("cacheId",_cacheId==null?cacheId:_cacheId);
				String result=Client.httpCallPost(null,null,serviceCode,httpChannel,"exists",params);
				params.clear();
				params=null;
				
				return (VerifyCodeBean)JObject.string2Serializable(result);	
			}else{
				return null;
			}
		}catch(Exception ex){
			log.log(ex,Logger.LEVEL_ERROR);
			return null;
		}
	}
	
	/**
	 * 
	 * @param _cacheId
	 * @param uuid
	 */
	public static void remove(String _cacheId,String uuid){
		try{
			VerifyCodeService service=(VerifyCodeService)Client.rmiGetService(serviceCode,true);
			String httpChannel=null;
			if(service==null){
				if("rmi".equalsIgnoreCase(serviceChannel)){
					service=(VerifyCodeService)Client.rmiGetService(serviceCode);			
				}else{
					httpChannel=Client.httpGetService(null,null,serviceCode);
				}
			} 
			
			if(service!=null){
				service.remove(Manager.getClientNodeUuid(),
						Client.md54Service(serviceCode,"remove"),
						_cacheId==null?cacheId:_cacheId,
						uuid);
			}else if("http".equalsIgnoreCase(serviceChannel)){
				Map params=new HashMap();
				params.put("uuid",uuid);
				params.put("cacheId",_cacheId==null?cacheId:_cacheId);
				Client.httpCallPost(null,null,serviceCode,httpChannel,"remove",params);
				params.clear();
				params=null;
			}
		}catch(Exception ex){
			log.log(ex,Logger.LEVEL_ERROR);
		}
	}
	
	
	/**
	 * 
	 *
	 */
	public static void load(){
		try{
			loading=true;
			
			//文件是否存在
			File file = new File(Properties.getConfigPath()+"VerifyCode.xml");
	        if(!file.exists()){
	        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
	        }
			
			Document document=JUtilDom4j.parse(Properties.getConfigPath()+"VerifyCode.xml",SysConfig.sysEncoding);
			Element root=document.getRootElement();
			
			VerifyCode.serviceChannel=root.elementText("service-channel");			
			VerifyCode.serviceCode=root.elementText("service-code");
			
			VerifyCode.TIMEOUT_A=Long.parseLong(root.elementText("TIMEOUT_A"));
			VerifyCode.TIMEOUT_B=Long.parseLong(root.elementText("TIMEOUT_B"));
			VerifyCode.TIMEOUT_C=Long.parseLong(root.elementText("TIMEOUT_C"));
			VerifyCode.TIMEOUT_D=Long.parseLong(root.elementText("TIMEOUT_D"));
			
			VerifyCode.INTERVAL_A=Long.parseLong(root.elementText("INTERVAL_A"));
			VerifyCode.INTERVAL_B=Long.parseLong(root.elementText("INTERVAL_B"));
			VerifyCode.INTERVAL_C=Long.parseLong(root.elementText("INTERVAL_C"));
			VerifyCode.INTERVAL_D=Long.parseLong(root.elementText("INTERVAL_D"));
			
			VerifyCode.cacheId=root.elementText("cache-id");
			
			root=null;
			document=null;

			//配置文件最近修改时间
			configLastModified=file.lastModified();
			
			loading=false;
		}catch(Exception e){
			loading=false;
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 *
	 */
	private static void waitWhileLoading(){
		while(loading){
			try{
				Thread.sleep(100);
			}catch(Exception ex){}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			try{
				Thread.sleep(5000);
			}catch(Exception e){}
			
			//移除超时验证码
			List uuids=records.listKeys();
			for(int i=0;i<uuids.size();i++){
				String uuid=(String)uuids.get(i);
				VerifyCodeSendRecord record=(VerifyCodeSendRecord)records.get(uuid);
				if(record==null){
					records.remove(uuid);
				}else if(record.removable()){
					records.remove(uuid);
					record=null;
				}
			}
			uuids.clear();
			uuids=null;
			//移除超时验证码  end
			
			if(configLastModified<=0) continue;

			File configFile=new File(Properties.getConfigPath()+"VerifyCode.xml");
			if(configLastModified<configFile.lastModified()){
				log.log("VerifyCode.xml has been modified, so reload it.",-1);
				load();
			}
			configFile=null;
		}
	}
}

/**
 * 
 * @author 肖炯
 *
 */
class VerifyCodeSendRecord{
	String uuid;
	String related;
	String code;
	long timeout;
	long sendTime;
	
	/**
	 * 
	 * @param uuid
	 * @param related
	 * @param code
	 * @param timeout
	 */
	VerifyCodeSendRecord(String uuid,String related,String code,long timeout){
		this.uuid=uuid;
		this.related=related;
		this.code=code;
		this.timeout=timeout;
		this.sendTime=SysUtil.getNow();
	}
	
	/**
	 * 
	 * @return
	 */
	boolean removable(){
		return (SysUtil.getNow()-sendTime)>timeout;
	}
}
