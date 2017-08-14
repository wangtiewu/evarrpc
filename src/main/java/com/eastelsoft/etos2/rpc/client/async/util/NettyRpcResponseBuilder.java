package com.eastelsoft.etos2.rpc.client.async.util;

import com.eastelsoft.etos2.rpc.RpcResponse;
import com.eastelsoft.etos2.rpc.client.async.NettyRpcResponse;

/**
 * @author xianwu.zhang
 */
public class NettyRpcResponseBuilder {

	private volatile Throwable cause;

	private volatile NettyRpcResponse content;

	private volatile RpcResponse rpcResponse;

	private volatile boolean success = false;

	public NettyRpcResponse build() {
		if (content == null) {
			synchronized (this) {
				if (content == null) {
					NettyRpcResponse response = new NettyRpcResponse();
					if (success) {
						response.setSuccess(true);
						response.setRpcResponse(rpcResponse);
					} else {
						response.setCause(cause);
					}
					content = response;
				}
			}
		}
		return content;
	}

	public RpcResponse getRpcResponse() {
		return rpcResponse;
	}

	public void setRpcResponse(RpcResponse rpcResponse) {
		this.rpcResponse = rpcResponse;
	}

	/**
	 * Getter method for property <tt>cause</tt>.
	 * 
	 * @return property value of cause
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
	 * Setter method for property <tt>cause</tt>.
	 * 
	 * @param cause
	 *            value to be assigned to property cause
	 */
	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	/**
	 * Getter method for property <tt>success</tt>.
	 * 
	 * @return property value of success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Setter method for property <tt>success</tt>.
	 * 
	 * @param success
	 *            value to be assigned to property success
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
