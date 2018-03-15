package j.util;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.DefaultFFMPEGLocator;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;
import j.common.JProperties;

import java.io.File;

/**
 * 
 * @author 肖炯
 *
 */
public class JUtilMedia {
	public static void main(String[] args) throws Exception {  
        String path1 = "f:\\temp\\x.amr";  
        String path2 = "f:\\temp\\x.mp3";  
        changeToMp3(path1, path2);  
    }  
    
  	/**
  	 * 
  	 * @param sourcePath
  	 * @param targetPath
  	 * @throws Exception
  	 */
	public static void changeToMp3(String sourcePath, String targetPath) throws Exception{  
		File source = new File(sourcePath);  
        File target = new File(targetPath);  
        
        AudioAttributes audio = new AudioAttributes();  
        audio.setCodec("libmp3lame");    
        audio.setBitRate(new Integer(128000));
        audio.setChannels(new Integer(2));
        audio.setSamplingRate(new Integer(44100));

        EncodingAttributes attrs = new EncodingAttributes();  
        attrs.setFormat("mp3"); 
        
        attrs.setAudioAttributes(audio);  

        Encoder encoder = new Encoder(new MyFFMPEGExecutableLocator());  
        encoder.encode(source,target,attrs);  
	}  
}  

class MyFFMPEGExecutableLocator extends DefaultFFMPEGLocator{
	@Override
	protected String getFFMPEGExecutablePath() {
		String os=System.getProperty("os.name");
		if(os==null) os="Windows";
		
		os=os.toLowerCase();
		
		String locale="";
		if(os.indexOf("windows")>-1){
			locale=JProperties.getProperty("FFMPEGLocale.windows");
		}else{
			locale=JProperties.getProperty("FFMPEGLocale.linux");
		}
		
		if(locale==null||locale.equals("")) locale=super.getFFMPEGExecutablePath();
		
		//System.out.println("DefaultFFMPEGLocator:"+locale);
		
		return locale;
	}
}
