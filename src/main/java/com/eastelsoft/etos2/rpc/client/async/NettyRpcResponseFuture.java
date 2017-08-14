package com.eastelsoft.etos2.rpc.client.async;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eastelsoft.etos2.rpc.client.async.util.NettyRpcResponseBuilder;
import com.eastelsoft.etos2.rpc.tool.Redis;
import com.eastelsoft.etos2.rpc.trace.TraceContext;
import com.eastelsoft.etos2.rpc.trace.TraceLog;

/**
 */
public class NettyRpcResponseFuture {
	private static final Logger logger = LoggerFactory
			.getLogger(NettyRpcResponseFuture.class);
	private static final String QUEUE_LOG = "queue.intf.log";

	private static Redis redisLog;

	private final CountDownLatch latch = new CountDownLatch(1);

	private volatile boolean isDone = false;

	private volatile boolean isCancel = false;

	private final AtomicBoolean isProcessed = new AtomicBoolean(false);

	private volatile NettyRpcResponseBuilder responseBuilder;

	private volatile Channel channel;

	private volatile List<NettyRpcResponseCallback> callbacks;

	public boolean cancel(Throwable cause) {
		if (isProcessed.getAndSet(true)) {
			return false;
		}
		responseBuilder = new NettyRpcResponseBuilder();
		responseBuilder.setSuccess(false);
		responseBuilder.setCause(cause);
		isCancel = true;
		latch.countDown();
		synchronized (this) {
			if (callbacks != null) {
				for (NettyRpcResponseCallback callback : callbacks) {
					callback.onComplete(this);
				}
			}
		}
		logTraceRecord();
		return true;
	}

	public NettyRpcResponse get() throws InterruptedException,
			ExecutionException {
		latch.await();
		return responseBuilder.build();
	}

	public NettyRpcResponse get(long timeout, TimeUnit unit)
			throws TimeoutException, InterruptedException {
		if (!latch.await(timeout, unit)) {
			throw new TimeoutException();
		}
		return responseBuilder.build();
	}

	public boolean done() {
		if (isProcessed.getAndSet(true)) {
			return false;
		}
		isDone = true;
		latch.countDown();
		synchronized (this) {
			if (callbacks != null) {
				for (NettyRpcResponseCallback callback : callbacks) {
					callback.onComplete(this);
				}
			}
		}
		logTraceRecord();
		return true;
	}

	public boolean isCancelled() {
		return isCancel;
	}

	public boolean isDone() {
		return isDone;
	}

	/**
	 * Getter method for property <tt>channel</tt>.
	 * 
	 * @return property value of channel
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * Setter method for property <tt>channel</tt>.
	 * 
	 * @param channel
	 *            value to be assigned to property channel
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/**
	 * Getter method for property <tt>responseBuilder</tt>.
	 * 
	 * @return property value of responseBuilder
	 */
	public NettyRpcResponseBuilder getResponseBuilder() {
		return responseBuilder;
	}

	/**
	 * Setter method for property <tt>responseBuilder</tt>.
	 * 
	 * @param responseBuilder
	 *            value to be assigned to property responseBuilder
	 */
	public void setResponseBuilder(NettyRpcResponseBuilder responseBuilder) {
		this.responseBuilder = responseBuilder;
	}

	public void addCallback(NettyRpcResponseCallback callback) {
		// 如果已经执行完毕，立刻回调
		if (isProcessed.get()) {
			while (true) {
				try {
					latch.await();
					break;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			callback.onComplete(this);
			return;
		}
		if (callbacks == null) {
			callbacks = new ArrayList<NettyRpcResponseCallback>();
		}
		synchronized (this) {
			if (isProcessed.get()) {
				while (true) {
					try {
						latch.await();
						break;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				callback.onComplete(this);
				return;
			}
			callbacks.add(callback);
		}
	}

	private void logTraceRecord() {
		try {
			TraceContext traceContext = channel
					.attr(TraceContext.TRACE_CONTEXT).get();
			if (null == traceContext) {
				return;
			}
			Redis redisLog = getRedisLog();
			if (null == redisLog) {
				return;
			}
			String seq = "";
			String txId = traceContext.getTxId();
			String spanId = traceContext.getSpanId();
			String pSpanId = traceContext.getpSpanId();
			String serviceType = traceContext.getServiceType().name();
			long reqTime = traceContext.getReqTime();
			long respTime = System.currentTimeMillis();
			long delay = respTime - reqTime;
			String api = traceContext.getApi();
			String ecode = "";
			String emsg = "";
			String reqMsg = "";
			String respMsg = "";
			String throwable = traceContext.getThrowable();
			NettyRpcResponse response = null;
			try {
				response = get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
				return;
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
				return;
			}
			if (response.isSuccess()) {
				respMsg = response.getRpcResponse().toString();
			} else {
				ecode = "500";
				Throwable cause = response.getCause();
				emsg = cause != null ? cause.getMessage() : "";
			}
			TraceLog traceLog = new TraceLog();
			traceLog.setSeq(seq);
			traceLog.setTxId(txId);
			traceLog.setSpanId(spanId);
			traceLog.setParentSpanId(pSpanId);
			traceLog.setServiceType(serviceType);
			traceLog.setApi(api);
			traceLog.setEcode(ecode);
			traceLog.setEmsg(emsg);
			traceLog.setDelay((int) delay);
			traceLog.setReqTime(reqTime);
			traceLog.setRespTime(respTime);
			traceLog.setReqMsg(reqMsg);
			traceLog.setRespMsg(respMsg);
			traceLog.setThrowable(throwable);
			getRedisLog().queueIn(QUEUE_LOG, traceLog);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private Redis getRedisLog() {
		return null;
	}
}
