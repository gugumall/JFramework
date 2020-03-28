package j.AI;

import java.io.File;
import java.io.File;
import java.io.InputStream;

import j.fs.JDFSFile;
import j.util.ConcurrentMap;

/**
 * 
 * @author 肖炯
 *
 * 2020年3月27日
 *
 * <b>功能描述</b> 语音合成/转换
 */
public abstract class Speech{
	private static ConcurrentMap<String, Speech> providers=new ConcurrentMap();
	
	/**
	 * 
	 * @param provider
	 * @return
	 */
	public static Speech getInstance(String provider) {
		if(provider==null||"".equals(provider)) return null;
		
		synchronized(providers) {
			Speech speech=providers.get(provider);
			if(speech==null) {
				if("BAIDU".equalsIgnoreCase(provider)) {
					speech=new SpeechBaidu();
					providers.put(provider.toUpperCase(), speech);
				}
			}
			
			return speech;
		}
	}
	
	/**
	 * 
	 * @param provider
	 * @param pathSaveInto
	 * @param text
	 * @param lang
	 * @param speed
	 * @param pitch
	 * @param volume
	 * @param person
	 * @param audioEncoder
	 */
	public static void text2Audio(String provider, String pathSaveInto, String text, String lang, String speed, String pitch, String volume, String person, String audioEncoder) {
		Speech speech=Speech.getInstance(provider);
		if(speech==null) return;
		
		File file=new File(pathSaveInto);
		if(file.exists()) return;
		
		InputStream mp3Url=speech.text2Audio(text, lang, speed, pitch, volume, person, audioEncoder);
		JDFSFile.saveStream(mp3Url, pathSaveInto);
	}
	
	/**
	 * 
	 * @param text
	 * @param lang
	 * @param speed
	 * @param pitch
	 * @param volume
	 * @param person
	 * @param audioEncoder
	 * @return
	 */
	public abstract InputStream text2Audio(String text, String lang, String speed, String pitch, String volume, String person, String audioEncoder);

	/**
	 * 
	 * @param stream
	 * @return
	 */
	public abstract String audio2Text(InputStream stream);
}
