## MySQL基础知识笔记_日常记录

### 1、varchar(n)

在设计数据库时一直纠结n取多少，多少够用、适合并且不浪费空间？

此时，有必要了解在mysql中varchar存储原则。

于是：

1、版本问题——存储最大长度限制

```teX
MySQL 数据库的varchar类型在4.1以下的版本中的最大长度限制为255，其数据范围可以是0~255或1~255（根据不同版本数据库来定）。

在 MySQL5.0以上的版本中，varchar数据类型的长度支持到了65535，也就是说可以存放65532个字节的数据，起始位和结束位占去了3个字 节，也就是说，在4.1或以下版本中需要使用固定的TEXT或BLOB格式存放的数据可以使用可变长的varchar来存放，这样就能有效的减少数据库文 件的大小。
```

2、varchar(n)定义不同

~~~tex
4.0版本以下，varchar(20)，指的是20字节，如果存放UTF8汉字时，只能存6个（每个汉字3字节） ；
5.0版本以上，varchar(20)，指的是20字符，无论存放的是数字、字母还是UTF8汉字（每个汉字3字节），都可以存放20个，最大大小是 65532字节 ；

此处，当版本一致时，如果编码不同的话也是有区别的：
UTF-8：一个汉字 = 3个字节，英文是一个字节
GBK： 一个汉字 = 2个字节，英文是一个字节
~~~

#### SQL语句执行顺序

​		Select  distinct   count(*)  from  join  on  where  group by  having   order by desc  limmit

1. From     join on

   知道哪个表

2. where 

   过滤条件 

3. group by  （**开始使用select中的别名，后面的语句中都可以使用**）

   将表处理为需要的结构(列结构)

4. coun,tavg,sum.... 

   分组统计

5. having

   对分组得数据进行过滤 配合group by

6. select 

7. distinct  唯一

8. 排序
   order by
   desc  

9. 数量
   limmit



