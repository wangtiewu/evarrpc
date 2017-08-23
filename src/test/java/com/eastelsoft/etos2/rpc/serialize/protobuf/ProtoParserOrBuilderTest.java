package com.eastelsoft.etos2.rpc.serialize.protobuf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.etos2.rpc.RpcRequest;
import com.eastelsoft.etos2.rpc.RpcResponse;
import com.eastelsoft.etos2.rpc.example.HelloWorld;

import junit.framework.TestCase;

public class ProtoParserOrBuilderTest extends TestCase {
	public void testHello() throws Exception {
		File protoFile = new File("D:/evarrpc.proto");
		ProtobufParserOrBuilder builder = new ProtobufParserOrBuilder(protoFile);
		RpcRequest rpcRequest = new RpcRequest();
		rpcRequest.setSeq("seq");
		rpcRequest.setInterfaceName(HelloWorld.class.getName());
		rpcRequest.setMethod("sayHello");
		Object[] params = new Object[1];
		params[0] = "wtw";
		rpcRequest.setParams(params);
		byte[] encoded = builder.encode(rpcRequest);
		RpcRequest decodedObj = builder.decode(RpcRequest.class, encoded);
		System.out.println(decodedObj);
		
		RpcResponse rpcResponse = new RpcResponse();
		rpcResponse.setSeq("1");
		rpcResponse.setEcode("0");
		rpcResponse.setEmsg("成功");
		rpcResponse.setData("Hello, wtw");
		encoded = builder.encode(rpcResponse);
		RpcResponse decodedObj1 = builder.decode(RpcResponse.class, encoded);
		System.out.println(decodedObj1);
		List a = new ArrayList();
		a.add("1");
		rpcResponse.setData(a);
		encoded = builder.encode(rpcResponse);
		decodedObj1 = builder.decode(RpcResponse.class, encoded);
//		System.out.println(((List)decodedObj1.getData()).get(0));
		rpcResponse.setData(new String[]{"1","2"});
		encoded = builder.encode(rpcResponse);
		decodedObj1 = builder.decode(RpcResponse.class, encoded);
	}
}
