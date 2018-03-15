package j.dao;

import j.app.webserver.JHandler;
import j.app.webserver.JSession;
import j.log.Logger;
import j.service.Constants;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author 肖炯
 *
 */
public class DataSource extends JHandler{
	private static Logger log=Logger.create(DataSource.class);
	
	/**
	 * 
	 * @param dbname
	 * @param uuid
	 * @param avail
	 */
	private static void setAvail(String dbname,String uuid,boolean avail){
		Database dbase=DB.database(dbname);
		if(dbase==null){
			return;
		}
		
		DBMirror m=dbase.mirror(uuid);
		if(m==null){
			return;
		}
		
		m.avail=avail;
		
		m.relatedXmlElement.attribute("avail").setValue(avail?"true":"false");
		
		DB.save();
	}
	
	/**
	 * 
	 * @param dbname
	 * @param uuid
	 * @param readable
	 */
	private static void setReadable(String dbname,String uuid,boolean readable){
		Database dbase=DB.database(dbname);
		if(dbase==null) return;
		
		DBMirror m=dbase.mirror(uuid);
		if(m==null) return;
		
		m.readable=readable;

		m.relatedXmlElement.attribute("readable").setValue(readable?"true":"false");
		
		DB.save();
	}
	

	
	/**
	 * 
	 * @param dbname
	 * @param uuid
	 * @param insertable
	 */
	private static void setInsertable(String dbname,String uuid,boolean insertable){
		Database dbase=DB.database(dbname);
		if(dbase==null) return;
		
		DBMirror m=dbase.mirror(uuid);
		if(m==null) return;
		
		m.insertable=insertable;
		
		m.relatedXmlElement.attribute("insertable").setValue(insertable?"true":"false");
		
		DB.save();
	}
	
	/**
	 * 
	 * @param dbname
	 * @param uuid
	 * @param updatable
	 */
	private static void setUpdatable(String dbname,String uuid,boolean updatable){
		Database dbase=DB.database(dbname);
		if(dbase==null) return;
		
		DBMirror m=dbase.mirror(uuid);
		if(m==null) return;
		
		m.updatable=updatable;
		
		m.relatedXmlElement.attribute("updatable").setValue(updatable?"true":"false");
		
		DB.save();
	}

	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void isAvail(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String dbname=request.getParameter("db_name");
		String mirrorUuid=request.getParameter("mirror_uuid");
		try{
			Database dbase=DB.database(dbname);
			if(dbase==null){
				jsession.resultString="false";
				return;
			}
			
			DBMirror m=dbase.mirror(mirrorUuid);
			if(m==null){
				jsession.resultString="false";
				return;
			}
			
			jsession.resultString=m.status==DBMirror.STATUS_UNAVAILABLE?"false":"true";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void setAvail(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String dbname=request.getParameter("db_name");
		String mirrorUuid=request.getParameter("mirror_uuid");
		String avail=request.getParameter("avail");
		try{
			setAvail(dbname,mirrorUuid,"true".equalsIgnoreCase(avail));
			jsession.resultString="1";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void setReadable(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String dbname=request.getParameter("db_name");
		String mirrorUuid=request.getParameter("mirror_uuid");
		String readable=request.getParameter("readable");
		try{
			setReadable(dbname,mirrorUuid,"true".equalsIgnoreCase(readable));
			jsession.resultString="1";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void setInsertable(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String dbname=request.getParameter("db_name");
		String mirrorUuid=request.getParameter("mirror_uuid");
		String insertable=request.getParameter("insertable");
		try{
			setInsertable(dbname,mirrorUuid,"true".equalsIgnoreCase(insertable));
			jsession.resultString="1";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}

	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws RemoteException
	 */
	public void setUpdatable(JSession jsession, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws RemoteException {
		String dbname=request.getParameter("db_name");
		String mirrorUuid=request.getParameter("mirror_uuid");
		String updatable=request.getParameter("updatable");
		try{
			setUpdatable(dbname,mirrorUuid,"true".equalsIgnoreCase(updatable));
			jsession.resultString="1";
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);			
			jsession.resultString=Constants.INVOKING_FAILED;
		}
	}
}
