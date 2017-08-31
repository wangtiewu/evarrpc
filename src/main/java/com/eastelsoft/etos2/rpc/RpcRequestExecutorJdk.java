package com.eastelsoft.etos2.rpc;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.eastelsoft.etos2.rpc.tool.AbortPolicyWithReport;
import com.eastelsoft.etos2.rpc.tool.NamedThreadFactory;

public class RpcRequestExecutorJdk implements RpcRequestExecutor{
	private ThreadPoolExecutor threadPoolExecutor;

	public RpcRequestExecutorJdk(int threadCount, int maxWaitTask) {
		threadPoolExecutor = (ThreadPoolExecutor) getExecutor(threadCount, maxWaitTask);
	}
	
	@Override
	public void submit(RpcRequestTask rpcInboundTask) {
		// TODO Auto-generated method stub
		threadPoolExecutor.execute(rpcInboundTask);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if (threadPoolExecutor != null) {
			threadPoolExecutor.shutdown();
			threadPoolExecutor = null;
		}
	}

	private Executor getExecutor(int threads, int queues) {
		String name = "RpcThreadPool";
		return new ThreadPoolExecutor(threads, threads, 0,
				TimeUnit.MILLISECONDS,
				queues == 0 ? new SynchronousQueue<Runnable>()
						: (queues < 0 ? new LinkedBlockingQueue<Runnable>()
								: new LinkedBlockingQueue<Runnable>(queues)),
				new NamedThreadFactory(name, true), new AbortPolicyWithReport(
						name));
	}

}
