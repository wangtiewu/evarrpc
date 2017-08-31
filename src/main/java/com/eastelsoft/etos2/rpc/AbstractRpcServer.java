package com.eastelsoft.etos2.rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.eastelsoft.etos2.rpc.Consts.SerializeType;

public abstract class AbstractRpcServer implements RpcServer {
	protected String host = "0.0.0.0";
	protected int port = 8080;
	protected String serializeType;// 序列化类型
	protected int workerThreadCount = Math.max(4, Runtime.getRuntime()
			.availableProcessors());
	protected boolean useNativeEpoll = false;// 在NIO模式下使用
	protected String userThreadPoolType = "";// 业务线程类型，在NIO模式下使用，为空表示不启用
	protected int userThreadPoolSize = 0;// 用户线程池数量，在NIO模式下使用，默认为IOWorkerThread+1
	protected Map<String, Object> handlers = new HashMap<String, Object>();
	protected static ConcurrentHashMap<String, Class> serializeClasses = new ConcurrentHashMap<String, Class>();
	static {
		serializeClasses
				.put(SerializeType.JDK_NATIVE.value(),
						com.eastelsoft.etos2.rpc.serialize.jdknative.JdkNativeRpcReqSerialize.class);
		serializeClasses
				.put(SerializeType.PROTOSTUFF.value(),
						com.eastelsoft.etos2.rpc.serialize.protostuff.ProtostuffRpcReqSerialize.class);
		serializeClasses
				.put(SerializeType.PROTOBUF.value(),
						com.eastelsoft.etos2.rpc.serialize.protobuf.ProtobufRpcReqSerialize.class);
	}

	public AbstractRpcServer() {
	}

	@Override
	public void setHost(String host) {
		// TODO Auto-generated method stub
		this.host = host;
	}

	@Override
	public String getHost() {
		// TODO Auto-generated method stub
		return this.host;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return this.port;
	}

	@Override
	public void setPort(int port) {
		// TODO Auto-generated method stub
		this.port = port;
	}

	@Override
	public String getSerializeType() {
		// TODO Auto-generated method stub
		return this.serializeType;
	}

	@Override
	public void setSerializeType(String serializeType) {
		// TODO Auto-generated method stub
		this.serializeType = serializeType;
	}

	@Override
	public int getWorkerThreadCount() {
		// TODO Auto-generated method stub
		return this.workerThreadCount;
	}

	@Override
	public void setWorkerThreadCount(int workerThreadCount) {
		// TODO Auto-generated method stub
		this.workerThreadCount = workerThreadCount;
	}

	public void registerHandler(String interfaceName, Object handler) {
		handlers.put(interfaceName, handler);
	}

	public Object findHandler(String interfaceName) {
		return handlers.get(interfaceName);
	}

	public boolean isUseNativeEpoll() {
		return useNativeEpoll;
	}

	public void setUseNativeEpoll(boolean useNativeEpoll) {
		this.useNativeEpoll = useNativeEpoll;
	}

	public String getUserThreadPoolType() {
		return userThreadPoolType;
	}

	public void setUserThreadPoolType(String userThreadPoolType) {
		this.userThreadPoolType = userThreadPoolType;
	}

	public int getUserThreadPoolSize() {
		return userThreadPoolSize;
	}

	public void setUserThreadPoolSize(int userThreadPoolSize) {
		this.userThreadPoolSize = userThreadPoolSize;
	}
	
}
