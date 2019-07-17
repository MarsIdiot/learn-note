# Netty权威指南(第二版)笔记

## 初级篇

Netty(初级篇)
  	先看看基础知识 大致原理  对其有个大致了解
  	再入门实战  搭建服务 如何发送请求  如何监听消息请求
  	后面有时间  再继续深入研究

### 1.基础IO

#### 1.1  UNIX的5种IO模型

1)阻塞IO模型
  	进程下达命令后，需要获取相应的数据包，内核会一直等待数据包直到准备就绪或出错，直到此刻才继续后续操作，所以等待过程称为阻塞。

2)非阻塞IO模型
  	与阻塞的区别在于，内核处增加判断条件，如果内核还没有准备好数据包,则进程会反复下达命令(轮询)直到内核处的数据包准备好，不会一直阻塞在内核处。

3)IO复用模型
  	对比改进：非阻塞中虽然会反复轮询，但是依旧是在同一时间段执行同一进程，效率依旧不高。
  	实现方式：所以在复用模型中引入轮询机制，该轮询机制可接收多个命令请求，当轮询到有数据包准备就绪时，立即回调对应服务执行后续操作(后续操作：将数据从内核复制到用户的缓存区)。
  	分析：此过程阻塞在轮询机制上，但其动态批量轮询机制让满足条件的服务进行操作，可避开某个服务一直处于阻塞状态。

4)信号驱动IO模型
  	对比：复用模型的改进版，信号处理程序代替轮询机制功能，实则就是轮询机制(只是实现方式不同)。
  	实现方式：目前感觉与复用模型没啥区别(留白？？？？)

5)异步IO模型
  	实现方式：进程下达命令让内核启动某个操作，并让内核在整个操作完成后(即：数据包准备就绪后，还要将数据从内核复制到用户的缓存区)才通知用户。
  	与信号驱动区别：信号驱动IO由内核通知我们何时可以开始一个IO操作;而异步IO模型由内核通知我们IO操作何时完成。即：前者在IO操作开始时通知，后者在结束时通知。
  		

对于操作系统而言，底层是支持异步IO通信的，不过Java在很长一段时间没有提高相应的类库。不过在JDK1.4首次引入(2004年)。

#### 1.2 IO多路复用技术

  	

#### 1.3 Java的IO演进

  	BIO——>NIO——>AIO

## 入门篇  Netty开发指南

### 2.NIO入门

2.1 传统BIO
Block阻塞IO

2.2 伪异步IO
在BIO基础上加入线程池，但无法解决其同步阻塞问题。

2.3 NIO编程

1)NIO简介
  	从流到通道的转变(单向——>双向)
  	缓冲区Buffer的加入(读写效率)
  	多路复用器Selector(轮询多个Channel)(对NIO编程至关重要)：处理多个客户端的并发接入

2)服务端序列流程

初始化：
  	1) 开启服务监听通道 绑定监听地址
  	2) 开启多路复用器Selector线程
  	3) 将服务监听通道注册到Selector的监听Accept事件(即：客户端接入)
  	4) Selector内无线循环轮询准备就绪的Key(如果没有客户端接入，则Selector每隔1s被唤醒一次)

处理请求：
  	1) 客户端发起接入请求
  	2) Selector的监听Accept事件会监听到客户端的接入，将客户端的信息写入到新的Key
  	(Key即：SelectionKey， 包含含有根据客户端请求消息封装的通道channel，并且该key属性种类为OP_ACCEPT)
  	3) Selector会轮询到新加入的Accept操作，调用handleInput(key)方法进行处理，此处会把SelectionKey(Accept)复制一份，但改为Read读操作；
  	待下次轮询就会执行Read操作（此处轮询过程把Accept操作转换为Read读操作，这样就可以根据SelectionKey操作位的类型判断网络事件类型）
  	4) 轮询到Read操作时，对应的通道会读取消息到缓冲区并对其解码
  	5) 将消息封装投递到业务线程池中，进行业务逻辑编排
  	6) 将Pojo对象编码到缓冲区，利用通道异步writer接口将异步消息发送给客户端。
  	
3)客户端序列流程

### 3.Netty入门应用

#### 1.服务端开发

