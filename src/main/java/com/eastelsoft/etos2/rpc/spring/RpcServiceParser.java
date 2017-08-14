package com.eastelsoft.etos2.rpc.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.w3c.dom.Element;

public class RpcServiceParser implements BeanDefinitionParser {
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String interfaceName = element.getAttribute("interfaceName");
		String ref = element.getAttribute("ref");
		String registry = element.getAttribute("registry");
		String server = element.getAttribute("server");

		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(RpcService.class);
		beanDefinition.setLazyInit(false);
		beanDefinition.getPropertyValues().addPropertyValue("interfaceName",
				interfaceName);
		beanDefinition.getPropertyValues().addPropertyValue("ref", ref);
		beanDefinition.getPropertyValues().addPropertyValue("registry", registry);
		beanDefinition.getPropertyValues().addPropertyValue("server", server);

		parserContext.getRegistry().registerBeanDefinition(interfaceName,
				beanDefinition);

		return beanDefinition;
	}
}
