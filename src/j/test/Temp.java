package j.test;


/**
 * 
 * @author 肖炯
 *
 */
public class Temp {
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		int lost=0;
		for(int i=1;i<=25;i++){
			int ifWin=lost+i;
			lost+=ifWin;
			System.out.println("第"+i+"次 下注"+ifWin+"元，如赢，利润为"+i+"元");
		}
	}
}
