package com.eastelsoft.etos2.rpc.client.sync;

import com.eastelsoft.etos2.rpc.RpcResponse;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class RpcSyncRespHandler extends SimpleChannelInboundHandler {

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
		// TODO Auto-generated method stub
		RpcResponse rpcResponse = (RpcResponse) arg1;
		RpcSyncClient.rpcFinished(rpcResponse);
	}
}
