package pers.ys.jms.mq.init;

import pers.ys.jms.mq.config.Config;
import pers.ys.jms.mq.thread.pool.ThreadPool;
import pers.ys.jms.mq.thread.pool.impl.FixedThreadPool;

/**
 * 工厂初始化类
 * 
 * @author YS
 * 
 */
public abstract class AsyncHandlerFactory {

	/**
	 * 全局配置
	 */
	private static Config config;

	/**
	 * 处理线程池
	 */
	private static ThreadPool pool;

	/**
	 * 处理者工厂
	 */
	private static HandlerFactory factory;

	static {
		config = Config.load("classpath:mq.properties");
		pool = new FixedThreadPool(Integer.valueOf(config.get("nThreads")), Integer.valueOf(config.get("length")));
	}

	/**
	 * 获取处理者工厂
	 * 
	 * @return
	 */
	public static HandlerFactory getInstance() {
		if (factory == null) build();
		return factory;
	}

	/**
	 * 建造处理者工厂
	 */
	private synchronized static void build() {
		if (factory == null)
			try {
				factory = new AsyncHander(config, pool);
			} catch (ClassNotFoundException e) {
			}
	}
}
