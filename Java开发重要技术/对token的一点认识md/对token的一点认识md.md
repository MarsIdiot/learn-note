# 对token的一点认识md

## cookie与session简介

cookie:数据存在客户端浏览器；不安全，容易被别人解析本地cookie进行cookie欺骗。适合保存一些不太重要的信息。

session:临时放在服务器上，安全性比cookie高；但访问过多暂用服务器性能；利用负载均衡时，下一个访问到另一台服务器时session会失效，需要重新验证，即重新输入账号密码。

## token简介

参考一：[token理解、学习](https://www.xgllseo.com/?p=6393)

参考二：[JSON Web Token 入门教程](http://www.ruanyifeng.com/blog/2018/07/json_web_token-tutorial.html)

### **基本介绍**

使用最多的场景就是用户输入账号密码后，一段时间内可以避免重复再次输入。

cookie与session都没有token安全，因为cookie和session都需要发出密码，即使加了密也总感觉不安全，使用token验证不用直接去操作用户的账号和密码。

作为身份认证，token安全性比session好，因为每个请求都有签名且能防窃听和攻击

```java
//用token作为身份认证最常见的工作流程：(密码模式)
1. 登录时候,客户端通过用户名与密码请求登录。(用正确的帐号密码来跟服务器换一个token可以理解为令牌)
2. 服务端收到客户端发来的帐号密码，并去验证用户名与密码
3. 验证通过,服务端会签发一个Token,再把这个Token发给客户端.
4. 客户端收到Token,存储到本地,如Cookie,SessionStorage,LocalStorage(如果涉及到跨域建议用cookie来暂时存储token).我们是存在SessionStorage
5. 客户端每次像服务器请求API接口时候,都要带上Token,把token放入到Request Headers服务器那里就可以获取到。(带上的token其实就是从存储在本地的Cookie,SessionStorage,LocalStorage来获取到token)
6. 服务端收到请求,验证Token,如果通过就返回数据,否则提示报错信息.
```

### **特性**

**无状态**

可以在多个服务器间共享  token的属性如过期时间存储token自身，对分布式服务特别有用。

补充：有状态—token的属性(如过期时间)存储在服务器上

 总之，token与服务器、会话任何没有关系，自身携带相关参数：如过期时间、加密方式等(不携带账号信息)，这就造成了它并不依赖于某台服务器上的session，从而实现跨域请求。

**可扩展**

单机当然没有问题，如果是服务器集群，或者是跨域的服务导向架构，就要求 session 数据共享，每台服务器都能够读取 session。

**安全性高**

不携带任何重要用户信息

## JWT

JSON Web Token（缩写 JWT）是目前最流行的跨域认证解决方案。

对比传统认证方案，服务器索性不保存 session 数据了，所有数据都保存在客户端，每次请求都发回服务器。JWT 就是这种方案的一个代表。

### **JWT适用于情景**

1. 需要认证用户。例如：需要账户和密码

2. 可能会存在跨域认证的问题。

如果遇上以上两种问题，使用JWT是最佳的解决方案。

### **JWT 的数据结构**

```java
eyxxxxxxx.eyxxxxxx.xxxxxxxx
```

它是一个很长的字符串，中间用点（.）分隔成三个部分。

这三部分，分别是：Header（头部）.Payload（负载）.Signature（签名）

### **Header介绍**

Header 部分是一个 JSON 对象，通常是下面的样子。

```
{
 "alg": "HS256",
 "typ": "JWT"
}
```

上面代码中，alg属性表示签名的算法（algorithm），默认是 HMAC SHA256（写成 HS256）；typ属性表示这个令牌（token）的类型（type），JWT 令牌统一写为JWT。

最后，将上面的 JSON 对象使用 Base64URL 算法（详见后文）转成字符串。

### **Payload介绍**

Payload 部分也是一个 JSON 对象，参数都是可选

```
{
 iss (issuer)：签发人
 exp (expiration time)：过期时间
 sub (subject)：主题
 aud (audience)：受众
 nbf (Not Before)：生效时间
 iat (Issued At)：签发时间
 jti (JWT ID)：编号
}

例子：
{
 "sub": "1234567890",
 "name": "John Doe",
 "admin": true
}
```

注意，JWT 默认是不加密的，任何人都可以读到，所以不要把秘密信息放在这个部分。

这个 JSON 对象也要使用 Base64URL 算法转成字符串。

### **Signature介绍**

Signature 作用是对前两部分的签名，防止数据篡改，但前提需要一个密钥（secret），这个密钥只有服务器才知道，不能泄露给用户。
		然后，使用 Header 里面指定的签名算法（默认是 HMAC SHA256），按照下面的公式产生签名。

就这样JWT的结果就是由以下公式得出：

```json
base64UrlEncodeRe = base64UrlEncode(header) + "." + base64UrlEncode(payload)
signature = HMACSHA256(base64UrlEncodeRe, secret) 
token = base64UrlEncodeRe + "." +  signature
```

### **Base64URL**

前面提到，Header 和 Payload 串型化的算法是 Base64URL。这个算法跟 Base64 算法基本类似，但有一些小的不同。

JWT 作为一个令牌（token），有些场合可能会放到 URL（比如 api.example.com/?token=xxx）。Base64 有三个字符+、/和=，在 URL 里
面有特殊含义，所以要被替换掉：=被省略、+替换成-，/替换成_ 。这就是 Base64URL 算法。

### **如何使用JWT**

既要有认证功能又要有跨域功能，一种方法事将token发放请求头heads的认证字段Authorization里，Authorization: Bearer eyxxxxxxx.eyxxxxxx.xxxxxxxx。另一种做法是，在跨越是放在post请求体中。

### **JWT 优缺点**

- 默认不加密的，最好在生成原始token后使用密钥加密一次
- JWT不仅可以做验证服务，也可以交换信息，减少查询数据库的次数。
- 最大的缺点之一就是，服务器不保存session状态，因此在使用中不能废除某个token,或修改token权限，也就是说，一旦 JWT 签发了，在到期之前就会始终有效，除非服务器部署额外的逻辑。
- 自身携带有验证信息，一定泄露，任何人都可以获取该令牌的所有权限。所以，为了减少盗用，限制token有限时间。对于一些比较重要的权限，使用时需要用户再次进行验证。
- 为了减少盗用，JWT 不应该使用 HTTP 协议明码传输，要使用 HTTPS 协议传输。

### **重要补充**

1、如何解决多点重复登录问题

这有必要在服务器端维护是否签发token的清单。具体说来就是维护一个token签名记录表，表中包含token_id、user_id对应。如果已签发，则将以前签发的置为失效并添加新签发记录。

而维护签发清单的开销和利用session的开销需要根据适当情况进行选择，不一定JWT就好。如果存在跨域，用token更好，因为利用session维护跨域比较麻烦。如果需要集群共享session的问题，可以通过分布式redis来解决。