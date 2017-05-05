package pers.ys.jms.mq.thread.queue;

/**
 * 阻塞队列接口
 * 
 * @author YS
 * 
 * @param <E>
 */
public interface BlockingQueue<E> {

	/**
	 * 入队
	 * 
	 * @param obj
	 * @throws InterruptedException
	 */
	public abstract void enqueue(E obj) throws InterruptedException;

	/**
	 * 出队
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public abstract E dequeue() throws InterruptedException;
}
