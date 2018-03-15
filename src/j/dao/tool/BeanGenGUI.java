/*
 * Created on 2005-5-20
 *
 */
package j.dao.tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import j.dao.DAO;

/**
 * @author JFramework
 *
 */
public class BeanGenGUI  extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JPanel hostPanel;
	protected JLabel label4Dbms;
	protected JComboBox dbms;
	protected JLabel label4Host;
	protected JTextField host;
	
	protected JPanel userPanel;
	protected JLabel label4User;
	protected JTextField user;
	protected JLabel label4Pass;
	protected JTextField pass;
	
	protected JPanel cataPanel;
	protected JLabel label4Cata;
	protected JComboBox catalogs;
	protected JLabel label4Scheme;
	protected JComboBox schemes;
	
	protected JFileChooser fileChooser;
	protected JButton choose;
	protected JTextField path;
	protected JPanel pathPanel;
	
	protected JLabel label4Db;
	protected JTextField db;
	protected JButton connect;	
	protected JPanel dbPanel;
	
	protected JList tables;
	
	protected JButton gen;
	protected JRadioButton useHbt;
	protected JRadioButton useUtf8;
	protected JPanel buttonPanel;
	
	protected JButton showTables;
	
	protected JTextArea info;	
	
	protected JLabel label4Pack;
	protected JTextField pack;
	protected JPanel packPanel;

	protected DAO dao;
	
	/**
	 * 构造函数
	 *
	 */
	public BeanGenGUI(){
		super();
		this.initHostPanel();
		this.initCataPanel();
		this.initUserPanel();
		this.initPathPanel();
		this.initDBPanel();
		this.initPackPanel();
		this.initButtonPanel();
		this.initGUI();
	}
	
	/**
	 * 数据库系统、主机面板
	 *
	 */
	private void initHostPanel(){
		Color fontColor=new Color(249,96,51);//字体颜色
		Color bg=Color.WHITE;//背景颜色
		Dimension buttonSize=new Dimension(140,30);
		
		/*
		 * 数据库系统
		 */
		this.dbms=new JComboBox();
		this.dbms.setBackground(bg);
		this.dbms.setForeground(fontColor);
		this.dbms.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//鼠标形状
		this.dbms.setPreferredSize(buttonSize);	
		
		this.label4Dbms=new JLabel("DBMS");
		
		/*
		 * 主机
		 */
		this.host=new JTextField();
		this.host.setBackground(bg);
		this.host.setForeground(fontColor);
		this.host.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//鼠标形状
		this.host.setPreferredSize(buttonSize);
		
		this.label4Host=new JLabel("HOST");
		
		/*
		 * 数据库系统、主机面板
		 */
		this.hostPanel=new JPanel();//面板
		Dimension size=new Dimension(400,34);//面板大小
		this.hostPanel.setPreferredSize(size);		
		
		FlowLayout layout=new FlowLayout(FlowLayout.CENTER,10,5);//流式布局
		this.hostPanel.setLayout(layout);//设置布局
		
		this.hostPanel.add(this.label4Dbms);
		this.hostPanel.add(this.dbms);
		this.hostPanel.add(this.label4Host);
		this.hostPanel.add(this.host);		
	}
	
	/**
	 * 包名面板
	 *
	 */
	private void initPackPanel(){
		Color fontColor=new Color(249,96,51);//字体颜色
		Color bg=Color.WHITE;//背景颜色
		Dimension buttonSize=new Dimension(340,30);
		
		/*
		 * 包名
		 */
		this.pack=new JTextField();
		this.pack.setBackground(bg);
		this.pack.setForeground(fontColor);
		this.pack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//鼠标形状
		this.pack.setPreferredSize(buttonSize);	
		
		this.label4Pack=new JLabel("PACK");
		
		/*
		 * 包名面板
		 */
		this.packPanel=new JPanel();//面板
		Dimension size=new Dimension(400,34);//面板大小
		this.packPanel.setPreferredSize(size);		
		
		FlowLayout layout=new FlowLayout(FlowLayout.CENTER,10,5);//流式布局
		this.packPanel.setLayout(layout);//设置布局
		
		this.packPanel.add(this.label4Pack);
		this.packPanel.add(this.pack);
	}
	
	/**
	 * 
	 *
	 */
	private void initButtonPanel(){
		this.gen=new JButton("GENERATE");
		this.useHbt=new JRadioButton("使用Hibernate？");
		this.useUtf8=new JRadioButton("使用UTF-8？");
		
		
		/*
		 * 包名面板
		 */
		this.buttonPanel=new JPanel();//面板
		Dimension size=new Dimension(400,34);//面板大小
		this.buttonPanel.setPreferredSize(size);		
		
		FlowLayout layout=new FlowLayout(FlowLayout.CENTER,10,5);//流式布局
		this.buttonPanel.setLayout(layout);//设置布局
		
		this.buttonPanel.add(this.useHbt);
		this.buttonPanel.add(this.useUtf8);
		this.buttonPanel.add(this.gen);
	}
	
	/**
	 * 用户名、密码面板
	 *
	 */
	private void initUserPanel(){
		Color fontColor=new Color(249,96,51);//字体颜色
		Color bg=Color.WHITE;//背景颜色
		Dimension cellSize=new Dimension(140,30);		
		/*
		 * 用户名
		 */
		this.user=new JTextField();
		this.user.setBackground(bg);
		this.user.setForeground(fontColor);
		this.user.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//鼠标形状
		this.user.setPreferredSize(cellSize);	
		
		this.label4User=new JLabel("USER");
		
		/*
		 * 密码
		 */
		this.pass=new JPasswordField();
		this.pass.setBackground(bg);
		this.pass.setForeground(fontColor);
		this.pass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//鼠标形状
		this.pass.setPreferredSize(cellSize);
		
		this.label4Pass=new JLabel("PASS");
		
		/*
		 * 用户名、密码面板
		 */
		this.userPanel=new JPanel();//面板
		Dimension size=new Dimension(400,34);//面板大小
		this.userPanel.setPreferredSize(size);		
		
		FlowLayout layout=new FlowLayout(FlowLayout.CENTER,10,5);//流式布局
		this.userPanel.setLayout(layout);//设置布局
		
		this.userPanel.add(this.label4User);
		this.userPanel.add(this.user);
		this.userPanel.add(this.label4Pass);
		this.userPanel.add(this.pass);
	}
	
	/**
	 * 编目、模式面板
	 *
	 */
	private void initCataPanel(){
		Color fontColor=new Color(249,96,51);//字体颜色
		Color bg=Color.WHITE;//背景颜色
		Dimension cellSize=new Dimension(140,30);
		
		/*
		 * 编目
		 */
		this.catalogs=new JComboBox();
		this.catalogs.setBackground(bg);
		this.catalogs.setForeground(fontColor);
		this.catalogs.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//鼠标形状
		this.catalogs.setPreferredSize(cellSize);	
		
		this.label4Cata=new JLabel("CATA");
		
		/*
		 * 模式
		 */
		this.schemes=new JComboBox();
		this.schemes.setBackground(bg);
		this.schemes.setForeground(fontColor);
		this.schemes.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//鼠标形状
		this.schemes.setPreferredSize(cellSize);
		
		this.label4Scheme=new JLabel("SCHE");
		
		/*
		 * 编目、模式面板
		 */
		this.cataPanel=new JPanel();//面板
		Dimension size=new Dimension(400,34);//面板大小
		this.cataPanel.setPreferredSize(size);		
		
		FlowLayout layout=new FlowLayout(FlowLayout.CENTER,10,5);//流式布局
		this.cataPanel.setLayout(layout);//设置布局
		
		this.cataPanel.add(this.label4Cata);
		this.cataPanel.add(this.catalogs);
		this.cataPanel.add(this.label4Scheme);
		this.cataPanel.add(this.schemes);
	}
	
	/**
	 * 存储路径面板
	 *
	 */
	private void initPathPanel(){
		Color fontColor=new Color(249,96,51);//字体颜色
		Color bg=Color.WHITE;//背景颜色
		Dimension cellSize=new Dimension(235,30);
		
		/*
		 * 路径
		 */
		this.path=new JTextField();
		this.path.setBackground(bg);
		this.path.setForeground(fontColor);
		this.path.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//鼠标形状
		this.path.setPreferredSize(cellSize);	
		
		/*
		 * 选择路径Button
		 */
		this.choose=new JButton("CHOOSE PATH...");
		
		/*
		 * 面板
		 */
		this.pathPanel=new JPanel();//面板
		Dimension size=new Dimension(400,34);//面板大小
		this.pathPanel.setPreferredSize(size);		
		
		FlowLayout layout=new FlowLayout(FlowLayout.CENTER,10,5);//流式布局
		this.pathPanel.setLayout(layout);//设置布局
		
		this.pathPanel.add(this.path);
		this.pathPanel.add(this.choose);
	}
	
	/**
	 * 按钮面板
	 *
	 */
	private void initDBPanel(){
		Color fontColor=new Color(249,96,51);//字体颜色
		Color bg=Color.WHITE;//背景颜色
		Dimension cellSize=new Dimension(120,30);
		
		/*
		 * 连接数据库
		 */
		this.connect=new JButton("CONNECT");
		this.connect.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//鼠标形状
		
		this.label4Db=new JLabel("DB");
		
		//数据库名
		this.db=new JTextField();
		this.db.setBackground(bg);
		this.db.setForeground(fontColor);
		this.db.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//鼠标形状
		this.db.setPreferredSize(cellSize);	
		
		this.showTables=new JButton("SHOW TABLE");
		
		/*
		 * 面板
		 */
		this.dbPanel=new JPanel();//面板
		Dimension size=new Dimension(400,34);//面板大小
		this.dbPanel.setPreferredSize(size);		
		
		FlowLayout layout=new FlowLayout(FlowLayout.CENTER,10,5);//流式布局
		this.dbPanel.setLayout(layout);//设置布局
		
		this.dbPanel.add(this.label4Db);
		this.dbPanel.add(this.db);
		this.dbPanel.add(this.connect);
		this.dbPanel.add(this.showTables);
	}
	
	/**
	 * 初始化界面
	 *
	 */
	private void initGUI(){				
		this.fileChooser=new JFileChooser();
		this.fileChooser.setPreferredSize(new Dimension(400,300));
		this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.fileChooser.setMultiSelectionEnabled(false);
		
		Font font=new Font("System",Font.BOLD,14);//字体
		Color borderColor=Color.LIGHT_GRAY;//边框颜色
		LineBorder lineBorder=new LineBorder(borderColor,2,true);//线边框
		
		TitledBorder border=new TitledBorder(lineBorder,"请选择存储路径");//边框
		border.setTitleColor(Color.WHITE);//边框颜色
		border.setTitleFont(font);
		border.setTitleJustification(TitledBorder.CENTER);//边框标题对齐方式
		this.fileChooser.setBorder(border);
		
		this.info=new JTextArea();
		this.info.setPreferredSize(new Dimension(400,30));
		this.info.setEditable(false);
		this.info.setForeground(Color.RED);
		
		//表
		this.tables=new JList();
		this.tables.setPreferredSize(new Dimension(400,5000));
		JScrollPane scroll=new JScrollPane();
		scroll.setPreferredSize(new Dimension(400,130));
		scroll.setViewportView(this.tables);
		
		/*
		 * 布局
		 */
		GridBagLayout layout=new GridBagLayout();
		this.setLayout(layout);
		GridBagConstraints constraints=new GridBagConstraints();
		
		constraints.weightx=1;
		constraints.weighty=1;		
		constraints.insets=new Insets(5,0,5,0);
		
		//编码、模式
		constraints.gridx=0;
		constraints.gridy=0;
		constraints.gridheight=1;
		constraints.gridwidth=1;
		layout.setConstraints(this.cataPanel,constraints);
		this.add(this.cataPanel);	
		
		//数据库系统
		constraints.gridx=0;
		constraints.gridy=1;
		constraints.gridheight=1;
		constraints.gridwidth=1;
		layout.setConstraints(this.hostPanel,constraints);
		this.add(this.hostPanel);
		
		//用户、密码
		constraints.gridx=0;
		constraints.gridy=2;
		constraints.gridheight=1;
		constraints.gridwidth=1;
		layout.setConstraints(this.userPanel,constraints);
		this.add(this.userPanel);
		
		//数据库名
		constraints.gridx=0;
		constraints.gridy=3;
		constraints.gridheight=1;
		constraints.gridwidth=1;
		layout.setConstraints(this.dbPanel,constraints);
		this.add(this.dbPanel);
		
		//表
		constraints.gridx=0;
		constraints.gridy=4;
		constraints.gridheight=1;
		constraints.gridwidth=1;
		layout.setConstraints(scroll,constraints);
		this.add(scroll);
		
		
		//选择路径
		constraints.gridx=0;
		constraints.gridy=5;
		constraints.gridheight=1;
		constraints.gridwidth=1;
		layout.setConstraints(this.pathPanel,constraints);
		this.add(this.pathPanel);
		
		//包名
		constraints.gridx=0;
		constraints.gridy=6;
		constraints.gridheight=1;
		constraints.gridwidth=1;
		layout.setConstraints(this.packPanel,constraints);
		this.add(this.packPanel);
		
		//信息
		constraints.gridx=0;
		constraints.gridy=7;
		constraints.gridheight=1;
		constraints.gridwidth=1;
		layout.setConstraints(this.info,constraints);
		this.add(this.info);
		
		//按钮
		constraints.gridx=0;
		constraints.gridy=8;
		constraints.gridheight=1;
		constraints.gridwidth=1;
		layout.setConstraints(this.buttonPanel,constraints);
		this.add(this.buttonPanel);
		
		this.path.setEditable(false);
		this.showTables.setEnabled(false);
		this.gen.setEnabled(false);
	}
	
	/**
	 * 显示界面
	 *
	 */
	protected void showGUI(){
		JFrame frame=new JFrame("");
		frame.getContentPane().add(this);
		frame.setSize(800,600);
		frame.setVisible(true);
		frame.setResizable(false);
		Dimension size=Toolkit.getDefaultToolkit().getScreenSize();
		
		Dimension size1=frame.getSize();
		
		frame.setLocation((size.width-size1.width)/2,(size.height-size1.height)/2);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent event){
				try{
					dao.close();
				}catch(Exception e){}
				System.exit(0);
			}
		});
	}
}
