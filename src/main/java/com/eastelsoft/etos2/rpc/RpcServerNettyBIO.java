package com.eastelsoft.etos2.rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.eastelsoft.etos2.rpc.Consts.SerializeType;
import com.eastelsoft.etos2.rpc.serialize.RpcReqSerialize;

public class RpcServerNettyBIO extends AbstractRpcServer {
	private static Logger logger = LoggerFactory
			.getLogger(RpcServerNettyBIO.class);
	EventLoopGroup boss = null;
	EventLoopGroup worker = null;

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		boss = new OioEventLoopGroup();
		worker = new OioEventLoopGroup(workerThreadCount);
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
		bootstrap.group(boss, worker).channel(OioServerSocketChannel.class)
				.childHandler(new RpcReqInitializer(this, rpcReqSerialize))
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.bind(host, port).sync();
		logger.info("Netty RPC bioserver start success，ip: {} port: {}，workerThreadCount: {}", host,
				port, workerThreadCount);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
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

	@Override
	public void handleRequest(RpcRequestTask rpcInboundTask) {
		// TODO Auto-generated method stub
		rpcInboundTask.setHandler(findHandler(rpcInboundTask.getRpcRequest().getInterfaceName()));
		rpcInboundTask.run();
	}

	public static void main(String[] args) {
		RpcServer rpcServer = new RpcServerNettyBIO();
		try {
			rpcServer.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
