package com.eastelsoft.etos2.rpc.client.async;

import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.eastelsoft.etos2.rpc.client.async.pool.NettyRpcChannelPool;
import com.eastelsoft.etos2.rpc.serialize.RpcRespSerialize;


/**
 */
public class NettyRpcClient {

	private NettyRpcChannelPool channelPool;

	private ConfigBuilder configBuilder;

	private NettyRpcClient(ConfigBuilder configBuilder) {
		this.configBuilder = configBuilder;
		this.channelPool = new NettyRpcChannelPool(
				configBuilder.getMaxPerRoute(),
				configBuilder.getConnectTimeOutInMilliSecondes(),
				configBuilder.getMaxIdleTimeInMilliSecondes(),
				configBuilder.getForbidForceConnect(),
				configBuilder.getOptions(), configBuilder.getGroup(), configBuilder.getRpcRespSerialize());
		if (configBuilder.isEnableShutdownHook()) {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						channelPool.close();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, "NettyRpcClientShutdownHook"));
		}
	}

	public NettyRpcResponseFuture send(final NettyRpcRequest request)
			throws Exception {
		InetSocketAddress route = new InetSocketAddress(request.getUri()
				.getHost(), (request.getUri().getPort() < 1 ? 80 : request
				.getUri().getPort()));
		final NettyRpcResponseFuture resp = channelPool.sendRequest(route,
				request.getContent());
		return resp;
	}

	public NettyRpcResponseFuture send(final NettyRpcRequest request,
			NettyRpcResponseCallback callback) throws Exception {
		InetSocketAddress route = new InetSocketAddress(request.getUri()
				.getHost(), (request.getUri().getPort() < 1 ? 80 : request
				.getUri().getPort()));
		final NettyRpcResponseFuture resp = channelPool.sendRequest(route,
				request.getContent(), callback);
		return resp;
	}

	public void close() throws InterruptedException {
		channelPool.close();
	}

	public ConfigBuilder getConfigBuilder() {
		return configBuilder;
	}

	public void setConfigBuilder(ConfigBuilder configBuilder) {
		this.configBuilder = configBuilder;
	}

	public static final class ConfigBuilder {
		@SuppressWarnings("unchecked")
		private Map<ChannelOption, Object> options = new HashMap<ChannelOption, Object>();

		// max idle time for a channel before close
		private int maxIdleTimeInMilliSecondes;

		// max time wait for a channel return from pool
		private int connectTimeOutInMilliSecondes;

		/**
		 * value is false indicates that when there is not any channel in pool
		 * and no new channel allowed to be create based on maxPerRoute, a new
		 * channel will be forced to create.Otherwise, a
		 * <code>TimeoutException</code> will be thrown value is false.
		 */
		private boolean forbidForceConnect = false;

		// max number of channels allow to be created per route
		private Map<String, Integer> maxPerRoute;

		private EventLoopGroup customGroup;

		private boolean enableShutdownHook = false;
		
		private RpcRespSerialize<ChannelPipeline> rpcRespSerialize;

		public ConfigBuilder() {
		}

		public NettyRpcClient build() {
			return new NettyRpcClient(this);
		}

		public ConfigBuilder maxPerRoute(Map<String, Integer> maxPerRoute) {
			this.maxPerRoute = maxPerRoute;
			return this;
		}

		public ConfigBuilder connectTimeOutInMilliSecondes(
				int connectTimeOutInMilliSecondes) {
			this.connectTimeOutInMilliSecondes = connectTimeOutInMilliSecondes;
			return this;
		}

		@SuppressWarnings("unchecked")
		public ConfigBuilder option(ChannelOption key, Object value) {
			options.put(key, value);
			return this;
		}

		public ConfigBuilder maxIdleTimeInMilliSecondes(
				int maxIdleTimeInMilliSecondes) {
			this.maxIdleTimeInMilliSecondes = maxIdleTimeInMilliSecondes;
			return this;
		}

		public ConfigBuilder customGroup(EventLoopGroup customGroup) {
			this.customGroup = customGroup;
			return this;
		}

		public ConfigBuilder forbidForceConnect(boolean forbidForceConnect) {
			this.forbidForceConnect = forbidForceConnect;
			return this;
		}

		public ConfigBuilder enableShutdownHook(boolean enable) {
			this.enableShutdownHook = enable;
			return this;
		}
		
		public ConfigBuilder rpcRespSerialize(RpcRespSerialize<ChannelPipeline> rpcRespSerialize) {
			this.rpcRespSerialize = rpcRespSerialize;
			return this;
		}

		@SuppressWarnings("unchecked")
		public Map<ChannelOption, Object> getOptions() {
			return options;
		}

		public int getMaxIdleTimeInMilliSecondes() {
			return maxIdleTimeInMilliSecondes;
		}

		public Map<String, Integer> getMaxPerRoute() {
			return maxPerRoute;
		}

		public int getConnectTimeOutInMilliSecondes() {
			return connectTimeOutInMilliSecondes;
		}

		public EventLoopGroup getGroup() {
			return this.customGroup;
		}

		public boolean getForbidForceConnect() {
			return this.forbidForceConnect;
		}

		public boolean isEnableShutdownHook() {
			return enableShutdownHook;
		}

		public RpcRespSerialize<ChannelPipeline> getRpcRespSerialize() {
			return rpcRespSerialize;
		}
		

	}

}
