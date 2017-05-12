package pers.ys.jms.mq.connection;

import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * JMS连接工厂接口
 * 
 * @author YS
 * 
 */
public interface MQConnectionFctory {

	/**
	 * 获取连接
	 * 
	 * @param failover
	 * @return
	 * @throws JMSException
	 */
	public abstract Connection getConnection(boolean failover) throws JMSException;
}
