package j.jms;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;

import j.log.Logger;

/**
 * 
 * @author JFramework
 *
 */
public class Sender{	
	private static Logger log=Logger.create(Sender.class);//日志输出
	
	protected Map properties;//JMS配置信息
	protected Context context;//上下文
	
	/*
	 * 通用接口（jms1.1规范）
	 */
	protected boolean init=false;
	protected ConnectionFactory connectionFactory;
	protected Connection connection;
	protected Session session;
	protected Destination destination;
	protected MessageProducer producer;
	
	/*
	 * 一对一
	 */
	protected boolean ppInit=false;
	protected QueueConnectionFactory queueConnectionFacotory;
	protected QueueConnection queueConnection;
	protected QueueSession queueSession;
	protected Queue queue;
	protected QueueSender queueSender;
	
	/*
	 * 一对多（发布-订阅）
	 */
	protected boolean psInit=false;
	protected TopicConnectionFactory topicConnectionFacotory;
	protected TopicConnection topicConnection;
	protected TopicSession topicSession;
	protected Topic topic;
	protected TopicPublisher publisher;

	protected String id="";
	protected String name="";
	protected int type=0;//发送类型 2，jms1.1规范，不区分queue和topic；0，queue；1，topic
	protected int durable=1;//消息是否持久化 1,NON_PERSISTENT   2,PERSISTENT
	
	
	/**
	 * 发送文本消息
	 * @param senderID 发送端id
	 * @param txt 消息文本
	 * @param propsMap 属性值map
	 * @throws Exception
	 */
	public static Sender sendTxt(String senderID,String txt,Map propsMap)throws Exception{
		Parameters sender=Config.getParamaters(senderID);
		if(sender==null){
			throw new Exception("找不到指定的发送端："+senderID);
		}

		Sender realSender=new Sender(sender);
		realSender.sendTxt(txt,propsMap);
		
		return realSender;
	}
	
	/**
	 * 发送文本消息
	 * @param senderID 发送端id
	 * @param txt 消息文本
	 * @param propsMap 属性值map
	 * @param secondsAlive 消息存活时间
	 * @throws Exception
	 */
	public static Sender sendTxt(String senderID,String txt,Map propsMap,long secondsAlive)throws Exception{
		Parameters sender=Config.getParamaters(senderID);
		if(sender==null){
			throw new Exception("找不到指定的发送端："+senderID);
		}

		Sender realSender=new Sender(sender);
		realSender.sendTxt(txt,propsMap,secondsAlive);
		
		return realSender;
	}
	
	/**
	 * 发送对象消息
	 * @param senderID 发送端id
	 * @param obj 可序列化对象
	 * @param propsMap 属性值map
	 * @throws Exception
	 */
	public static Sender sendObj(String senderID,Serializable obj,Map propsMap)throws Exception{
		Parameters sender=Config.getParamaters(senderID);
		if(sender==null){
			throw new Exception("找不到指定的发送端："+senderID);
		}

		Sender realSender=new Sender(sender);
		realSender.sendObj(obj,propsMap);
		
		return realSender;
	}
	
	/**
	 * 
	 * 发送对象消息
	 * @param senderID 发送端id
	 * @param obj 可序列化对象
	 * @param propsMap 属性值map
	 * @param secondsAlive 消息存活时间
	 * @throws Exception
	 */
	public static Sender sendObj(String senderID,Serializable obj,Map propsMap,long secondsAlive)throws Exception{
		Parameters sender=Config.getParamaters(senderID);
		if(sender==null){
			throw new Exception("找不到指定的发送端："+senderID);
		}

		Sender realSender=new Sender(sender);
		realSender.sendObj(obj,propsMap,secondsAlive);
		
		return realSender;
	}
	
	/**
	 * 读取configMap中的key-value配置信息并创建context
	 * @param configMap
	 * @throws Exception
	 */
	public Sender(Parameters  params) throws Exception {
		properties=new HashMap();		
		properties.put("INITIAL_CONTEXT_FACTORY",params.INITIAL_CONTEXT_FACTORY);
		properties.put("PROVIDER_URL",params.PROVIDER_URL);
		properties.put("CONNECTION_FACTORY",params.CONNECTION_FACTORY);
		properties.put("DESTINATION",params.DESTINATION);
		this.id=params.ID;
		this.name=params.NAME;
		this.durable=params.DURABLE;
		this.type=params.TYPE;
		createContext();
	}
	
	/**
	 * 创建context
	 * @param initFact
	 * @param providerUrl
	 * @return Context
	 * @throws Exception
	 */
	private void createContext()throws Exception{
		Hashtable properties = new Hashtable();
		
		properties.put(Context.INITIAL_CONTEXT_FACTORY,this.getProperty("INITIAL_CONTEXT_FACTORY"));//初始化工厂
				
		properties.put(Context.PROVIDER_URL,this.getProperty("PROVIDER_URL"));//提供商URL
		
		context =new InitialContext(properties);
	}
	
