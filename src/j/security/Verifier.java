package j.security;

import j.log.Logger;
import j.util.JUtilRandom;
import j.util.JUtilString;
import j.util.JUtilUUID;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author 肖炯
 *
 */
public class Verifier{
	private static Logger log=Logger.create(Verifier.class);

	
	/**
	 * 分配与验证码相关联的UUID
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public static String allotUuid(String uuid) throws Exception{
		if(uuid==null) uuid=JUtilUUID.genUUID();
	
		VerifyCode.get("verifier", 
				uuid,
				null,
				VerifyCode.TYPE_NUMBER, 
				VerifierSetting.chars(),
				VerifierSetting.timeout(),
				1000);
		return uuid;
	}
	
	/**
	 * 是否分配了该uuid
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public static boolean allotted(String uuid) throws Exception{
		if(uuid==null) return false;
		
		return VerifyCode.exists("verifier", uuid)!=null;
	}
	
	/**
	 * 验证码是否正确
	 * @param uuid
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static boolean isCorrect(String uuid,String code) throws Exception{
		if(uuid==null||code==null) return false;
		
		return VerifyCode.check("verifier", uuid,null, code,true);
	}
	
	/**
	 * 输出验证码图片（随机数字）
	 * @param flagInSession
	 * @param session
	 * @param response
	 * @param bgColor
	 * @param borderColor
	 * @param font
	 * @param width
	 * @param height
	 */
	public static void writeImage(String uuid,HttpServletResponse response) {
		try{		
			VerifyCodeBean vcb=VerifyCode.exists("verifier", uuid);
			if(vcb==null) throw new Exception("未分配UUID - "+uuid);	
			
			Color bgColor=VerifierSetting.bgColor();
			Color borderColor=VerifierSetting.borderColor();
			Color fontColor=VerifierSetting.fontColor();
			Font font=VerifierSetting.font();
			int width=VerifierSetting.width();
			int height=VerifierSetting.height();
			int chars=VerifierSetting.chars();
			//int posXOffset=VerifierSetting.posXOffset();
			int posYOffset=VerifierSetting.posYOffset();
			int noise=VerifierSetting.getNoise();
			
			String validateCode = vcb.getCode();
			
			//创建图片
			BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
			Graphics graphics = image.getGraphics();//获取图形上下文
		
			graphics.setColor(bgColor);//设定背景色
			graphics.fillRect(0, 0, width, height);
			graphics.setFont(font);//设定字体
	
			//画边框
			graphics.setColor(borderColor);
			graphics.drawRect(0,0,width-1,height-1);
			//画边框 end
	
			//取随机产生的认证码,显示到图象中
			graphics.setColor(fontColor);
			
			FontMetrics metrics = graphics.getFontMetrics(font);
		    int charHeight = metrics.getHeight();
		    int charWidth = metrics.charWidth(validateCode.charAt(0));
		    int posY=(int)Math.ceil((height-charHeight)/2+charHeight*0.7)+posYOffset;
		    //int posX=(width-charWidth*chars)/2+posXOffset;
		    //graphics.drawString(validateCode, posX, posY);

		    int prePosX=0;
			for (int i = 0; i < chars; i++) {
				int x=prePosX+charWidth+3+JUtilRandom.nextInt(3);
				prePosX=x;
				graphics.setColor(fontColor);
				graphics.drawString(validateCode.substring(i,i+1),x,posY+JUtilRandom.nextInt(3));
			}//取随机产生的认证码,显示到图象中 end
			
			//随机产生干扰线，使图象中的认证码不易被其它程序探测到
			for (int i = 0; i < noise; i++) {
				graphics.setColor(getRandColor(0,255));
				int x = JUtilRandom.nextInt(width-6);
				int y = JUtilRandom.nextInt(height-6);
				graphics.drawLine(x, y, x + 6, y + 6);
			}//随机产生干扰线，使图象中的认证码不易被其它程序探测到 end
			
			graphics.dispose();//图象生效
	
			//输出图象
			OutputStream os= response.getOutputStream();
			ImageIO.write(image, "JPEG",os);
			os.flush();
			os.close();
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
		}
	}
	
	/**
	 * 输出验证码图片至文件，用于测试
	 * @param file
	 * @param bgColor
	 * @param borderColor
	 * @param font
	 * @param fontColor
	 * @param width
	 * @param height
	 * @param length
	 */
	public static void writeImage(File file,
			Color bgColor,
			Color borderColor,
			Font font,
			Color fontColor,
			int width,
			int height,
			int length,
			int posXOffset,
			int posYOffset) {
		try{				
			//创建图片
			BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
			Graphics graphics = image.getGraphics();//获取图形上下文
		
			graphics.setColor(bgColor);//设定背景色
			graphics.fillRect(0, 0, width, height);
			graphics.setFont(font);//设定字体
	
			//画边框
			graphics.setColor(borderColor);
			graphics.drawRect(0,0,width-1,height-1);
			//画边框 end
	
			//取随机产生的认证码,显示到图象中
			String validateCode = JUtilString.randomNum(length);
			graphics.setColor(fontColor);
			
		    FontMetrics metrics = graphics.getFontMetrics(font);
		    int charHeight = metrics.getHeight();
		    int charWidth = metrics.charWidth(validateCode.charAt(0));
		    int posY=(int)Math.ceil((height-charHeight)/2+charHeight*0.7)+posYOffset;
		    //int posX=(width-charWidth*length)/2+posXOffset;
		    //graphics.drawString(validateCode, posX, posY);

			int prePosX=0;
			for (int i = 0; i < length; i++) {
				int x=prePosX+charWidth+3+JUtilRandom.nextInt(3);
				prePosX=x;
				graphics.setColor(fontColor);
				graphics.drawString(validateCode.substring(i,i+1),x,posY+JUtilRandom.nextInt(3));
			}//取随机产生的认证码,显示到图象中 end
			
			//随机产生干扰线，使图象中的认证码不易被其它程序探测到
			for (int i = 0; i < 32; i++) {
				graphics.setColor(getRandColor(0,255));
				int x = JUtilRandom.nextInt(width-6);
				int y = JUtilRandom.nextInt(height-6);
				graphics.drawLine(x, y, x + 6, y + 6);
			}//随机产生干扰线，使图象中的认证码不易被其它程序探测到 end
			
			graphics.dispose();//图象生效
	
			//输出图象
			OutputStream os= new FileOutputStream(file);
			ImageIO.write(image, "JPEG",os);
			os.flush();
			os.close();
		}catch(IOException e){
			log.log(e,Logger.LEVEL_ERROR);
		}
	}

	/**
	 * 给定范围获得随机颜色
	 * 
	 * @param min
	 * @param max
	 * @return Color
	 */
	public static Color getRandColor(int min, int max) {
		Random random = new Random();
		if (min > 255) {
			min = 255;
		}
		if (max > 255) {
			max = 255;
		}
		int r = min + random.nextInt(max - min);
		int g = min + random.nextInt(max - min);
		int b = min + random.nextInt(max - min);
		return new Color(r, g, b);
	}
	
	/**
	 * test
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		System.out.println("......");
		Color bg=new Color(255, 255,255);
		Color border=new Color(127,157,185);
		Color fontColor=new Color(255,110,0);
		
		Font font=null;
//		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		String[] fontFamilies = ge.getAvailableFontFamilyNames();
		for(int i=0;i<100;i++){
			font=new Font("Arial Narrow",Font.BOLD,14);
			Verifier.writeImage(new File("e:/tmp/"+i+".jpg"),bg,border,font,fontColor,62,20,4,1,1);
		}
	}
}


