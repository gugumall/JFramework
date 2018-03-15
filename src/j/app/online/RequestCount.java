package j.app.online;

/**
 * 
 * @author 肖炯
 *
 */
public class RequestCount {
	public volatile long firstRequestTime=0;
	public volatile long latestRequestTime=0;
	public volatile long requests=0;
}
