/*
 * Created on 2004-7-20
 *
 */
package j.dao.util;

/**
 * 存储过程（函数）中输入参数的类型-值对
 * 
 * @author 肖炯
 */

public class ParaValuePair {
	/**
	 * 参数类型，可能值为java.sql.Types中定义的所有类型
	 */
	private int type;

	/**
	 * 该参数出现在存储过程（函数）参数列表中的位置，取值1，2...
	 */
	private int position;

	/**
	 * 传给该参数的值，只能是Object类型，如果是基本类型请使用包装器类
	 */
	private Object value;

	/**
	 * 当传入值为java.sql.Types.DECIMAL 或 java.sql.Types.NUMERIC时的小数位数
	 */
	private int precision;

	/**
	 * 当无返回值时使用（仅限于输出参数类型）
	 */
	public final static int NO_RETURN_VALUE = -1717;

	/**
	 * 设置参数
	 * 
	 * @param _position
	 * @param _type
	 * @param _value
	 * @param _precision
	 */
	public void setPara(int _position, int _type, Object _value, int _precision) {
		this.position = _position;
		this.type = _type;
		this.value = _value;
		this.precision = _precision;
	}

	/**
	 * 设置参数
	 * 
	 * @param _position
	 * @param _type
	 * @param _value
	 */
	public void setPara(int _position, int _type, Object _value) {
		this.position = _position;
		this.type = _type;
		this.value = _value;
		this.precision = 0;
	}

	/**
	 * 取得该参数出现在存储过程（函数）参数列表中的位置，取值1，2...
	 * 
	 * @return
	 */
	public int getPosition() {
		return this.position;
	}

	/**
	 * 取得类型
	 * 
	 * @return
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * 取得小数位数
	 * 
	 * @return
	 */
	public int getPrecision() {
		return this.precision;
	}

	/**
	 * 取得值
	 * 
	 * @return
	 */
	public Object getValue() {
		return this.value;
	}
}