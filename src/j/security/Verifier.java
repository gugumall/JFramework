package j.security;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import j.log.Logger;
import j.util.JUtilRandom;
import j.util.JUtilString;
import j.util.JUtilUUID;

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
				100);
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
			int posXOffset=VerifierSetting.posXOffset();
			int posYOffset=VerifierSetting.posYOffset();
			int noise=VerifierSetting.getNoise();
			
			String validateCode = vcb.getCode();
			
			BufferedImage image=drawRandomText(bgColor,
					borderColor,
					font,
					fontColor,
					width,
					height,
					posXOffset,
					posYOffset,
					validateCode);
	
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
			//取随机产生的认证码,显示到图象中
			String validateCode = JUtilString.randomNum(length);
			System.out.println("validateCode:"+validateCode);
			
			BufferedImage image=drawRandomText(bgColor,
					borderColor,
					font,
					fontColor,
					width,
					height,
					posXOffset,
					posYOffset,
					validateCode);
	
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
	 * 
	 * @param bgColor
	 * @param borderColor
	 * @param font
	 * @param fontColor
	 * @param width
	 * @param height
	 * @param length
	 * @param posXOffset
	 * @param posYOffset
	 * @param chars
	 * @return
	 */
	public static BufferedImage drawRandomText(Color bgColor,
			Color borderColor,
			Font font,
			Color fontColor,
			int width,
			int height,
			int posXOffset,
			int posYOffset,
			String chars) {
		if(font==null) {
			font=(new Font("微软雅黑", Font.BOLD, 20));
		}
		
		BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D)image.getGraphics();
		graphics.setColor(bgColor==null?Color.WHITE:bgColor);//设置画笔颜色-验证码背景色
		graphics.fillRect(0, 0, width, height);//填充背景
		graphics.setFont(font);
		
		FontMetrics metrics = graphics.getFontMetrics(font);
	    int charHeight = metrics.getHeight();
	    int charWidth = metrics.charWidth(chars.charAt(0));
	    int posY=(int)Math.ceil((height-charHeight)/2+charHeight*0.7)+posYOffset;
	    int prePosX=0;
	    
 	    Random random = new Random();
 	    for(int i = 0; i < chars.length(); i++){
 	    	String ch=chars.substring(i, i+1);
 	    	
			int posX=prePosX+JUtilRandom.nextInt(3);
			prePosX=posX+charWidth;
			
 	    	graphics.setColor(fontColor==null?getRandColor(0, 150):fontColor);
 	        
 	    	//设置字体旋转角度
 	        int degree = random.nextInt() % 30;  //角度小于30度
 	        
 	        int y=posY+JUtilRandom.nextInt(3);
 	       
 	        //正向旋转
 	        graphics.rotate(degree * Math.PI / 180, posX, y);
 	        graphics.drawString(ch, posX, y);
 	        
 	        //反向旋转
 	        graphics.rotate(-degree * Math.PI / 180, posX, y);
 	    }
 	    
 	    //画干扰线
 	    for (int i = 0; i <4; i++) {
 	        // 设置随机颜色
 	    	graphics.setColor(getRandColor());
 	        
 	    	// 随机画线
 	    	graphics.drawLine(random.nextInt(width), 
 	    			random.nextInt(height),
 	    			random.nextInt(width), 
 	    			random.nextInt(height));
 	    }
 	    
 	    //添加噪点
 	    for(int i=0;i<20;i++){
 	    	int x1 = random.nextInt(width);
 	    	int y1 = random.nextInt(height);
 	    	graphics.setColor(getRandColor());
 	    	graphics.fillRect(x1, y1, 1, 1);
 	    }
 	    
		graphics.dispose();//图象生效
 	    
 	    return image;
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
	 * 
	 * @return
	 */
	public static Color getRandColor() {
	    Random ran = new Random();
	    Color color = new Color(ran.nextInt(256),
	    		ran.nextInt(256),
	    		ran.nextInt(256));
	    return color;
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
		Color fontColor=null;//new Color(255,110,0);
		
		Font font=null;
//		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		String[] fontFamilies = ge.getAvailableFontFamilyNames();
		for(int i=0;i<10;i++){
			font=new Font("Arial Narrow",Font.BOLD,24);
			Verifier.writeImage(new File("f:/temp/"+i+".jpg"),
					bg,
					border,
					font,
					fontColor,
					80,30,4,1,1);
		}
		
		System.exit(0);
	}
}


