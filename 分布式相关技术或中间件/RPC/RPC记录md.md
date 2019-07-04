

#什么是RPC

根据serviceId获取服务提供者ip（传入、本地缓存、prism服务治理平台远程获取、本地配置文件(modules.properties)(依次)）

根据ip和serviceId调用远程服务(Netty支持NIO)

服务端接收，根据serviceId获取bean并执行



服务提供者：

​	编写RPC接口

​	配置一些基本配置：nettyy配置(端口)，注册中心配置(nettyConfig.properties),zk

​	在prism服务治理平台注册服务

服务消费者：负责获取ip和serviceId，发起远程服务请求

​	需要一些插件：StartInitListener

