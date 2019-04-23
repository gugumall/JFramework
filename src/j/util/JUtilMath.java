package j.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 
 * @author 肖炯
 *
 */
public class JUtilMath {	
	/**
	 * 是否数字
	 * @param src
	 * @return
	 */
	public static boolean isNumber(String src){
//		if(src==null||(!src.matches("^\\d{1,}$")
//				&&!src.matches("^\\d{1,}.\\d{1,}$")
//				&&!src.matches("^-\\d{1,}$")
//				&&!src.matches("^-\\d{1,}.\\d{1,}$"))) return false;
		
		try{
			Double.parseDouble(src);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 是否Short
	 * @param src
	 * @return
	 */
	public static boolean isShort(String src){
//		if(src==null||(!src.matches("^\\d{1,}$")&&!src.matches("^-\\d{1,}$"))) return false;
		
		try{
			Short.parseShort(src);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 是否整型值
	 * @param src
	 * @return
	 */
	public static boolean isInt(String src){
//		if(src==null||(!src.matches("^\\d{1,}$")&&!src.matches("^-\\d{1,}$"))) return false;
		
		try{
			Integer.parseInt(src);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 是否长整型值
	 * @param src
	 * @return
	 */
	public static boolean isLong(String src){
//		if(src==null||(!src.matches("^\\d{1,}$")&&!src.matches("^-\\d{1,}$"))) return false;
		
		try{
			Long.parseLong(src);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 是否整型值,16进制
	 * @param src
	 * @return
	 */
	public static boolean isIntHex(String src){
//		if(src==null||(!src.matches("^[0-9a-fA-F]{1,}$")&&!src.matches("^-[0-9a-fA-F]{1,}$"))) return false;
		
		try{
			Integer.parseInt(src,16);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 是否长整型值,16进制
	 * @param src
	 * @return
	 */
	public static boolean isLongHex(String src){
//		if(src==null||(!src.matches("^[0-9a-fA-F]{1,}$")&&!src.matches("^-[0-9a-fA-F]{1,}$"))) return false;
		
		try{
			Long.parseLong(src,16);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static boolean equals(Integer v1,Integer v2){
		if(v1==null&&v2!=null) return false;
		if(v1!=null&&v2==null) return false;
		if(v1==null&&v2==null) return true;
		return v1.equals(v2);
	}
	
	/**
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static boolean equals(Long v1,Long v2){
		if(v1==null&&v2!=null) return false;
		if(v1!=null&&v2==null) return false;
		if(v1==null&&v2==null) return true;
		return v1.equals(v2);
	}
	
	/**
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static boolean equals(Double v1,Double v2){
		if(v1==null&&v2!=null) return false;
		if(v1!=null&&v2==null) return false;
		if(v1==null&&v2==null) return true;
		return v1.equals(v2);
	}
	
	/**
	 * 
	 * @param v1
	 * @param v2
	 * @param precision
	 * @return
	 */
	public static boolean equals(Double v1,Double v2,int precision){
		if(v1==null&&v2!=null) return false;
		if(v1!=null&&v2==null) return false;
		if(v1==null&&v2==null) return true;
		double diff=Double.parseDouble(JUtilMath.formatPrintWithoutZero(v1-v2,precision));
		return Math.abs(diff)==0d;
	}
	
	/**
	 * 
	 * @param v1
	 * @param v2
	 * @param precision
	 * @return
	 */
	public static boolean isBigger(Double v1,Double v2,int precision){
		if(v1==null||v2==null) return false;
		double diff=Double.parseDouble(JUtilMath.formatPrintWithoutZero(v1-v2,precision));
		return diff>0;
	}
	
	/**
	 * 
	 * @param v1
	 * @param v2
	 * @param precision
	 * @return
	 */
	public static boolean isSmaller(Double v1,Double v2,int precision){
		if(v1==null||v2==null) return false;
		double diff=Double.parseDouble(JUtilMath.formatPrintWithoutZero(v2-v1,precision));
		return diff>0;
	}
	
	/**
	 * 
	 * @param value
	 * @param precision
	 * @return
	 */
	public static double floor(double value,int precision){
		value=Double.parseDouble(JUtilMath.formatPrintWithoutZero(value,precision));
		return Math.floor(value);
	}
	
	/**
	 * 格式化数字，precision位小数，四舍五入
	 * @param src
	 * @param precision
	 * @return
	 */
	public static String formatPrint(double src,int precision){
		String format="0";
		for(int i=0;i<precision;i++){
			if(i==0) format+=".0";
			else format+="0";
		}
		DecimalFormat df = new DecimalFormat(format); 
		df.setRoundingMode(RoundingMode.HALF_UP);
		String num = df.format(src);  
		df=null;
		return num;
	}
	
	/**
	 * 格式化数字，precision位小数，去掉小数位末尾的0，四舍五入
	 * @param src
	 * @param precision
	 * @return
	 */
	public static String formatPrintWithoutZero(double src,int precision){
		String ret=formatPrint(src,precision);
		ret=trimZero(ret,0); 
		return ret;
	}
	
	/**
	 * 格式化数字，precision位小数，并且保留最少validDigits为有效数字（如果有小数部分），四舍五入
	 * @param src
	 * @param precision
	 * @param validDigits
	 * @return
	 */
	public static String formatPrint(double src,int precision,int validDigits){
		String temp=formatPrint(src,precision);
		
		if(validDigits==0||Double.parseDouble(temp)==src) return temp;
		
		String fraction=null;
		int dot=temp.indexOf(".");
		if(dot>0){
			fraction=temp.substring(dot+1);
			fraction=trimZeroAtHeader(fraction);
		}
		
		while(fraction==null||fraction.length()<validDigits){
			precision++;
			temp=formatPrint(src,precision);
			
			dot=temp.indexOf(".");
			if(dot>0){
				fraction=temp.substring(dot+1);
				fraction=trimZeroAtHeader(fraction);
			}
		}
		
		return temp;
	}
	
	/**
	 * 格式化数字，precision位小数，并且保留最少validDigits为有效数字（如果有小数部分），四舍五入
	 * @param src
	 * @param precision
	 * @param validDigits
	 * @return
	 */
	public static String formatPrintWithoutZero(double src,int precision,int validDigits){
		String temp=formatPrint(src,precision);
		
		if(validDigits==0||Double.parseDouble(temp)==src){
			return formatPrintWithoutZero(src,precision);
		}
		
		String fraction=null;
		int dot=temp.indexOf(".");
		if(dot>0){
			fraction=temp.substring(dot+1);
			fraction=trimZeroAtHeader(fraction);
		}
		
		while(fraction==null||fraction.length()<validDigits){
			precision++;
			temp=formatPrint(src,precision);
			
			dot=temp.indexOf(".");
			if(dot>0){
				fraction=temp.substring(dot+1);
				fraction=trimZeroAtHeader(fraction);
			}
		}

		return formatPrintWithoutZero(src,precision);
	}
	
	/**
	 * 格式化数字，precision位小数，有效小数位与参照数保持不变
	 * @param refer 参照数
	 * @param src
	 * @param precision
	 * @return
	 */
	public static String formatPrintPrecisionNoChange(double refer,double src,int precision){
		String original=JUtilMath.formatPrintWithoutZero(refer,10);
		String fraction=null;
		int dot=original.indexOf(".");
		if(dot>0){
			fraction=original.substring(dot+1);
		}
		
		return formatPrint(src,precision,fraction==null?precision:fraction.length());
	}
	
	/**
	 * 格式化数字，precision位小数，有效小数位与参照数保持不变
	 * @param refer 参照数
	 * @param src
	 * @param precision
	 * @return
	 */
	public static String formatPrintPrecisionNoChangeWithoutZero(double refer,double src,int precision){
		String original=JUtilMath.formatPrintWithoutZero(refer,10);
		String fraction=null;
		int dot=original.indexOf(".");
		if(dot>0){
			fraction=original.substring(dot+1);
		}
		
		return formatPrintWithoutZero(src,precision,fraction==null?precision:fraction.length());
	}
	
	/**
	 * 格式化数字，precision位小数，四舍五入
	 * @param src
	 * @param precision
	 * @return
	 */
	public static String formatPrintComma(double src,int precision){
		String format=",###";
		for(int i=0;i<precision;i++){
			if(i==0) format+=".0";
			else format+="0";
		}
		DecimalFormat df = new DecimalFormat(format); 
		df.setRoundingMode(RoundingMode.HALF_UP);
		String num = df.format(src);  
		df=null;
		return num;
	}
	
	/**
	 * 格式化数字，precision位小数，去掉小数位末尾的0，四舍五入
	 * @param src
	 * @param precision
	 * @return
	 */
	public static String formatPrintCommaWithoutZero(double src,int precision){
		String ret=formatPrintComma(src,precision);
		ret=trimZero(ret,0); 
		return ret;
	}
	
	/**
	 * 如果小数位少于precision，则0补足，如果多于precision，则去掉末尾无效的0（如果有的话）
	 * @param str
	 * @param precision
	 * @return
	 */
	public static String trimZero(String str,int precision){
		String p1=null;
		String p2=null;

		int dot=str.indexOf(".");
		if(dot>0){
			p1=str.substring(0,dot);
			p2=str.substring(dot+1);
		}else{
			p1=str;
		}
		
		if(p2==null){
			if(precision>0){
				p2=".";
				for(int i=0;i<precision;i++) p2+="0";
			}
		}else{
			while(p2.length()>precision&&p2.endsWith("0")) p2=p2.substring(0,p2.length()-1);
			while(p2.length()<precision) p2+="0";
		}
		
		if(p2==null||p2.equals("")) return p1;
		else return p1+"."+p2;
	}
	
	/**
	 * 去掉字符串开头的0
	 * @param str
	 * @return
	 */
	public static String trimZeroAtHeader(String str){
		while(str.startsWith("0")) str=str.substring(1);
		return str;
	}
	
	/**
	 * 四舍五入转换成整数
	 * @param value
	 * @return
	 */
	public static int toInt(double value){
		return (new Double(formatPrint(value,0))).intValue();
	}
	
	/**
	 * 
	 * @param scope
	 * @param selected
	 * @return
	 */
	public static long p(int scope, int selected){
		if(scope<1||selected<1||selected>scope) return 1;
		long p=1;
		for(int i=scope-selected+1;i<=scope;i++) p*=i;
		return p;
	}
	
	/**
	 * 
	 * @param scope
	 * @param selected
	 * @return
	 */
	public static long c(int scope, int selected){
		if(scope<1||selected<1||selected>scope) return 1;
		long p=1;
		for(int i=1;i<=selected;i++) p*=i;
		return p(scope,selected)/p;
	}
	
	/**
	 * 数字转中文格式
	 * @param value
	 * @return
	 */
	public static String number2CnFormat(long value){
		long wan=value/10000;
		
		value=value%10000;
		long qian=value/1000;
		
		value=value%1000;
		long bai=value/100;
		
		value=value%100;
		long shi=value/10;
		
		value=value%10;
		long ge=value;
		
		String out="";
		if(wan>0){
			out+=number2CnFormat(wan)+digit2CnChar(10000);
		}
		
		if(qian>0){
			out+=digit2CnChar((int)qian)+digit2CnChar(1000);
		}
		
		if(bai>0){
			out+=digit2CnChar((int)bai)+digit2CnChar(100);
		}else if(qian>0&&(shi>0||ge>0)){
			out+=digit2CnChar(0);
		}
		
		if(shi>0){
			out+=digit2CnChar((int)shi)+digit2CnChar(10);
		}else if(bai>0&&ge>0){
			out+=digit2CnChar(0);
		}
		
		if(ge>0){
			out+=digit2CnChar((int)ge);
		}
		
		return out;
	}
	
	/**
	 * 
	 * @param digit
	 * @return
	 */
	private static String digit2CnChar(int digit){
		if(digit==0){
			return "零";
		}else if(digit==1){
			return "壹";
		}else if(digit==2){
			return "贰";
		}else if(digit==3){
			return "叁";
		}else if(digit==4){
			return "肆";
		}else if(digit==5){
			return "伍";
		}else if(digit==6){
			return "陆";
		}else if(digit==7){
			return "柒";
		}else if(digit==8){
			return "捌";
		}else if(digit==9){
			return "玖";
		}else if(digit==10){
			return "拾";
		}else if(digit==100){
			return "佰";
		}else if(digit==1000){
			return "仟";
		}else if(digit==10000){
			return "万";
		}else{
			return "亿";
		}
	}
	
	/**
	 * 单byte转int
	 * @param b 字节
	 * @param unsigned 是否无符号
	 * @return
	 */
	public static int byteToInt(byte b,boolean unsigned) {
		return (unsigned&&b<0)?(b&0xFF):b;
	}
	
	/**
	 * 4 bytes转int
	 * @param bytes 4字节，不足4字节的高位补0，多于4字节则丢弃高位
	 * @param reverse bytes是否反转（即低位在左、高位在右）
	 * @return
	 */
	public static int fourBytesToInt(byte[] _bytes,boolean reverse) {
		byte[] _int=new byte[] {0,0,0,0};
		
		if(reverse) {//反转，反转后为高位在左
			byte[] _bytesReverse=new byte[_bytes.length];
			for(int i=0;i<_bytes.length;i++) _bytesReverse[i]=_bytes[_bytes.length-i-1];
			_bytes=_bytesReverse;
		}
		
		int maxBytes=_bytes.length>4?4:_bytes.length;//最多取4 byte
		
		for(int i=_bytes.length-1;i>=_bytes.length-maxBytes;i--) _int[i+(4-maxBytes)]=_bytes[i];
		
		return (_int[0] & 0xff) << 24 | (_int[1] & 0xff) << 16 | (_int[2] & 0xff) << 8 | _int[3] & 0xff;
	}
	
	/**
	 * int转byte数组
	 * @param _int int值
	 * @param length 转换成字节数组的长度（1~4）
	 * @param reverse 返回的bytes是否反转（即低位在左、高位在右）
	 * @return
	 */
	public static byte[] intToBytes(int _int,int length,boolean reverse) {
		if(length<1) length=1;//最少1字节
		if(length>4) length=4;//最多4字节
		
	    byte[] _bytes = new byte[length];  
	    
    	for(int i=3;i>=(4-length);i--) _bytes[i-(4-length)]=(byte) ((_int >> ((4-i-1)*8)) & 0xFF);  
	    
		if(reverse) {//反转
			byte[] _bytesReverse=new byte[_bytes.length];
			for(int i=0;i<_bytes.length;i++) _bytesReverse[i]=_bytes[_bytes.length-i-1];
			_bytes=_bytesReverse;
		}
		
	    return _bytes;  
	}
	
	/**
	 * 校验和
	 * @param bytes 源数据
	 * @param unsigned
	 * @param len
	 * @return
	 */
	public static byte checkSum(byte[] bytes, boolean unsigned, int len){
        int sum = 0;
        for(int i = 0; i < len; i++) {
        	sum += (JUtilMath.byteToInt(bytes[i], unsigned) & 0xff);
        }
        return (byte) (sum & 0xff);
    }
	
	/**
	 * 校验和
	 * @param bytes
	 * @param unsigned
	 * @param from 包含
	 * @param to 不包含
	 * @return
	 */
	public static byte checkSum(byte[] bytes, boolean unsigned, int from,int to){
        int sum = 0;
        for(int i = from; i < to; i++) {
        	sum += (JUtilMath.byteToInt(bytes[i], unsigned) & 0xff);
        }
        return (byte) (sum & 0xff);
    }
	
	/**
	 * 
	 * @param bytes 字节数组
	 * @param unsigned 是否无符号
	 * @param radix 进制
	 * @param space 是否添加空格
	 * @return
	 */
	public static String bytesToString(byte[] bytes,boolean unsigned,int radix,boolean space) {
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<bytes.length;i++) {
			if(i>0&&space) sb.append(" ");
			
			int v=byteToInt(bytes[i],unsigned);
			
			if(radix==16&&v<0xF) sb.append("0");
			sb.append(Integer.toString(v, radix));
		}
		return sb.toString().toUpperCase();
	}
	
	/**
	 * 解析16进制字符串格式的byte数组
	 * @param s 每个byte必须是2个字符表示，不足两位的用0补齐，如08
	 * @return
	 */
	public static byte[] hexToBytes(String s) {
		if(s==null) return null;
		s=s.toUpperCase();
		s=JUtilString.replaceAll(s, " ", "");//去掉空格
		s=JUtilString.replaceAll(s, "0X", "");//去掉0X
		if("".equals(s)) return new byte[0];
		
		int hexNumbers=s.length()/2;
		byte[] bytes=new byte[hexNumbers];
		for(int i=0;i<hexNumbers;i++) {
			bytes[i]=(byte)Integer.parseInt(s.substring(2*i,2*(i+1)), 16);
		}
		
		return bytes;
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		byte[] bytes = hexToBytes("FF F5 B2 00 10 D7 68 00 00 00 00 00");
				
		System.out.println(JUtilMath.bytesToString(bytes, true, 16, true));
	}
}
