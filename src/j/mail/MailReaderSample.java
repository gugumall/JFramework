package j.mail;

import j.log.Logger;
import j.util.JUtilInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 
 * @author one
 * 
 */
public class MailReaderSample extends MailReader{
	private static Logger log=Logger.create(MailReaderSample.class);// 日志输出
	
	
	/*
	 * (non-Javadoc)
	 * @see j.mail.MailReader#parse(javax.mail.Message[], boolean)
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
			
			log.log("fromAddress:"+fromAddress,-1);
			log.log("fromName:"+fromName,-1);
			log.log("mailTitle:"+mailTitle,-1);

			save(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see j.mail.MailReader#save(javax.mail.Part)
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
}