	/**
	 * 初始化点对点
	 * @throws Exception
	 */
	private void initP2P()throws Exception{
		/*
		 * 初始化连接工厂
		 */
		queueConnectionFacotory = (QueueConnectionFactory) context.lookup(getProperty("CONNECTION_FACTORY"));
		queueConnection = queueConnectionFacotory.createQueueConnection();
		queueConnection.start();
		
		/*
		 * 创建会话
		 */
		queueSession=queueConnection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
		
		/*
		 * 得到队列
		 */
		queue=(Queue) context.lookup(getProperty("DESTINATION"));		

		/*
		 * 发送者
		 */
		queueSender = queueSession.createSender(queue);
		queueSender.setDeliveryMode(durable);
		
		this.ppInit=true;
	}
	
	/**
	 * 初始化发布订阅
	 * @throws Exception
	 */
	private void initP2S()throws Exception{
		/*
		 * 初始化连接工厂
		 */
		topicConnectionFacotory = (TopicConnectionFactory) context.lookup(getProperty("CONNECTION_FACTORY"));
		topicConnection = topicConnectionFacotory.createTopicConnection();
		topicConnection.start();
		
		/*
		 * 创建会话
		 */
		topicSession=topicConnection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
		
		/*
		 * 得到主题
		 */
		topic=(Topic) context.lookup(getProperty("DESTINATION"));
		

		/*
		 * 发送者
		 */
		publisher = topicSession.createPublisher(topic);
		publisher.setDeliveryMode(durable);
		
		this.psInit=true;
	}
	
	/**
	 * 初始化producer-consumer
	 * @throws Exception
	 */
	private void initP2C()throws Exception{
		/*
		 * 初始化连接工厂
		 */
		connectionFactory = (ConnectionFactory) context.lookup(getProperty("CONNECTION_FACTORY"));
		connection = connectionFactory.createConnection();
		connection.start();
		
		/*
		 * 创建会话
		 */
		session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		
		
		
		/*
		 * 得到目的
		 */
		destination=(Destination) context.lookup(getProperty("DESTINATION"));
		
		/*
		 * 发送者
		 */
		producer = session.createProducer(destination);
		producer.setDeliveryMode(durable);
		
		this.init=true;
	}

	

	
	/**
	 * 发送文本消息
	 * @param txt 消息文本
	 * @param propsMap 属性值map
	 */
	public void sendTxt(String txt,Map propsMap){
		if(this.type==Parameters.TYPE_PC) this.sendTxtP2C(txt,propsMap);
		if(this.type==Parameters.TYPE_PP) this.sendTxtP2P(txt,propsMap);
		if(this.type==Parameters.TYPE_PS) this.sendTxtP2S(txt,propsMap);
	}
	
	/**
	 * 发送文本消息
	 * @param txt 消息文本
	 * @param propsMap 属性值map
	 * @param secondsAlive 消息存活时间
	 */
	public void sendTxt(String txt,Map propsMap,long secondsAlive){
		if(this.type==Parameters.TYPE_PC) this.sendTxtP2C(txt,propsMap,secondsAlive);
		if(this.type==Parameters.TYPE_PP) this.sendTxtP2P(txt,propsMap,secondsAlive);
		if(this.type==Parameters.TYPE_PS) this.sendTxtP2S(txt,propsMap,secondsAlive);
	}
	
	/**
	 * 发送对象消息
	 * @param obj 可序列化对象
	 * @param propsMap 属性值map
	 */
	public void sendObj(Serializable obj,Map propsMap){
		if(this.type==Parameters.TYPE_PC) this.sendObjP2C(obj,propsMap);
		if(this.type==Parameters.TYPE_PP) this.sendObjP2P(obj,propsMap);
		if(this.type==Parameters.TYPE_PS) this.sendObjP2S(obj,propsMap);
	}
	
	/**
	 * 发送对象消息
	 * @param obj 可序列化对象
	 * @param propsMap 属性值map
	 * @param secondsAlive 消息存活时间
	 */
	public void sendObj(Serializable obj,Map propsMap,long secondsAlive){
		if(this.type==Parameters.TYPE_PC) this.sendObjP2C(obj,propsMap,secondsAlive);
		if(this.type==Parameters.TYPE_PP) this.sendObjP2P(obj,propsMap,secondsAlive);
		if(this.type==Parameters.TYPE_PS) this.sendObjP2S(obj,propsMap,secondsAlive);
	}

	
	/**
	 * 发送文本消息(点对点)
	 * @param txt 消息文本
	 * @param propsMap 属性值map
	 */
	private void sendTxtP2P(String txt,Map propsMap){
		try {
			if(!ppInit){
				initP2P();
			}
			TextMessage msg=queueSession.createTextMessage();
			msg.setText(txt);
			this.addProperties(msg,propsMap);
			queueSender.send(msg);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			this.close();
		}
	}
	
