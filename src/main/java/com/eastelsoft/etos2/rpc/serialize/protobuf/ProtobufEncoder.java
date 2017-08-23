package com.eastelsoft.etos2.rpc.serialize.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProtobufEncoder extends MessageToByteEncoder<Object> {

    public ProtobufEncoder() {
    }

	@Override
	protected void encode(ChannelHandlerContext arg0, Object arg1, ByteBuf arg2)
			throws Exception {
		// TODO Auto-generated method stub
		ProtobufCodecUtil.encode(arg2, arg1);
	}
}

