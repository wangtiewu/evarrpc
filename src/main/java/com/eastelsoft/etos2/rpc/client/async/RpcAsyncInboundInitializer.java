package com.eastelsoft.etos2.rpc.client.async;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

import com.eastelsoft.etos2.rpc.RpcReqHandler;
import com.eastelsoft.etos2.rpc.serialize.RpcReqSerialize;

public class RpcAsyncInboundInitializer extends ChannelInitializer<Channel> {
	private RpcReqSerialize<ChannelPipeline> rpcInboundSerialize;

	public RpcAsyncInboundInitializer(
			RpcReqSerialize<ChannelPipeline> rpcInboundSerialize) {
		this.rpcInboundSerialize = rpcInboundSerialize;
	}

	@Override
	protected void initChannel(Channel arg0) throws Exception {
		// TODO Auto-generated method stub
		ChannelPipeline pipeline = arg0.pipeline();
		rpcInboundSerialize.initChannel(pipeline);
		pipeline.addLast(new RpcReqHandler());
	}

}
