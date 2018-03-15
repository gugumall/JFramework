package j.jms;

import javax.jms.DeliveryMode;

/**
 * @author JFramework
 *
 */
public class Parameters {
	public static final int TYPE_PP = 0;
	public static final int TYPE_PS = 1;
	public static final int TYPE_PC = 2;

	public static final int NON_PERSISTENT = DeliveryMode.NON_PERSISTENT;
	public static final int PERSISTENT = DeliveryMode.PERSISTENT;

	protected String ID;
	protected String NAME;
	protected String INITIAL_CONTEXT_FACTORY;
	protected String PROVIDER_URL;
	protected String CONNECTION_FACTORY;
	protected String DESTINATION;
	protected String CONSUMER;	
	protected String LISTENER;//消息接收/监听类
	protected int TYPE;//2，jms1.1规范，producer-consumer；0，queue；1，topic
	protected int DURABLE;//是否持久化
}
