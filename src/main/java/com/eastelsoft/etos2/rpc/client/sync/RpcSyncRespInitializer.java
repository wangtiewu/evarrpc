package com.eastelsoft.etos2.rpc.client.sync;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

import com.eastelsoft.etos2.rpc.serialize.RpcRespSerialize;

public class RpcSyncRespInitializer extends ChannelInitializer<Channel> {
	private RpcRespSerialize<ChannelPipeline> rpcOutboundSerialize;

	public RpcSyncRespInitializer(
			RpcRespSerialize<ChannelPipeline> rpcOutboundSerialize) {
		this.rpcOutboundSerialize = rpcOutboundSerialize;
	}

	@Override
	protected void initChannel(Channel arg0) throws Exception {
		// TODO Auto-generated method stub
		ChannelPipeline pipeline = arg0.pipeline();
		rpcOutboundSerialize.initChannel(pipeline);
		pipeline.addLast(new RpcSyncRespHandler());
	}

}
