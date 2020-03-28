package j.AI;

import java.io.InputStream;

import org.json.JSONObject;

import j.fs.JDFSFile;
import j.http.JHttp;
import j.log.Logger;
import j.sys.AppConfig;
import j.sys.SysConfig;
import j.sys.SysUtil;
import j.util.JUtilJSON;
import j.util.JUtilMath;
import j.util.JUtilString;

/**
 * 
 * @author 肖炯
 *
 * 2020年3月27日
 *
 * <b>功能描述</b> 基于百度云语音技术实现的语音合成/转换
 */
public class SpeechBaidu extends Speech{
	private static Logger log=Logger.create(SpeechBaidu.class);
	
	private static final Object lock=new Object();
	private static AccessToken accessToken=null;
	
	/**
	 * 
	 * @return
	 */
	public static AccessToken getAccessToken() {
		synchronized(lock) {
			if(accessToken!=null && !accessToken.expired()) return accessToken;
			
			String url=AppConfig.getPara("BAIDU-SPEECH", "TokenUrl");
			url=JUtilString.replaceAll(url, "APIKey", AppConfig.getPara("BAIDU-SPEECH", "APIKey"));
			url=JUtilString.replaceAll(url, "SecretKey", AppConfig.getPara("BAIDU-SPEECH", "SecretKey"));
			System.out.println("token url -> "+url);
			
			try {
				JHttp http=JHttp.getInstance();
				String respText=http.getResponse(null, null, url);
				log.log("get baidu speech access token -> "+respText, -1);
				
				JSONObject resp=JUtilJSON.parse(respText);
				String token=JUtilJSON.string(resp, "access_token");
				String expiresIn=JUtilJSON.string(resp, "expires_in");
				if(!JUtilMath.isLong(expiresIn)) return accessToken;
				
				accessToken=new AccessToken(token, Long.parseLong(expiresIn));
			}catch(Exception e) {
				log.log(e,Logger.LEVEL_ERROR);
			}
			
			return accessToken;
		}
	}
	

	@Override
	public InputStream text2Audio(String text, String lang, String speed, String pitch, String volume, String person, String audioEncoder) {
		try {
			if(text==null) return null;
			
			text=text.trim();
			if("".equals(text) || JUtilString.bytes(text, "GBK")>4096) {
				return null;
			}
			
			AccessToken accessToken=SpeechBaidu.getAccessToken();
			if(accessToken==null || accessToken.expired()) return null;
			
	//		tex	 必填	合成的文本，使用UTF-8编码。小于2048个中文字或者英文数字。（文本在百度服务器内转换为GBK后，长度必须小于4096字节）
	//		tok	 必填	开放平台获取到的开发者access_token（见上面的“鉴权认证机制”段落）
	//		cuid 必填	用户唯一标识，用来计算UV值。建议填写能区分用户的机器 MAC 地址或 IMEI 码，长度为60字符以内
	//		ctp	 必填	客户端类型选择，web端填写固定值1
	//		lan	 必填	固定值zh。语言选择,目前只有中英文混合模式，填写固定值zh
	//		spd	 选填	语速，取值0-15，默认为5中语速
	//		pit	 选填	音调，取值0-15，默认为5中语调
	//		vol	 选填	音量，取值0-15，默认为5中音量
	//		per（基础音库）	选填	度小宇=1，度小美=0，度逍遥=3，度丫丫=4
	//		per（精品音库）	选填	度博文=106，度小童=110，度小萌=111，度米朵=103，度小娇=5
	//		aue	选填	3为mp3格式(默认)； 4为pcm-16k；5为pcm-8k；6为wav（内容同pcm-16k）; 注意aue=4或者6是语音识别要求的格式，但是音频内容不是语音识别要求的自然人发音，所以识别效果会受影响。
	//		**tex字段2次urlencode
			
	
			String url=AppConfig.getPara("BAIDU-SPEECH", "text2audioUrl");
			url+="?tex="+JUtilString.encodeURI(JUtilString.encodeURI(text, "UTF-8"),"UTF-8");
			url+="&tok="+accessToken.token;
			url+="&cuid="+SysConfig.getSysId();
			url+="&ctp=1";
			url+="&lan=zh";
			url+="&spd="+(speed!=null?speed:AppConfig.getPara("BAIDU-SPEECH", "speed"));
			url+="&pit="+(pitch!=null?pitch:AppConfig.getPara("BAIDU-SPEECH", "pitch"));
			url+="&vol="+(volume!=null?volume:AppConfig.getPara("BAIDU-SPEECH", "volume"));
			url+="&per="+(person!=null?person:AppConfig.getPara("BAIDU-SPEECH", "person"));
			url+="&aue="+(audioEncoder!=null?audioEncoder:AppConfig.getPara("BAIDU-SPEECH", "audioEncoder"));
			
			//log.log("text2audio url -> "+url, -1);
			
			JHttp http=JHttp.getInstance();
			return http.getStreamResponse(null, null, url);
		}catch(Exception e) {
			log.log(e, Logger.LEVEL_ERROR);
			return null;
		}
	}

	@Override
	public String audio2Text(InputStream stream) {
		return null;
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		System.out.println("try convert text to audio....");
		Speech speech=Speech.getInstance("BAIDU");
		InputStream mp3Url=speech.text2Audio("您有一个新的堂食订单，请尽快处理", null, null, null, null, null, null);
		JDFSFile.saveStream(mp3Url, "F:/temp/堂食订单.mp3");
		
		mp3Url=speech.text2Audio("您有一个新的外卖订单，请尽快处理，thank you.", null, null, null, null, null, null);
		JDFSFile.saveStream(mp3Url, "F:/temp/外卖订单.mp3");
		System.exit(0);
	}
}

/**
 * 
 * @author 肖炯
 *
 * 2020年3月27日
 *
 * <b>功能描述</b>
 */
class AccessToken{
	public String token;
	public long update;
	public long expiresIn;
	
	/**
	 * 
	 * @param token
	 * @param expiresIn
	 */
	public AccessToken(String token, long expiresIn) {
		this.token=token;
		this.expiresIn=expiresIn;
		this.update=SysUtil.getNow();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean expired() {
		return SysUtil.getNow()-this.update>=this.expiresIn;
	}
}