/*
 * Created on 2019-03-14
 *
 */
package j.db;


import java.io.Serializable;


/**
 * @author JFramework-BeanGenerator
 *
 */
public class JthirdpartyUser implements Serializable{

	private java.lang.String uuid;
	private java.lang.String userId;
	private java.lang.String thirdpartyCode;
	private java.lang.String thirdpartyUserId;
	private java.lang.String thirdpartyNickname;
	private java.lang.String thirdpartyHeader;

	public java.lang.String getUuid(){
		return this.uuid;
	}
	public void setUuid(java.lang.String uuid){
		this.uuid=uuid;
	}

	public java.lang.String getUserId(){
		return this.userId;
	}
	public void setUserId(java.lang.String userId){
		this.userId=userId;
	}

	public java.lang.String getThirdpartyCode(){
		return this.thirdpartyCode;
	}
	public void setThirdpartyCode(java.lang.String thirdpartyCode){
		this.thirdpartyCode=thirdpartyCode;
	}

	public java.lang.String getThirdpartyUserId(){
		return this.thirdpartyUserId;
	}
	public void setThirdpartyUserId(java.lang.String thirdpartyUserId){
		this.thirdpartyUserId=thirdpartyUserId;
	}

	public java.lang.String getThirdpartyNickname(){
		return this.thirdpartyNickname;
	}
	public void setThirdpartyNickname(java.lang.String thirdpartyNickname){
		this.thirdpartyNickname=thirdpartyNickname;
	}

	public java.lang.String getThirdpartyHeader(){
		return this.thirdpartyHeader;
	}
	public void setThirdpartyHeader(java.lang.String thirdpartyHeader){
		this.thirdpartyHeader=thirdpartyHeader;
	}

	public boolean equals(Object obj){
		return super.equals(obj);
	}

	public int hashCode(){
		return super.hashCode();
	}

	public String toString(){
		return super.toString();
	}

}
