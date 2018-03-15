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
public class JthirdpartyForLogin implements Serializable{

	private java.lang.String thirdpartyCode;
	private java.lang.String thirdpartyName;
	private java.lang.String delBySys;

	public java.lang.String getThirdpartyCode(){
		return this.thirdpartyCode;
	}
	public void setThirdpartyCode(java.lang.String thirdpartyCode){
		this.thirdpartyCode=thirdpartyCode;
	}

	public java.lang.String getThirdpartyName(){
		return this.thirdpartyName;
	}
	public void setThirdpartyName(java.lang.String thirdpartyName){
		this.thirdpartyName=thirdpartyName;
	}

	public java.lang.String getDelBySys(){
		return this.delBySys;
	}
	public void setDelBySys(java.lang.String delBySys){
		this.delBySys=delBySys;
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
