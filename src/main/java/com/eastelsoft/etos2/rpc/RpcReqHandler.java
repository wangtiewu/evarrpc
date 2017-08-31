package com.eastelsoft.etos2.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcReqHandler extends SimpleChannelInboundHandler  {
	private static final Logger logger = LoggerFactory.getLogger(RpcReqHandler.class);
	private RpcServer rpcServer;
	
	RpcReqHandler(RpcServer rpcServer) {
		this.rpcServer = rpcServer;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
		// TODO Auto-generated method stub
		RpcRequest rpcRequest = (RpcRequest) arg1;
		RpcRequestTask rpcInboundTask = new RpcRequestTask(arg0, rpcRequest);
		rpcServer.handleRequest(rpcInboundTask);
	}}
