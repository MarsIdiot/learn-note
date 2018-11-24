# SpringBOOT高级篇笔记

## 一、缓存

### 1.简单使用

首先，在主配置类中标注@EnableCaching  //开启基于缓存的注解

```java
@MapperScan("com.atguigu.springboot.cache.mapper")
@SpringBootApplication
@EnableCaching  //开启基于缓存的注解
public class SpringbootSuper01CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootSuper01CacheApplication.class, args);
    }
}
```

然后在，使用的方法上加@Cacheable

```java
@ResponseBody
@GetMapping("/emp/{id}")
@Cacheable(cacheNames = {"emp"})
public Employee getEmployee(@PathVariable("id") Integer id){
    System.out.println("查询第"+id+"号员工");
    Employee emp = employeeMapper.getEmpById(id);
    return emp;
}
```



### 2.缓存的保存/更新/删除

@Cacheable
​	 将方法的运行结果进行缓存；以后再要相同的数据，直接从缓存中获取，不用调用方法。
@CachePut
​	既调用方法，又更新缓存数据；同步更新缓存。
@CacheEvict
​	默认：先从数据库中删除数据，再清除缓存。

```java

 *   几个属性：
 *      cacheNames/value：指定缓存组件的名字;将方法的返回结果放在哪个缓存中，是数组的方式，可以指定多个缓存；
 *
 *      key：缓存数据使用的key；可以用它来指定。默认是使用方法参数的值  1-方法的返回值
 *              编写SpEL； #i d;参数id的值   #a0  #p0  #root.args[0]
 *              getEmp[2]
 *
 *      keyGenerator：key的生成器；可以自己指定key的生成器的组件id
 *              key/keyGenerator：二选一使用;
 *
 *
 *      cacheManager：指定缓存管理器；或者cacheResolver指定获取解析器
 *
 *      condition：指定符合条件的情况下才缓存；
 *              ,condition = "#id>0"
 *          condition = "#a0>1"：第一个参数的值》1的时候才进行缓存
 *
 *      unless:否定缓存；当unless指定的条件为true，方法的返回值就不会被缓存；可以获取到结果进行判断
 *              unless = "#result == null"
 *              unless = "#a0==2":如果第一个参数的值是2，结果不缓存；
 *      sync：是否使用异步模式
 * @param id
 * @return
 *
 */
```
### 3.以@Cacheable为例梳理原理

1.原理：

```java
 * /**
     *
     * CacheManager管理多个Cache组件的，对缓存的真正CRUD操作在Cache组件中，每一个缓存组件有自己唯一一个名字；
     *
 * 原理：
 *   1、自动配置类；CacheAutoConfiguration
 *   2、缓存的配置类
 *   org.springframework.boot.autoconfigure.cache.GenericCacheConfiguration
 *   org.springframework.boot.autoconfigure.cache.JCacheCacheConfiguration
 *   org.springframework.boot.autoconfigure.cache.EhCacheCacheConfiguration
 *   org.springframework.boot.autoconfigure.cache.HazelcastCacheConfiguration
 *   org.springframework.boot.autoconfigure.cache.InfinispanCacheConfiguration
 *   org.springframework.boot.autoconfigure.cache.CouchbaseCacheConfiguration
 *   org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
 *   org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration
 *   org.springframework.boot.autoconfigure.cache.GuavaCacheConfiguration
 *   org.springframework.boot.autoconfigure.cache.SimpleCacheConfiguration【默认】
 *   org.springframework.boot.autoconfigure.cache.NoOpCacheConfiguration
 *   3、哪个配置类默认生效：SimpleCacheConfiguration；
 *
 *   4、给容器中注册了一个CacheManager：ConcurrentMapCacheManager
 *   5、可以获取和创建ConcurrentMapCache类型的缓存组件；他的作用将数据保存在ConcurrentMap中；
 *
 * 
 *
 	总之：
 *   @Cacheable标注的方法执行之前先来检查缓存中有没有这个数据，默认按照参数的值作为key去查询缓存，
 *   如果没有就运行方法并将结果放入缓存；以后再来调用就可以直接使用缓存中的数据；
 *
 *   核心：
 *      1）、使用CacheManager【ConcurrentMapCacheManager】按照名字得到Cache【ConcurrentMapCache】组件
 *      2）、key使用keyGenerator生成的，默认是SimpleKeyGenerator
 *
 *
 *   
```
2.运行流程：

