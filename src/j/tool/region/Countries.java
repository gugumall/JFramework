package j.tool.region;

import java.util.HashMap;
import java.util.Map;

import j.http.JHttp;
import j.http.JHttpContext;
import j.sys.AppConfig;
import j.util.ConcurrentList;

public class Countries {
	private static ConcurrentList countries=new ConcurrentList();
	public static final String DEFAULT_MOBILE_CODE="86";
	public static final String DEFAULT_COUNTRY_CODE="CN";
	public static final String TEL_RE="^\\d{3,6}\\-?\\d{5,10}\\-?\\d{0,6}$";
	
	static{
		countries.add(new CountryData("CN","86","中国大陆","China","中国","^(86){0,1}\\-?1[1,2,3,4,5,6,7,8,9]\\d{9}$"));
		countries.add(new CountryData("HK","852","香港","Hong Kong","中国","^(852){1}\\-?0{0,1}[1,5,6,9](?:\\d{7}|\\d{8}|\\d{12})$"));
		countries.add(new CountryData("MO","853","澳门","Macau","中国","^(853){1}\\-?6\\d{7}$"));
		countries.add(new CountryData("TW","886","台湾","Taiwan","中国","^(886){1}\\-?0{0,1}[6,7,9](?:\\d{7}|\\d{8}|\\d{10})$"));
		countries.add(new CountryData("KH","855","柬埔寨","Cambodia","亚洲","^(855){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("IN","91","印度","India","亚洲","^(91){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("ID","62","印度尼西亚","Indonesia","亚洲","^(62){1}\\-?[2-9]\\d{7,11}$"));
		countries.add(new CountryData("IL","972","以色列","Israel","亚洲","^(972){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("JP","81","日本","Japan","亚洲","^(81){1}\\-?0{0,1}[7,8,9](?:\\d{8}|\\d{9})$"));
		countries.add(new CountryData("JO","962","约旦","Jordan","亚洲","^(962){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("KG","996","吉尔吉斯斯坦","Kyrgyzstan","亚洲","^(996){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("MY","60","马来西亚","Malaysia","亚洲","^(60){1}\\-?1\\d{8,9}$"));
		countries.add(new CountryData("MV","960","马尔代夫","Maldives","亚洲","^(960){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("MN","976","蒙古","Mongolia","亚洲","^(976){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("PH","63","菲律宾","Philippines","亚洲","^(63){1}\\-?[24579](\\d{7,9}|\\d{12})$"));
		countries.add(new CountryData("QA","974","卡塔尔","Qatar","亚洲","^(974){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("SA","966","沙特阿拉伯","Saudi Arabia","亚洲","^(966){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("SG","65","新加坡","Singapore","亚洲","^(65){1}\\-?[13689]\\d{6,7}$"));
		countries.add(new CountryData("KR","82","韩国","South Korea","亚洲","^(82){1}\\-?0{0,1}[7,1](?:\\d{8}|\\d{9})$"));
		countries.add(new CountryData("LK","94","斯里兰卡","Sri Lanka","亚洲","^(94){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("TR","90","土耳其","Turkey","亚洲","^(90){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("TH","66","泰国","Thailand","亚洲","^(66){1}\\-?[13456789]\\d{7,8}$"));
		countries.add(new CountryData("AE","971","阿联酋","United Arab Emirates","亚洲","^(971){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("VN","84","越南","Vietnam","亚洲","^(84){1}\\-?[1-9]\\d{6,9}$"));
		countries.add(new CountryData("AT","43","奥地利","Austria","欧洲","^(43){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("BY","375","白俄罗斯","Belarus","欧洲","^(375){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("BE","32","比利时","Belgium","欧洲","^(32){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("BG","359","保加利亚","Bulgaria","欧洲","^(359){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("DK","45","丹麦","Denmark","欧洲","^(45){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("EE","372","爱沙尼亚","Estonia","欧洲","^(372){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("FI","358","芬兰","Finland","欧洲","^(358){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("FR","33","法国","France","欧洲","^(33){1}\\-?[1678](\\d{5}|\\d{7,8})$"));
		countries.add(new CountryData("DE","49","德国","Germany","欧洲","^(49){1}\\-?1(\\d{5,6}|\\d{9,12})$"));
		countries.add(new CountryData("GR","30","希腊","Greece","欧洲","^(30){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("HU","36","匈牙利","Hungary","欧洲","^(36){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("IE","353","爱尔兰","Ireland","欧洲","^(353){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("IT","39","意大利","Italy","欧洲","^(39){1}\\-?[37]\\d{8,11}$"));
		countries.add(new CountryData("LT","370","立陶宛","Lithuania","欧洲","^(370){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("LU","352","卢森堡","Luxembourg","欧洲","^(352){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("NL","31","荷兰","Netherlands","欧洲","^(31){1}\\-?6\\d{8}$"));
		countries.add(new CountryData("NO","47","挪威","Norway","欧洲","^(47){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("PL","48","波兰","Poland","欧洲","^(48){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("PT","351","葡萄牙","Portugal","欧洲","^(351){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("RO","40","罗马尼亚","Romania","欧洲","^(40){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("RU","7","俄罗斯","Russia","欧洲","^(7){1}\\-?[13489]\\d{9,11}$"));
		countries.add(new CountryData("RS","381","塞尔维亚","Serbia","欧洲","^(381){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("ES","34","西班牙","Spain","欧洲","^(34){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("SE","46","瑞典","Sweden","欧洲","^(46){1}\\-?[124-7](\\d{8}|\\d{10}|\\d{12})$"));
		countries.add(new CountryData("CH","41","瑞士","Switzerland","欧洲","^(41){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("UA","380","乌克兰","Ukraine","欧洲","^(380){1}\\-?[3-79]\\d{8,9}$"));
		countries.add(new CountryData("GB","44","英国","United Kingdom","欧洲","^(44){1}\\-?[347-9](\\d{8,9}|\\d{11,12})$"));
		countries.add(new CountryData("AR","54","阿根廷","Argentina","美洲","^(54){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("BS","1242","巴哈马","Bahamas","美洲","^(1242){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("BZ","501","伯利兹","Belize","美洲","^(501){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("BR","55","巴西","Brazil","美洲","^(55){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("CA","1","加拿大","Canada","美洲","^(1){1}\\-?\\d{10}$"));
		countries.add(new CountryData("CL","56","智利","Chile","美洲","^(56){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("CO","57","哥伦比亚","Colombia","美洲","^(57){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("MX","52","墨西哥","Mexico","美洲","^(52){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("PA","507","巴拿马","Panama","美洲","^(507){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("PE","51","秘鲁","Peru","美洲","^(51){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("US","1","美国","United States","美洲","^(1){1}\\-?\\d{10,12}$"));
		countries.add(new CountryData("VE","58","委内瑞拉","Venezuela","美洲","^(58){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("VG","1284","英属维尔京群岛","Virgin Islands, British","美洲","^(1284){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("EG","20","埃及","Egypt","非洲","^(20){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("MA","212","摩洛哥","Morocco","非洲","^(212){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("NG","234","尼日利亚","Nigeria","非洲","^(234){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("SC","248","塞舌尔","Seychelles","非洲","^(248){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("ZA","27","南非","South Africa","非洲","^(27){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("TN","216","突尼斯","Tunisia","非洲","^(216){1}\\-?\\d{7,11}"));
		countries.add(new CountryData("AU","61","澳大利亚","Australia","大洋洲","^(61){1}\\-?4\\d{8,9}$"));
		countries.add(new CountryData("NZ","64","新西兰","New Zealand","大洋洲","^(64){1}\\-?[278]\\d{7,9}$"));
	}
	
	/**
	 * 
	 * @param num
	 * @return
	 */
	public static String[] getMobileOrTelInfo(String num){
		if(num==null||!num.startsWith("+")){
			return new String[]{"",num};
		}
		
		if(num.indexOf("-")>1){
			return new String[]{num.substring(1,num.indexOf("-")),num.substring(num.indexOf("-")+1)};
		}
		
		return new String[]{"",num};
	}
	
	/**
	 * 
	 * @param countryCode
	 * @return
	 */
	public static CountryData getCountry(String countryCode){
		for(int i=0;i<countries.size();i++){
			CountryData c=(CountryData)countries.get(i);
			if(c.code.equals(countryCode)
					||c.mobileCode.equals(countryCode)) return c;
		}
		return null;
	}
	
	/**
	 * 
	 * @param countryCode
	 * @param num
	 * @return
	 */
	public static boolean isMobileValid(String num){
		if(num==null||"".equals(num)) return false;
		
		String[] temp=getMobileOrTelInfo(num);
		
		CountryData c=getCountry(temp[0]);
		if(c==null) return false;
		
		return (temp[0]+"-"+temp[1]).matches(c.RE);
	}
	
	/**
	 * 
	 * @param countryCode
	 * @param num
	 * @return
	 */
	public static boolean isTelValid(String num){
		if(num==null||"".equals(num)) return false;
		
		String[] temp=getMobileOrTelInfo(num);

		CountryData c=getCountry(temp[0]);
		if(c==null) return false;
		return temp[1].matches(TEL_RE);
	}
		
	/**
	 * 
	 * @param countryCode
	 * @param num
	 * @return
	 */
	public static boolean isMobileValid(String mobileCode,String num){
		if(mobileCode==null||"".equals(mobileCode)) return false;
		if(num==null||"".equals(num)) return false;
		
		String[] temp=getMobileOrTelInfo(num);
		
		if(!"".equals(temp[0])&&!temp[0].equals(mobileCode)) return false;
		
		CountryData c=getCountry(mobileCode);
		if(c==null) return false;
		
		return (temp[0]+"-"+temp[1]).matches(c.RE);
	}
	
	/**
	 * 
	 * @param countryCode
	 * @param num
	 * @return
	 */
	public static boolean isTelValid(String mobileCode,String num){
		if(mobileCode==null||"".equals(mobileCode)) return false;
		if(num==null||"".equals(num)) return false;
		
		String[] temp=getMobileOrTelInfo(num);
		
		if(!"".equals(temp[0])&&!temp[0].equals(mobileCode)) return false;

		CountryData c=getCountry(mobileCode);
		if(c==null) return false;
		
		return temp[1].matches(TEL_RE);
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		System.out.println(isMobileValid("+86-15730109974"));
		
		//^\\d{3,6}\\-?\\d{5,10}\\-?\\d{0,6}$
		System.out.println(isTelValid("+86-023-65420372"));
		System.out.println("+8226-15730109974".matches("^\\+?(\\d{2,4}\\-?)?\\d{4,16}$"));
		
//
//		JHttp http=JHttp.getInstance();
//		JHttpContext context=new JHttpContext();
//		context.setRequestEncoding("UTF-8");
//		
//		String to="18620005501";
//		
//		String[] dest=Countries.getMobileOrTelInfo(to);
//		
//		Map params=new HashMap();
//		params.put("account", "lvjingling85");
//		params.put("password", "000111");
//		params.put("mobile", to);
//		params.put("content", "【PAYACE】您的验证码是888456");
//		params.put("sendTime", "");
//		params.put("AddSign", "Y");
//		params.put("action", "send");
//		
//		String response=http.postResponse(context, null, "http://www.smswst.com/api/httpapi.aspx", params, "UTF-8");
//
//		System.out.println(response);
	}
}
