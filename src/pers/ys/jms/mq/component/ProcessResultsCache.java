package pers.ys.jms.mq.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pers.ys.jms.mq.config.Config;
import pers.ys.jms.mq.message.Message;
import pers.ys.jms.mq.thread.lock.ReadWriteLock;
import pers.ys.jms.mq.thread.lock.impl.ReentrantReadWriteLock;
import pers.ys.jms.mq.thread.semaphore.Semaphore;
import pers.ys.jms.mq.thread.semaphore.impl.HandleSemaphore;

/**
 * 处理结果缓存
 * 
 * @author YS
 * 
 */
public class ProcessResultsCache {

	private static final Log LOGGER = LogFactory.getLog(ProcessResultsCache.class);

	/**
	 * 处理信号读写锁
	 */
	private final ReadWriteLock semaphoreLock;

	/**
	 * 处理结果读写锁
	 */
	private final ReadWriteLock messageLock;

	/**
	 * 处理信号缓存
	 */
	private final Map<String, Semaphore> semaphores;

	/**
	 * 处理结果缓存
	 */
	private final Map<String, Message> messages;

	/**
	 * 处理超时时间
	 */
	private final long timeMillis;

	public ProcessResultsCache(Config config) {
		semaphoreLock = new ReentrantReadWriteLock();
		messageLock = new ReentrantReadWriteLock();
		semaphores = new HashMap<String, Semaphore>();
		messages = new HashMap<String, Message>();
		timeMillis = Long.valueOf(config.get("timeMillis"));
		LOGGER.info(this.getClass().getSimpleName() + " Init Complete [" + this.toString() + "] (" + config.get("broker_url") + ":" + config.get("target") + ").");
	}

	/**
	 * 缓存处理结果
	 * 
	 * @param message
	 * @return
	 * @throws InterruptedException
	 */
	public boolean putMessage(Message message) throws InterruptedException {
		boolean isHandle = false;
		Semaphore semaphore = getSemaphore(message);
		// 缓存完毕前锁定信号，避免取结果线程提前取空
		synchronized (semaphore) {
			isHandle = semaphore.release();
			if (isHandle) {
				messageLock.lockWrite();
				try {
					messages.put(message.getId(), message);
				} finally {
					messageLock.unlockWrite();
				}
			}
		}
		// 处理完毕后移除缓存的信号
		removeSemaphore(message);
		return isHandle;
	}

	/**
	 * 获取处理结果
	 * 
	 * @param id
	 * @return
	 * @throws InterruptedException
	 */
	public Message getMessage(String id) throws InterruptedException {
		Message message = null;
		boolean isHandle = getSemaphore(id).take();// 等待处理信号通知
		if (isHandle) {
			messageLock.lockRead();
			try {
				message = messages.get(id);
			} finally {
				messageLock.unlockRead();
			}
			// 取得结果后从缓存中移除
			messageLock.lockWrite();
			try {
				messages.remove(id);
			} finally {
				messageLock.unlockWrite();
			}
		}
		return message;
	}

	/**
	 * 设置请求处理信号
	 * 
	 * @param id
	 * @throws InterruptedException
	 */
	public void setSemaphore(String id) throws InterruptedException {
		semaphoreLock.lockWrite();
		try {
			semaphores.put(id, new HandleSemaphore(timeMillis));
			LOGGER.debug("Set the send semaphore: " + id);
		} finally {
			semaphoreLock.unlockWrite();
		}
	}

	/**
	 * 获取处理信号
	 * 
	 * @param message
	 * @return
	 * @throws InterruptedException
	 */
	private Semaphore getSemaphore(Message message) throws InterruptedException {
		return getSemaphore(message.getId());
	}

	/**
	 * 获取处理信号
	 * 
	 * @param id
	 * @return
	 * @throws InterruptedException
	 */
	private Semaphore getSemaphore(String id) throws InterruptedException {
		semaphoreLock.lockRead();
		try {
			return semaphores.get(id);
		} finally {
			semaphoreLock.unlockRead();
		}
	}

	/**
	 * 移除处理信号
	 * 
	 * @param message
	 * @throws InterruptedException
	 */
	private void removeSemaphore(Message message) throws InterruptedException {
		removeSemaphore(message.getId());
	}

	/**
	 * 移除处理信号
	 * 
	 * @param id
	 * @throws InterruptedException
	 */
	private void removeSemaphore(String id) throws InterruptedException {
		semaphoreLock.lockWrite();
		try {
			semaphores.remove(id);
		} finally {
			semaphoreLock.unlockWrite();
		}
	}
}
