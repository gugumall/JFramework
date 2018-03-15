package j.security;

import j.util.JUtilList;
import j.util.JUtilMap;
import j.util.JUtilString;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 
 * @author 肖炯
 *
 */
public class StringEncrypt{
	private static final String[] nums=new String[]{
			"a",
			"c",
			"d",
			"f",
			"x",
			"k",
			"w",
			"p",
			"z",
			"v"
	};
	
	private static final String[] splitters=new String[]{"s","y","m","n","q","j","l"};
	
	/**
	 * 加密
	 * 
	 * 每个字符都加0~9
	 * 以如下格式记录每次变化（每个字符的变化信息位置是随机的）：字符位置增加量splitter字符位置增加量splitter字符位置增加量
	 * 字符位置表示哪个位置的字母作了改变，增加量表示改变量，splitter为StringEncrypt的静态变量splitter的值，分隔各变化位置
	 * 字符位置、增加量的每位数字用nums对应位置的字符表示，如9用nums[9]表示，其中字符位置的个、十、百...各位颠倒顺序
	 * @param value
	 * @return
	 */
	public static String[] encrypt(String value) {
		if(value==null||value.equals("")){
			return null;
		}
		
		StringBuffer sb=new StringBuffer(value);
		
		Random ran=new Random();
		Map decryptKeys=new HashMap();
		String decryptKey="";
		for(int i=0;i<sb.length();i++){
			char c=sb.charAt(i);
			int add=ran.nextInt(nums.length);			
			char newChar=(char)(c-add);
			while(newChar=='&'
				||newChar=='?'
				||newChar=='\''
				||newChar=='\\'
				||newChar=='+'){
				add=ran.nextInt(nums.length);			
				newChar=(char)(c-add);
			}
			sb.setCharAt(i,newChar);
			
			String pos="";
			String iStr=i+"";
			for(int j=iStr.length();j>0;j--){
				pos+=nums[Integer.parseInt(iStr.substring(j-1,j))];
			}
			decryptKeys.put(JUtilString.randomStr(6)+i,pos+nums[add]);
		}
		
		for(Iterator it=decryptKeys.values().iterator();it.hasNext();){
			decryptKey+=it.next()+splitters[ran.nextInt(splitters.length)];
		}
		
		JUtilMap.clear_AllNull(decryptKeys);
		
		return new String[]{sb.toString(),decryptKey.substring(0,decryptKey.length()-1)};		
	}

	/**
	 * 解密
	 * 
	 * @param value
	 * @param key
	 * @return
	 */
	public static String decrypt(String value, String key) {
		if(value==null||value.equals("")){
			return null;
		}
		
		if(key==null||key.equals("")){
			return null;
		}

		for(int i=0;i<splitters.length;i++){
			key=key.replaceAll(splitters[i],"S");
		}
		String[] tokens=JUtilString.getTokens(key,"S");
		
		if(tokens.length!=value.length()) return value;

		List cells=new LinkedList();
		for(int j=0;j<value.length();j++){
			cells.add("");
		}
		
		for(int j=0;j<value.length();j++){
			int add=getNum(tokens[j].substring(tokens[j].length()-1));
			int pos=getPos(tokens[j].substring(0,tokens[j].length()-1));
			cells.set(pos,(char)(value.charAt(pos)+add)+"");
		}
		
		String ret="";
		for(int i=0;i<cells.size();i++){
			ret+=cells.get(i);
		}
		
		JUtilList.clear_AllNull(cells);
		
		return ret;
	}	
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	private static int getNum(String str){
		for(int i=0;i<nums.length;i++){
			if(nums[i].equals(str)) return i;
		}
		return -1;
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	private static int getPos(String str){
		String pos="";
		for(int i=str.length();i>0;i--){
			pos+=getNum(str.substring(i-1,i));
		}
		return Integer.parseInt(pos);
	}
	
	//  0,1,2,3,4,5,6,7,8,9
	//a
	//b
	//c
	//d
	//e
	
	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args)throws Exception{
		/**
		 * ABLE: 118483.930
BALANCE_USABLE_ENCRYPT: **5350)33*0,/ zalpwqxfjcpydfsapmacaydccsvwycczlkfqwkjfc
        BALANCE_FROZEN: 0.000
BALANCE_FROZEN_ENCRYPT: -*)0--)/ famwpykfldpnafjcxnxfqpc
           UPDATE_TIME: 2016-12-25 19:25:00

		 */
		String s1="**5350)33*0,/";
		String s2="zalpwqxfjcpydfsapmacaydccsvwycczlkfqwkjfc";
	
		System.out.println(StringEncrypt.decrypt(s1,s2));
	}
}
