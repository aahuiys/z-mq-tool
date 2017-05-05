package pers.ys.jms.mq.component;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pers.ys.jms.mq.message.Message;

/**
 * 消息处理器
 * 
 * @author YS
 * 
 */
public abstract class Processor implements Runnable {

	protected static final Log LOGGER = LogFactory.getLog(Processor.class);

	/**
	 * 待处理消息
	 */
	protected final javax.jms.Message message;

	/**
	 * 处理结果缓存
	 */
	protected final ProcessResultsCache resultsCache;

	public Processor(javax.jms.Message message, ProcessResultsCache resultsCache) {
		this.message = message;
		this.resultsCache = resultsCache;
	}

	@Override
	public void run() {
		try {
			// 转换消息
			Message msg = (Message) ((ObjectMessage) message).getObject();
			// 调用不同的处理器实现
			msg = process(msg);
			// 缓存处理结果
			resultsCache.putMessage(msg);
		} catch (JMSException e) {
			LOGGER.error(e);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * 处理消息
	 * 
	 * @param message
	 * @return
	 */
	protected abstract Message process(Message message);
}
