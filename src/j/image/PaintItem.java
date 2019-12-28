package j.image;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

/**
 * 
 * @author 肖炯
 *
 * 2019年12月27日
 *
 * <b>功能描述</b>
 */
public class PaintItem implements Serializable{
	private static final long serialVersionUID = 1L;

	public Object object;
	public int x;
	public int y;
	public int width;
	public int height;
	public Color color;
	public Font font;
	
	/**
	 * 
	 * @param object
	 * @param color
	 * @param font
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public PaintItem(Object object, Color color, Font font, int x, int y, int width, int height) {
		this.object=object;
		this.color=color;
		this.font=font;
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}
}
