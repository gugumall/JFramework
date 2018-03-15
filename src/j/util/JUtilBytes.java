package j.util;

public class JUtilBytes{
	/**
	 * 将byte转为16进制
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byte2Hex(byte[] bytes){
		StringBuffer buffer=new StringBuffer();
		String temp=null;
		for(int i=0;i<bytes.length;i++){
			temp=Integer.toHexString(bytes[i]&0xFF);
			if(temp.length()==1){
				//得到一位的进行补0操作
				buffer.append("0");
			}
			buffer.append(temp);
		}
		return buffer.toString();
	}
}
