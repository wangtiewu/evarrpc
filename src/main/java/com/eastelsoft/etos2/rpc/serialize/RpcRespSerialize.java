package com.eastelsoft.etos2.rpc.serialize;

public interface RpcRespSerialize<ChannelPipeline> {
	String getSerializeType();
	void initChannel(ChannelPipeline arg0) throws Exception;
}
