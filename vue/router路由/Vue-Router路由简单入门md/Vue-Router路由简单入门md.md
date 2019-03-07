# Vue -  Router路由简单入门

## 一、使用vue-router

要做什么：

​	将组件 (components) 映射到路由 (routes)，然后告诉 Vue Router 在哪里渲染它们

1.创建路由器  定义路由组件：router/index.js

~~~js
import  VueRouter from 'vue-router'
//使用路由器
Vue.use(VueRouter)
//导入路由对应得组件
import  About from '../views/About.vue'
		
export default  new VueRouter({
    routes: [
        { // 一般路由
            path: '/about',
            component: About
        },
        { // 自动跳转路由
            path: '/', 
            redirect: '/about'
        }
    ]
})
~~~

说明：

~~~js
VueRouter()  用于创建路由器的构建函数
new VueRouter({
    //多个配置项
})
~~~

2.注册路由器: main.js

~~~js
//引入index.js
import router from './router'
new Vue({//配置对象的属性名都是一些确定的名称，不能随便修改
	router // 注册路由器
})
~~~

3.使用路由组件标签

~~~vue

	<router-link to="/xxx">Go to XXX</router-link>
   相当于创建 a 标签来定义导航链接， 当 <router-link> 对应的路由匹配成功，将自动设置 class 属性值 .router-link-active
        <!--            router-link to属性就是指向某个具体的链接，链接的内容会被渲染到router-view标签中            router-link会被渲染成a标签，例如第一个会变成<a href="#/first">第一个页面</a>,前面加了个#        -->
    	<router-link  to="/first">第1个页面</router-link>        
        <router-link  to="/second">第2个页面</router-link>       
        <router-link  to="/third">第3个页面</router-link>    
    </div>    
    <router-view></router-view></div>
2、<router-view> ： 用来显示当前路由组件界面
	<router-view></router-view>
~~~

## 二、嵌套路由

```js
//使用children
export default  new VueRouter({
    routes: [
        { // 一般路由
            path: '/about',
            component: About
        },
        { //子路由
           path: '/home',
           component: home,
           children: [
          	 {
           		 path: 'news',//当 /home/news 匹配成功
           		 component: News
           	},
           	{
          		path: 'message',//当 /home/message 匹配成功
            	component: Message
           	}
          ]
        }
    ]
})
```

## 三、路由组件传参

方式1：<router-view>属性携带数据

~~~vue
 <router-view :msg='abc'>
~~~

方式2：路由路径携带参数（param/query）（官方文档称为：动态路由）
​	1)  配置路由

~~~js
//使用占位符:id
{
    path:'/message/messageDetail/:id',
    component:MessageDetail
}
~~~

​	2)  路由路径

~~~vue
<router-link :to="`/message/messageDetail/${message.id}`">{{message.title}}</router-link>
~~~

​	3)  路由组件中读取请求参数

~~~js
{{$route.params.id}}
~~~

另外：

路由中设置多段“路径参数”，对应的值都会设置到 `$route.params` 中。例如：

| 模式                          | 匹配路径            | $route.params                          |
| ----------------------------- | ------------------- | -------------------------------------- |
| /user/:username               | /user/evan          | `{ username: 'evan' }`                 |
| /user/:username/post/:post_id | /user/evan/post/123 | `{ username: 'evan', post_id: '123' }` |

需要注意的是：

​	当传不同参数时，**原来的组件实例会被复用**。因为两个路由都渲染同个组件，比起销毁再创建，复用则显得更加高效。**不过，这也意味着组件的生命周期钩子不会再被调用**。

复用组件时，想对路由参数的变化作出响应的话，你可以简单地 watch (监测变化) `$route` 对象

~~~js
 watch: {
    '$route' (to, from) {
      // 对路由变化作出响应...
    }
  }
~~~

补充：【vue-router params和query的区别】https://www.jianshu.com/p/4bc761100dc0

区分：

​	this.$router：router 实例，控制路由导航， 通过router.currentRoute可获得当前路由对象

​	this.$route：当前激活的[路由信息对象]，包含了当前 URL 解析得到的信息，还有 URL 匹配到的路由记录 (包含当前路由的路由配置信息)

## 四、缓存路由组件

为什么需要：

​	默认情况下，被切换的路由组件对象会死亡，再次切换回来时是重新创建的

​	为了提高用户体验

~~~vue
<keep-alive>
    <router-view></router-view>
</keep-alive>
~~~

示例说明：

![12](D:\ztl\笔记\vue\router路由\Vue-Router路由简单入门md\pictures\QQ截图20190118175321.png)

## 五、路由的编程式导航

**常用导航**

this.$router.push(path)：相当于点击路由链接(可以返回到当前路由界面)，向 history 添加新记录

this.$router.replace(path): 用新路由替换当前路由(不可以返回到当前路由界面)，不会向 history 添加新记录，替换掉当前的 history 记录

this.$router.back(): 请求(返回)上一个记录路由（重新加载，会被刷新）

this.$router.go(-1): 请求(返回)上一个记录路由

this.$router.go(1): 请求下一个记录路由

**声明式与编程式**

当你点击 `<router-link>` 时，这个方法会在内部调用，所以说，点击 `<router-link :to="...">` 等同于调用 `router.push(...)`。

| 声明式                    | 编程式             |
| ------------------------- | ------------------ |
| `<router-link :to="...">` | `router.push(...)` |

该方法的参数可以是一个字符串路径，或者一个描述地址的对象。例如：

```js
// 字符串
router.push('home')

// 对象
router.push({ path: 'home' })

// 命名的路由
router.push({ name: 'user', params: { userId: '123' }})

// 带查询参数，变成 /register?plan=private
router.push({ path: 'register', query: { plan: 'private' }})
```

**注意：如果提供了 path，params 会被忽略，上述例子中的 query 并不属于这种情况。取而代之的是下面例子的做法，你需要提供路由的 name 或手写完整的带有参数的 path：**

```js
const userId = '123'
router.push({ name: 'user', params: { userId }}) // -> /user/123
router.push({ path: `/user/${userId}` }) // -> /user/123
// 这里的 params 不生效
router.push({ path: '/user', params: { userId }}) // -> /user
```

同样的规则也适用于 `router-link` 组件的 `to` 属性。

## 六、命名路由（别名）

标识路由，特别是在链接一个路由，或者是执行一些跳转的时候。

```js
const router = new VueRouter({
  routes: [
    {
      path: '/user/:userId',
      name: 'user',
      component: User
    }
  ]
})
```

```js
<router-link :to="{ name: 'user', params: { userId: 123 }}"/>
```

```js
router.push({ name: 'user', params: { userId: 123 }})
```

这两种方式都会把路由导航到 `/user/123` 路径。



## 七、命名视图

有时候想同时 (同级) 展示多个视图，而不是嵌套展示，例如创建一个布局，有 `sidebar` (侧导航) 和 `main` (主内容) 两个视图。你可以在界面中拥有多个单独命名的视图，而不是只有一个单独的出口。如果 `router-view` 没有设置名字，那么默认为 `default`。

```html
<router-view class="view one"></router-view>
<router-view class="view two" name="a"></router-view>
<router-view class="view three" name="b"></router-view>
```

一个视图使用一个组件渲染，因此对于同个路由，多个视图就需要多个组件。确保正确使用 `components`配置 (**带上 s)**：

```js
const router = new VueRouter({
  routes: [
    {
      path: '/',
      components: {
        default: Foo,
        a: Bar,
        b: Baz
      }
    }
  ]
})
```

