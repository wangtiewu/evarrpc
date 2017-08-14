package com.eastelsoft.etos2.rpc.serialize.protostuff;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ProtostuffDecoder extends ByteToMessageDecoder {
	private static final int LENGTH_FIELD_LEN = 4;
	private boolean rpcRequest = true;
	
    public ProtostuffDecoder() {
        super();
    }
    
    public ProtostuffDecoder(boolean rpcRequest) {
        super();
        this.rpcRequest = rpcRequest;
    }

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		if (in.readableBytes() < LENGTH_FIELD_LEN) {
            return;
        }
        in.markReaderIndex();
        int messageLength = in.readInt();
        if (messageLength < 0) {
            ctx.close();
            return;
        }
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        } else {
            byte[] messageBody = new byte[messageLength];
            in.readBytes(messageBody);
            try {
                Object obj = ProtostuffCodecUtil.decode(messageBody, rpcRequest);
                out.add(obj);
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
	}
}

