package com.eastelsoft.etos2.rpc;

import java.io.Serializable;

public class RpcMessage implements Serializable {
	protected String txId;// 消息事务id，消息在多个服务处理时保持不变
	protected String spanId = "-1";// 调用方的server_id
	protected String seq;// 本次调用消息序列号
	protected String serviceType;// 服务类型，如crm、prm

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getSpanId() {
		return spanId;
	}

	public void setSpanId(String spanId) {
		this.spanId = spanId;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

}
