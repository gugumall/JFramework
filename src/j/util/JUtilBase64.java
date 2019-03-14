package j.util;

import java.util.Base64;

public final class JUtilBase64{
	/**
	 * Encodes hex octects into Base64
	 * 
	 * @param binaryData Array containing binaryData
	 * @return Encoded Base64 array
	 */
	public static String encode(byte[] binaryData){
		return Base64.getEncoder().encodeToString(binaryData);
	}

	/**
	 * Decodes Base64 data into octects
	 * 
	 * @param encoded string containing Base64 data
	 * @return Array contained decoded data.
	 */
	public static byte[] decode(String encoded){
		return Base64.getDecoder().decode(encoded);
	}
}
