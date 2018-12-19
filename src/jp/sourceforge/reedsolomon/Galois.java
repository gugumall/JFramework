package jp.sourceforge.reedsolomon;

/**
 * Galois Field GF(2^8)
 *
 * @author Masayuki Miyazaki
 * http://sourceforge.jp/projects/reedsolomon/
 */
public class Galois {
	private final int polynomial;
	private final int symStart;
	private int[] expTbl = new int[255 * 2];
	private int[] logTbl = new int[255 + 1];

	protected Galois(int polynomial, int symStart) {
		this.polynomial = polynomial;
		this.symStart = symStart;
		initGaloisTable();
	}

	private void initGaloisTable() {
		int d = 1;
		for(int i = 0; i < 255; i++) {
			expTbl[i] = expTbl[255 + i] = d;
			logTbl[d] = i;
			d <<= 1;
			if((d & 0x100) != 0) {
				d = (d ^ polynomial) & 0xff;
			}
		}
	}

	public int toExp(int a) {
		return expTbl[a];
	}

	public int toLog(int a) {
		return logTbl[a];
	}

	public int toPos(int length, int a) {
		return length - 1 - logTbl[a];
	}

	public int mul(int a, int b)	{
		return (a == 0 || b == 0)? 0 : expTbl[logTbl[a] + logTbl[b]];
	}

	public int mulExp(int a, int b)	{
		return (a == 0)? 0 : expTbl[logTbl[a] + b];
	}

	public int div(int a, int b) {
		return (a == 0)? 0 : expTbl[logTbl[a] - logTbl[b] + 255];
	}

	public int divExp(int a, int b) {
		return (a == 0)? 0 : expTbl[logTbl[a] - b + 255];
	}

	public int inv(int a) {
		return expTbl[255 - logTbl[a]];
	}

	public int[] mulPoly(int[] a, int[] b, int jisu) {
		int[] seki = new int[jisu];
		final int ia2 = Math.min(jisu, a.length);
		for(int ia = 0; ia < ia2; ia++) {
			if(a[ia] != 0) {
				final int loga = logTbl[a[ia]];
				final int ib2 = Math.min(b.length, jisu - ia);
				for(int ib = 0; ib < ib2; ib++) {
					if(b[ib] != 0) {
						seki[ia + ib] ^= expTbl[loga + logTbl[b[ib]]];
					}
				}
			}
		}
		return seki;
	}

	public boolean calcSyndrome(int[] data, int length, int[] syn) {
		int hasErr = 0;
		for(int i = 0, s = symStart; i < syn.length;  i++, s++) {
			int wk = 0;
			for(int idx = 0; idx < length; idx++) {
				if(wk != 0) {
					wk = expTbl[logTbl[wk] + s];
				}
				wk ^= data[idx];
			}
			syn[i] = wk;
			hasErr |= wk;
		}
		return hasErr == 0;
	}

	public int[] makeEncodeGx(int npar) {
		int[] encodeGx = new int[npar];
		encodeGx[npar - 1] = 1;
		for(int i = 0, kou = symStart; i < npar; i++, kou++) {
			int ex = toExp(kou);
			for(int j = 0; j < npar - 1; j++) {
				encodeGx[j] = mul(encodeGx[j], ex) ^ encodeGx[j + 1];
			}
			encodeGx[npar - 1] = mul(encodeGx[npar - 1], ex);
		}
		return encodeGx;
	}

	public int calcOmegaValue(int[] omega, int zlog) {
		int wz = zlog;
		int ov = omega[0];
		for(int i = 1; i < omega.length; i++) {
			ov ^= mulExp(omega[i], wz);
			wz = (wz + zlog) % 255;
		}
		if(symStart != 0) {
			ov = mulExp(ov, (zlog * symStart) % 255);
		}
		return ov;
	}

	public int calcSigmaDashValue(int[] sigma, int zlog) {
		final int jisu = sigma.length - 1;
		final int zlog2 = (zlog * 2) % 255;
		int wz = zlog2;
		int dv = sigma[1];
		for(int i = 3; i <= jisu; i += 2) {
			dv ^= mulExp(sigma[i], wz);
			wz = (wz + zlog2) % 255;
		}
		return dv;
	}
}
