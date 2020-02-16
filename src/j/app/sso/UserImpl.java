package j.app.sso;

import j.app.permission.Role;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author 肖炯
 *
 */
public class UserImpl extends User{
	private static final long serialVersionUID = 1L;
	protected String userId;
	protected String subUserId;
	protected String userName;
	
	/**
	 * constructor
	 *
	 */
	public UserImpl() {
		super();
	}

	/*
	 *  (non-Javadoc)
	 * @see j.framework.sso.User#getUserId()
	 */
	public String getUserId() {
		return this.userId;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.app.sso.User#setUserId(java.lang.String)
	 */
	public void setUserId(String userId) {	
		this.userId=userId;
	}

	/*
	 * (non-Javadoc)
	 * @see j.app.sso.User#getSubUserId()
	 */
	public String getSubUserId() {
		return this.subUserId;
	}

	/*
	 * (non-Javadoc)
	 * @see j.app.sso.User#setSubUserId(java.lang.String)
	 */
	public void setSubUserId(String subUserId) {	
		this.subUserId=subUserId;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.app.sso.User#getUserName()
	 */
	public String getUserName() {
		return this.userName;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.app.sso.User#setUserName(java.lang.String)
	 */
	public void setUserName(String userName) {	
		this.userName=userName;
	}

	/*
	 *  (non-Javadoc)
	 * @see j.app.sso.User#load(javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest)
	 */
	public boolean load(HttpSession _session,HttpServletRequest _request) throws Exception {
		UserInXml user=AuthenticatorImpl.getUser(userId);
		if(user==null) return false;
		
		setUserName(user.name);
		
		for(int i=0;i<user.roles.length;i++){
			this.roles.add(Role.getInstance(user.roles[i]));
		}
		return true;
	}
}
