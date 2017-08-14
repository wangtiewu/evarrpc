package com.eastelsoft.etos2.rpc.trace;

public interface TraceLogService {
	void init() throws Exception;
	void destroy();
	void save(TraceLog traceLog);
}
