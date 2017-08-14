package com.eastelsoft.etos2.rpc.trace;

import java.io.Serializable;

public class TraceLog implements Serializable{
	private String txId;// 消息事务id，消息在多个服务处理时保持不变
	private String spanId;// 处理消息的serverid
	private String parentSpanId;// 调用方的serverid
	private String seq;// 本次调用消息序列号
	private String serviceType;// 服务类型，如VOS，400-oss
	private String api;// 接口名称
	private int delay;// 单位秒
	private String ecode;// 错误码
	private String emsg;// 错误消息
	private String throwable;// 异常信息
	private String reqMsg;// 请求消息
	private String respMsg;// 响应消息
	private long reqTime;// 请求时间，1970年1月1日0时起的毫秒数
	private long respTime;// 响应时间，1970年1月1日0时起的毫秒数
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
	
	public String getParentSpanId() {
		return parentSpanId;
	}
	public void setParentSpanId(String parentSpanId) {
		this.parentSpanId = parentSpanId;
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
	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
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
	public String getReqMsg() {
		return reqMsg;
	}
	public void setReqMsg(String reqMsg) {
		this.reqMsg = reqMsg;
	}
	public String getRespMsg() {
		return respMsg;
	}
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	public long getReqTime() {
		return reqTime;
	}
	public void setReqTime(long reqTime) {
		this.reqTime = reqTime;
	}
	public long getRespTime() {
		return respTime;
	}
	public void setRespTime(long respTime) {
		this.respTime = respTime;
	}
	public String getThrowable() {
		return throwable;
	}
	public void setThrowable(String throwable) {
		this.throwable = throwable;
	}
	
}

