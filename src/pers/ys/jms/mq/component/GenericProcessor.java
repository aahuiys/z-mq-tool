package pers.ys.jms.mq.component;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pers.ys.jms.mq.message.Message;

/**
 * 通用处理器
 * 
 * @author YS
 * 
 */
public class GenericProcessor implements Runnable {

	private static final Log LOGGER = LogFactory.getLog(GenericProcessor.class);

	/**
	 * 待处理消息
	 */
	private final javax.jms.Message message;

	/**
	 * 消息处理器
	 */
	private final Processor processor;

	/**
	 * 处理结果缓存
	 */
	private final ProcessResultsCache resultsCache;

	public GenericProcessor(javax.jms.Message message, Processor processor, ProcessResultsCache resultsCache) {
		this.message = message;
		this.processor = processor;
		this.resultsCache = resultsCache;
	}

	@Override
	public void run() {
		try {
			// 转换消息
			Message msg = (Message) ((ObjectMessage) message).getObject();
			// 调用消息处理器处理消息
			msg = processor.process(msg);
			// 缓存处理结果
			resultsCache.putMessage(msg);
		} catch (JMSException e) {
			LOGGER.error(e);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}
}