~~~java
  运行流程：
 *   @Cacheable：
 *   1、方法运行之前，先去查询Cache（缓存组件），按照cacheNames指定的名字获取；
 *      （CacheManager先获取相应的缓存），第一次获取缓存如果没有Cache组件会自动创建。
 *   2、去Cache中查找缓存的内容，使用一个key，默认就是方法的参数；
 *      key是按照某种策略生成的；默认是使用keyGenerator生成的，默认使用SimpleKeyGenerator生成key；
 *          SimpleKeyGenerator生成key的默认策略；
 *                  如果没有参数；key=new SimpleKey()；
 *                  如果有一个参数：key=参数的值
 *                  如果有多个参数：key=new SimpleKey(params)；
 *   3、没有查到缓存就调用目标方法；
 *   4、将目标方法返回的结果，放进缓存中
~~~



3.总之：

~~~java
总之：
 *   @Cacheable标注的方法执行之前先来检查缓存中有没有这个数据，默认按照参数的值作为key去查询缓存，
 *   如果没有就运行方法并将结果放入缓存；以后再来调用就可以直接使用缓存中的数据；
 *
 *   核心：
 *      1）、使用CacheManager【ConcurrentMapCacheManager】按照名字得到Cache【ConcurrentMapCache】组件
 *      2）、key使用keyGenerator生成的，默认是SimpleKeyGenerator
~~~

### 4.@Cacheable：缓存保存

执行之前先来检查缓存中有没有这个数据，默认按照参数的值作为key去查询缓存，

如果没有就运行方法并将结果放入缓存；以后再来调用就可以直接使用缓存中的数据；

### 5.@CachePut：缓存更新

@CachePut：**既调用方法，又更新缓存数据；同步更新缓存**

简单来说：修改了数据库的某个数据，同时更新缓存；
运行时机：

​	1、先调用目标方法
​	2、将目标方法的结果缓存起来

需要注意的是：同一缓存（cacheNames相同）中的值以key来区分，如果更新的对象缓存在已存在，更新的可以需指定与存在的相同，否则就创建了另外一个key.

### 6.@CacheEvict：缓存清除

**默认：先从数据库中删除数据，再清除缓存**

key：指定要清除的数据
allEntries = true：指定清除这个缓存中所有的数据

beforeInvocation = false：缓存的清除是否在方法之前执行
​      **默认**代表缓存清除操作是在方法执行之后执行;如果出现异常缓存就不会清除

beforeInvocation = true：
​      代表清除缓存操作是在方法运行之前执行，无论方法是否出现异常，缓存都清除



### 7.Cacheing:复杂的缓存配置

~~~java
@CacheConfig(cacheNames="emp"/*,cacheManager = "employeeCacheManager"*/) //抽取缓存的公共配置
@Service
public class EmployeeService {
~~~

8.CacheConfig:抽取功能的缓存配置

~~~
@CacheConfig(cacheNames="emp"/*,cacheManager = "employeeCacheManager"*/) //抽取缓存的公共配置
@Service
public class EmployeeService {
~~~

### 9.整合redis作为缓存

开发中使用缓存中间件；redis、memcached、ehcache；

Redis 是一个开源（BSD许可）的，内存中的数据结构存储系统，它可以用作数据库、缓存和消息中间件。
 * 1、安装redis：使用docker；

 * 2、引入redis的starter

 * 3、配置redis

 * 4、测试缓存

   原理：CacheManager===Cache 缓存组件来实际给缓存中存取数据

   1）、引入redis的starter，容器中保存的是 RedisCacheManager；

   2）、RedisCacheManager 帮我们创建 RedisCache 来作为缓存组件；RedisCache通过操作redis缓存数据的

   3）、默认保存数据 k-v 都是Object；利用序列化保存；如何保存为json

   ​	1、引入了redis的starter，cacheManager变为 RedisCacheManager；

   ​	2、默认创建的 RedisCacheManager 操作redis的时候使用的是 RedisTemplate<Object, Object>

