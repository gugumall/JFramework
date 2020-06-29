package com.ocr.OtherDemo;

import java.awt.Rectangle;
import java.io.File;

import j.util.ConcurrentMap;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

/**
 * 
 * @author 肖炯
 *
 * 2020年6月27日
 *
 * <b>功能描述</b>
 */
public class Tess4J {
	private static ConcurrentMap<String, ITesseract> instances=new ConcurrentMap();

    /**
     * 
     * @param lang
     * @return
     */
    public static ITesseract getInstance(String lang) {
    	synchronized(lang.intern()) {
	    	ITesseract inst=instances.get(lang);
	    	if(inst==null) {
	    		inst=new Tesseract();
	    		inst.setDatapath("F:\\work\\JFramework_v2.0\\tessdata");
	    		inst.setLanguage(lang);
	    		instances.put(lang, inst);
	    	}
	    	return inst;
    	}
    }
    
    /**
     * 
     * @param lang
     * @param filePath
     * @return
     * @throws Exception
     */
    public static String doOCR(String lang, String filePath) throws Exception{
    	ITesseract instance=getInstance(lang);
    	synchronized(lang.intern()) {
            instance.setLanguage(lang);
            File file = new File(filePath);
            String ocrResult = instance.doOCR(file);
            return ocrResult;
    	}
    }

    /**
     * 
     * @param lang
     * @param filePath
     * @param rotateAngle
     * @return
     * @throws Exception
     */
    public static String doOCR(String lang, String filePath, int rotateAngle) throws Exception{
    	ITesseract instance=getInstance(lang);
    	synchronized(lang.intern()) {
	        instance.setLanguage(lang);
	        File file = new File(filePath);
	        
	        //BufferedImage img = ImageIO.read(file);
	        //if(rotateAngle!=0) img = ImageHelper.rotateImage(img, rotateAngle);
	
	        String ocrResult = instance.doOCR(file);
	        return ocrResult;
    	}
    }

    /**
     * 
     * @param lang
     * @param filePath
     * @param rotateAngle
     * @param rect
     * @return
     * @throws Exception
     */
    public static String doOCR(String lang, String filePath, int rotateAngle, Rectangle rect) throws Exception{
    	ITesseract instance=getInstance(lang);
    	synchronized(lang.intern()) {
	        instance.setLanguage(lang);
	        File file = new File(filePath);
	        
	        //BufferedImage img = ImageIO.read(file);
	        //if(rotateAngle!=0) img = ImageHelper.rotateImage(img, rotateAngle);
	
	        String ocrResult = instance.doOCR(file, rect);
	        return ocrResult;
    	}
    }
    
    /**
     * 
     * @param filePath
     * @return
     * @throws Exception
     */
    public static String doOCR(String filePath) throws Exception{
    	return doOCR("eng", filePath);
    }

    /**
     * 
     * @param filePath
     * @param rotateAngle
     * @return
     * @throws Exception
     */
    public static String doOCR(String filePath, int rotateAngle) throws Exception{
    	return doOCR("eng", filePath, rotateAngle);
    }

    /**
     * 
     * @param filePath
     * @param rotateAngle
     * @param rect
     * @return
     * @throws Exception
     */
    public static String doOCR(String filePath, int rotateAngle, Rectangle rect) throws Exception{
    	return doOCR("eng", filePath, rotateAngle, rect);
    }
    
    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
    	String s=Tess4J.doOCR("chi_sim", "C:\\Users\\ceo\\Desktop\\temp\\ed2e2d69fb1cccb5704314823ea4421e.jpg");
    	System.out.println(s);
    	System.exit(0);
    }
}
