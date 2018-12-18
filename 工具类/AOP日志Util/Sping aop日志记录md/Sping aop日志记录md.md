# Sping aop日志记录md

###  一、实现方式一：自定义注解

参考：(https://www.cnblogs.com/leifei/p/8194644.html)

所需jar包 ：spring4.3相关联以及aspectjweaver-1.8.5.jar

此方法需自定义注解，通过对方法的注解绑定切入点（即：需要记录的方法）

优点：通过注解可以绑定指定的方法

缺点：需一一去注解，比较麻烦

#### 1、注解OperLog

~~~java
package cn.itcast.crm.annotation;

import java.lang.annotation.*;

/**
 * @Description:
 * @Auther:
 * @Date: 2018/12/11 10:11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OperLog {

    //操作类型  内部操作/外部操作
    String operType() default "";
}

~~~

#### 2、切面OperLogAspect

切面即对切入的方法

~~~java
package cn.itcast.crm.logAspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Description:
 * @Auther:
 * @Date: 2018/12/11 10:15
 */
//声明这是一个组件
@Component
//声明这是一个切面Bean
@Aspect
public class OperLogAspect {
    /**
     * 环绕触发，可根据业务场景选择@before @After
     * 触发条件：cn.itcast.crm包下面所有的类且注解为OperLog的
     * @param pjp
     * @param
     * @return
     * @throws Throwable
     */
    // 此处可使用@Around注解指定aop切入条件：
  //@Around("within(cn.itcast.crm.service..*)&&@annotation(cn.itcast.crm.annotation.OperLog) ")
    public Object doAroundMethod(ProceedingJoinPoint pjp) throws Throwable {
        long startTime=System.currentTimeMillis();//开始时间

        //获取注解方法1
        Signature sig = pjp.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = pjp.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        System.out.println("监听到调用方法为："+currentMethod);

        //获取方法名2
        //String targetMethodName = pjp.getSignature().getName();

        //获取传入参数
        Object[] params=pjp.getArgs();//获取请求参数
        System.out.println(currentMethod+"监听到传入参数为：");
        for(Object param:params){
            System.out.println(param);
        }

        //###################上面代码为方法执行前#####################
        //获取返回值
        Object result  = pjp.proceed();//执行方法，获取返回参数
        //###################下面代码为方法执行后#####################
        System.out.println(currentMethod+"返回参数："+result);


        long endTime=System.currentTimeMillis();//结束时间
        float excTime=(float)(endTime-startTime)/1000;
        System.out.println(currentMethod+"执行时间:"+excTime+"s");
        System.out.println("#######################分隔符##########################");
        return result;
    }
}

~~~

#### 3、配置

~~~xml
</beans>
	<!-- 切面 注入到Sping -->
    <bean id="optLogAspect" class="cn.itcast.crm.logAspect.OperLogAspect" />
    <!--切入点为自定义注解类-->
    <aop:config>
        <aop:aspect ref="optLogAspect">
            <!--配置切入点：被注解了OperLog的方法-->
            <aop:pointcut id="optLogPointCut" expression="@annotation(cn.itcast.crm.annotation.OperLog)" />
            <!--调用切面的具体方法-->
            <aop:around method="doAroundMethod" pointcut-ref="optLogPointCut" />
        </aop:aspect>
    </aop:config>
    <aop:aspectj-autoproxy proxy-target-class="true" />

</beans>
~~~

#### 4、使用

使用注解，绑定需要记录的方法

~~~java
/**
     delete 根据id删除用户
     */
    @Override
	//使用注解，绑定需要记录的方法
    @OperLog(operType = "删除用户")
    public void deleteCustomerById(Integer id) {
        customerMapper.deleteCustomerById(id);
    }
~~~

#### 5、问题

1、切入点如果是注解，是否对需要切入的方法必须注解呢？？

必须，如果对方法不注解，则没法指定切入点，则aop失败。

### 二、实现方式二：指定包

此方法不需自定义注解，只需要通过指定包名来绑定切入点（即：需要记录的方法）

优点：只需指定相应包即可绑定该包下所有的方法，无需单个注解

缺点：切入方式太粗糙，很可能记录到不需要记录的方法。

#### 1、切面OperLogAspec

切面即对切入的方法。

注：同方式一写法相同。无论使用哪种方式都需写切面

```java
package cn.itcast.crm.logAspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Description:
 * @Auther:
 * @Date: 2018/12/11 10:15
 */
//声明这是一个组件
@Component
//声明这是一个切面Bean
@Aspect
public class OperLogAspect {
    /**
     * 环绕触发，可根据业务场景选择@before @After
     * 触发条件：cn.itcast.crm包下面所有的类且注解为OperLog的
     * @param pjp
     * @param
     * @return
     * @throws Throwable
     */
    // 此处可使用@Around注解指定aop：
  //@Around("within(cn.itcast.crm.service..*)&&@annotation(cn.itcast.crm.annotation.OperLog) ")
    public Object doAroundMethod(ProceedingJoinPoint pjp) throws Throwable {
        long startTime=System.currentTimeMillis();//开始时间

        //获取注解方法1
        Signature sig = pjp.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = pjp.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        System.out.println("监听到调用方法为："+currentMethod);

        //获取方法名2
        //String targetMethodName = pjp.getSignature().getName();

        //获取传入参数
        Object[] params=pjp.getArgs();//获取请求参数
        System.out.println(currentMethod+"监听到传入参数为：");
        for(Object param:params){
            System.out.println(param);
        }

        //###################上面代码为方法执行前#####################
        //获取返回值
        Object result  = pjp.proceed();//执行方法，获取返回参数
        //###################下面代码为方法执行后#####################
        System.out.println(currentMethod+"返回参数："+result);


        long endTime=System.currentTimeMillis();//结束时间
        float excTime=(float)(endTime-startTime)/1000;
        System.out.println(currentMethod+"执行时间:"+excTime+"s");
        System.out.println("#######################分隔符##########################");
        return result;
    }
}

```

#### 2、配置

```xml
</beans>
	<!-- 切面 注入到Sping -->
    <bean id="optLogAspect" class="cn.itcast.crm.logAspect.OperLogAspect" />
    <!--切入点为自定义注解类-->
    <aop:config>
        <aop:aspect ref="optLogAspect">
            <!--配置切入点：被注解了OperLog的方法-->
            <aop:pointcut id="optLogPointCut" expression="@annotation(cn.itcast.crm.annotation.OperLog)" />
            <!--调用切面的具体方法-->
            <aop:around method="doAroundMethod" pointcut-ref="optLogPointCut" />
        </aop:aspect>
    </aop:config>
    <aop:aspectj-autoproxy proxy-target-class="true" />

</beans>
```

#### 3、使用

无,根据配置改变需要记录的方法

~~~xml
<aop:pointcut id="optLogPointCut" expression="@annotation(cn.itcast.crm.annotation.OperLog)" />
~~~

### 三、切面Aspect详解

#### 一、注解解析

此处使用了spring的AOP（面向切面编程）特性

**通过@Aspect注解使该类成为切面类**

 **通过@Pointcut 指定切入点 ，这里指定的切入点为MyLog注解类型，也就是被@MyLog注解修饰的方法，进入该切入点。**

- @Before 前置通知：在某连接点之前执行的通知，但这个通知不能阻止连接点之前的执行流程（除非它抛出一个异常）。
- @Around 环绕通知：可以实现方法执行前后操作，需要在方法内执行point.proceed(); 并返回结果。
- @AfterReturning 后置通知：在某连接点正常完成后执行的通知：例如，一个方法没有抛出任何异常，正常返回。
- @AfterThrowing 异常通知：在方法抛出异常退出时执行的通知。
- @After 后置通知：在某连接点正常完成后执行的通知：例如，一个方法没有抛出任何异常，正常返回

#### 二、获取方法

**以参数的形式**

如：例如 public void add(JoinPoint joinpoint){}

~~~java
//获取被调用的方法名
Signature sig = pjp.getSignature();
MethodSignature msig = null;
if (!(sig instanceof MethodSignature)) {
     throw new IllegalArgumentException("该注解只能用于方法");
}
msig = (MethodSignature) sig;
Object target = pjp.getTarget();
Method currentMethod = target.getClass().getMethod(msig.getName(), 		msig.getParameterTypes());
System.out.println("监听到调用方法为："+currentMethod);
~~~

**只包含方法（建议）**

如： add

~~~java
//获取被调用的方法名
JoinPoint joinpoint = new JoinPoint();
String targetMethodName = joinpoint.getSignature().getName();
~~~

### 四、aop日志记录字段设计

表设计：

​	基础字段：id,user_id,ip(客户端),type(平台类型)，app_id,app_name,create_time
​	核心字段：interface_name，method_name,request_params,response_params

~~~sql
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_opt_log
-- ----------------------------
DROP TABLE IF EXISTS `t_opt_log`;
CREATE TABLE `t_opt_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` varchar(20) NOT NULL DEFAULT '' COMMENT '用户 ID',
  `ip` varchar(40) NOT NULL DEFAULT '' COMMENT 'IP 地址',
  `type` tinyint(2) unsigned NOT NULL COMMENT '调用类型（1：对内调用，2：对外调用）',
  `app_id` varchar(100) NOT NULL DEFAULT '' COMMENT '项目 ID',
  `app_name` varchar(50) NOT NULL DEFAULT '' COMMENT '项目名称',
  `interface_name` varchar(100) NOT NULL DEFAULT '' COMMENT '调用接口名',
  `method_name` varchar(100) NOT NULL DEFAULT '' COMMENT '调用方法名',
  `request_params` varchar(5000) NOT NULL DEFAULT '' COMMENT '传入参数',
  `response_params` varchar(5000) NOT NULL DEFAULT '' COMMENT '传出参数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_interface` (`interface_name`) USING BTREE COMMENT '接口名索引',
  KEY `idx_method` (`method_name`) USING BTREE COMMENT '方法名索引',
  KEY `idx_creatTime` (`create_time`) USING BTREE COMMENT '日志记录生成时间索引',
  KEY `idx_userId` (`user_id`) USING BTREE COMMENT '用户Id索引'
) ENGINE=InnoDB AUTO_INCREMENT=441 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='操作日志表';
~~~



