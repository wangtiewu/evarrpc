package com.eastelsoft.etos2.rpc.client.async.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eastelsoft.etos2.rpc.RpcResponse;
import com.eastelsoft.etos2.rpc.client.async.pool.NettyRpcChannelPool;
import com.eastelsoft.etos2.rpc.client.async.util.NettyRpcResponseFutureUtil;


/**
 */
public class NettyRpcChannelPoolHandler extends
		SimpleChannelInboundHandler<RpcResponse> {
	private static final Logger logger = LoggerFactory
			.getLogger(NettyRpcChannelPoolHandler.class.getName());

	private NettyRpcChannelPool channelPool;

	/**
	 * @param channelPool
	 */
	public NettyRpcChannelPoolHandler(NettyRpcChannelPool channelPool) {
		super();
		this.channelPool = channelPool;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg)
			throws Exception {
		NettyRpcResponseFutureUtil.setPendingResponse(ctx.channel(), msg);
		NettyRpcResponseFutureUtil.done(ctx.channel());
		channelPool.returnChannel(ctx.channel());
	}

	/**
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext,
	 *      java.lang.Object)
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
		if (evt instanceof IdleStateEvent) {
			logger.warn("remove idle channel: " + ctx.channel());
			if (NettyRpcResponseFutureUtil.getResponse(ctx.channel()) != null) {
				NettyRpcResponseFutureUtil.attributeSocketException(
						ctx.channel(), new SocketTimeoutException());
			}
			ctx.channel().close();
		} else {
			ctx.fireUserEventTriggered(evt);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		logger.error("连接 " + ctx.channel() + " 发生异常，将被关闭", cause);
		if (NettyRpcResponseFutureUtil.getResponse(ctx.channel()) != null) {
			NettyRpcResponseFutureUtil.attributeSocketException(ctx.channel(),
					cause);
		}
		ctx.close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("连接 " + ctx.channel() + " 已关闭");
	}

	/**
	 * @param channelPool
	 *            the channelPool to set
	 */
	public void setChannelPool(NettyRpcChannelPool channelPool) {
		this.channelPool = channelPool;
	}
}
