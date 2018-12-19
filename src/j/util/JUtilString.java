/*
 * Created on 2004-5-21
 *
 */
package j.util;

import j.fs.JDFSFile;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 
 * @author 肖炯
 *
 */
public class JUtilString extends JUtilSorter {
	private static final long serialVersionUID = 1L;

	public static int intValueOfUpperCaseA = (int) 'A';

	public static int intValueOfUpperCaseZ = (int) 'Z';

	public static int intValueOfLowerCaseA = (int) 'a';

	public static int intValueOfLowerCaseZ = (int) 'z';

	public static int intValueOf0 = (int) '0';

	public static int intValueOf9 = (int) '9';
	
	public static String[] irregularChars =new String[]{""+(char)65533,""+(char)65279};//乱码
	
	public static final String RegExpHttpUrl="^(?i)\\b((?:[a-z][\\w-]+:(?:/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:\\'\".,<>?«»“”‘’]))$";
	public static final String RegExpEmail="^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
	public static final String RegExpMobilePhone="^((\\+86)|(86))?1[3-8]+\\d{9}$";
	public static final String RegExpMobilePhoneWorldwide="^(\\+{0,1}\\d{2,3})?[0-9\\-]{3,16}$";
	public static final String RegExpTelephone="^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?$";
	public static final String RegExpNameCn="^([\\u4E00-\\u9FA5]{2,6})|([\\u4E00-\\u9FA5]{1,}\\.[\\u4E00-\\u9FA5]{1,})$";
	public static final String RegExpName="^([a-zA-Z_.,\\- ]{1,150})$"; 
	public static final String RegExpNick="^([\\u4E00-\\u9FA5a-zA-Z_.,\\- ]{2,16})$"; 
	public static final String RegExpComNameCn="^[\\u4E00-\\u9FA5]{2,30}$";
	public static final String RegExpComName="^([a-zA-Z_.,\\- ]{1,150})$"; 
	public static final String RegExpCharCn="^[\\u4E00-\\u9FA5]{0,}$";
	public static final String RegExpIdcard="^[0-9]{17}[0-9Xx]{1}$";
	public static final String RegExpBankCard="^([\\d]{16})|([\\d]{19})|([\\d]{20})$";
	public static final String RegCompanyLicenseNum="^(\\d{13})|(\\d{15})|(\\d{18})$";
	public static final String RegExpRegularName="^[\\S ]{0,}$"; 
	private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };


	/*
	 *  (non-Javadoc)
	 * @see j.util.JUtilSorter#compare(java.lang.Object, java.lang.Object)
	 */
	public String compare(Object pre, Object after){
		if (pre == null) {
			if (after == null)
				return JUtilSorter.EQUAL;
			else
				return JUtilSorter.SMALLER;
		} else {
			if (after == null)
				return JUtilSorter.BIGGER;
			else {
				String p = (String) pre;
				String a = (String) after;
				int r = p.compareTo(a);
				if (r == 0)
					return JUtilSorter.EQUAL;
				else if (r < 0)
					return JUtilSorter.SMALLER;
				else
					return JUtilSorter.BIGGER;
			}
		}
	}


	/**
	 * 字符串含有的字节数
	 * 
	 * @param str
	 * @return
	 */
	public static int bytes(String str) {
		if (str == null) {
			return -1;
		}
		return str.getBytes().length;
	}

	/**
	 * 字符串含有的字节数
	 * @param str
	 * @param encoding
	 * @return
	 */
	public static int bytes(String str, String encoding) {
		if (str == null) {
			return -1;
		}
		try {
			return str.getBytes(encoding).length;
		} catch (Exception e) {
			return str.getBytes().length;
		}
	}

	/**
	 * parent数组中是否包含subStr
	 * 
	 * @param parent
	 * @param subStr
	 * @return
	 */
	public static boolean contain(String[] parent, String subStr) {
		if (parent == null || parent.length==0) {
			return false;
		}
		
		for (int i = 0; i < parent.length; i++) {
			if(parent[i]==null&&subStr==null) return true;
			
			if (parent[i].equals(subStr)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断一个字符串用指定分割符分割成一个或多个子字符串后，其中是否包含一个指定的字符串
	 * 
	 * @param parentStr 父字符串
	 * @param subStr 子字符串
	 * @param splitter 分割符
	 * @return boolean
	 */
	public static boolean contain(String parentStr, String subStr, String splitter) {
		String[] tokens = getTokens(parentStr, splitter);// 分割父字符串

		return contain(tokens, subStr);
	}

	/**
	 * 判断parent里面是否包含subStrings数组中的任意一个字符串
	 * 
	 * @param parent
	 * @param subStrings
	 * @return
	 */
	public static boolean contain(String parent, String[] subStrings) {
		if (subStrings == null || subStrings.length==0) {
			return false;
		}
		if (parent == null || parent.equals("")) {
			return false;
		}
		for (int i = 0; i < subStrings.length; i++) {
			if (parent.equals(subStrings[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * parent数组中是否包含subStr
	 * 
	 * @param parent
	 * @param subStr
	 * @return
	 */
	public static boolean containIgnoreCase(String[] parent, String subStr) {
		if (parent == null || parent.length==0) {
			return false;
		}
		
		for (int i = 0; i < parent.length; i++) {
			if(parent[i]==null&&subStr==null) return true;
			
			if (parent[i].equalsIgnoreCase(subStr)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断一个字符串用指定分割符分割成一个或多个子字符串后，其中是否包含一个指定的字符串
	 * 
	 * @param parentStr 父字符串
	 * @param subStr 子字符串
	 * @param splitter 分割符
	 * @return boolean
	 */
	public static boolean containIgnoreCase(String parentStr, String subStr, String splitter) {
		String[] tokens = getTokens(parentStr, splitter);// 分割父字符串

		return containIgnoreCase(tokens, subStr);
	}

	/**
	 * 判断parent里面是否包含subStrings数组中的任意一个字符串
	 * 
	 * @param parent
	 * @param subStrings
	 * @return
	 */
	public static boolean containIgnoreCase(String parent, String[] subStrings) {
		if (subStrings == null || subStrings.length==0) {
			return false;
		}
		if (parent == null || parent.equals("")) {
			return false;
		}
		for (int i = 0; i < subStrings.length; i++) {
			if (parent.equalsIgnoreCase(subStrings[i])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param parent
	 * @param subStr
	 * @return
	 */
	public static boolean exists(String[] parent, String subStr) {
		if (parent == null || parent.length==0) {
			return false;
		}
		if (subStr == null || subStr.equals("")) {
			return false;
		}
		for (int i = 0; i < parent.length; i++) {
			if (parent[i] != null && parent[i].indexOf(subStr)>-1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param parent
	 * @param subStr
	 * @return
	 */
	public static boolean exists(String parent, String[] subStrs) {
		if (subStrs == null || subStrs.length==0) {
			return false;
		}
		if (parent == null || parent.equals("")) {
			return false;
		}
		for (int i = 0; i < subStrs.length; i++) {
			if (subStrs[i] != null && parent.indexOf(subStrs[i])>-1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param parent
	 * @param subStr
	 * @return
	 */
	public static boolean existsIgnoreCase(String[] parent, String subStr) {
		if (parent == null || parent.length==0) {
			return false;
		}
		if (subStr == null || subStr.equals("")) {
			return false;
		}
		for (int i = 0; i < parent.length; i++) {
			if (parent[i] != null && parent[i].toUpperCase().indexOf(subStr.toUpperCase())>-1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param parent
	 * @param subStr
	 * @return
	 */
	public static boolean existsIgnoreCase(String parent, String[] subStrs) {
		if (subStrs == null || subStrs.length==0) {
			return false;
		}
		if (parent == null || parent.equals("")) {
			return false;
		}
		for (int i = 0; i < subStrs.length; i++) {
			if (subStrs[i] != null && parent.toUpperCase().indexOf(subStrs[i].toUpperCase())>-1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将一个字符串用指定分割符分割成一个或多个子字符串，如果该字符串为空、分割符为空、程序抛出异常则返回null，<br>
	 * 如果该字符串中不包含指定的分割符，则返回长度为1的字符串数组，数组第一个元素为该字符串本身内容
	 * 
	 * @param src 将要被分割的字符串
	 * @param splitter 分割符
	 * @return java.lang.String[]
	 */
	public static String[] getTokens(String src, String splitter) {
		String ret[] = null;// 返回值
		try {
			StringBuffer sb = new StringBuffer(src);

			if (src == null || src.length() == 0 || splitter == null || splitter.length() == 0) {// 源字符串为空或分割符为空，返回null
				return null;
			}else if (sb.indexOf(splitter) == -1) {// 字符串中不包含指定的分割符，则返回长度为1的字符串数组，数组第一个元素为该字符串本身内容
				return new String[] { src };
			}else {// 将源字符串用指定分割符分割成一个或多个子字符串
				int index = sb.indexOf(splitter);
				List tokenList = new LinkedList();
				while (index != -1) {
					tokenList.add(sb.substring(0, index));
					sb.delete(0, index + splitter.length());
					index = sb.indexOf(splitter);
				}
				tokenList.add(sb.toString());
				ret = new String[tokenList.size()];
				tokenList.toArray(ret);
				return ret;
			}
		} catch (Exception e) {
			// 抛出异常，返回null
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将一个字符串用指定分割符分割成一个或多个子字符串(不包含首末尾的空子串)，如果该字符串为空、分割符为空、程序抛出异常则返回null，<br>
	 * 如果该字符串中不包含指定的分割符，则返回长度为1的字符串数组，数组第一个元素为该字符串本身内容
	 * 
	 * @param src 将要被分割的字符串
	 * @param splitter 分割符
	 * @return java.lang.String[]
	 */
	public static String[] getTokensWithoutEmptyStr(String src, String splitter) {
		String ret[] = null;// 返回值
		try {
			StringBuffer sb = new StringBuffer(src);

			if (src == null || src.length() == 0 || splitter == null || splitter.length() == 0) {// 源字符串为空或分割符为空，返回null
				return null;
			}else if (sb.indexOf(splitter) == -1) {// 字符串中不包含指定的分割符，则返回长度为1的字符串数组，数组第一个元素为该字符串本身内容
				return new String[] { src };
			}else {// 将源字符串用指定分割符分割成一个或多个子字符串
				int index = sb.indexOf(splitter);
				List tokenList = new LinkedList();
				int i = 0;
				while (index != -1) {
					String token = sb.substring(0, index);
					if (token.length() > 0 || i > 0) {//如果不是空串或者不是首位子串
						tokenList.add(token);
					}
					sb.delete(0, index + splitter.length());
					index = sb.indexOf(splitter);
					i++;
				}
				if (sb.length() != 0) {//如果末尾子串不为空
					tokenList.add(sb.toString());
				}
				ret = new String[tokenList.size()];
				tokenList.toArray(ret);
				return ret;
			}
		} catch (Exception e) {
			// 抛出异常，返回null
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 不使用正则表达式的替换
	 * 
	 * @param src 源字符串
	 * @param substring 需要被替换的子串
	 * @param alt 替换内容
	 * @return String
	 */
	public static String replaceAll(String src, String substring, String alt) {
		if (src == null || src.equals("")) {
			return src;
		}
		if (substring == null || substring.equals("")) {
			return src;
		}
		if (alt == null) {
			return src;
		}
		StringBuffer sb = new StringBuffer(src);

		int tokenPos = sb.indexOf(substring);
		int tokenLen = substring.length();
		while (tokenPos != -1) {
			sb.replace(tokenPos, tokenPos + tokenLen, alt);
			tokenPos = sb.indexOf(substring, tokenPos + alt.length());
		}
		String tmp = sb.toString();
		sb = null;
		return tmp;
	}
	

	/**
	 * 模糊匹配
	 * 
	 * @param src 源字符串，比如 http://www.sina.com.cn
	 * @param pattern 模式，日如 http://|-|.sina.com.cn
	 * @param wildcard 通配符，表示0个或多个任意字符，比如|-|
	 * @return
	 */
	public static int match(String src, String pattern, String wildcard) {
		if (src == null || src.equals("")) {//源字符串为空
			return -1;
		}

		if (pattern == null || pattern.equals("")) {//匹配模式为空
			return -1;
		}

		//匹配模式和源字符串相同，或匹配模式只包含通配符
		if (pattern.equals(wildcard) || JUtilString.replaceAll(pattern, wildcard, "").equals("")) {
			return 0;
		}

		if (wildcard == null 
				|| wildcard.equals("")
				||pattern.indexOf(wildcard) < 0) {//通配符为空/配模式不含通配符，则寻找子串
			return src.indexOf(pattern);
		}

		String[] tokens = JUtilString.getTokensWithoutEmptyStr(pattern, wildcard);

		if (tokens == null || tokens.length == 0) {
			return src.indexOf(pattern);
		}

		int index = -1;
		int startIndex = -1;
		int i = 0;
		for (i = 0; i < tokens.length; i++) {
			index = src.indexOf(tokens[i], index);
			if (index < 0) {
				break;
			} else {
				if (i == 0) {
					startIndex = index;
				}
				index += tokens[i].length();
			}
		}
		if (i < tokens.length) {
			return -1;
		}
		return startIndex;
	}

	/**
	 * 模糊匹配，忽略大小写
	 * 
	 * @param src 源字符串，比如 http://www.sina.com.cn
	 * @param pattern  模式，日如 http://|-|.sina.com.cn
	 * @param wildcard 通配符，表示0个或多个任意字符，比如|-|
	 * @return
	 */
	public static int matchIgnoreCase(String src, String pattern, String wildcard) {
		if (src == null || src.equals("")) {//源字符串为空
			return -1;
		}

		if (pattern == null || pattern.equals("")) {//匹配模式为空
			return -1;
		}

		//匹配模式和源字符串相同，或匹配模式只包含通配符
		if (pattern.equals(wildcard) || JUtilString.replaceAll(pattern, wildcard, "").equals("")) {
			return 0;
		}

		src = src.toUpperCase();//大写
		pattern = pattern.toUpperCase();//大写

		if (wildcard == null 
				|| wildcard.equals("")
				||pattern.indexOf(wildcard) < 0) {//通配符为空/配模式不含通配符，则寻找子串
			return src.indexOf(pattern);
		}

		String[] tokens = JUtilString.getTokensWithoutEmptyStr(pattern, wildcard);

		if (tokens == null || tokens.length == 0) {
			return src.indexOf(pattern);
		}

		int index = -1;
		int startIndex = -1;
		int i = 0;
		for (i = 0; i < tokens.length; i++) {
			index = src.indexOf(tokens[i], index);
			if (index < 0) {
				break;
			} else {
				if (i == 0) {
					startIndex = index;
				}
				index += tokens[i].length();
			}
		}
		if (i < tokens.length) {
			return -1;
		}
		return startIndex;
	}

	
	/**
	 * 替换文本中的乱码
	 * 
	 * @param str
	 * @param encoding 已无用，仅为兼容其它版本
	 * @return
	 */
	public static String reviseString(String str, String encoding) {
		if (str == null||str.equals("")) {
			return str;
		}
		
		if(!"utf-8".equalsIgnoreCase(encoding)) return str;

		for(int i=0;i<irregularChars.length;i++){
			if(str.startsWith(irregularChars[i])) str=str.substring(1);
		}
		return str;
	}

	/**
	 * 得到一组长度为length的随机字符串，包括大小写英文字母，阿拉伯数字，但排除相似的1、l、0、o、O
	 * 
	 * @param length
	 * @return
	 */
	public static String randomStr(int length) {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			char c = (char) r.nextInt(intValueOfLowerCaseZ + 1);
			while (!((c >= intValueOfUpperCaseA && c <= intValueOfUpperCaseZ) 
					|| (c >= intValueOfLowerCaseA && c <= intValueOfLowerCaseZ) 
					|| (c >= intValueOf0 && c <= intValueOf9)) 
					
					|| c == '0' 
					|| c == 'o' 
					|| c == 'O' 
					|| c == 'l' 
					|| c == '1') {
				c = (char) r.nextInt(intValueOfLowerCaseZ + 1);
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 得到一组长度为length的随机数字
	 * @param length
	 * @return
	 */
	public static String randomNum(int length) {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			char c = (char) r.nextInt(intValueOf9 + 1);
			while (!(c >= intValueOf0 && c <= intValueOf9)) {
				c = (char) r.nextInt(intValueOf9 + 1);
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 随机颜色(十六进制表示的rgb颜色)
	 * 
	 * @return
	 */
	public static String randomColorStr() {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 6; i++) {
			char c = (char) r.nextInt((int)'F' + 1);
			while (!((c >= intValueOfUpperCaseA && c <= 'F') || (c >= intValueOf0 && c <= intValueOf9))) {
				c = (char) r.nextInt((int)'F' + 1);
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 得到一组长度为length的随机数字
	 * @param length
	 * @return
	 */
	public static String randomCnName(int length) {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(zhCnStr.charAt(r.nextInt(zhCnStr.length())));
		}
		return sb.toString();
	}
	

	//====================网络地址处理=========================//
	/**
	 * 得到域名（不带端口），小写
	 * @param url
	 * @return
	 */
	public static final String getHost(String url) {
		int hostStart = url.indexOf("://")>0?(url.indexOf("://") + 3):0;
		int hostEnd = url.indexOf("/", hostStart + 1);
		if (hostEnd == -1) {
			hostEnd = url.length();
		}
		String host = url.substring(hostStart, hostEnd);
		if (host.indexOf(":") > -1) {
			host = host.substring(0, host.indexOf(":"));
		}
		return host.toLowerCase();
	}

	/**
	 * 得到域名（带端口），小写
	 * @param url
	 * @return
	 */
	public static final String getHostWithPort(String url) {
		int hostStart = url.indexOf("://")>0?(url.indexOf("://") + 3):0;
		int hostEnd = url.indexOf("/", hostStart + 1);
		if (hostEnd == -1) {
			hostEnd = url.length();
		}
		return url.substring(hostStart, hostEnd).toLowerCase();
	}
	
	/**
	 * 
	 * @param url
	 * @param cells
	 * @return
	 */
	public static final String getMainDomain(String url,int cells){
		String host=getHost(url);
		String[] _cells=host.split("\\.");
		if(_cells.length<cells) return host;
		
		host="";
		for(int i=_cells.length-cells;i<_cells.length;i++){
			host+=_cells[i]+".";
		}
		return host.substring(0,host.length()-1);
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static final String getMainDomain(String url){
		return getMainDomain(url,2);
	}


	/**
	 * 得到协议，http、https等，小写
	 * @param url
	 * @return
	 */
	public static final String getProtocal(String url) {
		return url.substring(0, url.indexOf(":")).toLowerCase();
	}
	
	/**
	 * 得到网址根地址
	 * @param url
	 */
	public static String getUrlBase(String url){
		int i=url.indexOf("/",8);
		if(i<0) return url;
		else return url.substring(0,i+1);
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String getUri(String url){
		if(url!=null&&url.startsWith("http")){
			url=url.substring(8);
			url=url.substring(url.indexOf("/"));
			if(url.indexOf("?")>0) url=url.substring(0,url.indexOf("?"));
		}
		return url;
	}
	//====================网络地址处理 end=========================//
	

	//====================繁体简体转换=========================//
	//常用简体字符
	public static final String zhCnStr = "注汇币赢并确号别杯皑蔼碍爱翱袄奥坝罢摆败颁办绊帮绑镑谤剥饱宝报鲍辈贝钡狈备惫绷笔毕毙闭边编贬变辩辫鳖瘪濒滨宾摈饼拨钵铂驳卜补参蚕残惭惨灿苍舱仓沧厕侧册测层诧搀掺蝉馋谗缠铲产阐颤场尝长偿肠厂畅钞车彻尘陈衬撑称惩诚骋痴迟驰耻齿炽冲虫宠畴踌筹绸丑橱厨锄雏础储触处传疮闯创锤纯绰辞词赐聪葱囱从丛凑窜错达带贷担单郸掸胆惮诞弹当挡党荡档捣岛祷导盗灯邓敌涤递缔点垫电淀钓调迭谍叠钉顶锭订东动栋冻斗犊独读赌镀锻断缎兑队对吨顿钝夺鹅额讹恶饿儿尔饵贰发罚阀珐矾钒烦范贩饭访纺飞废费纷坟奋愤粪丰枫锋风疯冯缝讽凤肤辐抚辅赋复负讣妇缚该钙盖干赶秆赣冈刚钢纲岗皋镐搁鸽阁铬个给龚宫巩贡钩沟构购够蛊顾剐关观馆惯贯广规硅归龟闺轨诡柜贵刽辊滚锅国过骇韩汉阂鹤贺横轰鸿红后壶护沪户哗华画划话怀坏欢环还缓换唤痪焕涣黄谎挥辉毁贿秽会烩汇讳诲绘荤浑伙获货祸击机积饥讥鸡绩缉极辑级挤几蓟剂济计记际继纪夹荚颊贾钾价驾歼监坚笺间艰缄茧检碱硷拣捡简俭减荐槛鉴践贱见键舰剑饯渐溅涧浆蒋桨奖讲酱胶浇骄娇搅铰矫侥脚饺缴绞轿较秸阶节茎惊经颈静镜径痉竞净纠厩旧驹举据锯惧剧鹃绢杰洁结诫届紧锦仅谨进晋烬尽劲荆觉决诀绝钧军骏开凯颗壳课垦恳抠库裤夸块侩宽矿旷况亏岿窥馈溃扩阔蜡腊莱来赖蓝栏拦篮阑兰澜谰揽览懒缆烂滥捞劳涝乐镭垒类泪篱离里鲤礼丽厉励砾历沥隶俩联莲连镰怜涟帘敛脸链恋炼练粮凉两辆谅疗辽镣猎临邻鳞凛赁龄铃凌灵岭领馏刘龙聋咙笼垄拢陇楼娄搂篓芦卢颅庐炉掳卤虏鲁赂禄录陆驴吕铝侣屡缕虑滤绿峦挛孪滦乱抡轮伦仑沦纶论萝罗逻锣箩骡骆络妈玛码蚂马骂吗买麦卖迈脉瞒馒蛮满谩猫锚铆贸么霉没镁门闷们锰梦谜弥觅绵缅庙灭悯闽鸣铭谬谋亩钠纳难挠脑恼闹馁腻撵捻酿鸟聂啮镊镍柠狞宁拧泞钮纽脓浓农疟诺欧鸥殴呕沤盘庞国爱赔喷鹏骗飘频贫苹凭评泼颇扑铺朴谱脐齐骑岂启气弃讫牵扦钎铅迁签谦钱钳潜浅谴堑枪呛墙蔷强抢锹桥乔侨翘窍窃钦亲轻氢倾顷请庆琼穷趋区躯驱龋颧权劝却鹊让饶扰绕热韧认纫荣绒软锐闰润洒萨鳃赛伞丧骚扫涩杀纱筛晒闪陕赡缮伤赏烧绍赊摄慑设绅审婶肾渗声绳胜圣师狮湿诗尸时蚀实识驶势释饰视试寿兽枢输书赎属术树竖数帅双谁税顺说硕烁丝饲耸怂颂讼诵擞苏诉肃虽绥岁孙损笋缩琐锁獭挞抬摊贪瘫滩坛谭谈叹汤烫涛绦腾誊锑题体屉条贴铁厅听烃铜统头图涂团颓蜕脱鸵驮驼椭洼袜弯湾顽万网韦违围为潍维苇伟伪纬谓卫温闻纹稳问瓮挝蜗涡窝呜钨乌诬无芜吴坞雾务误锡牺袭习铣戏细虾辖峡侠狭厦锨鲜纤咸贤衔闲显险现献县馅羡宪线厢镶乡详响项萧销晓啸蝎协挟携胁谐写泻谢锌衅兴汹锈绣虚嘘须许绪续轩悬选癣绚学勋询寻驯训讯逊压鸦鸭哑亚讶阉烟盐严颜阎艳厌砚彦谚验鸯杨扬疡阳痒养样瑶摇尧遥窑谣药爷页业叶医铱颐遗仪彝蚁艺亿忆义诣议谊译异绎荫阴银饮樱婴鹰应缨莹萤营荧蝇颖哟拥佣痈踊咏涌优忧邮铀犹游诱舆鱼渔娱与屿语吁御狱誉预驭鸳渊辕园员圆缘远愿约跃钥岳粤悦阅云郧匀陨运蕴酝晕韵杂灾载攒暂赞赃脏凿枣灶责择则泽贼赠扎札轧铡闸诈斋债毡盏斩辗崭栈战绽张涨帐账胀赵蛰辙锗这贞针侦诊镇阵挣睁狰帧郑证织职执纸挚掷帜质钟终种肿众诌轴皱昼骤猪诸诛烛瞩嘱贮铸筑驻专砖转赚桩庄装妆壮状锥赘坠缀谆浊兹资渍踪综总纵邹诅组钻致钟么为只凶准启板里雳余链泄";
	
	//常用繁体字符
	public static final String zhTwStr = "註匯幣贏並確號別盃皚藹礙愛翺襖奧壩罷擺敗頒辦絆幫綁鎊謗剝飽寶報鮑輩貝鋇狽備憊繃筆畢斃閉邊編貶變辯辮鼈癟瀕濱賓擯餅撥缽鉑駁蔔補參蠶殘慚慘燦蒼艙倉滄廁側冊測層詫攙摻蟬饞讒纏鏟産闡顫場嘗長償腸廠暢鈔車徹塵陳襯撐稱懲誠騁癡遲馳恥齒熾沖蟲寵疇躊籌綢醜櫥廚鋤雛礎儲觸處傳瘡闖創錘純綽辭詞賜聰蔥囪從叢湊竄錯達帶貸擔單鄲撣膽憚誕彈當擋黨蕩檔搗島禱導盜燈鄧敵滌遞締點墊電澱釣調叠諜疊釘頂錠訂東動棟凍鬥犢獨讀賭鍍鍛斷緞兌隊對噸頓鈍奪鵝額訛惡餓兒爾餌貳發罰閥琺礬釩煩範販飯訪紡飛廢費紛墳奮憤糞豐楓鋒風瘋馮縫諷鳳膚輻撫輔賦複負訃婦縛該鈣蓋幹趕稈贛岡剛鋼綱崗臯鎬擱鴿閣鉻個給龔宮鞏貢鈎溝構購夠蠱顧剮關觀館慣貫廣規矽歸龜閨軌詭櫃貴劊輥滾鍋國過駭韓漢閡鶴賀橫轟鴻紅後壺護滬戶嘩華畫劃話懷壞歡環還緩換喚瘓煥渙黃謊揮輝毀賄穢會燴彙諱誨繪葷渾夥獲貨禍擊機積饑譏雞績緝極輯級擠幾薊劑濟計記際繼紀夾莢頰賈鉀價駕殲監堅箋間艱緘繭檢堿鹼揀撿簡儉減薦檻鑒踐賤見鍵艦劍餞漸濺澗漿蔣槳獎講醬膠澆驕嬌攪鉸矯僥腳餃繳絞轎較稭階節莖驚經頸靜鏡徑痙競淨糾廄舊駒舉據鋸懼劇鵑絹傑潔結誡屆緊錦僅謹進晉燼盡勁荊覺決訣絕鈞軍駿開凱顆殼課墾懇摳庫褲誇塊儈寬礦曠況虧巋窺饋潰擴闊蠟臘萊來賴藍欄攔籃闌蘭瀾讕攬覽懶纜爛濫撈勞澇樂鐳壘類淚籬離裏鯉禮麗厲勵礫曆瀝隸倆聯蓮連鐮憐漣簾斂臉鏈戀煉練糧涼兩輛諒療遼鐐獵臨鄰鱗凜賃齡鈴淩靈嶺領餾劉龍聾嚨籠壟攏隴樓婁摟簍蘆盧顱廬爐擄鹵虜魯賂祿錄陸驢呂鋁侶屢縷慮濾綠巒攣孿灤亂掄輪倫侖淪綸論蘿羅邏鑼籮騾駱絡媽瑪碼螞馬罵嗎買麥賣邁脈瞞饅蠻滿謾貓錨鉚貿麽黴沒鎂門悶們錳夢謎彌覓綿緬廟滅憫閩鳴銘謬謀畝鈉納難撓腦惱鬧餒膩攆撚釀鳥聶齧鑷鎳檸獰甯擰濘鈕紐膿濃農瘧諾歐鷗毆嘔漚盤龐國愛賠噴鵬騙飄頻貧蘋憑評潑頗撲鋪樸譜臍齊騎豈啓氣棄訖牽扡釺鉛遷簽謙錢鉗潛淺譴塹槍嗆牆薔強搶鍬橋喬僑翹竅竊欽親輕氫傾頃請慶瓊窮趨區軀驅齲顴權勸卻鵲讓饒擾繞熱韌認紉榮絨軟銳閏潤灑薩鰓賽傘喪騷掃澀殺紗篩曬閃陝贍繕傷賞燒紹賒攝懾設紳審嬸腎滲聲繩勝聖師獅濕詩屍時蝕實識駛勢釋飾視試壽獸樞輸書贖屬術樹豎數帥雙誰稅順說碩爍絲飼聳慫頌訟誦擻蘇訴肅雖綏歲孫損筍縮瑣鎖獺撻擡攤貪癱灘壇譚談歎湯燙濤縧騰謄銻題體屜條貼鐵廳聽烴銅統頭圖塗團頹蛻脫鴕馱駝橢窪襪彎灣頑萬網韋違圍爲濰維葦偉僞緯謂衛溫聞紋穩問甕撾蝸渦窩嗚鎢烏誣無蕪吳塢霧務誤錫犧襲習銑戲細蝦轄峽俠狹廈鍁鮮纖鹹賢銜閑顯險現獻縣餡羨憲線廂鑲鄉詳響項蕭銷曉嘯蠍協挾攜脅諧寫瀉謝鋅釁興洶鏽繡虛噓須許緒續軒懸選癬絢學勳詢尋馴訓訊遜壓鴉鴨啞亞訝閹煙鹽嚴顔閻豔厭硯彥諺驗鴦楊揚瘍陽癢養樣瑤搖堯遙窯謠藥爺頁業葉醫銥頤遺儀彜蟻藝億憶義詣議誼譯異繹蔭陰銀飲櫻嬰鷹應纓瑩螢營熒蠅穎喲擁傭癰踴詠湧優憂郵鈾猶遊誘輿魚漁娛與嶼語籲禦獄譽預馭鴛淵轅園員圓緣遠願約躍鑰嶽粵悅閱雲鄖勻隕運蘊醞暈韻雜災載攢暫贊贓髒鑿棗竈責擇則澤賊贈紮劄軋鍘閘詐齋債氈盞斬輾嶄棧戰綻張漲帳賬脹趙蟄轍鍺這貞針偵診鎮陣掙睜猙幀鄭證織職執紙摯擲幟質鍾終種腫衆謅軸皺晝驟豬諸誅燭矚囑貯鑄築駐專磚轉賺樁莊裝妝壯狀錐贅墜綴諄濁茲資漬蹤綜總縱鄒詛組鑽緻鐘麼為隻兇準啟闆裡靂餘鍊洩";
	
	private static Map zhCn_Tw_Mapping = new HashMap();//简体-繁体对照
	
	private static Map zhTw_Cn_Mapping = new HashMap();//繁体-简体对照
	static {
		for (int i = 0; i < zhCnStr.length(); i++) {
			zhCn_Tw_Mapping.put(zhCnStr.substring(i, i + 1), zhTwStr.substring(i, i + 1));
		}
		for (int i = 0; i < zhTwStr.length(); i++) {
			zhTw_Cn_Mapping.put(zhTwStr.substring(i, i + 1), zhCnStr.substring(i, i + 1));
		}
	}

	/**
	 * 转换成繁体
	 * @param src
	 * @return
	 */
	public static String toZhTw(String src) {
		String tmp = "";
		for (int i = 0; i < src.length(); i++) {
			String ch = src.substring(i, i + 1);
			if (zhCn_Tw_Mapping.get(ch) != null) {
				tmp += zhCn_Tw_Mapping.get(ch);
			} else {
				tmp += ch;
			}
		}
		return tmp;
	}

	/**
	 * 转换成简体
	 * @param src
	 * @return
	 */
	public static String toZhCn(String src) {
		String tmp = "";
		for (int i = 0; i < src.length(); i++) {
			String ch = src.substring(i, i + 1);
			if (zhTw_Cn_Mapping.get(ch) != null) {
				tmp += zhTw_Cn_Mapping.get(ch);
			} else {
				tmp += ch;
			}
		}
		return tmp;
	}
	//====================繁体简体转换 end=========================//
	

	/**
	 * xml特殊字符处理
	 * 
	 * @param src
	 * @return
	 */
	public static String xmlConvertSpecialCharacters(String src) {
		if (src == null || src.equals("")) {
			return src;
		}
		src = src.replaceAll("&gt;", "#gt;");
		src = src.replaceAll("&lt;", "#lt;");
		src = src.replaceAll("&amp;", "#amp;");
		src = src.replaceAll("&quot;", "#quot;");
		src = src.replaceAll("&apos;", "#apos;");

		src = src.replaceAll("&", "&amp;");
		src = src.replaceAll(">", "&gt;");
		src = src.replaceAll("<", "&lt;");
		src = src.replaceAll("\"", "&quot;");
		src = src.replaceAll("'", "&apos;");

		src = src.replaceAll("#gt;", "&gt;");
		src = src.replaceAll("#lt;", "&lt;");
		src = src.replaceAll("#amp;", "&amp;");
		src = src.replaceAll("#quot;", "&quot;");
		src = src.replaceAll("#apos;", "&apos;");

		return src;
	}// xml支持 end

	
	/**
	 * 全角
	 * @param input
	 * @return
	 */
	public static String toQuanJiao(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] < 127)
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

	/**
	 * 转半角
	 * 全角空格为12288，半角空格为32
	 * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
	 * 
	 * @param input
	 * @return
	 */
	public static String toBanJiao(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375){
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}
	
	

	/**
	 * 将字符串编码成 Unicode 。
	 * 
	 * @param theString  待转换成Unicode编码的字符串。
	 * @return 返回转换后Unicode编码的字符串。
	 */
	public static String toUnicode(String theString) {
		int len = theString.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuffer outBuffer = new StringBuffer(bufLen);

		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			// Handle common case first, selecting largest block that
			// avoids the specials below
			if ((aChar > 61) && (aChar < 127)) {
				if (aChar == '\\') {
					outBuffer.append('\\');
					outBuffer.append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch (aChar) {
			case ' ':
				outBuffer.append('\\');
				outBuffer.append(' ');
				break;
			case '\t':
				outBuffer.append('\\');
				outBuffer.append('t');
				break;
			case '\n':
				outBuffer.append('\\');
				outBuffer.append('n');
				break;
			case '\r':
				outBuffer.append('\\');
				outBuffer.append('r');
				break;
			case '\f':
				outBuffer.append('\\');
				outBuffer.append('f');
				break;
			case '=': // Fall through
			case ':': // Fall through
			case '#': // Fall through
			case '!':
				outBuffer.append('\\');
				outBuffer.append(aChar);
				break;
			default:
				if ((aChar < 0x0020) || (aChar > 0x007e)) {
					outBuffer.append('\\');
					outBuffer.append('u');
					outBuffer.append(toHex((aChar >> 12) & 0xF));
					outBuffer.append(toHex((aChar >> 8) & 0xF));
					outBuffer.append(toHex((aChar >> 4) & 0xF));
					outBuffer.append(toHex(aChar & 0xF));
				} else {
					outBuffer.append(aChar);
				}
			}
		}
		return outBuffer.toString();
	}

	/**
	 * 解析unicode字符串
	 * @param words
	 * @return
	 */
	public static String decodeUnicode(String words){
		if(words==null||words.equals("")) return words;
		
		StringBuffer outBuffer = new StringBuffer();
		
		int i=0;
		for(;i<words.length()-1;){
			char c=words.charAt(i);
			if(c=='\\'){
				char cNext=words.charAt(i+1);
				switch (cNext) {
					case ' ':
						outBuffer.append(' ');
						i+=2;
						break;
					case 't':
						outBuffer.append('\t');
						i+=2;
						break;
					case 'n':
						outBuffer.append('\n');
						i+=2;
						break;
					case 'r':
						outBuffer.append('\r');
						i+=2;
						break;
					case 'f':
						outBuffer.append('\f');
						i+=2;
						break;
					case '=': // Fall through
					case ':': // Fall through
					case '#': // Fall through
					case '!':
						outBuffer.append(cNext);
						i+=2;
						break;
					case 'u':
						if(i+5<words.length()){
							String s=words.substring(i+2,i+6);
							if(JUtilMath.isIntHex(s)){
								outBuffer.append((char)Integer.parseInt(s, 16));
								i+=6;
							}else{
								outBuffer.append(cNext);
								i+=2;
							}
						}else{
							outBuffer.append(cNext);
							i+=2;
						}
						break;
					default:
						outBuffer.append(c);
						i+=1;
				}
			}else{
				outBuffer.append(c);
				i++;
			}
		}
		
		if(i==words.length()-1){
			outBuffer.append(words.charAt(words.length()-1));
		}
		return outBuffer.toString();
	}
	

	/**
	 * 
	 * @param nibble
	 * @return
	 */
	private static char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
	}
	

	
	/**
	 * 
	 * @param url
	 * @param encoding
	 * @return
	 */
	public static String encodeURI(String url,String encoding){
		try{
			if("".equals(url)||url==null) return url;
			
			String en=URLEncoder.encode(url,encoding);
			en=en.replaceAll("\\+","%20");
			return en;
		}catch(Exception e){
			e.printStackTrace();
			return url;
		}
	}
	
	/**
	 * 
	 * @param url
	 * @param encoding
	 * @return
	 */
	public static String decodeURI(String url,String encoding){
		try{
			return URLDecoder.decode(url,encoding);
		}catch(Exception e){
			e.printStackTrace();
			return url;
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static String decodeHtmlCharCodeSequence(String value){
		if(value==null||value.equals("")) return value;
		int start=value.indexOf("&#");
		int end=value.indexOf(";",start);
		while(start>-1&&end>start+2){
			String original=value.substring(start,end+1);
			String truth=value.substring(start+2,end);

			if(truth.startsWith("x")){
				value=JUtilString.replaceAll(value,original,((char)Integer.parseInt(truth.substring(1),16))+"");
			}else{
				value=JUtilString.replaceAll(value,original,((char)Integer.parseInt(truth))+"");
			}
			
			start=value.indexOf("&#");
			end=value.indexOf(";",start);
		}
		return value;
	}
	
	/**
	 * 删除html标记
	 * @param src
	 * @param tagName
	 * @return
	 */
	public static String delHtmlTags(String src,String tagName){
		src=src.replaceAll("<"+tagName+"[^<]*>","");
		src=src.replaceAll("</"+tagName+">","");
		
		return src;
	}
	
	/**
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isIP(String ip){
		if(ip==null||!ip.matches("^\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}$")) return false;
		String[] ipCells=JUtilString.getTokens(ip,".");
		if(Integer.parseInt(ipCells[0])<0
				||Integer.parseInt(ipCells[0])>255
				||Integer.parseInt(ipCells[1])<0
				||Integer.parseInt(ipCells[1])>255
				||Integer.parseInt(ipCells[2])<0
				||Integer.parseInt(ipCells[2])>255
				||Integer.parseInt(ipCells[3])<0
				||Integer.parseInt(ipCells[3])>255){
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isDate(String s){
		if(s==null||!s.matches("^\\d{4}-\\d{2}-\\d{2}$")) return false;
		String[] cells=JUtilString.getTokens(s,"-");
		if(Integer.parseInt(cells[1])<1
				||Integer.parseInt(cells[1])>12
				||Integer.parseInt(cells[2])<1
				||Integer.parseInt(cells[2])>31){
			return false;
		}
		try{
			Timestamp.valueOf(s+" 00:00:00");
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isIdcard(String s){
		if(s==null||!s.matches(JUtilString.RegExpIdcard)) return false;
		
		String birth=s.substring(6,10)+"-"+s.substring(10,12)+"-"+s.substring(12,14);
		return isDate(birth);
	}
	
	/**
	 * 
	 * @param s
	 * @param maxLength
	 * @return
	 */
	public static boolean isEmail(String s,int maxLength){
		if(s==null||!s.matches(JUtilString.RegExpEmail)) return false;
		
		if(s.length()!=bytes(s,"UTF-8")||s.length()>maxLength) return false;
		
		return true;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isMobilePhone(String s){
		return (s!=null&&s.matches(JUtilString.RegExpMobilePhone));
	}
	
	public static boolean isMobilePhoneWorldwide(String s){
		return (s!=null&&s.matches(JUtilString.RegExpMobilePhoneWorldwide));
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isPersonNameCn(String name){
		if(name==null
				||!name.matches(RegExpNameCn)
				||(name.indexOf(".")<0&&name.length()>6)
				||(name.indexOf(".")>0&&name.length()>15)){//不符合规则
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param strings
	 * @return
	 */
	public static String combine(String[] strings){
		if(strings==null) return null;
		else{
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<strings.length;i++){
				sb.append(strings[i]);
			}
			return sb.toString();
		}
	}
	
	/**
	 * 将第一个字符大写
	 * @param str
	 * @return
	 */
	public static String upperFirstChar(String str){
		StringBuffer sb = new StringBuffer(str);
		if (((int) 'a' <= (int) sb.charAt(0))
				&& ((int) sb.charAt(0) <= (int) 'z')){
			sb.setCharAt(0, (char) ((int) sb.charAt(0) - 32));
		}

		return sb.toString();
	}
	
	/**
	 * 
	 * @param file
	 */
	private static void replace(File file){
		//未指定支付订单不存在或状态无效
		if(file.isDirectory()){
			File[] files=file.listFiles();
			for(int i=0;i<files.length;i++) replace(files[i]);
		}else if(file.getName().endsWith(".jsp")
				||file.getName().endsWith(".xml")
				||file.getName().endsWith(".js")
				||file.getName().endsWith(".htm")
				||file.getName().endsWith(".java")
				||file.getName().endsWith(".html")){
			boolean changed=false;
			String s=JDFSFile.read(file, "UTF-8");
			
			if(s.indexOf("vselected.com")>0){
				s=JUtilString.replaceAll(s, "vselected.com", "payace.cn");
				changed=true;
			}
			
			if(s.indexOf("gugumall.cn")>0){
				s=JUtilString.replaceAll(s, "gugumall.cn", "payace.cn");
				changed=true;
			}
			
			if(s.indexOf("xiaoxiaoxia.cn")>0){
				s=JUtilString.replaceAll(s, "xiaoxiaoxia.cn", "payace.cn");
				changed=true;
			}
			
			if(s.indexOf("gugupay.cn")>0){
				s=JUtilString.replaceAll(s, "gugupay.cn", "payace.cn");
				changed=true;
			}
			
			if(s.indexOf("/webapps/jshop")>0){
				s=JUtilString.replaceAll(s, "/webapps/jshop", "/webapps/pay");
				changed=true;
			}
			
			if(s.indexOf("古古")>0){
				s=JUtilString.replaceAll(s, "古古", "王牌");
				changed=true;
			}
			
			if(s.indexOf("GuGu")>0){
				s=JUtilString.replaceAll(s, "GuGu", "payace");
				changed=true;
			}
			
			if(s.indexOf("GUGU")>0){
				s=JUtilString.replaceAll(s, "GUGU", "payace");
				changed=true;
			}
			
			if(s.indexOf("肖炯")>0){
				s=JUtilString.replaceAll(s, "肖炯", "payace");
				changed=true;
			}
			
			if(s.indexOf("15730109974")>0){
				s=JUtilString.replaceAll(s, "15730109974", "11111111");
				changed=true;
			}
			
			if(s.indexOf("157-3010-9974")>0){
				s=JUtilString.replaceAll(s, "157-3010-9974", "11111111");
				changed=true;
			}
			
			if(s.indexOf("023-65420372")>0){
				s=JUtilString.replaceAll(s, "023-65420372", "11111111");
				changed=true;
			}
			
			if(s.indexOf("023-6542-0372")>0){
				s=JUtilString.replaceAll(s, "023-6542-0372", "11111111");
				changed=true;
			}
			
			if(changed){
				JDFSFile.saveString(file.getAbsolutePath(), s, false, "UTF-8");
				System.out.println(file.getAbsolutePath());
			}
		}
	}
	
	/**
	* 所在时区 标准时区：-8 时区 UTC/GMT -8 个小时      
	* 夏时制：+1 个小时
	* 当地时区相当于：-7 时区 UTC/GMT -7 个小时  
	* 夏令时于当地标准时间 星期日, 13 三月 2016, 02:00 开始
	* 夏令时于当地标准时间 星期日, 06 十一月 2016, 02:00 结束 
	*/
	public static long getTimeCa(){
		long cn=System.currentTimeMillis();
		long ca=cn-3600000L*16;
		
		String year=(new Timestamp(ca)).toString().substring(0,4);
		
		long summerStart=Timestamp.valueOf(year+"-03-01 02:00:00").getTime();
		if(JUtilTimestamp.getValue(summerStart, Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			summerStart+=3600000L*24*7;
		}else{
			summerStart+=3600000L*24*(7+Calendar.SATURDAY-JUtilTimestamp.getValue(summerStart, Calendar.DAY_OF_WEEK)+1);
		}
		
		long summerEnd=Timestamp.valueOf(year+"-11-01 02:00:00").getTime();
		if(JUtilTimestamp.getValue(summerEnd, Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
			summerEnd+=3600000L*24*(Calendar.SATURDAY-JUtilTimestamp.getValue(summerEnd, Calendar.DAY_OF_WEEK)+1);
		}
		
		if(ca>summerStart&&ca<summerEnd) {//summer time
			ca+=3600000L;
		}
		
		return ca;
	}
	
	/**
	 * 
	 * @param _ca
	 * @return
	 */
	public static long caTime2Cn(long _ca){
		long cn=System.currentTimeMillis();
		long ca=cn-3600000L*16;
		
		String year=(new Timestamp(ca)).toString().substring(0,4);
		
		cn=_ca+3600000L*16;
		
		long summerStart=Timestamp.valueOf(year+"-03-01 02:00:00").getTime();
		if(JUtilTimestamp.getValue(summerStart, Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			summerStart+=3600000L*24*7;
		}else{
			summerStart+=3600000L*24*(7+Calendar.SATURDAY-Calendar.SUNDAY);
		}
		
		long summerEnd=Timestamp.valueOf(year+"-11-01 02:00:00").getTime();
		if(JUtilTimestamp.getValue(summerEnd, Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
			summerEnd+=3600000L*24*(Calendar.SATURDAY-Calendar.SUNDAY);
		}
		
		if(ca>summerStart&&ca<summerEnd) {//summer time
			cn-=3600000L;
		}
		
		return cn;
	}
	
	/**
	 * 
	 * @param objects
	 * @return
	 */
	public static String toString(Object[] objects){
		if(objects==null||objects.length==0) return "";
		
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<objects.length;i++){
			if(objects[i]==null) sb.append("null,");
			else sb.append(objects[i].toString()+",");
		}
	
		if(sb.length()>0) sb.deleteCharAt(sb.length()-1);
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param objects
	 * @return
	 */
	public static String toString(Object[] objects,String objectStartFlag,String objectEndFlag){
		if(objects==null||objects.length==0) return "";
		
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<objects.length;i++){
			if(objects[i]==null) sb.append(objectStartFlag+"null"+objectEndFlag+",");
			else sb.append(objectStartFlag+objects[i].toString()+objectEndFlag+",");
		}
	
		if(sb.length()>0) sb.deleteCharAt(sb.length()-1);
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
//		String s=JDFSFile.read(new File("C:\\Users\\one\\Desktop\\aaa.js"),"UTF-8");
//		JDFSFile.saveString("C:\\Users\\one\\Desktop\\aaax.js",JUtilString.decodeUnicode(s),false,"UTF-8");
//		StringBuffer sb=new StringBuffer();
//		for(int i=1;i<=100;i++){
//			String id="";
//			if(i<10) id="00"+i;
//			else if(i<100) id="0"+i;
//			else id=""+i;
//			sb.append("\r\nINSERT INTO w_user (U_ID,U_NAME,U_NICK,U_PWD,U_PWD_DE,U_EMAIL,U_REG_IP,U_REG_TIME,U_LOGIN_IP,U_LOGIN_TIME,U_TYPE,U_STAT,DRAW_PWD) VALUES ('fabiao"+id+"','哈哈','哈哈','1(-.0*','cvskpsaasffsxcsdx','someone@some.com','127.0.0.1','2008-08-08 08:08:08','127.0.0.1','2008-08-08 08:08:08','1','1','110110');");
//			sb.append("\r\nINSERT INTO w_role_of_user(USER_ROLE_UUID,U_ID,ROLE_CODE,START_TIME) VALUES ('fabiao"+id+"_1','fabiao"+id+"','ROLE_USER','2008-01-01 01:01:01');");
//			sb.append("\r\nINSERT INTO w_role_of_user(USER_ROLE_UUID,U_ID,ROLE_CODE,START_TIME) VALUES ('fabiao"+id+"_2','fabiao"+id+"','SELF_USER','2008-01-01 01:01:01');");
//			sb.append("\r\nINSERT INTO w_bill VALUES ('fabiao"+id+"',0,0,0,0,0,0,0,0,0,1,'');");
//		}
//		
//		sb.append("\r\n");
//		
//		for(int i=1;i<=100;i++){
//			String id="";
//			if(i<10) id="00"+i;
//			else if(i<100) id="0"+i;
//			else id=""+i;
//			sb.append("\r\nINSERT INTO w_user (U_ID,U_NAME,U_NICK,U_PWD,U_PWD_DE,U_EMAIL,U_REG_IP,U_REG_TIME,U_LOGIN_IP,U_LOGIN_TIME,U_TYPE,U_STAT,DRAW_PWD) VALUES ('putong"+id+"','哈哈','哈哈','1(-.0*','cvskpsaasffsxcsdx','someone@some.com','127.0.0.1','2008-08-08 08:08:08','127.0.0.1','2008-08-08 08:08:08','1','1','110110');");
//			sb.append("\r\nINSERT INTO w_role_of_user(USER_ROLE_UUID,U_ID,ROLE_CODE,START_TIME) VALUES ('putong"+id+"_1','putong"+id+"','ROLE_USER','2008-01-01 01:01:01');");
//			sb.append("\r\nINSERT INTO w_role_of_user(USER_ROLE_UUID,U_ID,ROLE_CODE,START_TIME) VALUES ('putong"+id+"_2','putong"+id+"','SELF_USER','2008-01-01 01:01:01');");
//			sb.append("\r\nINSERT INTO w_bill VALUES ('putong"+id+"',0,0,0,0,0,0,0,0,0,1,'');");
//		}
//		
//		sb.append("\r\n");
//		
//		for(int i=1;i<=100;i++){
//			String id="";
//			if(i<10) id="00"+i;
//			else if(i<100) id="0"+i;
//			else id=""+i;
//			sb.append("\r\nINSERT INTO w_user (U_ID,U_NAME,U_NICK,U_PWD,U_PWD_DE,U_EMAIL,U_REG_IP,U_REG_TIME,U_LOGIN_IP,U_LOGIN_TIME,U_TYPE,U_STAT,DRAW_PWD) VALUES ('zhuajia"+id+"','哈哈','哈哈','1(-.0*','cvskpsaasffsxcsdx','someone@some.com','127.0.0.1','2008-08-08 08:08:08','127.0.0.1','2008-08-08 08:08:08','1','1','110110');");
//			sb.append("\r\nINSERT INTO w_role_of_user(USER_ROLE_UUID,U_ID,ROLE_CODE,START_TIME) VALUES ('zhuajia"+id+"_1','putong"+id+"','ROLE_USER','2008-01-01 01:01:01');");
//			sb.append("\r\nINSERT INTO w_role_of_user(USER_ROLE_UUID,U_ID,ROLE_CODE,START_TIME) VALUES ('zhuajia"+id+"_2','putong"+id+"','SELF_USER','2008-01-01 01:01:01');");
//			sb.append("\r\nINSERT INTO w_bill VALUES ('zhuajia"+id+"',0,0,0,0,0,0,0,0,0,1,'');");
//		}
//		
//		JDFSFile.saveString("F://temp//temp.sql",sb.toString(),false,"UTF-8");
		System.out.println("111\\\"");
		
		String src=JDFSFile.read(new File("f:/temp/s.txt"),"UTF-8");
		src=JUtilString.replaceAll(src,"\\\"","\"");
		src=JUtilString.decodeUnicode(src);
		System.out.println(src);
	}
}