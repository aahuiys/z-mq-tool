package pers.ys.jms.mq;

import javax.jms.JMSException;

import pers.ys.jms.mq.component.Handler;
import pers.ys.jms.mq.init.AsyncHandlerFactory;
import pers.ys.jms.mq.init.HandlerFactory;
import pers.ys.jms.mq.message.Message;

public class Test {

	// 在需要调用的类中声明一个全局处理者工厂
	private static final HandlerFactory FACTORY = AsyncHandlerFactory.getInstance();

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			final int n = i;
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						if (FACTORY == null)
							return;
						// 使用工厂获取一个处理者
						Handler handler = FACTORY.getHandler();
						// 使用处理者发送消息请求并得到返回结果
						Message message = handler.request((n + 1) + "-Hellow");
						// 处理返回的结果
						System.out.println(message.getMessages()[0]);
					} catch (JMSException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}
