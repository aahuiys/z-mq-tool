package pers.ys.jms.mq.init;

import java.util.HashMap;
import java.util.Map;

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
	 * 默认处理者工厂
	 */
	private static HandlerFactory factory;

	/**
	 * 配置信息
	 */
	private static Map<String, Config> configures;

	/**
	 * 处理者工厂
	 */
	private static Map<Config, HandlerFactory> factories;

	static {
		config = Config.load("mq.properties");
		pool = new FixedThreadPool(Integer.valueOf(config.get("nThreads")), Integer.valueOf(config.get("length")));
		configures = new HashMap<String, Config>();
		factories = new HashMap<Config, HandlerFactory>();
	}

	/**
	 * 获取默认处理者工厂
	 * 
	 * @return
	 */
	public static HandlerFactory getInstance() {
		if (factory == null) build();
		return factory;
	}

	/**
	 * 通过配置文件获取处理者工厂
	 * 
	 * @param location
	 * @return
	 */
	public static HandlerFactory getInstance(String location) {
		location = parseLocation(location);
		// 全局配置返回默认处理者工厂
		if (location.equals("classpath:mq.properties")) return getInstance();
		HandlerFactory factory = null;
		synchronized (configures) {
			// 通过路径获取配置信息
			Config config = configures.get(location);
			// 获取配置信息对应的处理者工厂
			if (config != null) factory = factories.get(config);
			// 不存在时进行加载
			else factory = build(location);
		}
		return factory;
	}

	/**
	 * 转换为统一格式的配置文件路径
	 * 
	 * @param location
	 * @return
	 */
	private static String parseLocation(String location) {
		return getPrefix(location) + dealPath(removePrefix(location));
	}

	/**
	 * 获取路径前缀
	 * 
	 * @param location
	 * @return
	 */
	private static String getPrefix(String location) {
		if (location.startsWith("classpath:")) return "classpath:";
		return "classpath:";
	}

	/**
	 * 去除路径前缀
	 * 
	 * @param location
	 * @return
	 */
	private static String removePrefix(String location) {
		if (location.startsWith("classpath:")) return location.substring("classpath:".length());
		return location;
	}

	/**
	 * 处理路径
	 * 
	 * @param path
	 * @return
	 */
	private static String dealPath(String path) {
		if (path.startsWith("/")) return path.substring(1);
		return path;
	}

	/**
	 * 建造默认处理者工厂
	 */
	private synchronized static void build() {
		if (factory == null) {
			try {
				factory = new AsyncHander(pool, config);
			} catch (ClassNotFoundException e) {
			}
		}
	}

	/**
	 * 通过配置文件建造处理者工厂
	 * 
	 * @param location
	 * @return
	 */
	private static HandlerFactory build(String location) {
		HandlerFactory factory = null;
		try {
			Config config = Config.load(location);
			factory = new AsyncHander(pool, config);
			configures.put(location, config);
			factories.put(config, factory);
		} catch (ClassNotFoundException e) {
		}
		return factory;
	}
}
