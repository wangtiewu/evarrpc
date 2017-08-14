package com.eastelsoft.etos2.rpc.registry;

import java.util.List;

public interface Registry {
	boolean registerProvider(Provider provider);

	void unregisterProvider(Provider provider);

	boolean registerConsumer(Consumer consumer);

	void unregisterConsumer(Consumer consumer);
	
	List<Provider> allProviders(String interfaceName);
	
	List<Consumer> allConsumers(String interfaceName);
	
	Provider select(String host, String interfaceName, String serializeType);
}
