package j.log;

import j.Properties;
import j.app.webserver.JHandler;
import j.app.webserver.JSession;
import j.dao.DAO;
import j.dao.DB;
import j.db.Jlog;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.ConcurrentList;
import j.util.JUtilMath;
import j.util.JUtilTimestamp;
import j.util.JUtilUUID;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author JFramework
 *
 */
public class JLogger extends JHandler implements Runnable{
	private static Logger logger=Logger.create(JLogger.class);//日志输出	
	public final static String EVENT_TRACE="TRACE";
	public final static String EVENT_DEBUG="DEBUG";
	public final static String EVENT_INFO="INFO";
	public final static String EVENT_WARNING="WARNING";
	public final static String EVENT_ERROR="ERROR";
	public final static String EVENT_FATAL="FATAL";
	private static volatile int loggerSelector=0;//当前使用哪个日志处理器
	private static ConcurrentList loggers=new ConcurrentList();//日志处理器
	private String sn;
	private ConcurrentList events=new ConcurrentList();
	private volatile boolean shutdown=false;

	
	static{
		for(int i=0;i<Properties.getLoggers();i++){
			JLogger jlog=new JLogger("LOGGER_"+i);
			Thread thread=new Thread(jlog,jlog.getSn());
			thread.start();
			
			loggers.add(jlog);
			
			logger.log("Thread "+"LOGGER_"+i+" started.",-1);
		}
	}
	
	/**
	 * 
	 * @param log
	 */
	public static void log(String adomain,
			String aurl,
			String auIp,
			String auId,
			String bizCode,
			String bizId,
			String bizName,
			String bizLink,
			String bizIcon,
			String bizData,
			String eventCode,
			String eventData,
			String eventStat){
		Jlog log=new Jlog();
		log.setEventId(JUtilUUID.genUUID());
		log.setAsvrId(SysConfig.getMachineID());
		log.setAsysId(SysConfig.getSysId());
		log.setAdomain(adomain);
		log.setAurl(aurl);
		log.setAuIp(auIp);
		log.setAuId(auId);
		log.setBizCode(bizCode);
		log.setBizId(bizId);
		log.setBizName(bizName);
		log.setBizLink(bizLink);
		log.setBizIcon(bizIcon);
		log.setBizData(bizData);
		log.setEventTime(new Timestamp(SysUtil.getNow()));
		log.setEventCode(eventCode);
		log.setEventData(eventData);
		log.setEventStat(eventStat);
		log.setDelBySys("N");
		
		JLogger jlog=selectLogger();
		jlog.add(log);
	}
	
	/**
	 * 
	 * @param dao
	 * @param adomain
	 * @param aurl
	 * @param auIp
	 * @param auId
	 * @param bizCode
	 * @param bizId
	 * @param bizName
	 * @param bizLink
	 * @param bizIcon
	 * @param bizData
	 * @param eventCode
	 * @param eventData
	 * @param eventStat
	 * @throws Exception
	 */
	public static void logSyn(DAO dao,
			String adomain,
			String aurl,
			String auIp,
			String auId,
			String bizCode,
			String bizId,
			String bizName,
			String bizLink,
			String bizIcon,
			String bizData,
			String eventCode,
			String eventData,
			String eventStat)throws Exception{
		Jlog log=new Jlog();
		log.setEventId(JUtilUUID.genUUID());
		log.setAsvrId(SysConfig.getMachineID());
		log.setAsysId(SysConfig.getSysId());
		log.setAdomain(adomain);
		log.setAurl(aurl);
		log.setAuIp(auIp);
		log.setAuId(auId);
		log.setBizCode(bizCode);
		log.setBizId(bizId);
		log.setBizName(bizName);
		log.setBizLink(bizLink);
		log.setBizIcon(bizIcon);
		log.setBizData(bizData);
		log.setEventTime(new Timestamp(SysUtil.getNow()));
		log.setEventCode(eventCode);
		log.setEventData(eventData);
		log.setEventStat(eventStat);
		log.setDelBySys("N");
		
		dao.insert(log);
	}
	

