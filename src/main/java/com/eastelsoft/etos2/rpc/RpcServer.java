package com.eastelsoft.etos2.rpc;

public interface RpcServer {

	public void init() throws Exception;

	public void destroy();

	public void setHost(String host);
	
	public String getHost();

	public int getPort();

	public void setPort(int port);

	public String getSerializeType();

	public void setSerializeType(String serializeType);

	public int getWorkerThreadCount();

	public void setWorkerThreadCount(int workerThreadCount);
	
	public void registerHandler(String interfaceName, Object handler);
	
	public Object findHandler(String interfaceName);

	public void handleRequest(RpcRequestTask rpcInboundTask);
}
