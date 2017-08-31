package com.eastelsoft.etos2.rpc;

public interface RpcRequestExecutor {
	public void submit(RpcRequestTask rpcInboundTask);
	public void stop();
}
