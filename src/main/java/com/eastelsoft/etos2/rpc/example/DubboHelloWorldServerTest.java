package com.eastelsoft.etos2.rpc.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DubboHelloWorldServerTest {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationDubbo.xml" });
		try {
			Thread.sleep(10000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
