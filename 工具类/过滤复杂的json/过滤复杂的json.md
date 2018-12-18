## 过滤复杂的json

### 一、需求

对获取的json中的数据进行简单过滤，得到需要的数据。

### 二、实例说明

#### 一、需求说明

当查看合同详情时，可支持单个或多个文档id查看文档（注：一个合同包含多个文档）

需要过滤的数据：ContractDetailDto

```json
{
    "data": {
        "contractVo": {
            "createTime": "2018-11-23 14:27:23", 
            //过滤此数组
            "documents": [
                {
                    "createTime": "2018-11-23 14:27:23", 
                    "id": "2503472861989634822", 
                    "pages": 10, 
                    "size": 353780, 
                    "title": "合同文档0", 
                    "type": "pdf"
                },
                {
                    "createTime": "2018-11-23 14:27:23", 
                    "id": "2503472861989634822", 
                    "pages": 10, 
                    "size": 353780, 
                    "title": "合同文档0", 
                    "type": "pdf"
                }
            ], 
            "expireTime": "2018-12-24 00:00:00", 
            "id": "2503472862518117137",
            "updateTime": "2018-11-23 14:27:25"
        }
    }, 
    "msg": "接口调用成功", 
    "resultCode": 0, 
    "success": true
}
```

#### 二、设计

##### **前端请求**

~~~json
param={
    "contractId":"69",
    "userId":22222,
    //注意documentIds格式为数组
    "documentIds":["250966437","xxx","25096643xx79231904433"]
}
~~~

##### **入参**

~~~java
 private String contractId;//合同id(我方)
 private List<String >  documentIds;//文档Id集合
 private String userId;//用户id
~~~



##### **出参：过滤得到的数据**

思路：根据传入的文档id集合来逐个对比获取到的文档集合id，如果相等则保留，否则移除

具体：1）得到文档id集合Map<int,String>：contractDetailDto.documents
​	    2）int是为了记录文档id在集合中的序号，通过序号方便删除不需要的文档

​		排除掉不需要的的文档：
​				方法一：
​					1）复制一份contractDetailDto，清除其documents
​					2）foreacah(){如果能匹配，则添加文档到新的dto}
​					3）将新的dto赋给旧的dto ——保证返回的数据是被过滤掉的
​				方法二：
​					1）过滤掉传入的不正确的文档id
​					2）foreacah(){如果不能匹配，则移除对应的文档}

代码实现：

~~~java

~~~



