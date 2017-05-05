package pers.ys.jms.mq.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 加载配置文件类
 * 
 * @author YS
 * 
 */
public class Config {

	private static final Log LOGGER = LogFactory.getLog(Config.class);

	/**
	 * 配置属性
	 */
	private Map<String, String> config = new HashMap<String, String>();

	/**
	 * 加载配置文件
	 * 
	 * @param location
	 * @return
	 */
	public static Config load(String location) {
		return new Config().loadLocation(location);
	}

	/**
	 * 解析路径
	 * 
	 * @param location
	 * @return
	 */
	private Config loadLocation(String location) {
		if (location.startsWith("classpath:")) location = location.substring("classpath:".length());
		return loadClasspath(location);
	}

	/**
	 * 从classpath加载
	 * 
	 * @param classpath
	 * @return
	 */
	private Config loadClasspath(String classpath) {
		if (classpath.startsWith("/")) classpath = classpath.substring(1);
		InputStream is = null;
		try {
			is = getDefault().getResourceAsStream(classpath);
		} catch (Exception e) {
			LOGGER.error("Can not get InputStream [classpath:" + classpath + "]!", e);
			return null;
		}
		return loadInputStream(is, classpath);
	}

	/**
	 * 获取ClassLoader
	 * 
	 * @return
	 */
	private ClassLoader getDefault() {
		ClassLoader loader = null;
		try {
			loader = Thread.currentThread().getContextClassLoader();
			if (loader == null)
				loader = Config.class.getClassLoader();
			if (loader == null)
				loader = ClassLoader.getSystemClassLoader();
		} catch (Exception e) {
			LOGGER.error("Can not get ClassLoader!", e);
		}
		return loader;
	}

	/**
	 * 读入配置文件
	 * 
	 * @param is
	 * @param location
	 * @return
	 */
	private Config loadInputStream(InputStream is, String location) {
		Properties config = new Properties();
		try {
			config.load(is);
			LOGGER.info("Load config [classpath:" + location + "].");
		} catch (IOException e) {
			LOGGER.error("Config load failed [classpath:" + location + "]!", e);
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}
		return load(config);
	}

	/**
	 * 读入配置项
	 * 
	 * @param properties
	 * @return
	 */
	private Config load(Properties properties) {
		Iterator<Object> it = properties.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			String value = properties.getProperty(key);
			config.put(key, value);
		}
		return this;
	}

	/**
	 * 获取配置项
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return config.get(key);
	}
}
