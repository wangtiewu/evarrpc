package com.eastelsoft.etos2.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class RpcServerStart {
	private static Logger logger = LoggerFactory.getLogger(RpcServerStart.class);
	private static AbstractXmlApplicationContext context;
	
	public static AbstractXmlApplicationContext getContext() {
		return context;
	}

	public static void main(String[] args) {
		context = new ClassPathXmlApplicationContext(new String[]{"spring-bean-container-rpc.xml"});
	}
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				logger.info("RpcServerStart shutdown");
				if (context != null) {
					context.close();
					context = null;
				}
			}
		}, "RpcServerStart shutdown hook"));
	}
}
