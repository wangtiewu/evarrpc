package com.eastelsoft.etos2.rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.eastelsoft.etos2.rpc.tool.AbortPolicyWithReport;
import com.eastelsoft.etos2.rpc.tool.NamedThreadFactory;

public class RpcRequestExecutor {
	private static ThreadPoolExecutor threadPoolExecutor;
	private static Map<String, Object> handlers = new HashMap<String, Object>();

	public static void submit(RpcRequestTask rpcInboundTask) {
		// TODO Auto-generated method stub
		if (threadPoolExecutor == null) {
			synchronized (RpcRequestExecutor.class) {
				if (threadPoolExecutor == null) {
					threadPoolExecutor = (ThreadPoolExecutor) getExecutor(
							Runtime.getRuntime().availableProcessors() * 2, -1);
				}
			}
		}
		threadPoolExecutor.execute(rpcInboundTask);
	}
	
	public static void registerHandler(String interfaceName, Object handler) {
		handlers.put(interfaceName, handler);
	}
	
	public static Object findHandler(String interfaceName) {
		return handlers.get(interfaceName);
	}

	private static Executor getExecutor(int threads, int queues) {
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
