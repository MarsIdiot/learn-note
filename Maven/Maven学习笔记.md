## Maven

### 一、Maven简介

#### 	1.什么是maven

​		是apache下的一个开源项目，纯java开发，只用来管理java项目

#### 	 2.maven好处

​		小   因为没有jar包，没有存在maven项目里，jar包与maven项目分离

​		一键构建

​		跨平台

​		用于大型项目，提高开发效率

#### 	 3.依赖管理

​		就是对jar包的统一管理，可以节约空间

​		maven管理jar的方式：

​			坐标+索引——>>本地仓库(jar包库)——>>远程仓库（公司）——>>中心仓库（apache管理）

#### 	 4.maven可做的事

​		 编译  测试(junit)  运行  打包  部署 

### 二、Maven的安装配置

​	环境变量配置

​	Maven3.3.x   需要1.7jdk以上的版本

### 三、常用命令

​	Clean  清理编译的文件	  

​	Compile  编译了主目录main的文件

​	Test  编译并运行了test目录的代码	

​	Package 打包war

​	Install 就是把项目发布到本地仓库（compile+test+package  顺序执行）

​	Tomcat：run  一键启动

​	注意：以mvn开头  命令小写，如：mvn  tomcat:run

### 四、生命周期（了解）

​	Compile   test  package  install  deploy（发布到私服）

​	三种生命周期：

​	Clean生命周期

​		 Clean

​	Default生命周期

​		Compile   test  package  install  deploy

​	Site生命周期

 		Site（生成项目的站点文档）

### 五、依赖版本冲突解决

​	路径近者优先原则：自己添加冲突的jar包

​	第一声明优先原则

​	 排除原则

​	版本锁定原则

​    

​    

