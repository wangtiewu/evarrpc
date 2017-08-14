package com.eastelsoft.etos2.rpc.trace;

/**
 * 
 * @author Eastelsoft
 *
 */
public class TraceContextUtil {
	private final static ThreadLocal<TraceContext> localTraceContext = new ThreadLocal<TraceContext>();

	public static void setTraceContext(TraceContext traceContext) {
		localTraceContext.set(traceContext);
	}

	public static TraceContext removeTraceContext() {
		TraceContext traceContext = localTraceContext.get();
		localTraceContext.remove();
		return traceContext;
	}

	public static TraceContext getTraceContext() {
		return localTraceContext.get();
	}
}
