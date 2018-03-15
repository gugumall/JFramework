package j.service;

/**
 * 服务框架用到的一些常量
 * @author JFramework
 *
 */
public final class Constants {
	
	public static final String PRIVICY_PUBLIC="PUBLIC";//服务或方法是公开的
	public static final String PRIVICY_MD5="MD5";//服务或方法只有指定客户节点可访问，并需通过MD5校验

	public static final String STATUS_OK="__STATUS_OK";//状态正常
	public static final String STATUS_OFF="__STATUS_OFF";//状态为关闭
	public static final String AUTH_FAILED="__AUTH_FAILED";//认证失败
	public static final String AUTH_PASSED="__AUTH_PASSED";//认证成功

	public static final String INVOKING_ACCEPTED="__INVOKING_ACCEPTED";//调用某方法，已受理，但结果未知
	public static final String INVOKING_DONE="__INVOKING_ACCEPTED";//已经完成
	public static final String INVOKING_FAILED="__INVOKING_FAILED";//已经完成
	
	public static final String SERVICE_NOT_FOUND="__SERVICE_NOT_FOUND";//服务不存在
	public static final String SERVICE_NOT_AVAIL="__SERVICE_NOT_AVAIL";//服务不可用
	
	public static final String JSERVICE_PARAM_CLIENT_UUID="js_client_uuid";
	public static final String JSERVICE_PARAM_MACHINE_ID="js_machine_id";
	public static final String JSERVICE_PARAM_MD5_STRING_4SERVICE="js_md5_4service";
	public static final String JSERVICE_PARAM_MD5_STRING_4ROUTER="js_md5_4router";
	public static final String JSERVICE_PARAM_SERVICE_CODE="js_service_code";
	public static final String JSERVICE_PARAM_SERVICE_UUID="js_service_uuid";
	public static final String JSERVICE_PARAM_SERVICE_METHOD="js_service_method";
	public static final String JSERVICE_PARAM_INTERFACE_CLASS="js_interface_class";
	public static final String JSERVICE_PARAM_RMI_TYPE="js_rmi_type";
	public static final String JSERVICE_PARAM_RMI_CHANNEL="js_rmi_channel";
	public static final String JSERVICE_PARAM_HTTP_CHANNEL="js_http_channel";
	public static final String JSERVICE_PARAM_PARAMETERS="js_parameters";
	public static final String JSERVICE_PARAM_CHANNEL_TYPE="js_channel_type";
	
	public static final Object GLOBAL_LOCK=new Object();//全局锁
}
