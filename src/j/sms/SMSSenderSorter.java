package j.sms;

import j.util.JUtilSorter;

/**
 * 
 * @author 肖炯
 *
 */
public class SMSSenderSorter extends JUtilSorter{
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * @see j.util.JUtilSorter#compare(java.lang.Object, java.lang.Object)
	 */
	public String compare(Object pre, Object after) {
		SMSSenderConfig preSender=(SMSSenderConfig)pre;
		SMSSenderConfig afterSender=(SMSSenderConfig)after;
		
		if(preSender.priority>afterSender.priority){
			return JUtilSorter.BIGGER;
		}else if(preSender.priority<afterSender.priority){
			return JUtilSorter.SMALLER;
		}else{
			return JUtilSorter.EQUAL;
		}
	}
}