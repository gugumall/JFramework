package j.security;

import j.nvwa.Nvwa;
import j.util.JUtilMath;

import java.awt.Color;
import java.awt.Font;

/**
 * 
 * @author 肖炯
 *
 */
public class VerifierSetting{
	/**
	 * 
	 * @return
	 */
	public static Color bgColor() {
		String[] p=Nvwa.getParameter("VerifierCodeSetting","bgColor").split(",");

		return new Color(Integer.parseInt(p[0]),Integer.parseInt(p[1]),Integer.parseInt(p[2]));
	}

	/**
	 * 
	 * @return
	 */
	public static Color borderColor() {
		String[] p=Nvwa.getParameter("VerifierCodeSetting","borderColor").split(",");

		return new Color(Integer.parseInt(p[0]),Integer.parseInt(p[1]),Integer.parseInt(p[2]));
	}

	/**
	 * 
	 * @return
	 */
	public static Color fontColor() {
		String[] p=Nvwa.getParameter("VerifierCodeSetting","fontColor").split(",");

		return new Color(Integer.parseInt(p[0]),Integer.parseInt(p[1]),Integer.parseInt(p[2]));
	}

	/**
	 * 
	 * @return
	 */
	public static Font font() {
		String fontName=Nvwa.getParameter("VerifierCodeSetting","fontName");
		
		String fontStyleName=Nvwa.getParameter("VerifierCodeSetting","fontStyle");
		int fontStyle=0;
		if("PLAIN".equalsIgnoreCase(fontStyleName)) fontStyle=0;
		else if("BOLD".equalsIgnoreCase(fontStyleName)) fontStyle=1;
		else if("ITALIC".equalsIgnoreCase(fontStyleName)) fontStyle=2;
		
		int fontSize=Integer.parseInt(Nvwa.getParameter("VerifierCodeSetting","fontSize"));
		
		return new Font(fontName,fontStyle,fontSize);
	}

	/**
	 * 
	 * @return
	 */
	public static int width() {
		return Integer.parseInt(Nvwa.getParameter("VerifierCodeSetting","width"));
	}

	/**
	 * 
	 * @return
	 */
	public static int height() {
		return Integer.parseInt(Nvwa.getParameter("VerifierCodeSetting","height"));
	}

	/**
	 * 
	 * @return
	 */
	public static int chars() {
		return Integer.parseInt(Nvwa.getParameter("VerifierCodeSetting","chars"));
	}

	/**
	 * 
	 * @return
	 */
	public static int posXOffset() {
		return Integer.parseInt(Nvwa.getParameter("VerifierCodeSetting","posXOffset"));
	}

	/**
	 * 
	 * @return
	 */
	public static int posYOffset() {
		return Integer.parseInt(Nvwa.getParameter("VerifierCodeSetting","posYOffset"));
	}

	/**
	 * 
	 * @return
	 */
	public static long timeout() {
		return Long.parseLong(Nvwa.getParameter("VerifierCodeSetting","timeout"));
	}

	/**
	 * 干扰线条数
	 * @return
	 */
	public static int getNoise() {
		if(JUtilMath.isInt(Nvwa.getParameter("VerifierCodeSetting","noise"))){
			return Integer.parseInt(Nvwa.getParameter("VerifierCodeSetting","noise"));
		}else{
			return 32;
		}
	}
}
