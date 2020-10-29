package j.tool;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import j.util.JUtilString;
import j.util.JUtilTextWriter;

/**
 * 
 * @author 肖炯
 *
 * 2020年10月29日
 *
 * <b>功能描述</b> 商城老客户手机处理
 */
public class OldBuyersProcessor{
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		/**
		 * 
		DAO dao=DB.connect("jshopx", OldBuyersProcessor.class);
		StmtAndRs sr=dao.find("select s_phone, s_pw, u_name, u_nick from js_user");
		ResultSet rs=sr.resultSet();
		
		System.out.println("List<String[]> users=new ArrayList();");
		while(rs.next()) {
			String s1=rs.getString(1);
			String s2=rs.getString(2);
			String s3=rs.getString(3);
			String s4=rs.getString(4);
			System.out.println("users.add(new String[] {\""+s1+"\",\""+s2+"\",\""+s3+"\",\""+s4+"\"});");
		}
		sr.close();
		dao.close();
		dao=null;
		 */
		
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("F:\\work\\blueprint\\淘宝2020年前订单.xls"));

		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);
		int lr = sheet.getLastRowNum();
		System.out.println("total:"+lr);
		
		JUtilTextWriter log=new JUtilTextWriter(new File("F:\\work\\blueprint\\淘宝2020年前订单用户信息.jsp"),"UTF-8");
		
		System.out.println("List<String[]> users=new ArrayList();");
		log.addLine("List<String[]> users=new ArrayList();");
		
		for (int i = 1; i < lr; i++) {
			HSSFRow row = sheet.getRow(i);
			
			HSSFCell c0=row.getCell(1);
			if(c0==null) break;
			

			HSSFCell c1=row.getCell(12);
			HSSFCell c2=row.getCell(16);
			if(c2==null)c2=row.getCell(15);

			String unick=c0.getStringCellValue();
			String uname=c1.getStringCellValue();
			String sphone=c2.getStringCellValue()+"";
			
			sphone=JUtilString.replaceAll(sphone, "'", "");
			uname=JUtilString.replaceAll(uname, "	", "");
	
			System.out.println("users.add(new String[] {\""+sphone+"\",\"8ddcff3a80f4189ca1c9d4d902c3c909\",\""+uname+"\",\""+unick+"\"});");
			log.addLine("users.add(new String[] {\""+sphone+"\",\"8ddcff3a80f4189ca1c9d4d902c3c909\",\""+uname+"\",\""+unick+"\"});");
		}
	}
}
