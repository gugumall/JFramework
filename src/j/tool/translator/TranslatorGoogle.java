package j.tool.translator;

import j.http.JHttp;
import j.http.JHttpContext;
import j.log.Logger;
import j.util.JUtilString;

import org.apache.http.client.HttpClient;

/**
 * 
 * @author 肖炯
 *
 */
public class TranslatorGoogle extends Translator{
	private static Logger log = Logger.create(TranslatorGoogle.class);
	private JHttpContext context=null;
	private JHttp http=null;
	private HttpClient client=null;
	
	/**
	 * 
	 *
	 */
	protected TranslatorGoogle(){
		super();
		context=new JHttpContext();
		http=JHttp.getInstance();
		client=http.createClient(5000);
		
		try{
			http.get(context,client,"http://translate.google.com/");
		}catch(Exception e){
			log.log(e,Logger.LEVEL_ERROR);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see j.tool.translator.Translator#translate(java.lang.String, java.lang.String, java.lang.String)
	 */
	synchronized public String translate(String source,String sourceLang,String toLang) throws Exception{
		if(source==null
				||"".equals(source.trim())){
			return source;
		}
		String words=JUtilString.encodeURI(source,"UTF-8");
		context.addRequestHeader("Referer","http://translate.google.com/#en/zh-CN/"+words);

		String url="http://translate.google.com/translate_a/t?client=t&sl="+sourceLang+"&tl="+toLang+"&hl=zh-CN&sc=2&ie=UTF-8&oe=UTF-8&oc=1&otf=2&ssel=0&tsel=0&q="+words;
		try{
			http.get(context,client,url,"UTF-8");
			
			if(context==null||(context.getStatus()!=200&&context.getStatus()!=302)){
				log.log("翻译出错 - "+url+" - context - "+context+" status - "+(context==null?"unknown":context.getStatus()),Logger.LEVEL_ERROR);
				return source;
			}else{
				String response=context.getResponseText();
				
				int start=response.indexOf("[\"");
				if(start>-1){
					int end=response.indexOf("\"",start+2);
					
					response=response.substring(start+2,end);
				}
				
				return response;
			}
		}catch(Exception e){
			log.log(e.getMessage(),Logger.LEVEL_ERROR);
			return source;
		}
	}
}
