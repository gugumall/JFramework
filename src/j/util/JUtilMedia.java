package j.util;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.AudioInfo;
import it.sauronsoftware.jave.DefaultFFMPEGLocator;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.MultimediaInfo;
import it.sauronsoftware.jave.VideoInfo;
import j.common.JProperties;
import j.fs.JDFSFile;

import java.io.File;
import java.util.List;

/**
 * 
 * @author 肖炯
 *
 */
public class JUtilMedia {
	private File source;//文件路径
	private long size;//大小 bytes
	private String format;//格式
	private long duration;//时长ms
	private int bitRate;//比特率
	private String decoder;//解码器
	
	private int videoWidth;//宽
	private int videoHeight;//高
	private double videoFrameRate;//帧率
	
	private int audioSamplingRate;//
	private int audioChannels;//声道数
	
	/**
	 * 
	 * @param source
	 */
	public JUtilMedia(File _source) throws Exception{
		if(_source==null||!_source.exists()) throw new Exception("source not exists");
		
		source=_source;
		size=source.length();
		

        MultimediaInfo info=info(source);
		
        if(info!=null){
    		format=info.getFormat();
        	duration=info.getDuration();
        	
        	AudioInfo ai=info.getAudio();
        	if(ai!=null){
        		bitRate=ai.getBitRate();
        		decoder=ai.getDecoder();
        		audioChannels=ai.getChannels();
        		audioSamplingRate=ai.getSamplingRate();
        	}
        	
        	VideoInfo vi=info.getVideo();
        	if(vi!=null){
        		bitRate=vi.getBitRate();
        		decoder=vi.getDecoder();
        		videoWidth=vi.getSize().getWidth();
        		videoHeight=vi.getSize().getHeight();
        		videoFrameRate=vi.getFrameRate();
        	}
        }
	}

	public File getSource(){
		return this.source;
	}
	public long getSize(){
		return this.size;
	}
	public String getFormat(){
		return this.format;
	}
	public long getDuration(){
		return this.duration;
	}
	public int getBitRate(){
		return this.bitRate;
	}
	public String getDecoder(){
		return this.decoder;
	}
	public int getVideoWidth(){
		return this.videoWidth;
	}
	public int getVideoHeight(){
		return this.videoHeight;
	}
	public double getVideoFrameRate(){
		return this.videoFrameRate;
	}
	public int getAudioSamplingRate(){
		return this.audioSamplingRate;
	}
	public int getAudioChannels(){
		return this.audioChannels;
	}
	
	/**
	 * 
	 * @param savedTo
	 * @return
	 */
	public boolean getFrameImage(File savedTo){
		try{
			String executable=(new MyFFMPEGExecutableLocator()).getFFMPEGExecutablePath();
			List<String> commands = new java.util.ArrayList<String>();
	
			commands.add(executable);
			commands.add("-i");
			commands.add(source.getAbsolutePath());
			commands.add("-y");
			commands.add("-f");
			commands.add("image2");
			commands.add("-ss");
			commands.add("1");//这个参数是设置截取视频多少秒时的画面
			commands.add("-t");
			commands.add("0.001");
			commands.add("-s");
			commands.add(this.videoWidth+"x"+this.videoHeight);//宽X高
			commands.add(savedTo.getAbsolutePath());
	
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commands);
			builder.start();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param source
	 * @return
	 */
	private static MultimediaInfo info(File source){
		try{
			Encoder encoder = new Encoder();
			return encoder.getInfo(source);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
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

        Encoder encoder = new Encoder();  
        encoder.encode(source,target,attrs);  
	}  
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {  
//		String path = "f:\\temp\\900.mp4";  
//		JUtilMedia m=new JUtilMedia(new File(path));
//		
//		System.out.println(":getSize: "+m.getSize());
//		System.out.println(":getFormat: "+m.getFormat());
//		System.out.println(":getDuration: "+m.getDuration());
//		System.out.println(":getBitRate: "+m.getBitRate());
//		System.out.println(":getDecoder: "+m.getDecoder());
//		System.out.println(":getVideoWidth: "+m.getVideoWidth());
//		System.out.println(":getVideoHeight: "+m.getVideoHeight());
//		System.out.println(":getVideoFrameRate: "+m.getVideoFrameRate());
//		System.out.println(":getAudioSamplingRate: "+m.getAudioSamplingRate());
//		System.out.println(":getAudioChannels: "+m.getAudioChannels());
//		
//		m.getFrameImage(new File("F:/temp/xxx.jpg"));
		String s=JDFSFile.read(new File("F:\\backup\\godaddyX.txt"),"UTF-8");
		System.out.println(s.length());
		String ss=JUtilCompressor.gzipString(s,"UTF-8");
		System.out.println(ss.length());
		
		s=JUtilCompressor.gunzipString(ss,"UTF-8");
		System.out.println(s);
		System.out.println(s.length());
		
		System.exit(0);
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
		
		System.out.println("DefaultFFMPEGLocator:"+locale);
		
		return locale;
	}
}
