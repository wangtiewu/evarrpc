package com.eastelsoft.etos2.rpc.spring;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.eastelsoft.etos2.rpc.RpcRequestExecutorJdk;
import com.eastelsoft.etos2.rpc.RpcServer;
import com.eastelsoft.etos2.rpc.proxy.MethodInfoCache;
import com.eastelsoft.etos2.rpc.registry.Provider;
import com.eastelsoft.etos2.rpc.registry.Registry;
import com.eastelsoft.etos2.rpc.tool.NetUtils;

public class RpcService implements ApplicationContextAware, ApplicationListener {
	private static Logger logger = LoggerFactory.getLogger(RpcService.class);
	private String interfaceName;
	private String ref;
	private String registry;
	private String server;
	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		// TODO Auto-generated method stub
		RpcServer rpcServer = (RpcServer) applicationContext
				.getBean(this.server);
		if (rpcServer == null) {
			logger.error("server " + server + " not defined in spring");
			return;
		}
		rpcServer.registerHandler(interfaceName,
				applicationContext.getBean(ref));
		if (!StringUtils.isEmpty(this.registry)) {
			Registry registry2 = (Registry) applicationContext
					.getBean(registry);
			if (registry2 == null) {
				logger.error("registry " + registry
						+ " not defined in spring");
				return;
			}
			Provider provider = new Provider();
			provider.setInterfaceName(this.interfaceName);
			provider.setSerializeType(rpcServer.getSerializeType());
			String host = rpcServer.getHost();
			if (host.equals("0.0.0.0")) {
				host = NetUtils.getLocalHost();
			}
			provider.setServerAddress(host + ":" + rpcServer.getPort());
			if (!registry2.registerProvider(provider)) {
				logger.error("Provivder " + interfaceName
						+ " register to registry fail");
			}
		}
		MethodInfoCache.getInstance().register(interfaceName,
				applicationContext.getBean(ref).getClass());
	}
}
