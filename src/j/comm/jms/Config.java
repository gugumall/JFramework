package j.comm.jms;

import j.sys.Initializer;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 
 * 基于RFrame配置的jms一对一，一对多消息传输
 * 
 * @author 肖炯
 *
 */
public class Config implements Initializer{	
	/*
	 * jms提供商实现jms规范的版本
	 */
	public static String jmsVersion="1.0";
	
	/*
	 * 发送端
	 */
	public static List senders=new LinkedList();
	
	/*
	 * 接收端
	 */
	public static List receivers=new LinkedList();
	
	/**
	 * 发送端信息
	 * @param ID
	 * @return Parameters
	 */
	public static Parameters getParamaters(String ID){
		if(ID==null||ID.trim().equals("")){
			return null;
		}
		for(int i=0;i<Config.senders.size();i++){
			Parameters sender=(Parameters)Config.senders.get(i);
			if(sender.ID.equals(ID)){
				return sender;
			}
		}
		return null;
	}
	
	/**
	 * 读取发送端、接收断配置信息
	 * @throws Exception
	 */
	public static void load(File file)throws Exception{
		senders.clear();
		receivers.clear();

        if(!file.exists()){
        	throw new Exception("找不到配置文件："+file.getAbsolutePath());
        }
		//create jdom document
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new FileInputStream(file),"UTF-8");
		Element root = doc.getRootElement();
        //create jdom document ends
		
		/*
		 * 版本信息等
		 */
		Element jmsElement=root.element("jms");
		Config.jmsVersion=jmsElement.attributeValue("version");
		
		/*
		 * 接收端
		 */
		Element receiversElement=root.element("receivers");
		List receiverElements=receiversElement.elements("receiver");
		for(int i=0;receiverElements!=null&&i<receiverElements.size();i++){
			Element receiverElement=(Element)receiverElements.get(i);
			
			Parameters receiver=new Parameters();
			receiver.ID=receiverElement.attributeValue("ID");
			receiver.NAME=receiverElement.attributeValue("NAME");
			receiver.INITIAL_CONTEXT_FACTORY=receiverElement.elementText("INITIAL_CONTEXT_FACTORY");
			receiver.PROVIDER_URL=receiverElement.elementText("PROVIDER_URL");
			receiver.CONNECTION_FACTORY=receiverElement.elementText("CONNECTION_FACTORY");
			receiver.DESTINATION=receiverElement.elementText("DESTINATION");			
			receiver.CONSUMER=receiverElement.elementText("CONSUMER");
			receiver.LISTENER=receiverElement.elementText("LISTENER");
			
			String type=receiverElement.elementText("TYPE");
			if(Config.jmsVersion!=null&&Config.jmsVersion.equals("1.1")){
				receiver.TYPE=Parameters.TYPE_PC;
			}else{
				if(type==null||type.equals("queue")){
					receiver.TYPE=Parameters.TYPE_PP;
				}else if(type.equals("topic")){
					receiver.TYPE=Parameters.TYPE_PS;
				}
			}	
			
			receiver.DURABLE="true".equalsIgnoreCase(receiverElement.elementText("DURABLE"))?2:1;
			
			Config.receivers.add(receiver);
		}
		
		/*
		 * 发送端
		 */
		Element sendersElement=root.element("senders");
		List senderElements=sendersElement.elements("sender");
		for(int i=0;senderElements!=null&&i<senderElements.size();i++){
			Element senderElement=(Element)senderElements.get(i);
			
			Parameters sender=new Parameters();
			sender.ID=senderElement.attributeValue("ID");
			sender.NAME=senderElement.attributeValue("NAME");
			sender.INITIAL_CONTEXT_FACTORY=senderElement.elementText("INITIAL_CONTEXT_FACTORY");
			sender.PROVIDER_URL=senderElement.elementText("PROVIDER_URL");
			sender.CONNECTION_FACTORY=senderElement.elementText("CONNECTION_FACTORY");
			sender.DESTINATION=senderElement.elementText("DESTINATION");
			
			String type=senderElement.elementText("TYPE");
			if(Config.jmsVersion!=null&&Config.jmsVersion.equals("1.1")){
				sender.TYPE=Parameters.TYPE_PC;
			}else{
				if(type==null||type.equals("queue")){
					sender.TYPE=Parameters.TYPE_PP;
				}else if(type.equals("topic")){
					sender.TYPE=Parameters.TYPE_PS;
				}
			}					
			
			sender.DURABLE="true".equalsIgnoreCase(senderElement.elementText("DURABLE"))?2:1;
			
			Config.senders.add(sender);
		}
	}
	
	/**
	 * 启动各监听端
	 *
	 */
	public static void startup()throws Exception{
		for(int i=0;i<Config.receivers.size();i++){
			Parameters receiver=(Parameters)Config.receivers.get(i);			
			new Receiver(receiver);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.framework.sys.Initializer#initialization()
	 */
	public void initialization()throws Exception{
		String config_path = j.Properties.getConfigPath()+"jms.xml";
		File file = new File(config_path);
		load(file);
		startup();
	}
}
