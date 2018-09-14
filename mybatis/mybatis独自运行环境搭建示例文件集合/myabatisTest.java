package com.itheima.mybatis.test;

import com.itheima.mybatis.pojo.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static org.apache.ibatis.io.Resources.getResourceAsStream;

public class myabatisTest {

    private   SqlSession  sqlSession;
    public  void   getSqlSession() throws IOException {
        //1.加载SqlMapConfig.xml
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

        // 2. 加载SqlMapConfig.xml配置文件
        InputStream inputStream = getResourceAsStream("SqlMapConfig.xml");

        // 3. 创建SqlSessionFactory对象
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);

        //4.执行sql
        SqlSession session = sqlSessionFactory.openSession();
        sqlSession=session;
    }

    @Test
    //通过id查询用户
    public  void  test1() throws IOException {
        getSqlSession();
        User user = sqlSession.selectOne("user.findUserById", 10);
        System.out.println(user.toString());
    }

    @Test
    //通过用户名模糊查询用户列表
    public  void  test2() throws IOException {
        getSqlSession();
        List<User> userList = sqlSession.selectList("user.findUserListByUsername", "王");
        for(User user:userList){
            System.out.println(user.toString());
        }
    }

    @Test
    //添加用户
    public  void  test3() throws IOException {
        getSqlSession();
        User user=new User();
       // user.setId(1001);
        user.setUsername("浪");
        user.setSex("1");
        user.setBirthday(new Date());
        user.setAddress("天下名企汇");
        int count = sqlSession.insert("user.insertUser", user);

        // 需要进行事务提交
        sqlSession.commit();

        // 7. 释放资源
        sqlSession.close();


        System.out.println(count);

    }

    @Test
    //更新用户
    public  void  test4() throws IOException {
        getSqlSession();
        User user = new User();
        user.setId(1001);
        user.setUsername("浪迹天更新");
        user.setSex("1");
        user.setBirthday(new Date());
        user.setAddress("天下名企汇");
        int count = sqlSession.update("user.updateUserById", user);
        // 需要进行事务提交
        sqlSession.commit();

        // 7. 释放资源
        sqlSession.close();

        System.out.println(count);
    }

    //删除用户
    @Test
    public  void  test5() throws IOException {
        getSqlSession();
        int count = sqlSession.delete("user.deleteUserById", 1001);
        // 需要进行事务提交
        sqlSession.commit();

        // 7. 释放资源
        sqlSession.close();
        System.out.println(count);
    }
}
