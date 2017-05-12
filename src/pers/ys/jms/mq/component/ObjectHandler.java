package pers.ys.jms.mq.component;

import java.io.Serializable;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.QueueSession;

import pers.ys.jms.mq.message.Message;
import pers.ys.jms.mq.util.UUIDGenerator;

/**
 * 对象消息处理者实现
 * 
 * @author YS
 * 
 */
public class ObjectHandler implements Handler {

	/**
	 * ActiveMQ功能切换
	 */
	private final String mqSwitch;

	/**
	 * 消息处理器
	 */
	private Processor processor;

	/**
	 * 消息通道
	 */
	private String target;

	/**
	 * 处理结果缓存
	 */
	private ProcessResultsCache resultsCache;

	/**
	 * JMS会话
	 */
	private QueueSession session;

	public ObjectHandler(String mqSwitch, Processor processor) {
		this.mqSwitch = mqSwitch;
		this.processor = processor;
	}

	public ObjectHandler(String mqSwitch, String target, ProcessResultsCache resultsCache, QueueSession session) {
		this.mqSwitch = mqSwitch;
		this.target = target;
		this.resultsCache = resultsCache;
		this.session = session;
	}

	@Override
	public Message request(Serializable... object) throws JMSException, InterruptedException {
		// 生成唯一消息ID
		String messageId = UUIDGenerator.getUUID();
		// 创建消息
		Message message = new Message(messageId, object);
		if (mqSwitch.equals("1")) return request(message);
		return processor.process(message);
	}

	public Message request(Message message) throws JMSException, InterruptedException {
		QueueSender sender = null;
		try {
			Queue queue = session.createQueue(target);
			sender = session.createSender(queue);
			sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// 设置等待信号
			resultsCache.setSemaphore(message.getId());
			// 发送消息
			sender.send(session.createObjectMessage(message));
			// 从缓存中取回处理结果
			return resultsCache.getMessage(message.getId());
		} catch (JMSException e) {
			if (resultsCache.getSemaphore(message) != null) resultsCache.removeSemaphore(message);
			throw e;
		} finally {
			try {
				// 释放资源
				if (sender != null) sender.close();
				session.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