	/**
	 * 发送文本消息(点对点)
	 * @param txt 消息文本
	 * @param propsMap 属性值map
	 * @param secondsAlive 消息存活时间
	 */
	private void sendTxtP2P(String txt,Map propsMap,long secondsAlive){
		try {
			if(!ppInit){
				initP2P();
			}
			TextMessage msg=queueSession.createTextMessage();
			msg.setText(txt);
			this.addProperties(msg,propsMap);
			queueSender.setTimeToLive(secondsAlive*1000);	
			queueSender.send(msg);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			this.close();
		}
	}
	
	/**
	 * 发送文本消息(点对点)
	 * @param obj 可序列化对象
	 * @param propsMap 属性值map
	 */
	private void sendObjP2P(Serializable obj,Map propsMap){
		try {
			if(!ppInit){
				initP2P();
			}
			ObjectMessage msg=queueSession.createObjectMessage();
			msg.setObject(obj);
			this.addProperties(msg,propsMap);
			queueSender.send(msg);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			this.close();
		}
	}
	
	/**
	 * 发送文本消息(点对点)
	 * @param obj 可序列化对象
	 * @param propsMap 属性值map
	 * @param secondsAlive 消息存活时间
	 */
	private void sendObjP2P(Serializable obj,Map propsMap,long secondsAlive){
		try {
			if(!ppInit){
				initP2P();
			}
			ObjectMessage msg=queueSession.createObjectMessage();
			msg.setObject(obj);
			this.addProperties(msg,propsMap);
			queueSender.setTimeToLive(secondsAlive*1000);	
			queueSender.send(msg);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			this.close();
		}
	}
	
	/**
	 * 发送文本消息(发布-订阅)
	 * @param txt 消息文本
	 * @param propsMap 属性值map
	 */
	private void sendTxtP2S(String txt,Map propsMap){
		try {
			if(!psInit){
				initP2S();
			}
			TextMessage msg=topicSession.createTextMessage();
			msg.setText(txt);
			this.addProperties(msg,propsMap);
			publisher.publish(msg);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			this.close();
		}
	}
	
	/**
	 * 发送文本消息(发布-订阅)
	 * @param txt 消息文本
	 * @param propsMap 属性值map
	 * @param secondsAlive 消息存活时间
	 */
	private void sendTxtP2S(String txt,Map propsMap,long secondsAlive){
		try {
			if(!psInit){
				initP2S();
			}
			TextMessage msg=topicSession.createTextMessage();
			msg.setText(txt);
			this.addProperties(msg,propsMap);
			publisher.setTimeToLive(secondsAlive*1000);
			publisher.publish(msg);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			this.close();
		}
	}
	
	/**
	 * 发送对象消息(发布-订阅)
	 * @param obj 可序列化对象
	 * @param propsMap 属性值map
	 */
	private void sendObjP2S(Serializable obj,Map propsMap){
		try {
			if(!psInit){
				initP2S();
			}
			ObjectMessage msg=topicSession.createObjectMessage();
			msg.setObject(obj);
			this.addProperties(msg,propsMap);
			publisher.publish(msg);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			this.close();}
	}
	
	/**
	 * 发送对象消息(发布-订阅)
	 * @param obj 可序列化对象
	 * @param propsMap 属性值map
	 * @param secondsAlive 消息存活时间
	 */
	private void sendObjP2S(Serializable obj,Map propsMap,long secondsAlive){
		try {
			if(!psInit){
				initP2S();
			}
			ObjectMessage msg=topicSession.createObjectMessage();
			msg.setObject(obj);
			this.addProperties(msg,propsMap);
			publisher.setTimeToLive(secondsAlive*1000);
			publisher.publish(msg);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			this.close();
		}
	}
	
	/**
	 * 发送文本消息(jms1.1规范,producer-consumer)
	 * @param txt 消息文本
	 * @param propsMap 属性值map
	 */
	private void sendTxtP2C(String txt,Map propsMap){
		try {
			if(!init){
				initP2C();
			}
			TextMessage msg=session.createTextMessage();
			msg.setText(txt);
			this.addProperties(msg,propsMap);
			producer.send(msg);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			this.close();
		}
	}
	
