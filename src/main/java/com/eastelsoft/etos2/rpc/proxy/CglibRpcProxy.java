package com.eastelsoft.etos2.rpc.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.springframework.util.StringUtils;

import com.eastelsoft.etos2.rpc.RpcRequest;
import com.eastelsoft.etos2.rpc.RpcResponse;
import com.eastelsoft.etos2.rpc.client.async.RpcAsyncClient;
import com.eastelsoft.etos2.rpc.example.HelloWorld;
import com.eastelsoft.etos2.rpc.registry.Provider;
import com.eastelsoft.etos2.rpc.registry.Registry;
import com.eastelsoft.etos2.rpc.serialize.RpcRespSerialize;
import com.eastelsoft.etos2.rpc.tool.IdGenerator;


public class CglibRpcProxy extends RpcProxy implements MethodInterceptor {
	private Registry registry;
	private String serverAddress;
	private String interfaceName;
	private RpcRespSerialize rpcRespSerialize;
	private int retryTimes = 3;// rpc 调用重试次数

	public CglibRpcProxy() {
	}

	public CglibRpcProxy(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public CglibRpcProxy(String serverAddress, String interfaceName) {
		this.serverAddress = serverAddress;
		this.interfaceName = interfaceName;
	}
	
	public CglibRpcProxy(Registry registry, String interfaceName) {
		this.registry = registry;
		this.interfaceName = interfaceName;
	}

	public CglibRpcProxy(String serverAddress, String interfaceName,
			RpcRespSerialize rpcRespSerialize) {
		this.serverAddress = serverAddress;
		this.interfaceName = interfaceName;
		this.rpcRespSerialize = rpcRespSerialize;
	}
	
	public CglibRpcProxy(Registry registry, String interfaceName,
			RpcRespSerialize rpcRespSerialize) {
		this.registry = registry;
		this.interfaceName = interfaceName;
		this.rpcRespSerialize = rpcRespSerialize;
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> interfaceClass,
			String serverAddress) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(interfaceClass);
		enhancer.setCallback(new CglibRpcProxy(serverAddress));
		return (T) enhancer.create();
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> interfaceClass,
			Registry registry, String interfaceName) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(interfaceClass);
		enhancer.setCallback(new CglibRpcProxy(registry, interfaceName));
		return (T) enhancer.create();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> interfaceClass,
			String serverAddress, String interfaceName) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(interfaceClass);
		enhancer.setCallback(new CglibRpcProxy(serverAddress, interfaceName));
		return (T) enhancer.create();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> interfaceClass,
			String serverAddress, String interfaceName, RpcRespSerialize rpcRespSerialize) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(interfaceClass);
		enhancer.setCallback(new CglibRpcProxy(serverAddress, interfaceName, rpcRespSerialize));
		return (T) enhancer.create();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> interfaceClass,
			Registry registry, String interfaceName, RpcRespSerialize rpcRespSerialize) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(interfaceClass);
		enhancer.setCallback(new CglibRpcProxy(registry, interfaceName, rpcRespSerialize));
		return (T) enhancer.create();
	}

	@Override
	public Object intercept(Object arg0, Method arg1, Object[] arg2,
			MethodProxy arg3) throws Throwable {
		// TODO Auto-generated method stub
		RpcRequest rpcRequest = new RpcRequest();
		rpcRequest.setSeq(IdGenerator.createObjectIdHex());
		Class clazz = arg1.getDeclaringClass();
		if (clazz.isInterface()) {
			rpcRequest.setInterfaceName(arg1.getDeclaringClass().getName());
		} else if (clazz.getInterfaces() == null
				|| clazz.getInterfaces().length < 1) {
			if (StringUtils.isEmpty(this.interfaceName)) {
				rpcRequest.setInterfaceName(arg1.getDeclaringClass().getName());
			} else {
				rpcRequest.setInterfaceName(this.interfaceName);
			}
		} else if (clazz.getInterfaces().length == 1) {
			rpcRequest.setInterfaceName(clazz.getInterfaces()[0].getName());
		} else {
			if (StringUtils.isEmpty(this.interfaceName)) {
				rpcRequest.setInterfaceName(clazz.getInterfaces()[0].getName());
			} else {
				rpcRequest.setInterfaceName(this.interfaceName);
			}
		}
		rpcRequest.setMethod(arg1.getName());
		rpcRequest.setParams(arg2);
		return rpc(registry, serverAddress, rpcRespSerialize, rpcRequest, retryTimes);
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public static void main(String[] args) {
		HelloWorld helloWorld = CglibRpcProxy.createProxy(HelloWorld.class,
				"127.0.0.1:8888");
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			System.out.println(helloWorld.sayHello("wtw:" + i));
		}
		System.out.println(System.currentTimeMillis() - t1);
	}

}
