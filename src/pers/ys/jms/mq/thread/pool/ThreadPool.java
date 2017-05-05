package pers.ys.jms.mq.thread.pool;

/**
 * 线程池接口
 * 
 * @author YS
 * 
 */
public interface ThreadPool {

	/**
	 * 执行任务
	 * 
	 * @param r
	 */
	public abstract void execute(Runnable r);
}
