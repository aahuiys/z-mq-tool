package pers.ys.jms.mq;

import java.util.HashMap;
import java.util.Scanner;

import javax.jms.JMSException;

import pers.ys.jms.mq.component.Handler;
import pers.ys.jms.mq.init.AsyncHandlerFactory;
import pers.ys.jms.mq.init.HandlerFactory;
import pers.ys.jms.mq.message.Message;

public class Test {

	// 在需要调用的类中声明一个全局处理者工厂
	private static final HandlerFactory FACTORY = AsyncHandlerFactory.getInstance();

	private static volatile boolean isRun = false;

	public static void main(String[] args) {
		HandlerFactory a = AsyncHandlerFactory.getInstance("mq.properties");
		HandlerFactory b = AsyncHandlerFactory.getInstance("classpath:mq.properties");
		HandlerFactory c = AsyncHandlerFactory.getInstance("/mq.properties");
		HandlerFactory d = AsyncHandlerFactory.getInstance("classpath:/mq.properties");
		final HandlerFactory f = AsyncHandlerFactory.getInstance("multi.properties");
		HandlerFactory g = AsyncHandlerFactory.getInstance("classpath:multi.properties");
		HandlerFactory h = AsyncHandlerFactory.getInstance("/multi.properties");
		HandlerFactory j = AsyncHandlerFactory.getInstance("classpath:/multi.properties");
		System.out.println(FACTORY);
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
		System.out.println(d);
		System.out.println(FACTORY.equals(a) && a.equals(b) && b.equals(c) && c.equals(d));
		System.out.println(f);
		System.out.println(g);
		System.out.println(h);
		System.out.println(j);
		System.out.println(f.equals(g) && g.equals(h) && h.equals(j));
		new Thread(new Runnable() {

			@Override
			public void run() {
				String status = "";
				Scanner scanner = new Scanner(System.in);
				while (true) {
					status = scanner.nextLine();
					if (status.equals("start")) isRun = true;
					else if (status.equals("stop")) isRun = false;
				}
			}
		}).start();
		for (int i = 0;; i++) {
			final int n = i;
			try {
				Thread.sleep(1000);
				if (!isRun) continue;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						if (FACTORY == null) return;
						// 使用工厂获取一个处理者
						Handler handler = FACTORY.getHandler();
//						handler = f.getHandler();
						// 使用处理者发送消息请求并得到返回结果
//						String url = "http://localhost:8080/webswap/whetherDrug.ws";
						String url = "http://106.14.92.38/webswap/whetherDrug.ws";
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("isvEntId", "17c4656c-d0ea-11e6-95e5-28f10e23505a");
						map.put("entId", "001345");
						map.put("productEntName", "中信测试21世纪制药厂北京分公司1");
						map.put("physicName", "盐酸地芬尼多片");
						map.put("prepnType", "片剂");
						map.put("prepnSpe", "25mg");
						map.put("pkgSpe", "30片/盒");
						map.put("produceBatchNo", "盐酸地芬141219001");
						Message message = handler.request(url, map, n);
						// 处理返回的结果
						if (message != null)  System.out.println(message.getMessages()[1].toString() + message.getMessages()[0]);
						else System.out.println(n + "[Tiemout]!");
					} catch (JMSException e) {
						System.out.println(e.getMessage());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}
