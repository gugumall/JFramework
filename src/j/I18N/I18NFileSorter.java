package j.I18N;

import j.util.JUtilSorter;

/**
 * 
 * @author one
 *
 */
public class I18NFileSorter extends JUtilSorter{
	private static final long serialVersionUID=1L;

	@Override
	public String compare(Object pre,Object after){
		String preName=(String)pre;
		String afterName=(String)after;
		
		if(preName.compareTo(afterName)>0){
			return JUtilSorter.BIGGER;
		}else if(preName.compareTo(afterName)<0){
			return JUtilSorter.SMALLER;
		}else{
			return JUtilSorter.EQUAL;
		}
	}

}
