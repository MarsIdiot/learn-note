## 项目jar部署相关流程命令

此操作适合于springboot自带tomcat编译的jar.

### 部署流程

1、如果jar已部署，则直接删除jar(删除前可先停止该服务，当然直接删除没问题)

rm -f  xxx.jar   删除jar包

2、上传jar

3、启动

java -jar  xxx.jar  &     后台启动

nohup java -jar dkey-service-os-0.0.1-SNAPSHOT.jar &      

​		---------nohup 后台启动(带有nohup.out，实时日志)(开发测试阶段推荐)

4、查看是否启动

   1） 查看正在运行与Java相关的进程

ps -ef | grep java  

​    2）杀死相关的进程 

kill -9 xxxx  (xxxx为上条命令列出的后台进程的pid， -9表示无条件终止）

### linux相关命令补充

#### 删除

rm -f  xxx  删除文件

rm -rf  xxx    删除文件夹及其包含的所有文件

~~~xml
-r 就是向下递归，不管有多少级目录，一并删除
-f 就是直接强行删除，不作任何提示的意思
~~~

#### 查看

netstat -anp | grep 80       查看80端口

kill sid                                关闭sid的端口

cat start.sh                        查看start.sh 文件的内容