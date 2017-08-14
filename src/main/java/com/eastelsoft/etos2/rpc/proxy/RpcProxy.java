package com.eastelsoft.etos2.rpc.proxy;

import com.eastelsoft.etos2.rpc.ErrorCode;
import com.eastelsoft.etos2.rpc.RpcRequest;
import com.eastelsoft.etos2.rpc.RpcResponse;
import com.eastelsoft.etos2.rpc.client.async.RpcAsyncClient;
import com.eastelsoft.etos2.rpc.registry.Provider;
import com.eastelsoft.etos2.rpc.registry.Registry;
import com.eastelsoft.etos2.rpc.serialize.RpcRespSerialize;
import com.eastelsoft.etos2.rpc.tool.NetUtils;
import com.eastelsoft.etos2.rpc.tool.StringDeal;

public abstract class RpcProxy {
	Object rpc(Registry registry, String serverAddress,
			RpcRespSerialize rpcRespSerialize, RpcRequest rpcRequest,
			int retryTimes) {
		Provider oldProvider = null;
		String curServerAddress = serverAddress;
		while (retryTimes-- > 0) {
			if (registry != null) {
				Provider provider = registry.select(NetUtils.getLocalHost(),
						rpcRequest.getInterfaceName(),
						rpcRespSerialize.getSerializeType());
				if (provider == null) {
					throw new RuntimeException("Service "
							+ rpcRequest.getInterfaceName()
							+ " has no valid provider");
				}
				if (provider == oldProvider) {
					if (registry.allProviders(rpcRequest.getInterfaceName()).size() > 1) {
						retryTimes ++;
						continue;
					}
				}
				oldProvider = provider;
				curServerAddress = provider.getServerAddress();
			}
			RpcResponse rpcResponse = doRpc(curServerAddress,
					rpcRespSerialize, rpcRequest);
			if (rpcResponse.getEcode().equals(ErrorCode.ECODE_INTF_ERROR)) {
				// network error，retry
				continue;
			}
			return rpcResponse.getData();
		}
		throw new RuntimeException("Service " + rpcRequest.getInterfaceName()
				+ " has no valid provider");
	}

	private RpcResponse doRpc(String serverAddress,
			RpcRespSerialize rpcRespSerialize, RpcRequest rpcRequest) {
		String[] hostAndPort = StringDeal.split(serverAddress, ":");
		RpcAsyncClient rpcClient = (rpcRespSerialize != null ? new RpcAsyncClient(
				hostAndPort[0], Integer.parseInt(hostAndPort[1]),
				rpcRespSerialize) : new RpcAsyncClient(hostAndPort[0],
				Integer.parseInt(hostAndPort[1])));
		RpcResponse rpcResponse = rpcClient.send(rpcRequest);
		return rpcResponse;
	}
}
