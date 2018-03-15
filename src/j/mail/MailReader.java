package j.mail;

import j.log.Logger;
import j.util.JUtilInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

/**
 * 
 * @author one
 * 
 */
public class MailReader{
	private static Logger log=Logger.create(MailReader.class);// 日志输出
	private String senderId=null;
	private Session session=null;
	private IMAPStore store=null;
	private IMAPFolder folder=null;

	/**
	 * 
	 */
	public MailReader(){
	}
	
	/**
	 * 
	 * @param senderId
	 */
	public MailReader(String senderId){
		this.senderId=senderId;
	}
	
	/**
	 * 
	 * @param senderId
	 */
	public void setSenderId(String senderId){
		this.senderId=senderId;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSenderId(){
		return this.senderId;
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public boolean connect(){
		try{
			MailSenderConfig config=MailManager.getConfig(senderId);
	
			Properties props=new Properties();
			props.put("mail.store.protocol",config.getReaderProtocol());
			props.put("mail.imap.host",config.getReaderHost());
			props.put("mail.imap.port",config.getReaderPort());
			if(config.getSecure()){
				props.setProperty("mail.imap.socketFactory.class", MailSender.SSL_FACTORY);
				props.setProperty("mail.imap.socketFactory.fallback", "false");
				props.setProperty("mail.imap.socketFactory.port",config.getReaderPort());
			}
	
			session=Session.getInstance(props);
			store=(IMAPStore)session.getStore(config.getReaderProtocol());// 使用imap会话机制，连接服务器
			
			if(config.getAuthCode()!=null&&!"".equals(config.getAuthCode())){
				store.connect(config.getUser(),config.getAuthCode());
			}else{
				store.connect(config.getUser(),config.getPassword());
			}
			
			try{
				Map client=new HashMap();
				client.put("name",config.getReaderName());
				client.put("version",config.getReaderVersion());
				store.id(client);
			}catch(Exception ex){}
			
			return true;
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return false;
		}
	}
	
	/**
	 * 
	 */
	public void close(){
		if(folder!=null){
			try{
				folder.close(true);
			}catch(Exception e){}
		}
		
		if(store!=null){
			try{
				store.close();
			}catch(Exception e){}
		}
	}

	/**
	 * 
	 * @return
	 */
	public Folder[] getFolders(){
		try{
			Folder _default=store.getDefaultFolder();
			return _default.list();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			return null;
		}
	}

	/**
	 * 
	 * @param box
	 * @param count 读取条数
	 * @param read 是否标记为已读
	 */
	public void read(String box,int count,boolean read){
		try{
			if(box==null){
				Folder[] folders=getFolders();
				
//				for(int i=0;i<folders.length;i++){
//					System.out.println(folders[i].getName());
//				}
				
				if(folders!=null&&folders.length>0) box=folders[0].getName();
				else box="INBOX";
			}
			
			IMAPFolder folder=(IMAPFolder)store.getFolder(box);// 收件箱
			folder.open(Folder.READ_WRITE);

			// 获取邮件
			int start=folder.getMessageCount()-count+1;
			if(start<1) start=1;
			
			Message[] messages=folder.getMessages(start,folder.getMessageCount());

			// 解析邮件
			parse(messages,read);
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
		}
	}

	/**
	 * 
	 * @param messages
	 * @param read 是否标记为已读
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void parse(Message[] messages,boolean read) throws MessagingException,IOException{
		if(messages==null||messages.length<1){
			return;
		}

		// 解析所有邮件
		for(int i=0,count=messages.length;i<count;i++){
			MimeMessage msg=(MimeMessage)messages[i];
			if(read){
				msg.setFlag(Flags.Flag.SEEN,true);
			}
			
			InternetAddress address=getFrom(msg);//发件人
			
			String fromAddress=address.getAddress();
			String fromName=address.getPersonal();
			String mailTitle=decodeText(msg.getSubject());
			String receivers=getReceiveAddress(msg,null);
			Date receiveDate=msg.getSentDate();
			String contentType=msg.getContentType();
			
			log.log("fromAddress:"+fromAddress,-1);
			log.log("fromName:"+fromName,-1);
			log.log("mailTitle:"+mailTitle,-1);
			log.log("receivers:"+receivers,-1);
			log.log("receiveDate:"+receiveDate,-1);
			log.log("contentType:"+contentType,-1);

			save(msg);
		}
	}

	/**
	 * 保存附件
	 * 
	 * @param part 邮件中多个组合体中的其中一个组合体
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void save(Part part) throws UnsupportedEncodingException,MessagingException,FileNotFoundException,IOException{
		if(part.isMimeType("text/*")){
			String encoding="UTF-8";
			
			String contentType=part.getContentType().toLowerCase();
			if(contentType.indexOf("charset=")>0){
				encoding=contentType.substring(contentType.indexOf("charset=")+8);
			}
		
			String text=JUtilInputStream.string(part.getInputStream(),encoding);
			
			log.log("mail text:"+text,-1);
		}else if(part.isMimeType("multipart/*")){
			Multipart multipart=(Multipart)part.getContent();
			
			// 复杂体邮件包含多个邮件体
			int partCount=multipart.getCount();
			for(int i=0;i<partCount;i++){
				// 获得复杂体邮件中其中一个邮件体
				BodyPart bodyPart=multipart.getBodyPart(i);
				
				// 某一个邮件体也有可能是由多个邮件体组成的复杂体
				String disp=bodyPart.getDisposition();
				if(disp!=null&&(disp.equalsIgnoreCase(Part.ATTACHMENT)||disp.equalsIgnoreCase(Part.INLINE))){
					//JDFSFile.saveStream(bodyPart.getInputStream(),"path");
				}else{
					save(bodyPart);
				}
			}
		}else if(part.isMimeType("message/rfc822")){
			save((Part)part.getContent());
		}
	}

	/**
	 * 获得邮件发件人
	 * 
	 * @param msg 邮件内容
	 * @return 地址
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public static InternetAddress getFrom(MimeMessage msg) throws MessagingException,UnsupportedEncodingException{
		Address[] froms=msg.getFrom();
		if(froms.length<1){
			return null;
		}

		return (InternetAddress)froms[0];
	}

	/**
	 * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
	 * <p>
	 * Message.RecipientType.TO 收件人
	 * </p>
	 * <p>
	 * Message.RecipientType.CC 抄送
	 * </p>
	 * <p>
	 * Message.RecipientType.BCC 密送
	 * </p>
	 * 
	 * @param msg 邮件内容
	 * @param type  收件人类型
	 * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
	 * @throws MessagingException
	 */
	public static String getReceiveAddress(MimeMessage msg,Message.RecipientType type) throws MessagingException{
		StringBuffer receiveAddress=new StringBuffer();
		Address[] addresss=null;
		if(type==null){
			addresss=msg.getAllRecipients();
		}else{
			addresss=msg.getRecipients(type);
		}

		if(addresss==null||addresss.length<1){
			return "";
		}
		
		for(Address address:addresss){
			InternetAddress internetAddress=(InternetAddress)address;
			receiveAddress.append(internetAddress.toUnicodeString()).append(",");
		}

		receiveAddress.deleteCharAt(receiveAddress.length()-1); // 删除最后一个逗号

		return receiveAddress.toString();
	}


	/**
	 * 文本解码
	 * 
	 * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
	 * @return 解码后的文本
	 * @throws UnsupportedEncodingException
	 */
	public String decodeText(String encodeText) throws UnsupportedEncodingException{
		if(encodeText==null||"".equals(encodeText)){
			return "";
		}else{
			return MimeUtility.decodeText(encodeText);
		}
	}
}
