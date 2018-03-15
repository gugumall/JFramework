
package j.http;

/**
 * 
 * @author JFramework
 *
 */
public class UploadMsg {
	public static final int RESULT_SUCCESS=0;//成功
	public static final int RESULT_TOO_LARGE=1;//文件太大
	public static final int RESULT_EMPTY=2;//内容为空
	public static final int RESULT_BAD_FORMAT=3;//格式不正确，不可解析
	public static final int RESULT_EXCEPTION=4;//系统异常
	
	public boolean isSuccessful;//是否成功
	public int result;//上传处理结果
	public Exception e;//系统抛出的异常
	public int allowK;//最大允许的文件大小(K)
}