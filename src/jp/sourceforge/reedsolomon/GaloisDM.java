package jp.sourceforge.reedsolomon;

/**
 * Galois Field GF(2^8) for Data Matrix Barcode
 *
 * @author Masayuki Miyazaki
 * http://sourceforge.jp/projects/reedsolomon/
 */
public final class GaloisDM
	extends Galois {
	public static final int POLYNOMIAL = 0x2d;		// x^8 + x^5 + x^3 + x^2 + 1
	private static final Galois instance = new GaloisDM();

	private GaloisDM() {
		super(POLYNOMIAL, 1);
	}

	public static Galois getInstance() {
		return instance;
	}
}
