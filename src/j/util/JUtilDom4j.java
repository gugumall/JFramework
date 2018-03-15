package j.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 
 * @author JFramework
 *
 */
public final class JUtilDom4j {	
	
	/** 
	 * 将指定路径的xml文件解析成Document
	 * @param xmlPath
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static Document parse(String xmlPath,String encoding)throws Exception{
		File file = new File(xmlPath);        
		SAXReader reader = new SAXReader(false);
		return reader.read(new FileInputStream(file),encoding);
	}
	
	/** 
	 * 将xml字符串解析成Document
	 * @param xml
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static Document parseString(String xml,String encoding)throws Exception{   
		SAXReader reader = new SAXReader(false);
		return reader.read(new StringReader(xml),encoding);
	}	
	
	/**
	 * 将Document对象保存为指定编码的xml文件
	 * @param doc
	 * @param xmlPath
	 * @param encoding
	 * @throws Exception
	 */
	public static void save(Document doc,String xmlPath,String encoding)throws Exception{
		OutputFormat format=OutputFormat.createPrettyPrint();
	    format.setEncoding(encoding);
	    format.setLineSeparator(System.getProperty("line.separator"));
	    XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(xmlPath),encoding),format);
		writer.write(doc);
		writer.flush();
		writer.close();	
	}
	
	/**
	 * 将Document对象转换成xml字符串
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	public static String toString(Document doc)throws Exception{
		OutputFormat format=OutputFormat.createPrettyPrint();
	    format.setEncoding(doc.getXMLEncoding());
	    ByteArrayOutputStream byteOS=new ByteArrayOutputStream();
	    XMLWriter writer = new XMLWriter(new OutputStreamWriter(byteOS,doc.getXMLEncoding()),format);
		writer.write(doc);
		writer.flush();
		writer.close();		
		
		return byteOS.toString(doc.getXMLEncoding());
	}
	
	/**
	 * 将Document对象转换成指定编码的xml字符串
	 * @param doc
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String toString(Document doc,String encoding)throws Exception{
		OutputFormat format=OutputFormat.createPrettyPrint();
	    format.setEncoding(encoding);
	    ByteArrayOutputStream byteOS=new ByteArrayOutputStream();
	    XMLWriter writer = new XMLWriter(new OutputStreamWriter(byteOS,encoding),format);
		writer.write(doc);
		writer.flush();
		writer.close();		
		
		return byteOS.toString(encoding);
	}	
}