   ​	3、RedisTemplate<Object, Object> 是 默认使用jdk的序列化机制

   4）、自定义CacheManager；

## 二、消息中间件（整合RabbitMQ）

1.基本概念

2.RabbitMQ的使用

基本使用：

~~~java
/**
     * 发送消息
     */
    @Test
    public void sendMQ(){
        Map<String,Object> map1=new HashMap<String, Object>();
        map1.put("msg","哇晒，牛逼呀RabbitMQ");
        map1.put("data",Arrays.asList("周天浪2",1234));

        rabbitTemplate.convertAndSend("exchange.direct","atguigu.news",map1);
    }

    /**
     * 代码获取消息
     */
    @Test
    public void receive(){
        Object receiveMQ = rabbitTemplate.receiveAndConvert("atguigu.news");
        System.out.println(receiveMQ);
    }
	
~~~



自动配置：
 * 1、RabbitAutoConfiguration

 * 2、有自动配置了连接工厂ConnectionFactory；

 * 3、RabbitProperties 封装了 RabbitMQ的配置

 * 4、 RabbitTemplate ：给RabbitMQ发送和接受消息；

 * 5、 AmqpAdmin ： RabbitMQ系统管理功能组件;

   ​	AmqpAdmin：创建和删除 Queue，Exchange，Binding

 * 6、@EnableRabbit +  @RabbitListener **监听消息队列**的内容

   ~~~java
   /*
   *开启消息队列监听功能
   */
   @EnableRabbit
   @SpringBootApplication
   public class SpringBoot01RabbitmqApplication {
   
       public static void main(String[] args) {
           SpringApplication.run(SpringBoot01RabbitmqApplication.class, args);
       }
   }
   /*	
   *监听消息队列，当监听的队列中有消息进入便会触发此方法
   */
   @RabbitListener(queues = "atguigu.news")
       public void autorecieveMQ(Map<String,Object> map){
           System.out.println(map);
       }
   ~~~

## 三、检索(elasticSearch)

### 1.基本概念

**定义**

Elasticsearch 是一个分布式、可扩展、实时的搜索与数据分析引擎。 它能从项目一开始就赋予你的数据以搜索、分析和探索的能力，这是通常没有预料到的。 它存在还因为原始数据如果只是躺在磁盘里面根本就毫无用处。



Elasticsearch是一个分布式搜索服务，提供Restful API，底层基于Lucene，采用多shard（分片）的方式保证数据安全，并且提供自动resharding的功能，github等大型的站点也是采用了ElasticSearch作为其搜索服务。

**概念**

以员工文档的形式存储为例：一个文档代表一个员工数据。存储数据到ElasticSearch 的行为叫做索引，但在索引一个文档之前，需要确定将文档存储在哪里。
•一个ElasticSearch 集群可以包含多个索引，相应的每个索引可以包含多个类型。这些不同的类型存储着多个文档，每个文档又有多个属性。
•类似关系：
​	–索引-数据库
​	–类型-表
​	–文档-表中的记录
​	–属性-列

### 2.安装在linux

运行时注意：

1.由于elasticSearch是java写的，运行时默认占有2G的堆内存空间，此处由于可能出现能内存不足的情况，需设置运行内存大小，使用 '-e‘ 命令

2.端口配置

​	9200：9200    虚拟机：elasticSearch

​	9300：9300	当分布式时，elasticSearch个节点之间使用9300端口

3.示例

docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9200:9200 -p 9300:9300  --name ES01  5acf0e
8da90b 

### 3、SpringBoot操作ES

springBoot默认支持两种技术和ES交互；

1、Jest(默认不生效）

​	需导入Jest的工具包（JestClient.class）

2、SpringData  ElasticSearch

​	1)、Client节点信息  ——ClusterNodes；ClusterName

​	2)、ElasticsearchTemplate 操作ES

​	3)、编写一个ElasticsearchRepository的子接口来操作ES

​	4）版本适配说明：https://github.com/spring-projects/spring-data-elasticsearch

​	如果版本不适配：ES2.4.6

​		1）、升级SpringBoot版本

​		2）、安装对应版本的ES

### 4、SpringBoot整合Jest操作ES

1.导入Jest

