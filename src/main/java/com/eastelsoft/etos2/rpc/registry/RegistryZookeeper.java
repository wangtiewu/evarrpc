package com.eastelsoft.etos2.rpc.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eastelsoft.etos2.rpc.tool.JacksonUtils;

public class RegistryZookeeper implements Registry {
	private final static Logger logger = LoggerFactory
			.getLogger(RegistryZookeeper.class.getName());
	private static final String NODE_PROVIDERS = "providers";
	private static final String NODE_CONSUMES = "consumes";
	private String servers;// zookeeper 地址列表
	private String basePath = "/evar/rpc/";
	private CuratorFramework client;
	private AtomicLong rpcCalls = new AtomicLong();
	private ConcurrentHashMap<String, PathChildrenCache> providerWatchers;
	private ConcurrentHashMap<String, List<Provider>> allProviders;

	public void init() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 3);
		client = CuratorFrameworkFactory.builder().connectString(servers)
				.sessionTimeoutMs(10000).connectionTimeoutMs(10000)
				.retryPolicy(retryPolicy).build();
		client.start();
		providerWatchers = new ConcurrentHashMap<String, PathChildrenCache>();
		allProviders = new ConcurrentHashMap<String, List<Provider>>();
	}

	public void destroy() {
		if (providerWatchers != null) {
			Set<Map.Entry<String, PathChildrenCache>> entrys = providerWatchers
					.entrySet();
			for (Map.Entry<String, PathChildrenCache> entry : entrys) {
				try {
					entry.getValue().close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		providerWatchers.clear();
		providerWatchers = null;
		if (client != null) {
			client.close();
			client = null;
		}
	}

	@Override
	public boolean registerProvider(Provider provider) {
		// TODO Auto-generated method stub
		String path = basePath + provider.getInterfaceName() + "/"
				+ NODE_PROVIDERS;
		try {
			if (client.checkExists().forPath(path) == null) {
				client.create().creatingParentsIfNeeded()
						.withMode(CreateMode.PERSISTENT).forPath(path);
			}
			path = path + "/" + provider.getServerAddress();
			client.create()
					.withMode(CreateMode.EPHEMERAL)
					.forPath(path, JacksonUtils.beanToJson(provider).getBytes());
			logger.info("Privider registered: "
					+ JacksonUtils.beanToJson(provider));
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void unregisterProvider(Provider provider) {
		// TODO Auto-generated method stub
		String path = basePath + provider.getInterfaceName() + "/"
				+ NODE_PROVIDERS + "/" + provider.getServerAddress();
		try {
			client.delete().forPath(path);
			logger.info("Provider unregistered: "
					+ JacksonUtils.beanToJson(provider));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean registerConsumer(Consumer consumer) {
		// TODO Auto-generated method stub
		String path = basePath + consumer.getInterfaceName() + "/"
				+ NODE_CONSUMES;
		try {
			if (client.checkExists().forPath(path) == null) {
				client.create().creatingParentsIfNeeded()
						.withMode(CreateMode.PERSISTENT).forPath(path);
			}
			path = path + "/" + consumer.getAddress();
			client.create()
					.withMode(CreateMode.EPHEMERAL)
					.forPath(path, JacksonUtils.beanToJson(consumer).getBytes());
			logger.info("Privider registered: "
					+ JacksonUtils.beanToJson(consumer));
			// we try create watch providers
			tryCreateProvidersWatch(consumer.getInterfaceName());
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void unregisterConsumer(Consumer consumer) {
		// TODO Auto-generated method stub
		String path = basePath + consumer.getInterfaceName() + "/"
				+ NODE_CONSUMES + "/" + consumer.getAddress();
		try {
			client.delete().forPath(path);
			logger.info("Sunsumer unregistered: "
					+ JacksonUtils.beanToJson(consumer));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<Provider> allProviders(String interfaceName) {
		// TODO Auto-generated method stub
		String path = basePath + interfaceName + "/" + NODE_PROVIDERS;
		List<Provider> providers = allProviders.get(path);
		if (providers != null) {
			return providers;
		}
		providers = new ArrayList<Provider>();
		try {
			List<String> nodes = client.getChildren().forPath(path);
			for (String node : nodes) {
				String providerJson = new String(client.getData().forPath(
						path + "/" + node));
				Provider provider = (Provider) JacksonUtils.json2Object(
						providerJson, Provider.class);
				providers.add(provider);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Provider> oldProviders = allProviders.putIfAbsent(path, providers);
		if (oldProviders != null) {
			providers = oldProviders;
		}
		return providers;
	}

	@Override
	public List<Consumer> allConsumers(String interfaceName) {
		// TODO Auto-generated method stub
		String path = basePath + interfaceName + "/" + NODE_CONSUMES;
		List<Consumer> consumers = new ArrayList<Consumer>();
		try {
			List<String> nodes = client.getChildren().forPath(path);
			for (String node : nodes) {
				String providerJson = new String(client.getData().forPath(
						path + "/" + node));
				Consumer consumer = (Consumer) JacksonUtils.json2Object(
						providerJson, Consumer.class);
				consumers.add(consumer);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return consumers;
	}

	@Override
	public Provider select(String host, String interfaceName,
			String serializeType) {
		// TODO Auto-generated method stub
		List<Provider> providers = allProviders.get(interfaceName);
		if (providers == null || providers.isEmpty()) {
			providers = allProviders(interfaceName);
			if (providers == null || providers.isEmpty()) {
				return null;
			}
		}
		int size = providers.size();
		return providers.get((int) (rpcCalls.incrementAndGet() % size));
	}

	public String getServers() {
		return servers;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	private boolean tryCreateProvidersWatch(String interfaceName) {
		String path = basePath + interfaceName + "/" + NODE_PROVIDERS;
		if (providerWatchers.containsKey(path)) {
			return true;
		}
		PathChildrenCache childrenCache = new PathChildrenCache(client, path,
				true);
		PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework client,
					PathChildrenCacheEvent event) throws Exception {
				ChildData data = event.getData();
				switch (event.getType()) {
				case CHILD_ADDED: {
					String providerJson = new String(data.getData());
					Provider provider = (Provider) JacksonUtils.json2Object(
							providerJson, Provider.class);
					List<Provider> providers = allProviders.get(provider
							.getInterfaceName());
					if (providers == null) {
						providers = new ArrayList<Provider>();
						List<Provider> oldProviders = allProviders.putIfAbsent(
								provider.getInterfaceName(), providers);
						if (oldProviders != null) {
							providers = oldProviders;
						}
					}
					providers.add(provider);
					logger.info("PROVIDER_ADDED : " + data.getPath() + "  数据:"
							+ providerJson);
					break;
				}
				case CHILD_REMOVED: {
					String providerJson = new String(data.getData());
					Provider provider = (Provider) JacksonUtils.json2Object(
							providerJson, Provider.class);
					List<Provider> providers = allProviders.get(provider
							.getInterfaceName());
					if (providers != null) {
						providers.remove(provider);
					}
					logger.info("PROVIDER_REMOVED : " + data.getPath()
							+ "  数据:" + providerJson);
					break;
				}
				case CHILD_UPDATED:
					String providerJson = new String(data.getData());
					Provider provider = (Provider) JacksonUtils.json2Object(
							providerJson, Provider.class);
					List<Provider> providers = allProviders.get(provider
							.getInterfaceName());
					if (providers != null) {
						providers.remove(provider);
						providers.add(provider);
					}
					logger.info("PROVIDER_UPDATED : " + data.getPath()
							+ "  数据:" + providerJson);
					break;
				default:
					break;
				}
			}
		};
		childrenCache.getListenable().addListener(childrenCacheListener);
		try {
			childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
			providerWatchers.put(path, childrenCache);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
