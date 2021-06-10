package j.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

import com.gif4j.GifDecoder;
import com.gif4j.GifEncoder;
import com.gif4j.GifImage;
import com.gif4j.GifTransformer;

import j.fs.JDFSFile;
import j.image.PaintItem;

/**
 * 
 * @author 肖炯
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
	 * 
	 * @param srcFile
	 * @return
	 */
	public BufferedImage read(File srcFile) {
		Image img = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		img.flush();
		img = new ImageIcon(img).getImage();

		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = bimage.createGraphics();
		g2d.drawImage(img, 0, 0, this);
		g2d.dispose();

		return bimage;
	}
	
	/**
	 * 
	 * @param srcFile
	 * @return
	 */
	public void save(BufferedImage img, File destFile, String imageFormat) throws Exception{
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
		writer.write(null, new IIOImage(img, null, null), param);
		writer.dispose();
		ios.flush();
		ios.close();
		os.close();
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
		src.flush();
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
		src.flush();
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
	 * 
	 * @param original
	 * @param newWidth
	 * @param newHeight
	 * @param imageFormat
	 * @return
	 * @throws Exception
	 */
	public BufferedImage zoom(Image original, int newWidth, int newHeight, String imageFormat) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}

		BufferedImage resizedImage=new BufferedImage(newWidth, newHeight,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = resizedImage.createGraphics(); 
		
		//设置透明
		if(imageFormat.equals(JUtilImage.FORMAT_PNG)){
			resizedImage = g2d.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight, Transparency.TRANSLUCENT); 
		}
		g2d.dispose(); 
		g2d = resizedImage.createGraphics(); 
		//设置透明 end
		
		g2d.drawImage(original, 0, 0, newWidth, newHeight, this);// 绘制缩小后的图

		return resizedImage;
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
		src.flush();
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
		src.flush();
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
		src.flush();
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
		src.flush();
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
		src.flush();
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
		src.flush();
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
	public BufferedImage logo(File srcFile, File logo, File destFile, int offsetX,int offsetY, String imageFormat, String position) throws Exception {
		return logoWithTitle(srcFile,logo,destFile,offsetX,offsetY,imageFormat,position,null,null,null,null,0,0);
	}
	
	/**
	 * 
	 * @param srcFile
	 * @param logo
	 * @param offsetX
	 * @param offsetY
	 * @param imageFormat
	 * @param position
	 * @return
	 * @throws Exception
	 */
	public BufferedImage logo(BufferedImage srcFile, File logo, int offsetX,int offsetY, String imageFormat, String position) throws Exception {
		return logoWithTitle(srcFile,logo,offsetX,offsetY,imageFormat,position,null,null,null,null,0,0);
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
	public BufferedImage logoWithTitle(File srcFile, File logo, File destFile, int offsetX,int offsetY, String imageFormat, String position,String title,Font titleFont,Color titleColor,String titlePos,int titleOffsetX,int titleOffsetY) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		if (!chkLogoPos(position)) {
			throw new Exception("logo位置不合法");
		}
		Image srcImg = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		srcImg.flush();
		srcImg = new ImageIcon(srcImg).getImage();
		
		Image logoImg = Toolkit.getDefaultToolkit().getImage(logo.getAbsolutePath());
		logoImg.flush();
		logoImg = new ImageIcon(logoImg).getImage();
		
		int srcWidth = srcImg.getWidth(this);
		int srcHeight = srcImg.getHeight(this);
		int logoWidth = logoImg.getWidth(this);
		int logoHeight = logoImg.getHeight(this);

		BufferedImage original = new BufferedImage(srcWidth, srcHeight,BufferedImage.TYPE_INT_RGB);
		original.getGraphics().drawImage(srcImg, 0, 0, srcWidth, srcHeight, this);// 绘制大图
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
		original.getGraphics().drawImage(logoImg, realOffsetX, realOffsetY,logoWidth, logoHeight, this);// 绘制logo
		
		if(title!=null&&!title.equals("")){
			Graphics graphics=original.getGraphics();
			
			FontMetrics metrics = graphics.getFontMetrics(titleFont);
			int charHeight = metrics.getHeight();
			int titleWidth=metrics.charsWidth(title.toCharArray(),0,title.length());
			int titleHeight=charHeight;
			
			//换行
			int maxTitleWidth=srcWidth-30;
			List printTitleLines=new ArrayList();
			if(titleWidth>maxTitleWidth){
				for(int i=1;i<=title.length();i++){
					int temp=metrics.charsWidth(title.toCharArray(),0,i);
					if(temp>maxTitleWidth){
						printTitleLines.add(title.substring(0,i));
						title=title.substring(i);
						i=1;
					}
				}
			}
			if(title.length()>0) printTitleLines.add(title);
			//换行 end
			
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
			
			if(titleWidth>maxTitleWidth) realOffsetX=10;
			    
			graphics.setFont(titleFont);
			graphics.setColor(titleColor);
			for(int i=0;i<printTitleLines.size();i++){
				graphics.drawString((String)printTitleLines.get(i),realOffsetX,realOffsetY+(i*charHeight));
			}
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
		writer.write(null, new IIOImage(original, null, null), param);
		writer.dispose();
		ios.flush();
		ios.close();
		os.close();
		
		return original;
	}
	
	/**
	 * 
	 * @param srcFile
	 * @param logo
	 * @param offsetX
	 * @param offsetY
	 * @param imageFormat
	 * @param position
	 * @param title
	 * @param titleFont
	 * @param titleColor
	 * @param titlePos
	 * @param titleOffsetX
	 * @param titleOffsetY
	 * @return
	 * @throws Exception
	 */
	public BufferedImage logoWithTitle(BufferedImage original, File logo, int offsetX,int offsetY, String imageFormat, String position,String title,Font titleFont,Color titleColor,String titlePos,int titleOffsetX,int titleOffsetY) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		if (!chkLogoPos(position)) {
			throw new Exception("logo位置不合法");
		}
	
		Image logoImg = Toolkit.getDefaultToolkit().getImage(logo.getAbsolutePath());
		logoImg.flush();
		logoImg = new ImageIcon(logoImg).getImage();
		
		int srcWidth = original.getWidth();
		int srcHeight = original.getHeight();
		int logoWidth = logoImg.getWidth(this);
		int logoHeight = logoImg.getHeight(this);

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
		original.getGraphics().drawImage(logoImg, realOffsetX, realOffsetY,logoWidth, logoHeight, this);// 绘制logo
		
		if(title!=null&&!title.equals("")){
			Graphics graphics=original.getGraphics();
			
			FontMetrics metrics = graphics.getFontMetrics(titleFont);
			int charHeight = metrics.getHeight();
			int titleWidth=metrics.charsWidth(title.toCharArray(),0,title.length());
			int titleHeight=charHeight;
			
			//换行
			int maxTitleWidth=srcWidth-30;
			List printTitleLines=new ArrayList();
			if(titleWidth>maxTitleWidth){
				for(int i=1;i<=title.length();i++){
					int temp=metrics.charsWidth(title.toCharArray(),0,i);
					if(temp>maxTitleWidth){
						printTitleLines.add(title.substring(0,i));
						title=title.substring(i);
						i=1;
					}
				}
			}
			if(title.length()>0) printTitleLines.add(title);
			//换行 end
			
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
			
			if(titleWidth>maxTitleWidth) realOffsetX=10;
			    
			graphics.setFont(titleFont);
			graphics.setColor(titleColor);
			for(int i=0;i<printTitleLines.size();i++){
				graphics.drawString((String)printTitleLines.get(i),realOffsetX,realOffsetY+(i*charHeight));
			}
		}

		return original;
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
	 * @return
	 * @throws Exception
	 */
	public BufferedImage logo(BufferedImage srcFile, BufferedImage logoImg, int offsetX,int offsetY, String imageFormat, String position) throws Exception {
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
		srcImg.flush();
		srcImg = new ImageIcon(srcImg).getImage();
	
		int srcWidth = srcImg.getWidth(this);
		int srcHeight = srcImg.getHeight(this);
		int logoWidth = logoImg.getWidth(this);
		int logoHeight = logoImg.getHeight(this);

		BufferedImage original = new BufferedImage(srcWidth, srcHeight,BufferedImage.TYPE_INT_RGB);
		original.getGraphics().drawImage(srcImg, 0, 0, srcWidth, srcHeight, this);// 绘制大图
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
		original.getGraphics().drawImage(logoImg, realOffsetX, realOffsetY,logoWidth, logoHeight, this);// 绘制logo
		
		if(title!=null&&!title.equals("")){
			Graphics graphics=original.getGraphics();
			
			FontMetrics metrics = graphics.getFontMetrics(titleFont);
			int charHeight = metrics.getHeight();
			int titleWidth=metrics.charsWidth(title.toCharArray(),0,title.length());
			int titleHeight=charHeight;
			
			//换行
			int maxTitleWidth=srcWidth-30;
			List printTitleLines=new ArrayList();
			if(titleWidth>maxTitleWidth){
				for(int i=1;i<=title.length();i++){
					int temp=metrics.charsWidth(title.toCharArray(),0,i);
					if(temp>maxTitleWidth){
						printTitleLines.add(title.substring(0,i));
						title=title.substring(i);
						i=1;
					}
				}
			}
			if(title.length()>0) printTitleLines.add(title);
			//换行 end
			
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
			
			if(titleWidth>maxTitleWidth) realOffsetX=10;
			    
			graphics.setFont(titleFont);
			graphics.setColor(titleColor);
			for(int i=0;i<printTitleLines.size();i++){
				graphics.drawString((String)printTitleLines.get(i),realOffsetX,realOffsetY+(i*charHeight));
			}
		}
		
		return original;
	}
	
	/**
	 * 
	 * @param original
	 * @param logoImg
	 * @param offsetX
	 * @param offsetY
	 * @param imageFormat
	 * @param position
	 * @param title
	 * @param titleFont
	 * @param titleColor
	 * @param titlePos
	 * @param titleOffsetX
	 * @param titleOffsetY
	 * @return
	 * @throws Exception
	 */
	public BufferedImage logoWithTitle(BufferedImage original, BufferedImage logoImg, int offsetX,int offsetY, String imageFormat, String position,String title,Font titleFont,Color titleColor,String titlePos,int titleOffsetX,int titleOffsetY) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		if (!chkLogoPos(position)) {
			throw new Exception("logo位置不合法");
		}
	
		int srcWidth = original.getWidth();
		int srcHeight = original.getHeight();
		int logoWidth = logoImg.getWidth();
		int logoHeight = logoImg.getHeight();

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
		original.getGraphics().drawImage(logoImg, realOffsetX, realOffsetY,logoWidth, logoHeight, this);// 绘制logo
		
		if(title!=null&&!title.equals("")){
			Graphics graphics=original.getGraphics();
			
			FontMetrics metrics = graphics.getFontMetrics(titleFont);
			int charHeight = metrics.getHeight();
			int titleWidth=metrics.charsWidth(title.toCharArray(),0,title.length());
			int titleHeight=charHeight;
			
			//换行
			int maxTitleWidth=srcWidth-30;
			List printTitleLines=new ArrayList();
			if(titleWidth>maxTitleWidth){
				for(int i=1;i<=title.length();i++){
					int temp=metrics.charsWidth(title.toCharArray(),0,i);
					if(temp>maxTitleWidth){
						printTitleLines.add(title.substring(0,i));
						title=title.substring(i);
						i=1;
					}
				}
			}
			if(title.length()>0) printTitleLines.add(title);
			//换行 end
			
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
			
			if(titleWidth>maxTitleWidth) realOffsetX=10;
			    
			graphics.setFont(titleFont);
			graphics.setColor(titleColor);
			for(int i=0;i<printTitleLines.size();i++){
				graphics.drawString((String)printTitleLines.get(i),realOffsetX,realOffsetY+(i*charHeight));
			}
		}
		
		return original;
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
	public BufferedImage logo(OutputStream output, File srcFile, BufferedImage logoImg, int offsetX,int offsetY, String imageFormat, String position) throws Exception {
		return logoWithTitle(output,srcFile,logoImg,offsetX,offsetY,imageFormat,position,null,null,null,null,0,0);	
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
	public BufferedImage logoWithTitle(OutputStream output, File srcFile, BufferedImage logoImg, int offsetX,int offsetY, String imageFormat, String position,String title,Font titleFont,Color titleColor,String titlePos,int titleOffsetX,int titleOffsetY) throws Exception {
		BufferedImage img=logoWithTitle(srcFile, logoImg, offsetX,offsetY, imageFormat, position,title,titleFont,titleColor,titlePos,titleOffsetX,titleOffsetY);
		
		// 生成二维码QRCode图片
		ImageIO.write(img, imageFormat, output);
		
		output.flush();
		output.close();	
		
		return img;
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
	
	public static void webp2jpg(File from,File to){
		System.load("F:\\work\\JFramework_v2.0\\WebContent\\WEB-INF\\lib\\webp-imageio.so");
		System.out.println(System.getProperty("java.library.path"));  
		          
		try {  		          
		    BufferedImage im = ImageIO.read(from);   
		    ImageIO.write(im, "jpg", to);  
		} catch (IOException e) {  
		    e.printStackTrace();  
		} 
	}

	/**
	 * 批量处理加水印，递归处理各级目录中文件
	 * 
	 * @param args
	 * @throws Exception
	 */
	private static int all = 10000;

	public static void main(String[] args) throws Exception {	
//		//webp2jpg();
		JUtilImage im = new JUtilImage();
		im.setQuality(1f);
		
		int index=1;
		
		File dir = new File("F:\\images\\时光(足迹) VII\\temp");
		File[] fs=dir.listFiles();
		for(int i=0;i<fs.length;i++){
			if(fs[i].getName().toLowerCase().endsWith(".jpg")
					||fs[i].getName().toLowerCase().endsWith(".jpeg")){
				System.out.println(fs[i].getAbsolutePath());
				
				String newName=index+"";
				while(newName.length()<6) newName="0"+newName;
				try {
				im.zoomToSizeIfLarger(fs[i], new File("F:\\images\\时光(足迹) VII\\"+newName+".jpg"), 4000, JUtilImage.FORMAT_JPEG);
				fs[i].delete();
				}catch(Exception e) {
					
				}
				index++;
			}else if(fs[i].getName().toLowerCase().endsWith(".mp4")){
				System.out.println(fs[i].getAbsolutePath());
				
				String newName=index+"";
				while(newName.length()<6) newName="0"+newName;
				
				fs[i].renameTo(new File("F:\\images\\时光(足迹) VII\\"+newName+".mp4"));
				
				index++;	
			}
		}
	    
		System.exit(0);
	}

	public static void merge(JUtilImage img, File file) throws Exception {
		if (file.getAbsolutePath().indexOf("全部") > 0)
			return;

		if (file.isDirectory()) {
			File[] fs = file.listFiles();
			for (int i = 0; i < fs.length; i++) {
				merge(img, fs[i]);
			}
		} else if (!file.getName().toLowerCase().endsWith("jpg")) {
			return;
		} else {
			img.zoomToSizeIfLarger(file, new File("E:\\images\\全部\\" + (all++)+ ".jpg"), 1600, JUtilImage.FORMAT_JPG);
			System.out.println(file.getAbsolutePath());
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

	/**
	 * 将图片裁剪
	 * @param srcFile
	 * @param destFile
	 * @param leftRatio 左边裁去比率（相对图片原始宽度）
	 * @param rightRatio 右边裁去比率（相对图片原始宽度）
	 * @param topRatio 顶部裁去比率（相对图片原始高度）
	 * @param bottomRatio 底部裁去比率（相对图片原始高度）
	 * @param imageFormat
	 * @throws Exception
	 */
	public void trim(File srcFile, File destFile,double leftRatio,double rightRatio,double topRatio,double bottomRatio,String imageFormat) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		
		if(leftRatio<0
				||rightRatio<0
				||topRatio<0
				||bottomRatio<0
				||(leftRatio+rightRatio)>0.99
				||(topRatio+bottomRatio)>0.99){
			throw new Exception("截取区域设置错误");
		}
		
		Image src = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		src.flush();
		src = new ImageIcon(src).getImage();
		double oldWidth = (double) src.getWidth(this);
		double oldHeight = (double) src.getHeight(this);
		
		//左上角坐标
		int startX=(int)(oldWidth*leftRatio);
		int startY=(int)(oldHeight*topRatio);
		
		//截取后宽、高
		int newWidth=(int)(oldWidth*(1-leftRatio-rightRatio));
		int newHeight=(int)(oldHeight*(1-topRatio-bottomRatio));
		
		//截取后的区域
		Rectangle rect=new Rectangle(startX,startY,newWidth,newHeight);
		
		String suffix=srcFile.getName();
		suffix=suffix.substring(suffix.lastIndexOf(".")+1);
		
		FileInputStream fis=new FileInputStream(srcFile);
		ImageInputStream iis= ImageIO.createImageInputStream(fis);
		FileOutputStream fos=new FileOutputStream(destFile);
		
		ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ImageIO.write(bi, suffix, fos);
		
		fos.flush();
		fos.close();	
	}
	
	/**
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param angel
	 * @param imageFormat
	 * @throws Exception
	 */
	public void rotate(File srcFile, File destFile, int angel, String imageFormat) throws Exception {
		if (!chkImageFormat(imageFormat)) {
			throw new Exception("图片类型不合法");
		}
		
		Image src = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
		src.flush();
		src = new ImageIcon(src).getImage();
		
		BufferedImage rotated=Rotate(src, angel);
		
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
		
		writer.write(null, new IIOImage(rotated, null, null), param);
		writer.dispose();
		ios.flush();
		ios.close();
		os.close();				
	}
	
	
	/**
     * 对图片进行旋转
     *
     * @param src 被旋转图片
     * @param angel 旋转角度
     * @return 旋转后的图片
     */
    public static BufferedImage Rotate(Image src, int angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
       
        // 计算旋转后图片的尺寸
        Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(src_width, src_height)), angel);
        BufferedImage res = new BufferedImage(rect_des.width, rect_des.height,BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
      
        // 进行转换
        g2.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);
        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);
 
        g2.drawImage(src, null, null);
        
        return res;
    }
 
    /**
     * 计算旋转后的图片
     *
     * @param src 被旋转的图片
     * @param angel 旋转角度
     * @return 旋转后的图片
     */
    public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
        // 如果旋转的角度大于90度做相应的转换
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }
 
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);
 
        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new Rectangle(new Dimension(des_width, des_height));
    }
    
    /**
     * 
     * @param width
     * @param height
     * @param items
     * @param output
     * @return
     * @throws Exception
     */
    public BufferedImage paint(int width, int height, List<PaintItem> items, OutputStream output) throws Exception {
		BufferedImage img=paint(width, height, items);
		
		ImageIO.write(img, FORMAT_JPEG, output);
		
		output.flush();
		output.close();	
		
		return img;
	}
    
    /**
     * 
     * @param width
     * @param height
     * @param items
     * @return
     * @throws Exception
     */
    public BufferedImage paint(int width, int height, List<PaintItem> items) throws Exception {
		BufferedImage original = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
		Graphics graphics=original.getGraphics();
		
		for(int it=0; items!=null && it<items.size(); it++) {
			PaintItem item=items.get(it);
			
			if(item.object instanceof File) {
				File file=(File)item.object;
				if(!file.exists()) continue;
				
				Image img = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
				img.flush();
				img = new ImageIcon(img).getImage();
				
				item.object=img;
			}
			
			if(item.object instanceof Image) {
				Image img=(Image)item.object;
				graphics.drawImage(img, item.x, item.y, item.width, item.height, this);//绘制图片
			}else if(item.object instanceof String) {
				String txt=(String)item.object;
				
				FontMetrics metrics = graphics.getFontMetrics(item.font);
				int charHeight = metrics.getHeight();
				int txtWidth=metrics.charsWidth(txt.toCharArray(),0,txt.length());
				
				//换行
				int maxTxtWidth=item.width;
				List txtLines=new ArrayList();
				if(txtWidth>maxTxtWidth){
					for(int j=1;j<=txt.length();j++){
						int temp=metrics.charsWidth(txt.toCharArray(),0,j);
						if(temp>maxTxtWidth){
							txtLines.add(txt.substring(0,j));
							txt=txt.substring(j);
							j=1;
							
							if(charHeight*txtLines.size()>=item.height) {
								break;
							}
						}
					}
				}
				if(charHeight*txtLines.size()<item.height) {
					if(txt.length()>0) txtLines.add(txt);
				}
				//换行 end
				
				graphics.setFont(item.font);
				graphics.setColor(item.color);
				for(int j=0;j<txtLines.size();j++){
					graphics.drawString((String)txtLines.get(j),item.x,item.y+(j*charHeight));
				}
			}
		}
		
		return original;
	}
    
    /**
     * 
     * @param image
     * @param imageType
     * @return
     */
    public static InputStream image2InputStream(BufferedImage image, String imageType){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, imageType, os);
            InputStream input = new ByteArrayInputStream(os.toByteArray());
            return input;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
