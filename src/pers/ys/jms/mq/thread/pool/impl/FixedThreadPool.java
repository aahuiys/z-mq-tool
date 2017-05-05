package pers.ys.jms.mq.thread.pool.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pers.ys.jms.mq.thread.pool.ThreadPool;
import pers.ys.jms.mq.thread.queue.BlockingQueue;
import pers.ys.jms.mq.thread.queue.impl.BlockingLinkedQueue;

/**
 * 固定大小线程池实现
 * 
 * @author YS
 * 
 */
public class FixedThreadPool implements ThreadPool {

	private static final Log LOGGER = LogFactory.getLog(FixedThreadPool.class);

	/**
	 * 线程数
	 */
	private final int nThreads;

	/**
	 * 最大任务数
	 */
	private final int length;

	/**
	 * 线程组
	 */
	private final PoolWorker[] theads;

	/**
	 * 任务缓存阻塞队列
	 */
	private final BlockingQueue<Runnable> queue;

	public FixedThreadPool(int nThreads, int length) {
		this.nThreads = nThreads;
		this.length = length;
		theads = new PoolWorker[this.nThreads];
		queue = new BlockingLinkedQueue<Runnable>(this.length);
		// 创建线程组并启动线程
		for (int i = 0; i < this.nThreads; i++) {
			theads[i] = new PoolWorker("AsyncThread-[" + (i + 1) + "]");
			theads[i].start();
		}
		LOGGER.info("The " + this.getClass().getSimpleName() + " has been created [" + this.toString() + "].");
	}

	@Override
	public void execute(Runnable r) {
		try {
			queue.enqueue(r);// 缓存任务
		} catch (InterruptedException e) {
			LOGGER.warn("Runnable enqueue interrupted!", e);
		}
	}

	private class PoolWorker extends Thread {

		public PoolWorker(String name) {
			super(name);
		}

		@Override
		public void run() {
			Runnable r = null;
			while (true) {
				try {
					// 从缓存队列中取出任务并执行
					r = queue.dequeue();
					r.run();
				} catch (InterruptedException e) {
					LOGGER.warn("A async thread dequeue interrupted!", e);
				} catch (RuntimeException e) {
					LOGGER.error("Catch RuntimeException: " + e.getClass().getSimpleName(), e);
				} finally {
					r = null;
				}
			}
		}
	}
}
