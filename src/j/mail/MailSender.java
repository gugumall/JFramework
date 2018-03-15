package j.mail;

import j.log.Logger;
import j.sys.SysConfig;
import j.util.ConcurrentList;
import j.util.JUtilString;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



/**
 * 
 * @author JFramework
 *
 */
public class MailSender implements Runnable{	
	private static Logger log=Logger.create(MailSender.class);//日志输出
	public  final static String CONTENT_HTML="text/html";
	public  final static String CONTENT_TEXT="text/plain";
	public  final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	private String id;
	private int num;
	private MailSenderConfig config;
	private ConcurrentList tasks=new ConcurrentList();
	private volatile boolean shutdown=false;
	
	/**
	 * 
	 * @param id
	 * @param num
	 * @param config
	 */
	protected MailSender(String id,int num,MailSenderConfig config){
		this.id=id;
		this.num=num;
		this.config=config;
	}
	
	public MailSenderConfig getConfig(){
		return this.config;
	}

	/**
	 * 
	 * @param to 收件人
	 * @param cc 抄送
	 * @param subj 标题
	 * @param text 正文
	 * @param type 邮件正文内容类型，text/html或text/plain
	 * @param encoding 邮件正文内容字符编码
	 * @return
	 */
	public boolean doSend(String to,String cc,String subj,String text,String type,String encoding,String fromName){
		try {
			if(to==null||to.indexOf("@")<1){
				throw new Exception("no valid recipient!");
			}
			
			String[] tos=to.split(",");
			boolean recipientsValid=true;
			for(int i=0;i<tos.length;i++){
				tos[i]=tos[i].trim();
				if(!JUtilString.isEmail(tos[i], 64)){
					recipientsValid=false;
					break;
				}
			}
			if(!recipientsValid){
				throw new Exception("there are some invalid recipients!");
			}
			
			if(cc!=null&&cc.indexOf("@")>0){
				String[] ccs=to.split(",");
				boolean ccsValid=true;
				for(int i=0;i<ccs.length;i++){
					ccs[i]=ccs[i].trim();
					if(!JUtilString.isEmail(ccs[i], 64)){
						ccsValid=false;
						break;
					}
				}
				if(!ccsValid){
					throw new Exception("there are some invalid cc recipients!");
				}
			}
			
			Properties props = new Properties();
			props.put("mail.smtp.host", config.getHost());
			props.put("mail.smtp.port", config.getPort());
			props.put("mail.smtp.auth", "true");
			
			if(config.getSecure()){
				props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
				props.setProperty("mail.smtp.socketFactory.fallback", "false");
				props.setProperty("mail.smtp.socketFactory.port",config.getPort());
			}

			Authentication auth = null;
			if(config.getAuthCode()!=null&&!"".equals(config.getAuthCode())){
				auth=new Authentication(config.getUser(),config.getAuthCode());
			}else{
				auth=new Authentication(config.getUser(),config.getPassword());
			}
			Session session = Session.getInstance(props, auth);
			session.setDebug(false);

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(config.getFrom()));
			
			String nick=fromName;
			if(nick==null) nick=config.getFromName();
			if(nick!=null&&!"".equals(nick)){
				nick=javax.mail.internet.MimeUtility.encodeText(nick);
				msg.setFrom(new InternetAddress(nick+" <"+config.getFrom()+">"));
			}
			
			InternetAddress[] toAddrs = InternetAddress.parse(to, false);
			msg.setRecipients(Message.RecipientType.TO, toAddrs);
			if (text != null){
				if(MailSender.CONTENT_HTML.equals(type)){
					if(encoding==null||encoding.equals("")) msg.setContent(text, "text/html; charset="+SysConfig.sysEncoding);
					else msg.setContent(text, "text/html; charset="+encoding);
				}else{
					if(encoding==null||encoding.equals("")) msg.setContent(text, "text/plain; charset="+SysConfig.sysEncoding);
					else msg.setContent(text, "text/plain; charset="+encoding);
				}
			}
			
			InternetAddress[] ccAddrs=null;
			if (cc != null&&cc.indexOf("@")>0) {
				ccAddrs = null;ccAddrs = InternetAddress.parse(cc, false);
				msg.setRecipients(Message.RecipientType.CC, ccAddrs);
			}
			
			if (subj != null){
				msg.setSubject(subj);
			}
			
			msg.setSentDate(new Date());

			msg.saveChanges();
			Transport.send(msg);
			
			return true;
		} catch (Exception e) {
			log.log(e, Logger.LEVEL_ERROR);
			return false;
		}
	}

	/**
	 * 
	 * @param to 收件人
	 * @param cc 抄送
	 * @param subj 标题
	 * @param text 正文
	 * @param type 邮件正文内容类型，text/html或text/plain
	 * @param encoding 邮件正文内容字符编码
	 * @param filePaths 邮件附件的文件路径[]
	 * @return
	 */
	public boolean doSend(String to, String cc, String subj, String text, String type, String encoding, String[] filePaths,String fromName) {
		try {
			if (to == null || to.indexOf("@") < 1) {
				throw new Exception("no valid recipient!");
			}

			String[] tos = to.split(",");
			boolean recipientsValid = true;
			for (int i = 0; i < tos.length; i++) {
				tos[i] = tos[i].trim();
				if(!JUtilString.isEmail(tos[i], 64)){
					recipientsValid = false;
					break;
				}
			}
			if (!recipientsValid) {
				throw new Exception("there are some invalid recipients!");
			}

			if (cc != null && cc.indexOf("@") > 0) {
				String[] ccs = to.split(",");
				boolean ccsValid = true;
				for (int i = 0; i < ccs.length; i++) {
					ccs[i] = ccs[i].trim();
					if(!JUtilString.isEmail(ccs[i], 64)){
						ccsValid = false;
						break;
					}
				}
				if (!ccsValid) {
					throw new Exception("there are some invalid cc recipients!");
				}
			}
			
			if(filePaths!=null&&filePaths.length>0){
				boolean filesExist=true;
				for(int i=0;i<filePaths.length;i++){
					File file=new File(filePaths[i]);
					if(!file.exists()||!file.isFile()){
						file=null;
						filesExist=false;
						break;
					}else{
						file=null;
					}
				}
				if (!filesExist) {
					throw new Exception("there are some files non exist or is not a file!");
				}
			}

			Properties props = new Properties();
			props.put("mail.smtp.host", config.getHost());
			props.put("mail.smtp.port", config.getPort());
			props.put("mail.smtp.auth", "true");
			
			if(config.getSecure()){
				props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
				props.setProperty("mail.smtp.socketFactory.fallback", "false");
				props.setProperty("mail.smtp.socketFactory.port",config.getPort());
			}

			Authentication auth = new Authentication(config.getUser(), config.getPassword());
			Session session = Session.getInstance(props, auth);
			session.setDebug(false);

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(config.getFrom()));
			
			String nick=fromName;
			if(nick==null) nick=config.getFromName();
			if(nick!=null&&!"".equals(nick)){
				nick=javax.mail.internet.MimeUtility.encodeText(nick);
				 msg.setFrom(new InternetAddress(nick+" <"+config.getFrom()+">"));
			}

			InternetAddress[] toAddrs = InternetAddress.parse(to, false);
			msg.setRecipients(Message.RecipientType.TO, toAddrs);

			Multipart mp = new MimeMultipart();
			
			//正文
			if (text != null) {
				MimeBodyPart mbp = new MimeBodyPart();
				if (MailSender.CONTENT_HTML.equals(type)) {
					if (encoding == null || encoding.equals(""))
						mbp.setContent(text, "text/html; charset=" + SysConfig.sysEncoding);
					else
						mbp.setContent(text, "text/html; charset=" + encoding);
				} else {
					if (encoding == null || encoding.equals(""))
						mbp.setContent(text, "text/plain; charset=" + SysConfig.sysEncoding);
					else
						mbp.setContent(text, "text/plain; charset=" + encoding);
				}
				mp.addBodyPart(mbp);
			}
			
			//附件
			if(filePaths!=null&&filePaths.length>0){
				for(int i=0;i<filePaths.length;i++){
					File file=new File(filePaths[i]);
					if(file.exists()&&file.canRead()){//如果附件存在且可读
						MimeBodyPart mbp = new MimeBodyPart();
						FileDataSource fds = new FileDataSource(file.getAbsolutePath());
						mbp.setDataHandler(new DataHandler(fds));
						mbp.setFileName(file.getName());
						mp.addBodyPart(mbp);
					}
				}
			}
			msg.setContent(mp);

			InternetAddress[] ccAddrs = null;
			if (cc != null && cc.indexOf("@") > 0) {
				ccAddrs = null;
				ccAddrs = InternetAddress.parse(cc, false);
				msg.setRecipients(Message.RecipientType.CC, ccAddrs);
			}

			if (subj != null) {
				msg.setSubject(subj);
			}
			
			msg.setSentDate(new Date());

			msg.saveChanges();
			Transport.send(msg);

			return true;
		} catch (Exception e) {
			log.log(e.getMessage(), Logger.LEVEL_ERROR);
			return false;
		}
	}
	
	/**
	 * 
	 * @param to
	 * @param cc
	 * @param subj
	 * @param text
	 * @param type
	 * @param encoding
	 * @param filePaths
	 */
	public void send(String to, String cc, String subj, String text, String type, String encoding, String[] filePaths){
		tasks.add(new SendMailTask(to, cc, subj, text, type, encoding, filePaths));
	}
	
	/**
	 * 
	 * @param to
	 * @param cc
	 * @param subj
	 * @param text
	 * @param type
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 */
	public void send(String to, String cc, String subj, String text, String type, String encoding, String[] filePaths,String fromName){
		tasks.add(new SendMailTask(to, cc, subj, text, type, encoding, filePaths,fromName));
	}
	
	/**
	 * 
	 *
	 */
	public void shutdown(){
		this.shutdown=true;
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			try{
				try{
					Thread.sleep(1000);
				}catch(Exception ex){}
				
				SendMailTask task=null;
				if(tasks.size()>0) task=(SendMailTask)tasks.get(0);
				else if(shutdown) break;
				
				if(task!=null){					
					boolean ok=false;
					if(task.filePaths==null||task.filePaths.length<1){//无附件
						ok=doSend(task.to, task.cc, task.subj, task.text, task.type, task.encoding,task.fromName);
					}else{
						ok=doSend(task.to, task.cc, task.subj, task.text, task.type, task.encoding,task.filePaths,task.fromName);
					}
					if(ok){
						log.log("mail:"+task.toString()+" has been sent by thread "+this.id+","+this.num+"!",Logger.LEVEL_DEBUG);
						tasks.remove(0);
						task=null;
					}else{
						task.failCount++;
						if(task.failCount>config.getMaxTries()){
							log.log("mail:"+task.toString()+" failed to send! "+this.id+","+this.num+"!",Logger.LEVEL_DEBUG);
							tasks.remove(0);
							task=null;
						}else{
							log.log("mail:"+task.toString()+" has not been sent, to try again! "+this.id+","+this.num+"!",Logger.LEVEL_DEBUG);
						}
					}
				}
			}catch(Exception e){
				log.log(e, Logger.LEVEL_ERROR);
				try{
					Thread.sleep(5000);
				}catch(Exception ex){}
			}
		}
	}
}

