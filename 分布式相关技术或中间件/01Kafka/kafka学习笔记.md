研究Kafka消息广播、单播

```
	生产与消费
	发布与订阅
	group、topic、partion、broker Server之间的关系
	消息生产：规则，消息更新是覆盖吗？
```

​		
​		

```
	topic、Partition、segement的关系？
		包含关系？
```



​			
​		

​		

## 0、问与答

1）kafka被消费了的消息还可以再次被消费吗?

```
可以。
kafka集群会保留所有的消息，无论其被消费与否。两种策略删除旧数据：
	一基于时间的SLA(服务水平保证)，消息保存一定时间（通常为7天）后会被删除、
	二是基于Partition文件大小，可以通过配置$KAFKA_HOME/config/server.properties
```

2）group：group在发布是需要指定吗？在消费时必须指定吗？

```
发布时如不指定，为默认的group。
每个Consumer属于一个特定的Consumer Group（可为每个Consumer指定group name，若不指定group name则属于默认的group），会维护一个索引，用于标识一个消费集群的消费位置。为了对减小一个consumer group中不同consumer之间的分布式协调开销，指定partition为最小的并行消费单位，即一个group内的consumer只能消费不同的partition。

消费时？如果发布时指定消费时也需指定才能消费到？该是如此
```

3）消息有序性？

```
kafka 的消息模型是对 topic 分区以达到分布式效果。每个 topic 下的不同的 partitions (区)只能有一个 Owner 去消费。所以只有多个分区后才能启动多个消费者，对应不同的区去消费。其中协调消费部分是由 server 端协调而成。使用者不必考虑太多。只是消息的消费是无序的。

总结：如果想保证消息的顺序，那就用一个 partition。 kafka 的每个 partition 只能同时被同一个 group 中的一个 consumer 消费。
```

4)   每个 topic 下的不同的 partitions (区)只能有一个 Owner 去消费？

另外：一个group内的consumer只能消费不同的partition,于是问题：不同group的Owner能消费同一个partition吗？

```text
不同的组之间消费是相互不受影响的，相互隔离。
如果是同一个组，只能有一个Owner去消费一个partition

kafka 的消息模型是对 topic 分区以达到分布式效果。每个 topic 下的不同的 partitions (区)只能有一个 Owner 去消费。所以只有多个分区后才能启动多个消费者，对应不同的区去消费。其中协调消费部分是由 server 端协调而成。

总结：不同组互不影响，同一组内每个 topic 下的一个partition(区)只能有一个 Owner 去消费。
```

5）kafka协调存储的机制？



6） kafka协调消费的机制？







## 1、基本概念

参考：https://github.com/aalansehaiyang/technology-talk/blob/master/middle-software/kafka.md

Kafka是由LinkedIn开发的一个开源分布式基于发布/订阅的消息系统，Scala编写。 Producer向broker push消息；Consumer从broker pull消息（pull模式则可以根据Consumer的消费能力以适当的速率消费消息）

- Broker

  ```text
  消息中间件处理结点，一个Kafka节点就是一个broker，多个broker可以组成一个Kafka集群。
  ```

- Topic

```tex
  每条发布到Kafka集群的消息都有一个类别，这个类别被称为Topic，逻辑上可称之为队列（物理上不同Topic的消息分开存储，逻辑上一个Topic的消息虽然保存于一个或多个broker上但用户只需指定消息的Topic即可生产或消费数据而不必关心数据存于何处）
  Kafka集群能够同时负责多个topic的分发。即可以并发发布消息而不受影响。
```

- Producer

  ```
  负责发布消息到Kafka broker。
  ```

- Consumer

  ```tex
  消息消费者，向Kafka broker读取消息的客户端。
  ```

- Consumer Group

  ```tex
  每个Consumer属于一个特定的Consumer Group（可为每个Consumer指定group name，若不指定group name则属于默认的group），会维护一个索引，用于标识一个消费集群的消费位置。
  为了对减小一个consumer group中不同consumer之间的分布式协调开销，指定partition为最小的并行消费单位，即一个group内的consumer只能消费不同的partition。
  ```

## 2、kafak存储机制

参考：https://tech.meituan.com/2015/01/13/kafka-fs-design-theory.html

涉及概念

- Partition

  ~~~
  Partition是物理上的概念，每个Topic包含一个或多个Partition，每个Partition对应一个逻辑log，由多个segment组成。
  ~~~

- Segment

  ```text
  partition物理上由多个segment组成。
  ```

- offset

  ```text
  每个partition都由一系列有序的、不可变的消息组成，这些消息被连续的追加到partition中。partition中的每个消息都有一个连续的序列号叫做offset,用于partition唯一标识一条消息。
  ```

Partition命令规则

  在Kafka文件存储中，同一个topic下有多个不同partition，每个partition为一个目录，partiton命名规则为topic名称+有序序号，第一个partiton序号从0开始，序号最大值为partitions数量减1。

Kafka高效文件存储设计特点

- Kafka把topic中一个parition大文件分成多个小文件段，通过多个小文件段，就容易定期清除或删除已经消费完文件，减少磁盘占用。
- 通过索引信息可以快速定位message和确定response的最大大小。
- 通过index元数据全部映射到memory，可以避免segment file的IO磁盘操作。
- 通过索引文件稀疏存储，可以大幅降低index文件元数据占用空间大小。