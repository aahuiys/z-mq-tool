package pers.ys.jms.mq.init;

import java.lang.reflect.Constructor;

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
	 * 消息通道
	 */
	private final String target;

	/**
	 * 处理线程池
	 */
	private final ThreadPool pool;

	/**
	 * 消息处理类
	 */
	private final Class<Processor> processor;

	/**
	 * 处理结果缓存
	 */
	private final ProcessResultsCache resultsCache;

	/**
	 * JMS连接工厂
	 */
	private final MQConnectionFctory connectionFctory;

	@SuppressWarnings("unchecked")
	public AsyncHander(final Config config, ThreadPool pool) throws ClassNotFoundException {
		this.target = config.get("target");
		this.pool = pool;
		// 加载处理类
		try {
			String className = config.get("class");
			Class<?> clazz = Class.forName(className);
			if (!Processor.class.isAssignableFrom(clazz))
				throw new ClassNotFoundException("Class " + className + " not a Processor.");
			processor = (Class<Processor>) clazz;
		} catch (ClassNotFoundException e) {
			LOGGER.error(e);
			throw e;
		}
		// 初始化资源
		resultsCache = new ProcessResultsCache(config);
		connectionFctory = new ActiveMQConnectionFctory(config);
		// 配置消息监听器
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					QueueSession session = getSession();
					Queue queue = session.createQueue(target);
					QueueReceiver receiver = session.createReceiver(queue);
					receiver.setMessageListener(AsyncHander.this);
					LOGGER.info("MessageListener is registered (" + config.get("broker_url") + ":" + config.get("target") + ").");
				} catch (JMSException e) {
					LOGGER.error("MessageListener is registered failed (" + config.get("broker_url") + ":" + config.get("target") + ")!", e);
				}
			}
		}).start();
	}

	@Override
	public Handler getHandler() throws JMSException {
		return new ObjectHandler(target, getSession(), resultsCache);
	}

	@Override
	public void onMessage(Message message) {
		pool.execute(constructProcessor(message));
	}

	/**
	 * 构造处理器
	 * 
	 * @param message
	 * @return
	 */
	private Processor constructProcessor(Message message) {
		Processor processor = null;
		try {
			Class<?>[] types = new Class[] { Message.class, ProcessResultsCache.class };
			Object[] params = new Object[] { message, resultsCache };
			Constructor<Processor> constructor = this.processor.getConstructor(types);
			processor = constructor.newInstance(params);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return processor;
	}

	/**
	 * 获取JMS会话
	 * 
	 * @return
	 * @throws JMSException
	 */
	private QueueSession getSession() throws JMSException {
		QueueConnection connection = (QueueConnection) connectionFctory.getConnection();
		QueueSession session = connection.createQueueSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
		return session;
	}
}
