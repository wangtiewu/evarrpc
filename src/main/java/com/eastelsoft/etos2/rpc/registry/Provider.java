package com.eastelsoft.etos2.rpc.registry;

public class Provider {
	private String interfaceName;
	private String serverAddress;// host:port
	private String serializeType;

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getSerializeType() {
		return serializeType;
	}

	public void setSerializeType(String serializeType) {
		this.serializeType = serializeType;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj == this) {
			return true;
		}
		if (obj instanceof Provider) {
			Provider provider = (Provider) obj;
			if (provider.getInterfaceName().equals(getInterfaceName())
					&& provider.getServerAddress().equals(getServerAddress())
					&& provider.getSerializeType().equals(getSerializeType())) {
				return true;
			}
		}
		return false;
	}

}
