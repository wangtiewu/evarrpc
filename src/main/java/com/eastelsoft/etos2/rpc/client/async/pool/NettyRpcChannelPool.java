package com.eastelsoft.etos2.rpc.client.async.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eastelsoft.etos2.rpc.RpcRequest;
import com.eastelsoft.etos2.rpc.client.async.NettyRpcResponseCallback;
import com.eastelsoft.etos2.rpc.client.async.NettyRpcResponseFuture;
import com.eastelsoft.etos2.rpc.client.async.handler.NettyRpcChannelPoolHandler;
import com.eastelsoft.etos2.rpc.client.async.util.NettyRpcResponseFutureUtil;
import com.eastelsoft.etos2.rpc.serialize.RpcRespSerialize;

/**
 * 
 * @author Eastelsoft
 *
 */
public class NettyRpcChannelPool {
	private static final Logger logger = LoggerFactory
			.getLogger(NettyRpcChannelPool.class.getName());

	// channel pools per route
	private ConcurrentMap<String, LinkedBlockingQueue<Channel>> routeToPoolChannels;

	// max number of channels allow to be created per route
	private ConcurrentMap<String, Semaphore> maxPerRoute;

	// max time wait for a channel return from pool
	private int connectTimeOutInMilliSecondes;

	// max idle time for a channel before close
	private int maxIdleTimeInMilliSecondes;

	/**
	 * value is false indicates that when there is not any channel in pool and
	 * no new channel allowed to be create based on maxPerRoute, a new channel
	 * will be forced to create.Otherwise, a <code>TimeoutException</code> will
	 * be thrown
	 * */
	private boolean forbidForceConnect;

	// default max number of channels allow to be created per route
	private final static int DEFAULT_MAX_PER_ROUTE = 30;

	private EventLoopGroup group;

	private final Bootstrap clientBootstrap;

	private static final String COLON = ":";

	private RpcRespSerialize<ChannelPipeline> rpcOutboundSerialize;

