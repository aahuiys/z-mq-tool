package pers.ys.jms.mq.thread.semaphore.impl;

import pers.ys.jms.mq.thread.semaphore.Semaphore;

/**
 * 处理信号量实现
 * 
 * @author YS
 * 
 */
public class HandleSemaphore implements Semaphore {

	/**
	 * 处理超时时间
	 */
	private long timeMillis = 0;

	/**
	 * 是否处理成功
	 */
	private boolean isHandle = false;

	/**
	 * 是否开始等待
	 */
	private boolean isWait = false;

	/**
	 * 是否等待超时
	 */
	private boolean isTimeout = false;

	public HandleSemaphore(long timeMillis) {
		this.timeMillis = timeMillis;
	}

	@Override
	public synchronized boolean take() throws InterruptedException {
		isWait = true;
		notify();// 开始等待前唤醒处理线程
		wait(timeMillis);
		isTimeout = true;
		return isHandle;
	}

	@Override
	public synchronized boolean release() throws InterruptedException {
		while (!isWait) {
			wait();// 没有开始等待时阻塞信号发出
		}
		notify();
		if (!isTimeout)
			isHandle = true;// 如果没有等待超时则设置处理成功
		return isHandle;
	}
}