### 五、项目实战中且切面的写法

使用方式一（自定义注解）实现，配置及使用参考上面

~~~java
/**
 * Description: 操作日志切面
 *
 */
@Aspect
public class OptLogAspect {

    @Autowired
    private OptLogService optLogService;

    /**
     * 环绕通知
     *
     * @param joinPoint 连接点
     * @return
     * @throws Throwable
     */

    public void around(ProceedingJoinPoint joinPoint) throws Throwable{
        // 执行目标方法
        Object result = joinPoint.proceed();
        saveOptLog(joinPoint, result);
    }
	
    
    /**
     * 入库
     *
     * @param joinPoint 连接点
     * @return
     * @throws Throwable
     */
    private void saveOptLog(ProceedingJoinPoint joinPoint, Object result) {
        // 获取 方法签名对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        OptLogDO optLogDO = new OptLogDO();
        OptLogAnnotation optLogAnnotation = method.getAnnotation(OptLogAnnotation.class);

        if (optLogAnnotation != null) {
            // 获取 方法调用的类型
            optLogDO.setType(optLogAnnotation.type().getNumber());
        }

        // 暂时设空
        optLogDO.setUserId("");
        optLogDO.setIp("");
        optLogDO.setAppId("");
        optLogDO.setAppName("");

        // 获取 目标类名
        String className = joinPoint.getTarget().getClass().getName();
        optLogDO.setInterfaceName(className);

        // 获取 方法名
        String methodName = signature.getName();
        optLogDO.setMethodName(methodName);

        // 获取 入参
        // 参数名
        String[] parameterNames = signature.getParameterNames();
        // 参数值   转为json格式 
        Object[] parameterValues = joinPoint.getArgs();
        if (parameterNames.length != 0 && parameterValues.length != 0) {
            Map<String, Object> requestParams = new HashMap<>();
            for (int i = 0; i < parameterNames.length; i++) {
                requestParams.put(parameterNames[i], parameterValues[i]);
            }
            String requestJson = JSON.toJSONString(requestParams);
            optLogDO.setRequestParams(requestJson.length() <= 5000 ? requestJson : requestJson.substring(0, 5000));
        } else {
            optLogDO.setRequestParams("");
        }

        // 放入 responseParams
        optLogDO.setResponseParams(JSON.toJSONString(result).substring(0, 5000));

        // 操作数据插入数据库
        optLogService.insertOptLogAsyn(optLogDO);
    }
}

~~~

