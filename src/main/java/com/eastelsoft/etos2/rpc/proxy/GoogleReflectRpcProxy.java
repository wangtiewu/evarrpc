package com.eastelsoft.etos2.rpc.proxy;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import com.eastelsoft.etos2.rpc.RpcRequest;
import com.eastelsoft.etos2.rpc.client.async.RpcAsyncClient;
import com.eastelsoft.etos2.rpc.example.HelloWorld;
import com.eastelsoft.etos2.rpc.tool.IdGenerator;
import com.eastelsoft.etos2.rpc.tool.StringDeal;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Reflection;

/**
 * 谷歌的动态代理
 * 
 * @author Eastelsoft
 *
 * @param <T>
 */
public class GoogleReflectRpcProxy<T> extends AbstractInvocationHandler {
	String serverAddress;

	public GoogleReflectRpcProxy(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> interfaceClass,
			String serverAddress) {
		return (T) Reflection.newProxy(interfaceClass,
				new GoogleReflectRpcProxy<T>(serverAddress));
	}

	@Override
	public Object handleInvocation(Object proxy, Method method, Object[] args)
			throws Throwable {
		RpcRequest rpcRequest = new RpcRequest();
		rpcRequest.setSeq(IdGenerator.createObjectIdHex());
		rpcRequest.setInterfaceName(method.getDeclaringClass().getName());
		rpcRequest.setMethod(method.getName());
		rpcRequest.setParams(args);
		String[] hostAndPort = StringDeal.split(serverAddress, ":");
		RpcAsyncClient rpcClient = new RpcAsyncClient(hostAndPort[0],
				Integer.parseInt(hostAndPort[1]));
		return rpcClient.send(rpcRequest).getData();
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public static void main(String[] args) {
		HelloWorld helloWorld = (HelloWorld) GoogleReflectRpcProxy.createProxy(
				HelloWorld.class, "127.0.0.1:8888");
		long t1 = System.currentTimeMillis();
		int count = 10000;
		CountDownLatch finished = new CountDownLatch(count);
		for (int i = 0; i < count; i++) {
			System.out.println(helloWorld.sayHello("wtw:" + i));
			finished.countDown();
		}
		try {
			finished.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis() - t1);
	}
}
