package j.jms;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;

import j.log.Logger;

/**
 * 
 * @author JFramework
 *
 */
public class Receiver {
	private static Logger log=Logger.create(Receiver.class);//日志输出
	
	protected Map properties;//JMS传输配置
	protected Context context;//上下文
	
	/*
	 * 通用接口
	 */
	protected boolean init=false;
	protected ConnectionFactory connectionFactory;
	protected Connection connection;
	protected Session session;
	protected Destination destination;
	protected MessageConsumer consumer;
	
	/*
	 * PP
	 */
	protected boolean ppInit=false;
	protected QueueConnectionFactory queueConnectionFacotory;
	protected QueueConnection queueConnection;
	protected QueueSession queueSession;
	protected Queue queue;
	protected QueueReceiver queueReceiver;
	
	/*
	 * PS
	 */
	protected boolean psInit=false;
	protected TopicConnectionFactory topicConnectionFacotory;
	protected TopicConnection topicConnection;
	protected TopicSession topicSession;
	protected Topic topic;
	protected TopicSubscriber subscriber;


	protected String id="";
	protected String name="";
	protected int type=0;//发送类型 2，jms1.1规范，不区分queue和topic；0，queue；1，topic
	protected int durable=1;//消息是否持久化 1,NON_PERSISTENT   2,PERSISTENT
	
	/**
	 * 读取configMap中的key-value配置信息并创建context
	 * @param configMap
	 * @throws Exception
	 */
	public Receiver(Parameters  params) throws Exception {
		properties=new HashMap();		
		properties.put("INITIAL_CONTEXT_FACTORY",params.INITIAL_CONTEXT_FACTORY);
		properties.put("PROVIDER_URL",params.PROVIDER_URL);
		properties.put("CONNECTION_FACTORY",params.CONNECTION_FACTORY);
		properties.put("DESTINATION",params.DESTINATION);
		properties.put("CONSUMER",params.CONSUMER);		
		properties.put("LISTENER",params.LISTENER);
		this.id=params.ID;
		this.name=params.NAME;
		this.durable=params.DURABLE;
		this.type=params.TYPE;
		createContext();
		listen();
	}
	
	/**
	 * 创建context
	 * @param initFact
	 * @param providerUrl
	 * @return Context
	 * @throws Exception
	 */
	protected void createContext()throws Exception{
		Hashtable properties = new Hashtable();
		
		properties.put(Context.INITIAL_CONTEXT_FACTORY,this.getProperty("INITIAL_CONTEXT_FACTORY"));//初始化工厂
				
		properties.put(Context.PROVIDER_URL,this.getProperty("PROVIDER_URL"));//提供商URL
		
		context =new InitialContext(properties);
	}
	
	/**
	 * 初始化并开始监听消息
	 * @throws Exception
	 */
	private void listen() throws Exception {
		try {
			MessageListener listener = (MessageListener) Class.forName(this.getProperty("LISTENER")).newInstance();
			if (type == 0) {
				initP2P(DeliveryMode.PERSISTENT);
				queueReceiver.setMessageListener(listener);
			} else if (type == 1) {
				initP2S(DeliveryMode.PERSISTENT);
				subscriber.setMessageListener(listener);
			} else if (type == 2) {
				initP2C(DeliveryMode.PERSISTENT);

				if(this.durable==Parameters.PERSISTENT){
					subscriber.setMessageListener(listener);
				}else{
					consumer.setMessageListener(listener);
				}
			} else {
				throw new Exception("type illegal!");
			}
			
			System.out.println("message listener is to listen......\r\nlistener name is: "+ this.getProperty("LISTENER"));
		} catch (Exception e) {
			log.log(e,Logger.LEVEL_ERROR);
			this.close();
		}
	}
	
	

	
	/**
	 * 初始化点对点
	 * 
	 * @param persistent
	 * @throws Exception
	 */
	protected void initP2P(int persistent)throws Exception{
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
		 * 接收者
		 */
		queueReceiver=queueSession.createReceiver(queue);
		
		this.ppInit=true;
	}
	
	/**
	 * 初始化订阅发布
	 * 
	 * @param persistent
	 * @throws Exception
	 */
	protected void initP2S(int persistent)throws Exception{
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
		 * 接收者
		 */
		if(this.durable==Parameters.PERSISTENT){
			subscriber=topicSession.createDurableSubscriber(topic,getProperty("CONSUMER"));
		}else{
			subscriber=topicSession.createSubscriber(topic);
		}
		
		this.psInit=true;
	}
	
	/**
	 * 初始化producer-consumer
	 * @param persistent
	 * @throws Exception
	 */
	protected void initP2C(int persistent)throws Exception{
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
		if(this.durable==Parameters.PERSISTENT){
			topic=(Topic) context.lookup(getProperty("DESTINATION"));
		}else{
			destination=(Destination) context.lookup(getProperty("DESTINATION"));
		}
		
		/*
		 * 接收者
		 */
		if(this.durable==Parameters.PERSISTENT){			
			subscriber=session.createDurableSubscriber(topic,getProperty("CONSUMER"));
		}else{
			consumer=session.createConsumer(destination);	
		}
		
		this.init=true;
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
			if(this.queueSession!=null) try{this.queueSession.close();this.queueSession=null;}catch(Exception e){}
			if(this.queueConnection!=null) try{this.queueConnection.close();this.queueConnection=null;}catch(Exception e){}
			if(this.queueReceiver!=null) try{this.queueReceiver.close();this.queueReceiver=null;}catch(Exception e){}
			if(this.queue!=null) try{this.queue=null;}catch(Exception e){}
			if(this.queueConnectionFacotory!=null) try{this.queueConnectionFacotory=null;}catch(Exception e){}
			this.ppInit=false;
		
			if(this.topicSession!=null) try{this.topicSession.close();this.topicSession=null;}catch(Exception e){}
			if(this.topicConnection!=null) try{this.topicConnection.close();this.topicConnection=null;}catch(Exception e){}
			if(this.subscriber!=null) try{this.subscriber.close();this.subscriber=null;}catch(Exception e){}
			if(this.topic!=null) try{this.topic=null;}catch(Exception e){}
			if(this.topicConnectionFacotory!=null) try{this.topicConnectionFacotory=null;}catch(Exception e){}
			this.psInit=false;
		
			if(this.session!=null) try{this.session.close();this.session=null;}catch(Exception e){}
			if(this.connection!=null) try{this.connection.close();this.connection=null;}catch(Exception e){}
			if(this.consumer!=null) try{this.consumer.close();this.consumer=null;}catch(Exception e){}
			if(this.destination!=null) try{this.destination=null;}catch(Exception e){}
			if(this.connectionFactory!=null) try{this.connectionFactory=null;}catch(Exception e){}
			         
			this.init=false;
			log.log("receiver "+this.id+","+this.name+" closed",-1);
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		String config_path = "F:/work/RFrame3.0/cfg/config/jms.xml";//for test				
		File file = new File(config_path);
		Config.load(file);
		
		Config.startup();
	}
}