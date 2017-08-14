package com.eastelsoft.etos2.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcReqHandler extends SimpleChannelInboundHandler  {
	
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
		// TODO Auto-generated method stub
		RpcRequest rpcRequest = (RpcRequest) arg1;
		RpcRequestTask rpcInboundTask = new RpcRequestTask(arg0, rpcRequest);
		RpcRequestExecutor.submit(rpcInboundTask);
	}}
