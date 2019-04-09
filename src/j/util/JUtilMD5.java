package j.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author 肖炯
 *
 */
public class JUtilMD5 {
	private static byte padding[] = { -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0 };

	private InputStream in;

	private boolean stringp;

	private int state[];

	private long count;

	private byte buffer[];

	private byte digest[];

	public JUtilMD5(String s, String encoding) {
		in = null;
		stringp = false;
		state = null;
		count = 0L;
		buffer = null;
		digest = null;
		byte abyte0[] = null;
		try {
			abyte0 = s.getBytes(encoding);
		} catch (UnsupportedEncodingException unsupportedencodingexception) {
			throw new RuntimeException("no " + encoding + " encoding!!!");
		}
		stringp = true;
		in = new ByteArrayInputStream(abyte0);
		state = new int[4];
		buffer = new byte[64];
		count = 0L;
		state[0] = 0x67452301;
		state[1] = 0xefcdab89;
		state[2] = 0x98badcfe;
		state[3] = 0x10325476;
	}

	public JUtilMD5(String s) {
		this(s, "UTF-8");
	}

	public JUtilMD5(InputStream inputstream) {
		in = null;
		stringp = false;
		state = null;
		count = 0L;
		buffer = null;
		digest = null;
		stringp = false;
		in = inputstream;
		state = new int[4];
		buffer = new byte[64];
		count = 0L;
		state[0] = 0x67452301;
		state[1] = 0xefcdab89;
		state[2] = 0x98badcfe;
		state[3] = 0x10325476;
	}

	/**
	 * 
	 * @param origin
	 * @return
	 */
	public static String MD5EncodeToHex(String origin) {
		return bytesToHexString(MD5Encode(origin));
	}

