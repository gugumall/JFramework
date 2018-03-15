/*
 * Created on 2004-7-20
 *
 */
package j.dao.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

/**
 * 存储过程
 * 
 * @author JFramework
 */

public class Procedure {
	private Connection conn;

	/**
	 * 存储过程（函数）名
	 */
	private String procedureName;

	/**
	 * 存储过程（函数）返回值类型，只能是java.sql.Types中定义的类型或ParaValuePair.NO_RETURN_VALUE
	 */
	private int returnType;

	/**
	 * 保存输入参数－值对
	 */
	private Collection parameters;
	
	/**
	 * constructor
	 * @param _conn
	 * @param _procedureName
	 * @param _returnType
	 */
	public Procedure(Connection _conn, String _procedureName, int _returnType) {
		this.conn = _conn;
		this.procedureName = _procedureName;
		this.returnType = _returnType;
	}
	
	/**
	 * 增加参数类型——值对
	 * @param paraValuePair
	 */
	public void addParaValuePair(ParaValuePair paraValuePair) {
		this.parameters.add(paraValuePair);
	}
	
	/**
	 * 得到存储过程名
	 * @return
	 */
	public String getProcedureName() {
		return this.procedureName;
	}
	
	/**
	 * 得到返回值类型
	 * @return
	 */
	public int getReturnType() {
		return this.returnType;
	}
	
	/**
	 * 得到参数Collection
	 * @return
	 */
	public Collection getParameters() {
		return this.parameters;
	}

	/**
	 * 存储过程调用
	 * 
	 * @param procedure
	 * @return Object
	 * @throws SQLException
	 */
	public Object callProcedure() throws SQLException {
		CallableStatement cstmt = null;
		//判断是否有返回值
		boolean haveReturnValue = false;
		if (getReturnType() == ParaValuePair.NO_RETURN_VALUE) {
			haveReturnValue = false;
		} else {
			haveReturnValue = true;
		}
		//判断是否有返回值 end
		
		try {
			conn.setAutoCommit(false);
			
			//构造调用存储过程字符串
			String strProcedure = "";
			if (!haveReturnValue) {
				strProcedure = "{ call " + getProcedureName() + "(";
			} else {
				strProcedure = "{ ? = call " + getProcedureName()+ "(";
			}
			Collection paras = getParameters();
			if (paras.size() == 1) {
				strProcedure += ") }";
			}
			if (paras.size() > 1) {
				for (int i = 1; i < paras.size(); i++) {
					strProcedure += "?,";
				}
				strProcedure += "?) }";
			}
			cstmt = conn.prepareCall(strProcedure);
			//构造调用存储过程字符串 end
			
			//如果有返回值，注册返回值参数
			if (haveReturnValue) {
				cstmt.registerOutParameter(1, getReturnType());
			}
			//如果有返回值，注册返回值参数 end
			
			
			//设置参数值
			Iterator parasIterator=paras.iterator();
			for (int i = 1;parasIterator.hasNext(); i++) {
				ParaValuePair paraValuePair = (ParaValuePair)parasIterator.next();
				if (!haveReturnValue) {
					cstmt.setObject(paraValuePair.getPosition(), paraValuePair,paraValuePair.getType(), paraValuePair.getPrecision());
				} else {
					cstmt.setObject(paraValuePair.getPosition() + 1,paraValuePair, paraValuePair.getType(),paraValuePair.getPrecision());
				}
			}
			///设置参数值 end

			cstmt.execute();//执行存储过程
			
			//返回值
			if (!haveReturnValue) {
				return new Integer(ParaValuePair.NO_RETURN_VALUE);
			} else {
				return cstmt.getObject(1);
			}
			//返回值 end
		} catch (SQLException e) {
			System.out.println("调用存储过程错误(Procedure.callProcedure) - ");
			e.printStackTrace();
		} finally {
			cstmt.clearParameters();
			cstmt.close();
			conn.close();
		}
		return null;
	}
}