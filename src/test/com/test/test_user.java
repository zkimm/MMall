package com.test;

import com.controller.portal.UserController;
import com.dao.UserMapper;
import com.pojo.User;
import com.pojo.UserExample;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class test_user {
    ApplicationContext ioc=new ClassPathXmlApplicationContext("applicationContext.xml");
    UserMapper userMapper=ioc.getBean(com.dao.UserMapper.class);
//    UserController userController=ioc.getBean(UserController.class);

    @Test
   public void test_countByUsername(){
//        int count=userMapper.countByUsername("admin");
        UserExample userExample=new UserExample();
        userExample.createCriteria().andUsernameEqualTo("admin");
        long count=userMapper.countByExample(userExample);
        System.out.print(count);
    }

    @Test
    public void test_register(){
        User user=new User();
        user.setId(2);
        user.setUsername("zkimm");
        user.setPassword("104615");
        user.setQuestion("问题");
        user.setAnswer("答案");
        user.setPhone("123456");
        user.setRole(1);
        UserController userController=new UserController();
        userController.register(user);
    }
}
