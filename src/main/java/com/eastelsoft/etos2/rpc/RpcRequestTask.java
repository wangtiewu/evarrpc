package com.eastelsoft.etos2.rpc;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import org.apache.commons.beanutils.MethodUtils;

import com.eastelsoft.etos2.rpc.spring.RpcService;

public class RpcRequestTask implements Runnable {
	private ChannelHandlerContext ctx;
	private RpcRequest rpcRequest;

	public RpcRequestTask(ChannelHandlerContext arg0, RpcRequest rpcRequest) {
		this.ctx = arg0;
		this.rpcRequest = rpcRequest;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		RpcResponse response = new RpcResponse();
		response.setSeq(rpcRequest.getSeq());
		try {
			Object result = reflect(rpcRequest);
			response.setData(result);
			response.setEcode(ErrorCode.ECODE_SUCCESS);
			response.setEmsg("成功");
		} catch (Throwable t) {
			response.setEcode(ErrorCode.ECODE_SERVICE_EXCEPTION);
			response.setEmsg(t.toString());
			response.setCause(t);
			t.printStackTrace();
			System.err.printf("RPC Server invoke error!\n");
		}

		ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture channelFuture)
					throws Exception {
				// System.out.println("RPC Server Send message-id respone:"
				// + rpcRequest.getSeq());
			}
		});
	}

	private Object reflect(RpcRequest request) throws Throwable {
		String service = request.getInterfaceName();
		Object serviceBean = RpcServerStart.getContext().getBean(service);
		String methodName = request.getMethod();
		Object[] parameters = request.getParams();
		return MethodUtils.invokeMethod(
				serviceBean instanceof RpcService ? RpcServerStart.getContext()
						.getBean((((RpcService) serviceBean).getRef()))
						: serviceBean, methodName, parameters);
	}

}
