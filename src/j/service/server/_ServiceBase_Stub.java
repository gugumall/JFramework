// Stub class generated by rmic, do not edit.
// Contents subject to change without notice.

package j.service.server;

import j.app.webserver.JSession;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Util;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.ServantObject;


public class _ServiceBase_Stub extends Stub implements ServiceBase {
    
    private static final String[] _type_ids = {
        "RMI:j.service.server.ServiceBase:0000000000000000"
    };
    
        public String[] _ids() { 
            return (String[]) _type_ids.clone();
        }
        
        public void setServiceConfig(ServiceConfig arg0) throws java.rmi.RemoteException {
            if (!Util.isLocal(this)) {
                try {
                    org.omg.CORBA_2_3.portable.InputStream in = null;
                    try {
                        org.omg.CORBA_2_3.portable.OutputStream out = 
                            (org.omg.CORBA_2_3.portable.OutputStream)
                            _request("_set_serviceConfig", true);
                        out.write_value(arg0,ServiceConfig.class);
                        _invoke(out);
                    } catch (ApplicationException ex) {
                        in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                        String $_id = in.read_string();
                        throw new UnexpectedException($_id);
                    } catch (RemarshalException ex) {
                        setServiceConfig(arg0);
                    } finally {
                        _releaseReply(in);
                    }
                } catch (SystemException ex) {
                    throw Util.mapSystemException(ex);
                }
            } else {
                ServantObject so = _servant_preinvoke("_set_serviceConfig",ServiceBase.class);
                if (so == null) {
                    setServiceConfig(arg0);
                    return ;
                }
                try {
                    ServiceConfig arg0Copy = (ServiceConfig) Util.copyObject(arg0,_orb());
                    ((ServiceBase)so.servant).setServiceConfig(arg0Copy);
                } catch (Throwable ex) {
                    Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                    throw Util.wrapException(exCopy);
                } finally {
                    _servant_postinvoke(so);
                }
            }
        }
        
        public ServiceConfig getServiceConfig() throws java.rmi.RemoteException {
            if (!Util.isLocal(this)) {
                try {
                    org.omg.CORBA_2_3.portable.InputStream in = null;
                    try {
                        OutputStream out = _request("_get_serviceConfig", true);
                        in = (org.omg.CORBA_2_3.portable.InputStream)_invoke(out);
                        return (ServiceConfig) in.read_value(ServiceConfig.class);
                    } catch (ApplicationException ex) {
                        in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                        String $_id = in.read_string();
                        throw new UnexpectedException($_id);
                    } catch (RemarshalException ex) {
                        return getServiceConfig();
                    } finally {
                        _releaseReply(in);
                    }
                } catch (SystemException ex) {
                    throw Util.mapSystemException(ex);
                }
            } else {
                ServantObject so = _servant_preinvoke("_get_serviceConfig",ServiceBase.class);
                if (so == null) {
                    return getServiceConfig();
                }
                try {
                    ServiceConfig result = ((ServiceBase)so.servant).getServiceConfig();
                    return (ServiceConfig)Util.copyObject(result,_orb());
                } catch (Throwable ex) {
                    Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                    throw Util.wrapException(exCopy);
                } finally {
                    _servant_postinvoke(so);
                }
            }
        }
        
        public void init() throws java.rmi.RemoteException {
            if (!Util.isLocal(this)) {
                try {
                    org.omg.CORBA.portable.InputStream in = null;
                    try {
                        OutputStream out = _request("init", true);
                        _invoke(out);
                    } catch (ApplicationException ex) {
                        in = ex.getInputStream();
                        String $_id = in.read_string();
                        throw new UnexpectedException($_id);
                    } catch (RemarshalException ex) {
                        init();
                    } finally {
                        _releaseReply(in);
                    }
                } catch (SystemException ex) {
                    throw Util.mapSystemException(ex);
                }
            } else {
                ServantObject so = _servant_preinvoke("init",ServiceBase.class);
                if (so == null) {
                    init();
                    return ;
                }
                try {
                    ((ServiceBase)so.servant).init();
                } catch (Throwable ex) {
                    Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                    throw Util.wrapException(exCopy);
                } finally {
                    _servant_postinvoke(so);
                }
            }
        }
        
