package com.ocr.OtherDemo;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.recognition.software.jdeskew.ImageDeskew;

import net.sourceforge.tess4j.ITessAPI.TessPageSegMode;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.ImageHelper;

public class Tess4J {
    private static ITesseract instance = new Tesseract();

    /**
     * 
     * @param filePath
     * @return
     * @throws Exception
     */
    public static String doOCR(String filePath) throws Exception{
        instance.setLanguage("eng");
        instance.setPageSegMode(TessPageSegMode.PSM_SINGLE_CHAR);
        File file = new File(filePath);
        String ocrResult = instance.doOCR(file);
        return ocrResult;
    }

    /**
     * 
     * @param filePath
     * @param rotateAngle
     * @return
     * @throws Exception
     */
    public static String doOCR(String filePath, int rotateAngle) throws Exception{
        instance.setLanguage("eng");
        File file = new File(filePath);
        
        BufferedImage img = ImageIO.read(file);
        if(rotateAngle!=0) img = ImageHelper.rotateImage(img, rotateAngle);

        String ocrResult = instance.doOCR(img);
        return ocrResult;
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
        instance.setLanguage("eng");
        File file = new File(filePath);
        
        BufferedImage img = ImageIO.read(file);
        if(rotateAngle!=0) img = ImageHelper.rotateImage(img, rotateAngle);

        String ocrResult = instance.doOCR(img, rect);
        return ocrResult;
    }
}