~~~xml
<!-- https://mvnrepository.com/artifact/io.searchbox/jest -->
        <dependency>
            <groupId>io.searchbox</groupId>
            <artifactId>jest</artifactId>
            <version>5.3.3</version>
        </dependency>
~~~

2.配置uris

~~~properties
#application.properties
#Jest操作ES配置
spring.elasticsearch.jest.uris=http://10.103.41.43:9200
~~~



3.操作使用

~~~java
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBoot02ElasticsearchApplicationTests {
	//1.引入JestClient
    @Autowired
    JestClient jestClient;
    @Test
    public void contextLoads() {
    }
	/**
	* 保存对象测试
	*/
    @Test
    public void jestTest() {
       
        Article article=new Article(1,"ztl","weahter","今天天气晴，阳光明媚，是出行");
		//创建索引
        Index index = new Index.Builder(article).index("ucar").type("news").build();
        try {
            //
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
	
    /**
	* 搜索对象测试
	*/
    @Test
    public void jestTest2() {
        String query="{\n" +
                "    \"query\" : {\n" +
                "        \"match\" : {\n" +
                "            \"content\" : \"天气\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        //创建搜索对象
        Search search = new Search.Builder(query).addIndex("ucar").addType("news").build();
        try {
            //执行
            SearchResult searchResult = jestClient.execute(search);
            System.out.println(searchResult.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
~~~



### 5、SpringBoot整合JestSpringData操作ES

编写一个ElasticsearchRepository的子接口来操作ES

1.如果版本不适配：ES2.4.6

​		1）、升级SpringBoot版本

​		2）、安装对应版本的ES

版本适配说明：https://github.com/spring-projects/spring-data-elasticsearch

2.配置节点和节点名称

~~~properties
#SpringData来操作ES配置
spring.data.elasticsearch.cluster-name=elasticsearch
spring.data.elasticsearch.cluster-nodes=10.103.41.43:9300
~~~

3.使用

https://github.com/spring-projects/spring-data-elasticsearch

## 四、任务

### 1.异步任务

1、使用场景

在Java应用中，绝大多数情况下都是通过同步的方式来实现交互处理的；但是在处理与第三方系统交互的时候，容易造成响应迟缓的情况，之前大部分都是使用**多线程**来完成此类任务，其实，在Spring 3.x之后，就已经内置了@Async来完美解决这个问题。
两个注解：
@EnableAysnc、@Aysnc

2、示例及效果

~~~java
//对方法开启异步，此方法会开启一个线程
    @Async
    public void hello(){
        try {
            Thread.sleep(3000);
            System.out.println("正在处理....");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	
	//调用
    public String  helloTest(){
        asyncService.hello();
        return "Success";
    }
~~~

分析：如果不加异步注解，则需先执行hello(),再执行return语句，为了效果明显让hello睡了3秒，

​	   如果加了，会同时执行hello()和return语句。达到异步效果。

### 2、定时任务

#### 1.基本概念

**目的**

项目开发中经常需要执行一些定时任务，比如需要在每天凌晨时候，分析一次前一天的日志信息。

**Spring开发**

Spring为我们提供了异步执行任务调度的方式，提供TaskExecutor、TaskScheduler接口。
两个注解：@EnableScheduling、@Scheduled

**cron表达式**

| 字段 | 允许值                  | 允许的特殊字符    |
| ---- | ----------------------- | ----------------- |
| 秒   | 0-59                    | , -   * /         |
| 分   | 0-59                    | , -   * /         |
| 小时 | 0-23                    | , -   * /         |
| 日期 | 1-31                    | , -   * ? / L W C |
| 月份 | 1-12                    | , -   * /         |
| 星期 | 0-7或SUN-SAT   0,7是SUN | , -   * ? / L C # |

| 特殊字符 | 代表含义                   |
| -------- | -------------------------- |
| ,        | 枚举                       |
| -        | 区间                       |
| *        | 任意                       |
| /        | 步长                       |
| ?        | 日/星期冲突匹配            |
| L        | 最后                       |
| W        | 工作日                     |
| C        | 和calendar联系后计算过的值 |
| #        | 星期，4#2，第2个星期四     |

#### 2、示例

~~~java
/**
     * second(秒), minute（分）, hour（时）, day of month（日）, month（月）, day of week（周几）.
     * 0 * * * * MON-FRI
     *  【0 0/5 14,18 * * ?】 每天14点整，和18点整，每隔5分钟执行一次
     *  【0 15 10 ? * 1-6】 每个月的周一至周六10:15分执行一次
     *  【0 0 2 ? * 6L】每个月的最后一个周六凌晨2点执行一次
     *  【0 0 2 LW * ?】每个月的最后一个工作日凌晨2点执行一次
     *  【0 0 2-4 ? * 1#1】每个月的第一个周一凌晨2点到4点期间，每个整点都执行一次；
     */
   // @Scheduled(cron = "0 * * * * MON-SAT")
    //@Scheduled(cron = "0,1,2,3,4 * * * * MON-SAT")
   // @Scheduled(cron = "0-4 * * * * MON-SAT")
    @Scheduled(cron = "0/4 * * * * MON-SAT")  //每4秒执行一次
    public void hello(){
        System.out.println("hello ... ");
    }
~~~



### 3、邮件任务

0.邮件发送流程

zhansan@qq.com  ——>> qq邮箱服务器 ——>>163邮箱服务器 ——>>lisi@163.com

1.导包

邮件发送需要引入spring-boot-starter-mail

~~~xml
<!--引入邮件依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
~~~

2.配置

SpringBoot 自动配置MailSenderAutoConfiguration

只需在application.properties中配置即可

~~~properties
#发送邮件使用的服务器
spring.mail.host=smtp.163.com
spring.mail.username=18780024842@163.com
#此密码不是邮箱密码，而是授权码
#授权码是用于登录第三方邮件客户端的专用密码。
spring.mail.password= *****  

#开启ssl安全连接163邮箱
spring.mail.properties.mail.smtp.ssl.enable=true
~~~

3.编写测试

​	1)、自动装配JavaMailSender

​	2）、编写消息邮件Message:  SimpleMailMessage;MimeMessage

​	3）、发送邮件JavaMailSender.send(message)

~~~java
public class SpringBoot03TaskApplicationTests {
	
    //引入JavaMailSenderImpl来发送邮件
    @Autowired
    JavaMailSenderImpl javaMailSender;

    /**
     * 简单邮件发送
     */
    @Test
    public void contextLoads() {

        //创建一个简单的消息邮件
        SimpleMailMessage message=new  SimpleMailMessage();
        message.setSubject("节假日通知2");
        message.setText("由于公司特殊原因，本周六至下周一放假。祝您假期愉快！");
        message.setTo("1490784129@qq.com");
        message.setFrom("18780024842@163.com");

        //发送
        javaMailSender.send(message);
        System.out.println("发送成功");
    }

    /**
     * 复杂邮件发送——带附件
     */
    @Test
    public void contextLoads3() {
        //创建一个复杂的消息邮件
        MimeMessage message=javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject("节假日通知2");
            helper.setText("由于公司特殊原因，<b style='color:red'>本周六至下周一</b>放假。祝您假期愉快！",true);
            helper.setTo("1490784129@qq.com");
            //必须填写
            helper.setFrom("18780024842@163.com");

            //上传文件
            helper.addAttachment("1.jpg",new File("C:\\Users\\ucarinc\\Pictures\\Saved Pictures\\1.jpg"));
            helper.addAttachment("1.jpg",new File("C:\\Users\\ucarinc\\Pictures\\Saved Pictures\\2.jpg"));

            //发送
            javaMailSender.send(message);
            System.out.println("发送成功");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
~~~

需注意两点：

​	1）、配置的密码为授权码

​	1）、setFrom()要求必须填写，并且要求为邮箱地址，与配置邮箱的相同

​	2）、setText若有样式，则开启html格式，如：setText("由于公司特殊原因，<b style='color:red'>本周六至下周一</b>放假。祝您假期愉快！",true);

## 五、安全

### 1、安全



### 2、Web&安全



## 六、分布式

### 1、分布式应用



### 2、Zookeeper和Dubbo



### 3、Spring Boot和Spring Cloud



## 七、开发热部署



## 八、监控管理

### 1、监控管理



### 2、定制端点信息

