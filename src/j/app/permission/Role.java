package j.app.permission;

import java.io.Serializable;

import j.util.ConcurrentMap;


/**
 * 角色，代表一个权限的集合，拥有某个角色的用户可以执行某些操作/访问某些资源
 * @author JFramework
 *
 */
public class Role implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final String STATUS_AVAILABLE="1";
	public static final String STATUS_UNAVAILABLE="0";
	/**
	 * Role是静态的、线程安全的，为避免为每个登录用户创建一组角色实例，
	 * 对于某个ROLE_ID，仅创建一个角色实例，该实例以ROLE_ID为key保存在Map
	 */
	private static ConcurrentMap roles=new ConcurrentMap();

	private java.lang.String roleId;
	private java.lang.String roleName;
	private java.lang.String roleDesc;
	private java.lang.String roleStat;
	
	/**
	 * constructor
	 * 
	 */
	private Role(){
		this.roleStat=STATUS_AVAILABLE;
	}
	
	/**
	 * 
	 * @param roleId
	 * @return
	 */
	public static Role getInstance(String roleId){
		if(roles.containsKey(roleId)){
			return (Role)roles.get(roleId);
		}
		Role role=new Role();
		role.setRoleId(roleId);
		roles.put(roleId,role);
		return role;
	}

	//getters and setters
	public java.lang.String getRoleId(){
		return this.roleId;
	}
	public void setRoleId(java.lang.String roleId){
		this.roleId=roleId;
	}

	public java.lang.String getRoleName(){
		return this.roleName;
	}
	public void setRoleName(java.lang.String roleName){
		this.roleName=roleName;
	}

	public java.lang.String getRoleDesc(){
		return this.roleDesc;
	}
	public void setRoleDesc(java.lang.String roleDesc){
		this.roleDesc=roleDesc;
	}

	public java.lang.String getRoleStat(){
		return this.roleStat;
	}
	public void setRoleStat(java.lang.String roleStat){
		this.roleStat=roleStat;
	}
	//getters and setters end

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "roleName:"+roleId+", roleName:"+roleName+", roleDesc:"+roleDesc+", roleStat:"+roleStat;
	}
}
