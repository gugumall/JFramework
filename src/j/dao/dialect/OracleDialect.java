/*
 * Created on 2005-4-12
 *
 */
package j.dao.dialect;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

import j.dao.Column;
import j.dao.RdbmsDao;
import j.dao.type.Blob;
import j.dao.type.Clob;
import j.dao.util.Methods;
import j.log.Logger;
import j.util.JUtilBean;
import j.util.JUtilString;

/**
 * @author JFramework
 *
 */
public class OracleDialect extends RdbmsDao {
	private static Logger log=Logger.create(OracleDialect.class);
	
	/**
	 * 
	 *
	 */
	public OracleDialect() {
		super();
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.RdbmsDao#supportsLimitOffset()
	 */
	public boolean supportsLimitOffset() {
		return true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.RdbmsDao#getSQLWithRowSetLimit(java.lang.String, int, int)
	 */
	public String getSQLWithRowSetLimit(String sql,int start, int end) {
		sql = sql.trim();
		
		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 );
		pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
		pagingSelect.append(sql);
		pagingSelect.append(" ) row_ ) where rownum_ <= "+end+" and rownum_ > "+start);
		
		return pagingSelect.toString();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#insert(java.lang.Object)
	 */
	public void insert(String tblName,Object vo) throws Exception{
		PreparedStatement pstmt=null;		
		try{
			if(factory.getPlugin()!=null){
				factory.getPlugin().beforeInsert(vo);
			}
			if(vo==null){
				throw new Exception("待插入对象为空");
			}
			Class cls=vo.getClass();
			tblName=factory.getTrueTblName(tblName);
			List cols=factory.getColumns(tblName);	
			
			String sql="insert into "+tblName+"(";
			String sqlTmp=")VALUES(";
			List values=new LinkedList();
			for(int i=0;i<cols.size();i++){
				Column col=(Column)cols.get(i);
				String colName=col.colName;
				Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
				Object value=method.invoke(vo,(Object[])null);
				if(value==null){
					sql+=colName+",";
					sqlTmp+="null,";
					cols.remove(i);
					i--;
				}else{
					values.add(i,value);
					sql+=colName+",";
					sqlTmp+="?,";
				}
			}
			sql=sql.substring(0,sql.length()-1);
			sql+=sqlTmp;
			sql=sql.substring(0,sql.length()-1);
			sql+=")";			
			log.log("sql:"+sql,Logger.LEVEL_DEBUG);
			pstmt=connection.prepareStatement(sql);
			
			for(int i=1;i<=cols.size();i++){
				Column col=(Column)cols.get(i-1);
				int    colType=col.colType;
				Object[] paras=null;
				
				//从vo得到对应字段的值
				Object value=values.get(i-1);
				if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
					paras=new Object[3];
					InputStream is=(InputStream)value;			
					paras[0]=new Integer(i);
					paras[1]=is;
					paras[2]=new Integer(is.available());	
				}else if(colType==Types.BLOB){
					Blob thisBlob=(Blob)value;
					
					BLOB blob=BLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
					blob.open(BLOB.MODE_READWRITE);
					OutputStream os=blob.getBinaryOutputStream();	
					os.write(thisBlob.getBytes(0,(int)thisBlob.length()));
					os.flush();
					os.close();
					blob.close();
					paras=new Object[2];
					paras[0]=new Integer(i);
					paras[1]=blob;
				}else if(colType==Types.CLOB){
					Clob thisclob=(Clob)value;
					
					CLOB clob=CLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
					clob.open(CLOB.MODE_READWRITE);
					Writer writer=clob.getCharacterOutputStream();	
					writer.write(thisclob.getCharacters());
					writer.flush();
					writer.close();
					clob.close();
					paras=new Object[2];
					paras[0]=new Integer(i);
					paras[1]=clob;
				}else{
					paras=new Object[2];
					paras[0]=new Integer(i);
					paras[1]=value;
				}
				Methods.set(colType,col.gzip,pstmt,paras);
			}
			
			pstmt.execute();
			pstmt.close();
			if(factory.getPlugin()!=null){
				factory.getPlugin().afterInsert(vo);
			}
		}catch(Exception e){
			try{
				pstmt.close();
			}catch(Exception ex){}
			
			throw e;
		}	
	}

	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#insertIfNotExists(java.lang.String, java.lang.Object, java.lang.String[])
	 */
	public void insertIfNotExists(String tblName,Object vo,String[] conditionKeys) throws Exception{	
		PreparedStatement pstmt=null;		
		try{
			if(factory.getPlugin()!=null){
				factory.getPlugin().beforeInsert(vo);
			}
			if(vo==null){
				throw new Exception("待插入对象为空");
			}
			if(conditionKeys==null||conditionKeys.length==0){
				throw new Exception("没有指定主键");
			}
			
			Class cls=vo.getClass();
			tblName=factory.getTrueTblName(tblName);
			
			String condition="";
			for(int i=0;i<conditionKeys.length;i++){
				conditionKeys[i]=factory.getColName(tblName,conditionKeys[i]);
				Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(conditionKeys[i]),null);
				Object keyValue=method.invoke(vo,(Object[])null);
				if(keyValue==null){
					condition+=conditionKeys[i]+" is null and ";
				}else{
					if(keyValue instanceof Integer||keyValue instanceof Float||keyValue instanceof Double){
						condition+=conditionKeys[i]+"="+keyValue+" and ";
					}else{
						condition+=conditionKeys[i]+"='"+keyValue+"' and ";
					}
				}
			}
			condition=condition.substring(0,condition.length()-5);
			int exists=getRecordCnt(tblName,condition);
			if(exists>0) return;
			
			List cols=factory.getColumns(tblName);	
			
			String sql="insert into "+tblName+"(";
			String sqlTmp=")VALUES(";
			List values=new LinkedList();
			for(int i=0;i<cols.size();i++){
				Column col=(Column)cols.get(i);
				String colName=col.colName;
				Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
				Object value=method.invoke(vo,(Object[])null);
				if(value==null){
					sql+=colName+",";
					sqlTmp+="null,";
					cols.remove(i);
					i--;
				}else{
					values.add(i,value);
					sql+=colName+",";
					sqlTmp+="?,";
				}
			}
			sql=sql.substring(0,sql.length()-1);
			sql+=sqlTmp;
			sql=sql.substring(0,sql.length()-1);
			sql+=")";			
			log.log("sql:"+sql,Logger.LEVEL_DEBUG);
			pstmt=connection.prepareStatement(sql);
			
			for(int i=1;i<=cols.size();i++){
				Column col=(Column)cols.get(i-1);
				int    colType=col.colType;
				Object[] paras=null;
				
				//从vo得到对应字段的值
				Object value=values.get(i-1);
				if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
					paras=new Object[3];
					InputStream is=(InputStream)value;			
					paras[0]=new Integer(i);
					paras[1]=is;
					paras[2]=new Integer(is.available());	
				}else if(colType==Types.BLOB){
					Blob thisBlob=(Blob)value;
					
					BLOB blob=BLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
					blob.open(BLOB.MODE_READWRITE);
					OutputStream os=blob.getBinaryOutputStream();	
					os.write(thisBlob.getBytes(0,(int)thisBlob.length()));
					os.flush();
					os.close();
					blob.close();
					paras=new Object[2];
					paras[0]=new Integer(i);
					paras[1]=blob;
				}else if(colType==Types.CLOB){
					Clob thisclob=(Clob)value;
					
					CLOB clob=CLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
					clob.open(CLOB.MODE_READWRITE);
					Writer writer=clob.getCharacterOutputStream();	
					writer.write(thisclob.getCharacters());
					writer.flush();
					writer.close();
					clob.close();
					paras=new Object[2];
					paras[0]=new Integer(i);
					paras[1]=clob;
				}else{
					paras=new Object[2];
					paras[0]=new Integer(i);
					paras[1]=value;
				}
				Methods.set(colType,col.gzip,pstmt,paras);
			}
			
			pstmt.execute();
			pstmt.close();
			if(factory.getPlugin()!=null){
				factory.getPlugin().afterInsert(vo);
			}
		}catch(Exception e){
			try{
				pstmt.close();
			}catch(Exception ex){}
			
			throw e;
		}	
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#update(java.lang.String, java.util.Map, java.lang.String)
	 */
	public void update(String tblName,Map colsBeUpdated,String condition) throws Exception {
		PreparedStatement pstmt=null;
		try{
			if(factory.getPlugin()!=null){
				factory.getPlugin().beforeUpdate(tblName, colsBeUpdated, condition);
			}
			if(colsBeUpdated==null||colsBeUpdated.isEmpty()){
				return;
			}
			tblName=factory.getTrueTblName(tblName);
			//生成sql
			String sql="update "+tblName+" set ";
			Set keySet=colsBeUpdated.keySet();
			Iterator colNames=keySet.iterator();
			while(colNames.hasNext()){
				String colName=(String)colNames.next();
				Object value=colsBeUpdated.get(colName);
				if(value==null){
					sql+=factory.getColName(tblName,colName)+"=null,";
					colsBeUpdated.remove(colName);
				}else{
					sql+=factory.getColName(tblName,colName)+"=?,";
				}
			}
			sql=sql.substring(0,sql.length()-1);
			if(condition!=null&&condition.trim().length()>=3){
				sql+=" where "+condition;
			}//生成sql end	
			if(factory.isSynchronized(tblName)){
				String lock=tblName.toUpperCase().intern();
				synchronized(lock){	
					pstmt=connection.prepareStatement(sql);
					
					keySet=colsBeUpdated.keySet();
					colNames=keySet.iterator();
			
					int index=1;
					while(colNames.hasNext()){
						String colName=(String)colNames.next();
						int colType=factory.getColType(tblName,colName);
						Object[] paras=null;				
						Object value=colsBeUpdated.get(colName);
						
						if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
							InputStream is=(InputStream)value;
							paras=new Object[3];
							paras[0]=new Integer(index);
							paras[1]=is;
							paras[2]=new Integer(is.available());
						}else if(colType==Types.BLOB){
							Blob myBlob=(Blob)value;
							
							BLOB blob=BLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
							blob.open(BLOB.MODE_READWRITE);
							OutputStream os=blob.getBinaryOutputStream();	
							os.write(myBlob.getBytes(0,(int)myBlob.length()));
							os.flush();
							os.close();
							blob.close();
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=blob;
						}else if(colType==Types.CLOB){
							Clob myclob=(Clob)value;
							
							CLOB clob=CLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
							clob.open(CLOB.MODE_READWRITE);
							Writer writer=clob.getCharacterOutputStream();	
							writer.write(myclob.getCharacters());
							writer.flush();
							writer.close();
							clob.close();
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=clob;
						}else{
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=value;
						}
						Methods.set(colType,factory.getColIsGzip(tblName,colName),pstmt,paras);
						index++;
					}
					
					pstmt.execute();
					pstmt.close();
				}
			}else{
				pstmt=connection.prepareStatement(sql);
				
				keySet=colsBeUpdated.keySet();
				colNames=keySet.iterator();
		
				int index=1;
				while(colNames.hasNext()){
					String colName=(String)colNames.next();
					int colType=factory.getColType(tblName,colName);
					Object[] paras=null;				
					Object value=colsBeUpdated.get(colName);
					
					if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
						InputStream is=(InputStream)value;
						paras=new Object[3];
						paras[0]=new Integer(index);
						paras[1]=is;
						paras[2]=new Integer(is.available());
					}else if(colType==Types.BLOB){
						Blob myBlob=(Blob)value;
						
						BLOB blob=BLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
						blob.open(BLOB.MODE_READWRITE);
						OutputStream os=blob.getBinaryOutputStream();	
						os.write(myBlob.getBytes(0,(int)myBlob.length()));
						os.flush();
						os.close();
						blob.close();
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=blob;
					}else if(colType==Types.CLOB){
						Clob myclob=(Clob)value;
						
						CLOB clob=CLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
						clob.open(CLOB.MODE_READWRITE);
						Writer writer=clob.getCharacterOutputStream();	
						writer.write(myclob.getCharacters());
						writer.flush();
						writer.close();
						clob.close();
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=clob;
					}else{
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=value;
					}
					Methods.set(colType,factory.getColIsGzip(tblName,colName),pstmt,paras);
					index++;
				}
				
				pstmt.execute();
				pstmt.close();
			}
			if(factory.getPlugin()!=null){
				factory.getPlugin().afterUpdate(tblName, colsBeUpdated, condition);
			}
		}catch(Exception e){
			try{
				pstmt.close();
			}catch(Exception ex){}
			
			throw e;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#updateByKeys(java.lang.String, java.lang.Object, java.lang.String[])
	 */
	public void updateByKeys(String tblName,Object bean,String[] primaryKeys)throws Exception{
		PreparedStatement pstmt=null;
		try{
			if(factory.getPlugin()!=null){
				factory.getPlugin().beforeUpdateByKeys(bean, primaryKeys);
			}
			if(bean==null){
				throw new Exception("指定的对象为空");
			}
			if(primaryKeys==null||primaryKeys.length==0){
				throw new Exception("没有指定主键");
			}
			
			tblName=factory.getTrueTblName(tblName);
			Class cls=bean.getClass();
			
			//生成sql
			String sql="update "+tblName+" set ";
			List cols=factory.getColumns(tblName);
			for(int i=0;i<cols.size();i++){
				String colName=((Column)cols.get(i)).colName;
				if(JUtilString.containIgnoreCase(primaryKeys,colName)||factory.isColIgnoredWhileUpdating(tblName, colName)){
					continue;
				}
				Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
				Object value=method.invoke(bean,(Object[])null);
				if(value==null){
					sql+=colName+"=null,";
				}else{
					sql+=colName+"=?,";
				}
			}
			sql=sql.substring(0,sql.length()-1);
			String condition="";
			for(int i=0;i<primaryKeys.length;i++){
				primaryKeys[i]=factory.getColName(tblName,primaryKeys[i]);
				Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(primaryKeys[i]),null);
				Object keyValue=method.invoke(bean,(Object[])null);
				if(keyValue==null){
					throw new Exception("主键值不能为空"+tblName+"->"+primaryKeys[i]);
				}
				if(keyValue instanceof Integer||keyValue instanceof Float||keyValue instanceof Double){
					condition+=primaryKeys[i]+"="+keyValue+" and ";
				}else{
					condition+=primaryKeys[i]+"='"+keyValue+"' and ";
				}
			}
			condition=condition.substring(0,condition.length()-5);
			sql+=" where "+condition;
			//生成sql end	
			if(factory.isSynchronized(tblName)){
				String lock=tblName.toUpperCase().intern();
				synchronized(lock){	
					pstmt=connection.prepareStatement(sql);
					
					int index=1;
					for(int i=0;i<cols.size();i++){
						String colName=((Column)cols.get(i)).colName;
						if(JUtilString.containIgnoreCase(primaryKeys,colName)||factory.isColIgnoredWhileUpdating(tblName, colName)){
							continue;
						}
						
						int colType=factory.getColType(tblName,colName);
						Object[] paras=null;	
						Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
						Object value=method.invoke(bean,(Object[])null);
						if(value==null){
							continue;
						}
						
						if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
							InputStream is=(InputStream)value;
							paras=new Object[3];
							paras[0]=new Integer(index);
							paras[1]=is;
							paras[2]=new Integer(is.available());
						}else if(colType==Types.BLOB){
							Blob myBlob=(Blob)value;
							
							BLOB blob=BLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
							blob.open(BLOB.MODE_READWRITE);
							OutputStream os=blob.getBinaryOutputStream();	
							os.write(myBlob.getBytes(0,(int)myBlob.length()));
							os.flush();
							os.close();
							blob.close();
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=blob;
						}else if(colType==Types.CLOB){
							Clob myclob=(Clob)value;
							
							CLOB clob=CLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
							clob.open(CLOB.MODE_READWRITE);
							Writer writer=clob.getCharacterOutputStream();	
							writer.write(myclob.getCharacters());
							writer.flush();
							writer.close();
							clob.close();
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=clob;
						}else{
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=value;
						}
						Methods.set(colType,factory.getColIsGzip(tblName,colName),pstmt,paras);
						index++;
					}
					
					pstmt.execute();
					pstmt.close();
				}
			}else{
				pstmt=connection.prepareStatement(sql);
				
				int index=1;
				for(int i=0;i<cols.size();i++){
					String colName=((Column)cols.get(i)).colName;
					if(JUtilString.containIgnoreCase(primaryKeys,colName)||factory.isColIgnoredWhileUpdating(tblName, colName)){
						continue;
					}
					
					int colType=factory.getColType(tblName,colName);
					Object[] paras=null;	
					Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
					Object value=method.invoke(bean,(Object[])null);
					if(value==null){
						continue;
					}
					
					if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
						InputStream is=(InputStream)value;
						paras=new Object[3];
						paras[0]=new Integer(index);
						paras[1]=is;
						paras[2]=new Integer(is.available());
					}else if(colType==Types.BLOB){
						Blob myBlob=(Blob)value;
						
						BLOB blob=BLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
						blob.open(BLOB.MODE_READWRITE);
						OutputStream os=blob.getBinaryOutputStream();	
						os.write(myBlob.getBytes(0,(int)myBlob.length()));
						os.flush();
						os.close();
						blob.close();
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=blob;
					}else if(colType==Types.CLOB){
						Clob myclob=(Clob)value;
						
						CLOB clob=CLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
						clob.open(CLOB.MODE_READWRITE);
						Writer writer=clob.getCharacterOutputStream();	
						writer.write(myclob.getCharacters());
						writer.flush();
						writer.close();
						clob.close();
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=clob;
					}else{
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=value;
					}
					Methods.set(colType,factory.getColIsGzip(tblName,colName),pstmt,paras);
					index++;
				}
				
				pstmt.execute();
				pstmt.close();
			}
			if(factory.getPlugin()!=null){
				factory.getPlugin().afterUpdateByKeys(bean, primaryKeys);
			}
		}catch(Exception e){
			try{
				pstmt.close();
			}catch(Exception ex){
			}
			
			throw e;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.dao.DAO#updateByKeysIgnoreNulls(java.lang.String, java.lang.Object, java.lang.String[])
	 */
	public void updateByKeysIgnoreNulls(String tblName,Object bean,String[] primaryKeys)throws Exception{
		PreparedStatement pstmt=null;
		try{
			if(factory.getPlugin()!=null){
				factory.getPlugin().beforeUpdateByKeysIgnoreNulls(bean, primaryKeys);
			}
			if(bean==null){
				throw new Exception("指定的对象为空");
			}
			if(primaryKeys==null||primaryKeys.length==0){
				throw new Exception("没有指定主键");
			}
			
			tblName=factory.getTrueTblName(tblName);
			Class cls=bean.getClass();
			
			//生成sql
			String sql="update "+tblName+" set ";

			List cols=factory.getColumns(tblName);
			for(int i=0;i<cols.size();i++){
				String colName=((Column)cols.get(i)).colName;
				if(JUtilString.containIgnoreCase(primaryKeys,colName)||factory.isColIgnoredWhileUpdating(tblName, colName)){
					continue;
				}
				Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
				Object value=method.invoke(bean,(Object[])null);
				if(value==null){
					continue;
				}
				sql+=colName+"=?,";
			}
			if(sql.indexOf("?")==-1){
				throw new Exception("没有值需要更新！");
			}
			sql=sql.substring(0,sql.length()-1);
			String condition="";
			for(int i=0;i<primaryKeys.length;i++){
				primaryKeys[i]=factory.getColName(tblName,primaryKeys[i]);
				Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(primaryKeys[i]),null);
				Object keyValue=method.invoke(bean,(Object[])null);
				if(keyValue==null){
					throw new Exception("主键值不能为空"+tblName+"->"+primaryKeys[i]);
				}
				if(keyValue instanceof Integer||keyValue instanceof Float||keyValue instanceof Double){
					condition+=primaryKeys[i]+"="+keyValue+" and ";
				}else{
					condition+=primaryKeys[i]+"='"+keyValue+"' and ";
				}
			}
			condition=condition.substring(0,condition.length()-5);
			sql+=" where "+condition;
			//生成sql end	
			if(factory.isSynchronized(tblName)){
				String lock=tblName.toUpperCase().intern();
				synchronized(lock){	
					pstmt=connection.prepareStatement(sql);
					
					int index=1;
					for(int i=0;i<cols.size();i++){
						String colName=((Column)cols.get(i)).colName;
						if(JUtilString.containIgnoreCase(primaryKeys,colName)||factory.isColIgnoredWhileUpdating(tblName, colName)){
							continue;
						}
						
						int colType=factory.getColType(tblName,colName);
						Object[] paras=null;	
						Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
						Object value=method.invoke(bean,(Object[])null);
						if(value==null){
							continue;
						}
						
						if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
							InputStream is=(InputStream)value;
							paras=new Object[3];
							paras[0]=new Integer(index);
							paras[1]=is;
							paras[2]=new Integer(is.available());
						}else if(colType==Types.BLOB){
							Blob myBlob=(Blob)value;
							
							BLOB blob=BLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
							blob.open(BLOB.MODE_READWRITE);
							OutputStream os=blob.getBinaryOutputStream();	
							os.write(myBlob.getBytes(0,(int)myBlob.length()));
							os.flush();
							os.close();
							blob.close();
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=blob;
						}else if(colType==Types.CLOB){
							Clob myclob=(Clob)value;
							
							CLOB clob=CLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
							clob.open(CLOB.MODE_READWRITE);
							Writer writer=clob.getCharacterOutputStream();	
							writer.write(myclob.getCharacters());
							writer.flush();
							writer.close();
							clob.close();
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=clob;
						}else{
							paras=new Object[2];
							paras[0]=new Integer(index);
							paras[1]=value;
						}
						Methods.set(colType,factory.getColIsGzip(tblName,colName),pstmt,paras);
						index++;
					}
					
					pstmt.execute();
					pstmt.close();
				}
			}else{
				pstmt=connection.prepareStatement(sql);
				
				int index=1;
				for(int i=0;i<cols.size();i++){
					String colName=((Column)cols.get(i)).colName;
					if(JUtilString.containIgnoreCase(primaryKeys,colName)||factory.isColIgnoredWhileUpdating(tblName, colName)){
						continue;
					}
					
					int colType=factory.getColType(tblName,colName);
					Object[] paras=null;	
					Method method=JUtilBean.getGetter(cls,JUtilBean.colNameToVariableName(colName),null);
					Object value=method.invoke(bean,(Object[])null);
					if(value==null){
						continue;
					}
					
					if(colType==Types.BINARY||colType==Types.LONGVARBINARY||colType==Types.VARBINARY){
						InputStream is=(InputStream)value;
						paras=new Object[3];
						paras[0]=new Integer(index);
						paras[1]=is;
						paras[2]=new Integer(is.available());
					}else if(colType==Types.BLOB){
						Blob myBlob=(Blob)value;
						
						BLOB blob=BLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
						blob.open(BLOB.MODE_READWRITE);
						OutputStream os=blob.getBinaryOutputStream();	
						os.write(myBlob.getBytes(0,(int)myBlob.length()));
						os.flush();
						os.close();
						blob.close();
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=blob;
					}else if(colType==Types.CLOB){
						Clob myclob=(Clob)value;
						
						CLOB clob=CLOB.createTemporary(connection,true,BLOB.DURATION_CALL);
						clob.open(CLOB.MODE_READWRITE);
						Writer writer=clob.getCharacterOutputStream();	
						writer.write(myclob.getCharacters());
						writer.flush();
						writer.close();
						clob.close();
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=clob;
					}else{
						paras=new Object[2];
						paras[0]=new Integer(index);
						paras[1]=value;
					}
					Methods.set(colType,factory.getColIsGzip(tblName,colName),pstmt,paras);
					index++;
				}
				
				pstmt.execute();
				pstmt.close();
			}
			if(factory.getPlugin()!=null){
				factory.getPlugin().afterUpdateByKeysIgnoreNulls(bean, primaryKeys);
			}
		}catch(Exception e){
			try{
				pstmt.close();
			}catch(Exception ex){
			}
			throw e;
		}
	}	
}
