package com.eastelsoft.etos2.rpc.client.async.util;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eastelsoft.etos2.rpc.RpcResponse;
import com.eastelsoft.etos2.rpc.client.async.NettyRpcResponseFuture;

/**
 */
public class NettyRpcResponseFutureUtil {
	private static Logger logger = LoggerFactory.getLogger(NettyRpcResponseFutureUtil.class);
	private static final AttributeKey<Object> DEFAULT_ATTRIBUTE = AttributeKey
			.valueOf("nettyRcpResponse");

	private static final AttributeKey<Object> ROUTE_ATTRIBUTE = AttributeKey
			.valueOf("route");

	private static final AttributeKey<Object> FORCE_CONNECT_ATTRIBUTE = AttributeKey
			.valueOf("forceConnect");

	private static final AttributeKey<Object> SOCKET_EXCEPTION_ATTRIBUTE = AttributeKey
			.valueOf("socketException");

	public static void attributeForceConnect(Channel channel,
			boolean forceConnect) {
		if (forceConnect) {
			channel.attr(FORCE_CONNECT_ATTRIBUTE).set(true);
		}
	}

	public static void attributeSocketException(Channel channel,
			Throwable throwable) {
		channel.attr(SOCKET_EXCEPTION_ATTRIBUTE).set(throwable);
	}

	public static Throwable getSocketException(Channel channel) {
		return (Throwable) (channel.attr(SOCKET_EXCEPTION_ATTRIBUTE).get());
	}

	public static void attributeResponse(Channel channel,
			NettyRpcResponseFuture responseFuture) {
		channel.attr(DEFAULT_ATTRIBUTE).set(responseFuture);
		responseFuture.setChannel(channel);
	}

	public static void attributeRoute(Channel channel, InetSocketAddress route) {
		channel.attr(ROUTE_ATTRIBUTE).set(route);
	}

	public static NettyRpcResponseFuture getResponse(Channel channel) {
		return (NettyRpcResponseFuture) channel.attr(DEFAULT_ATTRIBUTE).get();
	}

	public static InetSocketAddress getRoute(Channel channel) {
		return (InetSocketAddress) channel.attr(ROUTE_ATTRIBUTE).get();
	}

	public static boolean getForceConnect(Channel channel) {
		Object forceConnect = channel.attr(FORCE_CONNECT_ATTRIBUTE).get();
		if (null == forceConnect) {
			return false;
		}
		return true;
	}

	public static void setPendingResponse(Channel channel,
			RpcResponse rpcResponse) {
		NettyRpcResponseFuture responseFuture = getResponse(channel);
		NettyRpcResponseBuilder responseBuilder = new NettyRpcResponseBuilder();
		responseBuilder.setSuccess(true);
		responseBuilder.setRpcResponse(rpcResponse);
		responseFuture.setResponseBuilder(responseBuilder);
	}

	public static boolean done(Channel channel) {
		NettyRpcResponseFuture responseFuture = getResponse(channel);
		channel.attr(DEFAULT_ATTRIBUTE).set(null);
		if (null != responseFuture) {
			return responseFuture.done();
		} else {
			logger.warn("NettyRpcResponseFuture not found: "
					+ channel.localAddress());
		}
		return true;
	}

	public static boolean cancel(Channel channel, Throwable cause) {
		NettyRpcResponseFuture responseFuture = getResponse(channel);
		if (responseFuture != null) {
			return responseFuture.cancel(cause);
		}
		return true;
	}
}
