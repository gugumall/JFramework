package j.tool.ip;


public class IPDBInit {
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{		
		String addr=IP.getLocation("119.103.130.101");
		long start=System.currentTimeMillis();
		String addr2=IP.getLocation("119.103.130.101");
		String addr3=IP.getLocation("116.93.102.83");
		System.out.println("--------------- "+addr);
		System.out.println("---------------2 "+addr2);
		System.out.println("---------------3 "+addr3);
		
		long end=System.currentTimeMillis();
		
		System.out.println(end-start);

//		int cnt=0;
//		DAO dao=DB.connect("IP",IPDBInit.class);
//		DB.sqliteSetSynchronous(dao,DB.sqliteSynchronousOff);
//		dao.executeSQL("create table IF NOT EXISTS j_ip(IP_ID int,IP_START long,IP_END long,IP_ADDR varchar)");
//		File file=new File("E:\\jstudio\\jframework\\doc\\ips.sql");
//		InputStream in=new FileInputStream(file);
//		BufferedReader reader=new BufferedReader(new InputStreamReader(in,"UTF-8"));
//		String line=null;
//		try{
//	    	line=reader.readLine();
//	    	while(line!=null){	 
//	    		dao.executeSQL(JUtilString.replaceAll(line,"\\'","''"));
//	    		line=reader.readLine();
//	    		if(cnt%1000==0){;
//	    			System.out.println(cnt);
//	    		}
//	    		cnt++;
//	    	}
//	    	
//	    	dao.close();
//		}catch(Exception e){
//			e.printStackTrace();
//			System.out.println(line);
//		}
		System.out.println("end");
    	System.exit(0);
	}
}
