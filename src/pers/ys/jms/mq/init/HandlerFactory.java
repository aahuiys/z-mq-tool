package pers.ys.jms.mq.init;

import javax.jms.JMSException;

import pers.ys.jms.mq.component.Handler;

/**
 * 处理者工厂接口
 * 
 * @author YS
 * 
 */
public interface HandlerFactory {

	/**
	 * 获取处理者
	 * 
	 * @return
	 * @throws JMSException
	 */
	public abstract Handler getHandler() throws JMSException;
}
