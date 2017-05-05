package pers.ys.jms.mq.thread.semaphore;

/**
 * 信号量接口
 * 
 * @author YS
 * 
 */
public interface Semaphore {

	/**
	 * 获取信号
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public abstract boolean take() throws InterruptedException;

	/**
	 * 释放信号
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public abstract boolean release() throws InterruptedException;
}
