package j.tool.BarCode; 

import j.log.Logger;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.jbarcode.JBarcode;
import org.jbarcode.encode.Code128Encoder;
import org.jbarcode.encode.EAN13Encoder;
import org.jbarcode.encode.EAN8Encoder;
import org.jbarcode.paint.BaseLineTextPainter;
import org.jbarcode.paint.EAN13TextPainter;
import org.jbarcode.paint.EAN8TextPainter;
import org.jbarcode.paint.WidthCodedPainter;
import org.jbarcode.util.ImageUtil;
/** 
* 支持EAN13, EAN8, UPCA, UPCE, Code 3 of 9, Codabar, Code 11, Code 93, Code 128, MSI/Plessey, Interleaved 2 of PostNet等
* 利用jbarcode生成各种条形码！测试成功！分享给大家！
*/ 
public class JBarCode { 
	private static Logger log=Logger.create(JBarCode.class);
	public static final String IMAGE_JPEG="jpeg";
	public static final String IMAGE_GIF="gif";
	public static final String IMAGE_PNG="png";
	
	/**
	 * 
	 * @param num
	 * @param imageType
	 */
	public static BufferedImage createCode128(String num,String imageType) { 
		try { 
			JBarcode code = new JBarcode(Code128Encoder.getInstance(),WidthCodedPainter.getInstance(),BaseLineTextPainter.getInstance()); 
			BufferedImage image = code.createBarcode(num); 
			
			return image;
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		} 
	} 

	/**
	 * 
	 * @param num
	 * @param imageType
	 * @param showText
	 */
	public static BufferedImage createCode128(String num,String imageType,boolean showText) { 
		try { 
			JBarcode code = new JBarcode(Code128Encoder.getInstance(),WidthCodedPainter.getInstance(),BaseLineTextPainter.getInstance()); 
			code.setShowText(showText);
			BufferedImage image = code.createBarcode(num); 
			
			return image;
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		} 
	} 
	
	/**
	 * 
	 * @param num
	 * @param imageType
	 * @param xDimension
	 */
	public static BufferedImage createCode128(String num,String imageType,double xDimension) { 
		try { 
			JBarcode code = new JBarcode(Code128Encoder.getInstance(),WidthCodedPainter.getInstance(),BaseLineTextPainter.getInstance()); 
			code.setXDimension(xDimension);
			BufferedImage image = code.createBarcode(num); 
			
			return image;
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		} 
	} 
	
	/**
	 * 
	 * @param num
	 * @param imageType
	 * @param xDimension
	 * @param showText
	 */
	public static BufferedImage createCode128(String num,String imageType,double xDimension,boolean showText) { 
		try { 
			JBarcode code = new JBarcode(Code128Encoder.getInstance(),WidthCodedPainter.getInstance(),BaseLineTextPainter.getInstance()); 
			code.setXDimension(xDimension);
			code.setShowText(showText);
			BufferedImage image = code.createBarcode(num); 
			
			return image;
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		} 
	} 
	
