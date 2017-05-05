package pers.ys.jms.mq.thread.queue.impl;

import java.util.LinkedList;

import pers.ys.jms.mq.thread.queue.BlockingQueue;

/**
 * 阻塞链表队列实现
 * 
 * @author YS
 * 
 * @param <E>
 */
public class BlockingLinkedQueue<E> implements BlockingQueue<E> {

	/**
	 * 队列长度
	 */
	private int length = 10;

	/**
	 * 队列元素
	 */
	private LinkedList<E> queue = new LinkedList<E>();

	public BlockingLinkedQueue(int length) {
		this.length = length;
	}

	@Override
	public synchronized void enqueue(E obj) throws InterruptedException {
		while (queue.size() == length) {
			wait();// 队满阻塞
		}
		if (queue.size() == 0) {
			notifyAll();// 队空唤醒等待线程
		}
		queue.addLast(obj);
	}

	@Override
	public synchronized E dequeue() throws InterruptedException {
		while (queue.size() == 0) {
			wait();// 队空阻塞
		}
		if (queue.size() == length) {
			notifyAll();// 队满唤醒等待线程
		}
		return queue.removeFirst();
	}
}
