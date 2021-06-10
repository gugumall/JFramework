package j.AI;

import java.io.File;

import com.ocr.OtherDemo.Tess4J;

import j.util.JUtilImage;
import j.util.JUtilString;

public class OCRTess extends OCR{

	@Override
	public String parse(String imagePath) {
		try {
	        String code = Tess4J.doOCR("chi_sim", imagePath);        
	        return code;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		System.out.println("111");
		JUtilImage i=new JUtilImage();
		i.setQuality(1f);
		
		i.zoomToSizeIfLarger(new File("F:\\外包\\OCR识别\\微信图片_20210530153948.jpg"), 
				new File("F:\\外包\\OCR识别\\微信图片_20210530153948X.jpg"), 
				1600, 
				JUtilImage.FORMAT_JPEG);
		
		i.rotate(new File("F:\\外包\\OCR识别\\微信图片_20210530153948X.jpg"), 
				new File("F:\\外包\\OCR识别\\微信图片_20210530153948XX.jpg"), 
						90, 
						JUtilImage.FORMAT_JPEG);
		
//		String result=OCR.parse("TESS", "F:\\外包\\OCR识别\\微信图片_20210530153948X.jpg");
//		System.out.println("result1 -> \r\n"+result);

		String result=OCR.parse("TESS", "F:\\外包\\OCR识别\\微信图片_20210530153948XX.jpg");
		String[] results=JUtilString.getTokens(result, "\n");
		for(int j=0; j<results.length; j++) {
			String line=results[j];
			line=JUtilString.replaceAll(line, "\r", "");
			line=JUtilString.replaceAll(line, "\n", "");
			if("".equals(line)) continue;
			System.out.println("result12 -> "+line);
		}
	}
}
