package pers.ys.jms.mq.component;

import java.io.Serializable;

import javax.jms.JMSException;

import pers.ys.jms.mq.message.Message;

/**
 * 处理者接口
 * 
 * @author YS
 * 
 */
public interface Handler {

	/**
	 * 发起处理请求
	 * 
	 * @param object
	 * @return
	 * @throws JMSException
	 * @throws InterruptedException
	 */
	public abstract Message request(Serializable... object) throws JMSException, InterruptedException;
}
