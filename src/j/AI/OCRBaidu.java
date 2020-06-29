package j.AI;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.aip.ocr.AipOcr;

import j.sys.AppConfig;
import j.util.JUtilJSON;

public class OCRBaidu extends OCR{

	@Override
	public String parse(String imagePath) {
		// 初始化一个AipOcr
		String APP_ID=AppConfig.getPara("BAIDU-OCR", "AppID");
		String API_KEY=AppConfig.getPara("BAIDU-OCR", "APIKey");
		String SECRET_KEY=AppConfig.getPara("BAIDU-OCR", "SecretKey");
		
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        //client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        //System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
        
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
//        options.put("recognize_granularity", "big");
//        options.put("language_type", "CHN_ENG");
//        options.put("detect_direction", "true");
//        options.put("detect_language", "true");
//        options.put("vertexes_location", "true");
//        options.put("probability", "true");

        // 调用接口
        //JSONObject res = client.basicGeneral(imagePath, options);
        JSONObject res = client.accurateGeneral(imagePath, options);
        
        /**
         * {
			  "log_id": 2557608456774799323,
			  "words_result": [{"words": "孜然"}],
			  "words_result_num": 1
			}
         */
        
        System.out.println(res.toString());
        
        JSONArray words=JUtilJSON.array(res, "words_result");
        if(words!=null && words.length()>0) {
        	return JUtilJSON.string(JUtilJSON.get(words, 0), "words");
        }
        
        return res.toString();
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		String result=OCR.parse("BAIDU", "C:\\Users\\ceo\\Desktop\\temp\\3263aacaad30fe21b70bc46a9a473f0a.jpg");
		System.out.println(result);
	}
}
