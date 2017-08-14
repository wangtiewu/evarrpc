package com.eastelsoft.etos2.rpc.registry;

public class Consumer {
	private String interfaceName;
	private String address;//host:port
	private String serializeType;
	
	
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSerializeType() {
		return serializeType;
	}
	public void setSerializeType(String serializeType) {
		this.serializeType = serializeType;
	}
	
	
}
