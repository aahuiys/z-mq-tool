package pers.ys.jms.mq;

import pers.ys.jms.mq.component.ProcessResultsCache;
import pers.ys.jms.mq.component.Processor;
import pers.ys.jms.mq.message.Message;

//继承Processor类实现不同的处理方法
public class TestProcessor extends Processor {

	// 调用父类唯一有参构造
	public TestProcessor(javax.jms.Message message, ProcessResultsCache resultsCache) {
		super(message, resultsCache);
	}

	@Override
	protected Message process(Message message) {
		// 解析message的数据并进行处理
		String s = message.getMessages()[0] + " World!";
		// 处理完毕后创建包含处理结果的新消息并返回
		return new Message(message.getId(), s);
	}
}
