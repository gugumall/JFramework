/*
 * Created on 2005-2-19
 *
 */
package j.dao.tool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;

import j.dao.DAO;
import j.dao.DAOFactory;


/**
 * @author JFramework
 *
 */
public class BeanGen {
	private static Map DBMS_DRIVER=new HashMap();
	private static Map DBMS_URL_PATTERN=new HashMap();
	private static Map DBMS_DIALECT=new HashMap();
	private BeanGenGUI gui;
	 
	/**
	 * 
	 */
	static{
		DBMS_DRIVER.put("MySQL","com.mysql.jdbc.Driver");
		DBMS_DRIVER.put("SQLITE","org.sqlite.JDBC");
		DBMS_DRIVER.put("IBM DB2(net)","COM.ibm.db2.jdbc.net.DB2Driver");
		DBMS_DRIVER.put("IBM DB2(jcc)","com.ibm.db2.jcc.DB2Driver");
		DBMS_DRIVER.put("IBM DB2(app)","COM.ibm.db2.jdbc.app.DB2Driver");
		DBMS_DRIVER.put("ORACLE","oracle.jdbc.driver.OracleDriver");
		DBMS_DRIVER.put("SQL Server","com.microsoft.jdbc.sqlserver.SQLServerDriver");
		DBMS_DRIVER.put("HSQL inMemory","org.hsqldb.jdbcDriver");
		DBMS_DRIVER.put("HSQL stand-alone","org.hsqldb.jdbcDriver");
		DBMS_DRIVER.put("HSQL server","org.hsqldb.jdbcDriver");
		DBMS_DRIVER.put("HSQL webserver","org.hsqldb.jdbcDriver");

		DBMS_URL_PATTERN.put("MySQL","jdbc:mysql://HOST/DATABASE?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Hongkong");
		DBMS_URL_PATTERN.put("SQLITE","jdbc:sqlite:DATABASE");
		DBMS_URL_PATTERN.put("IBM DB2(net)","jdbc:db2://HOST/DATABASE");
		DBMS_URL_PATTERN.put("IBM DB2(jcc)","jdbc:db2://HOST/DATABASE");
		DBMS_URL_PATTERN.put("IBM DB2(app)","jdbc:db2:DATABASE");		
		DBMS_URL_PATTERN.put("ORACLE","jdbc:oracle:thin:@HOST:DATABASE");
		DBMS_URL_PATTERN.put("SQL Server","jdbc:microsoft:sqlserver://HOST;DatabaseName=DATABASE");
		DBMS_URL_PATTERN.put("HSQL inMemory","jdbc:hsqldb:mem:DATABASE");
		DBMS_URL_PATTERN.put("HSQL stand-alone","jdbc:hsqldb:file:DATABASE");
		DBMS_URL_PATTERN.put("HSQL server","jdbc:hsqldb:hsql://HOST/DATABASE");
		DBMS_URL_PATTERN.put("HSQL webserver","jdbc:hsqldb:http://HOST");

		DBMS_DIALECT.put("MySQL",DAO.DB_TYPE_MYSQL);
		DBMS_DIALECT.put("SQLITE",DAO.DB_TYPE_SQLITE);
		DBMS_DIALECT.put("IBM DB2(net)",DAO.DB_TYPE_DB2);
		DBMS_DIALECT.put("IBM DB2(jcc)",DAO.DB_TYPE_DB2);
		DBMS_DIALECT.put("IBM DB2(app)",DAO.DB_TYPE_DB2);
		DBMS_DIALECT.put("ORACLE",DAO.DB_TYPE_ORACLE);
		DBMS_DIALECT.put("SQL Server",DAO.DB_TYPE_SQLSERVER);
		DBMS_DIALECT.put("HSQL inMemory",DAO.DB_TYPE_HSQL);
		DBMS_DIALECT.put("HSQL stand-alone",DAO.DB_TYPE_HSQL);
		DBMS_DIALECT.put("HSQL server",DAO.DB_TYPE_HSQL);
		DBMS_DIALECT.put("HSQL webserver",DAO.DB_TYPE_HSQL);
	}
	
	/**
	 * 
	 *
	 */
	public BeanGen(){		
		this.gui=new BeanGenGUI();
		
		Object[] dbmsTypes=new Object[]{
				"Select DBMS",
				"MySQL",
				"SQLITE",
				"IBM DB2(net)",
				"IBM DB2(jcc)",
				"IBM DB2(app)",
				"ORACLE",
				"SQL Server",
				"HSQL inMemory",
				"HSQL stand-alone",
				"HSQL server",
				"HSQL webserver"
		};
		ComboBoxModel dbms=new DefaultComboBoxModel(dbmsTypes);
		
		gui.dbms.setModel(dbms);
		
		init();
	}
	
