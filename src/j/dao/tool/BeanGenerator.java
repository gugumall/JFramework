/*
 * Created on 2005-5-20
 *
 */
package j.dao.tool;

import j.dao.Column;
import j.dao.DAO;
import j.dao.util.SQLUtil;
import j.sys.SysUtil;
import j.util.JUtilBean;
import j.util.JUtilString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author JFramework
 *
 * 根据数据库的表生成与表对应的Bean
 */
public class BeanGenerator {
	private static Map dialects=new HashMap();
	private static Map drivers=new HashMap();
	private String dialect;//指定数据库类型
	private String url;//
	private String user;//
	private String pass;// 
	
	
	static{			
		dialects.put(DAO.DB_TYPE_MYSQL,"j.dao.dialect.MysqlDialect");	
		dialects.put(DAO.DB_TYPE_SQLITE,"j.dao.dialect.SQLiteDialect");		
		dialects.put(DAO.DB_TYPE_DB2,"j.dao.dialect.DB2Dialect");		
		dialects.put(DAO.DB_TYPE_ORACLE,"j.dao.dialect.OracleDialect");		
		dialects.put(DAO.DB_TYPE_SQLSERVER,"j.dao.dialect.SqlServerDialect");
		dialects.put(DAO.DB_TYPE_HSQL,"j.dao.dialect.HSQLDialect");	

		drivers.put(DAO.DB_TYPE_MYSQL,"com.mysql.jdbc.Driver");	
		drivers.put(DAO.DB_TYPE_SQLITE,"org.sqlite.JDBC");		
		drivers.put(DAO.DB_TYPE_DB2,"COM.ibm.db2.jdbc.app.DB2Driver");		
		drivers.put(DAO.DB_TYPE_ORACLE,"oracle.jdbc.driver.OracleDriver");		
		drivers.put(DAO.DB_TYPE_SQLSERVER,"com.microsoft.jdbc.sqlserver.SQLServerDriver");
		drivers.put(DAO.DB_TYPE_HSQL,"org.hsqldb.jdbcDriver");	
	}
	
	/**
	 * 
	 * @param dialect
	 * @param url
	 * @param user
	 * @param pass
	 * @param useHbt
	 */
	public BeanGenerator(String dialect,String url,String user,String pass,boolean useHbt){
		this.dialect=dialect;
		this.url=url;
		this.user=user;
		this.pass=pass;
	}
	
