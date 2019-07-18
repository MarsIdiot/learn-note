# HTTP相关问题记录

## 状态码

![.\pictures\statusCode.png](.\pictures\statusCode.png)

### 502与504

​    通俗的来说，nginx作为一个代理服务器，将请求转发到其他服务器或者php-cgi来处理，当nginx收到了无法理解的响应时，就返回502。当nginx超过自己配置的超时时间还没有收到请求时，就返回504错误。

​    详情参考：<https://juejin.im/entry/589148f92f301e00690e863d>