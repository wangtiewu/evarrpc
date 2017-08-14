package com.eastelsoft.etos2.rpc.serialize.protostuff;


import java.io.InputStream;
import java.io.OutputStream;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.eastelsoft.etos2.rpc.RpcRequest;
import com.eastelsoft.etos2.rpc.RpcResponse;

public class ProtostuffSerialize {
    private static SchemaCache cachedSchema = SchemaCache.getInstance();
    private static Objenesis objenesis = new ObjenesisStd(true);
    private boolean rpcRequest = true;

    private static <T> Schema<T> getSchema(Class<T> cls) {
        return (Schema<T>) cachedSchema.get(cls);
    }

    public Object deserialize(InputStream input) {
        try {
            Class cls = isRpcRequest() ? RpcRequest.class : RpcResponse.class;
            Object message = (Object) objenesis.newInstance(cls);
            Schema<Object> schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(input, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void serialize(OutputStream output, Object object) {
        Class cls = (Class) object.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema schema = getSchema(cls);
            ProtostuffIOUtil.writeTo(output, object, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

	public boolean isRpcRequest() {
		return rpcRequest;
	}

	public void setRpcRequest(boolean rpcRequest) {
		this.rpcRequest = rpcRequest;
	}
    
}

