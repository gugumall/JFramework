package j.tool.QRCoder;

import j.log.Logger;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

import com.swetake.util.Qrcode;


/**
 * 
 * @author JFramework
 * 
 */
public class JQRCode {
	private static Logger log=Logger.create(JQRCode.class);

	////////////////////////图片输出到文件///////////////////////
	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content 存储内容
	 * @param imgPath 图片路径
	 */
	public static void encode(String content, String imgPath) {
		encode(content,imgPath,Color.BLACK);
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content 存储内容
	 * @param imgPath 图片路径
	 * @param imgType 图片类型
	 */
	public static void encode(String content, String imgPath, String imgType) {
		encode(content,imgPath,imgType,Color.BLACK);
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content 存储内容
	 * @param imgPath 图片路径
	 * @param imgType 图片类型
	 * @param size 二维码尺寸
	 */
	public static void encode(String content, String imgPath, String imgType,int size) {
		encode(content,imgPath,imgType,size,Color.BLACK);
	}
	
	
	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content 存储内容
	 * @param imgPath 图片路径
	 */
	public static void encode(String content, String imgPath, Color color) {
		encode(content, imgPath, "png",color);
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content 存储内容
	 * @param imgPath 图片路径
	 * @param imgType 图片类型
	 */
	public static void encode(String content, String imgPath, String imgType, Color color) {
		encode(content, imgPath, imgType, 7,color);
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content 存储内容
	 * @param imgPath 图片路径
	 * @param imgType 图片类型
	 * @param size 二维码尺寸
	 */
	public static void encode(String content, String imgPath, String imgType,int size, Color color) {
		try {
			BufferedImage bufImg = encode(content, imgType, size,color);

			File imgFile = new File(imgPath);
			imgFile.mkdirs();
			
			// 生成二维码QRCode图片
			ImageIO.write(bufImg, imgType, imgFile);
		} catch (Exception e) {
			log.log(e,Logger.LEVEL_ERROR);
		}
	}

	////////////////////////图片输出到输出流///////////////////////
	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content 存储内容
	 * @param output 输出流
	 */
	public static void encode(String content, OutputStream output) {
		encode(content,output,Color.BLACK);
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content 存储内容
	 * @param output 输出流
	 * @param imgType 图片类型
	 */
	public static void encode(String content, OutputStream output,String imgType) {
		encode(content,output,imgType,Color.BLACK);
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content 存储内容
	 * @param output 输出流
	 * @param imgType 图片类型
	 * @param size  二维码尺寸
	 */
	public static void encode(String content, OutputStream output,String imgType, int size) {
		encode(content,output,imgType,size,Color.BLACK);
	}
	
	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content 存储内容
	 * @param output 输出流
	 */
	public static void encode(String content, OutputStream output,Color color) {
		encode(content, output, "png",color);
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content 存储内容
	 * @param output 输出流
	 * @param imgType 图片类型
	 */
	public static void encode(String content, OutputStream output,String imgType,Color color) {
		encode(content, output, imgType, 7,color);
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content 存储内容
	 * @param output 输出流
	 * @param imgType 图片类型
	 * @param size  二维码尺寸
	 */
	public static void encode(String content, OutputStream output,String imgType, int size,Color color) {
		try {
			BufferedImage bufImg = encode(content, imgType, size,color);
			// 生成二维码QRCode图片
			ImageIO.write(bufImg, imgType, output);
			
			output.flush();
			output.close();
		} catch (Exception e) {
			log.log(e,Logger.LEVEL_ERROR);
		}
	}
	
	/**
	 * 
	 * @param content
	 * @param imgType
	 * @param size
	 * @param color
	 * @return
	 */
	public static BufferedImage encode(String content, String imgType, int size, Color color) {
		BufferedImage bufImg = null;
		try {
			Qrcode qrcodeHandler = new Qrcode();
			// 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
			qrcodeHandler.setQrcodeErrorCorrect('M');
			qrcodeHandler.setQrcodeEncodeMode('B');
			
			// 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
			qrcodeHandler.setQrcodeVersion(size);
			
			// 获得内容的字节数组，设置编码格式
			byte[] contentBytes = content.getBytes("utf-8");
			
			// 图片尺寸
			int imgSize = 67 + 12 * (size - 1);
			bufImg = new BufferedImage(imgSize, imgSize,BufferedImage.TYPE_INT_RGB);
			Graphics2D gs = bufImg.createGraphics();
			
			// 设置背景颜色
			gs.setBackground(Color.WHITE);
			gs.clearRect(0, 0, imgSize, imgSize);

			// 设定图像颜色> BLACK
			gs.setColor(color);
			
			// 设置偏移量，不设置可能导致解析出错
			int pixoff = 2;
			
			// 输出内容> 二维码
			if (contentBytes.length > 0 && contentBytes.length < 800) {
				boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
				for (int i = 0; i < codeOut.length; i++) {
					for (int j = 0; j < codeOut.length; j++) {
						if (codeOut[j][i]) {
							gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
						}
					}
				}
			} else {
				throw new Exception("QRCode content bytes length = "+ contentBytes.length + " not in [0, 800].");
			}
			gs.dispose();
			bufImg.flush();
		} catch (Exception e) {
			log.log(e,Logger.LEVEL_ERROR);
		}
		return bufImg;
	}

	/**
	 * 解析二维码（QRCode）
	 * 
	 * @param imgPath 图片路径
	 * @return
	 */
	public static String decode(String imgPath) {
		// QRCode 二维码图片的文件
		File imageFile = new File(imgPath);
		BufferedImage bufImg = null;
		String content = null;
		try {
			bufImg = ImageIO.read(imageFile);
			QRCodeDecoder decoder = new QRCodeDecoder();
			content = new String(decoder.decode(new JQRCodeImage(bufImg)), "utf-8");
		} catch (IOException e) {
			log.log(e,Logger.LEVEL_ERROR);
		} catch (DecodingFailedException e) {
			log.log(e,Logger.LEVEL_ERROR);
		}
		return content;
	}

	/**
	 * 解析二维码（QRCode）
	 * 
	 * @param input 输入流
	 * @return
	 */
	public static String decode(InputStream input) {
		BufferedImage bufImg = null;
		String content = null;
		try {
			bufImg = ImageIO.read(input);
			QRCodeDecoder decoder = new QRCodeDecoder();
			content = new String(decoder.decode(new JQRCodeImage(bufImg)), "utf-8");
			
			input.close();
			input=null;
		} catch (IOException e) {
			log.log(e,Logger.LEVEL_ERROR);
		} catch (DecodingFailedException e) {
			log.log(e,Logger.LEVEL_ERROR);
		}
		return content;
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		String imgPath = "F:\\temp\\taobao2.png";
//		String encoderContent = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM={'k1':'','k2':'023','k3':'','k4':'T6','k5':'755123456789','k6':''}";
		
//
//		String pathToCMYKProfile="E:\\software\\adobe\\Adobe ICC Profiles\\CMYK\\UncoatedFOGRA29.icc";
//		
//		ColorSpace cspace = new ICC_ColorSpace(ICC_Profile.getInstance(pathToCMYKProfile));
//		
//		Color color=new Color(cspace,new float[]{0,0,0,1},1);
		
//		JQRCode.encode(encoderContent, imgPath, "png",8);//路径形式操作
		
		//输出流形式操作
		//File tdcFile=File.createTempFile("jframework",".tdc",new File("E:/tmp"));
//		OutputStream output = new FileOutputStream(imgPath);
//	 	TDC.encode(encoderContent, output,"png",4);
//
		String decoderContent = JQRCode.decode(new FileInputStream(imgPath));
		System.out.println("解析结果如下 - "+decoderContent);
	}
}
