## Redis

### 一、NOSQL

#### 1.定义

​	NOSQL(Not only SQL)，非关系型数据库，表与表之间是没有关系的

#### 2.为什么使用Redis？

​	对数据库高并发读写的需求

​	海量存储

​	高可扩展性，高可用性

#### 3.Redis是什么？

​	Redis是C语言开发的开源的高性能键值对(key-value)NOSQL数据库

### 二、安装redis———linux（重点）

### 三、jedis

​	使用java去操作Redis

### 四、redis的数据操作类型5中

​	**string  字符型**

​	list  列表	 有序  不唯一

​	set  集合   无序  唯一

​	sortset   有序集合   唯一

​	**hash    散列  更像json数据**

### 五、redis其他

#### 	1.通用操作

​		key匹配，删除key，key是否存在等

#### 	2.特性

​		多数据库：最多16个，默认0库   可将key移库

​		服务器命令：ping,select,quit,flushdb等

​		消息订阅与发布

​		事务：其实redis事务属于批量操作——即使有错，只要是正确条命令都将被执行，与命令顺序无关

#### 	3.持久化

​		RDB持久化：时间间隔内备份快照

​		AOF持久化：日志形式，存所有的命令   

​		可同时使用两种持久化方式

​	

