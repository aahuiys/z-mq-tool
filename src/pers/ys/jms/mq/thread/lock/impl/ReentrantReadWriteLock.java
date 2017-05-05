package pers.ys.jms.mq.thread.lock.impl;

import java.util.HashMap;
import java.util.Map;

import pers.ys.jms.mq.thread.lock.ReadWriteLock;

/**
 * 可重入读写锁实现
 * 
 * @author YS
 * 
 */
public class ReentrantReadWriteLock implements ReadWriteLock {

	/**
	 * 持有读锁线程
	 */
	private Map<Thread, Integer> readingThreads = new HashMap<Thread, Integer>();

	/**
	 * 写锁入锁次数
	 */
	private int writeAccesses = 0;

	/**
	 * 写锁请求数量
	 */
	private int writeRequests = 0;

	/**
	 * 持有写锁线程
	 */
	private Thread writingThread = null;

	@Override
	public synchronized void lockRead() throws InterruptedException {
		Thread callingThread = Thread.currentThread();
		while (!canGrantReadAccess(callingThread)) {
			wait();
		}
		readingThreads
				.put(callingThread, getReadAccessCount(callingThread) + 1);
	}

	@Override
	public synchronized void unlockRead() {
		Thread callingThread = Thread.currentThread();
		if (!isReader(callingThread)) {
			throw new IllegalMonitorStateException(
					"Calling thread does not hold a read lock on this ReentrantReadWriteLock.");
		}
		int accessCount = getReadAccessCount(callingThread);
		if (--accessCount == 0) {
			readingThreads.remove(callingThread);
			notifyAll();
		} else {
			readingThreads.put(callingThread, accessCount);
		}
	}

	@Override
	public synchronized void lockWrite() throws InterruptedException {
		writeRequests++;
		Thread callingThread = Thread.currentThread();
		while (!canGrantWriteAccess(callingThread)) {
			wait();
		}
		writeRequests--;
		writeAccesses++;
		writingThread = callingThread;
	}

	@Override
	public synchronized void unlockWrite() {
		if (!isWriter(Thread.currentThread())) {
			throw new IllegalMonitorStateException(
					"Calling thread does not hold the write lock on this ReentrantReadWriteLock.");
		}
		if (--writeAccesses == 0) {
			writingThread = null;
		}
		notifyAll();
	}

	/**
	 * 获取读锁入锁次数
	 * 
	 * @param callingThread
	 * @return
	 */
	private int getReadAccessCount(Thread callingThread) {
		Integer accessCount = readingThreads.get(callingThread);
		if (accessCount == null)
			return 0;
		return accessCount.intValue();
	}

	/**
	 * 是否可获得读锁
	 * 
	 * @param callingThread
	 * @return
	 */
	private boolean canGrantReadAccess(Thread callingThread) {
		if (isWriter(callingThread))
			return true;
		if (hasWriter())
			return false;
		if (isReader(callingThread))
			return true;
		if (hasWriteRequests())
			return false;
		return true;
	}

	/**
	 * 是否可获得写锁
	 * 
	 * @param callingThread
	 * @return
	 */
	private boolean canGrantWriteAccess(Thread callingThread) {
		if (isOnlyReader(callingThread))
			return true;
		if (hasReaders())
			return false;
		if (!hasWriter())
			return true;
		if (!isWriter(callingThread))
			return false;
		return true;
	}

	/**
	 * 是否有持有读锁线程
	 * 
	 * @return
	 */
	private boolean hasReaders() {
		return readingThreads.size() > 0;
	}

	/**
	 * 是否持有读锁
	 * 
	 * @param callingThread
	 * @return
	 */
	private boolean isReader(Thread callingThread) {
		return readingThreads.get(callingThread) != null;
	}

	/**
	 * 是否唯一持有读锁线程
	 * 
	 * @param callingThread
	 * @return
	 */
	private boolean isOnlyReader(Thread callingThread) {
		return readingThreads.size() == 1 && isReader(callingThread);
	}

	/**
	 * 是否有持有写锁线程
	 * 
	 * @return
	 */
	private boolean hasWriter() {
		return writingThread != null;
	}

	/**
	 * 是否持有写锁
	 * 
	 * @param callingThread
	 * @return
	 */
	private boolean isWriter(Thread callingThread) {
		return writingThread == callingThread;
	}

	/**
	 * 是否有写锁请求
	 * 
	 * @return
	 */
	private boolean hasWriteRequests() {
		return writeRequests > 0;
	}
}