/**
 * 邮件发送任务
 * @author JFramework
 *
 */
class SendMailTask{
	String to;
	String cc;
	String subj;
	String text;
	String type;
	String encoding;
	String[] filePaths;
	int failCount=0;
	String fromName=null;
	
	/**
	 * 
	 * @param to 收件人
	 * @param cc 抄送
	 * @param subj 标题
	 * @param text 正文
	 * @param type 邮件正文内容类型，text/html或text/plain
	 * @param encoding 邮件正文内容字符编码
	 * @param filePaths 邮件附件的文件路径[]
	 */
	public SendMailTask(String to, String cc, String subj, String text, String type, String encoding, String[] filePaths){
		this.to=to;
		this.cc=cc;
		this.subj=subj;
		this.text=text;
		this.type=type;
		this.encoding=encoding;
		this.filePaths=filePaths;
	}
	
	/**
	 * 
	 * @param to
	 * @param cc
	 * @param subj
	 * @param text
	 * @param type
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 */
	public SendMailTask(String to, String cc, String subj, String text, String type, String encoding, String[] filePaths,String fromName){
		this.to=to;
		this.cc=cc;
		this.subj=subj;
		this.text=text;
		this.type=type;
		this.encoding=encoding;
		this.filePaths=filePaths;
		this.fromName=fromName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String s="to "+to+"|"+cc+"|"+subj+"|"+type+"\r\n"+text+"\r\n";
		if(filePaths!=null&&filePaths.length>0){
			for(int i=0;i<filePaths.length;i++){
				s+="["+filePaths[i]+"] ";
			}
		}
		return s;
	}
}
