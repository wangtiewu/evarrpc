package com.eastelsoft.etos2.rpc;

import io.netty.channel.ChannelHandlerContext;

import org.apache.commons.beanutils.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcRequestTask implements Runnable {
	private static Logger logger = LoggerFactory
			.getLogger(RpcRequestTask.class);
	private ChannelHandlerContext ctx;
	private RpcRequest rpcRequest;
	private Object handler;

	public RpcRequestTask(ChannelHandlerContext arg0, RpcRequest rpcRequest) {
		this.ctx = arg0;
		this.rpcRequest = rpcRequest;
	}

	public void setHandler(Object handler) {
		this.handler = handler;
	}

	public RpcRequest getRpcRequest() {
		return rpcRequest;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		RpcResponse response = new RpcResponse();
		response.setSeq(rpcRequest.getSeq());
		response.setInterfaceName(rpcRequest.getInterfaceName());
		response.setMethod(rpcRequest.getMethod());
		try {
			Object result = reflect(rpcRequest);
			response.setData(result);
			response.setEcode(ErrorCode.ECODE_SUCCESS);
			response.setEmsg("成功");
		} catch (Throwable t) {
			response.setEcode(ErrorCode.ECODE_SERVICE_EXCEPTION);
			response.setEmsg(t.toString());
			response.setCause(t);
			logger.error("RPC Server invoke error，" + t.getMessage(), t);
		}
		ctx.writeAndFlush(response);
	}

	private Object reflect(RpcRequest request) throws Throwable {
		String methodName = request.getMethod();
		Object[] parameters = request.getParams();
		return MethodUtils.invokeMethod(handler, methodName, parameters);
	}

}
