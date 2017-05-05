package pers.ys.jms.mq.message;

import java.io.Serializable;

/**
 * 消息类
 * 
 * @author YS
 * 
 */
public class Message implements Serializable {

	private static final long serialVersionUID = -8698248880097578326L;

	/**
	 * 唯一消息标识
	 */
	private final String id;

	/**
	 * 消息内容
	 */
	private final Serializable[] messages;

	public Message(String id, Serializable... obj) {
		this.id = id;
		messages = new Serializable[obj.length];
		for (int i = 0; i < obj.length; i++) messages[i] = obj[i];
	}

	/**
	 * 获取唯一消息标识
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 获取消息内容
	 * 
	 * @return
	 */
	public Serializable[] getMessages() {
		return messages;
	}
}
