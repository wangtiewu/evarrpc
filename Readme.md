# evarrpc

evarrpc is a java rpc framework.It's base on netty,spring,zookeeper.

## Features

* Reflect supports jdk dynamic and cglib.

* Serialize supports jdknative、protostuff、protobuf, and also supports selfdef serialize protocol.

* Registry supports zookeeper,register service provider and consumer,dynamic notify provider change.

* NetworkFramework use netty4.

* Ease use、hight performance、auto fail-over.

## Usage with registry
* Step-1,install Zookeeper

Suppose zookeeper address is xx.xx.xx.xx:2181

* Step-2,define a interface
```Java
package test;
public interface HelloWorld
{
    String hello(String who);
}
```

* Step-3,implements this HelloWorld interface
```Java
package test;
public class HelloWorldImpl implements HelloWorld
{
    String hello(String who) {
        return "Hello "+who;
    }
}
```

* Step-4, define server's spring file, suppose file name is spring-rpcserver.xml
```Xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
    xmlns:p="http://www.springframework.org/schema/p" xmlns:etosrpc="http://www.eastelsoft.com/schema/etosrpc"
    xsi:schemaLocation="http://www.springframework.org/schema/beans 
    http://www.springframework.org/schema/beans/spring-beans-3.2.xsd 
    http://www.springframework.org/schema/tx 
    http://www.springframework.org/schema/tx/spring-tx-3.2.xsd 
    http://www.springframework.org/schema/aop 
    http://www.springframework.org/schema/aop/spring-aop-3.2.xsd 
    http://www.eastelsoft.com/schema/etosrpc 
    http://www.eastelsoft.com/schema/etosrpc/etosrpc.xsd">

    <bean id="registry" class="com.eastelsoft.etos2.rpc.registry.RegistryZookeeper" init-method="init" destroy-method="destroy">
        <property name="servers">
            <!-- host1:port,host2:port,host3:port-->
            <value>xx.xx.xx.xx:2181</value>
        </property>
    </bean>
    <bean id="rpcServer" class="com.eastelsoft.etos2.rpc.RpcServer" init-method="init" destroy-method="destroy">
        <property name="host">
            <value>xx.xx.xx.xx</value>
        </property>
        <property name="port">
            <value>8888</value>
        </property>
        <property name="serializeType">
            <!-- jdknative or protostuff or protobuf -->
            <value>protostuff</value>
        </property>
    </bean>
    <etosrpc:service id="helloWorld"
        interfaceName="test.HelloWorld" ref="helloWorldTarget" server="rpcServer" registry="registry" />
    <bean id="helloWorldTarget" class="test.HelloWorldImpl">
    </bean>
 </beans>
```

* Step-5, run server
```Java
public static void main(String[] args) {
    new ClassPathXmlApplicationContext(new String[]{"spring-rpcserver.xml"});
}
```

* Step-6, define client's spring file, suppose file name is spring-rpcclient.xml
```Xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
    xmlns:p="http://www.springframework.org/schema/p" 
    xmlns:etosrpc="http://www.eastelsoft.com/schema/etosrpc" 
    xsi:schemaLocation="http://www.springframework.org/schema/beans 
    http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
    http://www.springframework.org/schema/tx 
    http://www.springframework.org/schema/tx/spring-tx-3.2.xsd
    http://www.springframework.org/schema/aop 
    http://www.springframework.org/schema/aop/spring-aop-3.2.xsd
    http://www.eastelsoft.com/schema/etosrpc 
    http://www.eastelsoft.com/schema/etosrpc/etosrpc.xsd">

    <bean id="registry" class="com.eastelsoft.etos2.rpc.registry.RegistryZookeeper" init-method="init" destroy-method="destroy">
        <property name="servers">
            <!-- host1:port,host2:port,host3:port-->
            <value>xx.xx.xx.xx:2181</value>
        </property>
    </bean>
    <!-- serializeType: jdknative or protostuff or protobuf-->
    <etosrpc:reference id="helloWorld" interfaceName="test.HelloWorld"
                        serializeType="protostuff" registry="registry" />
</beans>
```

* Step-7, run client
```Java
public static void main(String[] args) {
    AbstractXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring-rpcclient.xml"});
    HelloWorld helloWorld = (HelloWorld) context
                .getBean("helloWorld");
    System.out.println(helloWorld.hello("World"));
    // will print out "Hello World"
}
```

* protobuf use issue

On Server and client machine,you need install google protobuf3,tested version is protobuf-3.4.0
