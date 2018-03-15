/*
 * Created on 2016-12-08
 *
 */
package j.db;


import java.io.Serializable;


/**
 * @author JStudio-BeanGenerator
 *
 */
public class JthirdpartyUser implements Serializable{

	private java.lang.String uuid;
	private java.lang.String uid;
	private java.lang.String thirdpartyCode;
	private java.lang.String thirdpartyUid;

	public java.lang.String getUuid(){
		return this.uuid;
	}
	public void setUuid(java.lang.String uuid){
		this.uuid=uuid;
	}

	public java.lang.String getUid(){
		return this.uid;
	}
	public void setUid(java.lang.String uid){
		this.uid=uid;
	}

	public java.lang.String getThirdpartyCode(){
		return this.thirdpartyCode;
	}
	public void setThirdpartyCode(java.lang.String thirdpartyCode){
		this.thirdpartyCode=thirdpartyCode;
	}

	public java.lang.String getThirdpartyUid(){
		return this.thirdpartyUid;
	}
	public void setThirdpartyUid(java.lang.String thirdpartyUid){
		this.thirdpartyUid=thirdpartyUid;
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
