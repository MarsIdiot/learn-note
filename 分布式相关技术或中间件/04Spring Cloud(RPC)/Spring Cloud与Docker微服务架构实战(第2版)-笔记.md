##关于Spring Cloud与Docker微服务架构实战（第2版）笔记

###2019.5.13记录  规划
	#Spring Cloud与Docker微服务架构实战（第2版）/【第一天】
			1. 目的：
				1)通过快速实战学习了解微服务，打好微服务基础;
				2)正好以后公司也需要用SpringBoot开发  很可能需要引入Spring Cloud来管理微服务之间的调度、监控、容错等。
			2. 规划
				学习背景：学习需要落地，抽空之余学习本书。
				学习内容：共14章 2天一章节，一天理论+一天实战
				时间估算：预计一个月都有些艰难：可能一整天都在工作。所以说：利用好时间，一有空闲就学习本书。
				工作：核心为工作，切勿舍本逐末。
			3. 笔记记录
				不要一来就写笔记，那样就像拿放大镜看路上的风景一样 管中窥豹
				切勿为了笔记而笔记 
				笔记一定明白才写上去  最好用自己的话描述  
				
			3. 今日内容：
				1)第一章 微服务架构概述
				2)第二章 微服务开发框架——Spring Cloud
				3)第三章 开始使用Spring Cloud实战微服务
				4)第四章 微服务注册与发现
					至4.8元数据
					
			4. 今日笔记
				目前看起来本书有些过时，主要是Spring版本及编码上，但对有些知识的讲解比较适合入门。这是自己还在看本书的原因。
				先对Spring Cloud基础及实战过程有些了解，再使用其他最新学习资料进行实战  避免出现开发成本太高，舍本逐末。
				
				解决问题：
				定义：
				发展历史：
				技术基础：

###2019.5.14记录		
	#Spring Cloud与Docker微服务架构实战（第2版）/【第二天】
			1. 今日内容：
				昨日遗留：第四章 微服务注册与发现  至4.8元数据
				1)第四章 微服务注册与发现
					更多的的是对Eureka的实战  配置(【集群、安全认证、元数据、自我保护】、多网卡IP选择、健康检查)
				2)第五章 使用Ribbon实现客户端侧负载均衡
					基于负载均衡算法，自动替消费者选择请求哪个提供者者实例。			
				3)第六章 使用Feign实现声明式调用
				
				4)第七章 使用Hystrix实现微服务的容错处理
				5)第八章 使用Zuul构建服务网关	
					
			2. 今日笔记
				Eureka[ju?ri?k?] ：管理微服务实例访问地址，让消费端在面对微服务地址变动的情况下而不用变动。
				Ribbon[r?b?n]：当消费端多次发起大数量相同的请求时，均衡分摊到多个相同实例的微服务上(当访问量大时，同一微服务会部署多个实例，必须采用负载均衡来防止服务出错或宕机)
				Feign[fein] ：声明式的API调用
				Hystrix：
					官方：实现超时机制和断路器模式的工具类库。防止雪崩效应。
						超时机制：请求超时时结束请求，以达到尽快释放资源
						断路器模式：统计请求的失败次数，以决定是否继续调用依赖的服务
					Own:动态控制请求量。
				Zuul：服务网关,管理微服务的调用


			5. 今日体会
				1) Eureka服务器的重要性
					原因：在学习使用Tubine聚合监控数据时才深刻体会到，疑问监控数据是怎么取到的？？
					结论：不同微服务(一般服务、监控服务)之间的调用交互都是通过Eureka服务器进行的。
					所以说：微服务中注册中心的是极其重要的，就是微服务项目的中枢神经。
				2) 如何使用此书，目的是什么？
					本书在知识点上有些陈旧，但从简单入门来说，不失为一本不错的书。把此书作为对Spring Cloud
				大致认知的一本书，再利用最新的资料加以实战，方可算真正入门。
				3) 对学习新技术的浅见——注重阶段性
					对一个新事物的掌握必然是从认识、熟悉、使用、熟悉使用才到深入原理的，如果连基本的使用都不会，
				而一味关注其代码逻辑、设计原理等，那岂非本末倒置了。

###2019.5.15记录		
	#Spring Cloud与Docker微服务架构实战（第2版）/【第三天】
			1. 今日内容：
				昨日遗留：第八章 使用Zuul构建服务网关  
				1)第八章 使用Zuul构建服务网关	
				2)第九章 使用Spring Cloud Config统一管理微服务配置  14:30——16：00(1.5h)
				3)
				4)
			2. 今日笔记
				Zuul：服务网关  管理微服务的调用
					路由控制
					结合Ribbon(负载均衡)、Hystrix(容错)
					高可用(集群)(Ngnix)
				Spring Cloud Config：管理所有微服务的配置 达到不停用服务的情况下动态更新配置
					1) Config Server:配置中心微服务
						从git等后台仓库获取配置信息(这里包含所有微服务的各种配置)(application.yml)
					2) Config Client：改造后的微服务  
						可通过请求获取到Config Server中的配置信息(bootstrap.yml)
					3) Spring Cloud Bus ：自动刷新配置
						需对Spring Cloud Bus微服务引入MQ  联级触发(推送批量消息)
						缺点：需要手动触发其中一个微服务  并且放在Config Client处导致混乱不清(与业务配置需分开)
					4) 自动刷新配置架构改进：将触发放在Config Server处；最好能在配置被修改时自动触发
					5)高可用


​				




​			