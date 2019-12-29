package com.ocr.Utils;

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
		JHttp http=JHttp.getInstance();
		HttpClient client=http.createClient();
		JHttpContext context=new JHttpContext();
		
		for(int i=0; i<100; i++) {
			context=http.getStream(context, client, "https://7009846203-ab.cp168.ws/code?_="+System.currentTimeMillis());
			JDFSFile.saveStream(context.getResponseStream(), "F:\\temp\\code.jpg");
			
			//Thread.sleep(100);
			 
	        //原始验证码地址
	        String OriginalImg = "f:\\temp\\code.jpg";
	        
	        //识别样本输出地址
	        String ocrResult = "f:\\temp\\coder.jpg";
	       
	        //去噪点
	        //ImgUtils.removeBackground(OriginalImg, ocrResult);
	        
	        //裁剪边角
	        //ImgUtils.cuttingImg(ocrResult);
	       
	        //OCR识别
	        String code = Tess4J.executeTess4J(OriginalImg);
	        
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
	        
	       
	        if(code.length()==4&&JUtilMath.isInt(code)) {
		        System.out.println(i+", Ocr识别结果: \n" + code+","+code.length()+","+JUtilMath.isInt(code));
				Thread.sleep(2000);
	        }
		}
    }
}