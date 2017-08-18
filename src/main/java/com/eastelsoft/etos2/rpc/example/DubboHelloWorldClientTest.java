package com.eastelsoft.etos2.rpc.example;

import java.util.concurrent.CountDownLatch;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.eastelsoft.etos2.rpc.example.HelloWorld;

public class DubboHelloWorldClientTest {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "dubboClient.xml" });
		final HelloWorld helloWorld = (HelloWorld) context.getBean("helloWorld");
		long t1 = System.currentTimeMillis();
		final int count = 100000;
		int threadCount = 8;
		final CountDownLatch finished = new CountDownLatch(count * threadCount);
		for (int k = 0; k < threadCount; k++) {
			new Thread() {
				public void run() {
					for (int i = 0; i < count; i++) {
						try {
							String data = helloWorld.sayHello("wtw:" + i);
							if (!("Wellcome, " + "wtw:" + i).equals(data)) {
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
		System.out.println(System.currentTimeMillis() - t1);
		System.out.println(helloWorld.add(1, -1));
		System.out.println(helloWorld.list());
	}
}
