package j.app;

import j.util.JUtilString;


/**
 * @author 肖炯
 *
 */
public class Constants {	
	public static final String SSO_SYS_ID="sso_sys_id";	
	public static final String SSO_MACHINE_ID="sso_machine_id";	
	public static final String SSO_LOGIN_FROM_SYS_ID="sso_login_from";	
	public static final String SSO_USER_ID="sso_user_id";	
	public static final String SSO_USER_NAME="sso_user_name";	
	public static final String SSO_USER_ROLE="sso_user_role";	
	public static final String SSO_WEBSITE="sso_website";	
	public static final String SSO_USER_IP="sso_user_ip";
	public static final String SSO_USER_PWD="sso_user_pwd";
	public static final String SSO_USER_DOMAIN="sso_user_domain";
	public static final String SSO_VERIFIER_UUID="sso_verifier_uuid";
	public static final String SSO_VERIFIER_CODE="sso_verifier_code";
	public static final String SSO_BACK_URL="sso_back_url";
	public static final String SSO_LOGIN_PAGE="sso_login_page";
	public static final String SSO_CLIENT="sso_client";
	public static final String SSO_CLIENT_SESSION_ID="sso_client_sid";
	public static final String SSO_LOGIN_AGENT="sso_login_agent";
	public static final String SSO_GLOBAL_SESSION_ID_ON_SERVER="sso_global_session_id_on_server";
	public static final String SSO_GLOBAL_SESSION_ID="sso_global_session_id";
	public static final String SSO_LOGIN_RESULT_CODE="sso_login_result_code";
	public static final String SSO_LOGIN_TYPE="login_type";
	public static final String SSO_LOGIN_RESULT_MSG="sso_login_result_msg";
	public static final String SSO_LOGIN_CHANCES="sso_login_chances";
	public static final String SSO_LOGIN_INFO="sso_login_info";
	public static final String SSO_TOKEN="sso_token";
	public static final String SSO_PNAMES="sso_parameter_names";
	public static final String SSO_PVALUES="sso_parameter_values";
	public static final String SSO_PVERIFY="sso_parameter_verify";
	public static final String SSO_LOGIN_STATUS_CACHE="sso_login_status_cache";
	public static final String SSO_ONLINES_CACHE="sso_onlines";

	public static final String SSO_MSG="sso_msg";
	public static final String SSO_SERVICE_UNAVAILABLE="sso_service_unavailable";//服务不可用（比如不是sso server）
	public static final String SSO_BAD_CLIENT="sso_bad_client";//非法的SSO Client
	public static final String SSO_BAD_AGENT="sso_bad_agent";
	public static final String SSO_BAD_TOKEN="sso_bad_token";
	
	public static final String SSO_USER="sso_user";
	public static final String SSO_PASSPORT="sso_passport";
	public static final String SSO_STAT_CLIENT="sso_stat_client";
	public static final String SSO_TIME="sso_time";
	public static final String SSO_UPDATES="sso_updates";
	public static final String SSO_MD5_STRING="sso_md5_string";
	public static final String SSO_INFO_GETTER="/sso_info_getter.htm";
	public static final String SSO_INFO_GETTER_LOGIN="/sso_info_getter_login.htm";	
	public static final String SSO_GET_LOGIN_STATUS="sso_getting_login_status";
	public static final String SSO_PUT_LOGIN_STATUS="sso_putting_login_status";
	public static final String SSO_GET_LOGIN_STATUS_RESULT="/sso_getting_login_status.htm";
	public static final String RESPONSE_OK="ok";
	public static final String RESPONSE_ERR="err";
	public static final String RESPONSE_MD5_ERR="md5err";
	
	public static final String I18N_LANGUAGE="lang";
	
	public static final String J_REQUEST_UUID="j_request_uuid";
	public static final String J_REQUEST_UUID_SN="j_request_uuid_sn";
	public static final String J_DUPLICATED_RQUEST="j_duplicated_request";
	
	public static final String J_BACK_URL="j_back_url";
	public static final String J_BACK_TYPE="j_back_type";
	public static final String J_ACTION_RESULT="j_action_result";
	
	public static final String J_PAGE_CONTENT="j_page_content";
	public static final String J_ILLEGAL_VISITOR="j_illegal_visitor";
	public static final String J_NO_ACTION="j_no_action";
	
	public static final String RESPONSE_FILE_NOT_LOGIN="/WEB-INF/pages/helper/NOT_LOGIN.jsp";
	public static final String RESPONSE_FILE_MAX_UPLOAD_SIZE="/WEB-INF/pages/helper/MAX_UPLOAD_SIZE.jsp";
	public static final String RESPONSE_FILE_MAX_POST_SIZE="/WEB-INF/pages/helper/MAX_POST_SIZE.jsp";
	
	
	public static void main(String[] args){
		System.out.println(JUtilString.exists("baiduspider-image+(+http://www.baidu.com/search/spider.htm)",new String[]{"baiduspider"}));
	}
}
