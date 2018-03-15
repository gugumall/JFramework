package j.tool.QRCoder;

import java.awt.image.BufferedImage;

import jp.sourceforge.qrcode.data.QRCodeImage;

/**
 * 
 * @author JFramework
 * 
 */
public class JQRCodeImage implements QRCodeImage {
	BufferedImage bufImg;

	/**
	 * 
	 * @param bufImg
	 */
	public JQRCodeImage(BufferedImage bufImg) {
		this.bufImg = bufImg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jp.sourceforge.qrcode.data.QRCodeImage#getHeight()
	 */
	public int getHeight() {
		return bufImg.getHeight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jp.sourceforge.qrcode.data.QRCodeImage#getPixel(int, int)
	 */
	public int getPixel(int x, int y) {
		return bufImg.getRGB(x, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jp.sourceforge.qrcode.data.QRCodeImage#getWidth()
	 */
	public int getWidth() {
		return bufImg.getWidth();
	}
}
