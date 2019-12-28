package j.log;

import j.app.webserver.JHandler;
import j.app.webserver.JResponse;
import j.app.webserver.JSession;
import j.common.JProperties;
import j.dao.DAO;
import j.dao.DB;
import j.dao.util.SQLUtil;
import j.db.Jlog;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.ConcurrentList;
import j.util.JUtilBean;
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
 * @author 肖炯
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
		for(int i=0;i<JProperties.getLoggers();i++){
			JLogger jlog=new JLogger("LOGGER_"+i);
			Thread thread=new Thread(jlog,jlog.getSn());
			thread.start();
			
			loggers.add(jlog);
			
			logger.log("Thread "+"LOGGER_"+i+" started.",-1);
		}
	}
	
	/**
	 * 
	 * @param adomain
	 * @param aurl
	 * @param auIp
	 * @param auId
	 * @param staffId
	 * @param sellerId
	 * @param staffIdOfShop
	 * @param bizCode
	 * @param bizId
	 * @param bizName
	 * @param bizLink
	 * @param bizIcon
	 * @param bizData
	 * @param eventCode
	 * @param eventData
	 * @param eventInfluence
	 * @param eventStat
	 * @return
	 */
	public static Jlog newLog(String adomain,
			String aurl,
			String auIp,
			String auId,
			String staffId,
			String sellerId,
			String staffIdOfShop,
			String bizCode,
			String bizId,
			String bizName,
			String bizLink,
			String bizIcon,
			String bizData,
			String eventCode,
			String eventData,
			String eventInfluence,
			String eventStat){
		return newLog(adomain,
				aurl,
				auIp,
				auId,
				staffId,
				sellerId,
				staffIdOfShop,
				bizCode,
				bizId,
				bizName,
				bizLink,
				bizIcon,
				bizData,
				eventCode,
				eventData,
				eventInfluence,
				eventStat,
				null);
	}
	
	/**
	 * 
	 * @param adomain
	 * @param aurl
	 * @param auIp
	 * @param auId
	 * @param staffId
	 * @param sellerId
	 * @param staffIdOfShop
	 * @param bizCode
	 * @param bizId
	 * @param bizName
	 * @param bizLink
	 * @param bizIcon
	 * @param bizData
	 * @param eventCode
	 * @param eventData
	 * @param eventInfluence
	 * @param eventStat
	 * @param extras
	 * @return
	 */
	public static Jlog newLog(String adomain,
			String aurl,
			String auIp,
			String auId,
			String staffId,
			String sellerId,
			String staffIdOfShop,
			String bizCode,
			String bizId,
			String bizName,
			String bizLink,
			String bizIcon,
			String bizData,
			String eventCode,
			String eventData,
			String eventInfluence,
			String eventStat,
			List<String> extras){
		Jlog log=new Jlog();
		log.setEventId(JUtilUUID.genUUID());
		log.setAsvrId(SysConfig.getMachineID());
		log.setAsysId(SysConfig.getSysId());
		log.setAdomain(adomain);
		log.setAurl(aurl);
		log.setAuIp(auIp);
		log.setAuId(auId);
		log.setStaffId(staffId);
		log.setSellerId(sellerId);
		log.setStaffIdOfShop(staffIdOfShop);
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
		log.setEventInfluence(eventInfluence);
		log.setDelBySys("N");
		
		for(int i=0; i<=10; i++) {
			String extra=null;
			if(extras!=null && extras.size()>i) extra=extras.get(i);
			try {
				JUtilBean.setPropertyValue(log, "EXTRA"+i, new Object[] {extra}, new Class[] {String.class});
			}catch(Exception e) {}
		}
		
		return log;
	}
	
	/**
	 * 
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
	 */
	public static Jlog log(String adomain,
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
		return log(adomain,
				aurl,
				auIp,
				auId,
				null,
				null,
				null,
				bizCode,
				bizId,
				bizName,
				bizLink,
				bizIcon,
				bizData,
				eventCode,
				eventData,
				null,
				eventStat);
	}
	
	/**
	 * 
	 * @param adomain
	 * @param aurl
	 * @param auIp
	 * @param auId
	 * @param staffId
	 * @param sellerId
	 * @param staffIdOfShop
	 * @param bizCode
	 * @param bizId
	 * @param bizName
	 * @param bizLink
	 * @param bizIcon
	 * @param bizData
	 * @param eventCode
	 * @param eventData
	 * @param eventInfluence
	 * @param eventStat
	 */
	public static Jlog log(String adomain,
			String aurl,
			String auIp,
			String auId,
			String staffId,
			String sellerId,
			String staffIdOfShop,
			String bizCode,
			String bizId,
			String bizName,
			String bizLink,
			String bizIcon,
			String bizData,
			String eventCode,
			String eventData,
			String eventInfluence,
			String eventStat){
		return log(adomain,
				aurl,
				auIp,
				auId,
				staffId,
				sellerId,
				staffIdOfShop,
				bizCode,
				bizId,
				bizName,
				bizLink,
				bizIcon,
				bizData,
				eventCode,
				eventData,
				eventInfluence,
				eventStat,
				null);
	}
	
	/**
	 * 
	 * @param adomain
	 * @param aurl
	 * @param auIp
	 * @param auId
	 * @param staffId
	 * @param sellerId
	 * @param staffIdOfShop
	 * @param bizCode
	 * @param bizId
	 * @param bizName
	 * @param bizLink
	 * @param bizIcon
	 * @param bizData
	 * @param eventCode
	 * @param eventData
	 * @param eventInfluence
	 * @param eventStat
	 * @param extras
	 * @return
	 */
	public static Jlog log(String adomain,
			String aurl,
			String auIp,
			String auId,
			String staffId,
			String sellerId,
			String staffIdOfShop,
			String bizCode,
			String bizId,
			String bizName,
			String bizLink,
			String bizIcon,
			String bizData,
			String eventCode,
			String eventData,
			String eventInfluence,
			String eventStat,
			List<String> extras){
		Jlog log=new Jlog();
		log.setEventId(JUtilUUID.genUUID());
		log.setAsvrId(SysConfig.getMachineID());
		log.setAsysId(SysConfig.getSysId());
		log.setAdomain(adomain);
		log.setAurl(aurl);
		log.setAuIp(auIp);
		log.setAuId(auId);
		log.setStaffId(staffId);
		log.setSellerId(sellerId);
		log.setStaffIdOfShop(staffIdOfShop);
		log.setBizCode(bizCode);
		log.setBizId(bizId);
		log.setBizName(bizName);
		log.setBizLink(bizLink);
		log.setBizIcon(bizIcon);
		log.setBizData(bizData);
		log.setEventTime(new Timestamp(SysUtil.getNow()));
		log.setEventCode(eventCode);
		log.setEventData(eventData);
		log.setEventInfluence(eventInfluence);
		log.setEventStat(eventStat);
		log.setDelBySys("N");
		
		for(int i=0; i<=10; i++) {
			String extra=null;
			if(extras!=null && extras.size()>i) extra=extras.get(i);
			try {
				JUtilBean.setPropertyValue(log, "EXTRA"+i, new Object[] {extra}, new Class[] {String.class});
			}catch(Exception e) {}
		}
		
		JLogger jlog=selectLogger();
		jlog.add(log);
		
		return log;
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
	public static Jlog logSyn(DAO dao,
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
		return logSyn(dao,
				adomain,
				aurl,
				auIp,
				auId,
				null,
				null,
				null,
				bizCode,
				bizId,
				bizName,
				bizLink,
				bizIcon,
				bizData,
				eventCode,
				eventData,
				null,
				eventStat);
	}
	
	/**
	 * 
	 * @param dao
	 * @param adomain
	 * @param aurl
	 * @param auIp
	 * @param auId
	 * @param staffId
	 * @param sellerId
	 * @param staffIdOfShop
	 * @param bizCode
	 * @param bizId
	 * @param bizName
	 * @param bizLink
	 * @param bizIcon
	 * @param bizData
	 * @param eventCode
	 * @param eventData
	 * @param eventInfluence
	 * @param eventStat
	 * @throws Exception
	 */
	public static Jlog logSyn(DAO dao,
			String adomain,
			String aurl,
			String auIp,
			String auId,
			String staffId,
			String sellerId,
			String staffIdOfShop,
			String bizCode,
			String bizId,
			String bizName,
			String bizLink,
			String bizIcon,
			String bizData,
			String eventCode,
			String eventData,
			String eventInfluence,
			String eventStat)throws Exception{
		return logSyn(dao,
				adomain,
				aurl,
				auIp,
				auId,
				staffId,
				sellerId,
				staffIdOfShop,
				bizCode,
				bizId,
				bizName,
				bizLink,
				bizIcon,
				bizData,
				eventCode,
				eventData,
				eventInfluence,
				eventStat,
				null);
	}
	
	/**
	 * 
	 * @param dao
	 * @param adomain
	 * @param aurl
	 * @param auIp
	 * @param auId
	 * @param staffId
	 * @param sellerId
	 * @param staffIdOfShop
	 * @param bizCode
	 * @param bizId
	 * @param bizName
	 * @param bizLink
	 * @param bizIcon
	 * @param bizData
	 * @param eventCode
	 * @param eventData
	 * @param eventInfluence
	 * @param eventStat
	 * @param extras
	 * @return
	 * @throws Exception
	 */
	public static Jlog logSyn(DAO dao,
			String adomain,
			String aurl,
			String auIp,
			String auId,
			String staffId,
			String sellerId,
			String staffIdOfShop,
			String bizCode,
			String bizId,
			String bizName,
			String bizLink,
			String bizIcon,
			String bizData,
			String eventCode,
			String eventData,
			String eventInfluence,
			String eventStat,
			List<String> extras)throws Exception{
		Jlog log=new Jlog();
		log.setEventId(JUtilUUID.genUUID());
		log.setAsvrId(SysConfig.getMachineID());
		log.setAsysId(SysConfig.getSysId());
		log.setAdomain(adomain);
		log.setAurl(aurl);
		log.setAuIp(auIp);
		log.setAuId(auId);
		log.setStaffId(staffId);
		log.setSellerId(sellerId);
		log.setStaffIdOfShop(staffIdOfShop);
		log.setBizCode(bizCode);
		log.setBizId(bizId);
		log.setBizName(bizName);
		log.setBizLink(bizLink);
		log.setBizIcon(bizIcon);
		log.setBizData(bizData);
		log.setEventTime(new Timestamp(SysUtil.getNow()));
		log.setEventCode(eventCode);
		log.setEventData(eventData);
		log.setEventInfluence(eventInfluence);
		log.setEventStat(eventStat);
		log.setDelBySys("N");
		
		for(int i=0; i<=10; i++) {
			String extra=null;
			if(extras!=null && extras.size()>i) extra=extras.get(i);
			try {
				JUtilBean.setPropertyValue(log, "EXTRA"+i, new Object[] {extra}, new Class[] {String.class});
			}catch(Exception e) {}
		}
		
		dao.insert(log);
		
		return log;
	}
	
	/**
	 * 
	 * @param logs
	 */
	public static void saveLogs(List logs) {
		if(logs==null||logs.isEmpty()) return;
		
		StringBuffer sqls=new StringBuffer();
		sqls.append("INSERT INTO j_log VALUES ");
		for(int i=0; i<logs.size(); i++) {
			Jlog log=(Jlog)logs.get(i);
			
			if(i>0) sqls.append(",");
			sqls.append("(");
			sqls.append("'"+log.getEventId()+"'");//uuid
		
			if(log.getAsvrId()==null) sqls.append(",null");//svrId
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getAsvrId())+"'");
		
			if(log.getAsysId()==null) sqls.append(",null");//sysId
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getAsysId())+"'");
		
			if(log.getAdomain()==null) sqls.append(",null");//adomain
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getAdomain())+"'");
			
			if(log.getAurl()==null) sqls.append(",null");//aurl
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getAurl())+"'");
			
			if(log.getAuIp()==null) sqls.append(",null");//auip
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getAuIp())+"'");
			
			if(log.getAuId()==null) sqls.append(",null");//auid
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getAuId())+"'");
			
			if(log.getStaffId()==null) sqls.append(",null");//staffId
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getStaffId())+"'");
			
			if(log.getSellerId()==null) sqls.append(",null");//sellerId
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getSellerId())+"'");//sellerId
			
			if(log.getStaffIdOfShop()==null) sqls.append(",null");//staffIdOfShop
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getStaffIdOfShop())+"'");
			
			if(log.getBizCode()==null) sqls.append(",null");//bizCode
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getBizCode())+"'");
			
			if(log.getBizId()==null) sqls.append(",null");//bizId
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getBizId())+"'");
			
			if(log.getBizName()==null) sqls.append(",null");//bizName
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getBizName())+"'");
			
			if(log.getBizLink()==null) sqls.append(",null");//bizLink
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getBizLink())+"'");
			
			if(log.getBizIcon()==null) sqls.append(",null");//bizIcon
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getBizIcon())+"'");
			
			if(log.getBizData()==null) sqls.append(",null");//bizData
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getBizData())+"'");
			
			if(log.getEventTime()==null) sqls.append(",null");//eventTime
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getEventTime().toString().substring(0,19))+"'");
			
			if(log.getEventCode()==null) sqls.append(",null");//eventCode
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getEventCode())+"'");
			
			if(log.getEventData()==null) sqls.append(",null");//eventData
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getEventData())+"'");
			
			if(log.getEventInfluence()==null) sqls.append(",null");//eventInfluence
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getEventInfluence())+"'");
			
			if(log.getEventStat()==null) sqls.append(",null");//eventStat
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getEventStat())+"'");
			
			if(log.getDelBySys()==null) sqls.append(",null");//delBySys
			else sqls.append(",'"+SQLUtil.deleteCriminalChars(log.getDelBySys())+"'");
			
			for(int j=0; j<=10; j++) {
				String extra=null;
				try {
					extra=(String)JUtilBean.getPropertyValue(log, "EXTRA"+i);
				}catch(Exception e) {}
				if(extra==null) sqls.append(",null");//extra
				else sqls.append(",'"+SQLUtil.deleteCriminalChars(extra)+"'");
			}
			
			sqls.append(")");
		}
		
		//System.out.println(sqls.toString());
		
		JLogger jlog=selectLogger();
		jlog.add(sqls.toString());
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
		String staffId=SysUtil.getHttpParameter(request,"staff_id");
		String sellerId=SysUtil.getHttpParameter(request,"seller_id");
		String staffIdOfShop=SysUtil.getHttpParameter(request,"staff_id_of_shop");
		
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

		if(staffId!=null&&!"".equals(staffId)){
			sql+=" and staff_id='"+SQLUtil.deleteCriminalChars(staffId)+"'";
		}

		if(sellerId!=null&&!"".equals(sellerId)){
			sql+=" and seller_id='"+SQLUtil.deleteCriminalChars(sellerId)+"'";
		}

		if(staffIdOfShop!=null&&!"".equals(staffIdOfShop)){
			sql+=" and staff_id_of_shop='"+SQLUtil.deleteCriminalChars(staffIdOfShop)+"'";
		}
		
		for(int i=0;i<=10; i++) {
			String extra=SysUtil.getHttpParameter(request,"extra"+i);

			if(extra!=null&&!"".equals(extra)){
				sql+=" and extra"+i+"='"+SQLUtil.deleteCriminalChars(extra)+"'";
			}
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
			dao=DB.connect(JProperties.getLogDatabase(),JLogger.class);
			
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
			dao=DB.connect(JProperties.getLogDatabase(),JLogger.class);
			dao.beginTransaction();
			
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
			
			dao.commit();
			dao.close();
			dao=null;
			
			jsession.jresponse=new JResponse(true,"1","I{.删除成功}");
		}catch(Exception e){
			logger.log(e,Logger.LEVEL_ERROR);
			if(dao!=null){
				try{
					dao.rollback();
				}catch(Exception ex){}
				try{
					dao.close();
					dao=null;
				}catch(Exception ex){}
			}
			jsession.jresponse=new JResponse(false,"ERR","I{.系统错误}");
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
	
	/**
	 * 
	 * @param batchSql
	 */
	public void add(String batchSql){
		events.add(batchSql);
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
			
			Object log=null;
			try{
				if(dao==null||dao.isClosed()){
					dao=DB.connect(JProperties.getLogDatabase(),this.getClass(),3600000);
				}
				
				while(!this.events.isEmpty()){
					log=this.events.remove(0);
					if(log instanceof String) dao.executeSQL((String)log);
					else dao.insert(log);
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
