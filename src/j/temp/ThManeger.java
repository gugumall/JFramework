package j.temp;

import java.util.HashMap;
import java.util.Map;

public class ThManeger implements Runnable{
	private static Map finished=new HashMap();
	
	protected static void finish(int no){
		finished.put(""+no,"aaa");
	}
	
	public static void main(String[] args){
		
		ThManeger m=new ThManeger();
		Thread mth=new Thread(m);
		mth.start();
		
		for(int i=0;i<5;i++){
			Th t=new Th(i,"hello,"+i);
			Thread th=new Thread(t);
			th.start();
		}		
	}

	@Override
	public void run(){
		while(finished.size()<5){
			try{
				Thread.sleep(1000);
			}catch(Exception e){}
		}
		System.out.print("OK");
	}
}
