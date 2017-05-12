package pers.ys.jms.mq;

import java.util.HashMap;

import pers.ys.jms.mq.component.Processor;
import pers.ys.jms.mq.message.Message;

//通过Processor接口实现不同的处理器
public class TestProcessor implements Processor {

	@Override
	public Message process(Message message) {
		// 解析message的数据并进行处理
		// String s = message.getMessages()[0] + " World!";
		String url = message.getMessages()[0].toString();
		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) message.getMessages()[1];
		String s = "";
		try {
			s = ApacheHTTPSimulate.sendHttp("POST", url, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 处理完毕后创建包含处理结果的新消息并返回
		return new Message(message.getId(), s, message.getMessages()[2]);
	}
}
