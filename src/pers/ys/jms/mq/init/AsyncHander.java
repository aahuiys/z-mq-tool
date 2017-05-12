package pers.ys.jms.mq.init;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pers.ys.jms.mq.component.GenericProcessor;
import pers.ys.jms.mq.component.Handler;
import pers.ys.jms.mq.component.ObjectHandler;
import pers.ys.jms.mq.component.ProcessResultsCache;
import pers.ys.jms.mq.component.Processor;
import pers.ys.jms.mq.config.Config;
import pers.ys.jms.mq.connection.ActiveMQConnectionFctory;
import pers.ys.jms.mq.connection.MQConnectionFctory;
import pers.ys.jms.mq.thread.pool.ThreadPool;

/**
 * 异步处理者工厂实现
 * 
 * @author YS
 * 
 */
public class AsyncHander implements HandlerFactory, MessageListener {

	private static final Log LOGGER = LogFactory.getLog(AsyncHander.class);

	/**
	 * 消息处理器
	 */
	private final Processor processor;

	/**
	 * ActiveMQ功能切换
	 */
	private final String mqSwitch;

	/**
	 * 处理线程池
	 */
	private ThreadPool pool;

	/**
	 * 消息通道
	 */
	private String target;

	/**
	 * 处理结果缓存
	 */
	private ProcessResultsCache resultsCache;

	/**
	 * JMS连接工厂
	 */
	private MQConnectionFctory connectionFctory;

	public AsyncHander(ThreadPool pool, Config config) throws ClassNotFoundException {
		// 加载处理器
		try {
			String className = config.get("class");
			Class<?> clazz = Class.forName(className);
			if (!Processor.class.isAssignableFrom(clazz))
				throw new ClassNotFoundException("Class " + className + " not a Processor.");
			processor = constructProcessor(clazz);
		} catch (ClassNotFoundException e) {
			LOGGER.error(e);
			throw e;
		}
		mqSwitch = config.get("switch");
		if (mqSwitch.equals("1")) init(pool, config);// 状态开启则初始化队列资源
	}

	@Override
	public Handler getHandler() throws JMSException {
		if (mqSwitch.equals("1")) return new ObjectHandler(mqSwitch, target, resultsCache, getSession(false));
		return new ObjectHandler(mqSwitch, processor);
	}

	@Override
	public void onMessage(Message message) {
		pool.execute(new GenericProcessor(message, processor, resultsCache));
	}

	/**
	 * 构造处理器
	 * 
	 * @param clazz
	 * @return
	 */
	private Processor constructProcessor(Class<?> clazz) {
		Processor processor = null;
		try {
			processor = (Processor) clazz.newInstance();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return processor;
	}

	/**
	 * 获取JMS会话
	 * 
	 * @param failover
	 * @return
	 * @throws JMSException
	 */
	private QueueSession getSession(boolean failover) throws JMSException {
		QueueConnection connection = null;
		QueueSession session = null;
		try {
			connection = (QueueConnection) connectionFctory.getConnection(failover);
			session = connection.createQueueSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			if (connection != null) ((ActiveMQConnectionFctory) connectionFctory).resetConnection();
			throw e;
		}
		return session;
	}

	/**
	 * 初始化资源
	 * 
	 * @param pool
	 * @param config
	 */
	private void init(ThreadPool pool, final Config config) {
		this.pool = pool;
		target = config.get("target");
		resultsCache = new ProcessResultsCache(config);
		connectionFctory = new ActiveMQConnectionFctory(config);
		// 注册消息监听器
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					QueueSession session = getSession(true);
					Queue queue = session.createQueue(target);
					QueueReceiver receiver = session.createReceiver(queue);
					receiver.setMessageListener(AsyncHander.this);
					LOGGER.info("MessageListener is registered (" + "failover:" + config.get("broker_url") + ":" + config.get("target") + ").");
				} catch (JMSException e) {
					LOGGER.error("MessageListener is registered failed (" + "failover:" + config.get("broker_url") + ":" + config.get("target") + ")!", e);
				}
			}
		}).start();
	}
}
