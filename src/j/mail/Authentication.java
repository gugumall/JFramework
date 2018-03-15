package j.mail;

import javax.mail.PasswordAuthentication;

/**
 * 
 * @author 肖炯
 *
 */
public class Authentication extends javax.mail.Authenticator {
	private String user=null;
	private String password=null;
	
	/**
	 * 
	 * @param user
	 * @param password
	 */
	public Authentication(String user,String password){
		this.user=user;
		this.password=password;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.mail.Authenticator#getPasswordAuthentication()
	 */
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(this.user, this.password);
	}
}
