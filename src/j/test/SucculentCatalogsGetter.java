package j.test;

import j.util.JUtilString;

import java.io.File;


public class SucculentCatalogsGetter{
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		File dir=new File("F:\\gugu\\products\\多肉");
		String parentId="46";
		
		int index=0;
		File[] catsLevel1=dir.listFiles();
		for(int i=0;i<catsLevel1.length;i++){
			if(!catsLevel1[i].isDirectory()) continue;
			String cat1=catsLevel1[i].getName();
			cat1=JUtilString.replaceAll(cat1,"(荚)","");
			if(cat1.indexOf("（")>0) cat1=cat1.substring(0,cat1.indexOf("（"));
			if(cat1.indexOf("(")>0) cat1=cat1.substring(0,cat1.indexOf("("));
			
			index++;
			System.out.println(index+":"+cat1);
			
//			File[] catsLevel2=catsLevel1[i].listFiles();
//			for(int j=0;j<catsLevel2.length;j++){
//				if(!catsLevel2[j].isDirectory()) continue;
//				String cat2=catsLevel2[j].getName();
//				
//				index++;
//				System.out.println(index+":"+cat1+" > "+cat2);
//			}
		}
		
//		JHttp http=JHttp.getInstance();
//		HttpClient client=http.createClient();
//		
//		String dir="F:\\gugu\\products\\多肉\\";
//		
//		int pages=30;
//		for(int p=1;p<=pages;p++){
//			String url="http://www.mengsang.com/duorou/list_1_"+p+".html";
//			System.out.println(url);
//			
//			String txt=http.getResponse(null,client,url,"GBK");
//			
//			int start=txt.indexOf("<div class=\"tImgIcons center mt10 vm\"><a href=\"");
//			int end=txt.indexOf("\" class=\"title\">",start);
//			while(start>"<div class=\"tImgIcons center mt10 vm\"><a href=\"".length()
//					&&end>start){
//				String page=txt.substring(start+"<div class=\"tImgIcons center mt10 vm\"><a href=\"".length(),end);
//				System.out.println(page);
//				
//				String pageTxt=http.getResponse(null,client,page,"GBK");
//				pageTxt=JUtilString.replaceAll(pageTxt," target='_blank'","");
//				pageTxt=JUtilString.replaceAll(pageTxt,"<div class=\"mainBoxTitle\"><span class=\"mainBoxTitleCon\"><a href='http://www.mengsang.com/'>主页","<a href='http://www.mengsang.com/'>主页");
//	
//				int start2=pageTxt.indexOf("主页</a>");
//				int end2=pageTxt.indexOf("</div>",start2);
//				String _cats=pageTxt.substring(start2,end2);
//				_cats=JUtilString.delHtmlTags(_cats,"a");
//				_cats=JUtilString.delHtmlTags(_cats,"span");
//				
//				//主页 > 全部植物 > 多肉植物 > 百合科（LILIACEAE） > 十二卷属/瓦苇属 >
//				System.out.println(_cats);
//				
//				String[] cats=JUtilString.getTokens(_cats," > ");
//				
//				String cat1=cats[3];
//				cat1=JUtilString.replaceAll(cat1,"</u></a>","");
//				cat1=JUtilString.replaceAll(cat1,"/"," ");
//				
//				String cat2=cats[4];
//				cat2=JUtilString.replaceAll(cat2,"/"," ");
//				
//				start2=pageTxt.indexOf("<meta name=\"description\" content=\"")+"<meta name=\"description\" content=\"".length();
//				end2=pageTxt.indexOf("\"",start2);
//				String desc=pageTxt.substring(start2,end2);
//				if(desc.indexOf("简介 ")>0){
//					desc=desc.substring(desc.indexOf("简介 ")+3);
//				}
//				if(desc.indexOf("(本资料由")>0
//						&&desc.indexOf("添加）")>0){
//					String t1=desc.substring(0,desc.indexOf("(本资料由"));
//					String t2=desc.substring(desc.indexOf("添加）")+3);
//					desc=t1+t2;
//				}
//				
//				start2=pageTxt.indexOf("中文种名：</span>");
//				end2=pageTxt.indexOf("</div>",start2);
//				String name=start2<0?"":pageTxt.substring(start2+"中文种名：</span>".length(),end2);
//				if("".equals(name)){
//					start2=pageTxt.indexOf("<h2>");
//					end2=pageTxt.indexOf("</h2>");
//					name=start2<0?"":pageTxt.substring(start2+4,end2);
//					name=name.replaceAll("\r","");
//					name=name.replaceAll("\n","");
//					name=name.replaceAll("\t","");
//				}
//				if("".equals(name)){
//					start2=pageTxt.indexOf("<div class=\"mainBoxTitle\"><span class=\"mainBoxTitleCon\">");
//					end2=pageTxt.indexOf("</span>",start2);
//					name=start2<0?"":pageTxt.substring(start2+"<div class=\"mainBoxTitle\"><span class=\"mainBoxTitleCon\">".length(),end2);
//					name=name.replaceAll("\r","");
//					name=name.replaceAll("\n","");
//					name=name.replaceAll("\t","");
//				}
//				if("".equals(name)){
//					System.exit(0);
//				}
//				name=JUtilString.replaceAll(name,"/",",");
//				
//				start2=pageTxt.indexOf("英文学名：</span>");
//				end2=pageTxt.indexOf("</div>",start2);
//				String nameEn=start2<0?"":pageTxt.substring(start2+"英文学名：</span>".length(),end2);
//				
//				start2=pageTxt.indexOf("原产地:");
//				end2=pageTxt.indexOf("</td>",start2);
//				String src=start2<0?"":pageTxt.substring(start2+4,end2);
//
//				start2=pageTxt.indexOf("<b>繁殖");
//				start2=pageTxt.indexOf("<td>",start2);
//				end2=pageTxt.indexOf("</td>",start2);
//				String reproduce=start2<0?"":pageTxt.substring(start2+4,end2);
//				
//				start2=pageTxt.indexOf("<b>易活度");
//				start2=pageTxt.indexOf("<td>",start2);
//				end2=pageTxt.indexOf("</td>",start2);
//				String reproduceEasy=start2<0?"":pageTxt.substring(start2+4,end2);
//				reproduceEasy=JUtilString.replaceAll(reproduceEasy,"<img src=\"http://www.mengsang.com/templets/ms/ms2013/images/x","");
//				reproduceEasy=JUtilString.replaceAll(reproduceEasy,".png\"/>","");
//				
//				start2=pageTxt.indexOf("<b>季节");
//				start2=pageTxt.indexOf("<td>",start2);
//				end2=pageTxt.indexOf("</td>",start2);
//				String season=start2<0?"":pageTxt.substring(start2+4,end2);
//				
//				start2=pageTxt.indexOf("<b>温　度");
//				start2=pageTxt.indexOf("<td>",start2);
//				end2=pageTxt.indexOf("</td>",start2);
//				String temperature=start2<0?"":pageTxt.substring(start2+4,end2);
//				temperature=temperature.trim();
//				
//				start2=pageTxt.indexOf("<b>日照");
//				start2=pageTxt.indexOf("<td>",start2);
//				end2=pageTxt.indexOf("</td>",start2);
//				String sunshine=start2<0?"":pageTxt.substring(start2+4,end2);
//				sunshine=JUtilString.replaceAll(sunshine,"<img src=\"http://www.mengsang.com/templets/ms/ms2013/images/","");
//				sunshine=JUtilString.replaceAll(sunshine,".png\"/>","");
//				
//				start2=pageTxt.indexOf("<b>浇水量");
//				start2=pageTxt.indexOf("<td>",start2);
//				end2=pageTxt.indexOf("</td>",start2);
//				String water=start2<0?"":pageTxt.substring(start2+4,end2);
//				water=JUtilString.replaceAll(water,"<img src=\"http://www.mengsang.com/templets/ms/ms2013/images/s","");
//				water=JUtilString.replaceAll(water,".png\"/>","");
//				
//				start2=pageTxt.indexOf("<div class=\"imgCenter\">");
//				start2=pageTxt.indexOf("src=\"",start2);
//				end2=pageTxt.indexOf("\"",start2+5);
//				String img=pageTxt.substring(start2+5,end2);
//				if(!img.startsWith("http")) img="http://www.mengsang.com"+img;
//				System.out.println(img);
//				
//				String _dir=dir+cat1+"\\"+cat2+"\\";
//				File _dirFile=new File(_dir);
//				_dirFile.mkdirs();
//				
//				JDFSFile.saveStream(http.getStreamResponse(null,client,img),_dir+name+".jpg");
//				
//				String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
//				xml+="<root>\r\n";
//				xml+="\t<name><![CDATA["+name+"]]></name>\r\n";
//				xml+="\t<nameEn><![CDATA["+nameEn+"]]></nameEn>\r\n";
//				xml+="\t<src><![CDATA["+src+"]]></src>\r\n";
//				xml+="\t<desc><![CDATA["+desc+"]]></desc>\r\n";
//				xml+="\t<reproduce><![CDATA["+reproduce+"]]></reproduce>\r\n";
//				xml+="\t<reproduceEasy><![CDATA["+reproduceEasy+"]]></reproduceEasy>\r\n";
//				xml+="\t<season><![CDATA["+season+"]]></season>\r\n";
//				xml+="\t<temperature><![CDATA["+temperature+"]]></temperature>\r\n";
//				xml+="\t<sunshine><![CDATA["+sunshine+"]]></sunshine>\r\n";
//				xml+="\t<water><![CDATA["+water+"]]></water>\r\n";
//				xml+="</root>";
//				System.out.println(xml);
//				
//				JDFSFile.saveString(_dir+name+".xml",xml,false,"UTF-8");
//				
//				Thread.sleep(200);
//				
//				start=txt.indexOf("<div class=\"tImgIcons center mt10 vm\"><a href=\"",end);
//				end=txt.indexOf("\" class=\"title\">",start);
//			}
//		}
	}
}
