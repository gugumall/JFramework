package j.temp;

import j.util.JUtilRandom;

public class Th implements Runnable{
	private String words;
	private int no;
	
	public Th(int no,String words){
		this.no=no;
		this.words=words;
	}

	@Override
	public void run(){
		try{
			Thread.sleep(1000*JUtilRandom.nextInt(3));
		}catch(Exception e){}
		System.out.println(this.words);
		ThManeger.finish(this.no);
	}
}
