package pers.ys.jms.mq.connection;

import javax.jms.Connection;

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
	 * @return
	 */
	public abstract Connection getConnection();
}