        public String auth(String arg0, String arg1, String arg2) throws java.rmi.RemoteException {
            if (!Util.isLocal(this)) {
                try {
                    org.omg.CORBA_2_3.portable.InputStream in = null;
                    try {
                        org.omg.CORBA_2_3.portable.OutputStream out = 
                            (org.omg.CORBA_2_3.portable.OutputStream)
                            _request("auth__CORBA_WStringValue__CORBA_WStringValue__CORBA_WStringValue", true);
                        out.write_value(arg0,String.class);
                        out.write_value(arg1,String.class);
                        out.write_value(arg2,String.class);
                        in = (org.omg.CORBA_2_3.portable.InputStream)_invoke(out);
                        return (String) in.read_value(String.class);
                    } catch (ApplicationException ex) {
                        in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                        String $_id = in.read_string();
                        throw new UnexpectedException($_id);
                    } catch (RemarshalException ex) {
                        return auth(arg0,arg1,arg2);
                    } finally {
                        _releaseReply(in);
                    }
                } catch (SystemException ex) {
                    throw Util.mapSystemException(ex);
                }
            } else {
                ServantObject so = _servant_preinvoke("auth__CORBA_WStringValue__CORBA_WStringValue__CORBA_WStringValue",ServiceBase.class);
                if (so == null) {
                    return auth(arg0, arg1, arg2);
                }
                try {
                    return ((ServiceBase)so.servant).auth(arg0, arg1, arg2);
                } catch (Throwable ex) {
                    Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                    throw Util.wrapException(exCopy);
                } finally {
                    _servant_postinvoke(so);
                }
            }
        }
        
        public void auth(JSession arg0, HttpSession arg1, HttpServletRequest arg2, HttpServletResponse arg3) throws java.rmi.RemoteException {
            if (!Util.isLocal(this)) {
                try {
                    org.omg.CORBA_2_3.portable.InputStream in = null;
                    try {
                        org.omg.CORBA_2_3.portable.OutputStream out = 
                            (org.omg.CORBA_2_3.portable.OutputStream)
                            _request("auth__j_app_webserver_JSession__javax_servlet_http_HttpSession__javax_servlet_http_HttpServletRequest__javax_servlet_http_HttpServletResponse", true);
                        out.write_value((Serializable)arg0,JSession.class);
                        out.write_value((Serializable)arg1,HttpSession.class);
                        out.write_value((Serializable)arg2,HttpServletRequest.class);
                        out.write_value((Serializable)arg3,HttpServletResponse.class);
                        _invoke(out);
                    } catch (ApplicationException ex) {
                        in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                        String $_id = in.read_string();
                        throw new UnexpectedException($_id);
                    } catch (RemarshalException ex) {
                        auth(arg0,arg1,arg2,arg3);
                    } finally {
                        _releaseReply(in);
                    }
                } catch (SystemException ex) {
                    throw Util.mapSystemException(ex);
                }
            } else {
                ServantObject so = _servant_preinvoke("auth__j_app_webserver_JSession__javax_servlet_http_HttpSession__javax_servlet_http_HttpServletRequest__javax_servlet_http_HttpServletResponse",ServiceBase.class);
                if (so == null) {
                    auth(arg0, arg1, arg2, arg3);
                    return ;
                }
                try {
                    Object[] copies = Util.copyObjects(new Object[]{arg0,arg1,arg2,arg3},_orb());
                    JSession arg0Copy = (JSession) copies[0];
                    HttpSession arg1Copy = (HttpSession) copies[1];
                    HttpServletRequest arg2Copy = (HttpServletRequest) copies[2];
                    HttpServletResponse arg3Copy = (HttpServletResponse) copies[3];
                    ((ServiceBase)so.servant).auth(arg0Copy, arg1Copy, arg2Copy, arg3Copy);
                } catch (Throwable ex) {
                    Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                    throw Util.wrapException(exCopy);
                } finally {
                    _servant_postinvoke(so);
                }
            }
        }
        
