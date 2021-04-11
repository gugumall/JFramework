package j.app.webserver;

import j.app.Constants;
import j.app.sso.LoginStatus;
import j.app.sso.User;
import j.common.JProperties;
import j.dao.DAO;
import j.dao.DB;
import j.dao.QueryPool;
import j.dao.StmtAndRs;
import j.db.JactionLog;
import j.http.JHttp;
import j.log.Logger;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.ConcurrentMap;
import j.util.JUtilDom4j;
import j.util.JUtilJSON;
import j.util.JUtilMath;
import j.util.JUtilString;
import j.util.JUtilTimestamp;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 
 * @author 肖炯
 * 
 */
public class ActionLogger extends JHandler implements Runnable {
	private static Logger logger = Logger.create(ActionLogger.class);// 日志输出
	private String sn;
	private ConcurrentMap events = new ConcurrentMap();
	private volatile boolean shutdown = false;
	
	/**
	 * 
	 */
	public ActionLogger(){
		
	}

	/**
	 * 
	 * @param sn
	 */
	public ActionLogger(String sn) {
		this.sn = sn;
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
		String actionHandler=SysUtil.getHttpParameter(request,"action_handler");
		String actionId=SysUtil.getHttpParameter(request,"action_id");
		String t1=SysUtil.getHttpParameter(request,"t1");
		String t2=SysUtil.getHttpParameter(request,"t2");
		String eventStat=SysUtil.getHttpParameter(request,"event_stat");
		
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

		if(actionHandler!=null&&!"".equals(actionHandler)){
			sql+=" and action_handler='"+actionHandler+"'";
		}

		if(actionId!=null&&!"".equals(actionId)){
			sql+=" and action_id='"+actionId+"'";
		}
		
		if(JUtilTimestamp.isTimestamp(t1)){		
			sql+=" and event_time>='"+t1+"'";
		}
		
		if(JUtilTimestamp.isTimestamp(t2)){	
			t2=Timestamp.valueOf(t2).toString().substring(0,10);
			sql+=" and event_time<'"+(JUtilTimestamp.addToTime(Timestamp.valueOf(t2+" 00:00:00"),1))+"'";
		}

		if(eventStat!=null&&!"".equals(eventStat)){
			sql+=" and event_stat='"+eventStat+"'";
		}
			
		if(sql.startsWith(" and ")) sql=sql.substring(5);
		
		DAO dao=null;
		try{			
			dao=DB.connect(JProperties.getLogDatabase(),ActionLogger.class);
			
			List list=dao.find("j_action_log",sql+" order by event_time desc",rpp,pn);
			int total=dao.getRecordCnt("j_action_log",sql);
			
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
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public void ip(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String sql="select distinct a_u_id,a_u_ip from j_action_log where action_id='ssologin' and action_handler='/ssoserver.handler' and a_u_ip<>'127.0.0.1'";
		
		ResultSet rs=null;
		StmtAndRs sr=null; 
		try {
			Map result=new LinkedHashMap();			
			sr=QueryPool.getPool(JProperties.getLogDatabase()).query(null,sql);			
			rs=sr.resultSet();
			while(rs.next()){
				String userId=rs.getString(1);
				String userIp=rs.getString(2);
				List uids=(List)result.get(userIp);
				if(uids==null){
					uids=new LinkedList();
					result.put(userIp,uids);
				}
				if(!uids.contains(userId)) uids.add(userId);
			}
			sr.close();
			rs=null;
			sr=null;	
			
			request.setAttribute("ip",result);
		} catch (Exception ex) {
			logger.log(ex, Logger.LEVEL_ERROR);
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
		String actionHandler=SysUtil.getHttpParameter(request,"action_handler");
		String actionId=SysUtil.getHttpParameter(request,"action_id");
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

		if(actionHandler!=null&&!"".equals(actionHandler)){
			sql+=" and action_handler='"+actionHandler+"'";
		}

		if(actionId!=null&&!"".equals(actionId)){
			sql+=" and action_id='"+actionId+"'";
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
			dao=DB.connect(JProperties.getLogDatabase(),ActionLogger.class);
			dao.beginTransaction();
			
			if("T".equalsIgnoreCase(destroy)){
				if("".equals(sql)){
					dao.executeSQL("delete from j_action_log");
				}else{
					dao.executeSQL("delete from j_action_log where "+sql);
				}
			}else{
				if("".equals(sql)){
					dao.executeSQL("update j_action_log set del_by_sys='D'");
				}else{
					dao.executeSQL("update j_action_log set del_by_sys='D' where "+sql);
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
					dao.close();
					dao=null;
				}catch(Exception ex){}
			}jsession.jresponse=new JResponse(false,"ERR","I{.系统错误}");
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getSn() {
		return this.sn;
	}

	/**
	 * 
	 *
	 */
	public void shutdown() {
		this.shutdown = true;
	}

	/**
	 * 
	 * @param action
	 * @param uuid
	 * @param session
	 * @param request
	 */
	public void before(Action action, String uuid, HttpSession session,HttpServletRequest request) {
		if (action == null || action.isLogEnabled()==0) return;//对象为空或日志关闭
		if(action.isLogEnabled()==-1&&!Handlers.isLoggerOn()) return;//日志未设置且默认未开启

		User user = (User) session.getAttribute(Constants.SSO_USER);
		String userId=(user == null ? null : user.getUserId());
		if(userId==null){
			LoginStatus status = (LoginStatus) session.getAttribute(Constants.SSO_STAT_CLIENT);
			userId=(status == null ? null : status.getUserId());
		}
		if(userId==null){
			userId=SysUtil.getHttpParameter(request,Constants.SSO_USER_ID);
		}

		JactionLog log = new JactionLog();
		log.setEventId(uuid);
		log.setAsvrId(SysConfig.getMachineID());
		log.setAsysId(SysConfig.getSysId());
		log.setAdomain(JUtilString.getHost(request.getRequestURL().toString()));
		log.setAurl(request.getRequestURI());
		log.setAuIp(JHttp.getRemoteIp(request));
		log.setAuId(userId);
		log.setActionHandler(log.getAurl());
		log.setActionId(action.getId());		
		
		//保存参数
		StringBuffer ps=new StringBuffer();
		ps.append("{\"parameters\":{");
		
		int pIndex=0;
		
		if (action.isLogAllParameters()) {
			Enumeration parameters = request.getParameterNames();
			while (parameters.hasMoreElements()) {
				String parameter = (String) parameters.nextElement();
				String value=SysUtil.getHttpParameter(request, parameter);
				if(value==null) value="_IS_NULL_";
				
				if(pIndex>0) ps.append(",");
				ps.append("\""+parameter+"\":\""+JUtilJSON.convert(value)+"\"");
				pIndex++;
			}
		} else {
			List temp = action.getLogParams();
			for (int i = 0; i < temp.size(); i++) {
				String p = (String) temp.get(i);
				String value=SysUtil.getHttpParameter(request, p);
				if(value==null) value="_IS_NULL_";
				
				if(pIndex>0) ps.append(",");
				ps.append("\""+p+"\":\""+JUtilJSON.convert(value)+"\"");
				pIndex++;
			}
		}
		
		ps.append("}}");
		log.setActionParameters(ps.toString());
		ps=null;
		//保存参数 end

		log.setActionResult(null);
		log.setEventTime(new Timestamp(SysUtil.getNow()));
		log.setDelBySys("N");

		events.put(uuid, log);
	}

	/**
	 * 
	 * @param action
	 * @param uuid
	 */
	public void after(Action action,HttpSession session, String uuid) {
		if (action == null || action.isLogEnabled()==0) return;//对象为空或日志关闭
		if(action.isLogEnabled()==-1&&!Handlers.isLoggerOn()) return;//日志未设置且默认未开启

		JactionLog log = (JactionLog) events.get(uuid);
		if (log == null) return;
		
		if(log.getAuId()==null){
			User user = (User) session.getAttribute(Constants.SSO_USER);
			String userId=(user == null ? null : user.getUserId());
			if(userId==null){
				LoginStatus status = (LoginStatus) session.getAttribute(Constants.SSO_STAT_CLIENT);
				userId=(status == null ? null : status.getUserId());
			}
			log.setAuId(userId);
		}

		log.setEventStat("TRACE");
		log.setActionResult("");
	}

	/**
	 * 
	 * @param action
	 * @param uuid
	 * @param resultString
	 */
	public void after(Action action,HttpSession session, String uuid, String resultString) {
		if (action == null || action.isLogEnabled()==0) return;//对象为空或日志关闭
		if(action.isLogEnabled()==-1&&!Handlers.isLoggerOn()) return;//日志未设置且默认未开启
		
		JactionLog log = (JactionLog) events.get(uuid);
		if (log == null) return;
		
		if(log.getAuId()==null){
			User user = (User) session.getAttribute(Constants.SSO_USER);
			String userId=(user == null ? null : user.getUserId());
			if(userId==null){
				LoginStatus status = (LoginStatus) session.getAttribute(Constants.SSO_STAT_CLIENT);
				userId=(status == null ? null : status.getUserId());
			}
			log.setAuId(userId);
		}

		log.setEventStat("TRACE");
		log.setActionResult(resultString == null ? "" : resultString);
	}

	/**
	 * 
	 * @param action
	 * @param uuid
	 * @param navigateType
	 * @param navigateUrl
	 */
	public void after(Action action,HttpSession session, String uuid, String navigateType,String navigateUrl) {
		if (action == null || action.isLogEnabled()==0) return;//对象为空或日志关闭
		if(action.isLogEnabled()==-1&&!Handlers.isLoggerOn()) return;//日志未设置且默认未开启

		JactionLog log = (JactionLog) events.get(uuid);
		if (log == null) return;
		
		if(log.getAuId()==null){
			User user = (User) session.getAttribute(Constants.SSO_USER);
			String userId=(user == null ? null : user.getUserId());
			if(userId==null){
				LoginStatus status = (LoginStatus) session.getAttribute(Constants.SSO_STAT_CLIENT);
				userId=(status == null ? null : status.getUserId());
			}
			log.setAuId(userId);
		}

		log.setEventStat("TRACE");
		log.setActionResult(navigateType + " to " + navigateUrl);
	}

	/**
	 * 
	 * @param action
	 * @param uuid
	 * @param e
	 */
	public void after(Action action,HttpSession session, String uuid, Exception e) {
		if (action == null || action.isLogEnabled()==0) return;//对象为空或日志关闭
		if(action.isLogEnabled()==-1&&!Handlers.isLoggerOn()) return;//日志未设置且默认未开启

		JactionLog log = (JactionLog) events.get(uuid);
		if (log == null) return;

		if (e == null) {
			log.setActionResult("");
			return;
		}

		String ex = SysUtil.getException(e);
		
		if(log.getAuId()==null){
			User user = (User) session.getAttribute(Constants.SSO_USER);
			String userId=(user == null ? null : user.getUserId());
			if(userId==null){
				LoginStatus status = (LoginStatus) session.getAttribute(Constants.SSO_STAT_CLIENT);
				userId=(status == null ? null : status.getUserId());
			}
			log.setAuId(userId);
		}
		
		log.setEventStat("ERROR");
		log.setActionResult(ex == null ? "" : ex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true && !shutdown) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}

			if (this.events.isEmpty()) continue;

			try {
				long now = SysUtil.getNow();
				List keys = this.events.listKeys();
				for (int i = 0; i < keys.size(); i++) {
					String uuid = (String) keys.get(i);
					JactionLog log = (JactionLog) this.events.get(uuid);
					if(log==null){
						this.events.remove(uuid);
						continue;
					}

					if (log.getActionResult() != null
							|| now - log.getEventTime().getTime() > Handlers.getActionTimeout()) {
						if (log.getActionResult() == null) {
							log.setActionResult("ACTION_EXECUTION_TIMEOUT");
							log.setEventStat("ERROR");
						}

						this.events.remove(uuid);
						
						if(log.getActionResult()!=null&&log.getActionResult().length()>1024){
							//throw new Exception("action result too long to save in action log:"+log.getActionHandler()+","+log.getActionId());
							log.setActionResult(log.getActionResult().substring(0,1024));
						}
						
						QueryPool.getPool(JProperties.getLogDatabase()).insert(null,log);
						log = null;
					}
				}
				keys.clear();
				keys = null;
			} catch (Exception e) {
				logger.log(e, Logger.LEVEL_ERROR);
				try {
					Thread.sleep(5000);
				} catch (Exception ex) {
				}
			}
		}
	}
}
