package jp.sourceforge.reedsolomon;

/**
 * Galois Field GF(2^8) for QR-CODE
 *
 * @author Masayuki Miyazaki
 * http://sourceforge.jp/projects/reedsolomon/
 */
public final class GaloisQR
	extends Galois {
	public static final int POLYNOMIAL = 0x1d;		// x^8 + x^4 + x^3 + x^2 + 1
	private static final Galois instance = new GaloisQR();

	private GaloisQR() {
		super(POLYNOMIAL, 0);
	}

	public static Galois getInstance() {
		return instance;
	}
}
