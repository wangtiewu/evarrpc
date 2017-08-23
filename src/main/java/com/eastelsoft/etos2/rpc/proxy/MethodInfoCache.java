package com.eastelsoft.etos2.rpc.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.eastelsoft.etos2.rpc.RpcServerStart;
import com.eastelsoft.etos2.rpc.spring.RpcService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MethodInfoCache {
	private Map<String, Class<?>> serviceBeans;

	private MethodInfoCache() {
		serviceBeans = new HashMap<String, Class<?>>();
	}

	private static class MethodInfoCacheHolder {
		private static MethodInfoCache singleInstance = new MethodInfoCache();
	}

	public static MethodInfoCache getInstance() {
		return MethodInfoCacheHolder.singleInstance;
	}

	private Cache<String, MethodInfo> cache = CacheBuilder.newBuilder()
			.maximumSize(1024).expireAfterWrite(1, TimeUnit.HOURS).build();

	private MethodInfo get(final String interfaceName, final String methodName,
			final int argCount, Cache<String, MethodInfo> cache) {
		try {
			return cache.get(interfaceName + "." + methodName + "." + argCount,
					new Callable<MethodInfo>() {
						public MethodInfo call() throws Exception {
							Class cls = serviceBeans.get(interfaceName);
							if (cls == null) {
								return null;
							}
							Method[] methods = cls.getDeclaredMethods();
							for (Method method : methods) {
								if (method.getName().equals(methodName)
										&& method.getParameterTypes().length == argCount) {
									return new MethodInfo(cls, method);
								}
							}
							for (Method method : methods) {
								if (method.getName().equals(methodName)) {
									return new MethodInfo(cls, method);
								}
							}
							return null;
						}
					});
		} catch (ExecutionException e) {
			return null;
		}
	}

	public void register(String interfaceName, Class cls) {
		if (!this.serviceBeans.containsKey(interfaceName)) {
			this.serviceBeans.put(interfaceName, cls);
		}
	}

	public MethodInfo get(String interfaceName, String method, int argCount) {
		return get(interfaceName, method, argCount, cache);
	}

}
