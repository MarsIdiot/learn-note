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
         ​	?	ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
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

 练习代码地址：<https://github.com/MarsIdiot/JavaTest/tree/master/src/netty/b_tcpbug>

###   4.TCP沾包/拆包问题解决之道

传输大报文时出现

#### 沾包与拆包

TCP是一个“流”协议，所谓流，就是没有界限的一串数据。就像河里的水没有分界线。

沾包拆包解释如下图：

![.\pictures\TCP沾包拆包图解说明.png](.\pictures\TCP沾包拆包图解说明.png)

#### 发生原因

TCP底层不了解上层业务数据的具体含义，它会根据TCP缓存区的实际情况进行包的划分，所以在业务上认为，一个完整的包会被拆分成多个包发送，或把多个小包封装成一个大的数据包发送。

总结：TCP缓存区划分包规则与业务之间的差异导致。

#### 解决策略

- 消息定长

固定长度，不够用空格补

- 在包尾加入换行符进行分割

- 将消息分为消息头和消息体(消息头存消息长度)


- 其他更复杂的应用层协议


#### 异常案例

练习代码地址:   <https://github.com/MarsIdiot/JavaTest/tree/master/src/netty/b_tcpbug>

#### 解决案例

在包尾加入换行符：LineBasedFrameDecoder +StringEncoder

LineBasedFrameDecoder ：将缓存区消息以换行符分割。以换行符为结束标志的解码器。

StringEncoder：将消息转为字符串

 练习代码地址:<https://github.com/MarsIdiot/JavaTest/tree/master/src/netty/b_tcpbug_fix_01_LineBasedFrameDecoder>

### 5.  分割符和定长解码器的应用

TCP以流的方式进行数据传输，上层应用协议为了对消息进行区分，常采用如下4中方式：

- 消息定长

- 换行符分割     （特殊分割符的一中，比较常用所以单独列出）

- 特殊分割符作为结束标志

- 消息=消息头（消息长度）+消息体

分割符解码器：DelimiterBasedFrameDecoder

定长解码器：FixedLengthFrameDecoder

#### 分割符解码器

练习代码址:<https://github.com/MarsIdiot/JavaTest/tree/master/src/netty/b_tcpbug_fix_02_DelimiterBasedFrameDecoder>

#### 定长解码器

练习代码地址:<https://github.com/MarsIdiot/JavaTest/tree/master/src/netty/b_tcpbug_fix_03_FixedLengthFrameDecoder>

#### 重要补充

客户端还是服务端，都需要添加解码器。这个工具业务需要进行选择。

	对于客户端来说：发送消息不会触发解码器，而在接收服务器端消息是会触发解码器。
	
	对于服务端来说：接收服务端消息触发到解码器，回复消息时不会触发解码器。   

总之，对于客户端还是服务端而言，只有接收他端的消息才会触发解码器。

## 中级篇  Netty编解码开发指南

### 6.编解码技术

#### java序列化缺点

无法跨语言

序列化后的码流太大     (与二进制编码后大小对比)

序列化性能太低           (与二进制编码效率对比)

#### 业界主流编解码框架

Google的Protobuf

Facebook的Thrift

JBoss的Marshalling

### 7.Java序列化

ObjectDecoder  + ObjectEncoder     （编解码，并可以处理TCP沾包拆包问题)

Netty Java序列化服务端开发		    (接收解码，发送编码)

Netty Java序列化客户端开发		    (发送编码，接收解码)

###  8.Google Protobuf编解码

Protobuf优点

1）在谷歌内部长期使用，产品成熟度高

2）跨语言，支持多种如C++、Java和Python.（使用protoc.exe代码生成工具，一次定义xxx.proto,即生产需要语言的源代码）

3）编码后消息更小，有利于存储和传输

4）编解码性能高

5）支持不同版本协议的向前兼容

6）支持定义可选字段和必选字段

#### Protobuf入门

1）开发前准备：proto.exe  + protobuf-java-2.5.0.jar

2）编写 xxx.proto文件，用proto.exe 生成对于的java文件

protoc ./SubscribeReqProto.proto --java_out=./

3）编码  对比编码前后数据

练习代码地址：<https://github.com/MarsIdiot/JavaTest/tree/master/src/netty/d_protobuf_self>

#### Netty的Protobuf使用

练习代码地址：<https://github.com/MarsIdiot/JavaTest/tree/master/src/netty/d_protobuf_02_netty>

### 9.JBoss Marsshalling编解码

同Protobuf相似，故省略。

## 高级篇 Netty多协议开发和应用





## 源码分析篇  Netty功能介绍和源码分析



## 架构和行业应用篇  Netty高级特性



## 问题记录篇

  1. IO复用模型与信号驱动IO模型的区别？后者是否效率更高？

2. 词汇：
   unix linux，内核，轮询，套接字，bio nio aio 区别，线程池

   线程池：灵活调配线程资源		


  		
  		
  		
  		
  		
  		
  		
  		