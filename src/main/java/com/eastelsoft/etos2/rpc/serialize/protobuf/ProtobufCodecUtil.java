package com.eastelsoft.etos2.rpc.serialize.protobuf;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import sun.reflect.generics.repository.ClassRepository;

import com.eastelsoft.etos2.rpc.RpcRequest;
import com.eastelsoft.etos2.rpc.RpcResponse;
import com.eastelsoft.etos2.rpc.example.HelloWorld;
import com.eastelsoft.etos2.rpc.proxy.MethodInfo;
import com.eastelsoft.etos2.rpc.proxy.MethodInfoCache;
import com.eastelsoft.etos2.rpc.tool.JacksonUtils;
import com.google.common.io.Closer;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public class ProtobufCodecUtil {
	static ProtobufParserOrBuilder pb = new ProtobufParserOrBuilder(new File(
			"evarrpc.proto"));

	public static void encode(final ByteBuf out, final Object message)
			throws IOException {
		if (false) {
			if (message instanceof RpcRequest) {
				Evarrpc.RpcRequest.Builder builder = Evarrpc.RpcRequest
						.newBuilder();
				RpcRequest rpcRequest = (RpcRequest) message;
				builder.setInterfaceName(rpcRequest.getInterfaceName());
				builder.setMethod(rpcRequest.getMethod());
				builder.setSeq(rpcRequest.getSeq());
				byte[] body = builder.build().toByteArray();
				int dataLength = body.length;
				out.writeInt(dataLength);
				out.writeBytes(body);
				return;
			} else {
				Evarrpc.RpcResponse.Builder builder = Evarrpc.RpcResponse
						.newBuilder();
				RpcResponse rpcResponse = (RpcResponse) message;
				builder.setInterfaceName(rpcResponse.getInterfaceName());
				builder.setMethod(rpcResponse.getMethod());
				builder.setSeq(rpcResponse.getSeq());
				builder.setEcode(rpcResponse.getEcode());
				builder.setEmsg(rpcResponse.getEmsg());
				byte[] body = builder.build().toByteArray();
				int dataLength = body.length;
				out.writeInt(dataLength);
				out.writeBytes(body);
				return;
			}
		}
		byte[] body = null;
		try {
			body = pb.encode(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException(e);
		}
		int dataLength = body.length;
		out.writeInt(dataLength);
		out.writeBytes(body);
	}

	public static Object decode(byte[] body, boolean isRpcRequest)
			throws IOException {
		if (false) {
			if (isRpcRequest) {
				Evarrpc.RpcRequest o = Evarrpc.RpcRequest.parseFrom(body);
				RpcRequest rpcRequest = new RpcRequest();
				rpcRequest.setSeq(o.getSeq());
				rpcRequest.setInterfaceName(o.getInterfaceName());
				rpcRequest.setMethod(o.getMethod());
				return rpcRequest;
			} else {
				Evarrpc.RpcResponse o = Evarrpc.RpcResponse.parseFrom(body);
				RpcResponse rpcResponse = new RpcResponse();
				rpcResponse.setSeq(o.getSeq());
				rpcResponse.setInterfaceName(o.getInterfaceName());
				rpcResponse.setMethod(o.getMethod());
				rpcResponse.setEcode(o.getEcode());
				rpcResponse.setEmsg(o.getEmsg());
				return rpcResponse;
			}
		}
		if (isRpcRequest) {
			RpcRequest rpcRequest;
			try {
				rpcRequest = pb.decode(RpcRequest.class, body);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException(e);
			}
			MethodInfo methodInfo = MethodInfoCache.getInstance().get(
					rpcRequest.getInterfaceName(),
					rpcRequest.getMethod(),
					rpcRequest.getParams() == null ? 0
							: rpcRequest.getParams().length);
			Method method = methodInfo.getMethod();
			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes != null && paramTypes.length > 0) {
				Object[] params = rpcRequest.getParams();
				for (int i = 0; i < paramTypes.length; i++) {
					Class<?> paramType = paramTypes[i];
					params[i] = convertParam((String) params[i], paramType,
							method.getGenericReturnType());
				}
			}
			return rpcRequest;
		} else {
			RpcResponse rpcResponse = null;
			try {
				rpcResponse = pb.decode(RpcResponse.class, body);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException(e);
			}
			MethodInfo methodInfo = MethodInfoCache.getInstance().get(
					rpcResponse.getInterfaceName(), rpcResponse.getMethod(), 0);
			Method method = methodInfo.getMethod();
			rpcResponse.setData(convertParam((String) rpcResponse.getData(),
					method.getReturnType(), method.getGenericReturnType()));
			return rpcResponse;
		}
	}

	private static Object convertParam(String param, Class<?> paramType,
			Type genericType) {
		// TODO Auto-generated method stub
		if (paramType == String.class) {
			return param;
		} else if (paramType == boolean.class || paramType == Boolean.class) {
			return Boolean.parseBoolean(param);
		} else if (paramType == byte.class || paramType == Byte.class) {
			return Byte.parseByte(param);
		} else if (paramType == char.class || paramType == Character.class) {
			return param.charAt(0);
		} else if (paramType == short.class || paramType == Short.class) {
			return Short.valueOf(param);
		} else if (paramType == int.class || paramType == Integer.class) {
			return Integer.parseInt(param);
		} else if (paramType == long.class || paramType == Long.class) {
			return Long.parseLong(param);
		} else if (paramType == double.class || paramType == Double.class) {
			return Double.parseDouble(param);
		} else if (paramType == float.class || paramType == Float.class) {
			return Float.parseFloat(param);
		} else if (paramType == List.class) {
			// List
			if (genericType instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) genericType;
				// 返回表示此类型实际类型参数的 Type 对象的数组
				Type[] actualTypeArguments = parameterizedType
						.getActualTypeArguments();
				return JacksonUtils.json2List(param,
						(Class<?>) actualTypeArguments[0]);
			}
		} else if (paramType == Map.class) {
			return JacksonUtils.json2Map(param);
		} else if (paramType.isArray()) {
			return JacksonUtils.json2Ary(param, paramType.getComponentType());
		}
		return param;
	}

	public static void encode1(final ByteBuf out, final Object message)
			throws IOException {
		Closer closer = Closer.create();
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			closer.register(bos);
			CodedOutputStream outProtobuf = CodedOutputStream.newInstance(bos);
			if (message instanceof RpcRequest) {
				RpcRequest rpcRequest = (RpcRequest) message;
				outProtobuf.writeStringNoTag(rpcRequest.getSeq());
				outProtobuf.writeStringNoTag(rpcRequest.getInterfaceName());
				outProtobuf.writeStringNoTag(rpcRequest.getMethod());
				byte[] paramsBytes = packMethodInvokeParameters(rpcRequest
						.getParams());
				outProtobuf.writeByteArrayNoTag(paramsBytes);
				outProtobuf.flush();
			} else if (message instanceof RpcResponse) {
				RpcResponse rpcResponse = (RpcResponse) message;
				outProtobuf.writeStringNoTag(rpcResponse.getSeq());
				outProtobuf.writeStringNoTag(rpcResponse.getEcode());
				outProtobuf.writeStringNoTag(rpcResponse.getEmsg());
				byte[] retValueBytes = serializeMethodReturnValue(rpcResponse
						.getData());
				outProtobuf.writeByteArrayNoTag(retValueBytes);
				outProtobuf.flush();
			} else {
				throw new IOException(
						"Object type must be RpcRequest or RpcResponse");
			}
			byte[] body = bos.toByteArray();
			out.writeBytes(body);
		} finally {
			closer.close();
		}
	}

	private static byte[] serializeMethodReturnValue(Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	private static byte[] packMethodInvokeParameters(Object[] params) {
		// TODO Auto-generated method stub
		if (params == null || params.length < 1) {
			return new byte[0];
		}
		return null;
	}

	public static Object decode1(byte[] body, boolean isRpcRequest)
			throws IOException {
		CodedInputStream in = CodedInputStream.newInstance(body);
		if (isRpcRequest) {
			RpcRequest rpcRequest = new RpcRequest();
			rpcRequest.setSeq(in.readString());
			rpcRequest.setInterfaceName(in.readString());
			rpcRequest.setMethod(in.readString());
			rpcRequest.setParams(unpackMethodInvokeParameters(in
					.readByteArray()));
			return rpcRequest;
		} else {
			RpcResponse rpcResponse = new RpcResponse();
			rpcResponse.setSeq(in.readString());
			rpcResponse.setEcode(in.readString());
			rpcResponse.setEmsg(in.readString());
			rpcResponse
					.setData(deserializeMethodReturnValue(in.readByteArray()));
			return rpcResponse;
		}
	}

	private static Object deserializeMethodReturnValue(byte[] readByteArray) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Object[] unpackMethodInvokeParameters(byte[] readByteArray) {
		// TODO Auto-generated method stub
		return null;
	}
}
