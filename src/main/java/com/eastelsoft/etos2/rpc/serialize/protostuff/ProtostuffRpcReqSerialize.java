package com.eastelsoft.etos2.rpc.serialize.protostuff;

import io.netty.channel.ChannelPipeline;

import com.eastelsoft.etos2.rpc.serialize.RpcReqSerialize;

public class ProtostuffRpcReqSerialize implements
		RpcReqSerialize<ChannelPipeline> {

	public void initChannel(ChannelPipeline pipeline) throws Exception {
		// TODO Auto-generated method stub
		 pipeline.addLast(new ProtostuffEncoder());
	     pipeline.addLast(new ProtostuffDecoder());
	}

}
