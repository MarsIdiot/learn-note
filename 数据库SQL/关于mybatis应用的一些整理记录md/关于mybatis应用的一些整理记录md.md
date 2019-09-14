# 关于mybatis应用的一些整理记录md

## 1.常见面试题整理记录

参考：[mybatis常见面试题](http://www.mybatis.cn/category/interview/)

### 1、#{}和${}的区别是什么？

说明：#{}是**预编译处理**，${}是字符串替换(插值)。

备注：

对于#{}而言，先预编译为SQL语句的属性值，不会对预编译的SQL语句结构造成影响，只是作为{}变量值替换问号`?`。

使用#{}可以有效的防止SQL注入，提高系统安全性。原因在于：预编译机制。**预编译完成之后，SQL的结构已经固定，即便用户输入非法参数，也不会对SQL的结构产生影响，从而避免了潜在的安全风险。**

预编译是提前对SQL语句进行预编译，而其后注入的参数将*不会再进行SQL编译*。我们知道，SQL注入是发生在编译的过程中，因为恶意注入了某些特殊字符，最后被编译成了恶意的执行操作。而预编译机制则可以很好的防止SQL注入。

### 2、数据库链接中断如何处理

数据库的访问底层是通过tcp实现的，如果数据库链接中断，那么应用程序是不知道的。所以，面对数据库连接中断的异常，该怎么设置mybatis呢？

要想吃透这个问题，要明白链接中断产生的原因。其涉及到网络通信。在数据库链接种，connection操作底层是一个循环处理的过程，而这个过程自然就与时间有关系。而与时间有关的设置有：`max-idle_time`, `connect_timeout`。

max-idle_time表示最大空闲时间，超过这个时间socket就会关闭。这样好处在于：当数据库链接中断时，操作系统不用一直维持一个socket，而这是比较消耗资源的操作。(*长时间不进行库操作*)

connect_timeout表明链路的超时时间，可能网络环境不好，客户端长时间连不上服务端，这时候就需要设置timeout, 客户端超时就别一直连消耗资源。（长时间连接不上）

### 3、数据库插入重复如何处理

造成原因：并发

解决办法:在插入之前，加入redis机制-并发不可重复性，将并发问题转接到redis。

详细分析描述：

~~~java
插入的过程一般都是分两步的：先判断是否存在记录，没有存在则插入否则不插入。如果存在并发操作，那么同时进行了第一步，然后大家都发现没有记录，然后都插入了数据从而造成数据的重复。解决插入重复的思路可以是这样的：

（1）判断数据库是否有数据，有的话则无所作为。没有数据的话，则进行下面第2步
（2）向'redis' set key，其中只有一个操作a会成功，其他并发的操作b和c会失败的
（3）上面set key 成功的操作a，开始执行插入数据操作，无论是否插入数据成功，都在最后del key。【注】插入不成功可以多尝试几次，增加成功的概率。
（4）上面set key 失败的操作b和c，sleep一下，然后再判断数据库是否有数据，有数据则无所做为，没有数据则重复上面的set key，此时是b和c在竞争，失败者则无所作为，成功者则开始插入数据，然后无论插入成功还是失败则都要del key。
【注】既然是并发了，本身就是异常情况，就没有必要考虑用户体验了，就可以多sleep一会儿也无妨，不过对于单线程多事件处理的开发模式不要sleep太久。

总之，上面的过程就是：线程a 线程b 线程c，同时插入数据。如果线程a拿到'锁'之后，让它插入数据，它插入成功了，那么线程b 线程c啥也不用做；它插入失败了，线程b 线程c则抢锁，谁抢到了谁插入数据，不管最后是否成功，程序走到此步就可以了，已经完成了既定两个目标：执行插入，不重复插入。
~~~

### 4、事务执行过程中宕机的应对处理方式

问题：数据库插入百万级数据的时候，还没操作完，但是把服务器重启了，数据库会继续执行吗？ 还是直接回滚了？

答案：不会自动继续执行，不会自动直接回滚，但是可以人工手动选择继续执行或者直接回滚，依据是***事务日志***。

事务开启时，事务中的操作，都会先写入存储引擎的日志缓冲中，在事务提交之前，这些缓冲的日志都需要提前刷新到磁盘上持久化，这就是人们口中常说的“***日志先行*** ”(Write-Ahead Logging)。
日志分为两种类型：redo log和undo log
（1）redo log
在系统启动的时候，就已经为redo log分配了一块连续的存储空间，以顺序追加的方式记录redo log，通过顺序io来改善性能。所有的事务共享redo log的存储空间，它们的redo log按语句的执行顺序，依次交替的记录在一起。如下一个简单示例：
记录1：<trx1, insert...>
记录2：<trx2, delete...>
记录3：<trx3, update...>
记录4：<trx1, update...>
记录5：<trx3, insert...>
此时如果数据库崩溃或者宕机，那么当系统重启进行恢复时，就可以根据redo log中记录的日志，把数据库恢复到崩溃前的一个状态。未完成的事务，可以继续提交，也可以选择回滚，这基于恢复的策略而定。
（2）undo log
undo log主要为事务的回滚服务。在事务执行的过程中，除了记录redo log，还会记录一定量的undo log。undo log记录了数据在每个操作前的状态，如果事务执行过程中需要回滚，就可以根据undo log进行回滚操作。单个事务的回滚，只会回滚当前事务做的操作，并不会影响到其他的事务做的操作。

以下是undo+redo事务的简化过程，假设有2个数值，分别为A和B，值为1，2

1. start transaction;
2. 记录 A=1 到undo log;
3. update A = 3；
4. 记录 A=3 到redo log；
5. 记录 B=2 到undo log；
6. update B = 4；
7. 记录B = 4 到redo log；
8. 将redo log刷新到磁盘
9. commit

在1-8的任意一步系统宕机，事务未提交，该事务就不会对磁盘上的数据做任何影响。如果在8-9之间宕机，恢复之后可以选择回滚，也可以选择继续完成事务提交，因为此时redo log已经持久化。若在9之后系统宕机，内存映射中变更的数据还来不及刷回磁盘，那么系统恢复之后，可以根据redo log把数据刷回磁盘。所以，redo log其实保障的是事务的持久性和一致性，而undo log则保障了事务的原子性。

### 5、Java客户端中的一个Connection问题

问题：
		Java客户端中的一个Connection是不是在MySQL中就对应一个线程来处理这个链接呢？

答案：
		不是。凡是从线程思考问题的人，一般都是被Java技术的多线程思想所禁锢了，其实在高性能服务器端端开发底层往往靠***io复用***来处理，这种模式就是：**单线程+事件处理机制**。在MySQL里面往往有一个主线程，这是单线程（与Java中处处强调多线程的思想有点不同哦），它不断的循环查看是否有socket是否有读写事件，如果有读写事件，再从线程池里面找个工作线程处理这个socket的读写事件，完事之后工作线程会回到线程池。所以：Java客户端中的一个Connection不是在MySQL中就对应一个线程来处理这个链接，而是由监听socket的***主线程+线程池***里面固定数目的工作线程来处理的。

### 6、[Mybatis中的Dao接口和XML文件里的SQL是如何建立关系的？](http://www.mybatis.cn/archives/467.html)

### 7、实体类中的属性名和表中的字段名不一样

简单说来就是：取别名

详细说明如下：

```xml
1. 第1种解决方案：通过在查询的sql语句中定义字段名的别名，让字段名的别名和实体类的属性名一致。
<select id="getOrder" parametertype="int" resultetype="cn.mybatis.domain.order">
    select order_id id, order_no orderNo ,order_price price form orders where order_id=#{id};
</select>

2. 第2种解决方案：通过<resultMap>来映射字段名和实体类属性名的一一对应的关系。

<select id="getOrder" parameterType="int" resultMap="orderResultMap">
    select * from orders where order_id=#{id}
</select>

<resultMap id="orderResultMap" type="cn.mybatis.domain.order" >
    <!–用id属性来映射主键字段–>
    <id property="id" column="order_id">
    <!–用result属性来映射非主键字段，property为实体类属性名，column为数据表中的属性–>
    <result property= "orderNo" column="order_no"/>
    <result property="price" column="order_price"/>
</reslutMap>
```

8、模糊查询like语句

第1种：在Java代码中添加sql通配符。

```java
string wildcardname = "%tom%";
list<name> names = mapper.selectLike(wildcardname);
<select id="selectLike">
    select * from users where name like #{value}
</select>
```

第2种：在sql语句中拼接通配符，会引起sql注入

```xml
使用#{}
<select id="selectLike">
    select * from users where name like "%"#{value}"%"
</select>

实战补充:使用$
<sql id="where_fuzzy_query">
    <dynamic prepend="where">
        <!--<isNotEmpty property="id" prepend="and">
    baseUser.id = #id#
   </isNotEmpty>-->
        <isNotEmpty property="uniqueId" prepend="and">
            baseUser.unique_id  like '$uniqueId$%'
        </isNotEmpty>
        <isNotEmpty property="name" prepend="and">
            baseUser.name like '%$name$%'
        </isNotEmpty>
        <isNotEmpty property="licenseType" prepend="and">
            baseUser.license_type = #licenseType#
        </isNotEmpty>
    </dynamic>
</sql>

```

### 8、什么是MyBatis的接口绑定？有哪些实现方式？

接口绑定，就是在MyBatis中任意定义接口，然后把接口里面的方法和SQL语句绑定，我们直接调用接口方法就可以，这样比起原来了SqlSession提供的方法，可以有更加灵活的选择和设置。

接口绑定有两种实现方式，一种是通过`注解绑定`，就是在接口的方法上面加上 @Select、@Update等注解，里面包含Sql语句来绑定；

另外一种就是通过`xml`里面写SQL来绑定，在这种情况下，要指定xml映射文件里面的namespace必须为接口的全路径名。

当Sql语句比较简单时候，用注解绑定，当SQL语句比较复杂时候，用xml绑定。一般情况下，用xml绑定的比较多。

使用MyBatis的mapper接口调用时要注意的事项有：

（1）Mapper接口方法名和mapper.xml中定义的每个sql的id相同；

（2）Mapper接口方法的输入参数类型和mapper.xml中定义的每个sql 的parameterType的类型相同；

（3）Mapper接口方法的输出参数类型和mapper.xml中定义的每个sql的resultType的类型相同；

（4）Mapper.xml文件中的namespace即是mapper接口的类路径。

### 9、Dao接口的工作原理

通常一个Xml映射文件，都会写一个Dao接口与之对应，请问，这个Dao接口的工作原理是什么？Dao接口里的方法，参数不同时，方法能重载吗？

Dao接口即Mapper接口。接口的全限名，就是映射文件中的namespace的值；接口的方法名，就是映射文件中Mapper的Statement的id值；接口方法内的参数，就是传递给sql的参数。

Mapper接口是没有实现类的，当调用接口方法时，接口全限名+方法名拼接字符串作为key值，可唯一定位一个MapperStatement。在Mybatis中，每一个 <select>、<insert>、<update>、<delete>标签，都会被解析为一个MapperStatement对象。

举例来说：cn.mybatis.mappers.StudentDao.findStudentById，可以唯一找到namespace为 com.mybatis.mappers.StudentDao下面 id 为 findStudentById 的 MapperStatement。

Mapper接口里的方法，是不能重载的，因为是使用 全限名+方法名 的保存和寻找策略。Mapper 接口的工作原理是JDK动态代理，Mybatis运行时会使用JDK动态代理为Mapper接口生成代理对象proxy，代理对象会拦截接口方法，转而执行MapperStatement所代表的sql，然后将sql执行结果返回。

### 10、mybatis[的缓存机制](https://blog.csdn.net/u012373815/article/details/47069223)





