a. 服务端启动类编写
  	1) 服务端启动辅助类配置(ServerBootStrap)
  		绑定线程组、通道(NioServerSocketChannel)、TCP参数、IO处理Handler类
  	2) 绑定端口，同步等待成功
  	3) 等待服务器监听端口关闭
  	4) 优雅退出，释放线程池资源
  	5) main函数编写 

b. 服务端Handle类编写(继承xxxChannelxxHandler，即：SimpleChannelInboundHandler<——ChannelInboundHandlerAdapter)
  	6) 编写Handler类
  		1) 接收消息
  			channelRead(ChannelHandlerContext ctx, Object msg){ctx.write(msg);}
  				+
  			channelReadComplete(ChannelHandlerContext ctx){ ctx.writeAndFlush()} 刷新缓存并关闭通道结束通信
  		2) 异常处理
  			exceptionCaught(ChannelHandlerContext ctx, Throwable cause){ctx.close();}

补充：关于服务端关闭通信通道的优化建议

   1. 第一种方法(推荐)：写一个空的buf，并刷新写出区域。完成后关闭sock channel连接。
        	?	?	ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
   2. 第二种方法：在client端关闭channel连接，这样的话，会触发两次channelReadComplete方法。
        	?	ctx.flush(); 
   3. 第三种：改成这种写法也可以，但是这中写法，没有第一种方法的好。
        	ctx.flush().close().sync(); 		

#### 2.客户端开发

a.  客户端启动类编写
  	1) 客户端启动辅助类配置(BootStrap)
  		绑定线程组、通道(NioSocketChannel)、TCP参数、IO处理Handler类
  	2) 绑定端口，发起异步连解操作
  	3) 等待客户端监听端口关闭
  	4) 优雅退出，释放线程池资源
  	5) main函数编写 

b.  客户端Handle类编写(继承xxxChannelxxHandler,即：ChannelInboundHandlerAdapter)
  	1) 发送消息
  	channelActive(ChannelHandlerContext ctx){ctx.writeAndFlush();} 

  	2) 接收消息
  	channelRead0(ChannelHandlerContext ctx, ByteBuf msg){ctx.channel().close().sync();}

  	3) 异常处理
  	exceptionCaught(ChannelHandlerContext ctx, Throwable cause){ctx.close();}

补充：发送消息时必须存在flush		

 练习代码地址：<https://github.com/MarsIdiot/JavaTest/tree/master/src/netty/testlearn>	

###   4.TCP沾包/拆包问题解决之道

传输打大报文时出现

#### 沾包与拆包

TCP是一个“流”协议，所谓流，就是没有界限的一串数据。就像河里的水没有分界线。

含义及其导致原因：TCP缓存区划分包规则与业务之间的差异导致

?	TCP底层不了解上层业务数据的具体含义，它会根据TCP缓存区的实际情况进行包的划分，所以在业务上认为，一个完整的包会被拆分成多个包发送，或把多个小包封装成一个大的数据包发送。

![.\pictures\TCP沾包拆包图解说明.png](.\pictures\TCP沾包拆包图解说明.png)

#### 发生原因



#### 解决策略

- 消息定长

固定长度，不够用空格补

- 在包尾加入换行符进行分割

- 将消息分为消息头和消息体(消息头存消息长度)


- 其他更复杂的应用层协议


#### 异常案例

练习代码地址:    <https://github.com/MarsIdiot/JavaTest/tree/master/src/netty/tcpbug>

#### 解决案例

LineBasedFrameDecoder +StringEncoder

LineBasedFrameDecoder ：将缓存区消息以换行符分割。以换行符为结束标志的解码器。

StringEncoder：将消息转为字符串

 练习代码地址:   <https://github.com/MarsIdiot/JavaTest/tree/master/src/netty/tcpbug>

### 5.分割符和定长解码器的应用



## 中级篇  Netty编解码开发指南





## 高级篇 Netty多协议开发和应用





## 源码分析篇  Netty功能介绍和源码分析



## 架构和行业应用篇  Netty高级特性



## 问题记录篇

  1. IO复用模型与信号驱动IO模型的区别？后者是否效率更高？

2. 词汇：
   unix linux，内核，轮询，套接字，bio nio aio 区别，线程池

   线程池：灵活调配线程资源		


  		
  		
  		
  		
  		
  		
  		
  		