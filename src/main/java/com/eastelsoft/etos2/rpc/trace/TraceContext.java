package com.eastelsoft.etos2.rpc.trace;

import com.eastelsoft.etos2.rpc.tool.IdGenerator;

import io.netty.util.AttributeKey;

public class TraceContext {
	public static final String HTTP_HEAD_TXID = "trace_txid";
	public static final String HTTP_HEAD_PSPANID = "trace_pspanid";
	public static final String HTTP_HEAD_SPANID = "trace_spanid";
	public static final String ROOT_SPAN_ID = "-1";
	public static final AttributeKey<TraceContext> TRACE_CONTEXT = AttributeKey
			.<TraceContext> valueOf("traceContext");// traceContext绑定到netty
													// channel

	private String txId = IdGenerator.createObjectIdHex();// 事务id，用于关联消息处理
	private String spanId = "";// 处理请求消息的服务器id
	private String pSpanId = "-1";// 发送请求消息的服务器id，-1表示根span
	private long reqTime = System.currentTimeMillis();// 请求时间戳
	private ServiceType serviceType = ServiceType.NETTY_HTTP_SERVER;// 服务类型
	private String api;// 接口名称
	private String ecode;// 错误码
	private String emsg;// 错误信息
	private String throwable; //异常信息 

	public TraceContext(String txId, String pSpanId, String spanId) {
		this.txId = txId;
		this.pSpanId = pSpanId;
		this.spanId = spanId;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getpSpanId() {
		return pSpanId;
	}

	public void setpSpanId(String pSpanId) {
		this.pSpanId = pSpanId;
	}

	public String getSpanId() {
		return spanId;
	}

	public void setSpanId(String spanId) {
		this.spanId = spanId;
	}

	public long getReqTime() {
		return reqTime;
	}

	public void setReqTime(long reqTime) {
		this.reqTime = reqTime;
	}

	/**
	 * @return the serviceType
	 */
	public ServiceType getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * @return the api
	 */
	public String getApi() {
		return api;
	}

	/**
	 * @param api
	 *            the api to set
	 */
	public void setApi(String api) {
		this.api = api;
	}

	/**
	 * @return the ecode
	 */
	public String getEcode() {
		return ecode;
	}

	/**
	 * @param ecode
	 *            the ecode to set
	 */
	public void setEcode(String ecode) {
		this.ecode = ecode;
	}

	/**
	 * @return the emsg
	 */
	public String getEmsg() {
		return emsg;
	}

	/**
	 * @param emsg
	 *            the emsg to set
	 */
	public void setEmsg(String emsg) {
		this.emsg = emsg;
	}

	public String getThrowable() {
		return throwable;
	}

	public void setThrowable(String throwable) {
		this.throwable = throwable;
	}


	public enum ServiceType {
		NETTY_HTTP_SERVER(0), NETTY_HTTP_CLIENT(1);
		private int value;

		public int value() {
			return value;
		}

		private ServiceType(int value) {
			this.value = value;
		}

		public static String enumsToString() {
			StringBuffer sb = new StringBuffer();
			for (ServiceType enum2 : ServiceType.values()) {
				sb.append(enum2).append("(").append(enum2.value).append(")")
						.append(",");
			}
			return sb.toString().length() > 0 ? sb.toString().substring(0,
					sb.toString().length() - 1) : sb.toString();
		}
	};
}