	/**
	 * 
	 * @param dao DAO
	 * @param catalogPattern 编目
	 * @param schemePattern 模式
	 * @param tablePattern 表名
	 * @param packageName 生成类的包名
	 * @param savePath 保存路径
	 * @throws Exception
	 */
	protected void generateBean(
			DAO dao,
			String dbName,
			String catalogPattern,
			String schemePattern,
			String tablePattern,
			String packageName,
			String savePath,
			boolean useUtf8)throws Exception{
		
		
		if(dialect.equals(DAO.DB_TYPE_MYSQL)){
			if(useUtf8){
				this.url+="?useUnicode=true&characterEncoding=utf-8";
			}else{
				this.url+="?useUnicode=true&characterEncoding=gbk";
			}
		}else{
			//
		}
		
//		if(dialect.equals(DAO.DB_TYPE_MYSQL)){
//			if(useUtf8){
//				this.url+="?useUnicode=true&characterEncoding=utf-8";
//			}else{
//				this.url+="?useUnicode=true&characterEncoding=gbk";
//			}
//		}else{
//			
//		}
		
		//得到类文件的真实存储路径
		savePath=calRealPath(savePath,packageName);
		
		//得到指定数据库中的表
        List tables=new LinkedList();
        if(this.url.toUpperCase().indexOf("SQLITE")<0){
        	tables=dao.getTables(catalogPattern,
	        		schemePattern,
					tablePattern,
					new String[]{"TABLE","VIEW"});
        }
        
		String confStr="<?xml version='1.0' encoding='UTF-8'?>\r\n";
		confStr+="<!-- <!DOCTYPE hibernate-configuration PUBLIC\r\n";
		confStr+="\t\t\"-//Hibernate/Hibernate Configuration DTD 3.0//EN\"\r\n";
		confStr+="\t\t\"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd\">-->\r\n";
		confStr+="<hibernate-configuration>\r\n";
		confStr+="\t<session-factory>\r\n";
		confStr+="\t\t<!-- <property name=\"plugin\">j.dao.DAOPlugin4PrintSQL</property> -->\r\n";
		confStr+="\t\t<property name=\"dialect\">DB_DIALECT</property>\r\n";
		confStr+="\t\t<!-- <property name=\"connection.provider_class\">j.dao.connection.DriverManagerConnectionProvider</property> -->\r\n";	
		confStr+="\t\t<property name=\"connection.provider_class\">j.dao.connection.C3P0ConnectionProvider</property>\r\n";
		confStr+="\t\t<property name=\"connection.pool_size\">15</property>\r\n";
		confStr+="\r\n";	
		confStr+="\t\t<property name=\"c3p0.initialPoolSize\">15</property>\r\n";
		confStr+="\t\t<property name=\"c3p0.maxPoolSize\">300</property>\r\n";
		confStr+="\t\t<property name=\"c3p0.idleConnectionTestPeriod\">60</property>\r\n";
		confStr+="\t\t<property name=\"c3p0.maxIdleTime\">1800</property>\r\n";
		confStr+="\r\n";
		confStr+="\t\t<!-- <property name=\"dbcp.maxActive\">300</property> -->\r\n";
		confStr+="\t\t<!-- <property name=\"dbcp.validationQuery\">SELECT 1</property> -->\r\n";
		confStr+="\t\t<!-- <property name=\"dbcp.timeBetweenEvictionRunsMillis\">60000</property> -->\r\n";
		confStr+="\t\t<!-- <property name=\"dbcp.minEvictableIdleTimeMillis\">1800000</property> -->\r\n";
		confStr+="\r\n"; 
		confStr+="\t\t<!-- <property name=\"connection.datasource\">java:comp/env/jdbc/test</property> -->\r\n";	
		confStr+="\r\n";	
		confStr+="\t\t<!-- <property name=\"proxool.xml\">PROXOOL_XML</property> -->\r\n";
		confStr+="\t\t<!-- <property name=\"proxool.properties\">PROXOOL_PROPERTIES</property> -->\r\n";
		confStr+="\t\t<!-- <property name=\"proxool.pool_alias\">DBPool</property> -->\r\n";
		
		confStr+="\t\t<property name=\"connection.driver_class\">DB_DRIVER</property>\r\n";			
		confStr+="\t\t<property name=\"connection.url\">DB_URL</property>\r\n";		
		confStr+="\t\t<property name=\"connection.username\">DB_USER</property>\r\n";
		confStr+="\t\t<property name=\"connection.password\">DB_PASS</property>\r\n";
		
		confStr+="\t\t<property name=\"show_sql\">false</property>\r\n";
				
		this.url=JUtilString.replaceAll(this.url,"&","&amp;");
		confStr=confStr.replaceAll("DB_URL",this.url);
		confStr=confStr.replaceAll("DB_USER",this.user);
		confStr=confStr.replaceAll("DB_PASS",this.pass);
		confStr=confStr.replaceAll("DB_DRIVER",(String)drivers.get(this.dialect));
		confStr=confStr.replaceAll("DB_DIALECT",(String)dialects.get(this.dialect));
		confStr=confStr.replaceAll("DB_PASS",this.pass);
		confStr=confStr.replaceAll("PROXOOL_XML",JUtilString.replaceAll(packageName,".","/")+"/proxool.xml");
		confStr=confStr.replaceAll("PROXOOL_PROPERTIES",JUtilString.replaceAll(packageName,".","/")+"/proxool.properties");
		
		//遍历每个表
		for(int i=0;i<tables.size();i++){
			String tblName=(String)tables.get(i);//表名
			confStr+="\t\t<mapping resource=\""+JUtilString.replaceAll(packageName,".","/")+"/"+JUtilBean.dbNameToVariableName(tblName)+".xml\" />\r\n";
		}
		confStr+="\t</session-factory>\r\n";
		confStr+="</hibernate-configuration>\r\n";
		
		
		//写入配置文件
		File file = new File(savePath+"datasource.cfg.xml");
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		if(file.exists()){
			file.delete();
		}		
		Writer writer=new OutputStreamWriter(new FileOutputStream(file));
		writer.write(confStr);
		writer.flush();
		writer.close();
		//写入配置文件 ends	
		
		//创建时间
		String time=(new Timestamp(SysUtil.getNow())).toString().substring(0,10);
		
		//遍历每个表
		for(int i=0;i<tables.size();i++){
			String tblName=(String)tables.get(i);//表名
			String tmpConfStr="<?xml version=\"1.0\"?>\r\n";
			tmpConfStr+="<!-- <!DOCTYPE hibernate-mapping PUBLIC\r\n";
			tmpConfStr+="\t\t\"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\r\n";
			tmpConfStr+="\t\t\"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\" >-->\r\n";
			tmpConfStr+="<hibernate-mapping>\r\n";
			
			try{
				Column[] pks=dao.getPrimaryKeyColumns(tblName);
				if(pks==null||pks.length!=1){
					throw new Exception("表没有定义单一主键:"+tblName);
				}
				String[] pkNames=new String[pks.length];
				for(int p=0;pks!=null&&p<pks.length;p++){
					pkNames[p]=pks[p].colName;
				}
				String clsName=JUtilBean.dbNameToVariableName(tblName);//类名
				
				tmpConfStr+="<class";
				tmpConfStr+=" name=\""+packageName+"."+clsName+"\" "; 
				tmpConfStr+=" table=\""+tblName+"\">\r\n";
				
				String classContent="";//类源文件的内容
								
				//创建时间
				classContent+="/*\r\n";
				classContent+=" * Created on "+time+"\r\n";
				classContent+=" *\r\n";
				classContent+=" */\r\n";
				//创建时间 ends
				
				classContent+="package "+packageName+";\r\n\r\n\r\n";//包名
				
				//配置文件内容
				System.out.println(tblName+" 主键长度："+pks.length);
				
				if(pks!=null&&pks.length==1&&(pks[0].colType==Types.BIGINT
						||pks[0].colType==Types.INTEGER
						||pks[0].colType==Types.SMALLINT
						||pks[0].colType==Types.NUMERIC
						||pks[0].colType==Types.SMALLINT
						||pks[0].colType==Types.TINYINT
						||pks[0].colType==Types.DECIMAL
						||pks[0].colType==Types.VARCHAR
						||pks[0].colType==Types.CHAR)){
					tmpConfStr+="<id";
					tmpConfStr+=" name=\""+JUtilBean.colNameToVariableName(pks[0].colName)+"\"";
					
					tmpConfStr+=" type=\""+SQLUtil.getJavaTypeName(pks[0].colType)+"\"";
					
					tmpConfStr+=" column=\""+pks[0].colName+"\">\r\n";
					tmpConfStr+="\t<generator class=\"assigned\" />\r\n";
					tmpConfStr+="</id>\r\n\r\n";
				}
				
				List cols=dao.getColumns(tblName);//得到表的所有列
				
				classContent+="import java.io.Serializable;\r\n";//实现Serializable所需要import的类
				classContent+="\r\n\r\n";//回车
				
				//作者
				classContent+="/**\r\n";
				classContent+=" * @author JFramework-BeanGenerator\r\n";
				classContent+=" *\r\n";
				classContent+=" */\r\n";
				//作者 ends
				
				//类头
				classContent+="public class "+clsName+" implements Serializable{\r\n\r\n";
				//classContent+="\tprivate static final long serialVersionUID = 1L;\r\n\r\n";
				
				//遍历每个列,得到成员变量
				for(int j=0;j<cols.size();j++){
					Column col=(Column)cols.get(j);//一列
					String type=SQLUtil.getJavaTypeName(col.colType);
					if(type==null){
						throw new Exception("暂时不支持的字段类型："+col.colName+"<>"+col.colType+"<>"+tblName);
					}
					classContent+="\tprivate "+type+" "+JUtilBean.colNameToVariableName(col.colName)+";\r\n";//成员变量
					


					tmpConfStr+="<property";
					tmpConfStr+=" name=\""+JUtilBean.colNameToVariableName(col.colName)+"\"";
					tmpConfStr+=" type=\""+type+"\"";
					tmpConfStr+=" column=\""+col.colName+"\"";
					tmpConfStr+=" not-null=\""+col.notNull+"\"";
					tmpConfStr+=" length=\""+col.length+"\"/>\r\n\r\n";
				}//遍历每个列,得到成员变量 ends
				
				classContent+="\r\n";//回车
				
				//遍历每个列,得到getter , setter
				for(int j=0;j<cols.size();j++){
					Column col=(Column)cols.get(j);//一列
					String type=SQLUtil.getJavaTypeName(col.colType);
					if(type==null){
						throw new Exception("暂时不支持的字段类型："+col.colName+"<>"+col.colType+"<>"+tblName);
					}
					
					//getter
					classContent+="\tpublic "+type+" get"+JUtilBean.upperFirstChar(JUtilBean.dbNameToVariableName(col.colName))+"(){\r\n";
					classContent+="\t\treturn this."+JUtilBean.colNameToVariableName(col.colName)+";";
					classContent+="\r\n\t}\r\n";
					//getter ends
					
					//setter
					classContent+="\tpublic void set"+JUtilBean.upperFirstChar(JUtilBean.dbNameToVariableName(col.colName))+"(";
					classContent+=type+" "+JUtilBean.colNameToVariableName(col.colName);
					classContent+="){\r\n";
					classContent+="\t\tthis."+JUtilBean.colNameToVariableName(col.colName)+"="+JUtilBean.colNameToVariableName(col.colName)+";\r\n";
					classContent+="\t}\r\n\r\n";
					//setter ends
				}//遍历每个列,得到getter , setter ends
				
				classContent+="\tpublic boolean equals(Object obj){\r\n";
				classContent+="\t\treturn super.equals(obj);\r\n";
				classContent+="\t}\r\n\r\n";
				
				classContent+="\tpublic int hashCode(){\r\n";
				classContent+="\t\treturn super.hashCode();\r\n";
				classContent+="\t}\r\n\r\n";
				
				classContent+="\tpublic String toString(){\r\n";
				classContent+="\t\treturn super.toString();\r\n";
				classContent+="\t}\r\n\r\n";
				
				classContent+="}\r\n";//类内容结束
				
				//如果路径不存在，创建路径
				File path=new File(savePath);
				if(!path.exists()){
					path.mkdirs();
				}//如果路径不存在，创建路径 ends
				
				//写入文件
				file = new File(savePath+clsName+".java");
				if(file.exists()){
					file.delete();
				}
				writer = null;
				if(useUtf8){
					writer=new OutputStreamWriter(new FileOutputStream(file),"utf-8");
				}else{
					writer=new OutputStreamWriter(new FileOutputStream(file));
				}
				writer.write(classContent);
				writer.flush();
				writer.close();
				//写入文件 ends
				

				tmpConfStr+="</class>\r\n";
				tmpConfStr+="</hibernate-mapping>\r\n";
				//写入文件
				file = new File(savePath+clsName+".xml");
				if(file.exists()){
					file.delete();
				}
				writer = null;
				if(useUtf8){
					writer=new OutputStreamWriter(new FileOutputStream(file),"utf-8");
				}else{
					writer=new OutputStreamWriter(new FileOutputStream(file));
				}
				writer.write(tmpConfStr);
				writer.flush();
				writer.close();
				//写入文件 ends				
			}catch(Exception e){
				System.out.println("生成表："+tblName+" 的Bean时出错：\r\n");
				e.printStackTrace();
			}

		}
		//遍历每个表 ends
		
		//写入范例配置文件
		file = new File(savePath+"pool.properties");
		if(file.exists()){
			file.delete();
		}		
		writer=new OutputStreamWriter(new FileOutputStream(file));
		writer.write(pool);
		writer.flush();
		writer.close();
		//写入范例配置文件 ends
		
		//写入proxool配置文件
		String proxool1=proxool;
		proxool1=JUtilString.replaceAll(proxool1,"DB_URL",this.url);
		proxool1=JUtilString.replaceAll(proxool1,"&","&amp;");
		proxool1=JUtilString.replaceAll(proxool1,"DB_USER",this.user);
		proxool1=JUtilString.replaceAll(proxool1,"DB_PASS",this.pass);
		proxool1=JUtilString.replaceAll(proxool1,"DB_DRIVER",drivers.get(dialect).toString());
		file = new File(savePath+"proxool.xml");
		if(file.exists()){
			file.delete();
		}		
		writer=new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
		writer.write(proxool1);
		writer.flush();
		writer.close();
		//写入proxool配置文件 ends		
	}

	
	/**
	 * 拼出类实际存储的位置
	 * 
	 * @param savePath 类存储的根路径
	 * @param packageName 全限定的包名
	 * @return java.lang.String
	 */
	private String calRealPath(String savePath,String packageName){
		String realPath=savePath;//类实际存储的位置
		
		//文件路径的最后一个字符为\或/,去掉这个字符
		if(realPath.endsWith("/")||realPath.endsWith("\\")){
			realPath=realPath.substring(0,realPath.length()-1);
		}//文件路径的最后一个字符为\或/,去掉这个字符 ends
		
		//将文件路径中的\替换成/
		StringBuffer sb = new StringBuffer(realPath);
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '\\') {
				sb.setCharAt(i, '/');
			}
		}
		realPath = sb.toString();
		if(realPath.charAt(realPath.length()-1)!='/'){
			realPath+="/";
		}
		//将文件路径中的\替换成/ ends
		
		String[] packs=JUtilString.getTokens(packageName,".");//分割包名
		//遍历包名的每一段，拼出类实际存储的位置
		for(int i=0;i<packs.length;i++){
			realPath+=packs[i]+"/";
		}//遍历包名的每一段，拼出类实际存储的位置 ends
		return realPath;
	}
	
	private static String pool="";
	static{
		pool+="#################################\r\n";
		pool+="### Connection Pool ###\r\n";
		pool+="#################################\r\n";
		pool+="#hibernate.connection.provider_class j.dao.connection.DriverManagerConnectionProvider\r\n";
		pool+="#hibernate.connection.provider_class j.dao.connection.DatasourceConnectionProvider\r\n";
		pool+="#hibernate.connection.provider_class j.dao.connection.C3P0ConnectionProvider\r\n";
		pool+="#hibernate.connection.provider_class j.dao.connection.DBCPConnectionProvider\r\n";
		pool+="#hibernate.connection.provider_class j.dao.connection.ProxoolConnectionProvider\r\n";
		pool+="#hibernate.connection.pool_size 20\r\n";
		pool+="\r\n";
		pool+="\r\n";
		pool+="## JNDI Datasource\r\n";
		pool+="#hibernate.connection.datasource jdbc/test\r\n";
		pool+="#hibernate.connection.username db2\r\n";
		pool+="#hibernate.connection.password db2\r\n";
		pool+="\r\n";
		pool+="\r\n";
		pool+="## DB2\r\n";
		pool+="#hibernate.dialect net.sf.hibernate.dialect.DB2Dialect\r\n";
		pool+="#hibernate.connection.driver_class COM.ibm.db2.jdbc.app.DB2Driver\r\n";
		pool+="#hibernate.connection.url jdbc:db2:test\r\n";
		pool+="#hibernate.connection.username db2\r\n";
		pool+="#hibernate.connection.password db2\r\n";
		pool+="\r\n";
		pool+="## DB2/400 \r\n";
		pool+="#hibernate.dialect net.sf.hibernate.dialect.DB2400Dialect\r\n";
		pool+="#hibernate.connection.username user\r\n";
		pool+="#hibernate.connection.password password \r\n";
		pool+="\r\n";
		pool+="## Native driver\r\n";
		pool+="#hibernate.connection.driver_class COM.ibm.db2.jdbc.app.DB2Driver\r\n";
		pool+="#hibernate.connection.url jdbc:db2://systemname\r\n";
		pool+="\r\n";
		pool+="\r\n";
		pool+="## MySQL\r\n";
		pool+="#hibernate.dialect net.sf.hibernate.dialect.MySQLDialect\r\n";
		pool+="#hibernate.connection.driver_class org.gjt.mm.mysql.Driver\r\n";
		pool+="#hibernate.connection.driver_class com.mysql.jdbc.Driver\r\n";
		pool+="#hibernate.connection.url jdbc:mysql:///test\r\n";
		pool+="#hibernate.connection.username root\r\n";
		pool+="#hibernate.connection.password \r\n";
		pool+="\r\n";
		pool+="\r\n";
		pool+="## Oracle\r\n";
		pool+="#hibernate.dialect net.sf.hibernate.dialect.Oracle9Dialect\r\n";
		pool+="#hibernate.dialect net.sf.hibernate.dialect.OracleDialect\r\n";
		pool+="#hibernate.connection.driver_class oracle.jdbc.driver.OracleDriver\r\n";
		pool+="#hibernate.connection.username ora\r\n";
		pool+="#hibernate.connection.password ora\r\n";
		pool+="#hibernate.connection.url jdbc:oracle:thin:@localhost:1521:test\r\n";
		pool+="\r\n";
		pool+="\r\n";
		pool+="## MS SQL Server\r\n";
		pool+="#hibernate.dialect net.sf.hibernate.dialect.SQLServerDialect\r\n";
		pool+="#hibernate.connection.username sa\r\n";
		pool+="#hibernate.connection.password sa\r\n";
		pool+="\r\n";
		pool+="## Microsoft Driver (not recommended!)\r\n";
		pool+="#hibernate.connection.driver_class com.microsoft.jdbc.sqlserver.SQLServerDriver\r\n";
		pool+="#hibernate.connection.url jdbc:microsoft:sqlserver://1E1;DatabaseName=test;SelectMethod=cursor\r\n";
		pool+="\r\n";
		pool+="\r\n";
		pool+="\r\n";
		pool+="###########################\r\n";
		pool+="### C3P0 Connection Pool###\r\n";
		pool+="###########################\r\n";
		pool+="#hibernate.c3p0.max_size 2\r\n";
		pool+="#hibernate.c3p0.min_size 2\r\n";
		pool+="#hibernate.c3p0.timeout 5000\r\n";
		pool+="#hibernate.c3p0.max_statements 100\r\n";
		pool+="#hibernate.c3p0.idle_test_period 3000\r\n";
		pool+="#hibernate.c3p0.acquire_increment 2\r\n";
		pool+="##hibernate.c3p0.validate false\r\n";
		pool+="\r\n";
		pool+="\r\n";
		pool+="###################################\r\n";
		pool+="### Apache DBCP Connection Pool ###\r\n";
		pool+="###################################\r\n";
		pool+="#hibernate.dbcp.maxActive 100\r\n";
		pool+="#hibernate.dbcp.whenExhaustedAction 1\r\n";
		pool+="#hibernate.dbcp.maxWait 120000\r\n";
		pool+="#hibernate.dbcp.maxIdle 10\r\n";
		pool+="\r\n";
		pool+="## prepared statement cache\r\n";
		pool+="#hibernate.dbcp.ps.maxActive 100\r\n";
		pool+="#hibernate.dbcp.ps.whenExhaustedAction 1\r\n";
		pool+="#hibernate.dbcp.ps.maxWait 120000\r\n";
		pool+="#hibernate.dbcp.ps.maxIdle 10\r\n";
		pool+="\r\n";
		pool+="\r\n";
		pool+="##############################\r\n";
		pool+="### Proxool Connection Pool###\r\n";
		pool+="##############################\r\n";
		pool+="## Properties for external configuration of Proxool\r\n";
		pool+="#hibernate.proxool.pool_alias pool1\r\n";
		pool+="\r\n";
		pool+="## Only need one of the following\r\n";
		pool+="#hibernate.proxool.existing_pool true\r\n";
		pool+="#hibernate.proxool.xml proxool.xml\r\n";
		pool+="#hibernate.proxool.properties proxool.properties\r\n";
		pool+="\r\n";
	}
	
	private static String proxool="";
	static{
		proxool+="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
		proxool+="<something-else-entirely>\r\n";
		proxool+="	<proxool>\r\n";
		proxool+="		<alias>DBPool</alias>\r\n";
		proxool+="		<driver-url>DB_URL</driver-url>\r\n";
		proxool+="		<driver-class>DB_DRIVER</driver-class>\r\n";
		proxool+="\r\n";
		proxool+="		<driver-properties>\r\n";
		proxool+="			<property name=\"user\" value=\"DB_USER\" />\r\n";
		proxool+="			<property name=\"password\" value=\"DB_PASS\" />\r\n";
		proxool+="		</driver-properties>\r\n";
		proxool+="		<maximum-connection-count>20</maximum-connection-count>\r\n";
		proxool+="	</proxool>\r\n";
		proxool+="</something-else-entirely>\r\n";
	}
}
