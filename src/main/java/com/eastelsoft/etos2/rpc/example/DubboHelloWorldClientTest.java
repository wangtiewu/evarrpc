package com.eastelsoft.etos2.rpc.example;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.eastelsoft.etos2.rpc.example.HelloWorld;

public class DubboHelloWorldClientTest {
	private static void testP(final HelloWorld helloWorld, final int count,
			final int threadCount, final int payloadSize) {
		long t1 = System.currentTimeMillis();
		final CountDownLatch finished = new CountDownLatch(count * threadCount);
		StringBuffer payload = new StringBuffer();
		int size = payloadSize;
		while (size-- > 0) {
			payload.append(size % 2 == 0 ? "0" : "1");
		}
		final String str = payload.toString();
		for (int k = 0; k < threadCount; k++) {
			new Thread() {
				public void run() {
					for (int i = 0; i < count; i++) {
						try {
							String data = helloWorld.sayHello(str + i);
							if (!("Wellcome, " + str + i).equals(data)) {
								System.err.println("rpc fail: " + data);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						// System.out.println(helloWorld.sayHello("wtw:" + i));
						finished.countDown();

					}
				}
			}.start();
		}
		try {
			finished.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(payloadSize
				+ " bytes test: 请求数 " + count * threadCount
				+ "，耗时 "+ (System.currentTimeMillis() - t1)
				+ "，qps "+ count * threadCount / ((System.currentTimeMillis() - t1) / 1000 == 0 ? 1 : (System
						.currentTimeMillis() - t1) / 1000)+"，mbps "+ (2L * count * threadCount * payloadSize)/1000/((System.currentTimeMillis() - t1) / 1000 == 0 ? 1 : (System
								.currentTimeMillis() - t1)));
	}
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "dubboClient.xml" });
		final HelloWorld helloWorld = (HelloWorld) context.getBean("helloWorld");
		int count = 50000;
		int threadCount = Math.max(2, Runtime.getRuntime().availableProcessors());
		int payloadBytes = 512;
		if (args.length == 1) {
			count = Integer.parseInt(args[0]);
		}
		else if (args.length == 2) {
			count = Integer.parseInt(args[0]);
			threadCount = Integer.parseInt(args[1]);
		}
		if (args.length >= 3) {
			count = Integer.parseInt(args[0]);
			threadCount = Integer.parseInt(args[1]);
			payloadBytes = Integer.parseInt(args[2]);
		}
		testP(helloWorld, count, threadCount, payloadBytes);
		// testP(helloWorld, 10000, 100, 1024);
		// testP(helloWorld, 10000, 100, 2048);
		// testP(helloWorld, 10000, 100, 4096);
		// testP(helloWorld, 10000, 8, 8192);
		// testP(helloWorld, 10000, 8, 256);
		// testP(helloWorld, 10000, 8, 512);
		System.out.println(helloWorld.add(1, -1));
		System.out.println(helloWorld.list());
		helloWorld.test1("str", 1);
		helloWorld.test1(1, 2, "str");
		System.out.println(helloWorld.test_0());
		System.out.println(helloWorld.test_1());
		System.out.println(helloWorld.test_2());
		System.out.println(helloWorld.test_3());
		System.out.println(helloWorld.test_4());
		System.out.println(helloWorld.test_5());
		System.out.println(helloWorld.test_6());
		System.out.println(helloWorld.test_7());
		System.out.println(helloWorld.test_list());
		System.out.println(helloWorld.test_map());
		System.out.println(Arrays.deepToString(helloWorld.test_ary_string()));
	}
}
