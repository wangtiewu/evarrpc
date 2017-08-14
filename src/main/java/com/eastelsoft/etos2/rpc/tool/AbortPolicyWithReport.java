package com.eastelsoft.etos2.rpc.tool;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class AbortPolicyWithReport implements RejectedExecutionHandler {

	private final String threadName;

	public AbortPolicyWithReport(String threadName) {
		this.threadName = threadName;
	}

	public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
		String msg = String
				.format("RpcServer["
						+ " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d),"
						+ " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)]",
						threadName, e.getPoolSize(), e.getActiveCount(),
						e.getCorePoolSize(), e.getMaximumPoolSize(),
						e.getLargestPoolSize(), e.getTaskCount(),
						e.getCompletedTaskCount(), e.isShutdown(),
						e.isTerminated(), e.isTerminating());
		System.out.println(msg);
		throw new RejectedExecutionException(msg);
	}

}