	/**
	 * 发送文本消息(jms1.1规范,producer-consumer)
	 * @param txt 消息文本
	 * @param propsMap 属性值map
	 * @param secondsAlive 消息存活时间
	 */
	private void sendTxtP2C(String txt,Map propsMap,long secondsAlive){
		try {
			if(!init){
				initP2C();
			}
			TextMessage msg=session.createTextMessage();
			msg.setText(txt);
			this.addProperties(msg,propsMap);
			producer.setTimeToLive(secondsAlive*1000);
			producer.send(msg);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			this.close();
		}
	}
	
	/**
	 * 发送对象消息(jms1.1规范,producer-consumer)
	 * @param obj 可序列化对象
	 * @param propsMap 属性值map
	 */
	private void sendObjP2C(Serializable obj,Map propsMap){
		try {
			if(!init){
				initP2C();
			}
			ObjectMessage msg=session.createObjectMessage();
			msg.setObject(obj);
			this.addProperties(msg,propsMap);
			producer.send(msg);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			this.close();
		}
	}
	
	/**
	 * 发送对象消息(jms1.1规范,producer-consumer)
	 * @param obj 可序列化对象
	 * @param propsMap 属性值map
	 * @param secondsAlive 消息存活时间
	 */
	private void sendObjP2C(Serializable obj,Map propsMap,long secondsAlive){
		try {
			if(!init){
				initP2C();
			}
			ObjectMessage msg=session.createObjectMessage();
			msg.setObject(obj);
			this.addProperties(msg,propsMap);
			producer.setTimeToLive(secondsAlive*1000);
			producer.send(msg);	
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
			this.close();
		}
	}

	
	/**
	 * 向jms消息中添加propsMap中的key-value属性
	 * @param msg
	 * @param propsMap
	 * @throws Exception
	 */
	private void addProperties(Message msg,Map propsMap)throws Exception{
		if(propsMap!=null&&!propsMap.isEmpty()){
			Set keySet=propsMap.keySet();
			Iterator keys=keySet.iterator();
			while(keys.hasNext()){
				String key=(String)keys.next();
				String value=(String)propsMap.get(key);
				msg.setStringProperty(key,value);
				//System.out.println(key+":"+value);
			}
		}
	}
	

	
	/**
	 * 
	 * @param key
	 * @return String
	 */
	protected String getProperty(String key){
		if(properties==null||properties.isEmpty()){
			return "";
		}
		if(properties.containsKey(key)){
			return (String) properties.get(key);
		}
		return "";
	}

	
	/**
	 * 关闭所有
	 *
	 */
	protected void close(){
			if(this.queueSender!=null) try{this.queueSender.close();this.queueSender=null;}catch(Exception e){}
			if(this.queueSession!=null) try{this.queueSession.close();this.queueSession=null;}catch(Exception e){}
			if(this.queueConnection!=null) try{this.queueConnection.close();this.queueConnection=null;}catch(Exception e){}
			if(this.queue!=null) try{this.queue=null;}catch(Exception e){}
			if(this.queueConnectionFacotory!=null) try{this.queueConnectionFacotory=null;}catch(Exception e){}
			this.ppInit=false;
		
			if(this.publisher!=null) try{this.publisher.close();this.publisher=null;}catch(Exception e){}
			if(this.topicSession!=null) try{this.topicSession.close();this.topicSession=null;}catch(Exception e){}
			if(this.topicConnection!=null) try{this.topicConnection.close();this.topicConnection=null;}catch(Exception e){}
			if(this.topic!=null) try{this.topic=null;}catch(Exception e){}
			if(this.topicConnectionFacotory!=null) try{this.topicConnectionFacotory=null;}catch(Exception e){}
			this.psInit=false;
		
			if(this.producer!=null) try{this.producer.close();this.producer=null;}catch(Exception e){}
			if(this.session!=null) try{this.session.close();this.session=null;}catch(Exception e){}
			if(this.connection!=null) try{this.connection.close();this.connection=null;}catch(Exception e){}
			if(this.destination!=null) try{this.destination=null;}catch(Exception e){}
			if(this.connectionFactory!=null) try{this.connectionFactory=null;}catch(Exception e){}			         
			this.init=false;
			log.log("sender "+this.id+","+this.name+" closed",-1);
	}
	
	
	/**
	 * 测试
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		String config_path = "F:/work/RFrame3.0/cfg/config/jms.xml";//for test				
		File file = new File(config_path);
		Config.load(file);
		
		Map flags=new HashMap();
		flags.put("flag","me");
		Sender sender1=Sender.sendTxt("SENDER-TEST","yyyyyy",flags);
		Sender sender2=Sender.sendTxt("SENDER-TEST2","xxxxxx",flags);
		for(int i=0;i<9;i++){
			sender1.sendTxt("yyyyyy",flags);
			sender2.sendTxt("xxxxxx",flags);
		}
		sender1.close();
		sender2.close();
		System.exit(0);
	}
}