        public String heartbeat() throws java.rmi.RemoteException {
            if (!Util.isLocal(this)) {
                try {
                    org.omg.CORBA_2_3.portable.InputStream in = null;
                    try {
                        OutputStream out = _request("heartbeat__", true);
                        in = (org.omg.CORBA_2_3.portable.InputStream)_invoke(out);
                        return (String) in.read_value(String.class);
                    } catch (ApplicationException ex) {
                        in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                        String $_id = in.read_string();
                        throw new UnexpectedException($_id);
                    } catch (RemarshalException ex) {
                        return heartbeat();
                    } finally {
                        _releaseReply(in);
                    }
                } catch (SystemException ex) {
                    throw Util.mapSystemException(ex);
                }
            } else {
                ServantObject so = _servant_preinvoke("heartbeat__",ServiceBase.class);
                if (so == null) {
                    return heartbeat();
                }
                try {
                    return ((ServiceBase)so.servant).heartbeat();
                } catch (Throwable ex) {
                    Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                    throw Util.wrapException(exCopy);
                } finally {
                    _servant_postinvoke(so);
                }
            }
        }
        
        public void heartbeat(JSession arg0, HttpSession arg1, HttpServletRequest arg2, HttpServletResponse arg3) throws java.rmi.RemoteException {
            if (!Util.isLocal(this)) {
                try {
                    org.omg.CORBA_2_3.portable.InputStream in = null;
                    try {
                        org.omg.CORBA_2_3.portable.OutputStream out = 
                            (org.omg.CORBA_2_3.portable.OutputStream)
                            _request("heartbeat__j_app_webserver_JSession__javax_servlet_http_HttpSession__javax_servlet_http_HttpServletRequest__javax_servlet_http_HttpServletResponse", true);
                        out.write_value((Serializable)arg0,JSession.class);
                        out.write_value((Serializable)arg1,HttpSession.class);
                        out.write_value((Serializable)arg2,HttpServletRequest.class);
                        out.write_value((Serializable)arg3,HttpServletResponse.class);
                        _invoke(out);
                    } catch (ApplicationException ex) {
                        in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                        String $_id = in.read_string();
                        throw new UnexpectedException($_id);
                    } catch (RemarshalException ex) {
                        heartbeat(arg0,arg1,arg2,arg3);
                    } finally {
                        _releaseReply(in);
                    }
                } catch (SystemException ex) {
                    throw Util.mapSystemException(ex);
                }
            } else {
                ServantObject so = _servant_preinvoke("heartbeat__j_app_webserver_JSession__javax_servlet_http_HttpSession__javax_servlet_http_HttpServletRequest__javax_servlet_http_HttpServletResponse",ServiceBase.class);
                if (so == null) {
                    heartbeat(arg0, arg1, arg2, arg3);
                    return ;
                }
                try {
                    Object[] copies = Util.copyObjects(new Object[]{arg0,arg1,arg2,arg3},_orb());
                    JSession arg0Copy = (JSession) copies[0];
                    HttpSession arg1Copy = (HttpSession) copies[1];
                    HttpServletRequest arg2Copy = (HttpServletRequest) copies[2];
                    HttpServletResponse arg3Copy = (HttpServletResponse) copies[3];
                    ((ServiceBase)so.servant).heartbeat(arg0Copy, arg1Copy, arg2Copy, arg3Copy);
                } catch (Throwable ex) {
                    Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                    throw Util.wrapException(exCopy);
                } finally {
                    _servant_postinvoke(so);
                }
            }
        }
    }