package j.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 肖炯
 *
 */
public class JUtilMath extends JUtilSorter {
	private static final long serialVersionUID = 1L;
	private static final JUtilMath instance=new JUtilMath();
	public static JUtilMath getInstance() {return instance;}
	
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
	 * 是否长整型值
	 * @param src
	 * @return
	 */
	public static boolean isLong(String src){
//		if(src==null||(!src.matches("^[0-9a-fA-F]{1,}$")&&!src.matches("^-[0-9a-fA-F]{1,}$"))) return false;
		
		try{
			Long.parseLong(src);
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
	 * 解析科学计数法
	 * @param src 如：1.00000090586E11
	 * @return
	 */
	public static String convertScientificNotation(String src) throws Exception{
		if(src.indexOf(".")<0) return src;
		src=src.toUpperCase();
		if(src.indexOf("E")<0) return src;
		
		int digits=Integer.parseInt(src.substring(src.indexOf("E")+1)) + 1;
		src=src.substring(0,src.indexOf("E")).replaceAll("\\.", "");
		while(src.length()<digits) src+="0";

		return Long.valueOf(src).toString();
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
	public static boolean equals(Short v1,Short v2){
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
		
		//在原值基础上+/-比指定精度更小一个数量级的数，避免出现0.015四舍五入为0.01的情况，比如0.015后面是加0.0001
		String num = df.format(src+(src>=0?(1/Math.pow(10, precision+2)):(-1/Math.pow(10, precision+2))));  
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
		int maxFraction=precision>validDigits?precision:validDigits;
		String temp=formatPrint(src, maxFraction);
	
		String fraction="";
		String integer=temp;
		int dot=temp.indexOf(".");
		if(dot>0){
			fraction=temp.substring(dot+1);
			integer=temp.substring(0, dot);
		}
		
		while(fraction.length()<validDigits){
			fraction+="0";
		}
		
		while(fraction.length()>maxFraction && fraction.endsWith("0")){
			fraction=fraction.substring(0, fraction.length() - 1);
		}

		return integer+(fraction.length()==0?"":".")+fraction;
	}
	
	/**
	 * 格式化数字，precision位小数，并且保留最少validDigits为有效数字（如果有小数部分），四舍五入
	 * @param src
	 * @param precision
	 * @param validDigits
	 * @return
	 */
	public static String formatPrintWithoutZero(double src,int precision,int validDigits){
		int maxFraction=precision>validDigits?precision:validDigits;
		String temp=formatPrint(src, maxFraction);
	
		String fraction="";
		String integer=temp;
		int dot=temp.indexOf(".");
		if(dot>0){
			fraction=temp.substring(dot+1);
			integer=temp.substring(0, dot);
		}
		
		while(fraction.length()<validDigits){
			fraction+="0";
		}

		while(fraction.length()>validDigits && fraction.endsWith("0")){
			fraction=fraction.substring(0, fraction.length() - 1);
		}

		return integer+(fraction.length()==0?"":".")+fraction;
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
		
		String s="";
		if(p2==null||p2.equals("")) s=p1;
		else s=p1+"."+p2;
		if("-0".endsWith(s)) s="0";
		
		return s;
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
	 * @param numbers
	 * @return
	 */
	public static BigDecimal factorial(int numbers) {
		if(numbers<1) return new BigDecimal(1);
		BigDecimal f=new BigDecimal(1);
		for(int i=1;i<=numbers;i++) f=f.multiply(new BigDecimal(i));
		return f;
	}
	
	/**
	 * @deprecated
	 * @param scope
	 * @param selected
	 * @return
	 */
	public static long p(int scope, int selected){
		BigDecimal f1=factorial(scope);
		BigDecimal f2=factorial(selected);
		//System.out.println("f1-----"+f1.toPlainString());
		//System.out.println("f2-----"+f2.toPlainString());
		f1=f1.divide(f2);
		return f1.longValue();
	}
	
	/**
	 * 
	 * @param scope
	 * @param selected
	 * @return
	 */
	public static BigDecimal pNew(int scope, int selected){
		BigDecimal f1=factorial(scope);
		BigDecimal f2=factorial(scope-selected);
		f1=f1.divide(f2);
		return f1;
	}
	
	/**
	 * 
	 * @param scope
	 * @param selected
	 * @return
	 */
	public static long c(int scope, int selected){
		BigDecimal f1=pNew(scope,selected);
		BigDecimal f2=factorial(selected);
		f1=f1.divide(f2);
		return f1.longValue();
	}
	
	/**
	 * 打印出所有排列：从先选择1个元素，再从剩下元素中选择selected-1个元素......以此递归
	 * @param objects 对象列表
	 * @param selected 排列元素数
	 * @param assembled 已经选出的排列
	 * @param assembling 正在组装的排列
	 * @return
	 */
	public static void pPrint(List objects, int selected, List<List> assembled, List assembling){
		for(int j=0; j<objects.size(); j++) {
			List assemblingCopy=new ArrayList();
			assemblingCopy.addAll(assembling);
			assemblingCopy.add(objects.get(j));
			
			if(assemblingCopy.size()==selected) {
				assembled.add(assemblingCopy);
			}else {
				List objectsCopy=new ArrayList();
				objectsCopy.addAll(objects);
				objectsCopy.remove(objects.get(j));
				
				pPrint(objectsCopy, selected, assembled, assemblingCopy);
			}
		}
	}
	
	/**
	 * 打印出所有组合：从先选择1个元素，再从剩下元素中选择selected-1个元素......以此递归，并去掉重复组合
	 * @param objects 对象列表
	 * @param selected 组合元素数
	 * @param assembled 已经选出的组合
	 * @param assembling 正在组装的组合
	 * @return
	 */
	public static void cPrint(List objects, int selected, List<List> assembled, List assembling){
		for(int j=0; j<objects.size(); j++) {
			List assemblingCopy=new ArrayList();
			assemblingCopy.addAll(assembling);
			assemblingCopy.add(objects.get(j));
			
			if(assemblingCopy.size()==selected) {
				if(!cPermutationExists(assembled, assemblingCopy)) assembled.add(assemblingCopy);
			}else {
				List objectsCopy=new ArrayList();
				objectsCopy.addAll(objects);
				objectsCopy.remove(objects.get(j));
				
				cPrint(objectsCopy, selected, assembled, assemblingCopy);
			}
		}
	}
	
	/**
	 * 对组合内元素进行排序，然后拼串（为去掉重复组合）
	 * @param array
	 * @return
	 */
	private static String cPermutationFeature(List array) {
		array=JUtilString.getInstance().bubble(array, JUtilSorter.ASC);
		StringBuffer s=new StringBuffer();
		for(int i=0; i<array.size(); i++) {
			if(i>0) s.append(",");
			s.append(array.get(i).toString());
		}
		return s.toString();
	}
	
	/**
	 * 组合（assembling）是否已经存在列表中（assembled）
	 * @param assembled
	 * @param assembling
	 * @return
	 */
	private static boolean cPermutationExists(List assembled, List assembling) {
		String assemblingFeature=cPermutationFeature(assembling);
		for(int i=0; i<assembled.size(); i++) {
			List array=(List)assembled.get(i);
			if(assemblingFeature.equals(cPermutationFeature(array))) return true;
		}
		return false;
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
	public static String digit2CnChar(int digit){
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
	 * 
	 * @param digit
	 * @return
	 */
	public static String digit2CnCharSimple(int digit){
		if(digit==0){
			return "零";
		}else if(digit==1){
			return "一";
		}else if(digit==2){
			return "二";
		}else if(digit==3){
			return "三";
		}else if(digit==4){
			return "四";
		}else if(digit==5){
			return "五";
		}else if(digit==6){
			return "六";
		}else if(digit==7){
			return "七";
		}else if(digit==8){
			return "八";
		}else if(digit==9){
			return "九";
		}else if(digit==10){
			return "十";
		}else if(digit==100){
			return "百";
		}else if(digit==1000){
			return "千";
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
			
			if(radix==16&&v<=0xF) sb.append("0");
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
	
	/*
	 *  (non-Javadoc)
	 * @see j.util.JUtilSorter#compare(java.lang.Object, java.lang.Object)
	 */
	public String compare(Object pre, Object after){
		if (pre == null) {
			if (after == null) return JUtilSorter.EQUAL;
			else return JUtilSorter.SMALLER;
		} else {
			if (after == null) return JUtilSorter.BIGGER;
			else if(pre instanceof Double){
				Double p = (Double) pre;
				Double a = (Double) after;
				if (JUtilMath.equals(p, a)) return JUtilSorter.EQUAL;
				else if (JUtilMath.isSmaller(p, a, 6)) return JUtilSorter.SMALLER;
				else return JUtilSorter.BIGGER;
			}else if(pre instanceof Integer){
				Integer p = (Integer) pre;
				Integer a = (Integer) after;
				if (p==a) return JUtilSorter.EQUAL;
				else if (p<a) return JUtilSorter.SMALLER;
				else return JUtilSorter.BIGGER;
			}else if(pre instanceof Long){
				Long p = (Long) pre;
				Long a = (Long) after;
				if (p==a) return JUtilSorter.EQUAL;
				else if (p<a) return JUtilSorter.SMALLER;
				else return JUtilSorter.BIGGER;
			}else {
				return JUtilSorter.EQUAL;
			}
		}
	}
	
	/**
	 * 
	 * @param array
	 * @param number
	 * @return
	 */
	public static boolean contains(Integer[] array,Integer number) {
		if(array==null||number==null) return false;
		
		for(int i=0;i<array.length;i++) {
			if(JUtilMath.equals(array[i], number)) return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		long c1=JUtilMath.c(49,3);
		long c2=JUtilMath.c(6,3);
		System.out.println(Timestamp.valueOf("2021-02-19 07:00:00").getTime());	
		System.out.println(Timestamp.valueOf("2021-02-20 06:00:00").getTime());			
	}
}
