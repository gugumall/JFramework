package j.jms;

import java.io.Serializable;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

/**
 * 
 * @author JFramework
 *
 */
public class ListenerDefault implements MessageListener{
	private int count=1;
	
	/*
	 *  (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	public void onMessage(Message message) {
		try {
			//System.out.println(message.getStringProperty("flag"));
			if(message instanceof ObjectMessage){
				ObjectMessage msg=(ObjectMessage) message;
				Serializable obj=msg.getObject();
				System.out.println("第"+count+"条消息："+obj.toString());
				count++;
			}else if (message instanceof TextMessage) {
				String text = ((TextMessage) message).getText();
				System.out.println("第"+count+"条消息："+text);
				count++;
			}else{
				throw new Exception("暂时不支持此消息类型："+message.getClass());
			}
		} catch (Exception ex) {
			System.out.println("onMessage err:"+ex.getMessage());
		}
	}
}
