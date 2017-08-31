package com.eastelsoft.etos2.rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.eastelsoft.etos2.rpc.Consts.SerializeType;
import com.eastelsoft.etos2.rpc.serialize.RpcReqSerialize;

public class RpcServerNettyNIO extends AbstractRpcServer {
	private static Logger logger = LoggerFactory
			.getLogger(RpcServerNettyNIO.class);
	EventLoopGroup boss = null;
	EventLoopGroup worker = null;
	RpcRequestExecutor rpcRequestExecutor = null;
	
	public RpcServerNettyNIO() {
	}

	public void init() throws Exception {
		if (StringUtils.isEmpty(userThreadPoolType)) {
		}
		else if (userThreadPoolType.equals("native")) {
			rpcRequestExecutor = new RpcRequestExecutorJdk(userThreadPoolSize <=0 ? (workerThreadCount + 1) : userThreadPoolSize, 100);
		}
		else if (userThreadPoolType.equals("akka")) {
			rpcRequestExecutor = new RpcRequestExecutorAkka(userThreadPoolSize <=0 ? (workerThreadCount + 1) : userThreadPoolSize);
		}
		else {
			;
		}
		Class channelClass = NioServerSocketChannel.class;
		if (useNativeEpoll) {
			boss = new EpollEventLoopGroup();
			worker = new EpollEventLoopGroup(workerThreadCount);
			channelClass = EpollServerSocketChannel.class;
		}
		else {
			boss = new NioEventLoopGroup();
			worker = new NioEventLoopGroup(workerThreadCount);
		}
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
		bootstrap.group(boss, worker).channel(channelClass)
				.childHandler(new RpcReqInitializer(this, rpcReqSerialize))
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		bootstrap.bind(host, port).sync();
		logger.info("Netty RPC nioserver start success，ip: {} port: {}，workerThreadCount: {}，userThreadPool：{}", host,
				port, workerThreadCount, userThreadPoolType);
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
		if (rpcRequestExecutor != null) {
			rpcRequestExecutor.stop();
			rpcRequestExecutor = null;
		}
		logger.info("Netty RPC server stoped，ip:{} port:{}", host, port);
	}

	@Override
	public void handleRequest(RpcRequestTask rpcInboundTask) {
		// TODO Auto-generated method stub
		rpcInboundTask.setHandler(findHandler(rpcInboundTask.getRpcRequest()
				.getInterfaceName()));
		if (!StringUtils.isEmpty(userThreadPoolType)) {
			rpcRequestExecutor.submit(rpcInboundTask);
		}
		else {
			rpcInboundTask.run();
		}
	}	
}
