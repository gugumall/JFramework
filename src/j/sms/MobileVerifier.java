package j.sms;

/**
 * 
 * @author 肖炯
 *
 */
public class MobileVerifier {
	protected static MobileVerifierRule[] rules;
	
	/**
	 * 如果是有效号码，返回该号码所在地区
	 * @param mobile
	 * @return
	 */
	public static String valid(String mobile){
		if(mobile==null||"".equals(mobile)) return null;
		if(rules==null) return null;
		for(int i=0;i<rules.length;i++){
			if(mobile.matches(rules[i].rule)){
				return rules[i].region;
			}
		}
		return null;
	}
}

/**
 * 
 * @author 肖炯
 *
 */
class MobileVerifierRule{
	protected String region;
	protected String rule;
	
	protected MobileVerifierRule(String region,String rule){
		this.region=region;
		this.rule=rule;
	}
}