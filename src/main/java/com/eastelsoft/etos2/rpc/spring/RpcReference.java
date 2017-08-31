package com.eastelsoft.etos2.rpc.spring;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

import com.eastelsoft.etos2.rpc.Consts.SerializeType;
import com.eastelsoft.etos2.rpc.example.HelloWorld;
import com.eastelsoft.etos2.rpc.proxy.CglibRpcProxy;
import com.eastelsoft.etos2.rpc.proxy.JdkDynamicRpcProxy;
import com.eastelsoft.etos2.rpc.proxy.MethodInfoCache;
import com.eastelsoft.etos2.rpc.registry.Consumer;
import com.eastelsoft.etos2.rpc.registry.Registry;
import com.eastelsoft.etos2.rpc.serialize.RpcRespSerialize;
import com.eastelsoft.etos2.rpc.tool.NetUtils;

/**
 * 
 * rpc 引用
 * 
 * @author Eastelsoft
 *
 */
public class RpcReference implements FactoryBean, InitializingBean,
		DisposableBean, ApplicationListener, ApplicationContextAware {
	private static final Logger logger = LoggerFactory
			.getLogger(RpcService.class);
	private String interfaceName;
	private String ipAddr;
	private String serializeType;
	private String registry;
	private Class serializeClass;
	private Registry registry2;
	private static ConcurrentHashMap<String, Class> serializeClasses = new ConcurrentHashMap<String, Class>();
	static {
		serializeClasses
				.put(SerializeType.JDK_NATIVE.value(),
						com.eastelsoft.etos2.rpc.serialize.jdknative.JdkNativeRpcRespSerialize.class);
		serializeClasses
				.put(SerializeType.PROTOSTUFF.value(),
						com.eastelsoft.etos2.rpc.serialize.protostuff.ProtostuffRpcRespSerialize.class);
		serializeClasses
				.put(SerializeType.PROTOBUF.value(),
						com.eastelsoft.etos2.rpc.serialize.protobuf.ProtobufRpcRespSerialize.class);
	}

	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public RpcReference() {
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getSerializeType() {
		return serializeType;
	}

	public void setSerializeType(String serializeType) {
		this.serializeType = serializeType;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public void destroy() throws Exception {
	}

	public void afterPropertiesSet() throws Exception {
		if (StringUtils.isEmpty(serializeType)) {
			serializeClass = com.eastelsoft.etos2.rpc.serialize.jdknative.JdkNativeRpcRespSerialize.class;
			return;
		}
		serializeClass = serializeClasses.get(serializeType);
		if (StringUtils.isEmpty(serializeClass)) {
			serializeClass = Class.forName(serializeType);
			serializeClasses.putIfAbsent(serializeType, serializeClass);
		}
	}

	public void onApplicationEvent(ApplicationEvent arg0) {
		if (!StringUtils.isEmpty(registry)) {
			registry2 = (Registry) applicationContext.getBean(registry);
			if (registry2 == null) {
				System.err.println("registry " + registry
						+ " not defined in spring");
				return;
			}
			Consumer consumer = new Consumer();
			consumer.setInterfaceName(this.interfaceName);
			consumer.setSerializeType(this.serializeType);
			consumer.setAddress(NetUtils.getLocalHost());
			if (!registry2.registerConsumer(consumer)) {
				System.err.println("Consumer " + interfaceName
						+ " register to registry fail");
			}
			try {
				MethodInfoCache.getInstance().register(interfaceName,
						Class.forName(interfaceName));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public Object getObject() throws Exception {
		return execute(getObjectType());
	}

	private <T> T execute(Class<T> rpcInterface) throws Exception {
		if (!StringUtils.isEmpty(registry2)) {
			if (StringUtils.isEmpty(serializeClass)) {
				if (rpcInterface.isInterface()) {
					return JdkDynamicRpcProxy.createProxy(rpcInterface,
							registry2);
				} else {
					return CglibRpcProxy.createProxy(rpcInterface, registry2,
							interfaceName);
				}
			} else {
				RpcRespSerialize rpcRespSerialize = (RpcRespSerialize) serializeClass
						.newInstance();
				if (rpcInterface.isInterface()) {
					return JdkDynamicRpcProxy.createProxy(rpcInterface,
							registry2, rpcRespSerialize);
				} else {
					return CglibRpcProxy.createProxy(rpcInterface, registry2,
							interfaceName, rpcRespSerialize);
				}
			}
		}
		if (StringUtils.isEmpty(serializeClass)) {
			if (rpcInterface.isInterface()) {
				return JdkDynamicRpcProxy.createProxy(rpcInterface, ipAddr);
			} else {
				return CglibRpcProxy.createProxy(rpcInterface, ipAddr,
						interfaceName);
			}
		} else {
			RpcRespSerialize rpcRespSerialize = (RpcRespSerialize) serializeClass
					.newInstance();
			if (rpcInterface.isInterface()) {
				return JdkDynamicRpcProxy.createProxy(rpcInterface, ipAddr,
						rpcRespSerialize);
			} else {
				return CglibRpcProxy.createProxy(rpcInterface, ipAddr,
						interfaceName, rpcRespSerialize);
			}
		}
	}

	public Class<?> getObjectType() {
		try {
			return this.getClass().getClassLoader().loadClass(interfaceName);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public boolean isSingleton() {
		return true;
	}

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
		AbstractXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "spring-bean-container-rpcclient.xml" });
		final HelloWorld helloWorld = (HelloWorld) context
				.getBean("helloWorld");
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
