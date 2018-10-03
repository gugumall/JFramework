
package j.util;

import j.http.JHttp;
import j.http.JHttpContext;
import j.sys.SysUtil;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;


public class JUtilTimestamp implements Runnable{
	public static final String WEEK_DAY_1="星期一";
	public static final String WEEK_DAY_2="星期二";
	public static final String WEEK_DAY_3="星期三";
	public static final String WEEK_DAY_4="星期四";
	public static final String WEEK_DAY_5="星期五";
	public static final String WEEK_DAY_6="星期六";
	public static final String WEEK_DAY_7="星期日";
	
	public static final long millisOfSecond=1000L;
	public static final long millisOfMinute=60000L;
	public static final long millisOfHour=3600000L;
	public static final long millisOfDay=86400000L;
	
	private static volatile long bjTime=0;//北京时间与本地时间只差
	
	static{
//		JUtilTimestamp i=new JUtilTimestamp();
//		Thread t=new Thread(i);
//		t.start();
//		System.out.println("JUtilTimestamp thread started.");
	}
	

	/**
	 * 标准时间
	 * @return
	 */
	public static long getBjTime(){
		return System.currentTimeMillis()+bjTime;
	}
	
	/**
	 * 
	 * @param time
	 * @return 如 20080808
	 */
	public static int getDateAsYYYYMMDD(Timestamp time){
		return Integer.parseInt(JUtilString.replaceAll(time.toString().substring(0,10),"-",""));
	}
	
	/**
	 * 得到符合中国习惯的周一~周日顺序号（1~7）
	 * @param time
	 * @return
	 */
	public static int getWeekDayUsualOrderCn(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
        calendar=null;
        if(dayNum == Calendar.SUNDAY){
            return 7;
        }else if(dayNum == Calendar.MONDAY){
            return 1;
    	}else if(dayNum == Calendar.TUESDAY){
            return 2;
    	}else if(dayNum == Calendar.WEDNESDAY){
            return 3;
    	}else if(dayNum == Calendar.THURSDAY){
            return 4;
    	}else if(dayNum == Calendar.FRIDAY){
            return 5;
    	}else{
            return 6;
    	}
    }
	
	
	/**
	 * 得到指定时间是星期几的中文名
	 * @param time
	 * @return
	 */
    public static String getWeekDayCn(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
        calendar=null;
        if(dayNum == Calendar.SUNDAY){
            return "星期日";
        }else if(dayNum == Calendar.MONDAY){
            return "星期一";
    	}else if(dayNum == Calendar.TUESDAY){
            return "星期二";
    	}else if(dayNum == Calendar.WEDNESDAY){
            return "星期三";
    	}else if(dayNum == Calendar.THURSDAY){
            return "星期四";
    	}else if(dayNum == Calendar.FRIDAY){
            return "星期五";
    	}else{
            return "星期六";
    	}
    }
    
    /**
	 * 得到当前是星期几的中文名
     * @return
     */
    public static String getWeekDayCn(){
        return getWeekDayCn(SysUtil.getNow());
    }  
    
    
    public static String getWeekDayEn(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
        calendar=null;
        if(dayNum == Calendar.SUNDAY){
            return "Sunday";
        }else if(dayNum == Calendar.MONDAY){
            return "Monday";
    	}else if(dayNum == Calendar.TUESDAY){
            return "Tuesday";
    	}else if(dayNum == Calendar.WEDNESDAY){
            return "Wednesday";
    	}else if(dayNum == Calendar.THURSDAY){
            return "Thursday";
    	}else if(dayNum == Calendar.FRIDAY){
            return "Friday";
    	}else{
            return "Saturday";
    	}
    }
    
    /**
	 * 得到当前是星期几的中文名
     * @return
     */
    public static String getWeekDayEn(){
        return getWeekDayCn(SysUtil.getNow());
    }  
    
    /**
     * 得到星期几、时、分、秒、年、月、日、每月的第几周等，其中月份的返回值是实际月份数-1
     * @param time
     * @param type
     * @return
     */
    public static int getValue(long time,int type){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        int result = calendar.get(type);
        return result;
    }
    
