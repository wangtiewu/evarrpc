package com.eastelsoft.etos2.rpc.serialize.protostuff;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProtostuffEncoder extends MessageToByteEncoder<Object> {

    public ProtostuffEncoder() {
    }

	@Override
	protected void encode(ChannelHandlerContext arg0, Object arg1, ByteBuf arg2)
			throws Exception {
		// TODO Auto-generated method stub
		ProtostuffCodecUtil.encode(arg2, arg1);
	}
}

