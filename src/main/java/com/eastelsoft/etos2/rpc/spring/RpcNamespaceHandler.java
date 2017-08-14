package com.eastelsoft.etos2.rpc.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class RpcNamespaceHandler extends NamespaceHandlerSupport {
	
    public void init() {
        registerBeanDefinitionParser("service", new RpcServiceParser());
        registerBeanDefinitionParser("reference", new RpcReferenceParser());
    }
}

