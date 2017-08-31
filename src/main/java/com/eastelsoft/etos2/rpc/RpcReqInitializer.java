package com.eastelsoft.etos2.rpc;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

import com.eastelsoft.etos2.rpc.serialize.RpcReqSerialize;

public class RpcReqInitializer extends ChannelInitializer<Channel> {
	private RpcServer rpcServer;
	private RpcReqSerialize<ChannelPipeline> rpcReqSerialize;

	public RpcReqInitializer(RpcServer rpcServer, 
			RpcReqSerialize<ChannelPipeline> rpcReqSerialize) {
		this.rpcServer = rpcServer;
		this.rpcReqSerialize = rpcReqSerialize;
	}

	@Override
	protected void initChannel(Channel arg0) throws Exception {
		// TODO Auto-generated method stub
		ChannelPipeline pipeline = arg0.pipeline();
		rpcReqSerialize.initChannel(pipeline);
		pipeline.addLast(new RpcReqHandler(rpcServer));
	}

}
