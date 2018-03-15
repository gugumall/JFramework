package j.sms;

/**
 * 
 * @author 肖炯
 * 必须是线程安全的
 *
 */
public interface SMSChannel {	
	/**
	 * 是否可送到（该短信通道是否可给某号码发送短信）
	 * @param dest 目标号码
	 * @return
	 */
	public boolean reachable(String dest);
	
	/**
	 * 发送短信
	 * @param to
	 * @param text
	 * @param encoding
	 * @param fromName
	 * @return
	 * @throws Exception
	 */
	public boolean send(String to,String text,String encoding,String fromName) throws Exception;
	
	/**
	 * 发送彩信（包含文件）
	 * @param to
	 * @param text
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 * @return
	 * @throws Exception
	 */
	public boolean send(String to,String text, String encoding, String[] filePaths,String fromName) throws Exception;
	
	/**
	 * 发送短信(适用于模板短信)
	 * @param to
	 * @param texts
	 * @param encoding
	 * @param fromName
	 * @return
	 * @throws Exception
	 */
	public boolean send(String to,String[] texts,String encoding,String fromName) throws Exception;
	
	/**
	 * 发送彩信(适用于模板短信)
	 * @param to
	 * @param texts
	 * @param encoding
	 * @param filePaths
	 * @param fromName
	 * @return
	 * @throws Exception
	 */
	public boolean send(String to,String[] texts, String encoding, String[] filePaths,String fromName) throws Exception;
}
