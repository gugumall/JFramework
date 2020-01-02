package j.hp.thread;

public class SampleThreadTask extends ThreadTask{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param in
	 * @param retries
	 * @param uuid
	 */
	public SampleThreadTask(Object[] in,int retries,String uuid){
		super(in,retries,uuid);
	}
	
	@Override
	public Object[] execute() throws Exception {
		return new Object[] {"我是执行结果"};
	}

	@Override
	public boolean equalz(ThreadTask other) {
		return false;
	}
}