    /**
     * 是否闰年
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year){
    	if((year%4==0&&year%100!=0)||(year%100==0&&year%400==0)){
    		return true;
    	}
    	return false;
    }
    

    
    /**
     * 得到某月的天数
     * @param year
     * @return
     */
    public static int getDaysOfMonth(int year,int month){
    	if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){
    		return 31;
    	}else if(month==2){
    		if(isLeapYear(year)){
    			return 29;
    		}else{
    			return 28;    		
    		}
    	}else{
    		return 30;
    	}
    }
    
    /**
     * 
     * @param original
     * @param daysAdd
     * @return
     */
    public static Timestamp addToTime(Timestamp original,int daysAdd){
    	if(original==null||daysAdd==0) return original;
    	return new Timestamp(original.getTime()+daysAdd*3600000*24L);
    }

    
    /**
     * 
     * @param original
     * @param daysAdd
     * @return
     */
    public static Timestamp addToTime(Timestamp original,double daysAdd){
    	if(original==null||daysAdd==0) return original;
    	return new Timestamp(original.getTime()+(long)(daysAdd*3600000*24L));
    }


	/**
	 * 
	 * @return
	 */
	public static String timestamp(){
		return timestamp(System.currentTimeMillis());
	}

	/**
	 * 
	 * @param time
	 * @return
	 */
	public static String timestamp(long time){
		String t=(new Timestamp(time)).toString();
		if(t.length()==19) t+=".000";
		else if(t.length()==21) t+="00";
		else if(t.length()==22) t+="0";
		return t;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isTimestamp(String value){
		if(value==null) return false;
		return (value.matches("^\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}$")
					||value.matches("^\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}.\\d{1,3}$"));
	}
	
	/**
	 * 
	 * @param now
	 * @return
	 */
	public static Timestamp getMondayOfWeek(long now){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(now));
        
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
        calendar=null;
        if(dayNum == Calendar.SUNDAY){
        	return new Timestamp(now-millisOfDay*6);
        }else if(dayNum == Calendar.MONDAY){
        	return new Timestamp(now);
    	}else if(dayNum == Calendar.TUESDAY){
    		return new Timestamp(now-millisOfDay*1);
    	}else if(dayNum == Calendar.WEDNESDAY){
    		return new Timestamp(now-millisOfDay*2);
    	}else if(dayNum == Calendar.THURSDAY){
    		return new Timestamp(now-millisOfDay*3);
    	}else if(dayNum == Calendar.FRIDAY){
    		return new Timestamp(now-millisOfDay*4);
    	}else{
    		return new Timestamp(now-millisOfDay*5);
    	}  
	}
	
	/**
	 * 
	 * @param now
	 * @return
	 */
	public static Timestamp nextWeek(Timestamp now){
		if(now==null) return null;
		
		return new Timestamp(now.getTime()+millisOfDay*7);
	}
	
	/**
	 * 0000-00-00
	 * @param now
	 * @return
	 */
	public static Timestamp nextMonth(Timestamp now){
		if(now==null) return null;
		
		String _now=now.toString().substring(0,19);
		int year=Integer.parseInt(_now.substring(0,4));
		int month=Integer.parseInt(_now.substring(5,7));
		int day=Integer.parseInt(_now.substring(8,10));
		
		month+=1;
		if(month>12){
			year++;
			month=1;
		}
		
		int allDaysOfNextMonth=getDaysOfMonth(year,month);
		while(day>allDaysOfNextMonth) day--;
		
		_now=year+"-"+(month<10?"0":"")+month+"-"+day+_now.substring(10);
		
		return Timestamp.valueOf(_now);
	}
	
	/**
	 * 0000-00-00
	 * @param now
	 * @return
	 */
	public static Timestamp nextSeason(Timestamp now){
		if(now==null) return null;
		
		for(int i=0;i<3;i++) now=nextMonth(now);
		
		return now;
	}
	
	/**
	 * 0000-00-00
	 * @param now
	 * @return
	 */
	public static Timestamp nextYear(Timestamp now){
		if(now==null) return null;
		
		for(int i=0;i<12;i++) now=nextMonth(now);
		
		return now;
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		JHttp jhttp=JHttp.getInstance();
		int loop=-1;
		boolean done=false;
		while(true){
			try{
				Thread.sleep(1000);
			}catch(Exception e){}
			
			if(!done&&(loop==-1||loop==30)){
				try{
					JHttpContext context=new JHttpContext();
					context.addRequestHeader("Referer","http://bjtime.cn/");
					context=jhttp.get(context,null,"http://bjtime.cn/header6.asp");
					String response=context!=null&&context.getStatus()==200?context.getResponseText():null;
					if(response!=null){
						String t="";
						int start=response.indexOf("nyear=");
						int end=-1;
						if(start>0){
							end=response.indexOf(";",start);
							t+=response.substring(start+6,end)+"-";
							
							start=response.indexOf("nmonth=");
							end=response.indexOf(";",start);
							String s=response.substring(start+7,end);
							if(s.length()==1) s="0"+s;
							t+=s+"-";
							
							start=response.indexOf("nday=");
							end=response.indexOf(";",start);
							s=response.substring(start+5,end);
							if(s.length()==1) s="0"+s;
							t+=s+" ";
							
							start=response.indexOf("nhrs=");
							end=response.indexOf(";",start);
							s=response.substring(start+5,end);
							if(s.length()==1) s="0"+s;
							t+=s+":";
							
							start=response.indexOf("nmin=");
							end=response.indexOf(";",start);
							s=response.substring(start+5,end);
							if(s.length()==1) s="0"+s;
							t+=s+":";
							
							start=response.indexOf("nsec=");
							end=response.indexOf(";",start);
							s=response.substring(start+5,end);
							if(s.length()==1) s="0"+s;
							t+=s;
							
							bjTime=Timestamp.valueOf(t).getTime()-System.currentTimeMillis();
							
							done=true;
							
							System.out.println("get bj time from bjtime.cn: "+bjTime+", local time: "+System.currentTimeMillis());
						}
					}
				}catch(Exception e){
					//e.printStackTrace();
				}
				
				loop=0;
			}
			
			loop++;
		}
	}
	
	/**
	 * 
	 * @param time
	 * @return
	 */
	public static String[] getHowMuchTimeInDDHHMMSSFormat(long time){
		long _day=3600000L*24;
		long _hour=3600000L;
		long _minute=60000L;
		long _second=1000L;
		
		long t_day=(long)Math.floor(time/_day);
		String s_day=(t_day<10?"0":"")+t_day;
		
		time=time%_day;
		long t_hour=(long)Math.floor(time/_hour);
		String s_hour=(t_hour<10?"0":"")+t_hour;
		
		time=time%_hour;
		long t_minute=(long)Math.floor(time/_minute);
		if(t_minute<10) t_minute='0'+t_minute;
		String s_minute=(t_minute<10?"0":"")+t_minute;
		
		time=time%_minute;
		long t_second=(long)Math.floor(time/_second);
		String s_second=(t_second<10?"0":"")+t_second;
		
		return new String[]{s_day,s_hour,s_minute,s_second};
	}
    
    /**
     * SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss 'UTC'Z yyyy",Locale.ENGLISH);     
	   sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));   
	   String out=sdf.format(new Date(long time));
     */
	
	public static void main(String[] args){
		System.out.println(new Timestamp(1492394298492L));
		System.out.println(isLeapYear(2004));
		Timestamp now=Timestamp.valueOf("2016-02-29 01:01:01");
		System.out.println(JUtilTimestamp.nextWeek(now));
		System.out.println(JUtilTimestamp.nextMonth(now));
		System.out.println(JUtilTimestamp.nextSeason(now));
		System.out.println(JUtilTimestamp.nextYear(now));
	}
}
