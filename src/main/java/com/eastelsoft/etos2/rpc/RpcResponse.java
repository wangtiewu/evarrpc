package com.eastelsoft.etos2.rpc;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class RpcResponse extends RpcMessage {
	private String ecode;// 错误码
	private String emsg;// 错误消息
	private Object data;// 响应数据
	private Throwable cause;// 异常

	public RpcResponse() {
	}

	public RpcResponse(String ecode, String emsg) {
		this(ecode, emsg, null, null);
	}
	
	public RpcResponse(String ecode, String emsg, Object data) {
		this(ecode, emsg, data, null);
	}
	
	public RpcResponse(String ecode, String emsg, Throwable cause) {
		this(ecode, emsg, null, cause);
	}
	
	public RpcResponse(String ecode, String emsg, Object data, Throwable cause) {
		this.ecode = ecode;
		this.emsg = emsg;
		this.data = data;
		this.cause = cause;
	}

	public String getEcode() {
		return ecode;
	}

	public void setEcode(String ecode) {
		this.ecode = ecode;
	}

	public String getEmsg() {
		return emsg;
	}

	public void setEmsg(String emsg) {
		this.emsg = emsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("seq", seq).append("ecode", ecode).append("emsg", emsg)
				.toString();
	}
}
