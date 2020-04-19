package com.ocr.Utils;

import java.awt.Rectangle;

import org.apache.http.client.HttpClient;

import com.ocr.OtherDemo.Tess4J;

import j.fs.JDFSFile;
import j.http.JHttp;
import j.http.JHttpContext;
import j.util.JUtilMath;
import j.util.JUtilString;

public class ImgUtils {
	public static void main(String[] args) throws Exception{
		System.out.println(System.getProperty("user.dir"));//user.dir指定了当前的路径 
		
		//
//		JHttp http=JHttp.getInstance();
//		HttpClient client=http.createClient();
//		JHttpContext context=new JHttpContext();
		
		for(int i=0; i<100; i++) {
//			context=http.getStream(context, client, "https://7009846203-ab.cp168.ws/code?_="+System.currentTimeMillis());
//			JDFSFile.saveStream(context.getResponseStream(), "F:\\temp\\code.jpg");
			
			Thread.sleep(100);
			 
	        //原始验证码地址
	        String OriginalImg = "F:\\work\\JGame\\doc\\TransferTT\\cache\\code.png";
	        
	        //识别样本输出地址
	        String ocrResult = "f:\\temp\\coder.jpg";
	       
	        //去噪点
	        //ImgUtils.removeBackground(OriginalImg, ocrResult);
	        
	        //裁剪边角
	        //ImgUtils.cuttingImg(ocrResult);
	       
	        //OCR识别
	        //x,y是以左上角为原点，width和height是以xy为基础
	        String code = Tess4J.doOCR(OriginalImg, 0, new Rectangle(8, 12, 82, 19));
	        
	        code=JUtilString.replaceAll(code, " ", "");
	        code=JUtilString.replaceAll(code, "\t", "");
	        code=JUtilString.replaceAll(code, "\r", "");
	        code=JUtilString.replaceAll(code, "\n", "");
	        code=JUtilString.replaceAll(code, "\b", "");
	        code=JUtilString.replaceAll(code, ",", "");
	        code=JUtilString.replaceAll(code, ".", "");
	        code=JUtilString.replaceAll(code, ")", "");
	        code=JUtilString.replaceAll(code, "(", "");
	        code=JUtilString.replaceAll(code, "[", "");
	        code=JUtilString.replaceAll(code, "]", "");
	        code=JUtilString.replaceAll(code, "‘", "");
	        
	       
	        System.out.println(i+", Ocr识别结果: \n" + code+","+code.length()+","+JUtilMath.isInt(code));
			Thread.sleep(2000);
		}
    }
}