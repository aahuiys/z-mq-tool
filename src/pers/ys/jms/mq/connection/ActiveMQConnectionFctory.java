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
	public Connection getConnection(boolean failover) throws JMSException {
		if (failover) return createConnection("failover:" + config.get("broker_url"));
		if (connection == null) init();
		return connection;
	}

	/**
	 * 重置连接
	 */
	public synchronized void resetConnection() {
		connection = null;
	}

	/**
	 * 初始化生产者连接
	 * 
	 * @throws JMSException
	 */
	private synchronized void init() throws JMSException {
		if (connection == null) connection = createConnection(config.get("broker_url"));
	}

	/**
	 * 创建连接
	 * 
	 * @param broker
	 * @return
	 * @throws JMSException
	 */
	private Connection createConnection(String broker) throws JMSException {
		Connection connection = null;
		try {
			QueueConnectionFactory factory = new ActiveMQConnectionFactory(config.get("username"), config.get("password"), broker);
			connection = factory.createQueueConnection();
			connection.start();
			LOGGER.info("Connected to ActiveMQ (" + broker + ").");
		} catch (JMSException e) {
			LOGGER.error("Connected to ActiveMQ failed (" + broker + ")!", e);
			throw e;
		}
		return connection;
	}
}
