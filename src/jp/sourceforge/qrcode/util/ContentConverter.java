package jp.sourceforge.qrcode.util;

public class ContentConverter {
	
	static char n = '\n';

 	public static String convert(String targetString) {
 		if (targetString == null)
 			return targetString;
		if(targetString.indexOf("MEBKM:") > -1)
			targetString = convertDocomoBookmark(targetString);
		if(targetString.indexOf("MECARD:") > -1)
			targetString = convertDocomoAddressBook(targetString);
		if(targetString.indexOf("MATMSG:") > -1)
			targetString = convertDocomoMailto(targetString);
		if(targetString.indexOf("http\\://") > -1)
			targetString = replaceString(targetString, "http\\://", "\nhttp://");
		return targetString;
 	}

	 private static String convertDocomoBookmark(String targetString)
	 {
	 	targetString = removeString(targetString, "MEBKM:");
	 	targetString = removeString(targetString, "TITLE:");
	 	targetString = removeString(targetString, ";");
	 	targetString = removeString(targetString, "URL:");
	     return targetString;
	 }

	 private static String convertDocomoAddressBook(String targetString)
	 {

	 	targetString = removeString(targetString, "MECARD:");
	 	targetString = removeString(targetString, ";");
	 	targetString = replaceString(targetString, "N:", "NAME1:");
	 	targetString = replaceString(targetString, "SOUND:", n + "NAME2:");
	 	targetString = replaceString(targetString, "TEL:", n + "TEL1:");
	 	targetString = replaceString(targetString, "EMAIL:", n + "MAIL1:");
	 	targetString = targetString + n;
	     return targetString;
	 }

	 private static String convertDocomoMailto(String s)
	 {
	     String s1 = s;
	     char c = '\n';
	     s1 = removeString(s1, "MATMSG:");
	     s1 = removeString(s1, ";");
	     s1 = replaceString(s1, "TO:", "MAILTO:");
	     s1 = replaceString(s1, "SUB:", c + "SUBJECT:");
	     s1 = replaceString(s1, "BODY:", c + "BODY:");
	     s1 = s1 + c;
	     return s1;
	 }

	 private static String replaceString(String s, String s1, String s2)
	 {
	     String s3 = s;
	     for(int i = s3.indexOf(s1, 0); i > -1; i = s3.indexOf(s1, i + s2.length()))
	         s3 = s3.substring(0, i) + s2 + s3.substring(i + s1.length());

	     return s3;
	 }

	 private static String removeString(String s, String s1)
	 {
	     return replaceString(s, s1, "");
	 }



}
