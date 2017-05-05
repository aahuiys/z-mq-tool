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
	 * 消息通道
	 */
	private final String target;

	/**
	 * JMS会话
	 */
	private final QueueSession session;

	/**
	 * 处理结果缓存
	 */
	private final ProcessResultsCache resultsCache;

	public ObjectHandler(String target, QueueSession session, ProcessResultsCache resultsCache) {
		this.target = target;
		this.session = session;
		this.resultsCache = resultsCache;
	}

	@Override
	public Message request(Serializable... object) throws JMSException, InterruptedException {
		QueueSender sender = null;
		try {
			Queue queue = session.createQueue(target);
			sender = session.createSender(queue);
			sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// 生成唯一消息ID
			String messageId = UUIDGenerator.getUUID();
			// 创建消息
			Message msg = new Message(messageId, object);
			// 设置等待信号
			resultsCache.setSemaphore(messageId);
			// 发送消息
			sender.send(session.createObjectMessage(msg));
			// 从缓存中取回处理结果
			return resultsCache.getMessage(messageId);
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
