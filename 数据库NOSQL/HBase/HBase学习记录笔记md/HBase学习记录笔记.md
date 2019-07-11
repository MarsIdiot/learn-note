## HBase学习记录笔记md

学习文档：<https://www.yiibai.com/hbase/hbase_architecture.html>

### 1、HBase简介

~~~xml

~~~

结构简单，数据量非常大的数据(通常在TB级别以上)，如历史订单记录，日志数据，监控Metris数据等等，HBase提供了简单的基于Key值的快速查询能力。

### 2、HBase架构

在HBase中，表被分割成区域，并由区域服务器提供服务。

HBase有三个主要组成部分：客户端库，主服务器和区域服务器。区域服务器可以按要求添加或删除。如下：

![.\pictures\HBase有三个主要组成部分.jpg](.\pictures\HBase有三个主要组成部分.jpg)

主服务器

  分配区域给区域服务器（zookper配合）；

  处理不同区域的负载均衡；

  负责模式变化和其他元数据操作，如创建表和列。

区域

  区域只不过是被表拆分，并分布在区域服务器。

区域服务器

  与客户端进行通信并处理数据相关的操作；

​    ....

区域服务器：包含区域和存储,如图：

![.\pictures\区域服务器组成部分.jpg](.\pictures\区域服务器组成部分.jpg)

 存储包含内存存储和HFiles。memstore就像一个高速缓存。在这里开始进入了HBase存储。数据被传送并保存在Hfiles作为块并且memstore刷新。

### 3、



