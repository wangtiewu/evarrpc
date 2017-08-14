package com.eastelsoft.etos2.rpc.serialize.jdknative;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import com.eastelsoft.etos2.rpc.serialize.RpcReqSerialize;

public class JdkNativeRpcReqSerialize implements
		RpcReqSerialize<ChannelPipeline> {
	private static final int MAX_LEN = 1024 * 1024 * 8;
	private static final int LENGTH_FIELD_LEN = 4;

	public void initChannel(ChannelPipeline pipeline) throws Exception {
		// TODO Auto-generated method stub
		pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_LEN, 0,
				LENGTH_FIELD_LEN, 0, LENGTH_FIELD_LEN));
		pipeline.addLast(new LengthFieldPrepender(LENGTH_FIELD_LEN));
		pipeline.addLast(new ObjectEncoder());
		pipeline.addLast(new ObjectDecoder(MAX_LEN,
				ClassResolvers.weakCachingConcurrentResolver(this.getClass()
						.getClassLoader())));
		pipeline.addLast(new ObjectEncoder());
	}

}