	/**
	 * Create a new instance of ChannelPool
	 * 
	 * @param maxPerRoute
	 *            max number of channels per route allowed in pool
	 * @param connectTimeOutInMilliSecondes
	 *            max time wait for a channel return from pool
	 * @param maxIdleTimeInMilliSecondes
	 *            max idle time for a channel before close
	 * @param forbidForceConnect
	 *            value is false indicates that when there is not any channel in
	 *            pool and no new channel allowed to be create based on
	 *            maxPerRoute, a new channel will be forced to create.Otherwise,
	 *            a <code>TimeoutException</code> will be thrown. The default
	 *            value is false.
	 * @param additionalChannelInitializer
	 *            user-defined initializer
	 * @param options
	 *            user-defined options
	 * @param customGroup
	 *            user defined {@link EventLoopGroup}
	 */
	@SuppressWarnings("unchecked")
	public NettyRpcChannelPool(Map<String, Integer> maxPerRoute,
			int connectTimeOutInMilliSecondes, int maxIdleTimeInMilliSecondes,
			boolean forbidForceConnect, Map<ChannelOption, Object> options,
			EventLoopGroup customGroup,
			RpcRespSerialize<ChannelPipeline> rpcOutboundSerialize) {
		this.maxIdleTimeInMilliSecondes = maxIdleTimeInMilliSecondes;
		this.connectTimeOutInMilliSecondes = connectTimeOutInMilliSecondes;
		this.maxPerRoute = new ConcurrentHashMap<String, Semaphore>();
		this.routeToPoolChannels = new ConcurrentHashMap<String, LinkedBlockingQueue<Channel>>();
		this.group = null == customGroup ? new NioEventLoopGroup()
				: customGroup;
		this.forbidForceConnect = forbidForceConnect;
		this.rpcOutboundSerialize = rpcOutboundSerialize;
		this.clientBootstrap = new Bootstrap();
		clientBootstrap.group(group).channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.handler(new ChannelInitializer() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						NettyRpcChannelPool.this.rpcOutboundSerialize
								.initChannel(ch.pipeline());
						ch.pipeline()
								.addLast(
										IdleStateHandler.class.getSimpleName(),
										new IdleStateHandler(
												0,
												0,
												NettyRpcChannelPool.this.maxIdleTimeInMilliSecondes,
												TimeUnit.MILLISECONDS));
						ch.pipeline().addLast(
								NettyRpcChannelPoolHandler.class
										.getSimpleName(),
								new NettyRpcChannelPoolHandler(
										NettyRpcChannelPool.this));
					}

				});
		if (null != options) {
			for (Entry<ChannelOption, Object> entry : options.entrySet()) {
				clientBootstrap.option(entry.getKey(), entry.getValue());
			}
		}

		if (null != maxPerRoute) {
			for (Entry<String, Integer> entry : maxPerRoute.entrySet()) {
				this.maxPerRoute.put(entry.getKey(),
						new Semaphore(entry.getValue()));
			}
		}

	}

	/**
	 * send http request to server specified by the route. The channel used to
	 * send the request is obtained according to the follow rules
	 * <p>
	 * 1. poll the first valid channel from pool without waiting. If no valid
	 * channel exists, then go to step 2. 2. create a new channel and return. If
	 * failed to create a new channel, then go to step 3. Note: the new channel
	 * created in this step will be returned to the pool 3. poll the first valid
	 * channel from pool within specified waiting time. If no valid channel
	 * exists and the value of forbidForceConnect is false, then throw
	 * <code>TimeoutException</code>. Otherwise,go to step 4. 4. create a new
	 * channel and return. Note: the new channel created in this step will not
	 * be returned to the pool.
	 * </p>
	 * 
	 * @param route
	 *            target server
	 * @param request
	 *            {@link RpcRequest}
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws IOException
	 * @throws Exception
	 */
	public NettyRpcResponseFuture sendRequest(InetSocketAddress route,
			final RpcRequest request) throws InterruptedException, IOException {
		final NettyRpcResponseFuture responseFuture = new NettyRpcResponseFuture();
		if (sendRequestUsePooledChannel(route, request, responseFuture, false)) {
			return responseFuture;
		}

		if (sendRequestUseNewChannel(route, request, responseFuture,
				forbidForceConnect)) {
			return responseFuture;
		}

		if (sendRequestUsePooledChannel(route, request, responseFuture, true)) {
			return responseFuture;
		}

		throw new IOException(
				"send request failed: obtain channel from pool timeout");
	}

	public NettyRpcResponseFuture sendRequest(InetSocketAddress route,
			final RpcRequest request, NettyRpcResponseCallback callback)
			throws InterruptedException, IOException {
		final NettyRpcResponseFuture responseFuture = new NettyRpcResponseFuture();
		responseFuture.addCallback(callback);
		if (sendRequestUsePooledChannel(route, request, responseFuture, false)) {
			return responseFuture;
		}

		if (sendRequestUseNewChannel(route, request, responseFuture,
				forbidForceConnect)) {
			return responseFuture;
		}

		if (sendRequestUsePooledChannel(route, request, responseFuture, true)) {
			return responseFuture;
		}

		throw new IOException(
				"send request failed: obtain channel from pool timeout");
	}

	/**
	 * return the specified channel to pool
	 * 
	 * @param channel
	 */
	public void returnChannel(Channel channel) {
		if (NettyRpcResponseFutureUtil.getForceConnect(channel)) {
			return;
		}
		InetSocketAddress route = (InetSocketAddress) channel.remoteAddress();
		String key = getKey(route);
		LinkedBlockingQueue<Channel> poolChannels = routeToPoolChannels
				.get(key);

		if (null != channel && channel.isActive()) {
			if (poolChannels.offer(channel)) {
				if (logger.isDebugEnabled()) {
					logger.debug(channel + " returned");
				}
			}
		}
	}

	/**
	 * close all channels in the pool and shut down the eventLoopGroup
	 * 
	 * @throws InterruptedException
	 */
	public void close() throws InterruptedException {
		ChannelGroup channelGroup = new DefaultChannelGroup(
				GlobalEventExecutor.INSTANCE);

		for (LinkedBlockingQueue<Channel> queue : routeToPoolChannels.values()) {
			for (Channel channel : queue) {
				removeChannel(channel, null);
				channelGroup.add(channel);
			}
		}
		channelGroup.close().sync();
		group.shutdownGracefully();
	}

	/**
	 * remove the specified channel from the pool,cancel the responseFuture and
	 * release semaphore for the route
	 * 
	 * @param channel
	 */
	private void removeChannel(Channel channel, Throwable cause) {

		InetSocketAddress route = (InetSocketAddress) channel.remoteAddress();
		String key = getKey(route);

		NettyRpcResponseFutureUtil.cancel(channel, cause);

		if (!NettyRpcResponseFutureUtil.getForceConnect(channel)) {
			LinkedBlockingQueue<Channel> poolChannels = routeToPoolChannels
					.get(key);
			if (poolChannels.remove(channel)) {
				if (logger.isInfoEnabled()) {
					logger.info(channel + " removed");
				}
			}
			getAllowCreatePerRoute(key).release();
		}
	}

	private boolean sendRequestUsePooledChannel(InetSocketAddress route,
			final RpcRequest request, NettyRpcResponseFuture responseFuture,
			boolean isWaiting) throws InterruptedException {
		LinkedBlockingQueue<Channel> poolChannels = getPoolChannels(getKey(route));
		Channel channel = poolChannels.poll();

		while (null != channel && !channel.isActive()) {
			channel = poolChannels.poll();
		}

		if (null == channel || !channel.isActive()) {
			if (!isWaiting) {
				return false;
			}
			channel = poolChannels.poll(connectTimeOutInMilliSecondes,
					TimeUnit.MILLISECONDS);
			if (null == channel || !channel.isActive()) {
				logger.warn("obtain channel from pool timeout");
				return false;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(channel + " reuse");
		}
		NettyRpcResponseFutureUtil.attributeResponse(channel, responseFuture);
		channel.writeAndFlush(request);
		if (logger.isDebugEnabled()) {
			logger.debug(channel + " writeAndFlush");
		}
		return true;
	}

	private boolean sendRequestUseNewChannel(final InetSocketAddress route,
			final RpcRequest request,
			final NettyRpcResponseFuture responseFuture, boolean forceConnect) {
		ChannelFuture future = createChannelFuture(route, forceConnect);
		if (null != future) {
			NettyRpcResponseFutureUtil.attributeResponse(future.channel(),
					responseFuture);
			NettyRpcResponseFutureUtil.attributeRoute(future.channel(), route);
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					if (future.isSuccess()) {
						future.channel().closeFuture()
								.addListener(new ChannelFutureListener() {
									@Override
									public void operationComplete(
											ChannelFuture future)
											throws Exception {
										Throwable cause = NettyRpcResponseFutureUtil
												.getSocketException(future
														.channel());
										if (cause == null) {
											cause = future.cause();
										}
										if (cause != null) {
											logger.error(future.channel()
													+ " closed, exception: "
													+ cause);
										} else {
											if (logger.isDebugEnabled()) {
												logger.debug(future.channel()
														+ " closed, exception: "
														+ future.cause());
											}
										}
										removeChannel(future.channel(), cause);
									}

								});
						future.channel().writeAndFlush(request);
						if (logger.isDebugEnabled()) {
							logger.debug(future.channel() + " writeAndFlush");
						}
					} else {
						logger.error(future.channel()
								+ " connect failed, exception: "
								+ future.cause());

						NettyRpcResponseFutureUtil.cancel(future.channel(),
								future.cause());
						if (!NettyRpcResponseFutureUtil.getForceConnect(future
								.channel())) {
							releaseCreatePerRoute(future.channel());
						}
					}
				}

			});
			return true;
		}
		return false;
	}

	public void releaseCreatePerRoute(Channel channel) {
		InetSocketAddress route = NettyRpcResponseFutureUtil.getRoute(channel);
		getAllowCreatePerRoute(getKey(route)).release();
	}

	private Semaphore getAllowCreatePerRoute(String key) {
		Semaphore allowCreate = maxPerRoute.get(key);
		if (null == allowCreate) {
			Semaphore newAllowCreate = new Semaphore(DEFAULT_MAX_PER_ROUTE);
			allowCreate = maxPerRoute.putIfAbsent(key, newAllowCreate);
			if (null == allowCreate) {
				allowCreate = newAllowCreate;
			}
		}

		return allowCreate;
	}

	private LinkedBlockingQueue<Channel> getPoolChannels(String route) {
		LinkedBlockingQueue<Channel> oldPoolChannels = routeToPoolChannels
				.get(route);
		if (null == oldPoolChannels) {
			LinkedBlockingQueue<Channel> newPoolChannels = new LinkedBlockingQueue<Channel>();
			oldPoolChannels = routeToPoolChannels.putIfAbsent(route,
					newPoolChannels);
			if (null == oldPoolChannels) {
				oldPoolChannels = newPoolChannels;
			}
		}
		return oldPoolChannels;
	}

	private String getKey(InetSocketAddress route) {
		return route.getAddress().getHostAddress() + COLON + route.getPort();
	}

	private ChannelFuture createChannelFuture(InetSocketAddress route,
			boolean forceConnect) {
		String key = getKey(route);

		Semaphore allowCreate = getAllowCreatePerRoute(key);
		if (allowCreate.tryAcquire()) {
			try {
				ChannelFuture connectFuture = clientBootstrap.connect(route
						.getAddress().getHostAddress(), route.getPort());
				return connectFuture;
			} catch (Exception e) {
				logger.error("connect failed", e);
				allowCreate.release();
			}
		}
		if (forceConnect) {
			ChannelFuture connectFuture = clientBootstrap.connect(route
					.getAddress().getHostAddress(), route.getPort());
			if (null != connectFuture) {
				NettyRpcResponseFutureUtil.attributeForceConnect(
						connectFuture.channel(), forceConnect);
			}
			return connectFuture;
		}
		return null;
	}
}
