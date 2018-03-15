package j.test;

import j.util.JUtilTextWriter;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ExcelUtilViaPOI{

	public static void main(String[] args) throws Exception {
		JUtilTextWriter log=new JUtilTextWriter(new File("E:\\jstudio\\jframework\\doc\\regions.sql"),"UTF-8");
		log.addLine("use jframework;");
		log.addLine("delete from j_province;");
		log.addLine("delete from j_city;");
		log.addLine("delete from j_county;");
		log.addLine("delete from j_zone;");
		
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("E:\\JStudio\\JFramework\\doc\\2013最新全国街道乡镇级以上行政区划代码表.xls"));

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("records"); 
		
		String provinceId=null;
		String provinceName=null;
		
		String cityId=null;
		String cityName=null;
		
		String countyId=null;
		String countyName=null;

		int lr = sheet.getLastRowNum();
		System.out.println("total:"+lr);
		for (int i = 1; i < lr; i++) {
			HSSFRow row = sheet.getRow(i);
			
			String code="";
			String pcode="";
			String name="";
			String level="";
			
			HSSFCell c0=row.getCell(0);
			if(c0==null) break;
			
			if(c0.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
				code=""+(int)c0.getNumericCellValue();
			}else{
				code=c0.getStringCellValue();
			}
		
			HSSFCell c1=row.getCell(1);
			if(c1.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
				pcode=""+(int)c1.getNumericCellValue();
			}else{
				pcode=c1.getStringCellValue();
			}
			
			HSSFCell c2=row.getCell(2);
			if(c2.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
				name=""+(int)c2.getNumericCellValue();
			}else{
				name=c2.getStringCellValue();
			}
			
			HSSFCell c3=row.getCell(3);
			if(c3.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
				level=""+(int)c3.getNumericCellValue();
			}else{
				level=c3.getStringCellValue();
			}
			
			
			
			if(level.equals("1")){
				provinceId=code;
				provinceName=name;
				
				log.addLine("insert into j_province values ('"+code+"','"+name+"','"+name+"');");
				System.out.println("insert into j_province values ('"+code+"','"+name+"','"+name+"');");
			}else if(level.equals("2")){
				cityId=code;
				cityName=name;
				
				if(!name.equals("省直辖行政单位")&&!name.equals("市辖区")&&!name.equals("县")){
					log.addLine(" insert into j_city values ('"+code+"','"+provinceId+"','"+name+"','','','');");
					System.out.println(" insert into j_city values ('"+code+"','"+provinceId+"','"+name+"','','','');");
				}
			}else if(level.equals("3")){
				countyId=code;
				countyName=name;

				if(!cityName.equals("省直辖行政单位")&&!cityName.equals("市辖区")&&!cityName.equals("县")){
					if(!name.equals("市辖区")){
						log.addLine("  insert into j_county values ('"+code+"','"+cityId+"','"+name+"','','','');");
						System.out.println("  insert into j_county values ('"+code+"','"+cityId+"','"+name+"','','','');");
					}
				}else{
					if(!name.equals("市辖区")){
						log.addLine(" insert into j_city values ('"+code+"','"+provinceId+"','"+name+"','','','');");
						System.out.println(" insert into j_city values ('"+code+"','"+provinceId+"','"+name+"','','','');");
					}
				}
			}else if(level.equals("4")){

				if(!cityName.equals("省直辖行政单位")&&!cityName.equals("市辖区")&&!cityName.equals("县")){
					log.addLine("   insert into j_zone values ('"+code+"','"+countyId+"','"+name+"','','','');");
					System.out.println("   insert into j_zone values ('"+code+"','"+countyId+"','"+name+"','','','');");
				}else{
					log.addLine("  insert into j_county values ('"+code+"','"+countyId+"','"+name+"','','','');");
					System.out.println("  insert into j_county values ('"+code+"','"+countyId+"','"+name+"','','','');");
				}
			}
		}
	}
}