	/**
	 * 
	 *
	 */
	private void init(){
		this.gui.connect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				connect();
			}
		});
		
		this.gui.choose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				choosePath();
			}
		});
		
		this.gui.gen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				gen();
			}
		});
		
		this.gui.showTables.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				showTable();
			}
		});
		
		this.gui.db.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
				if(gui.dbms.getSelectedIndex()==0){
					return;
				}
				String dbms=gui.dbms.getSelectedItem().toString();
				String url=BeanGen.DBMS_URL_PATTERN.get(dbms).toString();
				url=url.replaceAll("HOST",gui.host.getText().trim());
				url=url.replaceAll("DATABASE",gui.db.getText().trim());
				
				gui.info.setText(url);
			}
			public void keyTyped(KeyEvent e) {
			}
		});
		
		this.gui.host.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
				if(gui.dbms.getSelectedIndex()==0){
					return;
				}
				String dbms=gui.dbms.getSelectedItem().toString();
				String url=BeanGen.DBMS_URL_PATTERN.get(dbms).toString();
				url=url.replaceAll("HOST",gui.host.getText().trim());
				url=url.replaceAll("DATABASE",gui.db.getText().trim());
				
				gui.info.setText(url);
			}
			public void keyTyped(KeyEvent e) {
			}
		});
	}
	
	/**
	 * 连接数据库
	 *
	 */
	private void connect(){
		this.gui.tables.setModel(new DefaultListModel());
		this.gui.catalogs.setModel(new DefaultComboBoxModel());
		this.gui.schemes.setModel(new DefaultComboBoxModel());
		this.gui.showTables.setEnabled(false);
		this.gui.gen.setEnabled(false);
		
		if(this.gui.dbms.getSelectedIndex()==0){
			this.gui.info.setText("No DBMS Selected!");
			return;
		}
		if(this.gui.host.getText().trim().equals("")){
			//this.gui.info.setText("Please Enter The Host Address!");
			//return;
		}
		if(this.gui.user.getText().trim().equals("")){
			//this.gui.info.setText("Please Enter User Name!");
			//return;
		}
		if(this.gui.pass.getText().trim().equals("")){
			//this.gui.info.setText("Please Enter The Password!");
			//return;
		}
		if(this.gui.db.getText().trim().equals("")){
			this.gui.info.setText("Please Enter The Database Name!");
			return;
		}
		
		String dbms=this.gui.dbms.getSelectedItem().toString();
		String driver=BeanGen.DBMS_DRIVER.get(dbms).toString();
		String url=BeanGen.DBMS_URL_PATTERN.get(dbms).toString();
		url=url.replaceAll("HOST",this.gui.host.getText().trim());
		url=url.replaceAll("DATABASE",this.gui.db.getText().trim());
		dbms=(String)BeanGen.DBMS_DIALECT.get(dbms);
		this.gui.info.setText(url);
		
		try{
			DAOFactory factory=DAOFactory.getSimpleInstance(this.gui.db.getText().trim(),dbms,driver,url,this.gui.user.getText().trim(),this.gui.pass.getText().trim());
			factory.setMaxPoolSize(50);
			factory.setConnectionProviderClass("j.dao.connection.DefaultConnectionProvider");
			this.gui.dao=factory.createDAO(BeanGen.class,3600000);
		
			if(!"SQLITE".equalsIgnoreCase(dbms)){
				List catas=gui.dao.getCatalogs();
				List schemes=gui.dao.getSchemas();
				if(catas!=null&&catas.size()>0){
					Object[] cataO=new Object[catas.size()+1];
					cataO[0]="Select Catalog";
					for(int i=0;i<catas.size();i++){
						cataO[i+1]=catas.get(i).toString();
					}
					ComboBoxModel cataC=new DefaultComboBoxModel(cataO);
					this.gui.catalogs.setModel(cataC);
				}
				if(schemes!=null&&schemes.size()>0){
					Object[] schemeO=new Object[schemes.size()+1];
					schemeO[0]="Select Scheme";
					for(int i=0;i<schemes.size();i++){
						schemeO[i+1]=schemes.get(i).toString();
					}
					ComboBoxModel schemeC=new DefaultComboBoxModel(schemeO);
					this.gui.schemes.setModel(schemeC);
				}
			}
			this.gui.info.setText("Connect To Db Successfully!");
			this.gui.showTables.setEnabled(true);
		}catch(Exception e){
			e.printStackTrace();
			this.gui.tables.setModel(new DefaultListModel());
			this.gui.dao=null;
			this.gui.info.setText("Connect To Db Failed!");
		}		
	}
	
	/**
	 * 显示表
	 *
	 */
	private void showTable(){
		try{
			if(this.gui.dao==null){
				this.gui.info.setText("Please Connect First!");
				return;
			}
			String cata=null;
			String scheme=null;
			if(this.gui.catalogs.getSelectedIndex()!=-1){
				if(this.gui.catalogs.getSelectedIndex()==0){
					//this.gui.info.setText("Please Select Catalog!");
					//return;
				}else{
					cata=this.gui.catalogs.getSelectedItem().toString();
				}				
			}
			
			if(this.gui.schemes.getSelectedIndex()!=-1){
				if(this.gui.schemes.getSelectedIndex()==0){
					//this.gui.info.setText("Please Select Scheme!");
					//return;
				}else{
					scheme=this.gui.schemes.getSelectedItem().toString();
				}				
			}

			String dbms=this.gui.dbms.getSelectedItem().toString();
			if(!"SQLITE".equalsIgnoreCase(dbms)){
				List tables=this.gui.dao.getTables(cata,scheme,null,new String[]{"TABLE","VIEW"});
				DefaultListModel tablesList=new DefaultListModel();
				for(int i=0;tables!=null&&i<tables.size();i++){
					tablesList.add(i,tables.get(i));
				}
				this.gui.tables.setModel(tablesList);
			}
			this.gui.info.setText("Show Tables Successfully!");
			this.gui.gen.setEnabled(true);
		}catch(Exception ex){
			this.gui.tables.setModel(new DefaultListModel());
			this.gui.dao=null;
			this.gui.info.setText("Sorry,Error Occur!");
		}
	}
	
	/**
	 * 选择文件路径
	 *
	 */
	private void choosePath(){
		int ok=this.gui.fileChooser.showDialog(this.gui,"OK");
		if(ok==JFileChooser.APPROVE_OPTION){
			this.gui.path.setText(this.gui.fileChooser.getSelectedFile().getAbsolutePath());
		}
	}
	
	/**
	 * 生成Bean
	 *
	 */
	private void gen(){
		if(this.gui.path.getText().equals("")){
			this.gui.info.setText("Please Choose The Path Beans Saved To!");
			return;
		}
		if(this.gui.pack.getText().trim().equals("")){
			this.gui.info.setText("Please Enter The Package Name!");
			return;
		}
		String dialect=(String)BeanGen.DBMS_DIALECT.get(this.gui.dbms.getSelectedItem().toString());

		String dbms=this.gui.dbms.getSelectedItem().toString();
		String url=BeanGen.DBMS_URL_PATTERN.get(dbms).toString();
		url=url.replaceAll("HOST",this.gui.host.getText().trim());
		url=url.replaceAll("DATABASE",this.gui.db.getText().trim());
		dbms=(String)BeanGen.DBMS_DIALECT.get(dbms);
	
		BeanGenerator gen=new BeanGenerator(dialect,url,this.gui.user.getText(),this.gui.pass.getText(),gui.useHbt.isSelected());
		String cata=null;
		String scheme=null;
		if(this.gui.catalogs.getSelectedIndex()!=-1
				&&this.gui.catalogs.getSelectedIndex()!=0){
			cata=this.gui.catalogs.getSelectedItem().toString();
		}
		
		if(this.gui.schemes.getSelectedIndex()!=-1
				&&this.gui.schemes.getSelectedIndex()!=0){
			scheme=this.gui.schemes.getSelectedItem().toString();				
		}
		try{
			this.gui.gen.setEnabled(false);
			if(this.gui.useUtf8.isSelected()){
			gen.generateBean(
					this.gui.dao,
					this.gui.db.getText().trim(),
					cata,
					scheme,
					null,
					this.gui.pack.getText().trim(),
					this.gui.path.getText(),
					true);
			}else{
				gen.generateBean(
						this.gui.dao,
						this.gui.db.getText().trim(),
						cata,
						scheme,
						null,
						this.gui.pack.getText().trim(),
						this.gui.path.getText(),
						false);
			}
		}catch(Exception e){
			this.gui.info.setText("Generating Beans Failed!");
			e.printStackTrace();
		}
		this.gui.gen.setEnabled(true);
		this.gui.info.setText("Beans Generated Successfully!");
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		System.out.println(System.getProperty("java.library.path"));
		BeanGen tool=new BeanGen();
		tool.gui.showGUI();
	} 
	
}