package j.util;

import j.fs.JDFSFile;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

import com.gif4j.GifDecoder;
import com.gif4j.GifEncoder;
import com.gif4j.GifImage;
import com.gif4j.GifTransformer;

/**
 * 
 * @author JFramework
 * 
 */
public final class JUtilImage implements ImageObserver {
	public static final String FORMAT_JPG = "JPG";

	public static final String FORMAT_JPEG = "JPEG";

	public static final String FORMAT_PNG = "PNG";

	public static final String FORMAT_GIF = "GIF";

	public static final String POS_LT = "POS_LT";// logo位置-左上

	public static final String POS_LB = "POS_LB";// logo位置-左下

	public static final String POS_RT = "POS_RT";// logo位置-右上

	public static final String POS_RB = "POS_RB";// logo位置-右下

	public static final String POS_CE = "POS_CE";// logo位置-中心
	
	public static final String POS_CT = "POS_CT";// logo位置-顶部居中
	
	public static final String POS_CB = "POS_CB";// logo位置-底部居中

	private float quality = 1f;// 图片质量
	
	/**
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public static int[] rgbToCmyk(int red, int green, int blue){
        int black = Math.min(Math.min(255 - red, 255 - green), 255 - blue);

        if (black!=255) {
            int cyan    = (255-red-black)/(255-black);
            int magenta = (255-green-black)/(255-black);
            int yellow  = (255-blue-black)/(255-black);
            return new int[] {cyan,magenta,yellow,black};
        } else {
            int cyan = 255 - red;
            int magenta = 255 - green;
            int yellow = 255 - blue;
            return new int[] {cyan,magenta,yellow,black};
        }
    }

	/**
	 * 
	 * @param cyan
	 * @param magenta
	 * @param yellow
	 * @param black
	 * @return
	 */
    public static int[] cmykToRgb(int cyan, int magenta, int yellow, int black){
        if (black!=255) {
            int R = ((255-cyan) * (255-black)) / 255; 
            int G = ((255-magenta) * (255-black)) / 255; 
            int B = ((255-yellow) * (255-black)) / 255;
            return new int[] {R,G,B};
        } else {
            int R = 255 - cyan;
            int G = 255 - magenta;
            int B = 255 - yellow;
            return new int[] {R,G,B};
        }
    }

	/**
	 * 设置图片质量
	 * 
	 * @param _quality
	 */
	public void setQuality(float _quality) {
		if (_quality >= 0 && _quality <= 1)
			this.quality = _quality;
	}

	/**
	 * 将srcFile调整为newWidth*newHeight大小，并保存为destFile
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param newWidth
	 * @param newHeight
	 * @param imageFormat
	 * @return
	 * @throws Exception
	 */
	public void resize(File srcFile, File destFile, int newWidth,int newHeight, String imageFormat) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		
		if(imageFormat.equalsIgnoreCase(FORMAT_GIF)
				||srcFile.getName().toLowerCase().endsWith(".gif")){
			resizeGifImage(srcFile,destFile,newWidth,newHeight,true);
			return;
		}
		
		Image src = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		src = new ImageIcon(src).getImage();
		
		BufferedImage tag = new BufferedImage(newWidth, newHeight,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = tag.createGraphics(); 
		
		//设置透明
		if(imageFormat.equals(JUtilImage.FORMAT_PNG)){
			tag = g2d.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight, Transparency.TRANSLUCENT); 
		} 
		g2d.dispose(); 
		g2d = tag.createGraphics(); 
		//设置透明 end
				
		g2d.drawImage(src, 0, 0, newWidth, newHeight, this);// 绘制缩小后的图

