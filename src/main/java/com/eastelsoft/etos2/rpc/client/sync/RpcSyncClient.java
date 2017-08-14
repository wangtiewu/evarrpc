package com.eastelsoft.etos2.rpc.client.sync;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.eastelsoft.etos2.rpc.RpcRequest;
import com.eastelsoft.etos2.rpc.RpcResponse;
import com.eastelsoft.etos2.rpc.serialize.RpcRespSerialize;
import com.eastelsoft.etos2.rpc.serialize.jdknative.JdkNativeRpcRespSerialize;
import com.eastelsoft.etos2.rpc.tool.IdGenerator;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcSyncClient {
	String host;
	int port;
	CountDownLatch finished = new CountDownLatch(1);
	RpcResponse rpcResponse;
	RpcRespSerialize<ChannelPipeline> rpcRespSerialize;
	static ConcurrentHashMap<String, RpcSyncClient> rpcCalls = new ConcurrentHashMap<String, RpcSyncClient>();
	
	public RpcSyncClient(String host, int port) {
		this(host, port, new JdkNativeRpcRespSerialize());
	}
	
	public RpcSyncClient(String host, int port, RpcRespSerialize<ChannelPipeline> rpcRespSerialize) {
		this.host = host;
		this.port = port;
		this.rpcRespSerialize = rpcRespSerialize;
	}

	public static void rpcFinished(RpcResponse rpcResponse) {
		RpcSyncClient rpcClient = rpcCalls.remove(rpcResponse.getSeq());
		if (rpcClient != null) {
			rpcClient.setRpcResponse(rpcResponse);
		}
	}

	public void setRpcResponse(RpcResponse rpcResponse) {
		this.rpcResponse = rpcResponse;
		finished.countDown();
	}

	public RpcResponse send(RpcRequest rpcRequest) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class)
					.handler(new RpcSyncRespInitializer(rpcRespSerialize));
			bootstrap.option(ChannelOption.SO_REUSEADDR, true);
			ChannelFuture future = bootstrap.connect(host, port).sync();
			rpcCalls.put(rpcRequest.getSeq(), this);
			future.channel().writeAndFlush(rpcRequest).sync();
			finished.await();
			return this.rpcResponse;
		} catch (Exception e) {
			e.printStackTrace();
			return new RpcResponse("-1", e.getMessage());
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		String host = "127.0.0.1";
		int port = 8888;

		long t1 = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			RpcSyncClient rpcClient = new RpcSyncClient(host, port);
			RpcRequest rpcRequest = new RpcRequest();
			rpcRequest.setSeq(IdGenerator.createObjectIdHex());
			rpcRequest
					.setInterfaceName("com.eastelsoft.etos2.rpc.example.HelloWorld");
			rpcRequest.setMethod("sayHello");
			rpcRequest.setParams(new String[] { "hello, world" });
			rpcRequest.setTxId("txId");
			rpcRequest.setServiceType("netty");
			rpcRequest.setSpanId("xxx");
			RpcResponse rpcReposne = rpcClient.send(rpcRequest);
//			System.out.println(rpcReposne);
		}
		System.out.println(System.currentTimeMillis()-t1);
	}
}
