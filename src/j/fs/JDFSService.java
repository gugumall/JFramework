package j.fs;

import j.app.webserver.JSession;
import j.service.server.ServiceBase;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author 肖炯
 *
 */
public interface JDFSService extends ServiceBase{
	public JFileMeta _new(String clientUuid, String md54Service,String path) throws RemoteException;
	public boolean delete(String clientUuid, String md54Service,String path) throws RemoteException;
	public String[] list(String clientUuid, String md54Service,String path) throws RemoteException;
	public boolean mkdir(String clientUuid, String md54Service,String path) throws RemoteException;
	public boolean mkdirs(String clientUuid, String md54Service,String path) throws RemoteException;
	public boolean renameTo(String clientUuid, String md54Service,String path,String dest) throws RemoteException;
	public boolean setExecutable(String clientUuid, String md54Service,String path,boolean executable,boolean ownerOnly) throws RemoteException;
	public boolean setLastModified(String clientUuid, String md54Service,String path,long time) throws RemoteException ;
	public boolean setReadable(String clientUuid, String md54Service,String path,boolean readable,boolean ownerOnly) throws RemoteException;
	public boolean setReadOnly(String clientUuid, String md54Service,String path) throws RemoteException;
	public boolean setWritable(String clientUuid, String md54Service,String path,boolean writable,boolean ownerOnly) throws RemoteException;
	

	public byte[] bytes(String clientUuid, String md54Service,String path) throws RemoteException;
	public String string(String clientUuid, String md54Service,String path,String encoding) throws RemoteException;
	public void saveString(String clientUuid, String md54Service,String path,String content, boolean append, String encoding) throws RemoteException;
	public void saveBytes(String clientUuid, String md54Service,String path,byte[] bytes) throws RemoteException;
	

	public void _new(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void delete(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void list(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void mkdir(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void mkdirs(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void renameTo(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void setExecutable(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void setLastModified(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException ;
	public void setReadable(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void setReadOnly(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void setWritable(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	

	public void bytes(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void string(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void saveString(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void saveBytes(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
	public void saveFile(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws RemoteException;
}