		// 如果父目录不存在，则创建目录
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		FileOutputStream os = new FileOutputStream(destFile); // 输出到文件流
		ImageWriter writer = (ImageWriter) ImageIO.getImageWritersByFormatName(imageFormat).next();
		ImageOutputStream ios = ImageIO.createImageOutputStream(os);
		writer.setOutput(ios);
		ImageWriteParam param = new JPEGImageWriteParam(Locale.getDefault());
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);
		writer.write(null, new IIOImage(tag, null, null), param);
		writer.dispose();
		ios.flush();
		ios.close();
		os.close();
	}

	/**
	 * 将srcFile按比例scale缩放，长宽比例不变，并保存为destFile
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param scale
	 * @param imageFormat
	 * @return
	 * @throws Exception
	 */
	public void zoom(File srcFile, File destFile, double scale,String imageFormat) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		
		
		Image src = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		src = new ImageIcon(src).getImage();

		double oldWidth = (double) src.getWidth(this);
		double oldHeight = (double) src.getHeight(this);
		int newWidth = JUtilMath.toInt(oldWidth * scale);
		int newHeight = JUtilMath.toInt(oldHeight * scale);
		
		if(imageFormat.equalsIgnoreCase(FORMAT_GIF)
				||srcFile.getName().toLowerCase().endsWith(".gif")){
			resizeGifImage(srcFile,destFile,newWidth,newHeight,true);
			return;
		}

		BufferedImage tag=new BufferedImage(newWidth, newHeight,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = tag.createGraphics(); 
		
		//设置透明
		if(imageFormat.equals(JUtilImage.FORMAT_PNG)){
			tag = g2d.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight, Transparency.TRANSLUCENT); 
		}
		g2d.dispose(); 
		g2d = tag.createGraphics(); 
		//设置透明 end
		
		g2d.drawImage(src, 0, 0, newWidth, newHeight, this);// 绘制缩小后的图

		// 如果父目录不存在，则创建目录
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		FileOutputStream os = new FileOutputStream(destFile); // 输出到文件流
		ImageWriter writer = (ImageWriter) ImageIO.getImageWritersByFormatName(imageFormat).next();
		ImageOutputStream ios = ImageIO.createImageOutputStream(os);
		writer.setOutput(ios);
		ImageWriteParam param = new JPEGImageWriteParam(Locale.getDefault());
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);
		writer.write(null, new IIOImage(tag, null, null), param);
		writer.dispose();
		ios.flush();
		ios.close();
		os.close();
	}

	/**
	 * 将srcFile按比例缩放，使其较长一边的长度等于longerSideSize，并保存为destFile
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param longerSideSize
	 * @param imageFormat
	 * @return
	 * @throws Exception
	 */
	public void zoomToSize(File srcFile, File destFile, int longerSideSize,String imageFormat) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		Image src = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		src = new ImageIcon(src).getImage();
		double oldWidth = (double) src.getWidth(this);
		double oldHeight = (double) src.getHeight(this);

		double scale = 1;
		if (oldWidth > oldHeight) {
			scale = longerSideSize / oldWidth;
		} else {
			scale = longerSideSize / oldHeight;
		}
		zoom(srcFile, destFile, scale, imageFormat);
	}

	/**
	 * 将srcFile按比例缩放，使其较长一边的长度等于longerSideSize，并保存为destFile
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param longerSideSize
	 * @param imageFormat
	 * @return
	 * @throws Exception
	 */
	public void zoomToSizeIfLarger(File srcFile, File destFile,int longerSideSize, String imageFormat) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		Image src = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		src = new ImageIcon(src).getImage();
		double oldWidth = (double) src.getWidth(this);
		double oldHeight = (double) src.getHeight(this);

		if (oldWidth <= longerSideSize && oldHeight <= longerSideSize) {
			JDFSFile.saveStream(new FileInputStream(srcFile), destFile.getAbsolutePath());
			return;
		}

		double scale = 1;
		if (oldWidth > oldHeight) {
			scale = longerSideSize / oldWidth;
		} else {
			scale = longerSideSize / oldHeight;
		}
		zoom(srcFile, destFile, scale, imageFormat);
	}

	/**
	 * 将srcFile按比例缩放，使其较长一边的长度等于longerSideSize，并保存为destFile
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param longerSideSize
	 * @param imageFormat
	 * @return
	 * @throws Exception
	 */
	public void zoomToSizeByShorterSide(File srcFile, File destFile, int shorterSideSize,String imageFormat) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		Image src = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		src = new ImageIcon(src).getImage();
		double oldWidth = (double) src.getWidth(this);
		double oldHeight = (double) src.getHeight(this);

		double scale = 1;
		if (oldWidth < oldHeight) {
			scale = shorterSideSize / oldWidth;
		} else {
			scale = shorterSideSize / oldHeight;
		}
		zoom(srcFile, destFile, scale, imageFormat);
	}

	/**
	 * 将srcFile按比例缩放，使其较长一边的长度等于longerSideSize，并保存为destFile
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param longerSideSize
	 * @param imageFormat
	 * @return
	 * @throws Exception
	 */
	public void zoomToSizeByShorterSideIfLarger(File srcFile, File destFile,int shorterSideSize, String imageFormat) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		Image src = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		src = new ImageIcon(src).getImage();
		double oldWidth = (double) src.getWidth(this);
		double oldHeight = (double) src.getHeight(this);

		if (oldWidth <= shorterSideSize && oldHeight <= shorterSideSize) {
			JDFSFile.saveStream(new FileInputStream(srcFile), destFile.getAbsolutePath());
			return;
		}

		double scale = 1;
		if (oldWidth < oldHeight) {
			scale = shorterSideSize / oldWidth;
		} else {
			scale = shorterSideSize / oldHeight;
		}
		zoom(srcFile, destFile, scale, imageFormat);
	}

	/**
	 * 将srcFile按比例缩放，使其高度等于height，并保存为destFile
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param height
	 * @param imageFormat
	 * @return
	 * @throws Exception
	 */
	public void zoomToHeight(File srcFile, File destFile, int height,String imageFormat) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		Image src = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		src = new ImageIcon(src).getImage();
		double oldHeight = (double) src.getHeight(this);
		double scale = height / oldHeight;
		zoom(srcFile, destFile, scale, imageFormat);
	}

	/**
	 * 将srcFile按比例缩放，使其宽度等于width，并保存为destFile
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param width
	 * @param imageFormat
	 * @return
	 * @throws Exception
	 */
	public void zoomToWidth(File srcFile, File destFile, int width,String imageFormat) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		Image src = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		src = new ImageIcon(src).getImage();
		double oldWidth = (double) src.getWidth(this);
		double scale = width / oldWidth;
		zoom(srcFile, destFile, scale, imageFormat);
	}

	/**
	 * 给srcFile加水印logo，并保存至destFile
	 * 
	 * @param srcFile
	 * @param logo
	 * @param destFile
	 * @param offsetX
	 * @param offsetY
	 * @param imageFormat
	 * @param position
	 * @throws Exception
	 */
	public void logo(File srcFile, File logo, File destFile, int offsetX,int offsetY, String imageFormat, String position) throws Exception {
		logoWithTitle(srcFile,logo,destFile,offsetX,offsetY,imageFormat,position,null,null,null,null,0,0);
	}

	/**
	 * 给srcFile加水印logo，并保存至destFile
	 * @param srcFile
	 * @param logo
	 * @param destFile
	 * @param offsetX
	 * @param offsetY
	 * @param imageFormat
	 * @param position
	 * @param title
	 * @param titleFont
	 * @param titleOffsetX
	 * @param titleOffsetY
	 * @param titleColor
	 * @throws Exception
	 */
	public void logoWithTitle(File srcFile, File logo, File destFile, int offsetX,int offsetY, String imageFormat, String position,String title,Font titleFont,Color titleColor,String titlePos,int titleOffsetX,int titleOffsetY) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		if (!chkLogoPos(position)) {
			throw new Exception("logo位置不合法");
		}
		Image srcImg = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		srcImg = new ImageIcon(srcImg).getImage();
		
		Image logoImg = Toolkit.getDefaultToolkit().getImage(logo.getAbsolutePath());
		logoImg = new ImageIcon(logoImg).getImage();
		
		int srcWidth = srcImg.getWidth(this);
		int srcHeight = srcImg.getHeight(this);
		int logoWidth = logoImg.getWidth(this);
		int logoHeight = logoImg.getHeight(this);

		BufferedImage tag = new BufferedImage(srcWidth, srcHeight,BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(srcImg, 0, 0, srcWidth, srcHeight, this);// 绘制大图
		int realOffsetX;
		int realOffsetY;
		if(position.equals(JUtilImage.POS_CE)){
			realOffsetX = (srcWidth - logoWidth) / 2;
			realOffsetY = (srcHeight - logoHeight) / 2;
		}else if (position.equals(JUtilImage.POS_CT)){
			realOffsetX = (srcWidth - logoWidth) / 2;
			realOffsetY = offsetY;
		}else if (position.equals(JUtilImage.POS_CB)){
			realOffsetX = (srcWidth - logoWidth) / 2;
			realOffsetY = srcHeight - offsetY - logoHeight;
		} else if (position.equals(JUtilImage.POS_LT)){
			realOffsetX = offsetX;
			realOffsetY = offsetY;
		}else if (position.equals(JUtilImage.POS_LB)){
			realOffsetX = offsetX;
			realOffsetY = srcHeight - offsetY - logoHeight;
		}else if (position.equals(JUtilImage.POS_RT)){
			realOffsetX = srcWidth - offsetX - logoWidth;
			realOffsetY = offsetY;
		}else{
			realOffsetX = srcWidth - offsetX - logoWidth;
			realOffsetY = srcHeight - offsetY - logoHeight;
		}
		tag.getGraphics().drawImage(logoImg, realOffsetX, realOffsetY,logoWidth, logoHeight, this);// 绘制logo
		
		if(title!=null&&!title.equals("")){
			Graphics graphics=tag.getGraphics();
			
			FontMetrics metrics = graphics.getFontMetrics(titleFont);
			int charHeight = metrics.getHeight();
			int titleWidth=metrics.charsWidth(title.toCharArray(),0,title.length());
			int titleHeight=charHeight;
			
			if(titlePos.equals(JUtilImage.POS_CE)){
				realOffsetX = (srcWidth - titleWidth) / 2;
				realOffsetY = (srcHeight - titleHeight) / 2;
			}else if (titlePos.equals(JUtilImage.POS_CT)){
				realOffsetX = (srcWidth - titleWidth) / 2;
				realOffsetY = titleOffsetY;
			}else if (titlePos.equals(JUtilImage.POS_CB)){
				realOffsetX = (srcWidth - titleWidth) / 2;
				realOffsetY = srcHeight - titleOffsetY - titleHeight;
			} else if (titlePos.equals(JUtilImage.POS_LT)){
				realOffsetX = titleOffsetX;
				realOffsetY = titleOffsetY;
			}else if (titlePos.equals(JUtilImage.POS_LB)){
				realOffsetX = titleOffsetX;
				realOffsetY = srcHeight - titleOffsetY - titleHeight;
			}else if (titlePos.equals(JUtilImage.POS_RT)){
				realOffsetX = srcWidth - titleOffsetX - titleWidth;
				realOffsetY = titleOffsetY;
			}else{
				realOffsetX = titleOffsetX;
				realOffsetY = titleOffsetY;
			}
			    
			graphics.setFont(titleFont);
			graphics.setColor(titleColor);
			graphics.drawString(title,realOffsetX,realOffsetY);
		}

		// 如果父目录不存在，则创建目录
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		FileOutputStream os = new FileOutputStream(destFile); // 输出到文件流
		ImageWriter writer = (ImageWriter) ImageIO.getImageWritersByFormatName(imageFormat).next();
		ImageOutputStream ios = ImageIO.createImageOutputStream(os);
		writer.setOutput(ios);
		ImageWriteParam param = new JPEGImageWriteParam(Locale.getDefault());
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);
		writer.write(null, new IIOImage(tag, null, null), param);
		writer.dispose();
		ios.flush();
		ios.close();
		os.close();
	}

	/**
	 * 给srcFile加水印logo，并保存至destFile
	 * 
	 * @param srcFile
	 * @param logo
	 * @param offsetX
	 * @param offsetY
	 * @param imageFormat
	 * @param position
	 * @throws Exception
	 */
	public BufferedImage logo(File srcFile, BufferedImage logoImg, int offsetX,int offsetY, String imageFormat, String position) throws Exception {
		return logoWithTitle(srcFile,logoImg,offsetX,offsetY,imageFormat,position,null,null,null,null,0,0);
	}

	/**
	 * 
	 * @param srcFile
	 * @param logoImg
	 * @param offsetX
	 * @param offsetY
	 * @param imageFormat
	 * @param position
	 * @param title
	 * @param titleFont
	 * @param titleOffsetX
	 * @param titleOffsetY
	 * @param titleColor
	 * @return
	 * @throws Exception
	 */
	public BufferedImage logoWithTitle(File srcFile, BufferedImage logoImg, int offsetX,int offsetY, String imageFormat, String position,String title,Font titleFont,Color titleColor,String titlePos,int titleOffsetX,int titleOffsetY) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		if (!chkLogoPos(position)) {
			throw new Exception("logo位置不合法");
		}
		
		Image srcImg = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		srcImg = new ImageIcon(srcImg).getImage();
	
		int srcWidth = srcImg.getWidth(this);
		int srcHeight = srcImg.getHeight(this);
		int logoWidth = logoImg.getWidth(this);
		int logoHeight = logoImg.getHeight(this);

		BufferedImage tag = new BufferedImage(srcWidth, srcHeight,BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(srcImg, 0, 0, srcWidth, srcHeight, this);// 绘制大图
		int realOffsetX;
		int realOffsetY;
		if(position.equals(JUtilImage.POS_CE)){
			realOffsetX = (srcWidth - logoWidth) / 2;
			realOffsetY = (srcHeight - logoHeight) / 2;
		}else if (position.equals(JUtilImage.POS_CT)){
			realOffsetX = (srcWidth - logoWidth) / 2;
			realOffsetY = offsetY;
		}else if (position.equals(JUtilImage.POS_CB)){
			realOffsetX = (srcWidth - logoWidth) / 2;
			realOffsetY = srcHeight - offsetY - logoHeight;
		} else if (position.equals(JUtilImage.POS_LT)){
			realOffsetX = offsetX;
			realOffsetY = offsetY;
		}else if (position.equals(JUtilImage.POS_LB)){
			realOffsetX = offsetX;
			realOffsetY = srcHeight - offsetY - logoHeight;
		}else if (position.equals(JUtilImage.POS_RT)){
			realOffsetX = srcWidth - offsetX - logoWidth;
			realOffsetY = offsetY;
		}else{
			realOffsetX = srcWidth - offsetX - logoWidth;
			realOffsetY = srcHeight - offsetY - logoHeight;
		}
		tag.getGraphics().drawImage(logoImg, realOffsetX, realOffsetY,logoWidth, logoHeight, this);// 绘制logo
		
		if(title!=null&&!title.equals("")){
			Graphics graphics=tag.getGraphics();
			
			FontMetrics metrics = graphics.getFontMetrics(titleFont);
			int charHeight = metrics.getHeight();
			int titleWidth=metrics.charsWidth(title.toCharArray(),0,title.length());
			int titleHeight=charHeight;
			
			if(titlePos.equals(JUtilImage.POS_CE)){
				realOffsetX = (srcWidth - titleWidth) / 2;
				realOffsetY = (srcHeight - titleHeight) / 2;
			}else if (titlePos.equals(JUtilImage.POS_CT)){
				realOffsetX = (srcWidth - titleWidth) / 2;
				realOffsetY = titleOffsetY;
			}else if (titlePos.equals(JUtilImage.POS_CB)){
				realOffsetX = (srcWidth - titleWidth) / 2;
				realOffsetY = srcHeight - titleOffsetY - titleHeight;
			} else if (titlePos.equals(JUtilImage.POS_LT)){
				realOffsetX = titleOffsetX;
				realOffsetY = titleOffsetY;
			}else if (titlePos.equals(JUtilImage.POS_LB)){
				realOffsetX = titleOffsetX;
				realOffsetY = srcHeight - titleOffsetY - titleHeight;
			}else if (titlePos.equals(JUtilImage.POS_RT)){
				realOffsetX = srcWidth - titleOffsetX - titleWidth;
				realOffsetY = titleOffsetY;
			}else{
				realOffsetX = titleOffsetX;
				realOffsetY = titleOffsetY;
			}
			    
			graphics.setFont(titleFont);
			graphics.setColor(titleColor);
			graphics.drawString(title,realOffsetX,realOffsetY);
		}
		
		return tag;
	}
	
	/**
	 * 
	 * @param output
	 * @param srcFile
	 * @param logoImg
	 * @param destFile
	 * @param offsetX
	 * @param offsetY
	 * @param imageFormat
	 * @param position
	 * @throws Exception
	 */
	public void logo(OutputStream output, File srcFile, BufferedImage logoImg, int offsetX,int offsetY, String imageFormat, String position) throws Exception {
		logoWithTitle(output,srcFile,logoImg,offsetX,offsetY,imageFormat,position,null,null,null,null,0,0);	
	}
	
	/**
	 * 
	 * @param output
	 * @param srcFile
	 * @param logoImg
	 * @param offsetX
	 * @param offsetY
	 * @param imageFormat
	 * @param position
	 * @throws Exception
	 */
	public void logoWithTitle(OutputStream output, File srcFile, BufferedImage logoImg, int offsetX,int offsetY, String imageFormat, String position,String title,Font titleFont,Color titleColor,String titlePos,int titleOffsetX,int titleOffsetY) throws Exception {
		BufferedImage img=logoWithTitle(srcFile, logoImg, offsetX,offsetY, imageFormat, position,title,titleFont,titleColor,titlePos,titleOffsetX,titleOffsetY);
		
		// 生成二维码QRCode图片
		ImageIO.write(img, imageFormat, output);
		
		output.flush();
		output.close();			
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int,
	 *      int, int, int)
	 */
	public boolean imageUpdate(Image img, int infoflags, int x, int y,int width, int height) {
		if ((infoflags & ALLBITS) == 0) {
			return true;
		} else {// 已经加载完
			return false;
		}
	}

	/**
	 * 图片类型是否在取值范围内
	 * 
	 * @param formatName
	 * @return
	 */
	private static boolean chkImageFormat(String formatName) {
		if (formatName == null) {
			return false;
		}
		if (formatName.equalsIgnoreCase(JUtilImage.FORMAT_JPEG)
				|| formatName.equalsIgnoreCase(JUtilImage.FORMAT_JPG)
				|| formatName.equalsIgnoreCase(JUtilImage.FORMAT_PNG)
				|| formatName.equalsIgnoreCase(JUtilImage.FORMAT_GIF)) {
			return true;
		}
		return false;
	}

	/**
	 * 水印位置是否在取值范围内
	 * 
	 * @param pos
	 * @return
	 */
	private static boolean chkLogoPos(String pos) {
		if (pos == null) {
			return false;
		}
		if (pos.equalsIgnoreCase(JUtilImage.POS_CE)
				|| pos.equalsIgnoreCase(JUtilImage.POS_LB)
				|| pos.equalsIgnoreCase(JUtilImage.POS_LT)
				|| pos.equalsIgnoreCase(JUtilImage.POS_RB)
				|| pos.equalsIgnoreCase(JUtilImage.POS_RT)
				|| pos.equalsIgnoreCase(JUtilImage.POS_CT)
				|| pos.equalsIgnoreCase(JUtilImage.POS_CB)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param srcImg
	 * @param destImg
	 * @param width
	 * @param height
	 * @param smooth
	 */
	public static void resizeGifImage(File srcImg, 
			File destImg, 
			int width,
			int height,
			boolean smooth) {
         try {
        	 GifImage gifImage = GifDecoder.decode(srcImg);// 创建一个GifImage对象
        	 GifImage resizedGifImage2 = GifTransformer.resize(gifImage, width, height, smooth);//1.缩放重新更改大小.
             GifEncoder.encode(resizedGifImage2, destImg,true);
         } catch (IOException e) {
             e.printStackTrace();
         }
     }




	/**
	 * 批量处理加水印，递归处理各级目录中文件
	 * 
	 * @param img
	 * @param logo
	 * @param file
	 * @param srcDir
	 * @param destDir
	 * @throws Exception
	 */
	public static void processLogo(JUtilImage img, File logo, File file,String srcDir, String destDir) throws Exception {
		if(file.isDirectory()){
			File[] fs = file.listFiles();
			for(int i = 0; i < fs.length; i++){
				processLogo(img, logo, fs[i], srcDir, destDir);
			}
		}else if(!file.getName().toLowerCase().endsWith("jpg")){
			return;
		}else{
			if(srcDir.indexOf("/")>0){
				img.logo(file,
						logo,
						new File(file.getAbsolutePath().replace("\\", "/")
						.replace(srcDir, destDir)
						.replace(".JPG",".jpg")
						.replace(".GIF", ".gif")),
						0,
						0,
						JUtilImage.FORMAT_JPG,
						JUtilImage.POS_LT);
				
				System.out.println(file.getAbsolutePath().replace("\\", "/")
						.replace(srcDir, destDir)
						.replace(".JPG", ".jpg")
						.replace(".GIF", ".gif"));
			}else{
				img.logo(file,
						logo,
						new File(file.getAbsolutePath()
						.replace(srcDir, destDir)
						.replace(".JPG", ".jpg")
						.replace(".GIF", ".gif")),
						0,
						0,
						JUtilImage.FORMAT_JPG,JUtilImage.POS_LT);
				
				System.out.println(file.getAbsolutePath()
						.replace(srcDir,destDir)
						.replace(".JPG", ".jpg")
						.replace(".GIF", ".gif"));
			}
		}
	}
}
