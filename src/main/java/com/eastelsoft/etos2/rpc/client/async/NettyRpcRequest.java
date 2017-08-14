package com.eastelsoft.etos2.rpc.client.async;

import java.net.URI;
import com.eastelsoft.etos2.rpc.RpcRequest;

public class NettyRpcRequest {
	private URI uri;

	private RpcRequest rpcRequest;

	public NettyRpcRequest uri(String uri) {
		this.uri = URI.create(uri);
		return this;
	}

	public NettyRpcRequest uri(URI uri) {
		if (null == uri) {
			throw new NullPointerException("uri");
		}
		this.uri = uri;
		return this;
	}

	public NettyRpcRequest content(RpcRequest rpcRequest) {
		if (null == rpcRequest) {
			throw new NullPointerException("content");
		}

		this.rpcRequest = rpcRequest;
		return this;
	}

	public URI getUri() {
		return uri;
	}

	public RpcRequest getContent() {
		return rpcRequest;
	}
}
