package com.eastelsoft.etos2.rpc.serialize.protostuff;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import com.eastelsoft.etos2.rpc.Consts.SerializeType;
import com.eastelsoft.etos2.rpc.serialize.RpcRespSerialize;

public class ProtostuffRpcRespSerialize implements RpcRespSerialize<ChannelPipeline> {
	public void initChannel(ChannelPipeline pipeline) throws Exception {
		// TODO Auto-generated method stub
		 pipeline.addLast(new ProtostuffEncoder());
	     pipeline.addLast(new ProtostuffDecoder(false));
	}

	@Override
	public String getSerializeType() {
		// TODO Auto-generated method stub
		return SerializeType.PROTOSTUFF.value();
	}

}
