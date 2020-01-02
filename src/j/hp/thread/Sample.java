package j.hp.thread;

public class Sample {
	public static void main(String[] args) {
		//创建一个线程池，分别指定参数为线程池ID，线程池内线程数量，执行任务线程两次检测是否有任务之间的时间间隔（ms）,线程池空闲多久（ms）后销毁
		//ThreadPool会创建指定线程数（ThreadRunner），ThreadRunner在其run方法内调用ThreadTask的execute方法
		//Thread
		ThreadPool pool=ThreadManager.getPool("the_thread_pool_id",50,100,60000);
		
		//将一个任务（实现ThreadTask的execute方法用于执行实际业务逻辑，并实现equalz方法比较两个任务是否相同，同一时间两个相同任务不能同时在任务列表内）
		//SampleThreadTask构造函数的第2个参数为如果执行失败，最多尝试执行任务的次数
		//可根据task-id从分配到ThreadRunner获取结果
		//执行结果（ThreadTaskResult）由ThreadTask的execute返回，可指定结果超时时间（默认为30秒），
		//即如果从runner获取了执行结果则ThreadTaskResult将从TaskRunner的结果记录中立即移除，否则超时移除
		ThreadTask task=new SampleThreadTask(new Object[]{"AA","BB"},1,"task-id");
		ThreadRunner runner=pool.addTask(task);
		System.out.println("111111111");
		ThreadTaskResult result=runner.getResult(task.getUuid());
		System.out.println(result.getResult()[0]);
	}
}
