package pers.ys.jms.mq.connection;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pers.ys.jms.mq.config.Config;

/**
 * JMS连接工厂实现
 * 
 * @author YS
 * 
 */
public class ActiveMQConnectionFctory implements MQConnectionFctory {

	private static final Log LOGGER = LogFactory.getLog(ActiveMQConnectionFctory.class);

	/**
	 * 配置信息
	 */
	private final Config config;

	/**
	 * JMS连接
	 */
	private Connection connection = null;

	public ActiveMQConnectionFctory(Config config) {
		this.config = config;
	}

	@Override
	public Connection getConnection() {
		if (connection == null) init();
		return connection;
	}

	/**
	 * 初始化连接
	 */
	private synchronized void init() {
		if (connection == null) {
			try {
				QueueConnectionFactory factory = new ActiveMQConnectionFactory(config.get("username"), config.get("password"), config.get("broker_url"));
				connection = factory.createQueueConnection();
				connection.start();
				LOGGER.info("Connected to ActiveMQ (" + config.get("broker_url") + ").");
			} catch (JMSException e) {
				LOGGER.error("Connected to ActiveMQ failed (" + config.get("broker_url") + ")!", e);
			}
		}
	}
}
