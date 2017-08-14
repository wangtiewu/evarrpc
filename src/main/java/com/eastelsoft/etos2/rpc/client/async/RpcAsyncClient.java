package com.eastelsoft.etos2.rpc.client.async;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.eastelsoft.etos2.rpc.ErrorCode;
import com.eastelsoft.etos2.rpc.RpcRequest;
import com.eastelsoft.etos2.rpc.RpcResponse;
import com.eastelsoft.etos2.rpc.serialize.RpcRespSerialize;
import com.eastelsoft.etos2.rpc.serialize.jdknative.JdkNativeRpcRespSerialize;
import com.eastelsoft.etos2.rpc.serialize.protostuff.ProtostuffRpcRespSerialize;

/**
 * 基于netty的异步rpc客户端
 * 
 * @author Eastelsoft
 *
 */
public class RpcAsyncClient {
	static final ConcurrentHashMap<String, NettyRpcClient> clients = new ConcurrentHashMap<String, NettyRpcClient>();
	String url;
	NettyRpcClient client;

	public RpcAsyncClient(String host, int port) {
		this(host, port, new JdkNativeRpcRespSerialize());
	}

	public RpcAsyncClient(String host, int port,
			RpcRespSerialize rpcRespSerialize) {
		url = "rpc://" + host + ":" + port;
		NettyRpcClient client1 = clients.get(rpcRespSerialize
				.getSerializeType());
		if (client1 == null) {
			synchronized (clients) {
				client1 = clients.get(rpcRespSerialize.getSerializeType());
				if (client1 == null) {
					client1 = new NettyRpcClient.ConfigBuilder()
							.maxIdleTimeInMilliSecondes(200 * 1000)
							.connectTimeOutInMilliSecondes(20 * 1000)
							.enableShutdownHook(
									"true".equals(System
											.getProperty("netty.asyncclient.shotdown")))
							.rpcRespSerialize(rpcRespSerialize).build();
					clients.put(rpcRespSerialize.getSerializeType(), client1);
				}
			}
		}
		this.client = client1;
	}

	public NettyRpcResponseFuture send1(RpcRequest rpcRequest) {
		final NettyRpcRequest request = new NettyRpcRequest().uri(url).content(
				rpcRequest);
		NettyRpcResponseFuture responseFuture = null;
		try {
			responseFuture = client.send(request);
			return responseFuture;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public RpcResponse send(RpcRequest rpcRequest) {
		final NettyRpcRequest request = new NettyRpcRequest().uri(url).content(
				rpcRequest);
		NettyRpcResponseFuture responseFuture = null;
		try {
			responseFuture = client.send(request);
			NettyRpcResponse nettyRpcResponse = responseFuture.get();
			if (nettyRpcResponse.isSuccess()) {
				return nettyRpcResponse.getRpcResponse();
			} else {
				return new RpcResponse(ErrorCode.ECODE_INTF_ERROR, nettyRpcResponse.getCause()
						.getMessage(), nettyRpcResponse.getCause());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new RpcResponse(ErrorCode.ECODE_SERVICE_EXCEPTION, e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		String host = "127.0.0.1";
		int port = 8888;
		int count = 1000000;
		final CountDownLatch finished = new CountDownLatch(count);
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			RpcAsyncClient rpcClient = new RpcAsyncClient(host, port,
					new ProtostuffRpcRespSerialize());
			RpcRequest rpcRequest = new RpcRequest();
			rpcRequest.setSeq("" + i);
			rpcRequest
					.setInterfaceName("com.eastelsoft.etos2.rpc.example.HelloWorld");
			rpcRequest.setMethod("sayHello");
			rpcRequest.setParams(new String[] { "hello, world" });
			rpcRequest.setTxId("txId");
			rpcRequest.setServiceType("netty");
			rpcRequest.setSpanId("xxx");
			// RpcResponse rpcReposne = rpcClient.send(rpcRequest);
			NettyRpcResponseFuture futrue = rpcClient.send1(rpcRequest);
			futrue.addCallback(new NettyRpcResponseCallback() {

				@Override
				public void onComplete(
						NettyRpcResponseFuture nettyRpcResponseFuture) {
					// TODO Auto-generated method stub
					finished.countDown();
				}
			});
			// System.out.println(rpcReposne);
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
