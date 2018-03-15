package j.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

/**
 * 
 * @author 肖炯
 *
 */
public class JUtilPinYin {
	private  static HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();   
    
	static{
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);   
	    defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE); 
	}
	
	
	//====================汉语转拼音=========================//
	public static String toPinYin(String src,String spliter,boolean uppercaseFirstChar) {
		if(src==null||"".equals(src)) return src;
		char[] temp=src.toCharArray();
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<temp.length;i++){
			try{
				String[] pys=PinyinHelper.toHanyuPinyinStringArray(temp[i],defaultFormat);
				
				if(pys!=null&&pys.length>0){
					String py=pys[0];
					if(uppercaseFirstChar) py=JUtilString.upperFirstChar(py);
					
					sb.append(py);
					sb.append(spliter);
				}else{
					sb.append(temp[i]);
				}
			}catch(Exception e){
				e.printStackTrace();
				sb.append(temp[i]);
			}
		}
		return sb.toString();
	}
	//====================汉语转拼音 end=========================//
}
