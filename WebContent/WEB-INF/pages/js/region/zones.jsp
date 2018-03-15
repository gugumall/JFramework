<%@ page contentType="text/html; charset=utf-8" %><%@ page import="java.util.*"%><%@ page import="j.db.*"%><%@ page import="j.tool.region.*"%><%
String lang=j.I18N.I18N.getCurrentLanguage(session);
String countyId=request.getParameter("county_id");
List zs=Region.getZones(countyId);
if(zs.size()>0){
	String zsJs="zones=new Array();\r\n";
	for(int x=0;x<zs.size();x++){
		Jzone z=(Jzone)zs.get(x);
		if("zh-cn".equals(lang)) zsJs+="zones.push(new Array('"+z.getZoneId()+"','"+z.getZoneName()+"'));\r\n";
		else if("zh-tw".equals(lang)) zsJs+="zones.push(new Array('"+z.getZoneId()+"','"+z.getZoneNameTw()+"'));\r\n";
		else zsJs+="zones.push(new Array('"+z.getZoneId()+"','"+z.getZoneNameEn()+"'));\r\n";
	}
out.print(zsJs);
}%>