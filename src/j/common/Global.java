package j.common;

import java.io.File;

/**
 * 
 * @author JFramework
 *
 */
public class Global {
	public static final String filePathSeparator=File.separator;//文件路径分隔符
	public static final String lineSeparator=System.getProperty("line.separator");//系统换行符
	public static final int lineSeparatorLength=System.getProperty("line.separator").length();//系统换行符长度
}
