package com.eastelsoft.etos2.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.eastelsoft.etos2.rpc.RpcRequest;
import com.eastelsoft.etos2.rpc.example.HelloWorld;
import com.eastelsoft.etos2.rpc.registry.Registry;
import com.eastelsoft.etos2.rpc.serialize.RpcRespSerialize;
import com.eastelsoft.etos2.rpc.tool.IdGenerator;

/**
 * 基于Jdk的动态rpc代理
 * 
 * @author Eastelsoft
 *
 */
public class JdkDynamicRpcProxy extends RpcProxy implements InvocationHandler {
	private Registry registry;
	private String serverAddress;
	private RpcRespSerialize rpcRespSerialize;
	private int retryTimes = 3;// rpc 调用重试次数

	public JdkDynamicRpcProxy() {
	}

	public JdkDynamicRpcProxy(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public JdkDynamicRpcProxy(String serverAddress,
			RpcRespSerialize rpcRespSerialize) {
		this.serverAddress = serverAddress;
		this.rpcRespSerialize = rpcRespSerialize;
	}

	public JdkDynamicRpcProxy(Registry registry,
			RpcRespSerialize rpcRespSerialize) {
		this.registry = registry;
		this.rpcRespSerialize = rpcRespSerialize;
	}

	public JdkDynamicRpcProxy(Registry registry) {
		this.registry = registry;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> interfaceClass,
			String serverAddress) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class<?>[] { interfaceClass }, new JdkDynamicRpcProxy(
						serverAddress));
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> interfaceClass, Registry registry) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class<?>[] { interfaceClass }, new JdkDynamicRpcProxy(
						registry));
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> interfaceClass, Registry registry,
			RpcRespSerialize rpcRespSerialize) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class<?>[] { interfaceClass }, new JdkDynamicRpcProxy(
						registry, rpcRespSerialize));
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> interfaceClass,
			String serverAddress, RpcRespSerialize rpcRespSerialize) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class<?>[] { interfaceClass }, new JdkDynamicRpcProxy(
						serverAddress, rpcRespSerialize));
	}

	public static void main(String[] args) {
		HelloWorld helloWorld = JdkDynamicRpcProxy.createProxy(
				HelloWorld.class, "127.0.0.1:8888");
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			System.out.println(helloWorld.sayHello("wtw:" + i));
		}
		System.out.println(System.currentTimeMillis() - t1);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// TODO Auto-generated method stub
		RpcRequest rpcRequest = new RpcRequest();
		rpcRequest.setSeq(IdGenerator.createObjectIdHex());
		rpcRequest.setInterfaceName(method.getDeclaringClass().getName());
		rpcRequest.setMethod(method.getName());
		rpcRequest.setParams(args);
		return rpc(registry, serverAddress, rpcRespSerialize, rpcRequest, retryTimes);
	}
}
