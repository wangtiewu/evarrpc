package com.eastelsoft.etos2.rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.eastelsoft.etos2.rpc.Consts.SerializeType;
import com.eastelsoft.etos2.rpc.serialize.RpcReqSerialize;

public class RpcServer {
	private static Logger logger = LoggerFactory.getLogger(RpcServer.class);
	private String host;
	private int port;
	private String serializeType;// 序列化类型
	private int workerThreadCount = Runtime.getRuntime().availableProcessors();
	private static ConcurrentHashMap<String, Class> serializeClasses = new ConcurrentHashMap<String, Class>();
	static {
		serializeClasses
				.put(SerializeType.JDK_NATIVE.value(),
						com.eastelsoft.etos2.rpc.serialize.jdknative.JdkNativeRpcReqSerialize.class);
		serializeClasses
				.put(SerializeType.PROTOSTUFF.value(),
						com.eastelsoft.etos2.rpc.serialize.protostuff.ProtostuffRpcReqSerialize.class);
	}
	EventLoopGroup boss = null;
	EventLoopGroup worker = null;

	public RpcServer() {
	}

	public void init() throws InterruptedException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		boss = new NioEventLoopGroup();
		worker = new NioEventLoopGroup(workerThreadCount);
		ServerBootstrap bootstrap = new ServerBootstrap();
		RpcReqSerialize rpcReqSerialize = null;
		if (StringUtils.isEmpty(serializeType)) {
			serializeType = SerializeType.JDK_NATIVE.value();
		}
		Class myRpcReqSerializeClass = serializeClasses.get(serializeType);
		if (StringUtils.isEmpty(myRpcReqSerializeClass)) {
			myRpcReqSerializeClass = Class.forName(serializeType);
			serializeClasses.put(serializeType, myRpcReqSerializeClass);
		}
		rpcReqSerialize = (RpcReqSerialize) myRpcReqSerializeClass
				.newInstance();
		bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
				.childHandler(new RpcReqInitializer(rpcReqSerialize))
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.bind(host, port).sync();
		logger.info("Netty RPC server start success，ip: {} port: {}", host,
				port);
	}

	public void destroy() {
		if (worker != null) {
			worker.shutdownGracefully();
			worker = null;
		}
		if (boss != null) {
			boss.shutdownGracefully();
			boss = null;
		}
		logger.info("Netty RPC server stoped，ip:{} port:{}", host, port);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSerializeType() {
		return serializeType;
	}

	public void setSerializeType(String serializeType) {
		this.serializeType = serializeType;
	}

	public int getWorkerThreadCount() {
		return workerThreadCount;
	}

	public void setWorkerThreadCount(int workerThreadCount) {
		this.workerThreadCount = workerThreadCount;
	}
}
