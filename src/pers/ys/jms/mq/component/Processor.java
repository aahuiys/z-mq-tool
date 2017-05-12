package pers.ys.jms.mq.component;

import pers.ys.jms.mq.message.Message;

/**
 * 消息处理器接口
 * 
 * @author YS
 * 
 */
public interface Processor {

	/**
	 * 处理消息
	 * 
	 * @param message
	 * @return
	 */
	public abstract Message process(Message message);
}
