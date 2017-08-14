package com.eastelsoft.etos2.rpc.serialize.protostuff;

import com.google.common.io.Closer;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProtostuffCodecUtil{
    private static ProtostuffSerializePool pool = ProtostuffSerializePool.getProtostuffPoolInstance();

	public static void encode(final ByteBuf out, final Object message) throws IOException {
		Closer closer = Closer.create();
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closer.register(byteArrayOutputStream);
            ProtostuffSerialize protostuffSerialization = pool.borrow();
            protostuffSerialization.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
            pool.restore(protostuffSerialization);
        } finally {
            closer.close();
        }
    }

    public static Object decode(byte[] body, boolean rpcRequest) throws IOException {
    	Closer closer = Closer.create();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            closer.register(byteArrayInputStream);
            ProtostuffSerialize protostuffSerialization = pool.borrow();
            protostuffSerialization.setRpcRequest(rpcRequest);
            Object obj = protostuffSerialization.deserialize(byteArrayInputStream);
            pool.restore(protostuffSerialization);
            return obj;
        } finally {
            closer.close();
        }
    }
}

