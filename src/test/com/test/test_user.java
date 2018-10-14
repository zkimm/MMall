package com.test;

import com.dao.UserMapper;
import com.pojo.User;
import com.pojo.UserExample;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class test_user {
    ApplicationContext ioc=new ClassPathXmlApplicationContext("applicationContext.xml");
    UserMapper userMapper=ioc.getBean(com.dao.UserMapper.class);

    @Test
   public void test_countByUsername(){
//        int count=userMapper.countByUsername("admin");
        UserExample userExample=new UserExample();
        userExample.createCriteria().andUsernameEqualTo("admin");
        long count=userMapper.countByExample(userExample);
        System.out.print(count);
    }
}