	/**
	 * 
	 * @return
	 */
	private static JLogger selectLogger(){
		synchronized(loggers){
			if(loggerSelector>=loggers.size()) loggerSelector=0;
			JLogger logger=(JLogger)loggers.get(loggerSelector);
			loggerSelector++;
			return logger;
		}
	}
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void search(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String auIp=SysUtil.getHttpParameter(request,"a_u_ip");
		String auId=SysUtil.getHttpParameter(request,"a_u_id");
		String bizCode=SysUtil.getHttpParameter(request,"biz_code");
		String bizId=SysUtil.getHttpParameter(request,"biz_id");
		String t1=SysUtil.getHttpParameter(request,"t1");
		String t2=SysUtil.getHttpParameter(request,"t2");
		
		int rpp=50;
		int pn=1;
		
		String RPP=SysUtil.getHttpParameter(request,"rpp");
		if(JUtilMath.isInt(RPP)
				&&Integer.parseInt(RPP)>0
				&&Integer.parseInt(RPP)<=50){
			rpp=Integer.parseInt(RPP);
		}
		
		String PN=SysUtil.getHttpParameter(request,"pn");
		if(JUtilMath.isInt(PN)
				&&Integer.parseInt(PN)>0){
			pn=Integer.parseInt(PN);
		}
		
		String sql="del_by_sys<>'D' and a_sys_id='"+SysConfig.getSysId()+"'";
		if(auIp!=null&&!"".equals(auIp)){
			sql+=" and a_u_ip='"+auIp+"'";
		}

		if(auId!=null&&!"".equals(auId)){
			sql+=" and a_u_id='"+auId+"'";
		}

		if(bizCode!=null&&!"".equals(bizCode)){
			sql+=" and biz_code='"+bizCode+"'";
		}

		if(bizId!=null&&!"".equals(bizId)){
			sql+=" and biz_id='"+bizId+"'";
		}
		
		if(JUtilTimestamp.isTimestamp(t1)){		
			sql+=" and event_time>='"+t1+"'";
		}
		
		if(JUtilTimestamp.isTimestamp(t2)){	
			t2=Timestamp.valueOf(t2).toString().substring(0,10);
			sql+=" and event_time<'"+(JUtilTimestamp.addToTime(Timestamp.valueOf(t2+" 00:00:00"),1))+"'";
		}
		
		if(sql.startsWith(" and ")) sql=sql.substring(5);
		
		DAO dao=null;
		try{			
			dao=DB.connect(Properties.getLogDatabase(),JLogger.class);
			
			List list=dao.find("j_log",sql+" order by event_time desc",rpp,pn);
			int total=dao.getRecordCnt("j_log",sql);
			
			dao.close();
			dao=null;
			
			request.setAttribute("list",list);
			request.setAttribute("total",new Integer(total));
		}catch(Exception e){
			logger.log(e,Logger.LEVEL_ERROR);
			if(dao!=null){
				try{
					dao.close();
					dao=null;
				}catch(Exception ex){}
			}
		}
	}
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void del(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String eventId=SysUtil.getHttpParameter(request,"event_id");
		String auIp=SysUtil.getHttpParameter(request,"a_u_ip");
		String auId=SysUtil.getHttpParameter(request,"a_u_id");
		String bizCode=SysUtil.getHttpParameter(request,"biz_code");
		String bizId=SysUtil.getHttpParameter(request,"biz_id");
		String t1=SysUtil.getHttpParameter(request,"t1");
		String t2=SysUtil.getHttpParameter(request,"t2");
		String destroy=SysUtil.getHttpParameter(request,"destroy");
		
		String sql="";
		if(eventId!=null&&!"".equals(eventId)){
			sql+=" and event_id='"+eventId+"'";
		}

		if(auIp!=null&&!"".equals(auIp)){
			sql+=" and a_u_ip='"+auIp+"'";
		}

		if(auId!=null&&!"".equals(auId)){
			sql+=" and a_u_id='"+auId+"'";
		}

		if(bizCode!=null&&!"".equals(bizCode)){
			sql += " and biz_code='"+bizCode+"'";
		}

		if(bizId!=null&&!"".equals(bizId)){
			sql+=" and biz_id='"+bizId+"'";
		}
		
		if(JUtilTimestamp.isTimestamp(t1)){		
			sql+=" and event_time>='"+t1+"'";
		}
		
		if(JUtilTimestamp.isTimestamp(t2)){	
			sql+=" and event_time<='"+t2+"'";
		}
		
		if(sql.startsWith(" and ")) sql=sql.substring(5);
		
		DAO dao=null;
		try{			
			dao=DB.connect(Properties.getLogDatabase(),JLogger.class);
			
			if("T".equalsIgnoreCase(destroy)){
				if("".equals(sql)){
					dao.executeSQL("delete from j_log");
				}else{
					dao.executeSQL("delete from j_log where "+sql);
				}
			}else{
				if("".equals(sql)){
					dao.executeSQL("update j_log set del_by_sys='D'");
				}else{
					dao.executeSQL("update j_log set del_by_sys='D' where "+sql);
				}
			}
			
			dao.close();
			dao=null;
			
			jsession.resultString="1";
		}catch(Exception e){
			logger.log(e,Logger.LEVEL_ERROR);
			if(dao!=null){
				try{
					dao.close();
					dao=null;
				}catch(Exception ex){}
			}
			jsession.resultString="ERR";
		}
	}
	
	/**
	 * 
	 */
	public JLogger(){
	}
	
	/**
	 * 
	 * @param sn
	 */
	public JLogger(String sn){
		this.sn=sn;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSn(){
		return this.sn;
	}
	
	/**
	 * 
	 *
	 */
	public void shutdown(){
		this.shutdown=true;
	}
	
	/**
	 * 
	 * @param log
	 */
	public void add(Jlog log){
		events.add(log);
	}
	

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		DAO dao=null;
		while(true&&!shutdown){
			try{
				Thread.sleep(100);
			}catch(Exception e){}
			
			if(this.events.isEmpty()) continue;
			
			Jlog log=null;
			try{
				if(dao==null||dao.isClosed()){
					dao=DB.connect(Properties.getLogDatabase(),this.getClass(),3600000);
				}
				
				while(!this.events.isEmpty()){
					log=(Jlog)this.events.remove(0);
					dao.insert(log);
					
					log=null;
				}
			}catch(Exception e){
				logger.log(e,Logger.LEVEL_ERROR);
				try{
					dao.close();
				}catch(Exception ex){}
				if(log!=null) log=null;
				try{
					Thread.sleep(5000);
				}catch(Exception ex){}
			}
		}		
	}
}
