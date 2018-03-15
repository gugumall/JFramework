package j.http;

public class UtilsForBET16 {
	private static String _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

	/**
	 * 
	 * @param c
	 * @return
	 */
	private static int h(char c) {
		return (int) c;
	}

	public static String decompressFromBase64(String e) {
		if (e == null)
			return "";
		String t = "";
		int n = 0;
		int r = 0;
		int i = 0;
		int s = 0;
		int o = 0;
		int u = 0;
		int a = 0;
		int f = 0;
		int l = 0;
		int c = 0;

		e = e.replaceAll("/[^A-Za-z0-9\\+\\/\\=]/g", "");

		while (c < e.length()) {
			u = _keyStr.indexOf(e.charAt(c++));
			a = _keyStr.indexOf(e.charAt(c++));
			f = _keyStr.indexOf(e.charAt(c++));
			l = _keyStr.indexOf(e.charAt(c++));
			i = u << 2 | a >> 4;
			s = (a & 15) << 4 | f >> 2;
			o = (f & 3) << 6 | l;
			if (n % 2 == 0) {
				r = i << 8;
				if (f != 64) {
					t += h((char) (r | s));
				}
				if (l != 64) {
					r = o << 8;
				}
			} else {
				t = t + h((char) (r | i));
				if (f != 64) {
					r = s << 8;
				}
				if (l != 64) {
					t += h((char) (r | o));
				}
			}
			n += 3;
		}
		return decompress(t);
	}

	public static String decompressFromUTF16(String e) {
        String t = "";
        int n=0;
        int r=0;
        int i=0;
        int s=0;

        while (s < e.length()) {
            r = e.charAt(s) - 32;
            switch (i++) {
            case 0:
                n = r << 1;
                break;
            case 1:
                t += h((char)(n | r >> 14));
                n = (r & 16383) << 2;
                break;
            case 2:
                t += h((char)(n | r >> 13));
                n = (r & 8191) << 3;
                break;
            case 3:
                t += h((char)(n | r >> 12));
                n = (r & 4095) << 4;
                break;
            case 4:
                t += h((char)(n | r >> 11));
                n = (r & 2047) << 5;
                break;
            case 5:
                t += h((char)(n | r >> 10));
                n = (r & 1023) << 6;
                break;
            case 6:
                t += h((char)(n | r >> 9));
                n = (r & 511) << 7;
                break;
            case 7:
                t += h((char)(n | r >> 8));
                n = (r & 255) << 8;
                break;
            case 8:
                t += h((char)(n | r >> 7));
                n = (r & 127) << 9;
                break;
            case 9:
                t += h((char)(n | r >> 6));
                n = (r & 63) << 10;
                break;
            case 10:
                t += h((char)(n | r >> 5));
                n = (r & 31) << 11;
                break;
            case 11:
                t += h((char)(n | r >> 4));
                n = (r & 15) << 12;
                break;
            case 12:
                t += h((char)(n | r >> 3));
                n = (r & 7) << 13;
                break;
            case 13:
                t += h((char)(n | r >> 2));
                n = (r & 3) << 14;
                break;
            case 14:
                t += h((char)(n | r >> 1));
                n = (r & 1) << 15;
                break;
            case 15:
                t += h((char)(n | r));
                i = 0;
                break;
            }
            s++;
        }
        
        System.out.println("t:"+t);
        
        return decompress(t);
	}

	private static String decompress(String e) {
        int[] t=new int[10000];
        for(int i=0;i<t.length;i++) t[i]=-1;
        
        int n=0;
        double r = 4;
        int i = 4;
        int s = 3;
        String o = "";
        String u = "";
        int a;
        int f;
        int l;
        int c;
        double h;
        int p;
        int d=0;
        
        M m=new M(e);
  
        for (a = 0; a < 3; a += 1) {
            t[a] = a;
        }
        l = 0;
        h = Math.pow(2, 2);
        p = 1;
        while (p != h) {
            c = m.val & m.position;
            m.position >>= 1;
            if (m.position == 0) {
                m.position = 32768;
                m.val = (int)m.string.charAt(m.index++);
            }
            l |= (c > 0 ? 1 : 0) * p;
            p <<= 1;
        }
        switch (n = l) {
        case 0:
            l = 0;
            h = Math.pow(2, 8);
            p = 1;
            while (p != h) {
                c = m.val & m.position;
                m.position >>= 1;
                if (m.position == 0) {
                    m.position = 32768;
                    m.val = m.string.charAt(m.index++);
                }
                l |= (c > 0 ? 1 : 0) * p;
                p <<= 1;
            }
            d = h((char)l);
            break;
        case 1:
            l = 0;
            h = Math.pow(2, 16);
            p = 1;
            while (p != h) {
                c = m.val & m.position;
                m.position >>= 1;
                if (m.position == 0) {
                    m.position = 32768;
                    m.val = m.string.charAt(m.index++);
                }
                l |= (c > 0 ? 1 : 0) * p;
                p <<= 1;
            }
            d = h((char)l);
            break;
        case 2:
            return "";
        }
        t[3] = d;
        f =d;
        u = ""+d;
        while (true) {
            if (m.index > m.string.length()) {
                return "";
            }
            l = 0;
            h = Math.pow(2, s);
            p = 1;
            while (p != h) {
                c = m.val & m.position;
                m.position >>= 1;
                if (m.position == 0) {
                    m.position = 32768;
                    m.val = m.string.charAt(m.index++);
                }
                l |= (c > 0 ? 1 : 0) * p;
                p <<= 1;
            }
            switch (d = l) {
            case 0:
                l = 0;
                h = Math.pow(2, 8);
                p = 1;
                while (p != h) {
                    c = m.val & m.position;
                    m.position >>= 1;
                    if (m.position == 0) {
                        m.position = 32768;
                        m.val = m.string.charAt(m.index++);
                    }
                    l |= (c > 0 ? 1 : 0) * p;
                    p <<= 1;
                }
                t[i++] = h((char)l);
                d = i - 1;
                r--;
                break;
            case 1:
                l = 0;
                h = Math.pow(2, 16);
                p = 1;
                while (p != h) {
                    c = m.val & m.position;
                    m.position >>= 1;
                    if (m.position == 0) {
                        m.position = 32768;
                        m.val = m.string.charAt(m.index++);
                    }
                    l |= (c > 0 ? 1 : 0) * p;
                    p <<= 1;
                }
                t[i++] = h((char)l);
                d = i - 1;
                r--;
                break;
            case 2:
                return u;
            }
            if (r == 0) {
                r = Math.pow(2, s);
                s++;
            }
            if (t[d]>-1) {
                o = ""+(char)t[d];
            } else {
                if (d == i) {
                    o = "";//f + f.charAt(0);
                } else {
                    o="-";//return null;
                }
            }
            u += o;
            t[i++] = f + o.charAt(0);
            r--;
            f = (char)o.charAt(0);//
            if (r == 0) {
                r = Math.pow(2, s);
                s++;
            }
        }
	}
}

class M {
	String string = "";
	int val = 0;
	int position = 32768;
	int index = 1;

	M(String e) {
		this.string = e;
		val = (int) e.charAt(0);
	}
}
