# 检索(elasticSearch）

## 一、入门介绍

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



![QQ截图20181203155418](.\pictures\QQ截图20181203155418.png)

### 2、安装

在linux

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

```xml
<!-- https://mvnrepository.com/artifact/io.searchbox/jest -->
        <dependency>
            <groupId>io.searchbox</groupId>
            <artifactId>jest</artifactId>
            <version>5.3.3</version>
        </dependency>
```

2.配置uris

```properties
#application.properties
#Jest操作ES配置
spring.elasticsearch.jest.uris=http://xxxx:9200
```



3.操作使用

```java
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
        Index index = new Index.Builder(article).index("uss").type("news").build();
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
        Search search = new Search.Builder(query).addIndex("usss").addType("news").build();
        try {
            //执行
            SearchResult searchResult = jestClient.execute(search);
            System.out.println(searchResult.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
```



### 5、SpringBoot整合JestSpringData操作ES

编写一个ElasticsearchRepository的子接口来操作ES

1.如果版本不适配：ES2.4.6

​		1）、升级SpringBoot版本

​		2）、安装对应版本的ES

版本适配说明：https://github.com/spring-projects/spring-data-elasticsearch

2.配置节点和节点名称

```properties
#SpringData来操作ES配置
spring.data.elasticsearch.cluster-name=elasticsearch
spring.data.elasticsearch.cluster-nodes=127.0.0.1:9300
```

3.使用

https://github.com/spring-projects/spring-data-elasticsearch