package j.test;

import j.util.JUtilSorter;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TestSorter extends JUtilSorter{
	/*
	 *  (non-Javadoc)
	 * @see j.util.JUtilSorter#compare(java.lang.Object, java.lang.Object)
	 */
	public String compare(Object pre, Object after) {
		Integer preNum=(Integer)pre;
		Integer afterNum=(Integer)after;
		if(preNum.intValue()>afterNum.intValue()) return JUtilSorter.BIGGER;
		else if(preNum.intValue()==afterNum.intValue()) return JUtilSorter.EQUAL;
		return JUtilSorter.SMALLER;
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		Random r=new Random();
		TestSorter s=new TestSorter();
		List list=new LinkedList();
		for(int i=0;i<1000;i++){
			list.add(new Integer(r.nextInt(10000)));
		}
		
		for(int i=0;i<list.size();i++){
			System.out.print(list.get(i)+",");
		}
		long t1=System.currentTimeMillis();
		
		list=s.mergeSort(list,JUtilSorter.ASC);
		long t2=System.currentTimeMillis();
		System.out.println();
		System.out.println("t = "+(t2-t1));
		
		for(int i=0;i<list.size();i++){
			System.out.print(list.get(i)+",");
		}
		System.out.println();
		System.out.println();
		
		
		Integer[] data=new Integer[]{3,5,1,99,7,9,9,2,33};
		s.mergeSort(data,JUtilSorter.DESC);
		
		for(int i=0;i<data.length;i++){
			System.out.print(data[i]+",");
		}
	}
}
