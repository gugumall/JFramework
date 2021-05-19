package j.http;

import j.common.JObject;
import j.util.JUtilJSON;
import j.util.JUtilMath;

/**
 * 
 * @author 恭喜发财
 *
 * 2019年7月10日
 *
 * <b>功能描述</b> 一注投注
 */
public class Betting extends JObject{
	private static final long serialVersionUID = 1L;

	public String uuid;
	public String batchId;
	public String agParent0;
	public String agParent1;
	public String defineSn;
	public String typeSn;
	public String typeCode;
	public String groupCode;
	public String selectUuid;
	public double betMoney=0d;//单注金额
	public double betOdds=0d;//投注赔率
	public String betOddKind;
	public String sContent;//投注内容
	public String gameSn;
	
	@Override
	public String toString() {
		StringBuffer s=new StringBuffer();
		
		s.append("{\"ag0\":\""+agParent0+"\"");
		s.append(",\"ag1\":\""+agParent1+"\"");
		//s.append(",\"defineSn\":\""+defineSn+"\"");
		s.append(",\"game\":\""+typeSn+"\"");
		s.append(",\"type\":\""+typeCode+"\"");
		//s.append(",\"groupCode\":\""+groupCode+"\"");
		s.append(",\"batchId\":\""+batchId+"\"");
		s.append(",\"uuid\":\""+uuid+"\"");
		s.append(",\"selectUuid\":\""+selectUuid+"\"");
		s.append(",\"money\":\""+JUtilMath.formatPrintWithoutZero(betMoney, 2)+"\"");
		s.append(",\"odds\":\""+JUtilMath.formatPrintWithoutZero(betOdds, 4)+"\"");
		s.append(",\"betOddKind\":\""+betOddKind+"\"");
		s.append(",\"sContent\":\""+(sContent==null?"":JUtilJSON.convert(sContent))+"\"");
		s.append(",\"gameSn\":\""+gameSn+"\"");		
		s.append("}");
		
		return s.toString();
	}
}