	/**
	 * 
	 * @param origin
	 * @param encoding
	 * @return
	 */
	public static byte[] MD5Encode(String origin,String encoding) {
		try{
			return MD5Encode(origin.getBytes(encoding));
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 
	 * @param origin
	 * @param encoding
	 * @return
	 */
	public static String MD5EncodeToHex(String origin,String encoding) {
		return bytesToHexString(MD5Encode(origin,encoding));
	}

	/**
	 * 
	 * @param origin
	 * @return
	 */
	public static byte[] MD5Encode(String origin) {
		return MD5Encode(origin.getBytes());
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] MD5Encode(byte[] bytes) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			return md.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	/**
	 * 
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;

		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)	sb.append(0);			
			sb.append(sTemp);
		}

		return sb.toString();
	}



	public byte[] getDigest() throws IOException {
		byte abyte0[] = new byte[1024];
		int i = -1;
		if (digest != null)
			return digest;
		while ((i = in.read(abyte0)) > 0) {
			update(abyte0, i);
		}
		digest = end();
		return digest;
	}

	public byte[] processString() {
		byte[] digest = null;
		if (!stringp) {
			throw new RuntimeException(getClass().getName() + "[processString]"+ " not a string.");
		}
		try {
			digest = getDigest();
		} catch (Exception e) {

		}
		return digest;
	}

	public String getStringDigest() {
		if (digest == null) {
			throw new RuntimeException(getClass().getName()+ "[getStringDigest]" + ": called before processing.");
		} else {
			return stringify(digest);
		}
	}
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	private static String stringify(byte bytes[]) {
		StringBuffer stringbuffer = new StringBuffer(2 * bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			int j = (bytes[i] & 0xf0) >> 4;
			int k = bytes[i] & 0xf;
			stringbuffer.append(new Character((char) (j <= 9 ? 48 + j: (97 + j) - 10)));
			stringbuffer.append(new Character((char) (k <= 9 ? 48 + k: (97 + k) - 10)));
		}

		return stringbuffer.toString();
	}

	private final int F(int i, int j, int k) {
		return i & j | ~i & k;
	}

	private final int G(int i, int j, int k) {
		return i & k | j & ~k;
	}

	private final int H(int i, int j, int k) {
		return i ^ j ^ k;
	}

	private final int I(int i, int j, int k) {
		return j ^ (i | ~k);
	}

	private final int rotate_left(int i, int j) {
		return i << j | i >>> 32 - j;
	}

	private final int FF(int i, int j, int k, int l, int i1, int j1, int k1) {
		i += F(j, k, l) + i1 + k1;
		i = rotate_left(i, j1);
		i += j;
		return i;
	}

	private final int GG(int i, int j, int k, int l, int i1, int j1, int k1) {
		i += G(j, k, l) + i1 + k1;
		i = rotate_left(i, j1);
		i += j;
		return i;
	}

	private final int HH(int i, int j, int k, int l, int i1, int j1, int k1) {
		i += H(j, k, l) + i1 + k1;
		i = rotate_left(i, j1);
		i += j;
		return i;
	}

	private final int II(int i, int j, int k, int l, int i1, int j1, int k1) {
		i += I(j, k, l) + i1 + k1;
		i = rotate_left(i, j1);
		i += j;
		return i;
	}

	private final void decode(int ai[], byte abyte0[], int i, int j) {
		int k = 0;
		for (int l = 0; l < j; l += 4) {
			ai[k] = abyte0[i + l] & 0xff | (abyte0[i + l + 1] & 0xff) << 8
					| (abyte0[i + l + 2] & 0xff) << 16
					| (abyte0[i + l + 3] & 0xff) << 24;
			k++;
		}

	}

	private final void transform(byte abyte0[], int i) {
		int j = state[0];
		int k = state[1];
		int l = state[2];
		int i1 = state[3];
		int ai[] = new int[16];
		decode(ai, abyte0, i, 64);
		j = FF(j, k, l, i1, ai[0], 7, 0xd76aa478);
		i1 = FF(i1, j, k, l, ai[1], 12, 0xe8c7b756);
		l = FF(l, i1, j, k, ai[2], 17, 0x242070db);
		k = FF(k, l, i1, j, ai[3], 22, 0xc1bdceee);
		j = FF(j, k, l, i1, ai[4], 7, 0xf57c0faf);
		i1 = FF(i1, j, k, l, ai[5], 12, 0x4787c62a);
		l = FF(l, i1, j, k, ai[6], 17, 0xa8304613);
		k = FF(k, l, i1, j, ai[7], 22, 0xfd469501);
		j = FF(j, k, l, i1, ai[8], 7, 0x698098d8);
		i1 = FF(i1, j, k, l, ai[9], 12, 0x8b44f7af);
		l = FF(l, i1, j, k, ai[10], 17, -42063);
		k = FF(k, l, i1, j, ai[11], 22, 0x895cd7be);
		j = FF(j, k, l, i1, ai[12], 7, 0x6b901122);
		i1 = FF(i1, j, k, l, ai[13], 12, 0xfd987193);
		l = FF(l, i1, j, k, ai[14], 17, 0xa679438e);
		k = FF(k, l, i1, j, ai[15], 22, 0x49b40821);
		j = GG(j, k, l, i1, ai[1], 5, 0xf61e2562);
		i1 = GG(i1, j, k, l, ai[6], 9, 0xc040b340);
		l = GG(l, i1, j, k, ai[11], 14, 0x265e5a51);
		k = GG(k, l, i1, j, ai[0], 20, 0xe9b6c7aa);
		j = GG(j, k, l, i1, ai[5], 5, 0xd62f105d);
		i1 = GG(i1, j, k, l, ai[10], 9, 0x2441453);
		l = GG(l, i1, j, k, ai[15], 14, 0xd8a1e681);
		k = GG(k, l, i1, j, ai[4], 20, 0xe7d3fbc8);
		j = GG(j, k, l, i1, ai[9], 5, 0x21e1cde6);
		i1 = GG(i1, j, k, l, ai[14], 9, 0xc33707d6);
		l = GG(l, i1, j, k, ai[3], 14, 0xf4d50d87);
		k = GG(k, l, i1, j, ai[8], 20, 0x455a14ed);
		j = GG(j, k, l, i1, ai[13], 5, 0xa9e3e905);
		i1 = GG(i1, j, k, l, ai[2], 9, 0xfcefa3f8);
		l = GG(l, i1, j, k, ai[7], 14, 0x676f02d9);
		k = GG(k, l, i1, j, ai[12], 20, 0x8d2a4c8a);
		j = HH(j, k, l, i1, ai[5], 4, 0xfffa3942);
		i1 = HH(i1, j, k, l, ai[8], 11, 0x8771f681);
		l = HH(l, i1, j, k, ai[11], 16, 0x6d9d6122);
		k = HH(k, l, i1, j, ai[14], 23, 0xfde5380c);
		j = HH(j, k, l, i1, ai[1], 4, 0xa4beea44);
		i1 = HH(i1, j, k, l, ai[4], 11, 0x4bdecfa9);
		l = HH(l, i1, j, k, ai[7], 16, 0xf6bb4b60);
		k = HH(k, l, i1, j, ai[10], 23, 0xbebfbc70);
		j = HH(j, k, l, i1, ai[13], 4, 0x289b7ec6);
		i1 = HH(i1, j, k, l, ai[0], 11, 0xeaa127fa);
		l = HH(l, i1, j, k, ai[3], 16, 0xd4ef3085);
		k = HH(k, l, i1, j, ai[6], 23, 0x4881d05);
		j = HH(j, k, l, i1, ai[9], 4, 0xd9d4d039);
		i1 = HH(i1, j, k, l, ai[12], 11, 0xe6db99e5);
		l = HH(l, i1, j, k, ai[15], 16, 0x1fa27cf8);
		k = HH(k, l, i1, j, ai[2], 23, 0xc4ac5665);
		j = II(j, k, l, i1, ai[0], 6, 0xf4292244);
		i1 = II(i1, j, k, l, ai[7], 10, 0x432aff97);
		l = II(l, i1, j, k, ai[14], 15, 0xab9423a7);
		k = II(k, l, i1, j, ai[5], 21, 0xfc93a039);
		j = II(j, k, l, i1, ai[12], 6, 0x655b59c3);
		i1 = II(i1, j, k, l, ai[3], 10, 0x8f0ccc92);
		l = II(l, i1, j, k, ai[10], 15, 0xffeff47d);
		k = II(k, l, i1, j, ai[1], 21, 0x85845dd1);
		j = II(j, k, l, i1, ai[8], 6, 0x6fa87e4f);
		i1 = II(i1, j, k, l, ai[15], 10, 0xfe2ce6e0);
		l = II(l, i1, j, k, ai[6], 15, 0xa3014314);
		k = II(k, l, i1, j, ai[13], 21, 0x4e0811a1);
		j = II(j, k, l, i1, ai[4], 6, 0xf7537e82);
		i1 = II(i1, j, k, l, ai[11], 10, 0xbd3af235);
		l = II(l, i1, j, k, ai[2], 15, 0x2ad7d2bb);
		k = II(k, l, i1, j, ai[9], 21, 0xeb86d391);
		state[0] += j;
		state[1] += k;
		state[2] += l;
		state[3] += i1;
	}

	private final void update(byte abyte0[], int i) {
		int j = (int) (count >> 3) & 0x3f;
		count += i << 3;
		int k = 64 - j;
		int l = 0;
		if (i >= k) {
			System.arraycopy(abyte0, 0, buffer, j, k);
			transform(buffer, 0);
			for (l = k; l + 63 < i; l += 64)
				transform(abyte0, l);

			j = 0;
		} else {
			l = 0;
		}
		System.arraycopy(abyte0, l, buffer, j, i - l);
	}

	private byte[] end() {
		byte abyte0[] = new byte[8];
		for (int i = 0; i < 8; i++)
			abyte0[i] = (byte) (int) (count >>> i * 8 & 255L);

		int j = (int) (count >> 3) & 0x3f;
		int k = j >= 56 ? 120 - j : 56 - j;
		update(padding, k);
		update(abyte0, 8);
		return encode(state, 16);
	}

	private byte[] encode(int ai[], int i) {
		byte abyte0[] = new byte[i];
		int j = 0;
		for (int k = 0; k < i; k += 4) {
			abyte0[k] = (byte) (ai[j] & 0xff);
			abyte0[k + 1] = (byte) (ai[j] >> 8 & 0xff);
			abyte0[k + 2] = (byte) (ai[j] >> 16 & 0xff);
			abyte0[k + 3] = (byte) (ai[j] >> 24 & 0xff);
			j++;
		}

		return abyte0;
	}
	
	//////////////////来自微信支付的方法//////////////////
	private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7","8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 转换字节数组为16进制字串
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }

    /**
     * 转换byte到16进制
     * @param b 要转换的byte
     * @return 16进制格式
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * MD5编码
     * @param origin 原始字符串
     * @return 经过MD5加密之后的结果
     */
    public static String MD5EncodeWeixin(String origin) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(resultString.getBytes("UTF-8"));
            resultString = byteArrayToHexString(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

	/**
	 * 测试
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws Exception {
		ConcurrentMap<String,String> list=new ConcurrentMap<String,String>();
		list.put("aaa","xx");
		String x=list.get("aaa");
		System.out.println(x);
	}
}