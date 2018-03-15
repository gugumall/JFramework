package j.app.sso;

import j.app.permission.Role;
import j.sys.SysConfig;
import j.tool.region.Countries;
import j.util.ConcurrentList;
import j.util.JUtilString;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author 肖炯
 *
 */
public abstract class User implements Serializable{	
	private static final long serialVersionUID = 1L;
	public ConcurrentList roles;// 用户角色列表
	
	//必须的用户基本信息 getters
	public abstract String getUserId();	
	
	//必须的用户基本信息 setters
	public abstract void setUserId(String userId);	
	
	//必须的用户基本信息 getters
	public abstract String getUserName();	
	
	//必须的用户基本信息 setters
	public abstract void setUserName(String userName);	
	
	//加载用户信息
	public abstract boolean load(HttpSession _session,HttpServletRequest _request) throws Exception;

	/**
	 * constructor
	 *
	 */
	public User() {
		roles = new ConcurrentList();
	}
	
	/**
	 * 
	 * @param _userId
	 * @throws Exception
	 */
	public User(String _userId) throws Exception {
		if (_userId == null) {
			throw new Exception("用户Id为空");
		} else {
			setUserId(_userId);
			roles = new ConcurrentList();
		}
	}

	/**
	 * 拥有的角色
	 * @return
	 * @throws Exception
	 */
	public ConcurrentList getRoles() throws Exception {
		return roles;
	}

	/**
	 * 用户是否拥有roleIds中的某个角色
	 * @param roleIds
	 * @return
	 */
	public boolean isUserInRole(String roleIds[]) {
		if (roleIds == null || roleIds.length == 0){
			return true;
		}else{
			for (int i = 0; i < roles.size(); i++) {
				Role role = (Role) roles.get(i);
				if(Role.STATUS_AVAILABLE.equals(role.getRoleStat())&&JUtilString.contain(roleIds,role.getRoleId())) return true;
			}
			return false;
		}
	}

	/**
	 * 用户是否拥有roleId所代表的角色
	 * @param roleId
	 * @return
	 */
	public boolean isUserInRole(String roleId){
		if (roleId == null||roleId.equals("")){
			return true;
		}else{
			for (int i = 0; i < roles.size(); i++) {
				Role role = (Role) roles.get(i);
				if (Role.STATUS_AVAILABLE.equals(role.getRoleStat())&&roleId.equals(role.getRoleId())){
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 加载用户详细信息
	 * @param _userId
	 * @return
	 */
	public static User loadUser(HttpSession _session,HttpServletRequest _request,String _userId){
		User user=null;
        try{
        	Client client=SSOConfig.getSsoClientByIdOrUrl(SysConfig.getSysId());
        	user=(User)Class.forName(client.getUserClass()).newInstance();
        	user.setUserId(_userId);
        	boolean loaded=user.load(_session,_request);
        	if(!loaded&&user!=null) user=null;
        }catch(Exception e){
        	e.printStackTrace();
        	if(user!=null) user=null;
        }
        return user;
    }

	/**
	 * 清除用户信息
	 * 
	 */
	public void destroy() {
		roles.clear();
	}
	
	/**
	 * 
	 * @param uid
	 * @return
	 */
	public static boolean isValidUid(String uid){
		if(uid==null||uid.equals("")) return false;

		uid=uid.toLowerCase();

		if(Countries.isMobileValid(uid)
				||JUtilString.isEmail(uid, 64)){
			return true;
		}
		
		return uid.matches("[0-9a-z]{1}[0-9a-z\\-._]{4,30}[0-9a-z]{1}$");
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		System.out.println(isValidUid("crazyroar@126.com"));
		System.out.println(isValidUid("15099782078"));
		System.out.println(isValidUid("xiao-jiong_x.c"));
		System.out.println(isValidUid("xiao-jiong_x."));
	}
}