	/**
	 * 
	 * @param num
	 * @param imageType
	 */
	public static BufferedImage createCodeEAN13(String num,String imageType) { 
		try { 
			JBarcode code = new JBarcode(EAN13Encoder.getInstance(),WidthCodedPainter.getInstance(),EAN13TextPainter.getInstance()); 
			BufferedImage image = code.createBarcode(num); 
			
			return image;
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imageType
	 * @param showText
	 */
	public static BufferedImage createCodeEAN13(String num,String imageType,boolean showText) { 
		try { 
			JBarcode code = new JBarcode(EAN13Encoder.getInstance(),WidthCodedPainter.getInstance(),EAN13TextPainter.getInstance()); 
			code.setShowText(showText);
			BufferedImage image = code.createBarcode(num); 
			
			return image;
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imageType
	 * @param xDimension
	 */
	public static BufferedImage createCodeEAN13(String num,String imageType,double xDimension) { 
		try { 
			JBarcode code = new JBarcode(EAN13Encoder.getInstance(),WidthCodedPainter.getInstance(),EAN13TextPainter.getInstance()); 
			code.setXDimension(xDimension);
			BufferedImage image = code.createBarcode(num); 
			
			return image;
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imageType
	 * @param xDimension
	 * @param showText
	 */
	public static BufferedImage createCodeEAN13(String num,String imageType,double xDimension,boolean showText) { 
		try { 
			JBarcode code = new JBarcode(EAN13Encoder.getInstance(),WidthCodedPainter.getInstance(),EAN13TextPainter.getInstance()); 
			code.setXDimension(xDimension);
			code.setShowText(showText);
			BufferedImage image = code.createBarcode(num); 
			
			return image;
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imageType
	 */
	public static BufferedImage createCodeEAN8(String num,String imageType) { 
		try { 
			JBarcode code = new JBarcode(EAN8Encoder.getInstance(),WidthCodedPainter.getInstance(),EAN8TextPainter.getInstance()); 
			BufferedImage image = code.createBarcode(num); 
			
			return image;
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imageType
	 * @param showText
	 */
	public static BufferedImage createCodeEAN8(String num,String imageType,boolean showText) { 
		try { 
			JBarcode code = new JBarcode(EAN8Encoder.getInstance(),WidthCodedPainter.getInstance(),EAN8TextPainter.getInstance()); 
			code.setShowText(showText);
			BufferedImage image = code.createBarcode(num); 
			
			return image;
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imageType
	 * @param xDimension
	 */
	public static BufferedImage createCodeEAN8(String num,String imageType,double xDimension) { 
		try { 
			JBarcode code = new JBarcode(EAN8Encoder.getInstance(),WidthCodedPainter.getInstance(),EAN8TextPainter.getInstance()); 
			code.setXDimension(xDimension);
			BufferedImage image = code.createBarcode(num); 
			
			return image;
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imageType
	 * @param xDimension
	 * @param showText
	 */
	public static BufferedImage createCodeEAN8(String num,String imageType,double xDimension,boolean showText) { 
		try { 
			JBarcode code = new JBarcode(EAN8Encoder.getInstance(),WidthCodedPainter.getInstance(),EAN8TextPainter.getInstance()); 
			code.setXDimension(xDimension);
			code.setShowText(showText);
			BufferedImage image = code.createBarcode(num); 
			
			return image;
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}  
	} 

	/**
	 * 
	 * @param num
	 * @param imagePath
	 * @param imageType
	 */
	public static void createCode128(String num,String imagePath,String imageType) { 
		try { 
			BufferedImage image = createCode128(num, imageType); 
			saveToFile(image, imagePath,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		} 
	} 

	/**
	 * 
	 * @param num
	 * @param imagePath
	 * @param imageType
	 * @param showText
	 */
	public static void createCode128(String num,String imagePath,String imageType,boolean showText) { 
		try { 
			BufferedImage image = createCode128(num,imageType,showText);
			
			saveToFile(image, imagePath,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		} 
	} 
	
	/**
	 * 
	 * @param num
	 * @param imagePath
	 * @param imageType
	 * @param xDimension
	 */
	public static void createCode128(String num,String imagePath,String imageType,double xDimension) { 
		try { 
			BufferedImage image = createCode128(num,imageType,xDimension);
			
			saveToFile(image, imagePath,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		} 
	} 
	
	/**
	 * 
	 * @param num
	 * @param imagePath
	 * @param imageType
	 * @param xDimension
	 * @param showText
	 */
	public static void createCode128(String num,String imagePath,String imageType,double xDimension,boolean showText) { 
		try { 
			BufferedImage image = createCode128(num,imageType,xDimension,showText);
			
			saveToFile(image, imagePath,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		} 
	} 
	
	/**
	 * 
	 * @param num
	 * @param imagePath
	 * @param imageType
	 */
	public static void createCodeEAN13(String num,String imagePath,String imageType) { 
		try { 
			BufferedImage image = createCodeEAN13(num,imageType); 
			
			saveToFile(image, imagePath,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imagePath
	 * @param imageType
	 * @param showText
	 */
	public static void createCodeEAN13(String num,String imagePath,String imageType,boolean showText) { 
		try { 
			BufferedImage image = createCodeEAN13(num,imageType,showText); 
			
			saveToFile(image, imagePath,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imagePath
	 * @param imageType
	 * @param xDimension
	 */
	public static void createCodeEAN13(String num,String imagePath,String imageType,double xDimension) { 
		try { 
			BufferedImage image = createCodeEAN13(num,imageType,xDimension); 
			
			saveToFile(image, imagePath,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imagePath
	 * @param imageType
	 * @param xDimension
	 * @param showText
	 */
	public static void createCodeEAN13(String num,String imagePath,String imageType,double xDimension,boolean showText) { 
		try { 
			BufferedImage image = createCodeEAN13(num,imageType,xDimension,showText); 
			
			saveToFile(image, imagePath,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imagePath
	 * @param imageType
	 */
	public static void createCodeEAN8(String num,String imagePath,String imageType) { 
		try { 
			BufferedImage image = createCodeEAN8(num, imageType); 
			
			saveToFile(image, imagePath,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imagePath
	 * @param imageType
	 * @param showText
	 */
	public static void createCodeEAN8(String num,String imagePath,String imageType,boolean showText) { 
		try { 
			BufferedImage image = createCodeEAN8(num,imageType,showText); 
			
			saveToFile(image, imagePath,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param imagePath
	 * @param imageType
	 * @param xDimension
	 */
	public static void createCodeEAN8(String num,String imagePath,String imageType,double xDimension) { 
		try { 
			BufferedImage image = createCodeEAN8(num,imageType,xDimension); 
			
			saveToFile(image, imagePath,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	
	/**
	 * 
	 * @param num
	 * @param response
	 * @param imageType
	 */
	public static void createCode128(String num,HttpServletResponse response,String imageType) { 
		try { 
			BufferedImage image = createCode128(num, imageType); 
			writeImage(image, response,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		} 
	} 

	/**
	 * 
	 * @param num
	 * @param response
	 * @param imageType
	 * @param showText
	 */
	public static void createCode128(String num,HttpServletResponse response,String imageType,boolean showText) { 
		try { 
			BufferedImage image = createCode128(num,imageType,showText);
			
			writeImage(image, response,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		} 
	} 
	
	/**
	 * 
	 * @param num
	 * @param response
	 * @param imageType
	 * @param xDimension
	 */
	public static void createCode128(String num,HttpServletResponse response,String imageType,double xDimension) { 
		try { 
			BufferedImage image = createCode128(num,imageType,xDimension);
			
			writeImage(image, response,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		} 
	} 
	
	/**
	 * 
	 * @param num
	 * @param response
	 * @param imageType
	 * @param xDimension
	 * @param showText
	 */
	public static void createCode128(String num,HttpServletResponse response,String imageType,double xDimension,boolean showText) { 
		try { 
			BufferedImage image = createCode128(num,imageType,xDimension,showText);
			
			writeImage(image, response,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		} 
	} 
	
	/**
	 * 
	 * @param num
	 * @param response
	 * @param imageType
	 */
	public static void createCodeEAN13(String num,HttpServletResponse response,String imageType) { 
		try { 
			BufferedImage image = createCodeEAN13(num,imageType); 
			
			writeImage(image, response,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param response
	 * @param imageType
	 * @param showText
	 */
	public static void createCodeEAN13(String num,HttpServletResponse response,String imageType,boolean showText) { 
		try { 
			BufferedImage image = createCodeEAN13(num,imageType,showText); 
			
			writeImage(image, response,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param response
	 * @param imageType
	 * @param xDimension
	 */
	public static void createCodeEAN13(String num,HttpServletResponse response,String imageType,double xDimension) { 
		try { 
			BufferedImage image = createCodeEAN13(num,imageType,xDimension); 
			
			writeImage(image, response,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param response
	 * @param imageType
	 * @param xDimension
	 * @param showText
	 */
	public static void createCodeEAN13(String num,HttpServletResponse response,String imageType,double xDimension,boolean showText) { 
		try { 
			BufferedImage image = createCodeEAN13(num,imageType,xDimension,showText); 
			
			writeImage(image, response,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param response
	 * @param imageType
	 */
	public static void createCodeEAN8(String num,HttpServletResponse response,String imageType) { 
		try { 
			BufferedImage image = createCodeEAN8(num, imageType); 
			
			writeImage(image, response,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param response
	 * @param imageType
	 * @param showText
	 */
	public static void createCodeEAN8(String num,HttpServletResponse response,String imageType,boolean showText) { 
		try { 
			BufferedImage image = createCodeEAN8(num,imageType,showText); 
			
			writeImage(image, response,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	} 
	
	/**
	 * 
	 * @param num
	 * @param response
	 * @param imageType
	 * @param xDimension
	 */
	public static void createCodeEAN8(String num,HttpServletResponse response,String imageType,double xDimension) { 
		try { 
			BufferedImage image = createCodeEAN8(num,imageType,xDimension); 
			
			writeImage(image, response,imageType);
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		}  
	}
	

	/**
	 * 
	 * @param image
	 * @param imagePath
	 * @param imageType
	 */
	private static void saveToFile(BufferedImage image, String imagePath, String imageType) { 
		try { 
			FileOutputStream fos = new FileOutputStream(imagePath); 
			ImageUtil.encodeAndWrite(image, imageType, fos, ImageUtil.DEFAULT_DPI, ImageUtil.DEFAULT_DPI); 
			fos.close(); 
		}catch (Exception e) { 
			log.log(e, Logger.LEVEL_ERROR);
		} 
	} 
	
	/**
	 * 
	 * @param image
	 * @param response
	 */
	private static void writeImage(BufferedImage image,HttpServletResponse response,String imageType) {
		try{
			//输出图象
			OutputStream os= response.getOutputStream();
			ImageIO.write(image,imageType.toUpperCase(),os);
			os.flush();
			os.close();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
		}
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		createCode128("20041701345","F:/temp/order.png",IMAGE_PNG,0.8,true);
	}
}