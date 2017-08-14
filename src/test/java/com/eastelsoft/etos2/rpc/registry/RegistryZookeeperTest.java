package com.eastelsoft.etos2.rpc.registry;

import com.eastelsoft.etos2.rpc.Consts.SerializeType;

import junit.framework.TestCase;

public class RegistryZookeeperTest extends TestCase {
	public void testRegisterProvider() {
		RegistryZookeeper registry = new RegistryZookeeper();
		String servers = "127.0.0.1:12181";
		registry.setServers(servers);
		registry.init();
		Provider provider = new Provider();
		String interfaceName = "com.eastelsoft.etos2.rpc.example.HelloWorld";
		provider.setInterfaceName(interfaceName);
		provider.setServerAddress("192.168.1.102:8889");
		provider.setSerializeType(SerializeType.JDK_NATIVE.value());
		assertEquals(true, registry.registerProvider(provider));
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		registry.destroy();
	}
	
	public void test() {
		RegistryZookeeper registry = new RegistryZookeeper();
		String servers = "127.0.0.1:12181";
		registry.setServers(servers);
		registry.init();
		String interfaceName = "com.eastelsoft.etos2.rpc.example.HelloWorld";
		assertEquals(0, registry.allConsumers(interfaceName).size());
		assertEquals(0, registry.allProviders(interfaceName).size());
		Provider provider = new Provider();
		provider.setInterfaceName(interfaceName);
		provider.setServerAddress("192.168.1.102:8888");
		provider.setSerializeType(SerializeType.JDK_NATIVE.value());
		assertEquals(true, registry.registerProvider(provider));
		assertEquals(1, registry.allProviders(interfaceName).size());
		Consumer consumer = new Consumer();
		consumer.setInterfaceName(interfaceName);
		consumer.setAddress("192.168.1.102:11222");
		consumer.setSerializeType(SerializeType.JDK_NATIVE.value());
		assertEquals(true, registry.registerConsumer(consumer));
		assertEquals(1, registry.allConsumers(interfaceName).size());
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		registry.destroy();
	}
}
