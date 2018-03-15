// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2013-12-25 22:56:10
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OCRScannerDemo.java

package j.test.ocr;

import java.awt.Image;
import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.*;
import net.sourceforge.javaocr.scanner.PixelImage;

public class OCRScannerDemo
{

    public OCRScannerDemo()
    {
        debug = true;
        scanner = new OCRScanner();
    }

    public void loadTrainingImages(String trainingImageDir)
    {
        if(debug)
            System.err.println((new StringBuilder()).append("loadTrainingImages(").append(trainingImageDir).append(")").toString());
        if(!trainingImageDir.endsWith(File.separator))
            trainingImageDir = (new StringBuilder()).append(trainingImageDir).append(File.separator).toString();
        try
        {
            scanner.clearTrainingImages();
            TrainingImageLoader loader = new TrainingImageLoader();
            HashMap trainingImageMap = new HashMap();
            if(debug)
                System.err.println("ascii.png");
            loader.load((new StringBuilder()).append(trainingImageDir).append("ascii.png").toString(), new CharacterRange(33, 126), trainingImageMap);
            if(debug)
                System.err.println("hpljPica.jpg");
            loader.load((new StringBuilder()).append(trainingImageDir).append("hpljPica.jpg").toString(), new CharacterRange(33, 126), trainingImageMap);
            if(debug)
                System.err.println("digits.jpg");
            loader.load((new StringBuilder()).append(trainingImageDir).append("digits.jpg").toString(), new CharacterRange(48, 57), trainingImageMap);
            if(debug)
                System.err.println("adding images");
            scanner.addTrainingImages(trainingImageMap);
            if(debug)
                System.err.println("loadTrainingImages() done");
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            System.exit(2);
        }
    }

    public void process(String imageFilename)
    {
        if(debug)
            System.err.println((new StringBuilder()).append("process(").append(imageFilename).append(")").toString());
        try
        {
            image = ImageIO.read(new File(imageFilename));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        if(image == null)
        {
            System.err.println((new StringBuilder()).append("Cannot find image file: ").append(imageFilename).toString());
            return;
        }
        if(debug)
            System.err.println("constructing new PixelImage");
        PixelImage pixelImage = new PixelImage(image);
        if(debug)
            System.err.println("converting PixelImage to grayScale");
        pixelImage.toGrayScale(true);
        if(debug)
            System.err.println("filtering");
        pixelImage.filter();
        if(debug)
            System.err.println("setting image for display");
        System.out.println((new StringBuilder()).append(imageFilename).append(":").toString());
        String text = scanner.scan(image, 0, 0, 0, 0, null);
        System.out.println((new StringBuilder()).append("[").append(text).append("]").toString());
    }

    public static void main(String args[])
    {
        if(args.length < 1)
        {
            System.err.println("Please specify one or more image filenames.");
            System.exit(1);
        }
        String trainingImageDir = System.getProperty("TRAINING_IMAGE_DIR");
        if(trainingImageDir == null)
        {
            System.err.println("Please specify -DTRAINING_IMAGE_DIR=<dir> on the java command line.");
            return;
        }
        OCRScannerDemo demo = new OCRScannerDemo();
        demo.loadTrainingImages(trainingImageDir);
        for(int i = 0; i < args.length; i++)
            demo.process(args[i]);

        System.out.println("done.");
    }

    private static final long serialVersionUID = 1L;
    private boolean debug;
    private Image image;
    private OCRScanner scanner;
    private static final Logger LOG = Logger.getLogger(OCRScannerDemo.class.getName());

}