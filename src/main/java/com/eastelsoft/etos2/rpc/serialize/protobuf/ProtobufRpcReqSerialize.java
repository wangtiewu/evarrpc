package com.eastelsoft.etos2.rpc.serialize.protobuf;

import io.netty.channel.ChannelPipeline;

import com.eastelsoft.etos2.rpc.serialize.RpcReqSerialize;

public class ProtobufRpcReqSerialize implements
		RpcReqSerialize<ChannelPipeline> {

	public void initChannel(ChannelPipeline pipeline) throws Exception {
		// TODO Auto-generated method stub
		 pipeline.addLast(new ProtobufEncoder());
	     pipeline.addLast(new ProtobufDecoder());
	}

}
