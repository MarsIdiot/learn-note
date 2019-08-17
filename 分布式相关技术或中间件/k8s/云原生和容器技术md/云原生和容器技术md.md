# 云原生和容器技术

•云原生和容器科普
•容器与虚机的几大区别
•开发最常用到的容器实战技
•为什么要上云？
•上云能带来什么？
•应用如何上云？
•你距离云上全栈开发只差Kubernetes的加持
•如丝般顺滑：云原生时代的DevOps体验
•微服务治理新理念：Service Mesh科普

## 1. 云原生和容器科普

### 云原生

my1.0:提供一些列基础服务，配置，协调开发、部署、测试、运营、迭代。

my2.0（2019-8-17）:云原生更像一种让应用变得更加轻量的思想。将非业务需求从笨重的胖应用中剥离出来，然后上云并下沉为基础设施。目的在于构建和运行可弹性扩展的应用。

敖小剑(蚂蚁金服)：云原生代表着原生为云设计。详细的解释是：应用原生被设计为在云上以最佳方式运行，充分发挥云的优势。

**云原生（Cloud Native）的定义**

[Pivotal](https://pivotal.io/) 是云原生应用的提出者。最初对云原生的探讨可参看，详见[迁移到云原生应用架构](https://jimmysong.io/migrating-to-cloud-native-application-architectures/)。其最新定义详情查看：[云原生](https://pivotal.io/cloud-native)

CNCF对云原生的定义

~~~tex
Cloud native technologies empower organizations to build and run scalable applications in modern, dynamic environments such as public, private, and hybrid clouds. Containers, service meshes, microservices, immutable infrastructure, and declarative APIs exemplify this approach.
云原生技术有利于各组织在公有云、私有云和混合云等新型动态环境中，构建和运行可弹性扩展的应用。云原生的代表技术包括容器、服务网格、微服务、不可变基础设施和声明式API。
~~~

目的

~~~tex
The Cloud Native Computing Foundation seeks to drive adoption of this paradigm by fostering and sustaining an ecosystem of open source, vendor-neutral projects. We democratize state-of-the-art patterns to make these innovations accessible for everyone.
这些技术能够构建容错性好、易于管理和便于观察的松耦合系统。结合可靠的自动化手段，云原生技术使工程师能够轻松地对系统作出频繁和可预测的重大变更。
~~~

**云计算演进史**

1、虚拟机技术成熟。2000 年前后 x86 的虚拟机技术成熟后，云计算逐渐发展起来；

2、基于虚拟机技术的云计算。基于虚拟机技术，陆续出现了 IaaS/PaaS/FaaS 等形态，以及他们的开源版本；

3、容器的兴起和编排大战。2013 年 docker 出现，容器技术成熟，然后围绕容器编排一场大战，最后在 2017 年底，kubernetes 胜出。2015 年 CNCF 成立，并在近年形成了 cloud native 生态。

4、云的形态变化。供应商提供的功能越来越多，而客户或者说应用需要自己管理的功能越来越少。

小结：云计算二十年来变化巨大

- 从物理机到虚拟机到容器

- IaaS,Paas,SaaS,CaaS,FaaS等多种形态

- 公有云、私有云、混合云

- `kubernetes` 出现并成为事实标准

- 在低可用率的硬件上搭建高可用率的服务

**云原生应用应该是什么样子**

云原生应用应该往轻量化方向努力。

**云原生的中间件该如何发展**

中间件应该下沉到基础设施，然后中间件对应用的赋能方式要云原生化，即轻量化。

**cloud native 和 service mesh 的主要区别**

cloud native是道, service mesh是术。 前面ppt里有, cloudnative, 具体的实现可能就包括: 微服务, 容器, service mesh等.。service mesh是为了实现云原生, 所提供的一种服务发现和治理的方式。

**云和应用如何衔接**

满足因素：既可以让应用使用云提供的能力，又不至于侵入应用，破坏应用的云原生特性？简单说，就是要实现应用无感知。

赋能方式：在运行时为应用`动态赋能`

动态赋能方式：

- 流量透明劫持（加入Sidecar 容器）。

在运行时透明的改变远程调用请求的行为，我们称之为 “流量透明劫持”。简单说来，就是在应用被调用前后对请求进行拦截然后赋予相应的服务能力，典型如服务发现 / 负载均衡 / 实施各种路由策略 / 认证 / 加密等一系列能力。

具体流程以Istio iptables为例子分析如下。

![.\pictures\透明劫持的具体流程-Istio.png](.\pictures\透明劫持的具体流程-Istio.png)

透明劫持的最大优点是对代码无侵入。

- DNS

应用如何控制赋予的能力：声明式 API 

**如何使产品更加符合云原生**

功能/性能之外

- 关注易用性

- 关注开发者体验

- 把应用和应用开发者当成baby来呵护

依托云原生架构

- 基于云，基于容器，基于kubernetes

顺势而为

- 顺着飞轮的方向，迎合云原生和社区方向

### 容器

对单个或一组软件的封装，无需安装即可运行。可随便移植。

## 2. 容器与虚机的几大区别

容器  

启动快速

沙箱机制（容器之间完全隔离，互不影响）

性能开销极低



## 3.开发最常用到的容器实战技

如：docker使用

## 4. 为什么要上云？

数据中心化，便于管理

迅捷开发

开发、测试、运维之间的桥梁

## 5. 上云能带来什么？

开发效率

各部门、岗位协调



## 6. 应用如何上云？

## 7. 你距离云上全栈开发只差Kubernetes的加持

## 8. 如丝般顺滑：云原生时代的DevOps体验

## 9. 微服务治理新理念：Service Mesh科普