package com.eastelsoft.etos2.rpc.proxy;

import java.lang.reflect.Method;

public class MethodInfo {
	private Class<?> cls;
	private Method method;

	public MethodInfo(Class<?> cls, Method method) {
		this.cls = cls;
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}
	
}
