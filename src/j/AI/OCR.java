package j.AI;

import j.util.ConcurrentMap;

/**
 * 
 * @author 肖炯
 *
 * 2020年3月27日
 *
 * <b>功能描述</b> 语音合成/转换
 */
public abstract class OCR{
	private static ConcurrentMap<String, OCR> providers=new ConcurrentMap();
	
	/**
	 * 
	 * @param provider
	 * @return
	 */
	public static OCR getInstance(String provider) {
		if(provider==null||"".equals(provider)) return null;
		
		synchronized(providers) {
			OCR ocr=providers.get(provider);
			if(ocr==null) {
				if("BAIDU".equalsIgnoreCase(provider)) {
					ocr=new OCRBaidu();
					providers.put(provider.toUpperCase(), ocr);
				}
			}
			
			return ocr;
		}
	}
	
	/**
	 * 
	 * @param provider
	 * @param imagePath
	 * @return
	 */
	public static String parse(String provider, String imagePath) {
		OCR ocr=OCR.getInstance(provider);
		if(ocr==null) return null;
		
		return ocr.parse(imagePath);
	}
	

	/**
	 * 
	 * @param imagePath
	 * @return
	 */
	public abstract String parse(String imagePath);
}
