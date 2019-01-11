# java基础-实战中遇到问题记录

### 1、内部类访问局部变量的时候，为什么局部变量必须加上final?

参考：https://www.cnblogs.com/xh0102/p/5729381.html

#### 1、存在矛盾

这里的局部变量就是在类方法中的变量，能访问方法中变量的类当然也是局部内部类了。

我们都知道，局部变量在所处的函数执行完之后就释放了，但是内部类对象如果还有引用指向的话它是还存在的。例如下面的代码：

~~~java
class Outer{                                                                       
        public static void main(String[] args){
                Outer out = new Outer();
                Object obj = out.method();
        }   

        Object method(){
                int locvar = 1;
                class Inner{
                        void displayLocvar(){
                                System.out.println("locvar = " + locvar);
                        }
                }
                Object in = new Inner();
                return in; 
        }
}
~~~

当out.method()方法执行结束后，局部变量 locvar 就消失了，但是在method（）方法中 obj in = new Inner() 产生的 in 对象还存在引用obj，这样对象就访问了一个不存在的变量，是不允许的。*这种矛盾是由局部内部类可以访问局部变量但是局部内部类对象和局部变量的生命周期不同而引起的。*

#### 2、局部内部类访问局部变量的机制

在java中，类是封装的，内部类也不例外。我们知道，非静态内部类能够访问外部类成员是因为它持有外部类对象的引用 Outer.this， 就像子类对像能够访问父类成员是持有父类对象引用super一样。局部内部类也和一般内部类一样，只持有了Outer.this，能够访问外部类成员，但是它又是如何访问到局部变量的呢？

实际上java是将局部变量作为参数传给了局部内部类的构造函数，而将其作为内部类的成员属性封装在了类中。我们看到的内部类访问局部变量实际上只是访问了自己的成员属性而已，这和类的封装性是一致的。那么上面的代码实际上是这样：

~~~java
Object method(){
                int locvar = 1;
                class Inner{
                    private int obj;
                    public Inner(int obj){
                        this.obj = obj;
                    }
                        void displayLocvar(){
                                System.out.println("locvar = " + locvar);
                        }
                }
                Object in = new Inner(locvar);  //将locvar作为参数传给构造，以初始话成员
                return in; 
        }
~~~

#### 3、问题解决

那么问题又来了，我们写代码的目的是在内部类中直接控制局部变量和引用，但是java这么整我们就不高兴了，我在内部类中整半天想着是在操作外部变量，结果你给**整个副本**给我，我搞半天丫是整我自己的东西啊？要是java不这么整吧，由破坏了封装性--------你个局部内部类牛啊，啥都没有还能看局部变量呢。这不是java风格，肯定不能这么干。这咋整呢？

想想，类的封装性咱们一定是要遵守的，不能破坏大局啊。但又要保证两个东西是一模一样的，包括对象和普通变量，那就使用final嘛，当传递普通变量的之前我把它变成一个常量给你，当传递引用对象的时候加上final就声明了这个引用就只能指着这一个对象了。这样就保证了内外统一。

#### 4、实战应用

~~~java
public void downloadContract{
 final SignHeadDto signHeadDto=获取signHeadDto
 //调三方下载合同
 final byte[] bytes = this.downContractByPlateType(platContractId, platformType);

 //将文件存到服务器
 ExecutorUtils.execute(new Runnable() {
   @Override
   public void run() {
       //上传合同
        UDFSUploadResultVO udfsUploadResultVO = udfsUpload(bytes);
                    
        signHeadDto.setFinishContractPath(udfsUploadResultVO.getOriginalName());
        signDao.updateSignHead(signHeadDto);
   }
 });
}
~~~

此处存在匿名类new Runnable()为内部类。而方法中的局部变量signHeadDto、bytes必须定义为final才不会报错。如图：

![QQ截图20181211150407](D:\ztl\笔记\java基础\java基础-实战中遇到问题记录md\pictures\QQ截图20181211150407.png)



### 2、引用传递和值传递

这里要用实际参数和形式参数的概念来帮助理解，

**值传递：**

方法调用时，实际参数把它的值传递给对应的形式参数，函数接收的是原始值的一个copy，此时内存中存在两个相等的基本类型，即实际参数和形式参数，后面方法中的操作都是对形参这个值的修改，不影响实际参数的值。

**引用传递：**

也称为传地址。方法调用时，实际参数的引用(地址，而不是参数的值)被传递给方法中相对应的形式参数，函数接收的是原始值的内存地址；
在方法执行中，形参和实参内容相同，指向同一块内存地址，方法执行中对引用的操作将会影响到实际对象。

例子：



 