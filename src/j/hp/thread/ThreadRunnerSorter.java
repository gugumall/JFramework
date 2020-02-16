package j.hp.thread;

import j.util.JUtilSorter;

/**
 * 
 * @author 肖炯
 *
 * 2020年2月17日
 *
 * <b>功能描述</b>
 */
public class ThreadRunnerSorter extends JUtilSorter{
	private static final long serialVersionUID = 1L;
	
	private static ThreadRunnerSorter instance=new ThreadRunnerSorter();
	
	/**
	 * 
	 * @return
	 */
	public static ThreadRunnerSorter instance(){
		return instance;
	}

	@Override
	public String compare(Object pre, Object after) {
		ThreadRunner _pre=(ThreadRunner)pre;
		ThreadRunner _after=(ThreadRunner)after;
		
		if(_pre.getTasksCount()<_after.getTasksCount()) {
			return JUtilSorter.SMALLER;
		}else if(_pre.getTasksCount()>_after.getTasksCount()) {
			return JUtilSorter.BIGGER;
		}else {
			return JUtilSorter.EQUAL;
		}
	}
}
