package com.eastelsoft.etos2.rpc.client.async;

import com.eastelsoft.etos2.rpc.RpcResponse;


/**
 */
public class NettyRpcResponse {
	private volatile boolean success = false;
	private volatile RpcResponse rpcResponse;
	private volatile Throwable cause;

	public NettyRpcResponse() {
		super();
	}

	public RpcResponse getRpcResponse() {
		return rpcResponse;
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

	public void setRpcResponse(RpcResponse rpcResponse) {
		this.rpcResponse = rpcResponse;
	}

}
