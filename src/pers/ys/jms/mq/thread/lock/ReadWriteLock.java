package pers.ys.jms.mq.thread.lock;

/**
 * 读写锁接口
 * 
 * @author YS
 * 
 */
public interface ReadWriteLock {

	/**
	 * 锁定读操作
	 * 
	 * @throws InterruptedException
	 */
	public abstract void lockRead() throws InterruptedException;

	/**
	 * 解锁读操作
	 */
	public abstract void unlockRead();

	/**
	 * 锁定写操作
	 * 
	 * @throws InterruptedException
	 */
	public abstract void lockWrite() throws InterruptedException;

	/**
	 * 解锁写操作
	 */
	public abstract void unlockWrite();
}
